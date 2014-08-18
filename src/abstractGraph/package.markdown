@package abstractGraph

@brief
This package contains the core and mainly abstracted classes to describe 
state machines.

The classes directly in this directory define the abstract classes for 
the state machines.
All verifiers (in the abstractGraph.verifiers package) and the model checkers
 ({@link ModelChecker}) are supposed to work on these abstract classes.
 
 There is also the definitions of the boolean formulas and the events that
 label transitions are used by the simulators.

@package abstractGraph.conditions

@brief
The implementation for boolean formulas, and more generally for formulas found
in the Condition fields.

The main class is the {@link Formula} class. In addition to this, the Domain
Specific language introduces the possibility to have enumerated variable.
For instance, it allows the user to define a variable to have its value within
a set (e.g; we can define x to be defined in {one, two, three}. In that case,
we must be able to find x = one in the Condition fields (and not only x or not x).
The EnumerationEqualityFormula class represents such kind of condition.

The cnf package contains the necessary classes to generate formulas
in the Conjunctive Normal Form (CNF). This is required because SAT-solvers
take mostly CNF formulas as their inputs.

The parser package is only a simple parser that parses boolean formulas using 
the true, false keywords, and the OR, NOT and AND keywors (other token are 
accepted such as | or &. See the grammar for further details).

To create formulas over variables, one need to use a {@link FormulaFactory}.
         
To evaluate a formula, you first need to create an {@link AbstractValuation}
for the formula. In particular, to set the value for a variable,
one need to retrieve the variables from the FormulaFactory.