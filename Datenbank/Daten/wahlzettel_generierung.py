#!/usr/bin/python
# -*- coding: utf-8 -*-

import psycopg2
from psycopg2 import extras

#######################################
# Richtiges Bundesland hier eintragen!#
bundesland = 'Bremen'
#######################################
conn = psycopg2.connect("dbname='Bundestagswahl' user=postgres password=admin")

cur = conn.cursor(cursor_factory=extras.DictCursor)

# Finde alle Wahlkreise des Bundeslands
cur.execute("""SELECT Wahlkreis.nummer FROM Bundesland,Wahlkreis WHERE Wahlkreis.bundesland=Bundesland.nummer AND Bundesland.name='%s'""" % (bundesland))
rows = cur.fetchall()

wahlkreise = []
for row in rows:
    wahlkreise.append(row[0])

for wahlkreis in wahlkreise:
    print "Erststimmen:\n"
    # Erststimmen
    cur.execute("""SELECT d.kandidat, d.stimmenanzahl, w.wahlkreis FROM Direktergebnis d, Wahlergebnis w WHERE w.id = d.wahlergebnis AND w.wahlkreis = %d AND w.wahljahr = 2009""" % (wahlkreis))
    rows = cur.fetchall()
    
    erststimmen = []
    for row in rows:
        print row
        anzahl = row[1]
        for i in range (1,anzahl):
            erststimmen.append(row[0])
    
    print "Zweitstimmen:\n"
    # Zweitstimmen
    cur.execute("""SELECT l.partei, l.stimmenanzahl, w.wahlkreis FROM Listenergebnis l, Wahlergebnis w WHERE w.id = l.wahlergebnis AND w.wahlkreis = %d AND w.wahljahr = 2009""" % (wahlkreis))
    rows = cur.fetchall()

    zweitstimmen = []
    for row in rows:
        print row
        anzahl = row[1]
        for i in range (1,anzahl):
            zweitstimmen.append(row[0])

    if (len(erststimmen) < len(zweitstimmen)):
        diff = len(zweitstimmen) - len(erststimmen)
        erststimmen += ["NULL"] * diff
    else:
        diff = len(erststimmen) - len(zweitstimmen)
        zweitstimmen += ["NULL"] * diff
    stimmen = zip(erststimmen, zweitstimmen)
    
    wahlbezirknr = 0
    j = 1
    for (erststimme,zweitstimme) in stimmen:
        if (j == 1):
            cur.execute("""INSERT INTO Wahlbezirk VALUES(%d, %d);""" % (wahlbezirknr, wahlkreis)) 
        cur.execute("""INSERT INTO Wahlzettel(erststimme,zweitstimme,wahlbezirk,wahlkreis) VALUES(%s, %s, %d, %d);""" % (str(erststimme), str(zweitstimme), wahlbezirknr, wahlkreis))
        cur.execute("""INSERT INTO Wahlberechtigte (gewaehlt, wahlbezirk, wahlkreis) VALUES(true, %d, %d);""" % (wahlbezirknr, wahlkreis))
        #conn.commit()
        if (j == 2500):
            conn.commit()
            print "commit"
            wahlbezirknr += 1
            j = 0
        j += 1

    #just to be sure...
    conn.commit()

    cur.execute("""SELECT wahlberechtigte FROM Struktur WHERE wahlkreis = %d AND jahr = 2009""" % (wahlkreis))
    rows = cur.fetchall()
    berechtigte = rows[0][0]
    cur.execute("""SELECT COUNT(*) FROM Wahlzettel WHERE wahlkreis = %d""" % (wahlkreis))
    rows = cur.fetchall()
    gewaehlt = rows[0][0]

    #new wahlbezirk, for those who did not vote
    wahlbezirknr += 1
    cur.execute("""INSERT INTO Wahlbezirk VALUES(%d, %d);""" % (wahlbezirknr, wahlkreis))
    conn.commit()

    if (gewaehlt > berechtigte):
        print "da ging was schief, mehr leute haben gewählt als erlaubt"
    else:
        while (berechtigte > gewaehlt):
            cur.execute("""INSERT INTO Wahlberechtigte (gewaehlt, wahlbezirk, wahlkreis) VALUES(false, %d, %d);""" % (wahlbezirknr, wahlkreis))
            gewaehlt += 1

    conn.commit()
