/**
 * IPFPOneR.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on 2005-3-3
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 13, 2008
 * 
 * See paper:
 * 	I-Divergence Geometry of Probability Distributions and Minimization Problems
 * 	I. Csiszar
 * 	The Annals of Probability, Vol. 3, No. 1 (Feb., 1975), pp. 146-158
 * 
 * 	Methods of Probabilistic Knowledge Integration
 * 	Jiri Vomlel
 * 	PhD thesis
 * 
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import umbc.ebiquity.BayesOWL.commonDefine.*;

/**
 * This class implements standard IPFP algorithm for only one constraint.<br>
 * <br>
 * Q(X): A joint distribution of random variables in X.<br> 
 * R(Si): A constraint to be satisfied, where Si is a non-empty subset of X.<br>
 * 	(1) Q_k(X) = 0 								if Q_k-1(Si) = 0 <br>
 * 	(2) Q_k(X) = Q_k-1(X) * R(Si) / Q_k-1(Si) 	if Q_k-1(Si) > 0<br>
 * Assume Q(X), R(Si) are valid distributions, complete and consistent.<br>
 *
 */
public class IPFPOneR {
	JointProbDistribution Q;		//the joint probability distribution Q(X)
	JointProbDistribution R;		//the given marginal constraint R(Si) on a subset of X

	/**
	 * Constructor.
	 * 
	 * @param q:	JPD
	 * @param r:	constraint (JPD)
	 */
	public IPFPOneR (JointProbDistribution q, JointProbDistribution r) {
		if (q == null || r == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.IPFPOneR.java: Wrong distribution provided!");
		}
		else {
			/************************************************
			 original code, P and Q should be instanced newly
			Q = q;
			R = r;
			*************************************************/
			Q = new JointProbDistribution(q);
			R = new JointProbDistribution(r);
		}
	}
	
	/**
	 * The computation process of one-step iterative proportional fitting procedure (IPFP) for a single constraint.
	 */
	public void computation () {
		int numOfEntries_Q = Q.getNumOfEntries();			//the total number of entries in the distribution Q(X)
		int numOfEntries_R = R.getNumOfEntries();			//the total number of entries in this constraint R(Si)
		int numOfVariables_Q = Q.getNumOfVariables();		//the total number of variables involved in the distribution Q(X)
		int numOfVariables_R = R.getNumOfVariables(); 		//the total number of variables involved in this constraint
		int[] dims_in_Q = new int[numOfVariables_R];	 	//the corresponding dimension numbers about Si (the variables in R) in Q
		RandomVariable[] vars_in_R = new RandomVariable[numOfVariables_R]; //the variables involved in this constraint
		for (int i=0; i<numOfVariables_R; i++) {
			vars_in_R[i] = R.getVariable(i);
			dims_in_Q[i] = Q.getDimension(vars_in_R[i].getName());
		} // end-for-i
		JointProbDistribution marginalDist_in_Q = Q.getMarginalDist(vars_in_R); //the marginal distribution Q_k-1(Si)
		for (int i=0; i<numOfEntries_R; i++) { //compute 'R(Si)/Q_k-1(Si)'
			double entry_R = R.getProbEntry(R.getIndices(i));
			double entry_Q_marginal = marginalDist_in_Q.getProbEntry(marginalDist_in_Q.getIndices(i));
			double divResult = 0.0;
			if (entry_Q_marginal > 0) {
				 divResult = entry_R / entry_Q_marginal;
			}
			marginalDist_in_Q.addProbEntry(marginalDist_in_Q.getIndices(i),divResult); //note this time the values are not necessary sum up to 1.
		}
		for (int i=0; i<numOfEntries_Q; i++) { //update Q(X) entry by entry according to the IPFP algorithm
			int[] indices_Q = new int[numOfVariables_Q];
			indices_Q = Q.getIndices(i);
			int[] indices_R = new int[numOfVariables_R];
			for (int j = 0; j<numOfVariables_R; j++)
				indices_R[j] = indices_Q[dims_in_Q[j]];
			double entry_Q_curr = 0.0;
			double entry_Q_prev = Q.getProbEntry(indices_Q);
			entry_Q_curr = entry_Q_prev * marginalDist_in_Q.getProbEntry(indices_R);			
			if (!(entry_Q_curr == entry_Q_prev)) {
				Q.addProbEntry(indices_Q,entry_Q_curr);
			}
		} // end-for-i		
	}
	
	/**
	 * Returns the joint probability distribution involved in the computation.
	 * 
	 * @return	JPD
	 */
	public JointProbDistribution getDistribution() {
		return Q;
	}

}
