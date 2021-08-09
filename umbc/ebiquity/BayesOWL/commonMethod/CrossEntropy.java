/**
 * CrossEntropy.java
 *
 * @author Zhongli Ding (Original)
 * 
 * Created on 2005-3-4
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aut. 07, 2008, comments added
 * 
 */

package umbc.ebiquity.BayesOWL.commonMethod;

import umbc.ebiquity.BayesOWL.commonDefine.JointProbDistribution;

/**
 * This class provides method to compute the cross entropy between two joint probability distributions.<br>
 * <br>
 * Definition of I-divergence (also named Kullback-Leibler divergence, cross-entroy):<br>
 * if P(X)<<Q(X), then:<br>
 * 		I(P||Q) = sum_over_all_X's assignments{P(x)log(P(x)/Q(x))}, only picks those assignments when P(x)>0<br>
 * if P(X)!(<<)Q(X), then:<br>
 * 		I(P||Q) = positive infinity<br>
 *<br>
 * Generally,I(P||Q) != I(Q||P)<br>
 * Note: When P is dominated by Q, we have: 0/0 = 0 and 0*log(0/0) = 0.<br>
 * see also: <br>
 * 	http://en.wikipedia.org/wiki/Kullback-Leibler_divergence<br>
 * 	http://en.wikipedia.org/wiki/Cross_entropy<br>
 * Assume: P(X) and Q(X) are valid distributions, w.r.t our implementation here.<br>
 *
 */
public class CrossEntropy {
	JointProbDistribution P;
	JointProbDistribution Q;
	
	/**
	 * Constructs a new class for cross entropy.
	 * 
	 * @param p	JPD
	 * @param q	JPD
	 */
	public CrossEntropy (JointProbDistribution p, JointProbDistribution q){
		if (p == null || q == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.CrossEntropy.java: Wrong distributions provided!");
		}
		else {
			P = p;
			Q = q;
		}
	}
	
	/**
	 * Computes and returns the cross entropy between two distributions P and Q.
	 * If P(!<<)Q, returns -1.0, which means "positive infinity".
	 * 
	 * @return	cross entropy
	 */
	public double getCrossEntropy() {
		boolean dominance = true;
		double crossEntropy = 0.0;
		int numOfEntries = P.getNumOfEntries();
		for (int i=0; i<numOfEntries; i++){
			double pv = P.getProbEntry(P.getIndices(i));
			double qv = Q.getProbEntry(Q.getIndices(i));
			//assume pv, qv is a value from [0.0,1.0]
			if (pv>0){ //ignore the case that pv=0.0
				if (qv>0) {
					crossEntropy = crossEntropy + pv * Math.log(pv/qv);
				}
				else {
					dominance = false; //in this case, pv>0 but qv=0
					break;
				}
			}			
		}
		if (dominance) {
			crossEntropy = crossEntropy / Math.log(2.0); // note: log2(x) = ln x / ln 2
			return crossEntropy;
		}
		else {
			return -1.0;
		}
	}

}