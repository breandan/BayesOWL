/**
 * DIPFPConditionalOneR.java
 * 
 * @author Zhongli Ding (original)
 *
 * Created on Oct 19, 2005, v0.4
 * Modified on Aug. 11, 2008
 * 
 * See paper:
 *  A Bayesian Approach to Uncertainty Modeling in OWL Ontology
 *  Zhongli Ding, Yun Peng, Ron Pan
 *  AISTA 2004
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;



import norsys.netica.*;

/**
 * This class implements the 'D-IPFP' algorithm based on the AISTA-2004 paper,<br> 
 * to process only one conditional constraint with the form 'R(A|B)', 'A={C1,C2,...,Cn}', 'B={P1,P2,...,Pm}', 'A' and 'B' are disjoint.<br>
 * <br>
 * Eight(8) variations of implementation are provided, for experimental and analytical purpose.<br>
 * <br>
 * Conditional Constraint provided might be either: (C, C1, ..., Cn, P1, ..., Pm are variables)<br>
 * (1) Local: R(C|L) and L is a non-empty subset of Pi(C);<br>
 * (2) Non-Local: R(C1,C2,...,Cn|P1, P2, ..., Pm), n>=2, {C1, C2, ..., Cn} and {P1, P2, ..., Pm} are disjoint.<br>
 * <br>
 * Variation 1:<br> 
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C|L) / Q_(k-1)(C|L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=1, m>=1)<br>
 * 				Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj} and it's loose closure S={Pi(Yj)}\Y (j=1 to m+n),<br>
 * 				Q_(k)(Y,S) = Q_(k-1)(Y,S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 				Getting Q_(k)(Yj|Pi(Yj)) from Q_(k)(Y,S) for each Yj in Y and updating its CPT (j=1 to m+n).<br>
 * <br>
 * Variation 2:<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C|L) / Q_(k-1)(C|L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=1, m>=1)<br>
 * 				Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj} and it's loose closure S={Pi(Yj)}\Y (j=1 to m+n),<br>
 * 				Do until converge { //Q_(k)(Y,S) ~= Q_(k)'(Y,S)<br>
 * 					Q_(k)'(Y,S) = Q_(k-1)(Y,S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 					Getting Q_(k)(Yj|Pi(Yj)) from Q_(k)'(Y,S) for each Yj in Y and updating its CPT (j=1 to m+n).<br>
 * 				}<br>
 *<br> 
 * Variation 3:<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C|L) / Q_(k-1)(C|L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=1, m>=1)<br>
 * 				Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj}, it's tight closure S, and a updated variable set Y', please refer to 'RetrieveTightClosure.java',<br>
 * 				Q_(k)(Y',S) = Q_(k-1)(Y',S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 				Getting Q_(k)(Y'j|Pi(Y'j)) from Q_(k)(Y',S) for each Y'j in Y' and updating its CPT (|Y'|>=(m+n)).<br>
 *<br> 
 * Variation 4:<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C|L) / Q_(k-1)(C|L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=1, m>=1)<br>
 * 				Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj}, it's tight closure S, and a updated variable set Y', please refer to 'RetrieveTightClosure.java',<br>
 * 				Do until converge { //Q_(k)(Y',S) ~= Q_(k)'(Y',S)<br>
 * 					Q_(k)'(Y',S) = Q_(k-1)(Y',S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 					Getting Q_(k)(Y'j|Pi(Y'j)) from Q_(k)'(Y',S) for each Y'j in Y' and updating its CPT (|Y'|>=(m+n)).<br>
 *				}<br> 
 *<br> 
 * Variation 5:<br>
 * 		(1) Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj} and it's loose closure S={Pi(Yj)}\Y (j=1 to m+n);<br>
 * 		(2) Q_(k)(Y,S) = Q_(k-1)(Y,S) * R(A|B) / Q_(k-1)(A|B);<br>
 * 		(3) Getting Q_(k)(Yj|Pi(Yj)) from Q_(k)(Y,S) for each Yj in Y and updating its CPT (j=1 to m+n).<br>
 *<br> 
 * Variation 6:<br>
 * 		(1) Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj} and it's loose closure S={Pi(Yj)}\Y (j=1 to m+n);<br>
 * 		(2) Do until converge { //Q_(k)(Y,S) ~= Q_(k)'(Y,S)<br>
 * 				Q_(k)'(Y,S) = Q_(k-1)(Y,S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 				Getting Q_(k)(Yj|Pi(Yj)) from Q_(k)'(Y,S) for each Yj in Y and updating its CPT (j=1 to m+n).<br>
 * 			}<br>
 *<br> 
 * Variation 7:<br>
 * 		(1) Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj}, it's tight closure S, and a updated variable set Y', please refer to 'RetrieveTightClosure.java';<br>
 * 		(2) Q_(k)(Y',S) = Q_(k-1)(Y',S) * R(A|B) / Q_(k-1)(A|B);<br>
 * 		(3) Getting Q_(k)(Y'j|Pi(Y'j)) from Q_(k)(Y',S) for each Y'j in Y' and updating its CPT (|Y'|>=(m+n)).<br>
 *<br> 
 * Variation 8:<br>
 * 		(1) Getting Y={C1,C2,...,Cn,P1,P2,...,Pm}={Yj}, it's tight closure S, and a updated variable set Y', please refer to 'RetrieveTightClosure.java';<br>
 * 		(2) Do until converge { //Q_(k)(Y',S) ~= Q_(k)'(Y',S)<br>
 * 				Q_(k)'(Y',S) = Q_(k-1)(Y',S) * R(A|B) / Q_(k-1)(A|B),<br>
 * 				Getting Q_(k)(Y'j|Pi(Y'j)) from Q_(k)'(Y',S) for each Y'j in Y' and updating its CPT (|Y'|>=(m+n)).<br>
 *			}<br> 
 *<br>
 */
public class DIPFPConditionalOneR {
	Net net;
	ConditionalConstraint constraint;
	int variation;
	long timeElapsed;
	
	/**
	 * Constructor.
	 * 
	 * @param bbn	Bayesian Net
	 * @param r	conditional constraint
	 * @param choice	implementation #
	 */
	public DIPFPConditionalOneR (Net bbn, ConditionalConstraint r, int choice) {
		if (bbn == null || r == null || choice > 8 || choice < 1) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java: Wrong BBN or Conditional Constraint or Implementation Choice provided!");
		}
		else {
			net = bbn;
			constraint = r;
			variation = choice;
			timeElapsed = 0;
		}
	}
		
	/**
	 * The computation process of one-step de-composed iterative proportional 
     * fitting procedure (DIPFP), for a single conditional constraint, either local or non-local.
	 */
	public void computation () {
		switch (variation) {
			case 1: variation1(); break;
			case 2: variation2(); break;
			case 3: variation3(); break;
			case 4: variation4(); break;
			case 5: variation5(); break;
			case 6: variation6(); break;
			case 7: variation7(); break;
			case 8: variation8(); break;
			default: variation2(); break;
		}
	}

	/**
	 * Implementation Variation 1.
	 */
	private void variation1() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();			
			net.compile();
			String scopeType = constraint.getScopeType();
			//local conditional constraint R(C|L)
			if (scopeType.equals("local")) { 
				LocalConditionalConstraint localConstraint = (LocalConditionalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(C|L) / Q_(k-1)(C|L)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation1()): Wrong local conditional constraint provided!");										
					}
					//getting information about C's parent nodes
					int numOfEntries = 1;
					int[] dims_of_parents = new int[parents.size()];
					String[] parentNames = new String[parents.size()];
					for (int i=0; i<parents.size(); i++) {
						Node p = (Node) parents.elementAt(i);
						parentNames[i] = p.getName();
						dims_of_parents[i] = p.getNumStates();
						numOfEntries *= dims_of_parents[i];
					}
					//getting information about L's nodes and it's relation to Pi(C)
					String[] LNames = localConstraint.getCondVariableNames();
					int[] locs_in_parents = new int[numOfParentsInvolved];
					for (int i=0; i<numOfParentsInvolved; i++) {
						boolean isFound = false;
						for (int j=0; j<parents.size(); j++) {
							if (LNames[i].equals(parentNames[j])) {
								locs_in_parents[i] = j;
								isFound = true;
								break;
							}
						}
						if (!isFound) //note that L is a non-empty subset of Pi(C)
							throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation1()): Wrong local conditional constraint provided!");																		
					}
					//Getting Q_(k-1)(C|L), in the same storage order as R(C|L)
					//Assume that the order of the states are the same for every node across R(C|L) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					CondProbDistribution cpdOfCL = new CondProbDistribution(localConstraint.getDistribution());
					NodeList nodeListCL = new NodeList(net);
					NodeList nodeListL = new NodeList(net);
					for (int i=0; i<numOfParentsInvolved; i++) {
						nodeListL.addElement(net.getNode(LNames[i]));
						nodeListCL.addElement(net.getNode(LNames[i]));
					}
					nodeListCL.addElement(node);
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesOfCL = localConstraint.getDistribution().getIndices(i);
						int[] indicesOfL = new int[numOfParentsInvolved];
						for (int j=0; j<numOfParentsInvolved; j++)
							indicesOfL[j] = indicesOfCL[j];
						double v1 = net.getJointProbability(nodeListCL,indicesOfCL);
						double v2 = net.getJointProbability(nodeListL, indicesOfL);
						double v = 0;
						if (v1>0)
							v = v1/v2; //note that if v1>0, then v2>0 too
						cpdOfCL.addCondProbEntry(indicesOfCL, v);
					}
					
					//computing, normalizing, and updating C's CPT entries 
					for (int i=0; i<numOfEntries; i++) { //iterate over all possible assignments of the parent nodes
						int[] indices_of_parents = new int[parents.size()];
						int offset = i;
						for (int t=parents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_parents[t];
							offset = offset  / dims_of_parents[t];
						}
						float sum = 0;
						float[] newTable = new float[numOfNodeStates];
						int[] stateIdxOfCLVars = new int[numOfParentsInvolved+1];
						for (int t=0; t<numOfParentsInvolved; t++) {
							stateIdxOfCLVars[t] = indices_of_parents[locs_in_parents[t]];
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfCLVars[numOfParentsInvolved] = t;	
							v2 = localConstraint.getDistribution().getCondProbEntry(stateIdxOfCLVars); //fetching R(C|L)
							v3 = cpdOfCL.getCondProbEntry(stateIdxOfCLVars); //fetching Q_(k-1)(C|L)	
							if (v2==0 || v3==0) {
								newTable[t] = 0;
							}
							else {
								newTable[t] = (float) (v1 * v2 / v3);
							}
							sum+=newTable[t];							
						}						
						for (int t=0; t<numOfNodeStates; t++) //normalization
							newTable[t] = newTable[t]/sum;
						node.setCPTable(indices_of_parents,newTable);
					}
					net.compile();
				}
				//when C has no parents: |L|=0, wrong case
				else { 
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation1()): Wrong local conditional constraint provided!");
				}								
			}
			//non-local conditional constraint: R(A|B), |A|>=1, |B|>=1
			else if (scopeType.equals("nonlocal")) { 
				//getting Y and S
				String[] YNames = constraint.getVariableNames();
				int numOfYVars = constraint.getNumOfVariables();
				RetrieveLooseClosure rlc = new RetrieveLooseClosure(YNames,net);
				int numOfCVars = rlc.getClosureSize();
				String[] closureNames;
				if (numOfCVars >0) {
					closureNames = new String[numOfCVars];
					Enumeration enu = rlc.getLooseClosure();
					int i = 0;
					while (enu.hasMoreElements()) {
						closureNames[i] = (String)enu.nextElement();
						i++;
					}
				}
				else {
					closureNames = null;
				}
				RandomVariable[] YVars = new RandomVariable[numOfYVars];
				RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
				NodeList nodeListY = new NodeList(net);
				NodeList nodeListYS = new NodeList(net);
				for (int i=0; i<numOfYVars; i++) {
					Node node = net.getNode(YNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(YNames[i],stateNames);
					YVars[i] = rv;
					YSVars[i] = rv;
					nodeListY.addElement(node);
					nodeListYS.addElement(node);
				}
				if (numOfCVars > 0) {
					for (int i=0; i<numOfCVars; i++) {
						Node node = net.getNode(closureNames[i]);
						int nodeNumStates = node.getNumStates();
						String[] stateNames = new String[nodeNumStates];
						for (int j=0; j<nodeNumStates; j++)
							stateNames[j] = node.state(j).getName();
						RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
						YSVars[numOfYVars+i] = rv;
						nodeListYS.addElement(node);
					}
				}
				//getting Q_(k-1)(Y,S)
				JointProbDistribution jpdOfYS = new JointProbDistribution(YSVars);
				int numOfYSEntries = jpdOfYS.getNumOfEntries();
				for (int i=0; i<numOfYSEntries; i++) {
					jpdOfYS.addProbEntry(jpdOfYS.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS.getIndices(i)));
				}
				//calling CIPFPOneR.java to get Q_(k)(Y,S)
				CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
				algo.computation();
				jpdOfYS = algo.getDistribution();
				//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y, updating the CPT; note that Pi(Cj) might be empty
				for (int i=0; i<numOfYVars; i++) {
					/* getting the distribution for CPT */
					RandomVariable rv = YVars[i];
					Node node = net.getNode(YNames[i]);
					NodeList nodeParents = node.getParents();
					if (nodeParents.size() > 0) { //|Pi(Cj)|>0					
						RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
						int[] dims_of_pvs = new int[nodeParents.size()];
						int numOfParentEntries = 1;
						for (int j=0; j<nodeParents.size(); j++) {
							Node pnode = (Node)nodeParents.elementAt(j);
							int pnodeNumStates = pnode.getNumStates();
							String[] pnodeStateNames = new String[pnodeNumStates];
							for (int k=0; k<pnodeNumStates; k++)
								pnodeStateNames[k] = pnode.state(k).getName();
							RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
							pvs[j] = prv;
							dims_of_pvs[j] = pnodeNumStates;
							numOfParentEntries *= dims_of_pvs[j];
						} //end-for-j
						CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
						/* updating the CPT */
						for (int j=0; j<numOfParentEntries; j++) {
							int[] indices_of_parents = new int[nodeParents.size()];
							int[] indices_in_CPTDist = new int[nodeParents.size()+1];
							int offset = j;
							for (int t=nodeParents.size()-1; t>=0; --t) {
								indices_of_parents[t] = offset % dims_of_pvs[t];
								offset = offset  / dims_of_pvs[t];
								indices_in_CPTDist[t] = indices_of_parents[t];
							}
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t=0; t<rv.getNumOfStates(); t++) {
								indices_in_CPTDist[nodeParents.size()] = t;
								newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
								sum += newTable[t];
							}
							for (int t=0; t<rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;
							node.setCPTable(indices_of_parents,newTable);
						} //end-for-j					
					}
					else { //|Pi(Cj)|=0
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t=0; t<rv.getNumOfStates(); t++) {
							newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
							sum += newTable[t];
						}	
						for (int t=0; t<rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t]/sum;						
						node.setCPTable("",newTable);
					}					
					net.compile();
				}//end-for-i
				net.compile();				
			}
			else { //otherwise
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation1()): Wrong conditional constraint provided!");				
			}
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;					
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Implementation Variation 2.
	 */
	private void variation2() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			String scopeType = constraint.getScopeType();
			//local conditional constraint R(C|L)
			if (scopeType.equals("local")) { 
				LocalConditionalConstraint localConstraint = (LocalConditionalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(C|L) / Q_(k-1)(C|L)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation2()): Wrong local conditional constraint provided!");										
					}
					//getting information about C's parent nodes
					int numOfEntries = 1;
					int[] dims_of_parents = new int[parents.size()];
					String[] parentNames = new String[parents.size()];
					for (int i=0; i<parents.size(); i++) {
						Node p = (Node) parents.elementAt(i);
						parentNames[i] = p.getName();
						dims_of_parents[i] = p.getNumStates();
						numOfEntries *= dims_of_parents[i];
					}
					//getting information about L's nodes and it's relation to Pi(C)
					String[] LNames = localConstraint.getCondVariableNames();
					int[] locs_in_parents = new int[numOfParentsInvolved];
					for (int i=0; i<numOfParentsInvolved; i++) {
						boolean isFound = false;
						for (int j=0; j<parents.size(); j++) {
							if (LNames[i].equals(parentNames[j])) {
								locs_in_parents[i] = j;
								isFound = true;
								break;
							}
						}
						if (!isFound) //note that L is a non-empty subset of Pi(C)
							throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation2()): Wrong local conditional constraint provided!");																		
					}
					//Getting Q_(k-1)(C|L), in the same storage order as R(C|L)
					//Assume that the order of the states are the same for every node across R(C|L) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					CondProbDistribution cpdOfCL = new CondProbDistribution(localConstraint.getDistribution());
					NodeList nodeListCL = new NodeList(net);
					NodeList nodeListL = new NodeList(net);
					for (int i=0; i<numOfParentsInvolved; i++) {
						nodeListL.addElement(net.getNode(LNames[i]));
						nodeListCL.addElement(net.getNode(LNames[i]));
					}
					nodeListCL.addElement(node);
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesOfCL = localConstraint.getDistribution().getIndices(i);
						int[] indicesOfL = new int[numOfParentsInvolved];
						for (int j=0; j<numOfParentsInvolved; j++)
							indicesOfL[j] = indicesOfCL[j];
						double v1 = net.getJointProbability(nodeListCL,indicesOfCL);
						double v2 = net.getJointProbability(nodeListL, indicesOfL);
						double v = 0;
						if (v1>0)
							v = v1/v2; //note that if v1>0, then v2>0 too
						cpdOfCL.addCondProbEntry(indicesOfCL, v);
					}
					
					//computing, normalizing, and updating C's CPT entries 
					for (int i=0; i<numOfEntries; i++) { //iterate over all possible assignments of the parent nodes
						int[] indices_of_parents = new int[parents.size()];
						int offset = i;
						for (int t=parents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_parents[t];
							offset = offset  / dims_of_parents[t];
						}
						float sum = 0;
						float[] newTable = new float[numOfNodeStates];
						int[] stateIdxOfCLVars = new int[numOfParentsInvolved+1];
						for (int t=0; t<numOfParentsInvolved; t++) {
							stateIdxOfCLVars[t] = indices_of_parents[locs_in_parents[t]];
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfCLVars[numOfParentsInvolved] = t;	
							v2 = localConstraint.getDistribution().getCondProbEntry(stateIdxOfCLVars); //fetching R(C|L)
							v3 = cpdOfCL.getCondProbEntry(stateIdxOfCLVars); //fetching Q_(k-1)(C|L)	
							if (v2==0 || v3==0) {
								newTable[t] = 0;
							}
							else {
								newTable[t] = (float) (v1 * v2 / v3);
							}
							sum+=newTable[t];							
						}						
						for (int t=0; t<numOfNodeStates; t++) //normalization
							newTable[t] = newTable[t]/sum;
						node.setCPTable(indices_of_parents,newTable);
					}
					net.compile();
				}
				//when C has no parents: |L|=0, wrong case
				else { 
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation1()): Wrong local conditional constraint provided!");
				}								
			}
			//non-local conditional constraint: R(A|B), |A|>=1, |B|>=1
			else if (scopeType.equals("nonlocal")) {
				//getting Y and S
				String[] YNames = constraint.getVariableNames();
				int numOfYVars = constraint.getNumOfVariables();
				RetrieveLooseClosure rlc = new RetrieveLooseClosure(YNames,net);
				int numOfCVars = rlc.getClosureSize();
				String[] closureNames;
				if (numOfCVars >0) {
					closureNames = new String[numOfCVars];
					Enumeration enu = rlc.getLooseClosure();
					int i = 0;
					while (enu.hasMoreElements()) {
						closureNames[i] = (String)enu.nextElement();
						i++;
					}
				}
				else {
					closureNames = null;
				}
				RandomVariable[] YVars = new RandomVariable[numOfYVars];
				RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
				NodeList nodeListY = new NodeList(net);
				NodeList nodeListYS = new NodeList(net);
				for (int i=0; i<numOfYVars; i++) {
					Node node = net.getNode(YNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(YNames[i],stateNames);
					YVars[i] = rv;
					YSVars[i] = rv;
					nodeListY.addElement(node);
					nodeListYS.addElement(node);
				}
				if (numOfCVars > 0) {
					for (int i=0; i<numOfCVars; i++) {
						Node node = net.getNode(closureNames[i]);
						int nodeNumStates = node.getNumStates();
						String[] stateNames = new String[nodeNumStates];
						for (int j=0; j<nodeNumStates; j++)
							stateNames[j] = node.state(j).getName();
						RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
						YSVars[numOfYVars+i] = rv;
						nodeListYS.addElement(node);
					}
				}
				//do-until-converge
				JointProbDistribution jpdOfYS_old = new JointProbDistribution(YSVars);
				int numOfYSEntries = jpdOfYS_old.getNumOfEntries();
				for (int i=0; i<numOfYSEntries; i++) {
					jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
				}
				double totalVariance = 0;
				do {
					//getting Q_(k-1)(Y,S)
					JointProbDistribution jpdOfYS = new JointProbDistribution(jpdOfYS_old);
					//calling IPFPOneR.java to get Q_(k)'(Y,S)
					CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
					algo.computation();
					jpdOfYS = algo.getDistribution();
					//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y, updating the CPT; note that Pi(Cj) might be empty
					for (int i=0; i<numOfYVars; i++) {
						/* getting the distribution for CPT */
						RandomVariable rv = YVars[i];
						Node node = net.getNode(YNames[i]);
						NodeList nodeParents = node.getParents();
						if (nodeParents.size() > 0) { //|Pi(Cj)|>0
							RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
							int[] dims_of_pvs = new int[nodeParents.size()];
							int numOfParentEntries = 1;
							for (int j=0; j<nodeParents.size(); j++) {
								Node pnode = (Node)nodeParents.elementAt(j);
								int pnodeNumStates = pnode.getNumStates();
								String[] pnodeStateNames = new String[pnodeNumStates];
								for (int k=0; k<pnodeNumStates; k++)
									pnodeStateNames[k] = pnode.state(k).getName();
								RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
								pvs[j] = prv;
								dims_of_pvs[j] = pnodeNumStates;
								numOfParentEntries *= dims_of_pvs[j];
							} //end-for-j
							CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
							/* updating the CPT */
							for (int j=0; j<numOfParentEntries; j++) {
								int[] indices_of_parents = new int[nodeParents.size()];
								int[] indices_in_CPTDist = new int[nodeParents.size()+1];
								int offset = j;
								for (int t=nodeParents.size()-1; t>=0; --t) {
									indices_of_parents[t] = offset % dims_of_pvs[t];
									offset = offset  / dims_of_pvs[t];
									indices_in_CPTDist[t] = indices_of_parents[t];
								}
								float[] newTable = new float[rv.getNumOfStates()];
								float sum = 0;
								for (int t=0; t<rv.getNumOfStates(); t++) {
									indices_in_CPTDist[nodeParents.size()] = t;
									newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
									sum += newTable[t];
								}
								for (int t=0; t<rv.getNumOfStates(); t++) //normalization
									newTable[t] = newTable[t]/sum;								
								node.setCPTable(indices_of_parents,newTable);
							} //end-for-j					
						}
						else { //|Pi(Cj)|=0
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t=0; t<rv.getNumOfStates(); t++) {
								newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
								sum += newTable[t];
							}
							for (int t=0; t<rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;							
							node.setCPTable("",newTable);
						}		
						net.compile();
					}//end-for-i
					net.compile();
					//getting Q_(k)(Y,S)
					for (int i=0; i<numOfYSEntries; i++) {
						jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
					}
					TotalVariance obj = new TotalVariance(jpdOfYS, jpdOfYS_old);
					totalVariance = obj.getTotalVariance();		
				}
				while (totalVariance > 0.005);
			}
			else { //otherwise
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation2()): Wrong conditional constraint provided!");				
			}
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Implementation Variation 3.
	 */
	private void variation3() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			String scopeType = constraint.getScopeType();
			//local conditional constraint R(C|L)
			if (scopeType.equals("local")) { 
				LocalConditionalConstraint localConstraint = (LocalConditionalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(C|L) / Q_(k-1)(C|L)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation3()): Wrong local conditional constraint provided!");										
					}
					//getting information about C's parent nodes
					int numOfEntries = 1;
					int[] dims_of_parents = new int[parents.size()];
					String[] parentNames = new String[parents.size()];
					for (int i=0; i<parents.size(); i++) {
						Node p = (Node) parents.elementAt(i);
						parentNames[i] = p.getName();
						dims_of_parents[i] = p.getNumStates();
						numOfEntries *= dims_of_parents[i];
					}
					//getting information about L's nodes and it's relation to Pi(C)
					String[] LNames = localConstraint.getCondVariableNames();
					int[] locs_in_parents = new int[numOfParentsInvolved];
					for (int i=0; i<numOfParentsInvolved; i++) {
						boolean isFound = false;
						for (int j=0; j<parents.size(); j++) {
							if (LNames[i].equals(parentNames[j])) {
								locs_in_parents[i] = j;
								isFound = true;
								break;
							}
						}
						if (!isFound) //note that L is a non-empty subset of Pi(C)
							throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation3()): Wrong local conditional constraint provided!");																		
					}
					//Getting Q_(k-1)(C|L), in the same storage order as R(C|L)
					//Assume that the order of the states are the same for every node across R(C|L) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					CondProbDistribution cpdOfCL = new CondProbDistribution(localConstraint.getDistribution());
					NodeList nodeListCL = new NodeList(net);
					NodeList nodeListL = new NodeList(net);
					for (int i=0; i<numOfParentsInvolved; i++) {
						nodeListL.addElement(net.getNode(LNames[i]));
						nodeListCL.addElement(net.getNode(LNames[i]));
					}
					nodeListCL.addElement(node);
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesOfCL = localConstraint.getDistribution().getIndices(i);
						int[] indicesOfL = new int[numOfParentsInvolved];
						for (int j=0; j<numOfParentsInvolved; j++)
							indicesOfL[j] = indicesOfCL[j];
						double v1 = net.getJointProbability(nodeListCL,indicesOfCL);
						double v2 = net.getJointProbability(nodeListL, indicesOfL);
						double v = 0;
						if (v1>0)
							v = v1/v2; //note that if v1>0, then v2>0 too
						cpdOfCL.addCondProbEntry(indicesOfCL, v);
					}
					//computing, normalizing, and updating C's CPT entries 
					for (int i=0; i<numOfEntries; i++) { //iterate over all possible assignments of the parent nodes
						int[] indices_of_parents = new int[parents.size()];
						int offset = i;
						for (int t=parents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_parents[t];
							offset = offset  / dims_of_parents[t];
						}
						float sum = 0;
						float[] newTable = new float[numOfNodeStates];
						int[] stateIdxOfCLVars = new int[numOfParentsInvolved+1];
						for (int t=0; t<numOfParentsInvolved; t++) {
							stateIdxOfCLVars[t] = indices_of_parents[locs_in_parents[t]];
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfCLVars[numOfParentsInvolved] = t;	
							v2 = localConstraint.getDistribution().getCondProbEntry(stateIdxOfCLVars); //fetching R(C|L)
							v3 = cpdOfCL.getCondProbEntry(stateIdxOfCLVars); //fetching Q_(k-1)(C|L)	
							if (v2==0 || v3==0) {
								newTable[t] = 0;
							}
							else {
								newTable[t] = (float) (v1 * v2 / v3);
							}
							sum+=newTable[t];							
						}						
						for (int t=0; t<numOfNodeStates; t++) //normalization
							newTable[t] = newTable[t]/sum;
						node.setCPTable(indices_of_parents,newTable);
					}
					net.compile();
				}
				//when C has no parents: |L|=0, wrong case
				else { 
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation3()): Wrong local conditional constraint provided!");
				}								
			}
			//non-local conditional constraint: R(A|B), |A|>=1, |B|>=1
			else if (scopeType.equals("nonlocal")) { 
				//getting Y' and S
				String[] YNamesOld = constraint.getVariableNames();
				RetrieveStrictClosure rtc = new RetrieveStrictClosure(YNamesOld,net);
				int numOfOldYVars = constraint.getNumOfVariables();
				int numOfYVars = rtc.getUpdatedVariablesSize();
				int numOfCVars = rtc.getClosureSize();
				String[] closureNames;
				if (numOfCVars >0) {
					closureNames = new String[numOfCVars];
					Enumeration enu = rtc.getStrictClosure();
					int i = 0;
					while (enu.hasMoreElements()) {
						closureNames[i] = (String)enu.nextElement();
						i++;
					}
				}
				else {
					closureNames = null;
				}
				String[] YNames = new String[numOfYVars];
				Enumeration enup = rtc.getUpdatedVariables();
				int ip = 0;
				while (enup.hasMoreElements()) {
					YNames[ip] = (String)enup.nextElement();
					ip++;
				}
				RandomVariable[] YVars = new RandomVariable[numOfYVars];
				RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
				NodeList nodeListY = new NodeList(net);
				NodeList nodeListYS = new NodeList(net);
				for (int i=0; i<numOfYVars; i++) {
					Node node = net.getNode(YNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(YNames[i],stateNames);
					YVars[i] = rv;
					YSVars[i] = rv;
					nodeListY.addElement(node);
					nodeListYS.addElement(node);
				}
				if (numOfCVars > 0) {
					for (int i=0; i<numOfCVars; i++) {
						Node node = net.getNode(closureNames[i]);
						int nodeNumStates = node.getNumStates();
						String[] stateNames = new String[nodeNumStates];
						for (int j=0; j<nodeNumStates; j++)
							stateNames[j] = node.state(j).getName();
						RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
						YSVars[numOfYVars+i] = rv;
						nodeListYS.addElement(node);
					}
				}
				//getting Q_(k-1)(Y',S)
				JointProbDistribution jpdOfYS = new JointProbDistribution(YSVars);
				int numOfYSEntries = jpdOfYS.getNumOfEntries();
				for (int i=0; i<numOfYSEntries; i++) {
					jpdOfYS.addProbEntry(jpdOfYS.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS.getIndices(i)));
				}
				//calling IPFPOneR.java to get Q_(k)(Y',S)
				CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
				algo.computation();
				jpdOfYS = algo.getDistribution();
				//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y', updating the CPT; note that Pi(Cj) might be empty
				for (int i=0; i<numOfYVars; i++) {
					/* getting the distribution for CPT */
					RandomVariable rv = YVars[i];
					Node node = net.getNode(YNames[i]);
					NodeList nodeParents = node.getParents();
					if (nodeParents.size() > 0) { //|Pi(Cj)|>0
						RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
						int[] dims_of_pvs = new int[nodeParents.size()];
						int numOfParentEntries = 1;
						for (int j=0; j<nodeParents.size(); j++) {
							Node pnode = (Node)nodeParents.elementAt(j);
							int pnodeNumStates = pnode.getNumStates();
							String[] pnodeStateNames = new String[pnodeNumStates];
							for (int k=0; k<pnodeNumStates; k++)
								pnodeStateNames[k] = pnode.state(k).getName();
							RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
							pvs[j] = prv;
							dims_of_pvs[j] = pnodeNumStates;
							numOfParentEntries *= dims_of_pvs[j];
						} //end-for-j
						CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
						/* updating the CPT */
						for (int j=0; j<numOfParentEntries; j++) {
							int[] indices_of_parents = new int[nodeParents.size()];
							int[] indices_in_CPTDist = new int[nodeParents.size()+1];
							int offset = j;
							for (int t=nodeParents.size()-1; t>=0; --t) {
								indices_of_parents[t] = offset % dims_of_pvs[t];
								offset = offset  / dims_of_pvs[t];
								indices_in_CPTDist[t] = indices_of_parents[t];
							}
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t=0; t<rv.getNumOfStates(); t++) {
								indices_in_CPTDist[nodeParents.size()] = t;
								newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
								sum += newTable[t];
							}
							for (int t=0; t<rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;							
							node.setCPTable(indices_of_parents,newTable);
						} //end-for-j					
					}
					else { //|Pi(Cj)|=0
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t=0; t<rv.getNumOfStates(); t++) {
							newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
							sum += newTable[t];
						}
						for (int t=0; t<rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t]/sum;						
						node.setCPTable("",newTable);
					}					
					net.compile();
				}//end-for-i
				net.compile();								
			}
			else { //otherwise
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation3()): Wrong conditional constraint provided!");				
			}
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Implementation Variation 4.
	 */
	private void variation4() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			String scopeType = constraint.getScopeType();
			//local conditional constraint R(C|L)
			if (scopeType.equals("local")) { 
				LocalConditionalConstraint localConstraint = (LocalConditionalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(C|L) / Q_(k-1)(C|L)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation4()): Wrong local conditional constraint provided!");										
					}
					//getting information about C's parent nodes
					int numOfEntries = 1;
					int[] dims_of_parents = new int[parents.size()];
					String[] parentNames = new String[parents.size()];
					for (int i=0; i<parents.size(); i++) {
						Node p = (Node) parents.elementAt(i);
						parentNames[i] = p.getName();
						dims_of_parents[i] = p.getNumStates();
						numOfEntries *= dims_of_parents[i];
					}
					//getting information about L's nodes and it's relation to Pi(C)
					String[] LNames = localConstraint.getCondVariableNames();
					int[] locs_in_parents = new int[numOfParentsInvolved];
					for (int i=0; i<numOfParentsInvolved; i++) {
						boolean isFound = false;
						for (int j=0; j<parents.size(); j++) {
							if (LNames[i].equals(parentNames[j])) {
								locs_in_parents[i] = j;
								isFound = true;
								break;
							}
						}
						if (!isFound) //note that L is a non-empty subset of Pi(C)
							throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation4()): Wrong local conditional constraint provided!");																		
					}
					//Getting Q_(k-1)(C|L), in the same storage order as R(C|L)
					//Assume that the order of the states are the same for every node across R(C|L) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					CondProbDistribution cpdOfCL = new CondProbDistribution(localConstraint.getDistribution());
					NodeList nodeListCL = new NodeList(net);
					NodeList nodeListL = new NodeList(net);
					for (int i=0; i<numOfParentsInvolved; i++) {
						nodeListL.addElement(net.getNode(LNames[i]));
						nodeListCL.addElement(net.getNode(LNames[i]));
					}
					nodeListCL.addElement(node);
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesOfCL = localConstraint.getDistribution().getIndices(i);
						int[] indicesOfL = new int[numOfParentsInvolved];
						for (int j=0; j<numOfParentsInvolved; j++)
							indicesOfL[j] = indicesOfCL[j];
						double v1 = net.getJointProbability(nodeListCL,indicesOfCL);
						double v2 = net.getJointProbability(nodeListL, indicesOfL);
						double v = 0;
						if (v1>0)
							v = v1/v2; //note that if v1>0, then v2>0 too
						cpdOfCL.addCondProbEntry(indicesOfCL, v);
					}
					//computing, normalizing, and updating C's CPT entries 
					for (int i=0; i<numOfEntries; i++) { //iterate over all possible assignments of the parent nodes
						int[] indices_of_parents = new int[parents.size()];
						int offset = i;
						for (int t=parents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_parents[t];
							offset = offset  / dims_of_parents[t];
						}
						float sum = 0;
						float[] newTable = new float[numOfNodeStates];
						int[] stateIdxOfCLVars = new int[numOfParentsInvolved+1];
						for (int t=0; t<numOfParentsInvolved; t++) {
							stateIdxOfCLVars[t] = indices_of_parents[locs_in_parents[t]];
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfCLVars[numOfParentsInvolved] = t;	
							v2 = localConstraint.getDistribution().getCondProbEntry(stateIdxOfCLVars); //fetching R(C|L)
							v3 = cpdOfCL.getCondProbEntry(stateIdxOfCLVars); //fetching Q_(k-1)(C|L)	
							if (v2==0 || v3==0) {
								newTable[t] = 0;
							}
							else {
								newTable[t] = (float) (v1 * v2 / v3);
							}
							sum+=newTable[t];							
						}						
						for (int t=0; t<numOfNodeStates; t++) //normalization
							newTable[t] = newTable[t]/sum;
						node.setCPTable(indices_of_parents,newTable);
					}
					net.compile();
				}
				//when C has no parents: |L|=0, wrong case
				else { 
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation4()): Wrong local conditional constraint provided!");
				}								
			}
			//non-local conditional constraint: R(A|B), |A|>=1, |B|>=1
			else if (scopeType.equals("nonlocal")) { 
				//getting Y' and S
				String[] YNamesOld = constraint.getVariableNames();
				RetrieveStrictClosure rtc = new RetrieveStrictClosure(YNamesOld,net);
				int numOfOldYVars = constraint.getNumOfVariables();
				int numOfYVars = rtc.getUpdatedVariablesSize();
				int numOfCVars = rtc.getClosureSize();
				String[] closureNames;
				if (numOfCVars >0) {
					closureNames = new String[numOfCVars];
					Enumeration enu = rtc.getStrictClosure();
					int i = 0;
					while (enu.hasMoreElements()) {
						closureNames[i] = (String)enu.nextElement();
						i++;
					}
				}
				else {
					closureNames = null;
				}
				String[] YNames = new String[numOfYVars];
				Enumeration enup = rtc.getUpdatedVariables();
				int ip = 0;
				while (enup.hasMoreElements()) {
					YNames[ip] = (String)enup.nextElement();
					ip++;
				}
				RandomVariable[] YVars = new RandomVariable[numOfYVars];
				RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
				NodeList nodeListY = new NodeList(net);
				NodeList nodeListYS = new NodeList(net);
				for (int i=0; i<numOfYVars; i++) {
					Node node = net.getNode(YNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(YNames[i],stateNames);
					YVars[i] = rv;
					YSVars[i] = rv;
					nodeListY.addElement(node);
					nodeListYS.addElement(node);
				}
				if (numOfCVars > 0) {
					for (int i=0; i<numOfCVars; i++) {
						Node node = net.getNode(closureNames[i]);
						int nodeNumStates = node.getNumStates();
						String[] stateNames = new String[nodeNumStates];
						for (int j=0; j<nodeNumStates; j++)
							stateNames[j] = node.state(j).getName();
						RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
						YSVars[numOfYVars+i] = rv;
						nodeListYS.addElement(node);
					}
				}
				//do-until-converge
				JointProbDistribution jpdOfYS_old = new JointProbDistribution(YSVars);
				int numOfYSEntries = jpdOfYS_old.getNumOfEntries();
				for (int i=0; i<numOfYSEntries; i++) {
					jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
				}
				double totalVariance = 0;
				do {
					//getting Q_(k-1)(Y',S)
					JointProbDistribution jpdOfYS = new JointProbDistribution(jpdOfYS_old);
					//calling IPFPOneR.java to get Q_(k)'(Y',S)
					CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
					algo.computation();
					jpdOfYS = algo.getDistribution();
					//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y', updating the CPT; note that Pi(Cj) might be empty
					for (int i=0; i<numOfYVars; i++) {
						/* getting the distribution for CPT */
						RandomVariable rv = YVars[i];
						Node node = net.getNode(YNames[i]);
						NodeList nodeParents = node.getParents();
						if (nodeParents.size() > 0) { //|Pi(Cj)|>0
							RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
							int[] dims_of_pvs = new int[nodeParents.size()];
							int numOfParentEntries = 1;
							for (int j=0; j<nodeParents.size(); j++) {
								Node pnode = (Node)nodeParents.elementAt(j);
								int pnodeNumStates = pnode.getNumStates();
								String[] pnodeStateNames = new String[pnodeNumStates];
								for (int k=0; k<pnodeNumStates; k++)
									pnodeStateNames[k] = pnode.state(k).getName();
								RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
								pvs[j] = prv;
								dims_of_pvs[j] = pnodeNumStates;
								numOfParentEntries *= dims_of_pvs[j];
							} //end-for-j
							CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
							/* updating the CPT */
							for (int j=0; j<numOfParentEntries; j++) {
								int[] indices_of_parents = new int[nodeParents.size()];
								int[] indices_in_CPTDist = new int[nodeParents.size()+1];
								int offset = j;
								for (int t=nodeParents.size()-1; t>=0; --t) {
									indices_of_parents[t] = offset % dims_of_pvs[t];
									offset = offset  / dims_of_pvs[t];
									indices_in_CPTDist[t] = indices_of_parents[t];
								}
								float[] newTable = new float[rv.getNumOfStates()];
								float sum = 0;
								for (int t=0; t<rv.getNumOfStates(); t++) {
									indices_in_CPTDist[nodeParents.size()] = t;
									newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
									sum += newTable[t];
								}
								for (int t=0; t<rv.getNumOfStates(); t++) //normalization
									newTable[t] = newTable[t]/sum;								
								node.setCPTable(indices_of_parents,newTable);
							} //end-for-j					
						}
						else { //|Pi(Cj)|=0
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t=0; t<rv.getNumOfStates(); t++) {
								newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
								sum += newTable[t];
							}
							for (int t=0; t<rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;							
							node.setCPTable("",newTable);
						}						
						net.compile();
					}//end-for-i
					net.compile();		
					//getting Q_(k)(Y',S)
					for (int i=0; i<numOfYSEntries; i++) {
						jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
					}
					TotalVariance obj = new TotalVariance(jpdOfYS, jpdOfYS_old);
					totalVariance = obj.getTotalVariance();									
				}
				while (totalVariance>0.005);				
			}
			else { //otherwise
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPConditionalOneR.java (method variation4()): Wrong conditional constraint provided!");				
			}
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Implementation Variation 5.
	 */
	private void variation5() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			//getting Y and S
			String[] YNames = constraint.getVariableNames();
			int numOfYVars = constraint.getNumOfVariables();
			RetrieveLooseClosure rlc = new RetrieveLooseClosure(YNames,net);
			int numOfCVars = rlc.getClosureSize();
			String[] closureNames;
			if (numOfCVars >0) {
				closureNames = new String[numOfCVars];
				Enumeration enu = rlc.getLooseClosure();
				int i = 0;
				while (enu.hasMoreElements()) {
					closureNames[i] = (String)enu.nextElement();
					i++;
				}
			}
			else {
				closureNames = null;
			}
			RandomVariable[] YVars = new RandomVariable[numOfYVars];
			RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
			NodeList nodeListY = new NodeList(net);
			NodeList nodeListYS = new NodeList(net);
			for (int i=0; i<numOfYVars; i++) {
				Node node = net.getNode(YNames[i]);
				int nodeNumStates = node.getNumStates();
				String[] stateNames = new String[nodeNumStates];
				for (int j=0; j<nodeNumStates; j++)
					stateNames[j] = node.state(j).getName();
				RandomVariable rv = new RandomVariable(YNames[i],stateNames);
				YVars[i] = rv;
				YSVars[i] = rv;
				nodeListY.addElement(node);
				nodeListYS.addElement(node);
			}
			if (numOfCVars > 0) {
				for (int i=0; i<numOfCVars; i++) {
					Node node = net.getNode(closureNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
					YSVars[numOfYVars+i] = rv;
					nodeListYS.addElement(node);
				}
			}
			//getting Q_(k-1)(Y,S)
			JointProbDistribution jpdOfYS = new JointProbDistribution(YSVars);
			int numOfYSEntries = jpdOfYS.getNumOfEntries();
			for (int i=0; i<numOfYSEntries; i++) {
				jpdOfYS.addProbEntry(jpdOfYS.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS.getIndices(i)));
			}
			//calling CIPFPOneR.java to get Q_(k)(Y,S)
			CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
			algo.computation();
			jpdOfYS = algo.getDistribution();
			//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y, updating the CPT; note that Pi(Cj) might be empty
			for (int i=0; i<numOfYVars; i++) {
				/* getting the distribution for CPT */
				RandomVariable rv = YVars[i];
				Node node = net.getNode(YNames[i]);
				NodeList nodeParents = node.getParents();
				if (nodeParents.size() > 0) { //|Pi(Cj)|>0
					RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
					int[] dims_of_pvs = new int[nodeParents.size()];
					int numOfParentEntries = 1;
					for (int j=0; j<nodeParents.size(); j++) {
						Node pnode = (Node)nodeParents.elementAt(j);
						int pnodeNumStates = pnode.getNumStates();
						String[] pnodeStateNames = new String[pnodeNumStates];
						for (int k=0; k<pnodeNumStates; k++)
							pnodeStateNames[k] = pnode.state(k).getName();
						RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
						pvs[j] = prv;
						dims_of_pvs[j] = pnodeNumStates;
						numOfParentEntries *= dims_of_pvs[j];
					} //end-for-j
					CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
					/* updating the CPT */
					for (int j=0; j<numOfParentEntries; j++) {
						int[] indices_of_parents = new int[nodeParents.size()];
						int[] indices_in_CPTDist = new int[nodeParents.size()+1];
						int offset = j;
						for (int t=nodeParents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_pvs[t];
							offset = offset  / dims_of_pvs[t];
							indices_in_CPTDist[t] = indices_of_parents[t];
						}
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t=0; t<rv.getNumOfStates(); t++) {
							indices_in_CPTDist[nodeParents.size()] = t;
							newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
							sum += newTable[t];
						}
						for (int t=0; t<rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t]/sum;						
						node.setCPTable(indices_of_parents,newTable);
					} //end-for-j					
				}
				else { //|Pi(Cj)|=0
					float[] newTable = new float[rv.getNumOfStates()];
					float sum = 0;
					for (int t=0; t<rv.getNumOfStates(); t++) {
						newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
						sum += newTable[t];
					}
					for (int t=0; t<rv.getNumOfStates(); t++) //normalization
						newTable[t] = newTable[t]/sum;					
					node.setCPTable("",newTable);
				}				
				net.compile();
			}//end-for-i
			net.compile();							
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Implementation Variation 6.
	 */
	private void variation6() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			//getting Y and S
			String[] YNames = constraint.getVariableNames();
			int numOfYVars = constraint.getNumOfVariables();
			RetrieveLooseClosure rlc = new RetrieveLooseClosure(YNames,net);
			int numOfCVars = rlc.getClosureSize();
			String[] closureNames;
			if (numOfCVars >0) {
				closureNames = new String[numOfCVars];
				Enumeration enu = rlc.getLooseClosure();
				int i = 0;
				while (enu.hasMoreElements()) {
					closureNames[i] = (String)enu.nextElement();
					i++;
				}
			}
			else {
				closureNames = null;
			}
			RandomVariable[] YVars = new RandomVariable[numOfYVars];
			RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
			NodeList nodeListY = new NodeList(net);
			NodeList nodeListYS = new NodeList(net);
			for (int i=0; i<numOfYVars; i++) {
				Node node = net.getNode(YNames[i]);
				int nodeNumStates = node.getNumStates();
				String[] stateNames = new String[nodeNumStates];
				for (int j=0; j<nodeNumStates; j++)
					stateNames[j] = node.state(j).getName();
				RandomVariable rv = new RandomVariable(YNames[i],stateNames);
				YVars[i] = rv;
				YSVars[i] = rv;
				nodeListY.addElement(node);
				nodeListYS.addElement(node);
			}
			if (numOfCVars > 0) {
				for (int i=0; i<numOfCVars; i++) {
					Node node = net.getNode(closureNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
					YSVars[numOfYVars+i] = rv;
					nodeListYS.addElement(node);
				}
			}
			//do-until-converge
			JointProbDistribution jpdOfYS_old = new JointProbDistribution(YSVars);
			int numOfYSEntries = jpdOfYS_old.getNumOfEntries();
			for (int i=0; i<numOfYSEntries; i++) {
				jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
			}
			double totalVariance = 0;
			do {
				//getting Q_(k-1)(Y,S)
				JointProbDistribution jpdOfYS = new JointProbDistribution(jpdOfYS_old);
				//calling IPFPOneR.java to get Q_(k)'(Y,S)
				CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
				algo.computation();
				jpdOfYS = algo.getDistribution();
				//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y, updating the CPT; note that Pi(Cj) might be empty
				for (int i=0; i<numOfYVars; i++) {
					/* getting the distribution for CPT */
					RandomVariable rv = YVars[i];
					Node node = net.getNode(YNames[i]);
					NodeList nodeParents = node.getParents();
					if (nodeParents.size() > 0) { //|Pi(Cj)|>0
						RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
						int[] dims_of_pvs = new int[nodeParents.size()];
						int numOfParentEntries = 1;
						for (int j=0; j<nodeParents.size(); j++) {
							Node pnode = (Node)nodeParents.elementAt(j);
							int pnodeNumStates = pnode.getNumStates();
							String[] pnodeStateNames = new String[pnodeNumStates];
							for (int k=0; k<pnodeNumStates; k++)
								pnodeStateNames[k] = pnode.state(k).getName();
							RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
							pvs[j] = prv;
							dims_of_pvs[j] = pnodeNumStates;
							numOfParentEntries *= dims_of_pvs[j];
						} //end-for-j
						CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
						/* updating the CPT */
						for (int j=0; j<numOfParentEntries; j++) {
							int[] indices_of_parents = new int[nodeParents.size()];
							int[] indices_in_CPTDist = new int[nodeParents.size()+1];
							int offset = j;
							for (int t=nodeParents.size()-1; t>=0; --t) {
								indices_of_parents[t] = offset % dims_of_pvs[t];
								offset = offset  / dims_of_pvs[t];
								indices_in_CPTDist[t] = indices_of_parents[t];
							}
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t=0; t<rv.getNumOfStates(); t++) {
								indices_in_CPTDist[nodeParents.size()] = t;
								newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
								sum += newTable[t];
							}
							for (int t=0; t<rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;							
							node.setCPTable(indices_of_parents,newTable);
						} //end-for-j					
					}
					else { //|Pi(Cj)|=0
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t=0; t<rv.getNumOfStates(); t++) {
							newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
							sum += newTable[t];
						}
						for (int t=0; t<rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t]/sum;						
						node.setCPTable("",newTable);
					}					
					net.compile();
				}//end-for-i
				net.compile();
				//getting Q_(k)(Y,S)
				for (int i=0; i<numOfYSEntries; i++) {
					jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
				}
				TotalVariance obj = new TotalVariance(jpdOfYS, jpdOfYS_old);
				totalVariance = obj.getTotalVariance();								
			}
			while (totalVariance > 0.005);
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Implementation Variation 7.
	 */
	private void variation7() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			//getting Y' and S
			String[] YNamesOld = constraint.getVariableNames();
			RetrieveStrictClosure rtc = new RetrieveStrictClosure(YNamesOld,net);
			int numOfOldYVars = constraint.getNumOfVariables();
			int numOfYVars = rtc.getUpdatedVariablesSize();
			int numOfCVars = rtc.getClosureSize();
			String[] closureNames;
			if (numOfCVars >0) {
				closureNames = new String[numOfCVars];
				Enumeration enu = rtc.getStrictClosure();
				int i = 0;
				while (enu.hasMoreElements()) {
					closureNames[i] = (String)enu.nextElement();
					i++;
				}
			}
			else {
				closureNames = null;
			}
			String[] YNames = new String[numOfYVars];
			Enumeration enup = rtc.getUpdatedVariables();
			int ip = 0;
			while (enup.hasMoreElements()) {
				YNames[ip] = (String)enup.nextElement();
				ip++;
			}
			RandomVariable[] YVars = new RandomVariable[numOfYVars];
			RandomVariable[] YSVars = new RandomVariable[numOfYVars+numOfCVars];
			NodeList nodeListY = new NodeList(net);
			NodeList nodeListYS = new NodeList(net);
			for (int i=0; i<numOfYVars; i++) {
				Node node = net.getNode(YNames[i]);
				int nodeNumStates = node.getNumStates();
				String[] stateNames = new String[nodeNumStates];
				for (int j=0; j<nodeNumStates; j++)
					stateNames[j] = node.state(j).getName();
				RandomVariable rv = new RandomVariable(YNames[i],stateNames);
				YVars[i] = rv;
				YSVars[i] = rv;
				nodeListY.addElement(node);
				nodeListYS.addElement(node);
			}
			if (numOfCVars > 0) {
				for (int i=0; i<numOfCVars; i++) {
					Node node = net.getNode(closureNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j=0; j<nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(closureNames[i],stateNames);
					YSVars[numOfYVars+i] = rv;
					nodeListYS.addElement(node);
				}
			}
			//getting Q_(k-1)(Y',S)
			JointProbDistribution jpdOfYS = new JointProbDistribution(YSVars);
			int numOfYSEntries = jpdOfYS.getNumOfEntries();
			for (int i=0; i<numOfYSEntries; i++) {
				jpdOfYS.addProbEntry(jpdOfYS.getIndices(i),net.getJointProbability(nodeListYS,jpdOfYS.getIndices(i)));
			}
			//calling IPFPOneR.java to get Q_(k)(Y',S)
			CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
			algo.computation();
			jpdOfYS = algo.getDistribution();
			//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y', updating the CPT; note that Pi(Cj) might be empty
			for (int i=0; i<numOfYVars; i++) {
				/* getting the distribution for CPT */
				RandomVariable rv = YVars[i];
				Node node = net.getNode(YNames[i]);
				NodeList nodeParents = node.getParents();
				if (nodeParents.size() > 0) { //|Pi(Cj)|>0
					RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
					int[] dims_of_pvs = new int[nodeParents.size()];
					int numOfParentEntries = 1;
					for (int j=0; j<nodeParents.size(); j++) {
						Node pnode = (Node)nodeParents.elementAt(j);
						int pnodeNumStates = pnode.getNumStates();
						String[] pnodeStateNames = new String[pnodeNumStates];
						for (int k=0; k<pnodeNumStates; k++)
							pnodeStateNames[k] = pnode.state(k).getName();
						RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
						pvs[j] = prv;
						dims_of_pvs[j] = pnodeNumStates;
						numOfParentEntries *= dims_of_pvs[j];
					} //end-for-j
					CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
					/* updating the CPT */
					for (int j=0; j<numOfParentEntries; j++) {
						int[] indices_of_parents = new int[nodeParents.size()];
						int[] indices_in_CPTDist = new int[nodeParents.size()+1];
						int offset = j;
						for (int t=nodeParents.size()-1; t>=0; --t) {
							indices_of_parents[t] = offset % dims_of_pvs[t];
							offset = offset  / dims_of_pvs[t];
							indices_in_CPTDist[t] = indices_of_parents[t];
						}
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t=0; t<rv.getNumOfStates(); t++) {
							indices_in_CPTDist[nodeParents.size()] = t;
							newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
							sum += newTable[t];
						}
						for (int t=0; t<rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t]/sum;						
						node.setCPTable(indices_of_parents,newTable);
					} //end-for-j					
				}
				else { //|Pi(Cj)|=0
					float[] newTable = new float[rv.getNumOfStates()];
					float sum = 0;
					for (int t=0; t<rv.getNumOfStates(); t++) {
						newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
						sum += newTable[t];
					}	
					for (int t=0; t<rv.getNumOfStates(); t++) //normalization
						newTable[t] = newTable[t]/sum;					
					node.setCPTable("",newTable);
				}				
				net.compile();
			}//end-for-i
			net.compile();											
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Implementation Variation 8.
	 */
	private void variation8() {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			net.compile();
			//getting Y' and S
			String[] YNamesOld = constraint.getVariableNames();
			RetrieveStrictClosure rtc = new RetrieveStrictClosure(YNamesOld, net);
			int numOfOldYVars = constraint.getNumOfVariables();
			int numOfYVars = rtc.getUpdatedVariablesSize();
			int numOfCVars = rtc.getClosureSize();
			String[] closureNames;
			if (numOfCVars > 0) {
				closureNames = new String[numOfCVars];
				Enumeration enu = rtc.getStrictClosure();
				int i = 0;
				while (enu.hasMoreElements()) {
					closureNames[i] = (String)enu.nextElement();
					i++;
				}
			}
			else {
				closureNames = null;
			}
			String[] YNames = new String[numOfYVars];
			Enumeration enup = rtc.getUpdatedVariables();
			int ip = 0;
			while (enup.hasMoreElements()) {
				YNames[ip] = (String)enup.nextElement();
				ip++;
			}
			RandomVariable[] YVars = new RandomVariable[numOfYVars];
			RandomVariable[] YSVars = new RandomVariable[numOfYVars + numOfCVars];
			NodeList nodeListY = new NodeList(net);
			NodeList nodeListYS = new NodeList(net);
			for (int i = 0; i < numOfYVars; i++) {
				Node node = net.getNode(YNames[i]);
				int nodeNumStates = node.getNumStates();
				String[] stateNames = new String[nodeNumStates];
				for (int j = 0; j < nodeNumStates; j++)
					stateNames[j] = node.state(j).getName();
				RandomVariable rv = new RandomVariable(YNames[i],stateNames);
				YVars[i] = rv;
				YSVars[i] = rv;
				nodeListY.addElement(node);
				nodeListYS.addElement(node);
			}
			if (numOfCVars > 0) {
				for (int i = 0; i < numOfCVars; i++) {
					Node node = net.getNode(closureNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j = 0; j < nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					RandomVariable rv = new RandomVariable(closureNames[i], stateNames);
					YSVars[numOfYVars + i] = rv;
					nodeListYS.addElement(node);
				}
			}
			//do-until-converge
			JointProbDistribution jpdOfYS_old = new JointProbDistribution(YSVars);
			int numOfYSEntries = jpdOfYS_old.getNumOfEntries();
			for (int i = 0; i < numOfYSEntries; i++) {
				jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i), net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
			}
			double totalVariance = 0;
//			do {
				//getting Q_(k-1)(Y',S)
				JointProbDistribution jpdOfYS = new JointProbDistribution(jpdOfYS_old);
				//calling IPFPOneR.java to get Q_(k)'(Y',S)
				CIPFPOneR algo = new CIPFPOneR(jpdOfYS, constraint.getDistribution());
				algo.computation();
				jpdOfYS = algo.getDistribution();
				//getting Q_(k)(Cj|Pi(Cj)) for each Cj in Y', updating the CPT; note that Pi(Cj) might be empty
				for (int i = 0; i < numOfYVars; i++) {
					/* getting the distribution for CPT */
					RandomVariable rv = YVars[i];
					Node node = net.getNode(YNames[i]);
					NodeList nodeParents = node.getParents();
					if (nodeParents.size() > 0) { //|Pi(Cj)|>0
						RandomVariable[] pvs = new RandomVariable[nodeParents.size()];
						int[] dims_of_pvs = new int[nodeParents.size()];
						int numOfParentEntries = 1;
						for (int j = 0; j < nodeParents.size(); j++) {
							Node pnode = (Node)nodeParents.elementAt(j);
							int pnodeNumStates = pnode.getNumStates();
							String[] pnodeStateNames = new String[pnodeNumStates];
							for (int k = 0; k < pnodeNumStates; k++)
								pnodeStateNames[k] = pnode.state(k).getName();
							RandomVariable prv = new RandomVariable(pnode.getName(),pnodeStateNames);
							pvs[j] = prv;
							dims_of_pvs[j] = pnodeNumStates;
							numOfParentEntries *= dims_of_pvs[j];
						} //end-for-j
						CondProbDistribution CPTDist = jpdOfYS.getMarginalCondDist(new RandomVariable[]{rv}, pvs);
						/* updating the CPT */
						for (int j = 0; j < numOfParentEntries; j++) {
							int[] indices_of_parents = new int[nodeParents.size()];
							int[] indices_in_CPTDist = new int[nodeParents.size() + 1];
							int offset = j;
							for (int t = nodeParents.size()-1; t >= 0; --t) {
								indices_of_parents[t] = offset % dims_of_pvs[t];
								offset = offset  / dims_of_pvs[t];
								indices_in_CPTDist[t] = indices_of_parents[t];
							}
							float[] newTable = new float[rv.getNumOfStates()];
							float sum = 0;
							for (int t = 0; t < rv.getNumOfStates(); t++) {
								indices_in_CPTDist[nodeParents.size()] = t;
								newTable[t] = (float) CPTDist.getCondProbEntry(indices_in_CPTDist);
								sum += newTable[t];
							}
							for (int t = 0; t < rv.getNumOfStates(); t++) //normalization
								newTable[t] = newTable[t]/sum;							
							node.setCPTable(indices_of_parents,newTable);
						} //end-for-j					
					}
					else { //|Pi(Cj)|=0
						float[] newTable = new float[rv.getNumOfStates()];
						float sum = 0;
						for (int t = 0; t < rv.getNumOfStates(); t++) {
							newTable[t] = (float)jpdOfYS.getMarginalDist(new RandomVariable[]{rv}).getProbEntry(new int[]{t});
							sum += newTable[t];
						}
						for (int t = 0; t < rv.getNumOfStates(); t++) //normalization
							newTable[t] = newTable[t] / sum;						
						node.setCPTable("", newTable);
					}					
					//net.compile();
				}//end-for-i
				net.compile();
				//getting Q_(k)(Y',S)
				for (int i = 0; i < numOfYSEntries; i++) {
					jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i), net.getJointProbability(nodeListYS, jpdOfYS_old.getIndices(i)));
				}
				TotalVariance obj = new TotalVariance(jpdOfYS, jpdOfYS_old);
				totalVariance = obj.getTotalVariance();								
//			}
//			while (totalVariance > 0.005);							
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
		
	/**
	 * Gets the execution time of running the algorithm.
	 * 
	 * @return algorithm execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}
	
	/**
	 * This method gets the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}
	
}
