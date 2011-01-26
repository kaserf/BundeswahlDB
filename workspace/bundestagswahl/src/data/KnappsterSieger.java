package data;

public class KnappsterSieger {
	private String partei;
	private Einzelergebnis<Kandidat, Integer> sieger;
	private Einzelergebnis<Kandidat, Integer> verlierer;
	
	public KnappsterSieger() {
		// TODO Auto-generated constructor stub
	}
	
	public KnappsterSieger(String partei, Einzelergebnis<Kandidat, Integer> sieger, Einzelergebnis<Kandidat, Integer> verlierer) {
		setPartei(partei);
		setSieger(sieger);
		setVerlierer(verlierer);
	}
	
	public void setSieger(Einzelergebnis<Kandidat, Integer> sieger) {
		this.sieger = sieger;
	}
	public Einzelergebnis<Kandidat, Integer> getSieger() {
		return sieger;
	}
	public void setVerlierer(Einzelergebnis<Kandidat, Integer> verlierer) {
		this.verlierer = verlierer;
	}
	public Einzelergebnis<Kandidat, Integer> getVerlierer() {
		return verlierer;
	}

	public void setPartei(String partei) {
		this.partei = partei;
	}

	public String getPartei() {
		return partei;
	}
}
