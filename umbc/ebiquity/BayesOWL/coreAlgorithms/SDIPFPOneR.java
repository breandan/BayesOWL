/**
 * SDIPFPOneR.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Mar. 15, 2005
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import umbc.ebiquity.BayesOWL.commonDefine.*;

import norsys.netica.*;

/**
 * This class implements the 'Simplified D-IPFP' algorithm based on the AISTA-2004 paper, 
 * with only one step for single simple constraint R(V) or R(V|P1,P2,...) provided.
 *
 */
public class SDIPFPOneR {
	Net net;
	SimpleConstraint constraint;
	HardEvidence[] hardEvidences;
	
	/**
	 * Constructor.
	 * 
	 * @param n	Bayesian Net
	 * @param r	simple constraint
	 * @param e	hard evidence
	 */
	public SDIPFPOneR (Net n, SimpleConstraint r, HardEvidence[] e) {
		if (n == null || r == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFPOneR.java: Wrong BBN or simple constraint provided!");
		}
		else {
			net = n;
			constraint = r;
			if (e != null && e.length>0) {
				hardEvidences = new HardEvidence[e.length];
				for (int i=0; i<e.length; i++) 
					hardEvidences[i] = e[i];
			}
		}
	}
	
	/**
	 * The computation process of one-step simplified de-composed iterative proportional fitting procedure (SDIPFP), 
	 * for a single simple constraint.
	 */
	public void computation () {
		try {
			String constraintType = constraint.getConstraintType();
			if (constraintType.equals("marginal")) { // constraint with form R(V)
				SimpleMarginalConstraint thisR = (SimpleMarginalConstraint) constraint;
				SDIPFPMarginalOneR one_step = new SDIPFPMarginalOneR(net,thisR,hardEvidences);
				one_step.computation();
				net = one_step.getNet();
			}
			else if (constraintType.equals("conditional")) { //constraint with form R(V|P1,P2,...)
				SimpleConditionalConstraint thisR = (SimpleConditionalConstraint) constraint;
				SDIPFPConditionalOneR one_step = new SDIPFPConditionalOneR(net,thisR,hardEvidences);
				one_step.computation();
				net = one_step.getNet();						
			}
			else { //otherwise
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFPOneR.java: Wrong simple constraint provided!");
			}		
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}

}
