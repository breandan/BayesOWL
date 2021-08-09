/**
 * TotalVariance.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on 2005-3-4
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 07, 2008, comments added
 *
 */

package umbc.ebiquity.BayesOWL.commonMethod;

import umbc.ebiquity.BayesOWL.commonDefine.JointProbDistribution;

/**
 * This class provides method to compute the total variance between two joint probability distributions.<br>
 * <br>
 * Definition of total variance:<br>
 * 	|P-Q| = sum_over_all_X's assignments{|P(x)-Q(x)|}<br>
 * 	|P-Q| = |Q-P|<br>
 * <br>
 * Assume P(X) and Q(X) are valid distributions, w.r.t our implementation here.<br>
 *
 */
public class TotalVariance {
	JointProbDistribution P;
	JointProbDistribution Q;
	
	/**
	 * Constructs a new class for total variance. 
	 * 
	 * @param p	JPD
	 * @param q	JPD
	 */
	public TotalVariance (JointProbDistribution p, JointProbDistribution q) {
		if (p == null || q == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.TotalVariance.java: Wrong distributions provided!");
		}
		else {
			P = p;
			Q = q;
		}
	}
	
	/**
	 * Computes and returns the total variance between two distributions P and Q.
	 * 
	 * @return	total variance
	 */
	public double getTotalVariance() {
		double totalVariance = 0.0;
		int numOfEntries = P.getNumOfEntries();
		for (int i=0; i<numOfEntries; i++) {
			double pv = P.getProbEntry(P.getIndices(i));
			double qv = Q.getProbEntry(Q.getIndices(i));
			totalVariance += Math.abs(pv - qv);
		}
		return totalVariance;
	}

}