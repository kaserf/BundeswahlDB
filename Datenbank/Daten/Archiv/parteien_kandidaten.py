#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv
import codecs

parteien = []
parteienmap = {}
kandidaten = []

# read Parteien
csvfile = open("Parteien.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
dialect.delimiter = ';'
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()

for row in reader:
    p_kurz = row[0].decode('iso8859_3')
    p_name = row[1].decode('iso8859_3')
    p_nummer = int(row[2].decode('iso8859_3'))
    parteienmap[p_nummer] = p_name
    parteien.append('INSERT INTO Partei VALUES (%i, "%s", "%s");' % (p_nummer, p_kurz, p_name))

datafile = codecs.open('parteien.sql', 'w', 'utf-8')
datafile.write(u'--Parteien einfügen\n')
datafile.write('\n'.join(parteien))
datafile.close()

# read Kandidaten
csvfile = open("Kandidaten.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
dialect.delimiter = ';'
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()

for row in reader:
#    k_id = int(row[0].decode('iso8859_15'))
    k_vorname = row[1].decode('iso8859_3')
    k_nachname = row[0].decode('iso8859_3')
    k_geburtsjahr = row[2].decode('iso8859_3')
    k_partei = int(row[4].decode('iso8859_3'))
    kandidaten.append('INSERT INTO Kandidat VALUES ("%s", "%s", "%s", %i);' % (k_vorname, k_nachname, k_geburtsjahr, k_partei))

datafile = codecs.open('kandidat.sql', 'w', 'utf-8')
datafile.write(u'--Kandidaten einfügen\n')
datafile.write('\n'.join(kandidaten))
datafile.close()

"""
# read Wahlergebnisse
csvfile = open("Wahlkreis_Ergebnisse.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()


wahlzettel_map = {
    '2005': {},
    '2009': {}
    }


for row in reader:
    (wahlkreis, parteinr, erststimmen, zweitstimmen, jahr) = row
    partei = parteienmap[parteinr]
    if len(erststimmen) == 0:
        erststimmen = 0
    else: 
        erststimmen = int(erststimmen)
    if len(zweitstimmen) == 0:
        zweitstimmen = 0
    else:
        zweitstimmen = int(zweitstimmen)
    if not wahlkreis in wahlzettel_map[jahr]:
        wahlzettel_map[jahr][wahlkreis] = {}
    if not 'erststimmen' in wahlzettel_map[jahr][wahlkreis]:
        wahlzettel_map[jahr][wahlkreis]['erststimmen'] = []
    if not 'zweitstimmen' in wahlzettel_map[jahr][wahlkreis]:
        wahlzettel_map[jahr][wahlkreis]['zweitstimmen'] = []
    wahlzettel_map[jahr][wahlkreis]['erststimmen'].extend([partei] * erststimmen)
    wahlzettel_map[jahr][wahlkreis]['zweitstimmen'].extend([partei] * zweitstimmen)

wahlzettel_file = codecs.open('wahlzettel.sql', 'w', 'utf-8')
for (year,wahlkreis_map) in wahlzettel_map.iteritems():
    for wahlkreis in wahlkreis_map:
        wahlkreis_erststimmen = wahlkreis_map[wahlkreis]['erststimmen']
        wahlkreis_zweitstimmen = wahlkreis_map[wahlkreis]['zweitstimmen']
        if len(wahlkreis_erststimmen) != len(wahlkreis_zweitstimmen):
            print ('Unterschiedlich viele Erst- und Zweitstimmen in WK %s im jahr %s: %d,%d' % (wahlkreis, year, len(wahlkreis_erststimmen), len(wahlkreis_zweitstimmen)))

wahlzettel_file.close()
"""
