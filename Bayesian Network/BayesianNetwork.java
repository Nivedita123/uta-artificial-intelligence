import java.util.*;

public class BayesianNetwork {

	// the probability values stored in the Bayesian network
	static Map<String, Double> prob = new HashMap<String, Double>();

	// the list of nodes and their parents in the bnet
	static Map<Character, List<Character>> bnet = new HashMap<Character, List<Character>>();

	public static void main(String[] args) {

		if (args.length < 1 || args.length > 6) {

			System.out.println("The number of arguments should be between 1 - 6");
			return;
		}

		// defining the bayesian network structure
		List<Character> parents = null;
		bnet.put('B', parents);
		bnet.put('E', parents);

		parents = new ArrayList<Character>();
		parents.add('B');
		parents.add('E');
		bnet.put('A', parents);

		parents = new ArrayList<Character>();
		parents.add('A');
		bnet.put('J', parents);

		parents = new ArrayList<Character>();
		parents.add('A');
		bnet.put('M', parents);

		// storing the prob values in the BNET
		prob.put("Bt", 0.001);
		prob.put("Et", 0.002);
		prob.put("At|Bt,Et", 0.95);
		prob.put("At|Bt,Ef", 0.94);
		prob.put("At|Bf,Et", 0.29);
		prob.put("At|Bf,Ef", 0.001);
		prob.put("Jt|At", 0.90);
		prob.put("Jt|Af", 0.05);
		prob.put("Mt|At", 0.70);
		prob.put("Mt|Af", 0.01);

		// P (x1,x2,x3,... | y1,y2,y3,...)
		Map<Character, Boolean> xList = new HashMap<Character, Boolean>();
		Map<Character, Boolean> yList = new HashMap<Character, Boolean>();

		// identifying and storing the x and y expressions from the input
		boolean given = false;

		for (int i = 0; i < args.length; i++) {

			if (args[i].equals("given")) {
				given = true;
				continue;
			}

			char var = args[i].charAt(0);
			boolean val = false;

			if (args[i].charAt(1) == 't')
				val = true;

			if (!given) {

				xList.put(var, val);
			} else {
				yList.put(var, val);
			}
		}
		double result = calculateProb(xList, yList);
		System.out.println(result);
	}

	// recursive function to calculate prob
	public static double calculateProb(Map<Character, Boolean> xList, Map<Character, Boolean> yList) {

		// check if the prob value is present in bnet
		Double prob = fromBnet(xList, yList);

		// if prob value was fround return the value
		if (prob != null) {
			return prob;
		}

		double res = 0.0;
		// Using bayes's formula : P (x1,x2,... | y1,y2,...) = P (x1,x2,...,y1,y2,...) /
		// P (y1,y2,...)
		if (yList != null && yList.size() > 0) {

			Map<Character, Boolean> xyList = new HashMap<Character, Boolean>();
			xyList.putAll(xList);
			xyList.putAll(yList);

			res = calculateProb(xyList, null) / calculateProb(yList, null);

		} else {

			// all bnet variable values are specified
			if (xList.size() == bnet.size()) {

				res = 1.0;

				// P (x1,x2,...) = P (x1 | parents (x1)) * P (x2 | parents (x2)) * ..
				for (Map.Entry<Character, Boolean> entry : xList.entrySet()) {

					Map<Character, Boolean> newList = new HashMap<Character, Boolean>();
					newList.put(entry.getKey(), entry.getValue());

					// parent value list
					Map<Character, Boolean> parentList = null;

					// parents of the variable in bnet
					List<Character> parents = bnet.get(entry.getKey());

					if (parents != null) {

						parentList = new HashMap<Character, Boolean>();
						for (int i = 0; i < parents.size(); i++) {
							parentList.put(parents.get(i), xList.get(parents.get(i)));
						}
					}

					res *= calculateProb(newList, parentList);
				}
			}
			// some bnet values are not specified
			else {

				// generate all combinations of missing values with specified values
				List<Map<Character, Boolean>> combinations = getCombinations(xList);
				
				System.out.println(combinations);

				// sum the prob values of all the combinations
				res = 0.0;
				for (int i = 0; i < combinations.size(); i++) {
					res += calculateProb(combinations.get(i), null);
				}
			}
		}
		return res;
	}

	// this method generates and returns all combinations of missing values with
	// specified values
	public static List<Map<Character, Boolean>> getCombinations(Map<Character, Boolean> xList) {

		// to store temp values
		List<Map<Character, Boolean>> temp = new ArrayList<Map<Character, Boolean>>();

		// to store the final result
		List<Map<Character, Boolean>> res = new ArrayList<Map<Character, Boolean>>();

		// init temp with xList
		temp.add(xList);

		// find missing variables
		List<Character> missing = new ArrayList<Character>();
		for (Map.Entry<Character, List<Character>> entry : bnet.entrySet()) {

			if (xList.get(entry.getKey()) == null) {
				missing.add(entry.getKey());
			}
		}

		// generate superset of all possible combinations
		for (int i = 0; i < missing.size(); i++) {

			char key = missing.get(i);
			int size = temp.size();

			for (int j = 0; j < size; j++) {

				Map<Character, Boolean> copy1 = new HashMap<Character, Boolean>();
				copy1.putAll(temp.get(j));
				copy1.put(key, false);

				Map<Character, Boolean> copy2 = new HashMap<Character, Boolean>();
				copy2.putAll(temp.get(j));
				copy2.put(key, true);

				temp.add(copy1);
				temp.add(copy2);

				// add to final result if the map contains all variable values
				if (copy1.size() == bnet.size()) {
					res.add(copy1);
				}
				if (copy2.size() == bnet.size()) {
					res.add(copy2);
				}
			}
		}
		return res;
	}

	// checks if the probability value can be fetched directly from given probaility
	// values
	public static Double fromBnet(Map<Character, Boolean> xList, Map<Character, Boolean> yList) {

		// search both the key and the complement of the key
		String key = "";
		String notKey = "";

		// converting to string representation to find value in bnet
		Iterator<Map.Entry<Character, Boolean>> itr = xList.entrySet().iterator(); 
		
		while(itr.hasNext())  {
			
			Map.Entry<Character, Boolean> entry = itr.next();

			key += entry.getKey();
			notKey += entry.getKey();

			if ((boolean) entry.getValue() == true) {
				key += "t,";
				notKey += "f,";

			} else {
				key += "f,";
				notKey += "t,";
			}
		}

		// remove trailing ","
		key = key.substring(0, key.length() - 1);
		notKey = notKey.substring(0, notKey.length() - 1);

		if (yList != null) {

			key += "|";
			notKey += "|";

			for (Map.Entry<Character, Boolean> entry : yList.entrySet()) {

				key += entry.getKey();
				notKey += entry.getKey();

				if (entry.getValue() == true) {
					key += "t,";
					notKey += "t,";

				} else {
					key += "f,";
					notKey += "f,";
				}
			}

			// remove trailing ,
			key = key.substring(0, key.length() - 1);
			notKey = notKey.substring(0, notKey.length() - 1);

		}

		Double res = null;

		// if prob is missing but prob of complement is present, subtract from one and
		// return, if cannot find prob return null
		if (prob.get(key) == null) {
			if (prob.get(notKey) != null) {
				res = 1 - prob.get(notKey);
			}
		} else {
			res = prob.get(key);
		}

		return res;
	}
}
