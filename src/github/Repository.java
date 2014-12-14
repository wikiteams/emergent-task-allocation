package github;

public class Repository{
	
	private String name;
	
	public static final String NO_CLUSTER = "none";

	public Repository(String name, String cluster) {
		this.name = name;
	}
	
	public Repository(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.name.toLowerCase() == ((Repository) obj).name.toLowerCase()))
			return true;
		else
			return false;
	}

}
