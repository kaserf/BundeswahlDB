#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv

def OpenCSV(name, skipfirst=True):
    csvfile = open(name, 'r')
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
Kandidat = [] #
Bundesland = [] #
Wahlkreis = [] #
Wahlbezirk = [] #
Landesliste = [] #
Wahlberechtigte = []
Wahlergebnis = []
Direktergebnis = []
Listenergebnis = []
Wahlzettel = []
Landesliste_Kandidat = [] #
Kandidat_Wahlkreis = [] #

# Laender einlesen
for (nummer, name, kurz) in laender:
    Bundesland.append("INSERT INTO Bundesland VALUES(%s, '%s', '%s');" % (nummer, kurz, name))

WriteSQL("bundesland", Bundesland)

# Parteien einlesen
for (kurz, name, nummer) in parteien:
    Partei.append("INSERT INTO Partei VALUES(%s,'%s','%s');" % (nummer, kurz, name))

# Dummypartei für Parteilose einfügen
Partei.append("INSERT INTO Partei VALUES(99, 'pl', 'parteilos');")

WriteSQL("partei", Partei)

# Wahlkreise einlesen
wahlkreis_nummern = []
for (nummer, name, bundeslandnr) in wahlkreise:
    nummer = int(nummer)
    wahlkreis_nummern.append(nummer)
    Wahlkreis.append("INSERT INTO Wahlkreis VALUES(%d, '%s', %s);" % (nummer, name, bundeslandnr))

WriteSQL("wahlkreis", Wahlkreis)

num_wbs = 20

# Wahlbezirke erfinden
for wk_nummer in wahlkreis_nummern:
    for wb_nummer in range(0,num_wbs):
        Wahlbezirk.append("INSERT INTO Wahlbezirk(nummer,wahlkreis) VALUES(%d,%d);" % (wb_nummer, wk_nummer))

WriteSQL("wahlbezirk", Wahlbezirk)

# Kandidaten, Landeslisten und Direktkandidaten
kandidatnr = 0
landeslisten = {}
landeslistennr = 0

for (nachname, vorname, geburtsjahr, partei, parteinr, land, landnr, platz, wknr) in kandidaten:
    if parteinr.isalnum():
        parteinr = int(parteinr)
    if partei.startswith("K:"):
        parteinr = 99
    Kandidat.append("INSERT INTO Kandidat VALUES(%d, '%s', '%s', %s, %d);" % (kandidatnr, vorname, nachname, geburtsjahr, parteinr))
    
    # Listenkandidatur
    if landnr.isalnum():
        landnr = int(landnr)
        llnr = landeslistennr
        if not (parteinr, landnr) in landeslisten:
            landeslisten[(parteinr, landnr)] = landeslistennr
            Landesliste.append("INSERT INTO Landesliste VALUES(%d, %d, %d);" % (landeslistennr, parteinr, landnr))
            landeslistennr += 1
        else:
            llnr = landeslisten[(parteinr, landnr)]
        platz = int(platz)
        Landesliste_Kandidat.append("INSERT INTO Landesliste_Kandidat VALUES(%d, %d, %d);" % (llnr, kandidatnr, platz))
        
    # Wahlkreiskandidatur
    if wknr.isalnum():
        wknr = int(wknr)
        Kandidat_Wahlkreis.append("INSERT INTO Kandidat_Wahlkreis VALUES(%d, %d);" % (kandidatnr, wknr))
    kandidatnr += 1

WriteSQL("kandidat", Kandidat)
WriteSQL("landesliste", Landesliste)
WriteSQL("landesliste_kandidat", Landesliste_Kandidat)
WriteSQL("kandidat_wahlkreis", Kandidat_Wahlkreis)

# Aggregierte Ergebnisse
for wk_nummer in wahlkreis_nummern:
    Wahlergebnis.append("INSERT INTO Wahlergebnis VALUES(%d, %d);" % (wk_nummer, 2005))
    Wahlergebnis.append("INSERT INTO Wahlergebnis VALUES(%d, %d);" % (wk_nummer, 2009))
for (wahlkreis, partei, erststimmen, zweitstimmen, jahr) in ergebnisse:
    if len(erststimmen) != 0:
       # Direktergebnis.append("INSERT INTO Direktergebnis(partei,stimmenanzahl,wahlergebnis) VALUES(' 

