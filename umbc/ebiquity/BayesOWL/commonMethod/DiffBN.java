/**
 * DiffBN.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.commonMethod;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;



import norsys.netica.*;

/**
 * This class implements a method to compare two Bayesian networks (same DAG, different CPT) node by node, 
 * and returns the sum of the 'absolute' difference value between each pair of nodes' posterior 
 * probabilities (may be in the case that a set of hard evidences are specified first). 
 *
 */
public class DiffBN {
	double diff_sum;
	double totalVariance;
	
	/**
	 * Constructor. 
	 * Assume 'net1' and 'net2' provided have the same DAG, but different CPTs.
	 * 
	 * @param net1:	Bayesian Net
	 * @param net2:	Bayesian Net
	 * @param hardEvidences:	hard evidence
	 */
	public DiffBN(Net net1, Net net2, HardEvidence[] hardEvidences) {
		if (net1 == null || net2 == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.DiffBN.java: Wrong BBNs provided!");
		}
		else {
			diff_sum = sumOfDifference(net1, net2, hardEvidences);
			totalVariance = computeTotalVariance(net1,net2);
		}
	}
	
	/**
	 * This method compares two Bayesian networks (same DAG, different CPT) node by node, 
	 * and returns the sum of the 'absolute' difference value between each pair of nodes' posterior 
	 * probabilities (may be in the case that a set of hard evidences are specified first).
	 * 
	 * @param net1: Bayesian Net
	 * @param net2:	Bayesian Net
	 * @param hardEvidences:	hard evidence
	 * @return	absolute difference value
	 */
	private double sumOfDifference (Net net1, Net net2, HardEvidence[] hardEvidences) {		
		double sum_diff = 0.0;
		try {
			net1.compile();
			net1.setAutoUpdate(1);
			net2.compile();
			net2.setAutoUpdate(1);
			if (hardEvidences != null && hardEvidences.length>0) { //enter hard evidence findings
				for (int i=0; i<hardEvidences.length; i++) {
					Node hardEviNode1 = net1.getNode(hardEvidences[i].getName());
					hardEviNode1.enterFinding(hardEvidences[i].getState());
					Node hardEviNode2 = net2.getNode(hardEvidences[i].getName());
					hardEviNode2.enterFinding(hardEvidences[i].getState());					
				}
			}
			NodeList list1 = net1.getNodes();
			int numOfNodes = list1.size();
			for (int i=0; i<numOfNodes; i++){
				Node node1 = (Node)list1.elementAt(i);
				String nodeName = node1.getName();
				boolean isHardEviNode = false;
				if (hardEvidences != null && hardEvidences.length>0) {
					for (int j=0; j<hardEvidences.length; j++) {
						if (nodeName.equals(hardEvidences[j].getName())){
							isHardEviNode = true;
							break;
						}
					}
				}
				if (!isHardEviNode) {
					Node node2 = net2.getNode(nodeName);
					int numOfStates = node1.getNumStates();
					double diff_over_all_states = 0.0;
					for (int j=0; j<numOfStates; j++) {
						double v1 = node1.getBelief(node1.state(j).getName());
						double v2 = node2.getBelief(node2.state(j).getName());
						diff_over_all_states += Math.abs(v1-v2);
					}
					diff_over_all_states = diff_over_all_states/numOfStates;
					sum_diff += diff_over_all_states;
				}
			}
			net1.retractFindings();
			net2.retractFindings();
			net1.compile();
			net2.compile();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sum_diff;
	}
	
	/**
	 * This method computes the total variance about the JPDs of the two Bayesian networks (same DAG, different CPT).
	 * 
	 * @param net1:	Bayesian net
	 * @param net2:	Bayesian net
	 * @return	total variance
	 */	
	private double computeTotalVariance(Net net1, Net net2) {
		double tv = 0.0;
		try {
			net1.compile();
			net2.compile();
			NodeList nodes1 = net1.getNodes();
			NodeList nodes2 = net2.getNodes();
			RandomVariable[] rndVars1 = new RandomVariable[nodes1.size()];
			int idx = 0;
			int numOfProbEntries = 1;
			Enumeration enum1 = nodes1.elements();
			while (enum1.hasMoreElements()) {
				Node node1 = (Node) enum1.nextElement();
				int numOfStates1 = node1.getNumStates();
				numOfProbEntries *= numOfStates1;
				String nodeName1 = node1.getName();
				String[] states1 = new String[numOfStates1];
				for (int i=0; i<numOfStates1; i++) {
					states1[i] = node1.state(i).getName();
				}
				RandomVariable rndVar1 = new RandomVariable(nodeName1,states1);
				rndVars1[idx] = rndVar1;				
				idx++;
			}
			JointProbDistribution jpd1 = new JointProbDistribution(rndVars1);
			for (int i=0; i<numOfProbEntries; i++) {
				int[] indices = jpd1.getIndices(i);
				double pv1 = net1.getJointProbability(nodes1,indices);
				double pv2 = net2.getJointProbability(nodes2,indices);
				tv += Math.abs(pv1 - pv2);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		return tv;
	}
	
	/**
	 * Returns the sum of differences about two nodes' posterior probabilities.
	 * 
	 * @return	differences sum
	 */
	public double getDifference() {
		return diff_sum;
	}

	/**
	 * Returns the total variance about the JPDs of the two BNs. 
	 * 
	 * @return	total variance
	 */
	public double getTotalVariance() {
		return totalVariance;
	}

}