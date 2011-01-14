 package beans;
 
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
 
 public class Auswertung
 {
   private Connection connection = null;
 
   private int connectionCounter = 0;
 
   private void initConnection() {
     this.connectionCounter += 1;
     if (this.connection != null)
       return;
     try
     {
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
       .executeQuery("SELECT p.kurzbezeichnung FROM Kandidaten_gewaehlt g, Kandidat k, Partei p WHERE g.kandidat = k.ausweisnummer AND k.partei = p.nummer");
 
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
       parteiSitze.add(
         new Einzelergebnis<Partei, Integer>(new Partei(partei), sitze.get(partei)));
     }
     return new Sitzverteilung(parteiSitze);
   }
 
   /** Q2: Bundestagsmitglieder */
   public List<Kandidat> getBundestagsMitglieder() throws SQLException
   {
     initConnection();
     Statement stmt = this.connection.createStatement();
     List<Kandidat> kandidaten = new ArrayList<Kandidat>();
     ResultSet result = stmt
       .executeQuery("SELECT * FROM Kandidaten_gewaehlt g " +
       		"JOIN Kandidat k ON g.kandidat = k.ausweisnummer " +
       		"JOIN Partei p ON k.partei = p.nummer " +
       		"LEFT OUTER JOIN (Landesliste_Kandidat lk " +
       		"JOIN Landesliste l ON lk.landesliste = l.id " +
       		"JOIN Bundesland b ON l.bundesland = b.nummer) ON lk.kandidat = g.kandidat");
 
     while (result.next()) {
       String nachname = result.getString("nachname");
       String vorname = result.getString("vorname");
       Partei partei = new Partei(result.getString("kurzbezeichnung"));
       int wahlkreisId = result.getInt("direktkandidat_wk");
       String bundesland = result.getString("kuerzel");
       int platz = result.getInt("listenplatz");
       Listenkandidatur listenKandidatur = null;
       if (platz != 0) {
         listenKandidatur = new Listenkandidatur(
           new Bundesland(bundesland, null), platz);
       }
       kandidaten.add(
         new Kandidat(nachname, vorname, partei, wahlkreisId, 
         listenKandidatur));
     }
     freeConnection();
     return kandidaten;
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
   public List<Wahlkreis> getWahlkreiseForBundesland(int bundeslandNr) throws SQLException
   {
     initConnection();
     Statement stmt = this.connection.createStatement();
     List<Wahlkreis> wahlkreise = new ArrayList<Wahlkreis>();
     ResultSet wkResult = stmt
       .executeQuery("SELECT * FROM wahlkreis WHERE bundesland = " + 
       bundeslandNr);
     while (wkResult.next()) {
       wahlkreise.add(
         new Wahlkreis(wkResult.getInt("nummer"), wkResult
         .getString("name")));
     }
     freeConnection();
     return wahlkreise;
   }
 
   /** Gets a {@link Bundesland} object from its id. Convenience method for jsps */
   public Bundesland getBundesland(int bundeslandnr) throws SQLException {
     initConnection();
     Statement stmt = this.connection.createStatement();
     ResultSet result = stmt
       .executeQuery("SELECT * FROM bundesland WHERE nummer = " + 
       bundeslandnr);
     Bundesland bundesland = null;
     while (result.next()) {
       bundesland = createBundesland(result);
     }
     freeConnection();
     return bundesland;
   }
 
   /** Q3: Wahlkreisübersicht */
   /** TODO: Implement Query in SQL; don't forget initConnection() and freeConnection() :) */
   public WahlkreisUebersicht getWahlkreisUebersicht(int id) {
     List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = new ArrayList<Einzelergebnis<Partei, Integer>>();
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("CDU/CSU"), Integer.valueOf(2499)));
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("FDP"), Integer.valueOf(367)));
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("SPD"), Integer.valueOf(1499)));
     List<Einzelergebnis<Partei, Double>> stimmenProzentual = new ArrayList<Einzelergebnis<Partei, Double>>();
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("CDU/CSU"), Double.valueOf(49.399999999999999D)));
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("FDP"), Double.valueOf(5.4D)));
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("SPD"), Double.valueOf(35.200000000000003D)));
     List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = new ArrayList<Einzelergebnis<Partei, Double>>();
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("CDU/CSU"), Double.valueOf(4.2D)));
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("FDP"), Double.valueOf(1.3D)));
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("SPD"), Double.valueOf(-4.7D)));
     return new WahlkreisUebersicht(new Wahlkreis(id, "Testwahlkreis"), 
       createKandidat("Aigner", "Ilse", "CSU", 224, "BY", 4), 68.700000000000003D, 
       stimmenAbsolut, stimmenProzentual, stimmenEntwicklung);
   }
   
   /** Convenience method for stubbing. */
   private Kandidat createKandidat(String nachname, String vorname, String partei, int wkr, String bundesland, int platz)
   {
     Listenkandidatur kandidatur = new Listenkandidatur(
       new Bundesland(bundesland, null), platz);
     return new Kandidat(nachname, vorname, new Partei(partei), wkr, 
       kandidatur);
   }
   
   /** Q4: Wahlkreissieger */
   /** TODO: Implement Query in SQL; don't forget initConnection() and freeConnection() :) */
   public List<Wahlkreissieger> getWahlkreisSieger() {
	   List<Wahlkreissieger> sieger = new ArrayList<Wahlkreissieger>();
	   for (int i = 0; i < 299; i++) {
		   Wahlkreissieger wahlkreissieger = new Wahlkreissieger(i, 
				   new Einzelergebnis<Kandidat, Integer>(new Kandidat("Schmidt", "Horst", null, i, null), 85), 
				   new Einzelergebnis<Partei, Integer>(new Partei("SPD"), 35));
		   sieger.add(wahlkreissieger);
	   }
	   return sieger;
   }
 
   /** Q5: Überhangmandate */
   /** TODO: Implement Query in SQL; don't forget initConnection() and freeConnection() :) */
   public List<Ueberhangmandate> getUeberhangmandate() throws SQLException {
	   List<Ueberhangmandate> mandate = new ArrayList<Ueberhangmandate>();
	   List<Bundesland> bundeslaender = getAllBundeslaender();
	   for (Bundesland bundesland : bundeslaender) {
		   List<Einzelergebnis<Partei, Integer>> ergebnisse = new ArrayList<Einzelergebnis<Partei,Integer>>();
		   ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei("SPD"), 29));
		   ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei("FDP"), 5));
		   ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei("CDU"), 7));
		   ergebnisse.add(new Einzelergebnis<Partei, Integer>(new Partei("Grüne"), 3));
		   Ueberhangmandate mandat = new Ueberhangmandate(bundesland.getName(), ergebnisse);
		   mandate.add(mandat);
	   }
	   return mandate;
   }
 
   /** Q6: Knappste Sieger */
   /** TODO: Implement Query in SQL; don't forget initConnection() and freeConnection() :) */
   public List<KnappsterSieger> getKnappsteSieger() {
     return null;
   }
   
   /** Q7: Wahlkreisübersicht (Einzelstimmen) */
   /** TODO: Implement Query in SQL; don't forget initConnection() and freeConnection() :) */
   public WahlkreisUebersicht getWahlkreisUebersichtEinzelstimmen(int id) {
     List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = new ArrayList<Einzelergebnis<Partei, Integer>>();
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("CDU/CSU"), Integer.valueOf(2499)));
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("FDP"), Integer.valueOf(367)));
     stimmenAbsolut.add(
       new Einzelergebnis<Partei, Integer>(new Partei("SPD"), Integer.valueOf(1499)));
     List<Einzelergebnis<Partei, Double>> stimmenProzentual = new ArrayList<Einzelergebnis<Partei, Double>>();
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("CDU/CSU"), Double.valueOf(49.399999999999999D)));
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("FDP"), Double.valueOf(5.4D)));
     stimmenProzentual.add(
       new Einzelergebnis<Partei, Double>(new Partei("SPD"), Double.valueOf(35.200000000000003D)));
     List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = new ArrayList<Einzelergebnis<Partei, Double>>();
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("CDU/CSU"), Double.valueOf(4.2D)));
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("FDP"), Double.valueOf(1.3D)));
     stimmenEntwicklung.add(
       new Einzelergebnis<Partei, Double>(new Partei("SPD"), Double.valueOf(-4.7D)));
     return new WahlkreisUebersicht(new Wahlkreis(id, "Testwahlkreis"), 
       createKandidat("Aigner", "Ilse", "CSU", 224, "BY", 4), 68.700000000000003D, 
       stimmenAbsolut, stimmenProzentual, stimmenEntwicklung);
   }
   
 }