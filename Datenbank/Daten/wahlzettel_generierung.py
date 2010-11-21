#!/usr/bin/python
# -*- coding: utf-8 -*-

import psycopg2
from psycopg2 import extras

#######################################
# Richtiges Bundesland hier eintragen!#
bundesland = 'Bayern'
#######################################

try:
    conn = psycopg2.connect("dbname='Bundestagswahl'")
except:
    print "I am unable to connect to the database"

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
        if (j == 2500):
            wahlbezirknr += 1
            j = 0
        j += 1
