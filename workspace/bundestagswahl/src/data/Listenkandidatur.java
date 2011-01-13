 package data;
 
 public class Listenkandidatur
 {
   private Bundesland bundesland;
   private int listenplatz;
 
   public Listenkandidatur(Bundesland bundesland, int platz)
   {
     setBundesland(bundesland);
     setListenplatz(platz);
   }
 
   public void setBundesland(Bundesland bundesland) {
     this.bundesland = bundesland;
   }
 
   public Bundesland getBundesland() {
     return this.bundesland;
   }
 
   public void setListenplatz(int listenplatz) {
     this.listenplatz = listenplatz;
   }
 
   public int getListenplatz() {
     return this.listenplatz;
   }
 }
