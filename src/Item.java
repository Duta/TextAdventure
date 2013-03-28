public class Item {
	private String name;

	public Item(String name) {
		setName(name);
	}

	public void setName(String name) {
		this.name = name == null ? "" : name;
	}

	public String getName() {
		return name.toLowerCase();
	}
}