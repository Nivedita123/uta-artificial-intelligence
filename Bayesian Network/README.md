# Bayesian Networks

Implements and caculates the proabilities for below bnet - 

 ![Bayesian Network](https://github.com/Nivedita123/ai-bayesian-network/blob/master/bnet.png?raw=true)
 
## Code Structure:

- `Map<String, Double> prob`: the probability values stored in the Bayesian network
- `Map<Character, List<Character>> bnet`: the list of nodes and their parents in the bnet
- `main` method: inits the prob and bnet hashmaps and reads cmd args and then calls calculateProb and prints the result
- `calculateProb(Map<Character, Boolean> xList, Map<Character, Boolean> yList)`: recursively calulates proability using bayes theorem and probability product formula as mentioned in the comments in the code.
- `getCombinations(Map<Character, Boolean> xList)`: this method generates and returns all combinations of missing values with specified values
- `fromBnet(Map<Character, Boolean> xList, Map<Character, Boolean> yList)`: checks if the probability value can be fetched directly from given probaility values

## Command Line

- Compilation: `javac BayesianNetwork.java`
- Execution: `java BayesianNetwork <args (1 - 6)>`
