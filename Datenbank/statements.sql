--wahlbeteiligung für einen wahlkreis
--wk 55 liegt in bremen
SELECT 55 as Wahlkreis, (CAST (anz_gew as float)/anz_ber) * 100 as Beteiligung
  FROM (SELECT COUNT(*) as anz_gew FROM Wahlberechtigte WHERE gewaehlt = true AND wahlkreis = 55)
        AS gewaehlt_kreis,
       (SELECT COUNT(*) as anz_ber FROM Wahlberechtigte WHERE wahlkreis = 55)
        AS berechtigte_kreis;

--gewählter direktkandidat für wahlkreis
WITH stimmen_pro_kandidat (kandidat, anz) AS (SELECT erststimme, COUNT(*) FROM Wahlzettel WHERE Wahlkreis = 55 GROUP BY erststimme)

SELECT ausweisnummer, vorname, nachname
FROM Kandidat
WHERE ausweisnummer = (SELECT kandidat FROM stimmen_pro_kandidat WHERE anz = (SELECT MAX(anz) FROM stimmen_pro_kandidat));

--stimmenanzahl nach partei
WITH stimmen_pro_partei (partei_nr, anz_p)
AS (SELECT zweitstimme, COUNT(*)
    FROM Wahlzettel
    WHERE Wahlkreis = 55
    GROUP BY zweitstimme)
SELECT p.kurzbezeichnung, (CAST (s.anz_p as float) / foo.anz_ges) * 100 as prozente, anz_p as stimmen
FROM Partei p, stimmen_pro_partei s, (SELECT COUNT(*) AS anz_ges FROM Wahlzettel WHERE Wahlkreis = 55 AND zweitstimme IS NOT NULL) as foo
WHERE p.nummer = s.partei_nr
ORDER BY prozente DESC;

--partei x jahr -> stimmenanzahl
select w.wahljahr, p.kurzbezeichnung, l.stimmenanzahl from Wahlergebnis w, Listenergebnis l, Partei p where w.id = l.wahlergebnis AND w.wahlkreis = 55 AND l.partei = p.nummer;


--test
WITH stimmen_pro_partei (partei_nr, anz_p)
AS (SELECT zweitstimme, COUNT(*)
    FROM Wahlzettel
    WHERE Wahlkreis = 55
    GROUP BY zweitstimme),
ergebnis
AS (SELECT p.kurzbezeichnung, (CAST (s.anz_p as float) / foo.anz_ges) * 100 as prozente, anz_p as stimmen
FROM Partei p, stimmen_pro_partei s, (SELECT COUNT(*) AS anz_ges FROM Wahlzettel WHERE Wahlkreis = 55 AND zweitstimme IS NOT NULL) as foo
WHERE p.nummer = s.partei_nr
ORDER BY prozente DESC)

SELECT SUM(prozente) FROM ergebnis;

--berechnung der gewählten kandidaten (direkt + liste)

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

create table divisoren(divisor decimal(4,1));
insert into divisoren(divisor) values (0.5), (1.5), (2.5), (3.5), (4.5), (5.5), (6.5), (7.5), (8.5), (9.5), (10.5), (11.5), (12.5), (13.5), (14.5), (15.5), (16.5), (17.5), (18.5), (19.5), (20.5), (21.5), (22.5), (23.5), (24.5), (25.5), (26.5), (27.5), (28.5), (29.5), (30.5), (31.5), (32.5);
--ca 20 - 30 mal ausführen:
insert into divisoren(divisor) values ((select max(divisor) from divisoren) + 1), ((select max(divisor) from divisoren) + 2), ((select max(divisor) from divisoren) + 3), ((select max(divisor) from divisoren) + 4), ((select max(divisor) from divisoren) + 5);

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
--	struktur_bundesland as
--	(
--		select w.bundesland, sum(s.gueltig_zweit) as gueltige_stimmen
--		from Struktur s join Wahlkreis w on s.wahlkreis = w.nummer
--		where jahr = 2009
--		group by w.bundesland
--	)
	parteien_deutschland as
	(
		select Partei, sum(stimmenanzahl) as partei_stimmen, cast(sum(stimmenanzahl) as float)/(select gueltige_stimmen from struktur_deutschland)*100 as prozente
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
		from parteien_deutschland S, divisoren D
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
		from parteien_bundesland P, divisoren D
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
	--Direktmandate berechnen
	direktmandate_parteien_bundesland as
	(
		select K.Partei, W.Bundesland, count(*) as Direktmandate
		from Kandidaten_Gewaehlt G, Kandidat K, Wahlkreis W
		where not G.direktkandidat_wk is null and G.direktkandidat_wk=W.Nummer and G.Kandidat=K.ausweisnummer and not K.Partei is null
		group by K.Partei,W.Bundesland
	),
	--Überhangmandate berechnen
	ueberhangmandate_parteien_bundesland as
	(
		select D.Partei, D.Bundesland,
		(case when M.Direktmandate - D.Sitze > 0 then M.Direktmandate - D.Sitze else 0 end) as Ueberhangmandate
		from parteien_bundesland_sitze D left outer join direktmandate_parteien_bundesland M on D.Partei=M.Partei and D.Bundesland=M.Bundesland
	),
	--Gesamtsitze berechnen
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

	--restlichen kandidaten eintragen
	select L.Kandidat, null
	from Index_Landeslisten L, landeslistenplaetze_parteien_bundesland P
	where L.Partei=P.Partei and L.Bundesland=P.Bundesland and L.Rang <= P.Listenplaetze
--	ueber_huerde as
--	(
--		select Partei, partei_stimmen
--		from parteien_deutschland
--		where prozente >= 0.05
--	),

--	summe_ueber_huerde_stimmen as
--	(
--		select cast(sum(partei_stimmen) as double precision) as gesamtstimmen
--		from ueber_huerde
--	),

--	parteien_angepasst as
--	(
--		select Partei, partei_stimmen, prozente/(select gesamtstimmen from summe_ueber_huerde_stimmen) as prozente_angepasst
--		from parteien_deutschland
--	),
);
