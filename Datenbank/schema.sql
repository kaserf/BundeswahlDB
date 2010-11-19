-- drop alle Tabellen
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE Partei (
	nummer integer primary key,
	kurzbezeichnung varchar(50),
	name varchar(100)
);

CREATE TABLE Kandidat (
	ausweisnummer integer primary key,
	vorname varchar(50),
	nachname varchar(50),
	geburtsjahr varchar(4),
	partei integer REFERENCES Partei
	--strasse varchar(50),
	--hausnummer integer,
	--postleitzahl integer,
	--stadt varchar(50)
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
	id serial primary key,
	nummer integer,
	--name varchar(50),
	--wahlvorstand varchar(50),
	--strasse varchar(50),
	--hausnummer integer,
	--postleitzahl integer,
	--stadt varchar,
	wahlkreis integer REFERENCES Wahlkreis,
	unique (nummer, wahlkreis)
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
	vorname varchar(50),
	nachname varchar(50),
	geburtsdatum varchar(10),
	strasse varchar(50),
	hausnummer integer,
	postleitzahl integer,
	stadt varchar(50),
	wahlbezirk integer REFERENCES Wahlbezirk,
	gewaehlt boolean
);

-- Enthält Beziehung "hat"
CREATE TABLE Wahlergebnis (
	wahljahr integer primary key,
	wahlkreis integer REFERENCES Wahlkreis
);

-- Enthält Beziehung "part of"
CREATE TABLE Direktergebnis (
	kandidat varchar(50),
	partei varchar(50),
	stimmenanzahl integer,
	wahlergebnis integer REFERENCES Wahlergebnis,
	primary key (partei, wahlergebnis)
);

-- Enthält Beziehung "part of"
CREATE TABLE Listenergebnis (
	partei varchar(50),
	stimmenanzahl integer,
	wahlergebnis integer REFERENCES Wahlergebnis,
	primary key (partei, wahlergebnis)
);

-- Enthält Beziehungen "enthält Erststimme", "enthält Zweitstimme" und "abgegeben in"
CREATE TABLE Wahlzettel (
	id serial primary key,
	erststimme integer REFERENCES Kandidat,
	zweitstimme integer REFERENCES Partei,
	wahlbezirk integer REFERENCES Wahlbezirk
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
