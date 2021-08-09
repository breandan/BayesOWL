/**
 * SimpleMarginalConstraint.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 09, 2004
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 * Used in classes SDIPFP, SDIPFPMarginalOneR and SDIPFPOneR.
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements a simple marginal constraint with the form of "R(V)", 
 * where "V" is one variable in the Bayesian Belief Network.
 *
 */
public class SimpleMarginalConstraint extends SimpleConstraint {	
	String nodeName;		// "V"'s name
	String[] nodeStates;	// "V"'s states
	double[] probValues;	// "V"'s marginal probability values corresponding to the different states
	
	/**
	 * Constructor.
	 * 
	 * @param r	JPD
	 */
	public SimpleMarginalConstraint(JointProbDistribution r) {
		super("marginal");
		if (r == null || r.getNumOfVariables() != 1) { 
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleMarginalConstraint.java: Not a valid simple marginal constraint!");
		}
		else {
			RandomVariable rndVar = new RandomVariable(r.getVariable(0));
			nodeName = rndVar.getName();
			nodeStates = new String[rndVar.getNumOfStates()];
			probValues = new double[rndVar.getNumOfStates()];
			for (int i=0; i<rndVar.getNumOfStates(); i++) {
				nodeStates[i] = rndVar.getState(i);
				probValues[i] = r.getProbEntry(r.getIndices(i));
			}
		}
	}
	
	/**
	 * This method returns the name of the variable regarding to this simple marginal constraint.
	 * 
	 * @return	node name
	 */
	public String getNodeName () {
		return nodeName;
	}
	
	/**
	 * This method returns the name of a state in the specified index.
	 * 
	 * @param idx	state index
	 * @return	state name
	 */
	public String getNodeStateName (int idx) {
		if (idx < 0 || idx >= nodeStates.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleMarginalConstraint.java: Wrong state index for the random variable specified!");
		}
		else {
			return nodeStates[idx];
		}
	}
	
	/**
	 * This method returns the index of a state given its name.
	 * 
	 * @param state	state name
	 * @return	state index
	 */
	public int getNodeStateIndex (String state) {
		for (int i=0; i<nodeStates.length; i++) {
			if (nodeStates[i].equals(state))
				return i;
		}
		return -1;
	}
	
	/**
	 * This method returns the marginal probability value about a specified state by its index.
	 * 
	 * @param idx	state index
	 * @return	marginal probability value
	 */
	public double getNodeProbValueByStateIndex (int idx) {
		if (idx<0 || idx >= probValues.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleMarginalConstraint.java: Wrong state index for the random variable specified!");
		}
		else {
			return probValues[idx];
		}
	}
	
	/**
	 * This method returns the marginal probability value about a specified state by its name.
	 * 
	 * @param state	state name
	 * @return	marginal probability value
	 */
	public double getNodeProbValueByStateName (String state) {
		int idx = getNodeStateIndex(state);
		if (idx<0) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleMarginalConstraint.java: Wrong state name for the random variable specified!");
		}
		else {
			return probValues[idx];
		}
	}
	
}
