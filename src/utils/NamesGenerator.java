package utils;

import collaboration.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.soqqo.datagen.RandomDataGenerator;
import org.soqqo.datagen.config.DataTypes.Name;
import org.soqqo.datagen.config.GenConfig;

public class NamesGenerator {
	
	static RandomDataGenerator rdg = new RandomDataGenerator();
	private static ArrayList<String> nicknames = new ArrayList<String>();
	
	public static void clear(){
		nicknames.clear();
	}

	public static List<Agent> getnames(int count) {
		List<Agent> randomPersons = rdg.generateList(
				count,
				new GenConfig().name(Name.Firstname, "firstName").name(
						Name.Lastname, "lastName"), Agent.class);
		
		for(Agent agent : randomPersons){
			String nick = agent.getFirstName() + "_" + agent.getLastName();
			if (! nicknames.contains(nick))
				agent.setNick(nick);
			else {
				while (nicknames.contains(nick)){
					Random generator = new Random();
					int i = generator.nextInt(9);
					nick += "" + i;
				}
				agent.setNick(nick);
			}
			nicknames.add(agent.getNick());
		}
		
		return randomPersons;
	}

}
