package beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import data.Bundesland;
import data.Einzelergebnis;
import data.Kandidat;
import data.KnappsterSieger;
import data.Listenkandidatur;
import data.Partei;
import data.Sitzverteilung;
import data.Ueberhangmandate;
import data.Wahlkreis;
import data.WahlkreisUebersicht;
import data.Wahlkreissieger;
import data.Wahlzettelauswahl;

public class Auswertung {
	private Connection connection = null;

	private int connectionCounter = 0;

	private void initConnection() {
		this.connectionCounter += 1;
		if (this.connection != null)
			return;
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Bundestagswahl");
			this.connection = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private void freeConnection() {
		try {
			this.connectionCounter -= 1;
			if (this.connectionCounter == 0) {
				this.connection.close();
				this.connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Q1: Sitzverteilung */
	/** TODO: SQL Query may be improved by crazy aggregation stuff :D */
	public Sitzverteilung getSitzverteilung() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("SELECT p.kurzbezeichnung FROM Kandidaten_gewaehlt g, "
						+ "Kandidat k, Partei p WHERE g.kandidat = k.ausweisnummer AND k.partei = p.nummer");

		Map<String, Integer> sitze = new HashMap<String, Integer>();
		while (result.next()) {
			String partei = result.getString("kurzbezeichnung");
			Integer current = sitze.get(partei);
			if (current != null)
				sitze.put(partei, Integer.valueOf(current.intValue() + 1));
			else {
				sitze.put(partei, Integer.valueOf(1));
			}
		}
		freeConnection();
		List<Einzelergebnis<Partei, Integer>> parteiSitze = new ArrayList<Einzelergebnis<Partei, Integer>>();
		for (String partei : sitze.keySet()) {
			parteiSitze.add(new Einzelergebnis<Partei, Integer>(new Partei(
					partei), sitze.get(partei)));
		}
		return new Sitzverteilung(parteiSitze);
	}

	/** Q2: Bundestagsmitglieder */
	public List<Kandidat> getBundestagsMitglieder() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<Kandidat> kandidaten = new ArrayList<Kandidat>();
		ResultSet result = stmt
				.executeQuery("SELECT * FROM Kandidaten_gewaehlt g "
						+ "JOIN Kandidat k ON g.kandidat = k.ausweisnummer "
						+ "JOIN Partei p ON k.partei = p.nummer "
						+ "LEFT OUTER JOIN (Landesliste_Kandidat lk "
						+ "JOIN Landesliste l ON lk.landesliste = l.id "
						+ "JOIN Bundesland b ON l.bundesland = b.nummer) ON lk.kandidat = g.kandidat");

		while (result.next()) {
			int id = result.getInt("ausweisnummer");
			String nachname = result.getString("nachname");
			String vorname = result.getString("vorname");
			Partei partei = new Partei(result.getString("kurzbezeichnung"));
			int wahlkreisId = result.getInt("direktkandidat_wk");
			String bundesland = result.getString("kuerzel");
			int platz = result.getInt("listenplatz");
			Listenkandidatur listenKandidatur = null;
			if (platz != 0) {
				listenKandidatur = new Listenkandidatur(new Bundesland(
						bundesland, null), platz);
			}
			kandidaten.add(new Kandidat(nachname, vorname, partei, wahlkreisId,
					listenKandidatur, id));
		}
		freeConnection();
		return kandidaten;
	}

	/** Helper method for jsps. */
	public List<Partei> getAllParteien() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<Partei> parteien = new ArrayList<Partei>();
		ResultSet result = stmt.executeQuery("SELECT * FROM partei");
		while (result.next()) {
			parteien.add(createPartei(result));
		}
		freeConnection();
		return parteien;
	}

	/** Creates a {@link Partei} object from a result set. */
	private Partei createPartei(ResultSet result) throws SQLException {
		String parteiName = result.getString("kurzbezeichnung");
		int id = result.getInt("nummer");
		Partei partei = new Partei(id, parteiName);
		return partei;
	}

	/** Gets a {@link Partei} object from its id. Convenience method for jsps */
	public Partei getPartei(int parteinr) throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("SELECT * FROM partei WHERE nummer = " + parteinr);
		Partei partei = null;
		while (result.next()) {
			partei = createPartei(result);
		}
		freeConnection();
		return partei;
	}

	/** Helper method for queries and jsps. */
	public List<Bundesland> getAllBundeslaender() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<Bundesland> bundeslaender = new ArrayList<Bundesland>();
		ResultSet result = stmt.executeQuery("SELECT * FROM bundesland");
		while (result.next()) {
			bundeslaender.add(createBundesland(result));
		}
		freeConnection();
		return bundeslaender;
	}

	/** Creates a {@link Bundesland} object from a result set. */
	private Bundesland createBundesland(ResultSet result) throws SQLException {
		Bundesland bundesland = new Bundesland();
		bundesland.setKuerzel(result.getString("kuerzel"));
		bundesland.setName(result.getString("name"));
		bundesland.setNummer(Integer.valueOf(result.getInt("nummer")));
		bundesland.setWahlkreise(getWahlkreiseForBundesland(bundesland
				.getNummer().intValue()));
		return bundesland;
	}

	/** Gets all {@link Wahlkreis}e for a {@link Bundesland}. */
	public List<Wahlkreis> getWahlkreiseForBundesland(int bundeslandNr)
			throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<Wahlkreis> wahlkreise = new ArrayList<Wahlkreis>();
		ResultSet wkResult = stmt
				.executeQuery("SELECT * FROM wahlkreis WHERE bundesland = "
						+ bundeslandNr);
		while (wkResult.next()) {
			wahlkreise.add(new Wahlkreis(wkResult.getInt("nummer"), wkResult
					.getString("name")));
		}
		freeConnection();
		return wahlkreise;
	}

	/**
	 * Gets a {@link Bundesland} object from its id. Convenience method for jsps
	 */
	public Bundesland getBundesland(int bundeslandnr) throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("SELECT * FROM bundesland WHERE nummer = "
						+ bundeslandnr);
		Bundesland bundesland = null;
		while (result.next()) {
			bundesland = createBundesland(result);
		}
		freeConnection();
		return bundesland;
	}

	/** Q3: Wahlkreis�bersicht */
	/**
	 * TODO: Implement Query in SQL; don't forget initConnection() and
	 * freeConnection() :)
	 * 
	 * @throws SQLException
	 */
	public WahlkreisUebersicht getWahlkreisUebersicht(int id)
			throws SQLException {
		initConnection();
		double wahlbeteiligung;
		String wk_name;
		List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = new ArrayList<Einzelergebnis<Partei, Integer>>();
		List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = new ArrayList<Einzelergebnis<Partei, Double>>();
		List<Einzelergebnis<Partei, Double>> stimmenProzentual = new ArrayList<Einzelergebnis<Partei, Double>>();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("SELECT CAST(waehler AS float)/wahlberechtigte FROM struktur WHERE wahlkreis = "
						+ id + " AND jahr = 2009");
		result.next();
		wahlbeteiligung = result.getDouble(1);

		result = stmt.executeQuery("SELECT name FROM wahlkreis WHERE nummer = "
				+ id);
		result.next();
		wk_name = result.getString(1);

		result = stmt
				.executeQuery("with gesamt09 as (select gueltig_zweit as gesamt from struktur where wahlkreis = "
						+ id
						+ " and jahr = 2009), "
						+ "gesamt05 as (select gueltig_zweit as gesamt from struktur where wahlkreis = "
						+ id
						+ " and jahr = 2005), "
						+ "old_prozente as (select l2.partei, cast(l2.stimmenanzahl as float)/gesamt05.gesamt as vor_stimmen "
						+ "from gesamt05, listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2005 and w2.wahlkreis = "
						+ id
						+ "), "
						+ "new_prozente as (select l2.partei, cast(l2.stimmenanzahl as float)/gesamt09.gesamt as vor_stimmen "
						+ "from gesamt09, listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2009 and w2.wahlkreis = "
						+ id
						+ ") "
						+ "select p.kurzbezeichnung, lw.stimmenanzahl as stimmen, old_prozente.vor_stimmen * 100 as prozente_old, "
						+ "new_prozente.vor_stimmen * 100 as prozente_new, (new_prozente.vor_stimmen - old_prozente.vor_stimmen) * 100 as prozente_diff "
						+ "from (listenergebnis l join wahlergebnis w on l.wahlergebnis = w.id) lw join partei p on lw.partei = p.nummer, old_prozente, "
						+ "new_prozente where lw.wahljahr = 2009 and lw.wahlkreis = "
						+ id
						+ " and old_prozente.partei = lw.partei and "
						+ "new_prozente.partei = lw.partei");
		while (result.next()) {
			// TODO: sum liste + direkt stimmen? prozente berechnen, entwicklung
			// berechnen
			stimmenAbsolut.add(new Einzelergebnis<Partei, Integer>(new Partei(
					result.getString(1)), Integer.valueOf(result.getInt(2))));
			stimmenProzentual.add(new Einzelergebnis<Partei, Double>(
					new Partei(result.getString(1)), Double.valueOf(result
							.getDouble(4))));
			stimmenEntwicklung.add(new Einzelergebnis<Partei, Double>(
					new Partei(result.getString(1)), Double.valueOf(result
							.getDouble(5))));
		}

		result = stmt
				.executeQuery("WITH wdk as (SELECT * FROM ((wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd"
						+ " JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer) wdk WHERE wdk.wahljahr = 2009 AND wdk.wahlkreis = "
						+ id
						+ ")"
						+ " SELECT wdk.vorname, wdk.nachname, wdk.kurzbezeichnung"
						+ " FROM wdk WHERE wdk.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk)");
		result.next();

		String vorname = result.getString(1);
		String nachname = result.getString(2);
		String partei = result.getString(3);

		WahlkreisUebersicht ret = new WahlkreisUebersicht(new Wahlkreis(id,
				wk_name), createKandidat(nachname, vorname, partei, 0),
				wahlbeteiligung, stimmenAbsolut, stimmenProzentual,
				stimmenEntwicklung);

		freeConnection();
		return ret;
	}

	/** Convenience method for stubbing. */
	private Kandidat createKandidat(String nachname, String vorname,
			String partei, int id) {
		return new Kandidat(nachname, vorname, new Partei(partei), 0, null, id);
	}

	/** Q4: Wahlkreissieger */
	/**
	 * TODO: Implement Query in SQL; don't forget initConnection() and
	 * freeConnection() :)
	 * 
	 * @throws SQLException
	 */
	public List<Wahlkreissieger> getWahlkreisSieger() throws SQLException {
		initConnection();
		List<Wahlkreissieger> sieger = new ArrayList<Wahlkreissieger>();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("WITH wdk as (SELECT * FROM ((wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) " +
						"wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer) wdk " +
						"WHERE wdk.wahljahr = 2009), " +
						"pwl as (SELECT * FROM ((wahlergebnis w JOIN listenergebnis l ON w.id = l.wahlergebnis) " +
						"wl JOIN partei p ON p.nummer = wl.partei) pwl WHERE pwl.wahljahr = 2009), " +
						"erst_sieger as (SELECT relation_outer.kurzbezeichnung, relation_outer.wahlkreis, relation_outer.stimmenanzahl, relation_outer.vorname, relation_outer.nachname " +
						"FROM wdk relation_outer WHERE relation_outer.stimmenanzahl = (SELECT MAX(stimmenanzahl) " +
						"FROM wdk relation_inner WHERE relation_outer.wahlkreis=relation_inner.wahlkreis)) " +
						"SELECT es.kurzbezeichnung as erstpartei, es.vorname, es.nachname, es.stimmenanzahl as erststimmen, zweit_outer.kurzbezeichnung as zweitsieger, zweit_outer.stimmenanzahl as zweitstimmen, zweit_outer.wahlkreis " +
						"FROM pwl zweit_outer JOIN erst_sieger es ON zweit_outer.wahlkreis = es.wahlkreis " +
						"WHERE zweit_outer.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM pwl zweit_inner " +
						"WHERE zweit_outer.wahlkreis=zweit_inner.wahlkreis);");
		while (result.next()) {
			Wahlkreissieger wahlkreissieger = new Wahlkreissieger(result.getInt(7),
					new Einzelergebnis<Kandidat, Integer>(new Kandidat(
							result.getString(3), result.getString(2), new Partei(result.getString(1)), result.getInt(7), null, 0), result.getInt(4)),
					new Einzelergebnis<Partei, Integer>(new Partei(result.getString(5)), result.getInt(6)));
			sieger.add(wahlkreissieger);
		}
		return sieger;
	}

	/** Q5: �berhangmandate */
	/**
	 * TODO: Implement Query in SQL; don't forget initConnection() and
	 * freeConnection() :)
	 */
	public List<Ueberhangmandate> getUeberhangmandate() throws SQLException {
		List<Ueberhangmandate> mandate = new ArrayList<Ueberhangmandate>();
		List<Bundesland> bundeslaender = getAllBundeslaender();
		for (Bundesland bundesland : bundeslaender) {
			List<Einzelergebnis<Partei, Integer>> ergebnisse = new ArrayList<Einzelergebnis<Partei, Integer>>();
			ergebnisse.add(new Einzelergebnis<Partei, Integer>(
					new Partei("SPD"), 29));
			ergebnisse.add(new Einzelergebnis<Partei, Integer>(
					new Partei("FDP"), 5));
			ergebnisse.add(new Einzelergebnis<Partei, Integer>(
					new Partei("CDU"), 7));
			ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei(
					"Gr�ne"), 3));
			Ueberhangmandate mandat = new Ueberhangmandate(bundesland,
					ergebnisse);
			mandate.add(mandat);
		}
		return mandate;
	}

	/** Q6: Knappste Sieger */
	/**
	 * TODO: Implement Query in SQL; don't forget initConnection() and
	 * freeConnection() :)
	 */
	public List<KnappsterSieger> getKnappsteSieger() {
		List<KnappsterSieger> knappsteSieger = new ArrayList<KnappsterSieger>();
		for (int i = 0; i < 10; i++) {
			Einzelergebnis<Kandidat, Integer> sieger = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat("Schmidt", "Horst", new Partei("SPD"), 5,
							null, 0), 73);
			Einzelergebnis<Kandidat, Integer> verlierer = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat("M�ller", "Hans", new Partei("FDP"), 5, null,
							0), 71);
			KnappsterSieger knSieger = new KnappsterSieger("SPD", sieger,
					verlierer);
			knappsteSieger.add(knSieger);
		}
		return knappsteSieger;
	}

	/** Q7: Wahlkreis�bersicht (Einzelstimmen) */
	/**
	 * TODO: Implement Query in SQL; don't forget initConnection() and
	 * freeConnection() :)
	 */
	public WahlkreisUebersicht getWahlkreisUebersichtEinzelstimmen(int id) {
		List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = new ArrayList<Einzelergebnis<Partei, Integer>>();
		stimmenAbsolut.add(new Einzelergebnis<Partei, Integer>(new Partei(
				"CDU/CSU"), Integer.valueOf(2499)));
		stimmenAbsolut.add(new Einzelergebnis<Partei, Integer>(
				new Partei("FDP"), Integer.valueOf(367)));
		stimmenAbsolut.add(new Einzelergebnis<Partei, Integer>(
				new Partei("SPD"), Integer.valueOf(1499)));
		List<Einzelergebnis<Partei, Double>> stimmenProzentual = new ArrayList<Einzelergebnis<Partei, Double>>();
		stimmenProzentual.add(new Einzelergebnis<Partei, Double>(new Partei(
				"CDU/CSU"), Double.valueOf(49.399999999999999D)));
		stimmenProzentual.add(new Einzelergebnis<Partei, Double>(new Partei(
				"FDP"), Double.valueOf(5.4D)));
		stimmenProzentual.add(new Einzelergebnis<Partei, Double>(new Partei(
				"SPD"), Double.valueOf(35.200000000000003D)));
		List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = new ArrayList<Einzelergebnis<Partei, Double>>();
		stimmenEntwicklung.add(new Einzelergebnis<Partei, Double>(new Partei(
				"CDU/CSU"), Double.valueOf(4.2D)));
		stimmenEntwicklung.add(new Einzelergebnis<Partei, Double>(new Partei(
				"FDP"), Double.valueOf(1.3D)));
		stimmenEntwicklung.add(new Einzelergebnis<Partei, Double>(new Partei(
				"SPD"), Double.valueOf(-4.7D)));
		return new WahlkreisUebersicht(new Wahlkreis(id, "Testwahlkreis"),
				createKandidat("K�hler", "Horst", "CSU", 0),
				68.700000000000003D, stimmenAbsolut, stimmenProzentual,
				stimmenEntwicklung);
	}

	/** Return Kandidats and Landeslists for an id of a citizen. */
	/* TODO: Implement */
	public Wahlzettelauswahl getWahlzettelauswahl(String persnr) {
		Wahlzettelauswahl auswahl = new Wahlzettelauswahl();
		List<Kandidat> kandidaten = new ArrayList<Kandidat>();
		kandidaten.add(createKandidat("Mueller", "Hans", "SPD", 0));
		kandidaten.add(createKandidat("Meier", "Horst", "FDP", 1));
		kandidaten.add(createKandidat("Kaufmann", "Anna", "Gruene", 2));
		kandidaten.add(createKandidat("Lieber", "Rudolf", "Piraten", 3));
		kandidaten.add(createKandidat("Mustermann", "Frederik", "Die Rosanen",
				4));
		auswahl.setKandidaten(kandidaten);
		List<Partei> parteien = new ArrayList<Partei>();
		parteien.add(new Partei(0, "SPD"));
		parteien.add(new Partei(1, "FDP"));
		parteien.add(new Partei(2, "Gruene"));
		parteien.add(new Partei(3, "CDU"));
		parteien.add(new Partei(4, "Die Grauen"));
		auswahl.setParteien(parteien);
		return auswahl;
	}

	/* TODO: Implement */
	public void setWahlzettel(int kandidatId, int parteiId, String hash) {

	}
}