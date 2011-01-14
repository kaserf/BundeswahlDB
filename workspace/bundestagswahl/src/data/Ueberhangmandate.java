package data;

import java.util.List;

public class Ueberhangmandate {
	private String bundesland;
	private List<Einzelergebnis<Partei, Integer>> mandate;
	
	public Ueberhangmandate(String bundesland, List<Einzelergebnis<Partei, Integer>> mandate) {
		setBundesland(bundesland);
		setMandate(mandate);
	}
	
	public void setBundesland(String bundesland) {
		this.bundesland = bundesland;
	}
	public String getBundesland() {
		return bundesland;
	}
	public void setMandate(List<Einzelergebnis<Partei, Integer>> mandate) {
		this.mandate = mandate;
	}
	public List<Einzelergebnis<Partei, Integer>> getMandate() {
		return mandate;
	}
}
