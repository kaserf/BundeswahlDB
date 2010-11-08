#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv
import codecs

parteien = []
parteienmap = {}
wahlzettel = []

# read Parteien
csvfile = open("Parteien.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()

for row in reader:
    name = row[0].decode('iso8859_15')
    nummer = row[2].decode('iso8859_15').split(',')[0]
    parteienmap[nummer] = name
    parteien.append('INSERT INTO Partei VALUES ("%s");' % name)

datafile = codecs.open('parteien.sql', 'w', 'utf-8')
datafile.write(u'--Parteien einfügen\n')
datafile.write('\n'.join(parteien))
datafile.close()

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
