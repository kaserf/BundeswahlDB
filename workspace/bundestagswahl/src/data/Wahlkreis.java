package data;


public class Wahlkreis {
	private int id;
	private String name;

	public Wahlkreis() {
		// TODO Auto-generated constructor stub
	}

	public Wahlkreis(int id, String name) {
		setId(id);
		setName(name);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
