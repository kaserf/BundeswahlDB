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
