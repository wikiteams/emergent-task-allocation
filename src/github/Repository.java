package github;

import java.io.Serializable;

public class Repository implements Serializable{
	
	private String name;
	//private String cluster;
	
	public static final String NO_CLUSTER = "none";

	public Repository(String name, String cluster) {
		this.name = name;
		//this.cluster = cluster;
	}
	
	public Repository(String name) {
		this.name = name;
		//this.cluster = NO_CLUSTER;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getCluster() {
//		return cluster;
//	}
//
//	public void setCluster(String cluster) {
//		this.cluster = cluster;
//	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode() ;//* cluster.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.name.toLowerCase() == ((Repository) obj).name.toLowerCase()))
			return true;
		else
			return false;
	}
	
//	public boolean equals(Object obj) {
//		if ((this.name.toLowerCase() == ((Repository) obj).name.toLowerCase())
//				&& (this.cluster.equals((((Repository) obj).cluster))))
//			return true;
//		else
//			return false;
//	}

}
