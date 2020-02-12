import java.io.*;
import java.util.*;

/**
 * @author nivedita gautam
 */

public class CheckTrueFalse {

	public static void main(String[] args) {

		if (args.length != 3) {

			// takes three arguments
			System.out.println("Usage: " + args[0] + " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}

		// create some buffered IO streams
		String buffer;
		BufferedReader inputStream;

		// create the knowledge base and the statement
		LogicalExpression knowledgeBase = new LogicalExpression();
		LogicalExpression alpha = new LogicalExpression();

		// open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader(new FileReader(args[0]));

			// load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledgeBase.setConnective("and");

			while ((buffer = inputStream.readLine()) != null) {
				if (!(buffer.startsWith("#") || (buffer.equals("")))) {
					// the line is not a comment
					LogicalExpression subExpression = readExpression(buffer);
					knowledgeBase.setSubexpression(subExpression);
				} else {
					// the line is a comment. do nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[0]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading wumpus rules

		// read the additional knowledge file
		try {
			inputStream = new BufferedReader(new FileReader(args[1]));

			// load the additional knowledge
			System.out.println("loading the additional knowledge...");

			// the connective for knowledge_base is already set. no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			// knowledge_base.setConnective("and");

			while ((buffer = inputStream.readLine()) != null) {
				if (!(buffer.startsWith("#") || (buffer.equals("")))) {
					LogicalExpression subExpression = readExpression(buffer);
					knowledgeBase.setSubexpression(subExpression);
				} else {
					// the line is a comment. do nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[1]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading additional knowledge

		// check for a valid knowledge_base
		if (!valid_expression(knowledgeBase)) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}

		// print the knowledge_base
		knowledgeBase.print_expression("\n");

		// read the statement file
		try {
			inputStream = new BufferedReader(new FileReader(args[2]));

			System.out.println("\n\nLoading the statement file...");
			// buffer = inputStream.readLine();

			// actually read the statement file
			// assuming that the statement file is only one line long
			while ((buffer = inputStream.readLine()) != null) {
				if (!buffer.startsWith("#")) {
					// the line is not a comment
					alpha = readExpression(buffer);
					break;
				} else {
					// the line is a commend. no nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[2]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file

		// check for a valid statement
		if (!valid_expression(alpha)) {
			System.out.println("invalid statement");
			exit_function(0);
		}

		// print the statement
		alpha.print_expression("");
		// print a new line
		System.out.println("\n");

		// check if KB entails alpha
		boolean kbEntailsAlpha = ttEntails(knowledgeBase, alpha);

		LogicalExpression notAlpha = new LogicalExpression();
		notAlpha.setConnective("not");
		notAlpha.setSubexpression(alpha);

		// check if kb entails not (alpha)
		boolean kbEntailsNotAlpha = ttEntails(knowledgeBase, notAlpha);

		String result = "";

		if (kbEntailsAlpha && kbEntailsNotAlpha) {
			result = "both true and false";
		} else if (kbEntailsAlpha && !kbEntailsNotAlpha) {
			result = "definitely true";
		} else if (!kbEntailsAlpha && kbEntailsNotAlpha) {
			result = "definitely false";
		} else {
			result = "possibly true, possibly false";
		}

		System.out.println(result + " (See result.txt)");

		// store the output in result.txt
		try {
			File file = new File("result.txt");
			FileOutputStream fop = new FileOutputStream(file);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = result.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (Exception e) {
			System.out.println("ERROR creating result file");
		}

	} // end of main

	/** checks kb -> alpha */
	public static boolean ttEntails(LogicalExpression kb, LogicalExpression alpha) {

		// add the symbol values to model, which are already specified in the KB
		Map<String, Boolean> model = initModel(kb);

		// add remaining symbols to symbol list
		List<String> symbols = getSymbols(kb, alpha, model);

		return ttCheckAll(kb, alpha, symbols, model);
	}

	private static boolean ttCheckAll(LogicalExpression kb, LogicalExpression alpha, List<String> symbols,
			Map<String, Boolean> model) {

		if (symbols.isEmpty()) {

			// for a given model, alpha should be true if KB is true
			if (plTrue(kb, model)) {
				return plTrue(alpha, model);
			} else {
				// invalid kb entails everything
				return true;
			}

		} else {

			// get first symbol
			String symbol = symbols.get(0);

			// get the remaining symbols
			// creating 2 copies since the list is passed by reference
			List<String> symbolCopy1 = new ArrayList<String>();
			symbolCopy1.addAll(symbols);
			symbolCopy1.remove(0);

			List<String> symbolCopy2 = new ArrayList<String>();
			symbolCopy2.addAll(symbols);
			symbolCopy2.remove(0);

			// create model with p = true
			model.put(symbol, true);
			boolean checkTrue = ttCheckAll(kb, alpha, symbolCopy1, model);

			// create model with p = false
			model.put(symbol, false);
			boolean checkFalse = ttCheckAll(kb, alpha, symbolCopy2, model);

			return checkTrue && checkFalse;
		}
	}

	/** checks if the expression is true in the given model */
	private static boolean plTrue(LogicalExpression exp, Map<String, Boolean> model) {

		// base case
		if (exp.getUniqueSymbol() != null) {

			// return the truth value of the symbol from model
			return model.get(exp.getUniqueSymbol());
		}
		// recursive case
		else {

			Boolean result = null;

			// get truth value of first subexpression, if it exists
			if (!exp.getSubexpressions().isEmpty()) {
				result = plTrue(exp.getSubexpressions().get(0), model);
			}

			// not has exactly one subexpression
			if (exp.getConnective().equalsIgnoreCase("not")) {

				return !result;

			} else if (exp.getConnective().equalsIgnoreCase("and")) {

				// if 0 arguments, return true
				if (result == null) {
					result = true;
				}

				// evaluate all subexpressions
				for (int i = 1; i < exp.getSubexpressions().size(); i++) {

					// if a subexpression if false, return false, no need to evaluate the others
					if (!result) {
						break;
					}
					result = result && plTrue(exp.getSubexpressions().get(i), model);
				}

			} else if (exp.getConnective().equalsIgnoreCase("or")) {

				// if 0 arguments return false;
				if (result == null) {
					result = false;
				}
				// evaluate all subexpressions
				for (int i = 1; i < exp.getSubexpressions().size(); i++) {

					// if a subexpression is true, return true, no need to evaluate the others
					if (result) {
						break;
					}
					result = result || plTrue(exp.getSubexpressions().get(i), model);
				}

			} else if (exp.getConnective().equalsIgnoreCase("xor")) {

				// if 0 arguments return false
				if (result == null) {
					result = false;
				}

				// evaluate all subexpressions
				for (int i = 1; i < exp.getSubexpressions().size(); i++) {

					result = xor(result, plTrue(exp.getSubexpressions().get(i), model));
				}

			} else if (exp.getConnective().equalsIgnoreCase("if")) { // implication

				// evaluate all subexpressions (a -> b -> c -> d ...)
				for (int i = 1; i < exp.getSubexpressions().size(); i++) {

					result = implies(result, plTrue(exp.getSubexpressions().get(i), model));
				}
			} else { // double implication

				// evaluate all subexpressions (a <-> b <-> c <-> d ...)
				for (int i = 1; i < exp.getSubexpressions().size(); i++) {

					result = doubleImplies(result, plTrue(exp.getSubexpressions().get(i), model));
				}
			}

			// return the final result
			return result;
		}
	}

	/** add symbols to model whose value is specified in KB */
	private static Map<String, Boolean> initModel(LogicalExpression kb) {

		// initialize empty hashmap
		Map<String, Boolean> model = new HashMap<String, Boolean>();

		// loop through all the subexpressions of KB and get the value (true or false)
		for (Enumeration<LogicalExpression> e = kb.getSubexpressions().elements(); e.hasMoreElements();) {

			LogicalExpression exp = e.nextElement();

			// if the subexpression is a unique symbol e.g A
			if (exp.getUniqueSymbol() != null) {

				// it means A = true in KB
				if (exp.getConnective() == null) {
					model.put(exp.getUniqueSymbol(), true);
				}

			} else {

				// find symbols which are false in KB e.g. (not A)
				if (exp.getConnective().equals("not") && exp.getSubexpressions().size() == 1) {

					// not has exactly one subexp
					LogicalExpression subExp = exp.getSubexpressions().get(0);

					// if the subexp is a unique symbol, A = false
					if (subExp.getUniqueSymbol() != null) {
						model.put(subExp.getUniqueSymbol(), false);
					}
				}
			}
		}
		return model;
	}

	/**
	 * returns a list of unique symbols from kb and alpha which are not in model. We
	 * do not want want to add the symbols present in model because we not want to
	 * generate combination for the symbols which have the value already given in
	 * the KB.
	 */
	private static List<String> getSymbols(LogicalExpression kb, LogicalExpression alpha, Map<String, Boolean> model) {

		// create set to avoid repeated values
		Set<String> set = new HashSet<String>();

		List<LogicalExpression> list = new ArrayList<LogicalExpression>();

		// initialize the list with KB and alpha
		list.add(kb);
		list.add(alpha);

		while (!list.isEmpty()) {

			LogicalExpression exp = list.remove(0);

			// if the exp has subexp, add them all to the list
			if (exp.getUniqueSymbol() == null) {
				list.addAll(exp.getSubexpressions());
			} else {
				// if it's a unique symbol and not present in the initial model, add it to the
				// symbol list
				if (model.get(exp.getUniqueSymbol()) == null)
					set.add(exp.getUniqueSymbol());
			}
		}

		// create a list from the set, since set is unordered
		List<String> res = new ArrayList<String>(set);
		return res;
	}

	/** returns result of a -> b */
	private static boolean implies(boolean a, boolean b) {
		if (a && !b) {
			return false;
		} else {
			return true;
		}
	}

	/** returns result of a <-> b */
	private static boolean doubleImplies(boolean a, boolean b) {
		return a == b;
	}

	/** returns result of a XOR b */
	private static boolean xor(boolean a, boolean b) {
		return !doubleImplies(a, b);
	}

	/**
	 * this method reads logical expressions if the next string is a: - '(' => then
	 * the next 'symbol' is a subexpression - else => it must be a unique_symbol
	 * 
	 * @return a logical expression
	 * 
	 */
	public static LogicalExpression readExpression(String input_string) {

		LogicalExpression result = new LogicalExpression();

		// trim the whitespace off
		input_string = input_string.trim();

		if (input_string.startsWith("(")) {
			// its a subexpression

			String symbolString = "";

			// remove the '(' from the input string
			symbolString = input_string.substring(1);

			if (!symbolString.endsWith(")")) {

				// missing the closing paren - invalid expression
				System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString);
				exit_function(0);

			} else {

				// remove the last ')' , it should be at the end
				symbolString = symbolString.substring(0, (symbolString.length() - 1));
				symbolString.trim();

				// read the connective into the result LogicalExpression object
				symbolString = result.setConnective(symbolString);
			}

			// read the subexpressions into a vector and call setSubExpressions( Vector );
			result.setSubexpressions(read_subexpressions(symbolString));

		} else {
			// the next symbol must be a unique symbol
			// if the unique symbol is not valid, the setUniqueSymbol will tell us.
			result.setUniqueSymbol(input_string);

		}

		return result;
	}

	/**
	 * this method reads in all of the unique symbols of a subexpression the only
	 * place it is called is by read_expression(String, long)(( the only
	 * read_expression that actually does something ));
	 * 
	 * each string is EITHER: - a unique Symbol - a subexpression - Delineated by
	 * spaces, and parent pairs
	 * 
	 * @return a vector of logicalExpressions
	 * 
	 */

	public static Vector<LogicalExpression> read_subexpressions(String input_string) {

		Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
		LogicalExpression newExpression;// = new LogicalExpression();
		String newSymbol = new String();

		input_string.trim();

		while (input_string.length() > 0) {

			newExpression = new LogicalExpression();

			if (input_string.startsWith("(")) {

				// its a subexpression.
				// have readExpression parse it into a LogicalExpression object
				// find the matching ')'

				int parenCounter = 1;
				int matchingIndex = 1;

				while ((parenCounter > 0) && (matchingIndex < input_string.length())) {
					if (input_string.charAt(matchingIndex) == '(') {
						parenCounter++;
					} else if (input_string.charAt(matchingIndex) == ')') {
						parenCounter--;
					}
					matchingIndex++;
				}

				// read until the matching ')' into a new string
				newSymbol = input_string.substring(0, matchingIndex);

				// pass that string to readExpression,
				newExpression = readExpression(newSymbol);

				// add the LogicalExpression that it returns to the vector symbolList
				symbolList.add(newExpression);

				// trim the logicalExpression from the input_string for further processing
				input_string = input_string.substring(newSymbol.length(), input_string.length());

			} else {

				// its a unique symbol ( if its not, setUniqueSymbol() will tell us )
				// I only want the first symbol, so, create a LogicalExpression object and
				// add the object to the vector

				if (input_string.contains(" ")) {

					// remove the first string from the string
					newSymbol = input_string.substring(0, input_string.indexOf(" "));
					input_string = input_string.substring((newSymbol.length() + 1), input_string.length());

				} else {

					newSymbol = input_string;
					input_string = "";
				}

				newExpression.setUniqueSymbol(newSymbol);
				symbolList.add(newExpression);
			}

			input_string.trim();

			if (input_string.startsWith(" ")) {
				// remove the leading whitespace
				input_string = input_string.substring(1);
			}

		}
		return symbolList;
	}

	/**
	 * this method checks to see if a logical expression is valid or not a valid
	 * expression either: ( this is an XOR ) - is a unique_symbol - has: -- a
	 * connective -- a vector of logical expressions
	 * 
	 */
	public static boolean valid_expression(LogicalExpression expression) {

		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if (!(expression.getUniqueSymbol() == null) && (expression.getConnective() == null)) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol(expression.getUniqueSymbol());
		}

		// symbol is empty, so
		// check to make sure the connective is valid

		// check for 'if / iff'
		if ((expression.getConnective().equalsIgnoreCase("if"))
				|| (expression.getConnective().equalsIgnoreCase("iff"))) {

			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() + "\" with "
						+ expression.getSubexpressions().size() + " arguments\n");
				return false;
			}
		}

		// check for 'not'
		else if (expression.getConnective().equalsIgnoreCase("not")) {
			// the connective is NOT - there can be only one symbol / subexpression
			if (expression.getSubexpressions().size() != 1) {
				System.out.println("error: connective \"" + expression.getConnective() + "\" with "
						+ expression.getSubexpressions().size() + " arguments\n");
				return false;
			}
		}

		// check for 'and / or / xor'
		else if ((!expression.getConnective().equalsIgnoreCase("and"))
				&& (!expression.getConnective().equalsIgnoreCase("or"))
				&& (!expression.getConnective().equalsIgnoreCase("xor"))) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n");
			return false;
		}

		// checks for validity of the logical_expression 'symbols' that go with the
		// connective
		for (Enumeration<LogicalExpression> e = expression.getSubexpressions().elements(); e.hasMoreElements();) {
			LogicalExpression testExpression = (LogicalExpression) e.nextElement();

			// for each subExpression in expression,
			// check to see if the subexpression is valid
			if (!valid_expression(testExpression)) {
				return false;
			}
		}

		// if the method made it here, the expression must be valid
		return true;
	}

	/**
	 * this function checks to see if a unique symbol is valid this function should
	 * be done and complete originally returned a data type of long. I think this
	 * needs to return true /false public long valid_symbol( String symbol ) {
	 * 
	 */
	public static boolean valid_symbol(String symbol) {

		if (symbol == null || (symbol.length() == 0)) {

			return false;
		}

		for (int counter = 0; counter < symbol.length(); counter++) {

			if ((symbol.charAt(counter) != '_') && (!Character.isLetterOrDigit(symbol.charAt(counter)))) {

				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt(counter)
						+ "---\n");
				return false;
			}
		}

		// the characters of the symbol string are either a letter or a digit or an
		// underscore,
		// return true
		return true;
	}

	private static void exit_function(int value) {
		System.out.println("exiting from checkTrueFalse");
		System.exit(value);
	}
}