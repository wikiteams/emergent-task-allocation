package social;

import java.util.List;

public class SocialVector {
	private List<SocialVectorAttribute> attributeList;

	public int getSize() {
		return attributeList.size();
	}

	public void addAttribute(SocialVectorAttribute socialVectorAttribute) {
		attributeList.add(socialVectorAttribute);
	}

	public SocialVectorAttribute getAttribute(int position) {
		return attributeList.get(position);
	}

	/**
	 * Magnitude is a square of a sum of values raised to power of 2
	 * 
	 * @return sqrv((E{Vi^2}))
	 */
	public double getMagnitude() {
		double f = 0;
		for (int i = 0; i < getSize(); i++) {
			f += Math.pow(attributeList.get(i).getValue(), 2);
		}
		Math.pow(f, 1 / 2);
		return f;
	}

	public double distance(SocialVector sv) {
		double f = 0;
		for (int i = 0; i < getSize(); i++) {
			f += Math.pow(attributeList.get(i).getValue()
					- sv.getAttribute(i).getValue(), 2);
		}
		Math.pow(f, 1 / 2);
		return 0;
	}
}
