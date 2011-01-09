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
	--geburtsjahr zu date wechseln?
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

-- TODO: in doku eintragen
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

-- Enthält Beziehung "WB liegt in"
CREATE TABLE Wahlbezirk (
	nummer integer,
	--name varchar(50),
	--wahlvorstand varchar(50),
	--strasse varchar(50),
	--hausnummer integer,
	--postleitzahl integer,
	--stadt varchar,
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
	--geburtsdatum als DATE?
	--geburtsdatum varchar(10),
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

-- Enthält Beziehung "part of"
-- TODO: Referenzierung in der Doku ergänzen
CREATE TABLE Direktergebnis (
	id serial primary key,
	kandidat integer REFERENCES Kandidat,
	partei integer REFERENCES Partei,
	stimmenanzahl integer,
	wahlergebnis integer REFERENCES Wahlergebnis
	--primary key (kandidat, wahlergebnis)
);

-- Enthält Beziehung "part of"
-- TODO: Referenzierung in der Doku ergänzen
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

-- Tabelle für die gewählten Kandidaten (Kandidaten mit Sitze)
CREATE TABLE Kandidaten_Gewaehlt (
	kandidat integer primary key references Kandidat,
	direktkandidat_wk integer references wahlkreis
);

-- Tabelle für die Divisoren
CREATE TABLE Divisor(
	divisor decimal(4,1)
);
