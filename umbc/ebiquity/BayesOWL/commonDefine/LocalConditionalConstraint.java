/**
 * LocalConditionalConstraint.java
 * 
 * @author Zhongli Ding
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 11, 2007
 *
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a local conditional constraint with the form of "R(C|L)", 
 * where "L" (a set) contains at least one parent variables of variable C from 
 * the Bayesian Belief Network.
 *
 */
public class LocalConditionalConstraint extends ConditionalConstraint {
	String localConceptName;
	int numOfParentsInvolved;
	String[] parentNamesInvolved;
	
	/**
	 * Constructor.
	 * Constructs a local conditional constraint, 'R(C|L)' (L is a non-empty subset of C's parents, C is a variable).
	 * Assume the provided constraint is a legal local conditional constraint. We do not check it's validity here.
	 * 
	 * @param constraint	conditional PD
	 */
	public LocalConditionalConstraint (CondProbDistribution constraint) {
		super("local", constraint);
		if (constraint == null || (constraint.getNumOfPriorVariables()!= 1) || constraint.getNumOfCondVariables()<1) { 
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.LocalConditionalConstraint.java: Not a valid local conditional constraint!");
		}
		else {
			localConceptName = constraint.getPriorVariable(0).getName();
			numOfParentsInvolved = constraint.getNumOfCondVariables();
			parentNamesInvolved = new String[numOfParentsInvolved];
			for (int i=0; i<numOfParentsInvolved; i++) {
				String pname = constraint.getCondVariable(i).getName();
				parentNamesInvolved[i] = pname;
			}
		}		
	}

	/**
	 * This method gets the local concept name in this local conditional constraint.
	 * @return	concept name
	 */
	public String getConceptName() {
		return localConceptName;
	}	

	/**
	 * Gets the number of parent concepts involved in this local conditional constraint.
	 * 
	 * @return	parent concept number	
	 */
	public int getNumOfParentsInvolved() {
		return numOfParentsInvolved;
	}

	/**
	 * Gets an array of involved parent names in this local conditional constraint.
	 * 
	 * @return	parent names
	 */
	public String[] getParentNamesInvolved() {
		return parentNamesInvolved;
	}
	
}