package data;

import java.util.List;

public class WahlkreisUebersicht {
	private Wahlkreis wahlkreis;
	private Kandidat direktkandidat;
	private double wahlbeteiligung;
	private List<Einzelergebnis<Partei, Integer>> stimmenAbsolut;
	private List<Einzelergebnis<Partei, Double>> stimmenProzentual;
	private List<Einzelergebnis<Partei, Double>> stimmenEntwicklung;

	public WahlkreisUebersicht() {
		// TODO Auto-generated constructor stub
	}

	public WahlkreisUebersicht(Wahlkreis wahlkreis, Kandidat direktkandidat,
			double wahlbeteiligung,
			List<Einzelergebnis<Partei, Integer>> stimmenAbsolut,
			List<Einzelergebnis<Partei, Double>> stimmenProzentual,
			List<Einzelergebnis<Partei, Double>> stimmenEntwicklung) {
		setWahlkreis(wahlkreis);
		setDirektkandidat(direktkandidat);
		setWahlbeteiligung(wahlbeteiligung);
		setStimmenAbsolut(stimmenAbsolut);
		setStimmenProzentual(stimmenProzentual);
		setStimmenEntwicklung(stimmenEntwicklung);
	}

	public void setDirektkandidat(Kandidat direktkandidat) {
		this.direktkandidat = direktkandidat;
	}

	public Kandidat getDirektkandidat() {
		return this.direktkandidat;
	}

	public void setWahlbeteiligung(double wahlbeteiligung) {
		this.wahlbeteiligung = wahlbeteiligung;
	}

	public double getWahlbeteiligung() {
		return this.wahlbeteiligung;
	}

	public void setStimmenProzentual(
			List<Einzelergebnis<Partei, Double>> stimmenProzentual) {
		this.stimmenProzentual = stimmenProzentual;
	}

	public List<Einzelergebnis<Partei, Double>> getStimmenProzentual() {
		return this.stimmenProzentual;
	}

	public void setStimmenEntwicklung(
			List<Einzelergebnis<Partei, Double>> stimmenEntwicklung) {
		this.stimmenEntwicklung = stimmenEntwicklung;
	}

	public List<Einzelergebnis<Partei, Double>> getStimmenEntwicklung() {
		return this.stimmenEntwicklung;
	}

	public void setStimmenAbsolut(
			List<Einzelergebnis<Partei, Integer>> stimmenAbsolut) {
		this.stimmenAbsolut = stimmenAbsolut;
	}

	public List<Einzelergebnis<Partei, Integer>> getStimmenAbsolut() {
		return this.stimmenAbsolut;
	}

	public void setWahlkreis(Wahlkreis wahlkreis) {
		this.wahlkreis = wahlkreis;
	}

	public Wahlkreis getWahlkreis() {
		return this.wahlkreis;
	}
}
