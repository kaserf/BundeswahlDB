--Berechnung der gewählten Kandidaten (direkt + liste)

--direkt:
INSERT INTO Kandidaten_Gewaehlt
(
	with ergebnis_pro_wk as (
		select D.Kandidat, E.Wahlkreis, D.Stimmenanzahl
		from Direktergebnis D join Wahlergebnis E on D.wahlergebnis = E.id
		where E.wahljahr = 2009)

	select A.Kandidat, A.Wahlkreis
	from ergebnis_pro_wk A
	where A.stimmenanzahl=
	(
		select max(B.stimmenanzahl)
		from ergebnis_pro_wk B
		where A.Wahlkreis=B.Wahlkreis
	)
);

--liste:
INSERT INTO Kandidaten_Gewaehlt
(
	with
	ergebnis_pro_wk as
	(
		select L.Partei, E.Wahlkreis, L.Stimmenanzahl
		from Listenergebnis L join Wahlergebnis E on L.wahlergebnis = E.id
		where E.wahljahr = 2009
	),
	struktur_deutschland as
	(
		select sum(gueltig_zweit) as gueltige_stimmen
		from Struktur
		where jahr = 2009
	),
	parteien_deutschland as
	(
		select Partei, sum(stimmenanzahl) as partei_stimmen, cast(sum(stimmenanzahl) as float)/(select gueltige_stimmen from struktur_deutschland) as prozente
		from ergebnis_pro_wk
		group by Partei
	),
	parteien_bundesland as
	(
		select w.bundesland, e.Partei, sum(stimmenanzahl) as partei_stimmen
		from ergebnis_pro_wk e join wahlkreis w on e.wahlkreis = w.nummer
		group by e.Partei, w.bundesland
	),
	partei_dividiert as
	(
		select S.Partei, S.partei_stimmen / D.divisor
		from parteien_deutschland S, Divisor D
		where S.prozente >= 0.05
		order by S.partei_stimmen / D.divisor desc
		limit (598)
	),
	parteien_sitze as
	(
		select D.Partei, count(*) as Sitze
		from partei_dividiert D
		group by D.Partei
		order by Sitze desc
	),
	parteien_bundesland_ranking as
	(
		select dense_rank() over (partition by P.Partei order by P.partei_stimmen / D.divisor desc) as Rang, P.Partei, P.Bundesland, P.partei_stimmen / D.divisor
		from parteien_bundesland P, Divisor D
		order by P.Partei,Rang
	),
	parteien_bundesland_sitze as
	(
		select D.Partei, D.Bundesland, count(*) as Sitze
		from parteien_bundesland_ranking D, parteien_sitze S
		where D.Partei = S.Partei and D.Rang <= S.Sitze
		group by D.Partei,D.Bundesland
		order by D.Partei
	),
	--Direktmandate pro partei und bundesland berechnen, natürlich nur für jene mit partei (partei = 99 sind parteilose)
	direktmandate_parteien_bundesland as
	(
		select K.Partei, W.Bundesland, count(*) as Direktmandate
		from Kandidaten_Gewaehlt G, Kandidat K, Wahlkreis W
		where not G.direktkandidat_wk is null and G.direktkandidat_wk=W.Nummer and G.Kandidat=K.ausweisnummer and K.Partei <> 99
		group by K.Partei,W.Bundesland
	),
	--Überhangmandate berechnen (anzahl der direktmandate pro partei minus anzahl sitze die der partei zugesprochen wurden durch höchstzahlverfahren)
	ueberhangmandate_parteien_bundesland as
	(
		select D.Partei, D.Bundesland,
		(case when M.Direktmandate - D.Sitze > 0 then M.Direktmandate - D.Sitze else 0 end) as Ueberhangmandate
		from parteien_bundesland_sitze D left outer join direktmandate_parteien_bundesland M on D.Partei=M.Partei and D.Bundesland=M.Bundesland
	),
	--Gesamtsitze berechnen (berechnete sitze durch höchstzahlverfahren plus die überhangmandate)
	gesamtsitze_parteien_bundesland as
	(
		select S.Partei,S.Bundesland, S.Sitze + U.Ueberhangmandate as Sitze
		from  parteien_bundesland_sitze S left outer join ueberhangmandate_parteien_bundesland U on S.Partei=U.Partei and S.Bundesland=U.Bundesland
	),
	--Anzahl der aus Landeslisten zu besetzenden Plätze berechnen
	landeslistenplaetze_parteien_bundesland as
	(
		select S.Partei, S.Bundesland, (case when not D.Direktmandate is null then S.Sitze - D.Direktmandate else Sitze end) as Listenplaetze
		from gesamtsitze_parteien_bundesland S left outer join direktmandate_parteien_bundesland D on S.Partei=D.Partei and S.Bundesland=D.Bundesland
	),
	--Landeslisten mit Rangzahlen versehen. Direktkandidaten auslassen
	Index_Landeslisten as
	(
		select dense_rank() over (partition by L.Partei,L.Bundesland order by LK.listenplatz) as Rang, L.Partei, L.Bundesland, LK.Kandidat
		from Landesliste L, Landesliste_kandidat LK
		where L.Id=LK.Landesliste
			and not exists(select * from Kandidaten_Gewaehlt G where G.Kandidat=LK.Kandidat)
	)
	--restlichen Kandidaten eintragen
	select L.Kandidat, null
	from Index_Landeslisten L, landeslistenplaetze_parteien_bundesland P
	where L.Partei=P.Partei and L.Bundesland=P.Bundesland and L.Rang <= P.Listenplaetze
);
