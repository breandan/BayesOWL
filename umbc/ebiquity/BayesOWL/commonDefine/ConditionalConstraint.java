/**
 * ConditionalConstraint.java
 * 
 * @author Zhongli Ding
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 09, 2008
 *
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * ConditionalConstraint is an abstract class.
 * Super class of "LocalConditionalConstraint" and "NonlocalConditionalConstraint".
 * 
 * A conditional constraint has the form "R(A|B)", where "A" (a set), "B" (a set) contains 
 * at least one variables from the Bayesian Belief Network, and are disjoint with each other.
 *
 */
public abstract class ConditionalConstraint extends Constraint {	
	String scopeType;
	CondProbDistribution constraint;
	int numOfPriorVariables;
	int numOfCondVariables;
	int numOfVariables;
	String[] priorVarNames;
	String[] condVarNames;
	String[] variableNames;

	/**
	 * Constructs a conditional constraint with the specific type, i.e., local or non-local.
	 * 
	 * @param stype	scope type
	 * @param con	conditional PD
	 */
	public ConditionalConstraint (String stype, CondProbDistribution con) {
		super("conditional");
		setScopeType(stype);
		if (con == null || con.getNumOfPriorVariables()<1 || con.getNumOfCondVariables()<1) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.ConditionalConstraint.java: Not a valid conditional constraint!");			
		}
		else {
			constraint = con;
			numOfPriorVariables = con.getNumOfPriorVariables();
			numOfCondVariables = con.getNumOfCondVariables();
			numOfVariables = numOfPriorVariables + numOfCondVariables;
			priorVarNames = new String[numOfPriorVariables];
			condVarNames = new String[numOfCondVariables];
			variableNames = new String[numOfVariables];
			for (int i=0; i<numOfPriorVariables; i++) {
				priorVarNames[i] = con.getPriorVariable(i).getName();
				variableNames[i] = priorVarNames[i];
			}
			for (int i=0; i<numOfCondVariables; i++) {
				condVarNames[i] = con.getCondVariable(i).getName();
				variableNames[numOfPriorVariables+i] = condVarNames[i];
			}
		}
		
	}
	
	/**
	 * Sets the scope type of this conditional constraint, i.e., local or non-local.
	 * 
	 * @param stype	scope type
	 */
	public void setScopeType (String stype) {
		if (stype == null || stype.equals("")) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.ConditionalConstraint.java: A conditional constraint's scope type can not be an empty string!");
		}
		else {
			scopeType = stype;	
		}
	}
	
	/**
	 * Gets the scope type of this conditional constraint.
	 * 
	 * @return	scope type
	 */
	public String getScopeType () {
		return scopeType;
	}

	/**
	 * Gets the underlying conditional probability distribution for this conditional constraint.
	 * 
	 * @return	constraint
	 */
	public CondProbDistribution getDistribution() {
		return constraint;
	}
	
	/**
	 * Gets the number of variables involved in this conditional constraint.
	 * 
	 * @return	number of variables
	 */
	public int getNumOfVariables() {
		return numOfVariables;
	}
	
	/**
	 * Gets an array of variable names involved in this conditional constraint.
	 * 
	 * @return	variable name
	 */
	public String[] getVariableNames() {
		return variableNames;
	}

	/**
	 * Gets the number of prior variables involved in this conditional constraint.
	 * 
	 * @return	number of prior variables
	 */
	public int getNumOfPriorVariables() {
		return numOfPriorVariables;
	}
	
	/**
	 * Gets an array of prior variable names involved in this conditional constraint.
	 * 
	 * @return	prior variables' names
	 */
	public String[] getPriorVariableNames() {
		return priorVarNames;
	}

	/**
	 * Gets the number of condition variables involved in this conditional constraint.
	 * 
	 * @return	number of conditional variables
	 */
	public int getNumOfCondVariables() {
		return numOfCondVariables;
	}
	
	/**
	 * Gets an array of condition variable names involved in this conditional constraint.
	 * 
	 * @return	conditional variables' names
	 */
	public String[] getCondVariableNames() {
		return condVarNames;
	}
	
	/**
	 * Override toString method.
	 * Returns a string representation.
	 */
	public String toString() {
		return scopeType + " conditional constraint:\n" + constraint.toString();
	}
	
}