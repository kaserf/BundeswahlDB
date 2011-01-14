package data;

public class KnappsterSieger {
	private Einzelergebnis<Kandidat, Integer> sieger;
	private Einzelergebnis<Kandidat, Integer> verlierer;
	
	public KnappsterSieger(Einzelergebnis<Kandidat, Integer> sieger, Einzelergebnis<Kandidat, Integer> verlierer) {
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
}
