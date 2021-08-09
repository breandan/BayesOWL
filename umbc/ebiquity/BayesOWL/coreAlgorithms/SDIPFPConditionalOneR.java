/**
 * SDIPFPConditionalOneR.java
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
 * with only one step for one single local conditional constraint R(V|P1,P2,...) provided.
 *
 */
public class SDIPFPConditionalOneR {
	Net net;
	SimpleConditionalConstraint constraint;
	HardEvidence[] hardEvidences;
	
	/**
	 * Constructor.
	 * 
	 * @param n:	Bayesian Net
	 * @param r:	simple conditional constraint
	 * @param e:	hard evidence
	 */
	public SDIPFPConditionalOneR (Net n, SimpleConditionalConstraint r, HardEvidence[] e) {
		if (n == null || r == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFPConditionalOneR.java: Wrong BBN or simple conditional constraint provided!");
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
     * fitting procedure (SDIPFP), for a single simple local conditional constraint.
	 */
	public void computation () {
		try {
			net.compile();
			Node node = net.getNode(constraint.getNodeName());
			int numOfNodeStates = node.getNumStates();
			int numOfCondVars = constraint.getNumOfConds();
			String[] condVarNames = new String[numOfCondVars];
			for (int k=0; k<numOfCondVars; k++)
				condVarNames[k] = constraint.getCondNodeName(k);
			NodeList parents = node.getParents();
			if (parents.size() > 0) { //this node has some parents: Q_k(V|parents) = Q_k-1(V|parents) * R(V|P1,P2,...) / Q_k-1(V|P1,P2,...,hard evidences)
				int numOfEntries = 1;
				int[] dims_of_parents = new int[parents.size()];
				int[] locations_of_conds_in_parents = new int[numOfCondVars];
				for (int k = 0; k < parents.size(); k++) {
					Node p = (Node) parents.elementAt(k);
					dims_of_parents[k] = p.getNumStates();
					numOfEntries *= dims_of_parents[k];
					for (int t = 0; t < numOfCondVars; t++){
						if (condVarNames[t].equals(p.getName())){
							locations_of_conds_in_parents[t] = k;
							break;
						}
					}
				}							
				float[][] newTable = new float[numOfEntries][numOfNodeStates];
				int[][] pindices = new int[numOfEntries][parents.size()];
				for (int k = 0; k < numOfEntries; k++) { //iterate over all possible assignments of the parent nodes
					int[] indices_of_parents = new int[parents.size()];
					int offset = k;
					for (int t = parents.size()-1; t >= 0; --t) {
						indices_of_parents[t] = offset % dims_of_parents[t];
						offset = offset  / dims_of_parents[t];
					}								
					pindices[k] = indices_of_parents;
					if (hardEvidences != null && hardEvidences.length>0) { //enter hard evidence findings
						for (int t=0; t<hardEvidences.length; t++) {
							Node hardEviNode = net.getNode(hardEvidences[t].getName());
							hardEviNode.enterFinding(hardEvidences[t].getState());
						}
					}
					for (int t = 0; t < numOfCondVars; t++){ //enter the findings of the conditional variables
						Node condHardEviNode = net.getNode(condVarNames[t]);
						condHardEviNode.enterFinding(indices_of_parents[locations_of_conds_in_parents[t]]);
					}
					double[] pv2s = new double[numOfNodeStates];
					for (int t = 0; t < numOfNodeStates; t++)
						pv2s[t] = node.getBelief(node.state(t).getName()); //get Q_k-1(V|P1,P2,...,hard evidences)
					net.retractFindings();
					net.compile();
					double[] rs = new double[numOfNodeStates];
					int[] indices_of_conds = new int[numOfCondVars];
					for (int t = 0; t < numOfCondVars; t++)
						indices_of_conds[t] = indices_of_parents[locations_of_conds_in_parents[t]];
					for (int t = 0; t < numOfNodeStates; t++)
						rs[t] = constraint.getNodeProbValueByStateIndex(t, indices_of_conds); //get R(V|P1,P2,...)
					float sum = 0;
					float[] newTableEntry = new float[numOfNodeStates];
					for (int t = 0; t < numOfNodeStates; t++) {
						double pv1 = node.getCPTable(indices_of_parents, null)[t]; //get Q_k-1(V|parents)
						if (rs[t] == 0 || pv2s[t] == 0) {
							newTableEntry[t] = 0;
						}
						else {
							newTableEntry[t] = (float) (pv1 * rs[t] / pv2s[t]);
						}
						sum += newTableEntry[t];
					}
					for (int t = 0; t < numOfNodeStates; t++) //normalization
						newTableEntry[t] = newTableEntry[t] / sum;
					newTable[k] = newTableEntry;
				}
				for (int k = 0; k < numOfEntries; k++)
					node.setCPTable(pindices[k],newTable[k]);
				net.compile();
			}
			else { // this node has no parents
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFPConditionalOneR.java: Wrong simple conditional constraint provided!");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}
	
}
