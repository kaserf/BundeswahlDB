package data;

import java.util.List;

public class Wahlzettelauswahl {

	private int wahlbezirk;
	private int wahlkreis;

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

	public void setWahlbezirk(int wahlbezirk) {
		this.wahlbezirk = wahlbezirk;
	}

	public int getWahlbezirk() {
		return wahlbezirk;
	}

	public void setWahlkreis(int wahlkreis) {
		this.wahlkreis = wahlkreis;
	}

	public int getWahlkreis() {
		return wahlkreis;
	}

}
