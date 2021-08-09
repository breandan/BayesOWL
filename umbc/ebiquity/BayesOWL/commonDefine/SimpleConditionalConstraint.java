/**
 * SimpleConditionalConstraint.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 09, 2004
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 * Used by classes SDIPFP, SDIPFPOneR, SDIPFPConditionalOneR
 * 
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a simple conditional constraint with the form 
 * of "R(V|C1, C2, ...)", where "V", "C1", "C2" ... are variables in Bayesian Belief Network.
 *
 */
public class SimpleConditionalConstraint extends SimpleConstraint {
	
	CondProbDistribution constraint;
	
	/**
	 * Constructor.
	 * 
	 * @param r	conditional probability distribution
	 */
	public SimpleConditionalConstraint (CondProbDistribution r) {
		super("conditional");
		if (r == null || r.getNumOfPriorVariables() != 1) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConditionalConstraint.java: Not a valid simple conditional constraint!");
		}
		else {
			constraint = new CondProbDistribution(r);
		}
	}
	
	/**
	 * This method returns the name of the prior variable "V" regarding to this simple conditional constraint.
	 * 
	 * @return	node name
	 */
	public String getNodeName () {
		return constraint.getPriorVariable(0).getName();
	}

	/**
	 * This method returns the name of a state of prior variable "V" in the specified index.
	 * 
	 * @param idx	index
	 * @return	node state name
	 */
	public String getNodeStateName (int idx) {
		int numOfStates = constraint.getPriorVariable(0).getNumOfStates();
		if (idx<0 || idx >= numOfStates) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConditionalConstraint.java: Wrong state index for the node specified!");
		}
		else {
			return constraint.getPriorVariable(0).getState(idx);
		}
	}
	
	/**
	 * This method returns the index of a state given its name, w.r.t prior variable "V".
	 * 
	 * @param state	state name
	 * @return	state index
	 */ 
	public int getNodeStateIndex (String state) {
		return constraint.getPriorVariable(0).getStateIndex(state);
	}	
	
	/**
	 * This method returns the number of conditions involved.
	 * 
	 * @return	condition number
	 */
	public int getNumOfConds () {
		return constraint.getNumOfCondVariables();
	}
	
	/**
	 * This method returns the conditional variable name in the specified index.
	 * 
	 * @param idx	node index
	 * @return	node name
	 */
	public String getCondNodeName (int idx) {
		return constraint.getCondVariable(idx).getName();
	}
	
	/**
	 * This method returns the conditional probability value of "V" about a specified state, 
	 * given an indices array of ints of its conditions "C1, C2, ...".
	 * 
	 * @param idx	state index
	 * @param idxCond	condition indices
	 * @return	conditional probability value
	 */
	public double getNodeProbValueByStateIndex (int idx, int[] idxCond) {
		int numOfStates = constraint.getPriorVariable(0).getNumOfStates();
		if (idx < 0 || idx >= numOfStates) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConditionalConstraint.java: Wrong state index for the node specified!");
		}
		else {
			int numOfConditions = constraint.getNumOfCondVariables();
			if (idxCond == null || idxCond.length != numOfConditions) {
				throw new NullPointerException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConditionalConstraint.java: Wrong indices about conditions specified!");
			}
			else {
				int[] indices = new int[numOfConditions+1];
				for (int i=0; i<numOfConditions; i++) 
					indices[i] = idxCond[i];
				indices[numOfConditions] = idx;
				return constraint.getCondProbEntry(indices);
			}
		}
	}

	/**
	 * This method returns the conditional probability value of "V" about a specified state, 
   	 * given an indices array of ints of its conditions "C1, C2, ...".
   	 * 
	 * @param state	state name
	 * @param idxCond	condition indices
	 * @return	conditional probability value
	 */
	public double getNodeProbValueByStateName (String state, int[] idxCond) {
		int idx = getNodeStateIndex (state);
		if (idx<0) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConditionalConstraint.java: Wrong state index for the node specified!");
		}
		else {
			return getNodeProbValueByStateIndex(idx,idxCond);
		}
	}

}