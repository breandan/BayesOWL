/**
 * LocalMarginalConstraint.java
 * 
 * @author Zhongli Ding(original)
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 11, 2008
 *
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a local marginal constraint with the form of "R(C)" or "R(C, L)", where "L" (a set) contains 
 * at least one parent variables of variable "C" from the Bayesian Belief Network.
 *
 */
public class LocalMarginalConstraint extends MarginalConstraint {
	String localConceptName;
	int numOfParentsInvolved;
	String[] parentNamesInvolved;
	
	/**
	 * Constructor.
	 * Constructs a local marginal constraint, either in 'R(C)' or 'R(C,L)' (L is a non-empty subset of C's parents).
	 * Assume the provided constraint is a legal local marginal constraint. We do not check it's validity here.
	 * 
	 * @param constraint	JPD
	 * @param concept	concept
	 */
	public LocalMarginalConstraint (JointProbDistribution constraint, String concept) {
		super("local", constraint);
		if (constraint == null || concept == null || concept.equals("")) { 
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.LocalMarginalConstraint.java: Not a valid local marginal constraint!");
		}
		else {
			localConceptName = concept;
			numOfParentsInvolved = constraint.getNumOfVariables() - 1;
			if (numOfParentsInvolved > 0) {
				parentNamesInvolved = new String[numOfParentsInvolved];
				for (int i=0; i<constraint.getNumOfVariables(); i++) {
					String pname = constraint.getVariable(i).getName();
					if (!pname.equals(concept)) {
						parentNamesInvolved[i] = pname;
					}
				}
			}
			else {
				numOfParentsInvolved = 0;
				parentNamesInvolved = null;
			}
		}
	}
	
	/**
	 * Gets the local concept name in this local marginal constraint.
	 * 
	 * @return	local concept name
	 */
	public String getConceptName() {
		return localConceptName;
	}	

	/**
	 * Gets the number of parent concepts involved in this local marginal constraint.
	 * 
	 * @return	parent number
	 */
	public int getNumOfParentsInvolved() {
		return numOfParentsInvolved;
	}
	
	/**
	 * Gets an array of involved parent names in this local marginal constraint.
	 * 
	 * @return	parent names
	 */
	public String[] getParentNamesInvolved() {
		return parentNamesInvolved;
	}

}