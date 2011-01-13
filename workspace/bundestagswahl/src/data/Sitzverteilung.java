 package data;
 
 import java.util.ArrayList;
import java.util.List;
 
 public class Sitzverteilung
 {
   private List<Einzelergebnis<Partei, Integer>> parteiSitze = new ArrayList<Einzelergebnis<Partei, Integer>>();
 
   public String toString()
   {
     return getParteiSitze().toString();
   }
 
   public Sitzverteilung(List<Einzelergebnis<Partei, Integer>> parteiSitze) {
     setParteiSitze(parteiSitze);
   }
 
   public void setParteiSitze(List<Einzelergebnis<Partei, Integer>> parteiSitze) {
     this.parteiSitze = parteiSitze;
   }
 
   public List<Einzelergebnis<Partei, Integer>> getParteiSitze() {
     return this.parteiSitze;
   }
 }
