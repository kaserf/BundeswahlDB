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

--wahlbeteiligung für einen wahlkreis (direktberechnung, ohne aggregate)
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
