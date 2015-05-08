package collaboration;

import java.io.Serializable;

import logger.VerboseLogger;

public class Category implements Serializable{
	
	/**
	 * Generated serialVersionUID for serialisation
	 */
	private static final long serialVersionUID = 4085149617945605852L;
	private CategoryType type;
	private int hash;
	
	public Category(){
		say("Category initialized");
	}
	
	public Category(CategoryType type){
		say("Category initialized with type: " + type);
		this.type = type;
	}
	
	public Category(String name){
		say("Category initialized with name: " + name);
		hash = name.hashCode();
		this.type = parseType(name);
	}
	
	private static CategoryType parseType(String name){
		if (name.toLowerCase().equals("programming")){
			return CategoryType.PROGRAMMING;
		}
		if (name.toLowerCase().equals("data")){
			return CategoryType.DATA;
		}
		if (name.toLowerCase().equals("markup")){
			return CategoryType.MARKUP;
		}
		if (name.toLowerCase().equals("unknown/other")){
			return CategoryType.UNKNOWN;
		}
		return CategoryType.UNKNOWN;
	}

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
	}

	private void say(String s) {
		VerboseLogger.say(s);
	}
	
	@Override
	public int hashCode() {
		return hash * type.ordinal();
	}
	
	@Override
	public String toString() {
		switch (type) {
			case PROGRAMMING: {
				return "programming";
			}
			case MARKUP: {
				return "markup";
			}
			case DATA: {
				return "data";
			}
			default:
				break;
		}
		return "other/unknown";
	}

}
