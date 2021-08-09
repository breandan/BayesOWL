/**
 * IPFP.java
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 07, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 13, 2008
 * 
 * See paper:<br>
 * 	I-Divergence Geometry of Probability Distributions and Minimization Problems<br>
 * 	I. Csiszar<br>
 * 	The Annals of Probability, Vol. 3, No. 1 (Feb., 1975), pp. 146-158<br>
 * 
 * 	Methods of Probabilistic Knowledge Integration<br>
 * 	Jiri Vomlel<br>
 * 	PhD thesis<br>
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.io.*;
import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;



/**
 * This class implements standard IPFP algorithm.<br>
 * Also see class IPFPOneR.<br>
 * <br>
 * Q(X): A joint distribution of random variables in X.<br> 
 * {R(Si)}: A set of constraints to be satisfied and each Si is a non-empty subset of X.<br>
 * 	(1) Q_k(X) = 0 								if Q_k-1(Si) = 0 <br>
 * 	(2) Q_k(X) = Q_k-1(X) * R(Si) / Q_k-1(Si) 	if Q_k-1(Si) > 0<br>
 * Assume Q(X), {R(Si)} are valid distributions, complete and consistent.<br>
 *
 */
public class IPFP {	
	JointProbDistribution Q;		//the joint probability distribution Q(X)
	JointProbDistribution[] R;		//the given constraints {R(Si)}
	long timeElapsed;
	int loopsUsed;
	
	/**
	 * Constructor.
	 * 
	 * @param q:	JPD
	 * @param r:	JPD (constraints)
	 */
	public IPFP (JointProbDistribution q, JointProbDistribution[] r) {
		if (q == null || r == null || r.length == 0) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.IPFP.java: Wrong distributions provided!");
		}
		else {
			Q = new JointProbDistribution(q);
			R = new JointProbDistribution[r.length];
			for (int i = 0; i<r.length; i++) {
				R[i] = r[i];
			}
			timeElapsed = 0;
			loopsUsed = 0;
		}
	}
	
	/**
	 * The loop process of iterative proportional fitting procedure (IPFP).
	 * 
	 * @param maxLoops if this procedure does not converge in 'maxLoops' number of loops, we think it will not converge at all. 
	 * @param threshold if the difference between two iterations is smaller than the threshold we think the algorithm converges.
	 */
	public void run (int maxLoops, double threshold) {
		// begin of procedure
		double totalVariance = 1.0;
		int counter = 0;
		boolean success = true;

		// trace the time
		Date startDate = new Date();
		long startTime = startDate.getTime();
		
		// do IPFP
		do {
			if (counter>maxLoops) {
				success = false;
				break;
			}
			JointProbDistribution Q_ori = new JointProbDistribution(Q);
			for (int j=0; j<R.length; j++) { 	//iterate over all the given constraints one by one
				IPFPOneR one_step = new IPFPOneR(Q,R[j]);
				one_step.computation();
				Q = one_step.getDistribution();
				counter++;
			} // end-for-j
			TotalVariance obj = new TotalVariance(Q_ori,Q);
			totalVariance = obj.getTotalVariance();
		}while (totalVariance > threshold);
		// end of procedure

		// trace the time
		Date endDate = new Date();
		long endTime = endDate.getTime();
		timeElapsed = endTime - startTime;
		loopsUsed = counter;
		if (!success)
		{
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.IPFP.java: Failure - The set of constraints will not be converged in " + maxLoops + " loops!");
		}
	}
	
	/**
	 * Returns the joint probability distribution involved in the computation.
	 * 
	 * @return	JPD
	 */
	public JointProbDistribution getDistribution() {
		return Q;
	}

	/**
	 * Returns the execution time of running the algorithm. 
	 * 
	 * @return	execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}

	/**
	 * Returns the number of iteration steps of running the algorithm. 
	 * 
	 * @return	execution iteration
	 */
	public int getExecLoops() {
		return loopsUsed;
	}
	
	/**
	 * This method write the result to specified file, just for result analyze.
	 * 
	 * @param s:	string line to be write
	 * @param filepath:	file path, including file extension
	 */
	private void writeDatatoFiles(String s, String filepath)
	{
		try{
			RandomAccessFile raf = new RandomAccessFile(filepath, "rw");
			raf.seek(raf.length());
			raf.writeBytes(s);
			raf.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
