 package data;
 
 import java.util.List;
 
 public class Bundesland
 {
   private String name;
   private Integer nummer;
   private String kuerzel;
   private List<Wahlkreis> wahlkreise;

   
   public Bundesland(String name, List<Wahlkreis> wahlkreise)
   {
     setName(name);
     setWahlkreise(wahlkreise);
   }
 
   public Bundesland() {
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public String getName() {
     return this.name;
   }
 
   public void setWahlkreise(List<Wahlkreis> wahlkreise) {
     this.wahlkreise = wahlkreise;
   }
 
   public List<Wahlkreis> getWahlkreise() {
     return this.wahlkreise;
   }
 
   public void setNummer(Integer nummer) {
     this.nummer = nummer;
   }
 
   public Integer getNummer() {
     return this.nummer;
   }
 
   public void setKuerzel(String kuerzel) {
     this.kuerzel = kuerzel;
   }
 
   public String getKuerzel() {
     return this.kuerzel;
   }
 }