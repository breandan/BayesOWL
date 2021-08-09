/**
 * BN2JPD.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 08, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.commonMethod;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;



import norsys.netica.*;

/**
 * This class converts a given BBN (assume one that is valid) into a joint probability distribution.
 *
 */
public class BN2JPD {
	JointProbDistribution jpd;

	/**
	 * Constructor - 1:
     * Takes a BN as argument and gets the underlying joint probability distribution.
     * 
	 * @param net:	Bayesian Net
	 */
	public BN2JPD (Net net) {
		constructJPD(net);
	}
	
	/**
	 * Constructor - 2:
	 * Takes a saved BN file name as argument and gets the underlying joint probability distribution.
	 * 
	 * @param fname:	BN file name
	 */
	public BN2JPD (String fname) {
		try {
			//Environ env = new Environ("+PengY/UMarylandBC/120,310-2-A/27700");
			Net net = new Net(new Streamer(fname));
			constructJPD(net);
			//env.finalize();
		}
		catch (Exception e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.BN2JPD.java: Wrong BN file name provided!");
			e.printStackTrace();
		}
	}

	/**
	 * Recovers the underlying joint probability distribution from the given BBN.
	 * 
	 * @param net:	Bayesian net
	 */
	private void constructJPD (Net net) {
		try {
			net.compile();
			NodeList nodes = net.getNodes();
			RandomVariable[] rndVars = new RandomVariable[nodes.size()];
			int idx = 0;
			int numOfProbEntries = 1;
			Enumeration enum_enum = nodes.elements(); //enum_enum original: enum  Oct. 30 2007 Shenyong
			while (enum_enum.hasMoreElements()) {
				Node node = (Node) enum_enum.nextElement();
				int numOfStates = node.getNumStates();
				numOfProbEntries *= numOfStates;
				String nodeName = node.getName();
				String[] states = new String[numOfStates];
				for (int i=0; i<numOfStates; i++) {
					states[i] = node.state(i).getName();
				}
				RandomVariable rndVar = new RandomVariable(nodeName, states);
				rndVars[idx] = rndVar;
				idx++;
			}
			jpd = new JointProbDistribution(rndVars);
			for (int i=0; i<numOfProbEntries; i++) {
				int[] indices = jpd.getIndices(i);
				double pv = net.getJointProbability(nodes,indices);
				jpd.addProbEntry(indices,pv);
			}
		}
		catch (Exception e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.BN2JPD.java: Wrong BN provided!");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the underlying joint probability distribution obtained from the given BN.
	 * 
	 * @return	JPD
	 */
	public JointProbDistribution getJPD () {
		return jpd;
	}
	
}
