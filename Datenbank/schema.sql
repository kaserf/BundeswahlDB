﻿-- drop alle Tabellen
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE Partei (
	nummer integer primary key,
	kurzbezeichnung varchar(50),
	name varchar(100)
);

-- Enthält Beziehung "ist Mitglied in"
CREATE TABLE Kandidat (
	ausweisnummer integer primary key,
	vorname varchar(50),
	nachname varchar(50),
	partei integer REFERENCES Partei
);

CREATE TABLE Bundesland (
	nummer integer primary key,
	kuerzel varchar(10),
	name varchar(50)
);

-- Enthält Beziehung "WK liegt in"
CREATE TABLE Wahlkreis (
	nummer integer primary key,
	name varchar(100),
	bundesland integer REFERENCES Bundesland
);

-- Enthält Beziehung "WB liegt in"
CREATE TABLE Wahlbezirk (
	nummer integer,
	wahlkreis integer REFERENCES Wahlkreis,
	primary key (nummer, wahlkreis)
);

-- Enthält Beziehung "gehört zu", "aufgestellt für"
CREATE TABLE Landesliste (
	id integer primary key,
	partei integer REFERENCES Partei,
	bundesland integer REFERENCES Bundesland,
	unique (partei, bundesland)
);

-- Enthält Beziehung "wählt in"
CREATE TABLE Wahlberechtigte (
	ausweisnummer serial primary key,
	--vorname varchar(50),
	--nachname varchar(50),
	--geburtsdatum date,
	--strasse varchar(50),
	--hausnummer integer,
	--postleitzahl integer,
	--stadt varchar(50),
	gewaehlt boolean,
	wahlbezirk integer,
	wahlkreis integer,
	FOREIGN KEY (wahlbezirk,wahlkreis) REFERENCES Wahlbezirk
);

-- Enthält Beziehung "hat"
CREATE TABLE Wahlergebnis (
	id integer primary key,
	wahljahr integer,
	wahlkreis integer REFERENCES Wahlkreis
);

-- Enthält Beziehung "Direkterg. part of"
CREATE TABLE Direktergebnis (
	id serial primary key,
	stimmenanzahl integer,
	kandidat integer REFERENCES Kandidat,
	partei integer REFERENCES Partei,
	wahlergebnis integer REFERENCES Wahlergebnis
);

-- Enthält Beziehung "Listenerg. part of"
CREATE TABLE Listenergebnis (
	partei integer REFERENCES Partei,
	stimmenanzahl integer,
	wahlergebnis integer REFERENCES Wahlergebnis,
	primary key (partei, wahlergebnis)
);

-- Enthält Beziehungen "enthält Erststimme", "enthält Zweitstimme" und "abgegeben in"
CREATE TABLE Wahlzettel (
	id serial primary key,
	erststimme integer REFERENCES Kandidat,
	zweitstimme integer REFERENCES Partei,
	wahlbezirk integer,
	wahlkreis integer,
	FOREIGN KEY (wahlbezirk, wahlkreis) REFERENCES Wahlbezirk

);

-- Hilfstabelle für Werte wie ungültige Stimmen, Wahlberechtigte, etc.
-- Enthält Beziehung "Str. gehört zu"
CREATE TABLE Struktur (
	wahlkreis integer REFERENCES Wahlkreis,
	jahr integer,
	wahlberechtigte integer,
	waehler integer,
	ungueltig_erst integer,
	ungueltig_zweit integer,
	gueltig_erst integer,
	gueltig_zweit integer,
	primary key (wahlkreis, jahr)
);

-- Hilfstabelle für die Sitzplatzberechnung
CREATE TABLE Divisor(
	divisor decimal(4,1)
);

-- Beziehung "vertreten auf"
CREATE TABLE Landesliste_Kandidat (
	landesliste integer REFERENCES Landesliste,
	kandidat integer REFERENCES Kandidat unique,
	listenplatz integer,
	primary key (landesliste, kandidat),
	unique (landesliste, listenplatz)
);

-- Beziehung "kandidiert in"
CREATE TABLE Kandidat_Wahlkreis (
	kandidat integer REFERENCES Kandidat unique,
	wahlkreis integer REFERENCES Wahlkreis,
	primary key (kandidat, wahlkreis)
);

-- Beziehung "gewählt in"
CREATE TABLE Kandidaten_Gewaehlt (
	kandidat integer primary key references Kandidat,
	direktkandidat_wk integer references wahlkreis
);
