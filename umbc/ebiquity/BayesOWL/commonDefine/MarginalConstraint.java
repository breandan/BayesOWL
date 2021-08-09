/**
 * MarginalConstraint.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 11, 2008
 *
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class is  an abstract class.
 * Super class of "LocalMarginalConstraint" and "NonlocalMarginalConstraint".
 * 
 * A marginal constraint has the form "R(Y)", where "Y" (a set) contains 
 * at least one variables from the Bayesian Belief Network.
 *
 */
public abstract class MarginalConstraint extends Constraint {	
	String scopeType;
	JointProbDistribution constraint;
	int numOfVariables;
	String[] variableNames;
	
	/**
	 * Constructor.
	 * Constructs a marginal constraint with the specific type, i.e., local or non-local.
	 * 
	 * @param stype	scope type
	 * @param con	JPD
	 */
	public MarginalConstraint(String stype, JointProbDistribution con) {
		super("marginal");
		setScopeType(stype);
		if (con == null || con.getNumOfVariables()<1) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.MarginalConstraint.java: Not a valid marginal constraint!");			
		}
		else {
			constraint = con;
			numOfVariables = con.getNumOfVariables();
			variableNames = new String[numOfVariables];
			for (int i=0; i<numOfVariables; i++) {
				variableNames[i] = con.getVariable(i).getName();
			}
		}
	}
	
	/**
	 * Sets the scope type of this marginal constraint, i.e., local or non-local.
	 * 
	 * @param stype	scope type
	 */
	public void setScopeType (String stype) {
		if (stype == null || stype.equals("")) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.MarginalConstraint.java: A marginal constraint's scope type can not be an empty string!");
		}
		else {
			scopeType = stype;	
		}
	}
	
	/**
	 * Gets the scope type of this marginal constraint.
	 * 
	 * @return	scope type
	 */
	public String getScopeType () {
		return scopeType;
	}
		
	/**
	 * Gets the underlying joint probability distribution for this marginal constraint.
	 * 
	 * @return	JPD
	 */
	public JointProbDistribution getDistribution() {
		return constraint;
	}
	
	/**
	 * Gets the number of variables involved in this marginal constraint.
	 * 
	 * @return	variable number
	 */
	public int getNumOfVariables() {
		return numOfVariables;
	}
	
	/**
	 * Gets an array of variable names involved in this marginal constraint.
	 * 
	 * @return	variable names
	 */
	public String[] getVariableNames() {
		return variableNames;
	}

	/**
	 * Override toString method.
	 * Returns a string representation.
	 */
	public String toString() {
		return scopeType + " marginal constraint:\n" + constraint.toString();
	}
	
}