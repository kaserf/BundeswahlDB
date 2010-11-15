#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv
import codecs

wahlzettel = []

# read Wahlergebnisse
csvfile = open("Wahlkreis_Ergebnisse.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()

for row in reader:
    (wahlkreis, partei, erststimmen, zweitstimmen, jahr) = row

