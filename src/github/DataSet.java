package github;

public class DataSet {
	
	private enum Type {
		SocketListener, Sqlite, FileSoup, Mockup, AgentTestUniverse;
	}
	
	public int Current;
	
	public DataSet(String name){
		this.setCurrent(name);
	}
	
	public void setCurrent(String name){
		if (name.toLowerCase().equals("socketlistener")){
			Current = Type.SocketListener.ordinal();
		} else if (name.toLowerCase().equals("sqlite")){
			Current = Type.Sqlite.ordinal();
		} else if (name.toLowerCase().equals("filesoup")){
			Current = Type.FileSoup.ordinal();
		} else if (name.toLowerCase().equals("mockup")){
			Current = Type.Mockup.ordinal();
		} else if (name.toLowerCase().equals("agenttestuniverse")){
			Current = Type.AgentTestUniverse.ordinal();
		}
	}
	
	public Boolean isContinuus(){
		if (Current == Type.Sqlite.ordinal()){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isMockup(){
		if (Current == Type.Mockup.ordinal()){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isTest(){
		if (Current == Type.AgentTestUniverse.ordinal()){
			return true;
		} else {
			return false;
		}
	}

}
