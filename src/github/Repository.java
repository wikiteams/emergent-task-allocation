package github;

public class Repository{
	
	private String name;
	private String cluster;
	private String created_at;

	public Repository(String name, String cluster) {
		this.name = name;
		this.cluster = cluster;
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
	
	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
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
