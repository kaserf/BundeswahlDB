--direktkandidaten die eine person waehlen kann
with
wk_wb as (SELECT wahlkreis, wahlbezirk FROM Wahlberechtigte WHERE ausweisnummer = 1),
wk_kandidaten as (SELECT ausweisnummer, vorname, nachname, wkk.wahlkreis, wahlbezirk, partei FROM (kandidat_wahlkreis wk JOIN kandidat k ON wk.kandidat = k.ausweisnummer) wkk JOIN wk_wb wahlkreis ON wahlkreis.wahlkreis = wkk.wahlkreis)
SELECT ausweisnummer, vorname, nachname, wahlkreis, wahlbezirk, kurzbezeichnung FROM wk_kandidaten wkk JOIN partei p ON wkk.partei = p.nummer;

--parteien die eine person waehlen kann
with
wk_wb as (SELECT wahlkreis, wahlbezirk FROM Wahlberechtigte WHERE ausweisnummer = 1),
wk_wb_bl as (SELECT wahlkreis, wahlbezirk, bl.nummer FROM (wk_wb w JOIN wahlkreis wk ON w.wahlkreis = wk.nummer) wahlkreis JOIN bundesland bl ON wahlkreis.bundesland = bl.nummer),
parteien_bl as (SELECT l.partei, wahlkreis, wahlbezirk FROM wk_wb_bl wk_bl JOIN landesliste l ON l.bundesland = wk_bl.nummer)
SELECT p.nummer, p.kurzbezeichnung, wahlkreis, wahlbezirk FROM parteien_bl pbl JOIN partei p ON pbl.partei = p.nummer;


--knappste sieger (top 10 werden in java ausgewählt)
with
wdk as (SELECT wd.wahlkreis, pk.ausweisnummer, pk.partei, pk.kurzbezeichnung, pk.vorname, pk.nachname, wd.stimmenanzahl FROM (wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer WHERE wd.wahljahr = 2009),
sieger as (SELECT * FROM wdk wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)),
ohne_sieger as (SELECT * FROM wdk EXCEPT SELECT * FROM sieger),
zweite as (SELECT * FROM ohne_sieger wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM ohne_sieger wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)),
knappste_sieger as (SELECT s.wahlkreis, s.partei as sieger_p_nummer, s.vorname as sieger_v, s.nachname as sieger_n, s.kurzbezeichnung as sieger_partei, s.stimmenanzahl as sieger_stimmen, z.stimmenanzahl as zweiter_stimmen, z.vorname as zweiter_v, z.nachname as zweiter_n, z.partei as zweiter_p_nummer, z.kurzbezeichnung as zweiter_partei FROM sieger s join zweite z on s.wahlkreis = z.wahlkreis)
SELECT * FROM knappste_sieger ORDER BY (sieger_stimmen - zweiter_stimmen) ASC;

--knappste verlierer (top 10 werden in java ausgewählt)
with
wdk as (SELECT wd.wahlkreis, pk.ausweisnummer, pk.partei, pk.kurzbezeichnung, pk.vorname, pk.nachname, wd.stimmenanzahl FROM (wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer WHERE wd.wahljahr = 2009),
sieger as (SELECT * FROM wdk wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)),
parteien_ohne_sieger as (SELECT * FROM partei p WHERE p.nummer NOT IN (SELECT DISTINCT partei FROM sieger)),
pos_kandidaten as (SELECT * FROM wdk WHERE partei IN (SELECT nummer FROM parteien_ohne_sieger)),
knappste_verlierer as (SELECT posk.wahlkreis, posk.partei as verlierer_p_nummer, posk.vorname as verlierer_v, posk.nachname as verlierer_n, posk.kurzbezeichnung as verlierer_partei, posk.stimmenanzahl as verlierer_stimmen, s.stimmenanzahl as sieger_stimmen, s.vorname as sieger_v, s.nachname as sieger_n, s.kurzbezeichnung as sieger_partei FROM pos_kandidaten posk JOIN sieger s ON posk.wahlkreis = s.wahlkreis)
SELECT * FROM knappste_verlierer ORDER BY (verlierer_stimmen - sieger_stimmen) DESC;


--ueberhangmandate pro partei und bundesland
with
ergebnis_pro_wk as
(
	select L.Partei, E.Wahlkreis, L.Stimmenanzahl
	from Listenergebnis L join Wahlergebnis E on L.wahlergebnis = E.id
	where E.wahljahr = 2009
),
direktergebnis_pro_wk as
(
		select D.Kandidat, E.Wahlkreis, D.Stimmenanzahl
		from Direktergebnis D join Wahlergebnis E on D.wahlergebnis = E.id
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
--direktmandate berechnen
direktkandidaten_gewaehlt as
(
	select A.Kandidat, A.Wahlkreis
	from direktergebnis_pro_wk A
	where A.stimmenanzahl=
	(
		select max(B.stimmenanzahl)
		from direktergebnis_pro_wk B
		where A.Wahlkreis=B.Wahlkreis
	)
),
direktmandate_parteien_bundesland as
(
	select K.Partei, W.Bundesland, count(*) as Direktmandate
	from direktkandidaten_gewaehlt G, Kandidat K, Wahlkreis W
	where G.Wahlkreis=W.Nummer and G.Kandidat=K.ausweisnummer and K.Partei <> 99
	group by K.Partei,W.Bundesland
),
ueberhang as
(
	select D.Partei, D.Bundesland,
	(case when M.Direktmandate - D.Sitze > 0 then M.Direktmandate - D.Sitze else 0 end) as Ueberhangmandate
	from parteien_bundesland_sitze D left outer join direktmandate_parteien_bundesland M on D.Partei=M.Partei and D.Bundesland=M.Bundesland
)
select b.name, b.kuerzel, up.kurzbezeichnung, up.ueberhangmandate
from (ueberhang u join partei p on u.partei = p.nummer) up join bundesland b on up.bundesland = b.nummer where up.ueberhangmandate > 0;

--wahlkreissieger (parteien) erst und zweitstimmen (aggregiert)
WITH
wdk as (SELECT * FROM ((wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer) wdk WHERE wdk.wahljahr = 2009),
pwl as (SELECT * FROM ((wahlergebnis w JOIN listenergebnis l ON w.id = l.wahlergebnis) wl JOIN partei p ON p.nummer = wl.partei) pwl WHERE pwl.wahljahr = 2009),
erst_sieger as (SELECT relation_outer.kurzbezeichnung, relation_outer.wahlkreis, relation_outer.stimmenanzahl, relation_outer.vorname, relation_outer.nachname FROM wdk relation_outer WHERE relation_outer.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk relation_inner WHERE relation_outer.wahlkreis=relation_inner.wahlkreis))
SELECT es.kurzbezeichnung as erstpartei, es.vorname, es.nachname, es.stimmenanzahl as erststimmen, zweit_outer.kurzbezeichnung as zweitsieger, zweit_outer.stimmenanzahl as zweitstimmen, zweit_outer.wahlkreis FROM pwl zweit_outer JOIN erst_sieger es ON zweit_outer.wahlkreis = es.wahlkreis WHERE zweit_outer.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM pwl zweit_inner WHERE zweit_outer.wahlkreis=zweit_inner.wahlkreis);


--wahlausgang für einen wahlkreis (aggregierte berechnung)
with
gesamt09 as 
(select gueltig_zweit as gesamt from struktur where wahlkreis = 55 and jahr = 2009),
gesamt05 as 
(select gueltig_zweit as gesamt from struktur where wahlkreis = 55 and jahr = 2005), 
old_prozente as 
(select l2.partei, cast(l2.stimmenanzahl as float)/gesamt05.gesamt as vor_stimmen from gesamt05, listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2005 and w2.wahlkreis = 55), 
new_prozente as 
(select l2.partei, cast(l2.stimmenanzahl as float)/gesamt09.gesamt as vor_stimmen from gesamt09, listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2009 and w2.wahlkreis = 55)
select p.kurzbezeichnung, lw.stimmenanzahl as stimmen, old_prozente.vor_stimmen * 100 as prozente_old, new_prozente.vor_stimmen * 100 as prozente_new, (new_prozente.vor_stimmen - old_prozente.vor_stimmen) * 100 as prozente_diff from (listenergebnis l join wahlergebnis w on l.wahlergebnis = w.id) lw join partei p on lw.partei = p.nummer, old_prozente, new_prozente where lw.wahljahr = 2009 and lw.wahlkreis = 55 and old_prozente.partei = lw.partei and new_prozente.partei = lw.partei;

--direktkandidat aggregiert
WITH wdk as (SELECT * FROM ((wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer) wdk WHERE wdk.wahljahr = 2009 AND wdk.wahlkreis = 55)
SELECT wdk.vorname, wdk.nachname, wdk.kurzbezeichnung FROM wdk WHERE wdk.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk);



--wahlausgang für einen wahlkreis (nicht aggregiert, bis auf werte von 2005)
with
stimmen_pro_partei as
(select zweitstimme as partei, COUNT(*) as stimmenanzahl FROM Wahlzettel WHERE Wahlkreis = 55 GROUP BY zweitstimme),
gesamt09 as 
(select SUM(stimmenanzahl) as gesamt from stimmen_pro_partei),
gesamt05 as 
(select gueltig_zweit as gesamt from struktur where wahlkreis = 55 and jahr = 2005), 
old_prozente as 
(select l2.partei, cast(l2.stimmenanzahl as float)/gesamt05.gesamt as vor_stimmen from gesamt05, listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2005 and w2.wahlkreis = 55), 
new_prozente as 
(select spp.partei, cast(spp.stimmenanzahl as float)/gesamt09.gesamt as vor_stimmen from gesamt09, stimmen_pro_partei spp)
select p.kurzbezeichnung, spp.stimmenanzahl as stimmen, old_prozente.vor_stimmen * 100 as prozente_old, new_prozente.vor_stimmen * 100 as prozente_new, (new_prozente.vor_stimmen - old_prozente.vor_stimmen) * 100 as prozente_diff from stimmen_pro_partei spp join partei p on spp.partei = p.nummer, old_prozente, new_prozente where old_prozente.partei = spp.partei and new_prozente.partei = spp.partei;


--*******************************************************************************

--wahlbeteiligung für einen wahlkreis (direktberechnung, ohne aggregate)
--wk 55 liegt in bremen
SELECT 55 as Wahlkreis, (CAST (anz_gew as float)/anz_ber) * 100 as Beteiligung
  FROM (SELECT COUNT(*) as anz_gew FROM Wahlberechtigte WHERE gewaehlt = true AND wahlkreis = 55)
        AS gewaehlt_kreis,
       (SELECT COUNT(*) as anz_ber FROM Wahlberechtigte WHERE wahlkreis = 55)
        AS berechtigte_kreis;

--gewählter direktkandidat für wahlkreis
WITH stimmen_pro_kandidat (kandidat, anz) AS (SELECT erststimme, COUNT(*) FROM Wahlzettel WHERE Wahlkreis = 55 GROUP BY erststimme),
stimmen_pro_partei (partei_nr, anz_p) AS (SELECT zweitstimme, COUNT(*) FROM Wahlzettel WHERE Wahlkreis = 55 GROUP BY zweitstimme)
SELECT vorname, nachname, kurzbezeichnung
FROM Kandidat k join Partei p on p.nummer = k.partei
WHERE ausweisnummer = (SELECT kandidat FROM stimmen_pro_kandidat WHERE anz = (SELECT MAX(anz) FROM stimmen_pro_kandidat));

--stimmenanzahl nach partei
SELECT p.kurzbezeichnung, (CAST (s.anz_p as float) / foo.anz_ges) * 100 as prozente, anz_p as stimmen
FROM Partei p, stimmen_pro_partei s, (SELECT COUNT(*) AS anz_ges FROM Wahlzettel WHERE Wahlkreis = 55 AND zweitstimme IS NOT NULL) as foo
WHERE p.nummer = s.partei_nr
ORDER BY prozente DESC;

--partei x jahr -> stimmenanzahl
select w.wahljahr, p.kurzbezeichnung, l.stimmenanzahl from Wahlergebnis w, Listenergebnis l, Partei p where w.id = l.wahlergebnis AND w.wahlkreis = 55 AND l.partei = p.nummer;
