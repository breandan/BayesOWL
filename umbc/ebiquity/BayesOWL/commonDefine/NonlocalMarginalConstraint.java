/**
 * NonlocalMarginalConstraint.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 11, 2008
 *
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a non-local marginal constraint with the form of "R(Y)", where "Y" (a set) contains 
 * at least two variables from the Bayesian Belief Network.
 *
 */
public class NonlocalMarginalConstraint extends MarginalConstraint {
	
	/**
	 * Constructs a non-local marginal constraint, R(Y), with |Y|>=2.
	 * Assume the provided constraint is a legal non-local marginal constraint. We do not check it's validity here.
	 */	
	public NonlocalMarginalConstraint (JointProbDistribution constraint) {
		super("nonlocal", constraint);
		if (constraint == null || constraint.getNumOfVariables() < 2) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.NonlocalMarginalConstraint.java: Not a valid non-local marginal constraint!");
		}
	}
	
}
