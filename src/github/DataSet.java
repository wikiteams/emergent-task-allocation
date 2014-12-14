package github;

public class DataSet {

	private enum Type {
		SocketListener, Sqlite, FileSoup, Mockup, AgentTestUniverse;
	}

	public int Current;

	public DataSet(String name) {
		System.out.println("Marking the [dataset] as: " + name);
		this.setCurrent(name);
	}

	public void setCurrent(String name) {
		if (name.toLowerCase().equals("socketlistener")) {
			Current = Type.SocketListener.ordinal();
		} else if (name.toLowerCase().equals("sqlite")) {
			Current = Type.Sqlite.ordinal();
		} else if (name.toLowerCase().equals("filesoup")) {
			Current = Type.FileSoup.ordinal();
		} else if (name.toLowerCase().equals("mockup")) {
			Current = Type.Mockup.ordinal();
		} else if (name.toLowerCase().equals("agenttestuniverse")) {
			Current = Type.AgentTestUniverse.ordinal();
		}
	}

	/**
	 * This should tell the simulator whether to launch scheduled task appender
	 * or not
	 * 
	 * @return
	 */
	public Boolean isContinuus() {
		if ((Current == Type.Sqlite.ordinal())
				|| (Current == Type.SocketListener.ordinal())) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isDb() {
		if (Current == Type.Sqlite.ordinal()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isMockup() {
		if (Current == Type.Mockup.ordinal()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isTest() {
		if (Current == Type.AgentTestUniverse.ordinal()) {
			return true;
		} else {
			return false;
		}
	}

}
