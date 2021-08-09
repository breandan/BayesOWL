/**
 * JPD2BN.java
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
 * This class revises CPTs of a given BBN according to a JPD provided.
 *
 */
public class JPD2BN {
	Net net;
	
	/**
	 * Constructor - 1:
	 * Takes a joint probability distribution (JPD) and a BN as arguments, and revises CPTs of the BN based on the JPD.
	 * 
	 * @param jpd:	JPD
	 * @param net_orig:	Bayesian Net
	 */
	public JPD2BN (JointProbDistribution jpd, Net net_orig) {
		net = reviseCPT(jpd, net_orig);
	}
	
	/**
	 * Constructor - 2: 
	 * Takes a joint probability distribution (JPD) and a BN file name as arguments, and revises CPTs of the BN based on the JPD.
	 * 
	 * @param jpd: JPD
	 * @param fname:	BN file name
	 */
	public JPD2BN (JointProbDistribution jpd, String fname) {
		try {
			net = new Net(new Streamer(fname));
			net = reviseCPT(jpd, net);
		}
		catch (Exception e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.JPD2BN.java: Wrong BN file name provided!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Revises the CPTs of the given BBN based on the given joint probability distribution.
	 * 
	 * @param jpd: JPD
	 * @param net:	Bayesian Net
	 * @return	Bayesian net
	 */
	private Net reviseCPT (JointProbDistribution jpd, Net net) {
		Net currNet = net;
		try {
			NodeList nodes = currNet.getNodes();
			Enumeration enum_enum = nodes.elements();//enum_enum original: enum  Oct. 30 2007 Shenyong
			while (enum_enum.hasMoreElements()) {
				Node node = (Node) enum_enum.nextElement();
				int numOfStates = node.getNumStates();
				String nodeName = node.getName();
				String[] states = new String[numOfStates];
				for (int i = 0; i < numOfStates; i++) {
					states[i] = node.state(i).getName();
				}
				RandomVariable rndVar = new RandomVariable(nodeName, states);				
				NodeList parents = node.getParents();
				// start - revise CPTs
				if (parents.size() == 0) { // if this node has no parents
					JointProbDistribution marginalDist = jpd.getMarginalDist(new RandomVariable[]{rndVar});
					float[] table = new float[numOfStates];
					float sum = 0;
					for (int i = 0; i < numOfStates; i++) {
						table[i] = (float) marginalDist.getProbEntry(marginalDist.getIndices(i));
						sum += table[i];
					}
					for (int i = 0; i < numOfStates; i++) //normalization
						table[i] = table[i] / sum;
					node.setCPTable("", table);
				}
				else { // if this node has some parents
					int numOfParents = parents.size();
					RandomVariable[] rndVars_parents = new RandomVariable[numOfParents];
					Enumeration enup = parents.elements();
					int idx = 0;
					int numOfTableEntries = 1;
					int[] numOfStates_of_parents = new int[numOfParents]; //stores the number of states for each parent
					while (enup.hasMoreElements()) {
						Node p = (Node) enup.nextElement();
						int numOfParentStates = p.getNumStates();
						numOfTableEntries *= numOfParentStates;
						numOfStates_of_parents[idx] = numOfParentStates;
						String parentName = p.getName();
						String[] parentStates = new String[numOfParentStates];
						for (int i=0; i<numOfParentStates; i++) {
							parentStates[i] = p.state(i).getName();
						}
						rndVars_parents[idx] = new RandomVariable(parentName, parentStates);
						idx++;
					}
					CondProbDistribution marginalCondDist = jpd.getMarginalCondDist(new RandomVariable[]{rndVar},rndVars_parents);
					for (int i=0; i<numOfTableEntries; i++) {
						int[] parentStateIndices = new int[numOfParents];
						int[] allStateIndices = new int[numOfParents+1];
						int offset = i;
						for (int j=numOfParents-1; j>=0; --j) {
							parentStateIndices[j] = offset % numOfStates_of_parents[j];
							offset = offset  / numOfStates_of_parents[j];
							allStateIndices[j] = parentStateIndices[j];
						}
						float[] table = new float[numOfStates];
						float sum = 0;
						for (int j=0; j<numOfStates; j++) {
							allStateIndices[numOfParents] = j;
							table[j] = (float) marginalCondDist.getCondProbEntry(allStateIndices);
							sum += table[j];
						}
						for (int j = 0; j < numOfStates; j++) //normalization
							table[j] = table[j] / sum;
						node.setCPTable(parentStateIndices, table);
					}
				}
				// end - revise CPTs
				currNet.compile();
			}
		}
		catch (Exception e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.JPD2BN.java: Wrong BN provided!");
			e.printStackTrace();
		}
		return currNet;		
	}
		
	/**
	 * Saves the revised BBN obtained into a file.
	 * 
	 * @param fname: BN file name
	 */
	public void saveNet (String fname) {
		try {
			if (fname == null || fname.equals("")) {
				throw new NullPointerException("Class umbc.ebiquity.BayesOWL.commonMethod.JPD2BN.java: Please give a valid *.dne file name to save with!");
			}
			else {
				net.setAutoUpdate(1);
				net.write(new Streamer(fname));
				net.finalize();   // not strictly necessary, but a good habit
			}
		}
		catch (Exception e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.JPD2BN.java: Error in saving the BN to file!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}

}
