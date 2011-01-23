package data;

public class Kandidat {
	private int id;
	private String name;
	private String vorname;
	private Partei partei;
	private int wahlkreis;
	private Listenkandidatur listenKandidatur;

	public Kandidat() {}

	public Kandidat(String name, String vorname, Partei partei, int wahlkreis,
			Listenkandidatur listenkandidatur, int id) {
		setName(name);
		setVorname(vorname);
		setPartei(partei);
		setWahlkreis(wahlkreis);
		setListenKandidatur(listenkandidatur);
		setId(id);
	}

	public void setPartei(Partei partei) {
		this.partei = partei;
	}

	public Partei getPartei() {
		return this.partei;
	}

	public void setWahlkreis(int wahlkreis) {
		this.wahlkreis = wahlkreis;
	}

	public int getWahlkreis() {
		return this.wahlkreis;
	}

	public void setListenKandidatur(Listenkandidatur listenKandidatur) {
		this.listenKandidatur = listenKandidatur;
	}

	public Listenkandidatur getListenKandidatur() {
		return this.listenKandidatur;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
