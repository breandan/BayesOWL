/**
 * SDIPFPMarginalOneR.java
 * 
 * @author Zhongli Ding (original) 
 * 
 * Created on Mar. 03, 2005
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import umbc.ebiquity.BayesOWL.commonDefine.*;

import norsys.netica.*;

/**
 * This class implements the 'Simplified D-IPFP' algorithm based on the AISTA-2004 paper, 
 * with only one step for one single simple local marginal constraint R(V) provided.
 *
 */
public class SDIPFPMarginalOneR {

	Net net;
	SimpleMarginalConstraint constraint;
	HardEvidence[] hardEvidences;
	
	/**
	 * Constructor.
	 * 
	 * @param n:	Bayesian Net
	 * @param r:	simple marginal constraint
	 * @param e:	hard evidence
	 */
	public SDIPFPMarginalOneR (Net n, SimpleMarginalConstraint r, HardEvidence[] e) {
		if (n == null || r == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFPMarginalOneR.java: Wrong BBN or simple marginal constraint provided!");
		}
		else {
			net = n;
			constraint = r;
			if (e != null && e.length>0) {
				hardEvidences = new HardEvidence[e.length];
				for (int i=0; i<e.length; i++) 
					hardEvidences[i] = e[i];
			}
		}
	}
	
	/**
	 * The computation process of one-step simplified de-composed iterative proportional 
	 * fitting procedure (SDIPFP), for a single simple local marginal constraint.
	 */
	public void computation () {
		try {
			net.compile();
			Node node = net.getNode(constraint.getNodeName());
			int numOfNodeStates = node.getNumStates();
			NodeList parents = node.getParents();
			if (parents.size() > 0) { //this node has some parents: Q_k(V|parents) = Q_k-1(V|parents) * R(V) / Q_k-1(V|hard evidences)
				int numOfEntries = 1;
				int[] dims_of_parents = new int[parents.size()];
				for (int i = 0; i < parents.size(); i++) {
					Node p = (Node) parents.elementAt(i);
					dims_of_parents[i] = p.getNumStates();
					numOfEntries *= dims_of_parents[i];
				}
				if (hardEvidences != null && hardEvidences.length > 0) { //enter hard evidence findings
					for (int i = 0; i < hardEvidences.length; i++) {
						Node hardEviNode = net.getNode(hardEvidences[i].getName());
						hardEviNode.enterFinding(hardEvidences[i].getState());
					}
				}
				double[] pv2s = new double[numOfNodeStates];
				for (int i = 0; i < numOfNodeStates; i++)
					pv2s[i] = node.getBelief(node.state(i).getName()); //get Q_k-1(V|hard evidences)
				net.retractFindings();
				net.compile();
				double[] rs = new double[numOfNodeStates];
				for (int i = 0; i < numOfNodeStates; i++)
					rs[i] = constraint.getNodeProbValueByStateName(node.state(i).getName()); //get R(V)
				for (int i = 0; i < numOfEntries; i++) { //iterate over all possible assignments of the parent nodes
					int[] indices_of_parents = new int[parents.size()];
					int offset = i;
					for (int t=parents.size()-1; t>=0; --t) {
						indices_of_parents[t] = offset % dims_of_parents[t];
						offset = offset  / dims_of_parents[t];
					}
					float sum = 0;
					float[] newTable = new float[numOfNodeStates];
					for (int t=0; t<numOfNodeStates; t++) {
						double pv1 = node.getCPTable(indices_of_parents,null)[t]; //get Q_k-1(V|parents)
						if (rs[t] ==0 || pv2s[t]==0) {
							newTable[t] = 0;
						}
						else {
							newTable[t] = (float) (pv1 * rs[t] / pv2s[t]);
						}
						sum+=newTable[t];
					}
					for (int t=0; t<numOfNodeStates; t++) //normalization
						newTable[t] = newTable[t]/sum;
					node.setCPTable(indices_of_parents,newTable);
				}
				net.compile();
			}
			else { // this node has no parents: Q_k(V) = Q_k-1(V) * R(V) / Q_k-1(V|hard evidences)
				float sum = 0;
				float[] newTable = new float[numOfNodeStates];
				if (hardEvidences != null && hardEvidences.length>0) { //enter hard evidence findings
					for (int i = 0; i < hardEvidences.length; i++) {
						Node hardEviNode = net.getNode(hardEvidences[i].getName());
						hardEviNode.enterFinding(hardEvidences[i].getState());
					}
				}
				for (int i = 0; i < numOfNodeStates; i++) {
					double pv1 = node.getCPTable("",null)[i];    //get Q_k-1(V)
					double pv2 = node.getBelief(node.state(i).getName()); // get Q_k-1(V|hard evidences)
					double r = constraint.getNodeProbValueByStateName(node.state(i).getName()); //get R(V)
					if (r == 0 || pv2 == 0) {
						newTable[i] = 0; 
					}
					else {
						newTable[i] = (float) (pv1 * r / pv2);
					}
					sum += newTable[i];
				}
				net.retractFindings();
				for (int i = 0; i < numOfNodeStates; i++) //normalization
					newTable[i] = newTable[i] / sum;
				node.setCPTable("", newTable);
				net.compile();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the revised BBN obtained.
	 * 
	 * @return	Bayesian net
	 */
	public Net getNet () {
		return net;
	}

}