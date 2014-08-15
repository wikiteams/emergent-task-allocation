package argonauts;

public class GranularityType {
	
	public enum TYPE {TASKANDSKILL, TASKONLY, OFF};
	
	public static TYPE desc(String name){
		if (name.toLowerCase().equals("taskandskill")){
			return TYPE.TASKANDSKILL;
		} else if (name.toLowerCase().equals("taskonly")){
			return TYPE.TASKONLY;
		}
		return TYPE.OFF;
	}

}
