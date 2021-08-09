/**
 * CIPFP.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 08, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug, 13, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;


/**
 * Q(X): A joint distribution of random variables in X. <br>
 * {R(Si|Li)}: A set of conditional constraints to be satisfied and each Si, Li are disjoint subsets of X; Si must be non-empty, Li may be empty.<br>
 * 	(1) Q_k(X) = 0 										if Q_k-1(Si|Li) = 0 <br>
 * 	(2) Q_k(X) = Q_k-1(X) * R(Si|Li) / Q_k-1(Si|Li) 	if Q_k-1(Si|Li) > 0<br>
 * Assume Q(X), {R(Si|Li)} are valid distributions, complete and consistent.<br>
 *
 */
public class CIPFP {	
	JointProbDistribution Q;		//the joint probability distribution Q(X)
	ProbDistribution[] R;			//the given constraints, in the form of either {R(Si)} when Li is empty; or {R(Si|Li)} when Li is not empty
	long timeElapsed;
	int loopsUsed;
	
	/**
	 * Constructor.
	 */
	public CIPFP (JointProbDistribution q, ProbDistribution[] r) {
		if (q == null || r == null || r.length == 0) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.CIPFP.java: Wrong distributions provided!");
		}
		else {
			Q = new JointProbDistribution(q);
			R = new ProbDistribution[r.length];
			for (int i = 0; i<r.length; i++) {
				R[i] = r[i];
			}
			timeElapsed = 0;
			loopsUsed = 0;
		}
	}
	
	/**
	 * The loop process of conditional iterative proportional fitting procedure (CIPFP).
	 * 
	 * @param maxLoops if this procedure does not converge in 'maxLoops' number of loops, we think it will not converge at all. 
	 * @param threshold if the difference between two iterations is smaller than the threshold we think the algorithm converges.
	 */
	public void run (int maxLoops, double threshold) {
		// trace the time
		Date startDate = new Date();
		long startTime = startDate.getTime();
		// begin of procedure
		double totalVariance = 1.0;
		int counter = 0;
		boolean success = true;
		do {
			if (counter>maxLoops) {
				success = false;
				break;
			}
			JointProbDistribution Q_orig = new JointProbDistribution(Q);
			for (int j=0; j<R.length; j++) { //iterate over all the given constraints one by one
				String distributionType = R[j].getDistributionType();
				if (distributionType.equals("JPD")) { 		// constraint with form R(Si)
					JointProbDistribution thisR = (JointProbDistribution) R[j];
					IPFPOneR one_step = new IPFPOneR(Q,thisR);
					one_step.computation();
					Q = one_step.getDistribution();
				}
				else if (distributionType.equals("CPD")) {	//constraint with form R(Si|Li)
					CondProbDistribution thisR = (CondProbDistribution) R[j];
					CIPFPOneR one_step = new CIPFPOneR(Q,thisR);
					one_step.computation();
					Q = one_step.getDistribution();
				}
				else { //otherwise
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.CIPFP.java: Wrong constraints provided!");
				}
			} // end-for-j
			counter++;
			TotalVariance obj = new TotalVariance(Q_orig,Q);
			totalVariance = obj.getTotalVariance();
		}
		while (totalVariance>threshold);
		// end of procedure
		// trace the time
		Date endDate = new Date();
		long endTime = endDate.getTime();
		timeElapsed = endTime - startTime;	
		loopsUsed = counter;
		if (!success)
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.CIPFP.java: Failure - The set of constraints will not be converged in " + maxLoops + " loops!");
	}
	
	/**
	 * Returns the joint probability distrbution involved in the computation.
	 */
	public JointProbDistribution getDistribution() {
		return Q;
	}

	/**
	 * Returns the execution time of running the algorithm. 
	 */
	public long getExecTime() {
		return timeElapsed;
	}

	/**
	 * Returns the number of iteration steps of running the algorithm. 
	 */
	public int getExecLoops() {
		return loopsUsed;
	}

}