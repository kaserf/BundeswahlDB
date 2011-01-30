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

	/**
	 * Q3: Wahlkreisuebersicht
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

	/**
	 * Q4: Wahlkreissieger
	 */
	public List<Wahlkreissieger> getWahlkreisSieger() throws SQLException {
		initConnection();
		List<Wahlkreissieger> sieger = new ArrayList<Wahlkreissieger>();
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt
				.executeQuery("WITH "
						+ "wdk as (SELECT * FROM ((wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd "
						+ "JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON wd.kandidat = pk.ausweisnummer) wdk "
						+ "WHERE wdk.wahljahr = 2009),	pwl as (SELECT * FROM ((wahlergebnis w JOIN listenergebnis l ON w.id = l.wahlergebnis) wl "
						+ "JOIN partei p ON p.nummer = wl.partei) pwl "
						+ "WHERE pwl.wahljahr = 2009),	pwl_filtered as (SELECT * FROM pwl pwl_inner "
						+ "WHERE pwl_inner.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM pwl pwl_inner2 WHERE pwl_inner2.wahlkreis = pwl_inner.wahlkreis)), "
						+ "erst_sieger as (SELECT relation_outer.kurzbezeichnung, relation_outer.wahlkreis, relation_outer.stimmenanzahl, "
						+ "relation_outer.vorname, relation_outer.nachname "
						+ "FROM wdk relation_outer WHERE relation_outer.stimmenanzahl = "
						+ "(SELECT MAX(stimmenanzahl) FROM wdk relation_inner "
						+ "WHERE relation_outer.wahlkreis=relation_inner.wahlkreis)) "
						+ "SELECT "
						+ "es.kurzbezeichnung as erstpartei, "
						+ "es.vorname,	es.nachname, "
						+ "es.stimmenanzahl as erststimmen, "
						+ "zweit.kurzbezeichnung as zweitsieger, "
						+ "zweit.stimmenanzahl as zweitstimmen, "
						+ "zweit.wahlkreis FROM pwl_filtered zweit JOIN erst_sieger es ON zweit.wahlkreis = es.wahlkreis;");
		while (result.next()) {
			Wahlkreissieger wahlkreissieger = new Wahlkreissieger(
					result.getInt(7), new Einzelergebnis<Kandidat, Integer>(
							new Kandidat(result.getString(3),
									result.getString(2), new Partei(
											result.getString(1)),
									result.getInt(7), null, 0),
							result.getInt(4)),
					new Einzelergebnis<Partei, Integer>(new Partei(result
							.getString(5)), result.getInt(6)));
			sieger.add(wahlkreissieger);
		}
		freeConnection();
		return sieger;
	}

	/**
	 * Q5: Ueberhangmandate
	 */
	public List<Ueberhangmandate> getUeberhangmandate() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<Ueberhangmandate> mandate = new ArrayList<Ueberhangmandate>();
		ResultSet result = stmt
				.executeQuery("with ergebnis_pro_wk as (select L.Partei, E.Wahlkreis, L.Stimmenanzahl from Listenergebnis L join Wahlergebnis E on L.wahlergebnis = E.id where E.wahljahr = 2009),"
						+ "direktergebnis_pro_wk as (select D.Kandidat, E.Wahlkreis, D.Stimmenanzahl from Direktergebnis D join Wahlergebnis E on D.wahlergebnis = E.id where E.wahljahr = 2009),"
						+ "struktur_deutschland as (select sum(gueltig_zweit) as gueltige_stimmen from Struktur where jahr = 2009),"
						+ "parteien_deutschland as (select Partei, sum(stimmenanzahl) as partei_stimmen, cast(sum(stimmenanzahl) as float)/(select gueltige_stimmen from struktur_deutschland) as prozente	from ergebnis_pro_wk	group by Partei),"
						+ "parteien_bundesland as (select w.bundesland, e.Partei, sum(stimmenanzahl) as partei_stimmen from ergebnis_pro_wk e join wahlkreis w on e.wahlkreis = w.nummer group by e.Partei, w.bundesland),"
						+ "partei_dividiert as (select S.Partei, S.partei_stimmen / D.divisor from parteien_deutschland S, Divisor D where S.prozente >= 0.05 order by S.partei_stimmen / D.divisor desc limit (598)),"
						+ "parteien_sitze as (select D.Partei, count(*) as Sitze from partei_dividiert D group by D.Partei	order by Sitze desc),"
						+ "parteien_bundesland_ranking as (select dense_rank() over (partition by P.Partei order by P.partei_stimmen / D.divisor desc) as Rang, P.Partei, P.Bundesland, P.partei_stimmen / D.divisor from parteien_bundesland P, Divisor D order by P.Partei,Rang),"
						+ "parteien_bundesland_sitze as (select D.Partei, D.Bundesland, count(*) as Sitze from parteien_bundesland_ranking D, parteien_sitze S where D.Partei = S.Partei and D.Rang <= S.Sitze group by D.Partei,D.Bundesland order by D.Partei),"
						+ "direktkandidaten_gewaehlt as (select A.Kandidat, A.Wahlkreis from direktergebnis_pro_wk A where A.stimmenanzahl=(select max(B.stimmenanzahl) from direktergebnis_pro_wk B where A.Wahlkreis=B.Wahlkreis)),"
						+ "direktmandate_parteien_bundesland as (select K.Partei, W.Bundesland, count(*) as Direktmandate from direktkandidaten_gewaehlt G, Kandidat K, Wahlkreis W where G.Wahlkreis=W.Nummer and G.Kandidat=K.ausweisnummer and K.Partei <> 99 group by K.Partei,W.Bundesland),"
						+ "ueberhang as (select D.Partei, D.Bundesland, (case when M.Direktmandate - D.Sitze > 0 then M.Direktmandate - D.Sitze else 0 end) as Ueberhangmandate from parteien_bundesland_sitze D left outer join direktmandate_parteien_bundesland M on D.Partei=M.Partei and D.Bundesland=M.Bundesland)"
						+ "select b.name, b.kuerzel, up.kurzbezeichnung, up.ueberhangmandate from (ueberhang u join partei p on u.partei = p.nummer) up join bundesland b on up.bundesland = b.nummer where up.ueberhangmandate > 0;");
		while (result.next()) {
			List<Einzelergebnis<Partei, Integer>> ergebnisse = new ArrayList<Einzelergebnis<Partei, Integer>>();
			ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei(
					result.getString(3)), result.getInt(4)));
			Bundesland bl = new Bundesland(result.getString(1), null);
			bl.setKuerzel(result.getString(2));
			mandate.add(new Ueberhangmandate(bl, ergebnisse));
		}
		freeConnection();
		return mandate;
	}

	/** Q6: Knappste Sieger */
	/**
	 * @throws SQLException
	 */
	public List<KnappsterSieger> getKnappsteSieger() throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		List<KnappsterSieger> knappsteSieger = new ArrayList<KnappsterSieger>();
		ResultSet result = stmt.executeQuery("select max(nummer) from partei");
		result.next();
		int max_partei = result.getInt(1);
		int parteien_count[] = new int[max_partei];

		result = stmt
				.executeQuery("with wdk as (SELECT wd.wahlkreis, pk.ausweisnummer, pk.partei, pk.kurzbezeichnung, pk.vorname, pk.nachname, wd.stimmenanzahl "
						+ "FROM (wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk ON "
						+ "wd.kandidat = pk.ausweisnummer WHERE wd.wahljahr = 2009), "
						+ "sieger as (SELECT * FROM wdk wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) FROM wdk wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)), "
						+ "ohne_sieger as (SELECT * FROM wdk EXCEPT SELECT * FROM sieger), "
						+ "zweite as (SELECT * FROM ohne_sieger wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) "
						+ "FROM ohne_sieger wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)), "
						+ "knappste_sieger as (SELECT s.wahlkreis, s.partei as sieger_p_nummer, s.vorname as sieger_v, s.nachname as sieger_n, "
						+ "s.kurzbezeichnung as sieger_partei, s.stimmenanzahl as sieger_stimmen, z.stimmenanzahl as zweiter_stimmen, "
						+ "z.vorname as zweiter_v, z.nachname as zweiter_n, z.partei as zweiter_p_nummer, z.kurzbezeichnung as zweiter_partei "
						+ "FROM sieger s join zweite z on s.wahlkreis = z.wahlkreis) "
						+ "SELECT * FROM knappste_sieger ORDER BY (sieger_stimmen - zweiter_stimmen) ASC;");
		while (result.next()) {
			if (parteien_count[result.getInt("sieger_p_nummer") - 1] == 10)
				continue;
			Einzelergebnis<Kandidat, Integer> sieger = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat(result.getString("sieger_n"),
							result.getString("sieger_v"), new Partei(
									result.getString("sieger_partei")),
							result.getInt("wahlkreis"), null, 0),
					result.getInt("sieger_stimmen"));
			Einzelergebnis<Kandidat, Integer> verlierer = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat(result.getString("zweiter_n"),
							result.getString("zweiter_v"), new Partei(
									result.getString("zweiter_partei")),
							result.getInt("wahlkreis"), null, 0),
					result.getInt("zweiter_stimmen"));
			KnappsterSieger knSieger = new KnappsterSieger(
					result.getString("sieger_partei"), sieger, verlierer);
			knappsteSieger.add(knSieger);
			parteien_count[result.getInt("sieger_p_nummer") - 1]++;
		}

		result = stmt
				.executeQuery("with wdk as (SELECT wd.wahlkreis, pk.ausweisnummer, pk.partei, pk.kurzbezeichnung, pk.vorname, pk.nachname, wd.stimmenanzahl "
						+ "FROM (wahlergebnis w JOIN direktergebnis d ON w.id = d.wahlergebnis) wd JOIN (kandidat k JOIN partei p ON p.nummer = k.partei) pk "
						+ "ON wd.kandidat = pk.ausweisnummer WHERE wd.wahljahr = 2009), "
						+ "sieger as (SELECT * FROM wdk wdk_1 WHERE wdk_1.stimmenanzahl = (SELECT MAX(stimmenanzahl) "
						+ "FROM wdk wdk_2 WHERE wdk_1.wahlkreis=wdk_2.wahlkreis)), "
						+ "parteien_ohne_sieger as (SELECT * FROM partei p WHERE p.nummer NOT IN (SELECT DISTINCT partei FROM sieger)), "
						+ "pos_kandidaten as (SELECT * FROM wdk WHERE partei IN (SELECT nummer FROM parteien_ohne_sieger)), "
						+ "knappste_verlierer as (SELECT posk.wahlkreis, posk.partei as verlierer_p_nummer, posk.vorname as verlierer_v, "
						+ "posk.nachname as verlierer_n, posk.kurzbezeichnung as verlierer_partei, posk.stimmenanzahl as verlierer_stimmen, "
						+ "s.stimmenanzahl as sieger_stimmen, s.vorname as sieger_v, s.nachname as sieger_n, s.kurzbezeichnung as sieger_partei "
						+ "FROM pos_kandidaten posk JOIN sieger s ON posk.wahlkreis = s.wahlkreis) "
						+ "SELECT * FROM knappste_verlierer ORDER BY (verlierer_stimmen - sieger_stimmen) DESC;");
		while (result.next()) {
			if (parteien_count[result.getInt("verlierer_p_nummer") - 1] == 10)
				continue;
			Einzelergebnis<Kandidat, Integer> sieger = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat(result.getString("sieger_n"),
							result.getString("sieger_v"), new Partei(
									result.getString("sieger_partei")),
							result.getInt("wahlkreis"), null, 0),
					result.getInt("sieger_stimmen"));
			Einzelergebnis<Kandidat, Integer> verlierer = new Einzelergebnis<Kandidat, Integer>(
					new Kandidat(result.getString("verlierer_n"),
							result.getString("verlierer_v"), new Partei(
									result.getString("verlierer_partei")),
							result.getInt("wahlkreis"), null, 0),
					result.getInt("verlierer_stimmen"));
			KnappsterSieger knSieger = new KnappsterSieger(
					result.getString("verlierer_partei"), sieger, verlierer);
			knappsteSieger.add(knSieger);
			parteien_count[result.getInt("verlierer_p_nummer") - 1]++;
		}
		freeConnection();
		return knappsteSieger;
	}

	/** Q7: Wahlkreisuebersicht (Einzelstimmen) */
	public WahlkreisUebersicht getWahlkreisUebersichtEinzelstimmen(int id)
			throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		String wk_name;
		List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = new ArrayList<Einzelergebnis<Partei, Integer>>();
		List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = new ArrayList<Einzelergebnis<Partei, Double>>();
		List<Einzelergebnis<Partei, Double>> stimmenProzentual = new ArrayList<Einzelergebnis<Partei, Double>>();
		ResultSet result = stmt
				.executeQuery("SELECT (CAST (anz_gew as float)/anz_ber) * 100 as Beteiligung "
						+ "FROM (SELECT COUNT(*) as anz_gew FROM Wahlberechtigte WHERE gewaehlt = true AND wahlkreis = "
						+ id
						+ ") AS gewaehlt_kreis, (SELECT COUNT(*) as anz_ber FROM Wahlberechtigte WHERE wahlkreis = 55) AS berechtigte_kreis;");
		result.next();
		double wahlbeteiligung = result.getDouble(1);

		result = stmt.executeQuery("SELECT name FROM wahlkreis WHERE nummer = "
				+ id);
		result.next();
		wk_name = result.getString(1);

		result = stmt
				.executeQuery("with stimmen_pro_partei as (select zweitstimme as partei, COUNT(*) as stimmenanzahl FROM Wahlzettel WHERE Wahlkreis = "
						+ id
						+ " GROUP BY zweitstimme), "
						+ "gesamt09 as (select SUM(stimmenanzahl) as gesamt from stimmen_pro_partei), "
						+ "gesamt05 as (select gueltig_zweit as gesamt from struktur where wahlkreis = "
						+ id
						+ " and jahr = 2005), "
						+ "old_prozente as (select l2.partei, cast(l2.stimmenanzahl as float)/gesamt05.gesamt as vor_stimmen from gesamt05, "
						+ "listenergebnis l2 join wahlergebnis w2 on l2.wahlergebnis = w2.id where w2.wahljahr = 2005 and w2.wahlkreis = "
						+ id
						+ "), new_prozente as (select spp.partei, cast(spp.stimmenanzahl as float)/gesamt09.gesamt as vor_stimmen from gesamt09, "
						+ "stimmen_pro_partei spp) select p.kurzbezeichnung, spp.stimmenanzahl as stimmen, old_prozente.vor_stimmen * 100 as prozente_old, "
						+ "new_prozente.vor_stimmen * 100 as prozente_new, (new_prozente.vor_stimmen - old_prozente.vor_stimmen) * 100 as prozente_diff "
						+ "from stimmen_pro_partei spp join partei p on spp.partei = p.nummer, old_prozente, new_prozente where old_prozente.partei = spp.partei "
						+ "and new_prozente.partei = spp.partei;");
		while (result.next()) {
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
				.executeQuery("WITH stimmen_pro_kandidat (kandidat, anz) AS (SELECT erststimme, COUNT(*) FROM Wahlzettel WHERE Wahlkreis = "
						+ id
						+ " GROUP BY erststimme), stimmen_pro_partei (partei_nr, anz_p) AS (SELECT zweitstimme, COUNT(*) FROM Wahlzettel WHERE Wahlkreis = "
						+ id
						+ " GROUP BY zweitstimme) SELECT vorname, nachname, kurzbezeichnung FROM Kandidat k join Partei p on p.nummer = k.partei "
						+ "WHERE ausweisnummer = (SELECT kandidat FROM stimmen_pro_kandidat WHERE anz = (SELECT MAX(anz) FROM stimmen_pro_kandidat));");
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

	/**
	 * Check whether Personalnummer is valid.
	 * 
	 * @throws SQLException
	 */
	public boolean checkPersNr(String persNr) throws SQLException {
		initConnection();
		boolean ret = false;

		try {
			int ausweisnr = Integer.parseInt(persNr);
			Statement stmt = this.connection.createStatement();
			ResultSet result = stmt
					.executeQuery("select * from wahlberechtigte where ausweisnummer = "
							+ ausweisnr + " and gewaehlt = false");
			if (result.next())
				ret = true;
			else
				ret = false;
		} catch (NumberFormatException e) {
			ret = false;
		}
		freeConnection();
		return ret;
	}

	/** Return Kandidats and Landeslists for an id of a citizen. */
	public Wahlzettelauswahl getWahlzettelauswahl(String persnr)
			throws SQLException {
		Wahlzettelauswahl auswahl = new Wahlzettelauswahl();
		List<Kandidat> kandidaten = new ArrayList<Kandidat>();
		kandidaten.add(createKandidat("Mueller", "Hans", "SPD", 0));
		kandidaten.add(createKandidat("Meier", "Horst", "FDP", 1));
		kandidaten.add(createKandidat("Kaufmann", "Anna", "Gruene", 2));
		kandidaten.add(createKandidat("Lieber", "Rudolf", "Piraten", 3));
		kandidaten.add(createKandidat("Mustermann", "Frederik", "Die Rosanen",
				4));
		auswahl.setKandidaten(kandidaten);
		auswahl.setWahlbezirk(1);
		auswahl.setWahlkreis(2);
		List<Partei> parteien = new ArrayList<Partei>();
		parteien.add(new Partei(0, "SPD"));
		parteien.add(new Partei(1, "FDP"));
		parteien.add(new Partei(2, "Gruene"));
		parteien.add(new Partei(3, "CDU"));
		parteien.add(new Partei(4, "Die Grauen"));
		auswahl.setParteien(parteien);
		return auswahl;
	}

	public void setGewaehlt(int personalNummer) throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		stmt.executeQuery("update wahlberechtigte set gewaehlt = true where ausweisnummer = "
				+ personalNummer);
		freeConnection();
	}

	public void setWahlzettel(int kandidatId, int parteiId, int wahlbezirk,
			int wahlkreis) throws SQLException {
		initConnection();
		Statement stmt = this.connection.createStatement();
		stmt.executeQuery("insert into wahlzettel (erststimme, zweitstimme, wahlbezirk, wahlkreis) values("
				+ kandidatId
				+ ", "
				+ parteiId
				+ ", "
				+ wahlbezirk
				+ ", "
				+ wahlkreis + ")");
		freeConnection();
	}
}