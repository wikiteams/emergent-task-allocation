package collaboration;

import java.io.Serializable;

import logger.PjiitOutputter;

/***
 * Represents a "skill" - a GitHub language
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 1.1
 */
public class Skill implements Serializable{

	/**
	 * Generated serialVersionUID for serialization
	 */
	private static final long serialVersionUID = 1804253817796408790L;

	private String name;
	
	private int id;
	private Category category;
	
	private int strength;
	private double probability;
	private int cardinalProbability;

	public Skill() {
		say("Empty-constructor Skill initialized");
	}

	public Skill(String name, short id, int strength) {
		this.name = name;
		this.id = id;
		this.strength = strength;
		say("Skill created");
	}

	public Skill(String name) {
		this.name = name;
		say("Skill created with minimum data");
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	public Skill(String name, String type, int id) {
		this.name = name;
		this.id = id;
		this.category = new Category(type);
		say("Skill created with name=" + name + " type=" + type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public int getCardinalProbability() {
		return cardinalProbability;
	}

	public void setCardinalProbability(int cardinalProbability) {
		this.cardinalProbability = cardinalProbability;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Skill)) {
			return false;
		}
		if (this.name.toLowerCase().equals(((Skill) obj).name.toLowerCase()))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
