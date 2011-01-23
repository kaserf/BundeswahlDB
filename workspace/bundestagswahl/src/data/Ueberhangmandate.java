package data;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Ueberhangmandate {
	private Bundesland bundesland;
	private List<Einzelergebnis<Partei, Integer>> mandate;
	
	public Ueberhangmandate() {
	}
	
	public Ueberhangmandate(Bundesland bundesland, List<Einzelergebnis<Partei, Integer>> mandate) {
		setBundesland(bundesland);
		setMandate(mandate);
	}
	
	public void setBundesland(Bundesland bundesland) {
		this.bundesland = bundesland;
	}
	public Bundesland getBundesland() {
		return bundesland;
	}
	public void setMandate(List<Einzelergebnis<Partei, Integer>> mandate) {
		this.mandate = mandate;
	}
	public List<Einzelergebnis<Partei, Integer>> getMandate() {
		return mandate;
	}
}
