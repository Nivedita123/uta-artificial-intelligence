# Inference Engine

Given a knowledge base and a statement it determines if, based on the knowledge base, the statement is definitely true, definitely false, or of unknown truth value.

## Statements Syntax -

Statements are given in prefix notation. Some examples of prefix notation are:

```
(or M_1_1 B_1_2)
(and M_1_2 S_1_1 (not (or M_1_3 M_1_4)))
(if M_1_1 (and S_1_2 S_1_3))
(iff M_1_2 (and S_1_1 S_1_3 S_2_2))
(xor B_2_2 P_1_2)
P_1_1
B_3_4
(not P_1_1)
```

Statements can be nested, as shown in the above examples.

Note that:
- Any open parenthesis that is not the first character of a text line must be preceded by white space.
- Any open parenthesis must be immediately followed by a connective (without any white space in
between).
- Any close parenthesis that is not the last character of a text line must be followed by white space.
- If the logical expression contains just a symbol (and no connectives), the symbol should NOT be enclosed in parentheses. For example, (P_1_1) is not legal, whereas (not P_1_1) is legal.
- Each logical expression should be contained in a single line.
- The statement file should contain a single logical expression. If it contains more than one logical expression, only the first one is read.
- Lines starting with # are treated as comment lines, and ignored.
- There are six connectives: `and`, `or`, `xor`, `not`, `if`, `iff`. No other connectives are allowed to be used in the input files.
- A statement can consist of either a single symbol, or a connective connecting multiple (sub)statements.
- Connectives "and", "or", and "xor" can connect any number of statements, including 0 statements. It is legal for a statement consisting of an "and", "or", or "xor" connective to have no substatements, e.g., (and). An "and" statement with zero substatements is true. An "or" or "xor" statement with zero substatements is false. An "xor" statement is true if exactly 1 substatement is true (no more, no fewer).
- Connectives "if" and "iff" require exactly two substatements.
- Connective "not" requires exactly one substatement.

## Code Structure:
	
### Class `CheckTrueFalse`
- The class contains the main function.
		
##### Methods:
- `ttEntails(knowledge_base, statement)`: returns true if kb entails the given statement
- `ttCheckAll(knowledge_base, statement, symbol_list, model)`: a helper method for ttEntails. It generates all the possible worlds and checks if the statement is true in all the worlds where knowledge base is true.
- `plTrue (logical_expression, model)`: returns the value of the logical expression in the given model
- `initModel(knowledge_base)`: returns an initial model with the symbols whose truth value is directly given in the knowledge_base
- `getSymbols(knowledge_base, statement, model)`: returns a list of unique symbols from knowledge_base and alpha which are not in model. We do not want want to add the symbols present in model because we not want to generate combination for the symbols which have the value already given in the knowledge_base.
- `implied (a,b)`: returns the result of a-> b
- `doubleImplies (a,b)`: returns the result of a <-> b
- `xor (a,b)`: returns true if exactly 1 out out of is true.
- `readExpression(input_string)`: this method reads logical expressions if the next string is a: - '(' => then the next 'symbol' is a subexpression - else => it must be a unique_symbol
- `read_subexpressions(input_string)`: this method reads in all of the unique symbols of a subexpression.
- `valid_expression (expression)`: this method checks to see if a logical expression is valid or not, based on the rules specified in the assignment
- `valid_symbol(symbol)`: this method checks to see if a unique symbol is valid or not.
- `exit_function(value)`: exists from the program
	 		
### Class `LogicalExpression`:
	
- describes a logical expression recursively
		
##### Constructors:
- `LogicalExpression()`: default constructor
- `LogicalExpression(old_expression)`: creates a copy of the old_expression
		
##### Methods:

- `setUniqueSymbol (new_symbol)`: method sets the symbol for the LogicalExpression it checks to make sure that it starts with one of the appropriate letters, then checks to make sure that the rest of the string is either digits or '_'
- `setConnective (input_string)`: sets the connective for this logical expression.
- `setSubexpression (subexp)`: setter method
- `setSubexpressions (subexp_list)`: setter method
- `getUniqueSymbol ()`: getter method
- `getConnective ()`: getter
- `getNextSubexpression()`: returns the last subexpression.
- `getSubexpressions()`: getter method
- `print_expression(seperator)`: prints the subexpression on console
- `exit_function (exit_code)`: exists from the program
			
## Command Line
- Compilation	: `javac CheckTrueFalse.java`
- Execution	: `java CheckTrueFalse [knowledge_base_file] [additional_knowledge_file] [statement_file]`

 