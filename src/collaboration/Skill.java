package collaboration;

import java.io.Serializable;
import java.util.List;

import constants.Constraints;
import repast.simphony.context.DefaultContext;

/***
 * Represents a "skill" - a GitHub language
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 3.0
 */
public class Skill extends DefaultContext<Skill> implements Serializable{

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

	public Skill(String name, String type, int id) {
		this.name = name;
		this.id = id;
		this.category = new Category(type);
		System.out.println("Skill created with name=" + name + " type=" + type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public String getCategoryName() {
		return category.toString();
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
	
	public double getWorkRemainingAbsolute(){
		double result = 0;
		long size = 0;
		
		try{
			List<TaskInternals> allTaskInternals = ((Skills) CollaborationBuilder.skills).getSkillsUsed().get(this);
			for(TaskInternals taskInternal: allTaskInternals) {
				if(!taskInternal.isWorkDone()) {
					result += taskInternal.getWorkLeftInPerc();
					size += 1;
				}
			}
		} catch (NullPointerException nexc) {
			return Constraints.statisticEmpty;
		}
		
		return result / size;
	}

	public double getWorkRemaining(){
		double result = 0;
		long size = 0;
		
		try{
			List<TaskInternals> allTaskInternals = ((Skills) CollaborationBuilder.skills).getSkillsUsed().get(this);
			
			for(TaskInternals taskInternal: allTaskInternals) {
				if(!taskInternal.isWorkDone()) {
					result += taskInternal.getWorkLeft();
					size += 1;
				}
			}
		} catch (NullPointerException nexc) {
			return Constraints.statisticEmpty;
		}
		
		return result / size;
	}
	
	public double getWorkDone(){
		double result = 0;
		long size = 0;
		
		try{
			List<TaskInternals> allTaskInternals = ((Skills) CollaborationBuilder.skills).getSkillsUsed().get(this);
			
			for(TaskInternals taskInternal: allTaskInternals) {
				if(!taskInternal.isWorkDone()) {
					result += taskInternal.getWorkDone().d;
					size += 1;
				}
			}
		} catch (NullPointerException nexc) {
			return Constraints.statisticEmpty;
		}
		
		return result / size;
	}
	
	public double getWorkDoneAbsolute(){
		double result = 0;
		long size = 0;
		
		try{
			List<TaskInternals> allTaskInternals = ((Skills) CollaborationBuilder.skills).getSkillsUsed().get(this);
			
			for(TaskInternals taskInternal: allTaskInternals) {
				if(!taskInternal.isWorkDone()) {
					result += taskInternal.getProgress();
					size += 1;
				}
			}
		} catch (NullPointerException nexc) {
			return Constraints.statisticEmpty;
		}
		
		return result / size;
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
