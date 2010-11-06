-- drop alle Tabellen
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE Partei (
	name varchar(50) primary key
);

CREATE TABLE Kandidat (
	ausweisnummer integer primary key,
	vorname varchar(50),
	nachname varchar(50),
	strasse varchar(50),
	hausnummer integer,
	postleitzahl integer,
	stadt varchar(50)
);

CREATE TABLE Bundesland (
	kuerzel varchar(10) primary key,
	name varchar(50),
	hauptstadt varchar(50)
);

-- Enthält Beziehung "WK liegt in"
CREATE TABLE Wahlkreis (
	nummer integer primary key,
	name varchar(50),
	bundesland varchar(10) REFERENCES Bundesland
);

-- Enthält Beziehung "WB liegt in"
CREATE TABLE Wahlbezirk (
	nummer serial primary key,
	wahlvorstand varchar(50),
	strasse varchar(50),
	hausnummer integer,
	postleitzahl integer,
	stadt varchar,
	wahlkreis integer REFERENCES Wahlkreis
);

-- Enthält Beziehung "gehört zu", "aufgestellt für"
CREATE TABLE Landesliste (
	id serial primary key,
	partei varchar(50) REFERENCES Partei,
	bundesland varchar(10) REFERENCES Bundesland,
	unique (partei, bundesland)
);

-- Enthält Beziehung "wählt in"
CREATE TABLE Wahlberechtigte (
	ausweisnummer integer primary key,
	vorname varchar(50),
	nachname varchar(50),
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
	primary key (kandidat, wahlergebnis)
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
	zweitstimme varchar(50) REFERENCES Partei,
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
