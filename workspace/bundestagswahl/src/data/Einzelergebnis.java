 package data;
 
 public class Einzelergebnis<K, V extends Comparable<V>>
   implements Comparable<Einzelergebnis<K, V>>
 {
   private K entity;
   private V value;
 
   public Einzelergebnis(K entity, V value)
   {
     setEntity(entity);
     setValue(value);
   }
 
   public void setEntity(K entity) {
     this.entity = entity;
   }
 
   public K getEntity() {
     return this.entity;
   }
 
   public void setValue(V value) {
     this.value = value;
   }
 
   public V getValue() {
     return this.value;
   }
 
   public int compareTo(Einzelergebnis<K, V> o)
   {
     return this.value.compareTo(o.getValue());
   }
 }
