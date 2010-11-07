#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv
import codecs

parteien = []

csvfile = open("Parteien.csv", "r")
dialect = csv.Sniffer().sniff(csvfile.read(1024))
csvfile.seek(0)
reader = csv.reader(csvfile, dialect)
# ignore headline
reader.next()

for row in reader:
    name = row[0].decode('iso8859_15')
    parteien.append('INSERT INTO Partei VALUES ("%s");' % name)

datafile = codecs.open('parteien.sql', 'w', 'utf-8')
datafile.write(u'--Parteien einfügen\n')
datafile.write('\n'.join(parteien))
