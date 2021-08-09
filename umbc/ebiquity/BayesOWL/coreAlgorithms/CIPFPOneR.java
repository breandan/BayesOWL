/**
 * CIPFPOneR.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on 2005-3-3
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug, 13, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import umbc.ebiquity.BayesOWL.commonDefine.*;

/**
 * This class implements algorithm "CIPFP" for one constraint.<br>
 * <br>
 * Q(X): A joint distribution of random variables in X.<br> 
 * R(Si|Li): A conditional constraint to be satisfied and Si, Li are disjoint non-empty subsets of X.<br>
 * 	(1) Q_k(X) = 0 										if Q_k-1(Si|Li) = 0 <br>
 * 	(2) Q_k(X) = Q_k-1(X) * R(Si|Li) / Q_k-1(Si|Li) 	if Q_k-1(Si|Li) > 0<br>
 * Assume Q(X), R(Si|Li) are valid distributions, complete and consistent.<br>
 *
 */
public class CIPFPOneR {
	JointProbDistribution Q;	//the joint probability distribution Q(X)
	CondProbDistribution R;		//the given constraint R(Si|Li), where Si and Li are non-empty disjoint subsets of X

	/**
	 * Constructor.
	 * 
	 * @param q:	JPD
	 * @param r:	constraint (conditional PD)	
	 */
	public CIPFPOneR (JointProbDistribution q, CondProbDistribution r) {
		if (q == null || r == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.CIPFPOneR.java: Wrong distribution provided!");
		}
		else {
			Q = q;
			R = r;
		}
	}
	
	/**
	 * The computation process of one-step conditional iterative proportional fitting procedure (CIPFP), 
	 * for a single conditional constraint.
	 */
	public void computation () {
		int numOfEntries_Q = Q.getNumOfEntries();			//the total number of entries in the distribution Q(X) 
		int numOfVariables_Q = Q.getNumOfVariables();		//the total number of variables involved in the distribution Q(X)
		int numOfPriorVariables_R = R.getNumOfPriorVariables();	//the total number of prior variables involved in this constraint
		int numOfCondVariables_R = R.getNumOfCondVariables();	//the total number of condition variables involved in this constraint
		int numOfVariables_R = numOfCondVariables_R + numOfPriorVariables_R; //the total number of variables (priors+conditions) involved in this constraint
		int[] dims_in_Q = new int[numOfVariables_R];		//the corresponding dimension numbers about Si+Li (all the variables in R) in Q
		RandomVariable[] varsCond_in_R = new RandomVariable[numOfCondVariables_R];	//the condition variables involved in this constraint
		RandomVariable[] vars_in_R = new RandomVariable[numOfVariables_R]; //the variables (priors+conditions) involved in this constraint
		for (int i = 0; i < numOfCondVariables_R; i++) {
			varsCond_in_R[i] = R.getCondVariable(i);
			vars_in_R[i] = varsCond_in_R[i];
			dims_in_Q[i] = Q.getDimension(varsCond_in_R[i].getName());
		} // end-for-i
		for (int i = 0; i < numOfPriorVariables_R; i++) {
			vars_in_R[numOfCondVariables_R+i] = R.getPriorVariable(i);
			dims_in_Q[numOfCondVariables_R+i] = Q.getDimension(vars_in_R[numOfCondVariables_R+i].getName());
		} // end-for-i
		JointProbDistribution marginalDist1_in_Q = Q.getMarginalDist(vars_in_R); 		//the marginal distribution Q(Si+Li) 
		JointProbDistribution marginalDist2_in_Q = Q.getMarginalDist(varsCond_in_R);	//the marginal distribution Q(Li) 		
		for (int i = 0; i < numOfEntries_Q; i++) { //update Q(X) entry by entry according to the CIPFP algorithm
			int[] indices_Q = new int[numOfVariables_Q];
			indices_Q = Q.getIndices(i);
			int[] indices_R = new int[numOfVariables_R];
			int[] indicesCond_R = new int[numOfCondVariables_R];
			for (int j = 0; j < numOfCondVariables_R; j++) {
				indices_R[j] = indices_Q[dims_in_Q[j]];
				indicesCond_R[j] = indices_R[j];
			}
			for (int j = 0; j < numOfPriorVariables_R; j++)
				indices_R[numOfCondVariables_R + j] = indices_Q[dims_in_Q[numOfCondVariables_R + j]];
			double entry_Q_curr = 0.0;
			double entry_Q_prev = Q.getProbEntry(indices_Q);	
			double entry_R = R.getCondProbEntry(indices_R);
			double entry_Q_prev_marginal1 = marginalDist1_in_Q.getProbEntry(indices_R);
			double entry_Q_prev_marginal2 = marginalDist2_in_Q.getProbEntry(indicesCond_R);
			double entry_Q_prev_conditional = 0.0;
			if (entry_Q_prev_marginal1 == 0.0 || entry_Q_prev_marginal2 == 0.0) {
				entry_Q_prev_conditional = 0.0;
			}
			else {
				entry_Q_prev_conditional = entry_Q_prev_marginal1 / entry_Q_prev_marginal2;
			}
			if (entry_Q_prev_conditional > 0.0) {
				entry_Q_curr = entry_Q_prev * entry_R / entry_Q_prev_conditional;
			}
			if (entry_Q_curr != entry_Q_prev) {
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