package collaboration;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import repast.simphony.context.DefaultContext;
import constants.Constraints;

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
	
	public String getMostLeastRequired() {
		Skills skills = ((Skills) CollaborationBuilder.skills);
		SortedMap<Double, Skill> mostNeeded = new TreeMap<>();
		
		for (Entry<Skill, List<TaskInternals>> entry : skills.getSkillsUsed().entrySet()) {
		    Skill skill = entry.getKey();
		    double workDone = 0;
		    List<TaskInternals> taskInternals = entry.getValue();
		    for(TaskInternals taskInternal : taskInternals) workDone += taskInternal.getWorkDone().d;
		    
		    mostNeeded.put(workDone, skill);
		}
		
		double min = mostNeeded.firstKey();
		double max = mostNeeded.lastKey();
		double threshold = 0.1 * (max - min);
		
		SortedMap<Double, Skill> mostNeededSet = mostNeeded.tailMap(max - threshold);
		SortedMap<Double, Skill> leastNeededSet = mostNeeded.headMap(min + threshold);
		
		if (mostNeededSet.containsValue(this)) {
			return "MR";
		} else if (leastNeededSet.containsValue(this)) {
			return "LR";
		} if (!skills.getSkillsUsed().containsKey(this)) {
			return "N/A";
		}
		
		return "";
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
		
		if(size < 1)
			return Constraints.statisticEmpty;
		
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
		
		if(size < 1)
			return Constraints.statisticEmpty;
		
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
		
		if(size < 1)
			return Constraints.statisticEmpty;
		
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
		
		if(size < 1)
			return Constraints.statisticEmpty;
		
		return result / size;
	}
	
	public double getAverageAgentProductivity(){
		double result = 0;
		long size = 0;
		
		for(Agent agent: CollaborationBuilder.agents.getObjects(Agent.class)){
			AgentInternals internals = agent.getAgentInternals(this.getName());
			if (internals != null) {
				result += internals.getExperience().getDelta();
				size += 1;
			}
		}
		
		if(size < 1)
			return Constraints.statisticEmpty2;
		
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
