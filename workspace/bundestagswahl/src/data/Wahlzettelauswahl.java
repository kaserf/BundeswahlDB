package data;

import java.util.List;

public class Wahlzettelauswahl {

	private List<Kandidat> kandidaten;

	private List<Partei> parteien;

	public Wahlzettelauswahl() {
	}

	public void setKandidaten(List<Kandidat> kandidaten) {
		this.kandidaten = kandidaten;
	}

	public List<Kandidat> getKandidaten() {
		return kandidaten;
	}

	public void setParteien(List<Partei> parteien) {
		this.parteien = parteien;
	}

	public List<Partei> getParteien() {
		return parteien;
	}

}
