/**
 * NonlocalConditionalConstraint.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 11, 2008
 *
 */
package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a non-local conditional constraint with the form of "R(A|B)", where "A" (a set), "B" (a set) are disjoint 
 * with each other and both contains at least one variable from the Bayesian Belief Network.
 *
 */
public class NonlocalConditionalConstraint extends ConditionalConstraint {
	
	/**
	 * Constructs a non-local conditional constraint, R(A|B), with |A|>=1, |B|>=1, "A" and "B" are disjoint.
	 * Assume the provided constraint is a legal non-local conditional constraint. We do not check it's validity here.
	 */	
	public NonlocalConditionalConstraint (CondProbDistribution constraint) {
		super("nonlocal", constraint);
		if (constraint == null || constraint.getNumOfPriorVariables() < 1 || constraint.getNumOfCondVariables() < 1) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.NonlocalConditionalConstraint.java: Not a valid non-local conditional constraint!");
		}		
	}
	
}