/**
 * HardEvidence.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 09, 2004
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class implements one hard evidence of BBN.
 * A hard evidence is a node in BN.
 *
 */
public class HardEvidence {	
	String nodeName;
	String nodeState;
	
	/**
	 * Constructor.
	 * 
	 * @param name:	hard evidence name
	 * @param state:	hard evidence state
	 */
	public HardEvidence (String name, String state) {
		if (name == null || name.equals("") || state == null || state.equals("")) { 
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.Transform.HardEvidence.java: Wrong hard evidence specified!");
		}
		else {
			nodeName = name;
			nodeState = state;
		}
	}
	
	/**
	 * This method returns the variable name of this hard evidence. 
	 * 
	 * @return hard evidence name
	 */
	public String getName () {
		return nodeName;
	}
	
	/**
	 * This method returns the variable state of this hard evidence.
	 * 
	 * @return hard evidence state
	 */
	public String getState () {
		return nodeState;
	}
	
}
