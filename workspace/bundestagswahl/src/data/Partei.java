package data;

public class Partei {
	private int id;
	private String name;

	public Partei() {}

	public Partei(String name) {
		setName(name);
	}

	public Partei(int id, String name) {
		setId(id);
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return getName();
	}

	public int hashCode() {
		return getName().hashCode();
	}

	public boolean equals(Object obj) {
		return (obj != null) && (getClass().equals(obj.getClass()))
				&& (getName().equals(((Partei) obj).getName()));
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
