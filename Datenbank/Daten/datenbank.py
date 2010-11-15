#!/usr/bin/python

import csv

def OpenCSV(name, skipfirst=True):
    csvfile = open(name, "r")
    dialect = csv.Sniffer().sniff(csvfile.read(1024))
    dialect.delimiter = ','
    csvfile.seek(0)
    reader = csv.reader(csvfile, dialect)
    if skipfirst:
        reader.next()
    return reader

def WriteSQL(name, data):
    datafile = open(name + '.sql', 'w')
    for line in data:
        datafile.write(line + '\n') 
    datafile.close()

    

laender = OpenCSV('Wahlbewerber_2009_Bundeslaender.csv', skipfirst=False)
kandidaten = OpenCSV('Wahlbewerber_2009_Kandidaten.csv')
parteien = OpenCSV('Wahlbewerber_2009_Parteien.csv')
wahlkreise = OpenCSV('Wahlbewerber_2009_Wahlkreise.csv')
ergebnisse = OpenCSV('Wahlkreis_Ergebnisse.csv')

Partei = [] #
Kandidat = []
Bundesland = [] #
Wahlkreis = [] #
Wahlbezirk = [] #
Landesliste = []
Wahlberechtigte = []
Wahlergebnis = []
Direktergebnis = []
Listenergebnis = []
Wahlzettel = []
Landesliste_Kandidat = []
Kandidat_Wahlkreis = []

# Laender einlesen
for (nummer, name, kurz) in laender:
    Bundesland.append('INSERT INTO Bundesland VALUES(%s, "%s", "%s");' % (nummer, kurz, name))

WriteSQL('bundesland', Bundesland)

# Parteien einlesen
for (kurz, name, nummer) in parteien:
    Partei.append('INSERT INTO Partei VALUES(%s,"%s","%s");' % (nummer, kurz, name))

# Dummypartei für Parteilose einfügen
Partei.append('INSERT INTO Partei VALUES(99, "pl", "parteilos");')

WriteSQL('partei', Partei)

# Wahlkreise einlesen
wahlkreis_nummern = []
for (nummer, name, bundeslandnr) in wahlkreise:
    nummer = int(nummer)
    wahlkreis_nummern.append(nummer)
    Wahlkreis.append('INSERT INTO Wahlkreis VALUES(%d, "%s", %s)' % (nummer, name, bundeslandnr))

WriteSQL('wahlkreis', Wahlkreis)

num_wbs = 20

# Wahlbezirke erfinden
for wk_nummer in wahlkreis_nummern:
    for wb_nummer in range(0,num_wbs):
        Wahlbezirk.append('INSERT INTO Wahlbezirk VALUES(%d,%d)' % (wb_nummer, wk_nummer))

WriteSQL('wahlbezirk', Wahlbezirk)

# Kandidaten, Landeslisten und Direktkandidaten
landeslisten = {}

for (vorname, nachname, geburtsjahr, partei, parteinr, land, landnr, platz, wknr) in kandidaten:
    if partei.startswith('K:'):
        parteinr = 99
    Kandidat.append('INSERT INTO Kandidat VALUES("%s", "%s", %s, %s)' % (vorname, nachname, geburtsjahr, parteinr))
    
    # Listenkandidatur
#    if len(land) != 0:
#        if 
