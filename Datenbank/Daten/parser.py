#!/usr/bin/python
# -*- coding: utf-8 -*-

bundeslaender = []
wahlkreise = []
wahlbezirke = []

g = open('bundeslaender.csv', 'r')

bundeslaender_map = {}
for line in g:
    (name, kuerzel, hauptstadt) = line.strip().split('|')
    bundeslaender_map[name] = kuerzel
    bundeslaender.append('INSERT INTO Bundesland VALUES ("%s", "%s", "%s");' % (kuerzel, name, hauptstadt))

g.close()

f = open('Wahlkreise_bezirke.csv', 'r')

bundesland = None

def addWhaleCircle(ar):
    (nummer, name, bezirke) = ar
    wahlkreise.append('INSERT INTO Wahlkreis VALUES (%s, "%s", "%s");' % (nummer, name, bundeslaender_map[bundesland]))
    for bezirk in bezirke.split(','):
        wahlbezirke.append('INSERT INTO Wahlbezirk (name, wahlkreis) VALUES("%s", %s);' % (bezirk.strip(), nummer))
    
for line in f:
    parts = line.strip().split('|')
    parts = filter(len, parts)
    if len(parts)==1:
        bundesland = parts[0]
    elif len(parts)==3:
        addWhaleCircle(parts)
    elif len(parts)==6:
        addWhaleCircle(parts[0:3])
        addWhaleCircle(parts[3:6])

f.close()

datafile = open('bundeslaender_wahlkreise_wahlbezirke.sql', 'w')
datafile.write('--Bundesländer einfügen\n')
datafile.write('\n'.join(bundeslaender))
datafile.write('\n\n--Wahlkreise einfügen\n')
datafile.write('\n'.join(wahlkreise))
datafile.write('\n\n--Wahlbezirke einfügen\n')
datafile.write('\n'.join(wahlbezirke))

datafile.close()

