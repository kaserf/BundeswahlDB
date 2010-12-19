#!/usr/bin/python
# -*- coding: utf-8 -*-

import csv

def OpenCSV(name, skipfirst=True, delimiter=','):
    csvfile = open(name, 'r')
    dialect = csv.Sniffer().sniff(csvfile.read(1024))
    dialect.delimiter = delimiter
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
ergebnisse = OpenCSV('Wahlkreis_Ergebnisse.csv', delimiter=';')
struktur = OpenCSV('struktur_kreis.csv', delimiter=';')

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
Struktur = []

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

# Strukturdaten einlesen
for (wk, jahr, ber, w, ung1, ung2, g1, g2) in struktur:
    Struktur.append("INSERT INTO Struktur VALUES(%d, %d, %d, %d, %d, %d, %d, %d);" % (int(wk), int(jahr), int(ber), int(w), int(ung1), int(ung2), int(g1), int(g2)))

WriteSQL("struktur", Struktur)
#num_wbs = 20

# Wahlbezirke erfinden
#for wk_nummer in wahlkreis_nummern:
#    for wb_nummer in range(0,num_wbs):
#        Wahlbezirk.append("INSERT INTO Wahlbezirk(nummer,wahlkreis) VALUES(%d,%d);" % (wb_nummer, wk_nummer))
#
#WriteSQL("wahlbezirk", Wahlbezirk)

# Kandidaten, Landeslisten und Direktkandidaten
kandidatnr = 0
kandidat_partei = {}
kandidat_map = {}
landeslisten = {}
landeslistennr = 0

for (nachname, vorname, geburtsjahr, partei, parteinr, land, landnr, platz, wknr) in kandidaten:
    if parteinr.isalnum():
        parteinr = int(parteinr)
    if partei.startswith("K:"):
        parteinr = 99
    Kandidat.append("INSERT INTO Kandidat VALUES(%d, '%s', '%s', %s, %d);" % (kandidatnr, vorname, nachname, geburtsjahr, parteinr))
    kandidat_partei[kandidatnr] = parteinr
    
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
        partei = kandidat_partei[kandidatnr]
        kandidat_map[(partei,wknr)] = kandidatnr
    kandidatnr += 1

WriteSQL("kandidat", Kandidat)
WriteSQL("landesliste", Landesliste)
WriteSQL("landesliste_kandidat", Landesliste_Kandidat)
WriteSQL("kandidat_wahlkreis", Kandidat_Wahlkreis)

# Aggregierte Ergebnisse
wahlergebnisse = {}
we_nr = 0
for wk_nummer in wahlkreis_nummern:
    Wahlergebnis.append("INSERT INTO Wahlergebnis VALUES(%d, %d, %d);" % (we_nr, 2005, wk_nummer))
    wahlergebnisse[(wk_nummer, 2005)] = we_nr
    Wahlergebnis.append("INSERT INTO Wahlergebnis VALUES(%d, %d, %d);" % (we_nr + 1, 2009, wk_nummer))
    wahlergebnisse[(wk_nummer, 2009)] = we_nr + 1
    we_nr += 2
for (wahlkreis, partei, erststimmen, zweitstimmen, jahr) in ergebnisse:
    wahlkreis = int(wahlkreis)
    jahr = int(jahr)
    partei = int(partei)
    wahlergebnisnr = wahlergebnisse[(wahlkreis, jahr)]
    if len(erststimmen) != 0:
        erststimmen = int(erststimmen)
        if (jahr == 2005):
            Direktergebnis.append("INSERT INTO Direktergebnis(partei,stimmenanzahl,wahlergebnis) VALUES(%d, %d, %d);" % (partei, erststimmen, wahlergebnisnr))
        if (jahr == 2009):
            kandidat = kandidat_map[(partei, wahlkreis)]
            Direktergebnis.append("INSERT INTO Direktergebnis(kandidat,partei,stimmenanzahl,wahlergebnis) VALUES(%d, %d, %d, %d);" % (kandidat, partei, erststimmen, wahlergebnisnr))
    if (len(zweitstimmen)) != 0:
        zweitstimmen = int(zweitstimmen)
        Listenergebnis.append("INSERT INTO Listenergebnis VALUES(%d, %d, %d);" % (partei, zweitstimmen, wahlergebnisnr))

WriteSQL("wahlergebnis", Wahlergebnis)
WriteSQL("direktergebnis", Direktergebnis)
WriteSQL("listenergebnis", Listenergebnis)
