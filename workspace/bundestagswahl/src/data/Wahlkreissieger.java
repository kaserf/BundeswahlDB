package data;

public class Wahlkreissieger {
	private int wahlkreisNr;
	private Einzelergebnis<Kandidat, Integer> erstStimmenSieger;
	private Einzelergebnis<Partei, Integer> zweitStimmenSieger;
	
	public Wahlkreissieger(int wahlkreisNr, Einzelergebnis<Kandidat, Integer> erstStimmenSieger, Einzelergebnis<Partei, Integer> zweitStimmenSieger) {
		setWahlkreisNr(wahlkreisNr);
		setErstStimmenSieger(erstStimmenSieger);
		setZweitStimmenSieger(zweitStimmenSieger);
	}
	
	public void setWahlkreisNr(int wahlkreisNr) {
		this.wahlkreisNr = wahlkreisNr;
	}
	public int getWahlkreisNr() {
		return wahlkreisNr;
	}

	public void setErstStimmenSieger(Einzelergebnis<Kandidat, Integer> erstStimmenSieger) {
		this.erstStimmenSieger = erstStimmenSieger;
	}

	public Einzelergebnis<Kandidat, Integer> getErstStimmenSieger() {
		return erstStimmenSieger;
	}

	public void setZweitStimmenSieger(Einzelergebnis<Partei, Integer> zweitStimmenSieger) {
		this.zweitStimmenSieger = zweitStimmenSieger;
	}

	public Einzelergebnis<Partei, Integer> getZweitStimmenSieger() {
		return zweitStimmenSieger;
	}

}
