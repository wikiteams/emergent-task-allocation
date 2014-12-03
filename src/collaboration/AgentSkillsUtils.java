package collaboration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class AgentSkillsUtils {

	public static BigInteger checksum(Object obj) throws IOException,
			NoSuchAlgorithmException {

		if (obj == null) {
			return BigInteger.ZERO;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();

		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(baos.toByteArray());

		return new BigInteger(1, m.digest());
	}

	protected static HashMap<Skill, Double> getByIndex(
			LinkedHashMap<String, HashMap<Skill, Double>> hMap, int index) {
		return (HashMap<Skill, Double>) hMap.values().toArray()[index];
	}

	public static HashMap<Skill, Double> choseRandom(
			LinkedHashMap<String, HashMap<Skill, Double>> skillSet) {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet, i);
	}

}
