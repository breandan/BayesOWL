/**
 * DIPFPMarginalOneR.java
 * 
 * @author Zhongli Ding (original)
 *
 * Created on Oct 19, 2005, v0.4
 * Modified on Aug. 11, 2008 
 * 
 * See paper:
 * 	Modifying Bayesian Networks by Probability Constraints
 * 	Yun Peng, Zhongli Ding
 * 	UAI 2005
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.util.*;

import norsys.netica.*;
import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;
/**
 * This class implements the 'D-IPFP' algorithm based on the UAI-2005 paper, <br>
 * to process only one marginal constraint with the form 'R(Y)', 'Y={C1, C2, ..., Cn}'.<br>
 *<br> 
 * Eight(8) variations of implementation are provided, for experimental and analytical purpose.<br>
 *<br> 
 * Marginal Constraint provided might be either: (C, C1, C2, ...,Cn are variables)<br>
 * (1) Local: R(C), or, R(C,L) and L is a non-empty subset of Pi(C);<br>
 * (2) Non-Local: R(C1,C2,...,Cn), n>=2.<br>
 *<br> 
 * Variation 1:<br> 
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C,L) / Q_(k-1)(C,L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=2)<br>
 * 				Getting Y={C1,C2,...,Cn} and it's loose closure S={Pi(Cj)}\Y (j=1 to n),<br>
 * 				Q_(k)(Y,S) = Q_(k-1)(Y,S) * R(Y) / Q_(k-1)(Y),<br>
 * 				Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)(Y,S) for each Cj in Y and updating its CPT (j=1 to n).<br>
 *<br> 				 
 * Variation 2: //Default: This is exactly the algorithm in the UAI-2005 paper.<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C,L) / Q_(k-1)(C,L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=2)<br>
 * 				Getting Y={C1,C2,...,Cn} and it's loose closure S={Pi(Cj)}\Y (j=1 to n),<br>
 * 				Do until converge { //Q_(k)(Y,S) ~= Q_(k)'(Y,S)<br>
 * 					Q_(k)'(Y,S) = Q_(k-1)(Y,S) * R(Y) / Q_(k-1)(Y),<br>
 * 					Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)'(Y,S) for each Cj in Y and updating its CPT (j=1 to n).<br>
 * 				}<br>
 *<br> 
 * Variation 3:<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C,L) / Q_(k-1)(C,L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=2)<br>
 * 				Getting a tight closure S of Y, and a updated variable set Y', please refer to 'RetrieveTightClosure.java',<br>  
 * 				Q_(k)(Y',S) = Q_(k-1)(Y',S) * R(Y) / Q_(k-1)(Y),<br>
 * 				Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)(Y',S) for each Cj in Y' and updating its CPT (|Y'|>=n).<br>
 *<br> 
 * Variation 4:<br>
 * 		(1) Judge whether this constraint is local or non-local;<br>
 * 		(2) If local, updating C's CPT only, using Q_(k)(C|Pi(C)) = Q_(k-1)(C|Pi(C)) * R(C,L) / Q_(k-1)(C,L), then normalize to 1;<br>
 * 		(3) If non-local: (n>=2)<br>
 * 				Getting a tight closure S of Y, and a updated variable set Y', please refer to 'RetrieveTightClosure.java',<br>  
 * 				Do until converge { //Q_(k)(Y',S) ~= Q_(k)'(Y',S)<br>
 * 					Q_(k)'(Y',S) = Q_(k-1)(Y',S) * R(Y) / Q_(k-1)(Y),<br>
 * 					Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)'(Y',S) for each Cj in Y' and updating its CPT (|Y'|>=n).<br>
 * 				}<br>
 *<br> 
 * Variation 5: (treat local and non-local marginal constraint as the same, n>=1)<br>
 * 		(1) Getting Y={C1,C2,...,Cn} and it's loose closure S={Pi(Cj)}\Y (j=1 to n);<br>
 * 		(2) Q_(k)(Y,S) = Q_(k-1)(Y,S) * R(Y) / Q_(k-1)(Y);<br>
 * 		(3) Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)(Y,S) for each Cj in Y and updating its CPT (j=1 to n).<br>
 *<br> 		
 * Variation 6: (treat local and non-local marginal constraint as the same, n>=1)<br>
 * 		(1) Getting Y={C1,C2,...,Cn} and it's loose closure S={Pi(Cj)}\Y (j=1 to n);<br>
 * 		(2) Do until converge { //Q_(k)(Y,S) ~= Q_(k)'(Y,S)<br>
 * 				Q_(k)'(Y,S) = Q_(k-1)(Y,S) * R(Y) / Q_(k-1)(Y),<br>
 * 				Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)'(Y,S) for each Cj in Y and updating its CPT (j=1 to n).<br>
 * 			}<br>
 *<br> 
 * Variation 7: (treat local and non-local marginal constraint as the same, n>=1)<br>
 * 		(1) Getting a tight closure S of Y, and a updated variable set Y', please refer to 'RetrieveTightClosure.java';<br> 
 * 		(2) Q_(k)(Y',S) = Q_(k-1)(Y',S) * R(Y) / Q_(k-1)(Y);<br>
 * 		(3) Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)(Y',S) for each Cj in Y' and updating its CPT (|Y'|>=n).<br>
 *<br> 
 * Variation 8: (treat local and non-local marginal constraint as the same, n>=1)<br>
 * 		(1) Getting a tight closure S of Y, and a updated variable set Y', please refer to 'RetrieveTightClosure.java';<br>  
 * 		(2) Do until converge { //Q_(k)(Y',S) ~= Q_(k)'(Y',S)<br>
 * 				Q_(k)'(Y',S) = Q_(k-1)(Y',S) * R(Y) / Q_(k-1)(Y),<br>
 * 				Getting Q_(k)(Cj|Pi(Cj)) from Q_(k)'(Y',S) for each Cj in Y' and updating its CPT (|Y'|>=n).<br>
 * 			}<br>
 *
 */
public class DIPFPMarginalOneR {
	Net net;
	MarginalConstraint constraint;
	int variation;
	long timeElapsed;

	/**
	 * Constructor.
	 * 
	 * @param bbn	Bayesian Net
	 * @param r		Marginal constraint
	 * @param choice	implementation #
	 */
	public DIPFPMarginalOneR(Net bbn, MarginalConstraint r, int choice) {
		if (bbn == null || r == null || choice>8 || choice<1) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java: Wrong BBN or Marginal Constraint or Implementation Choice provided!");
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
	 * fitting procedure (DIPFP), for a single marginal constraint, either local or non-local.
	 */
	public void computation() {
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
			//local marginal constraint R(Y): R(C) or R(C,L)
			if (scopeType.equals("local")) { 
				LocalMarginalConstraint localConstraint = (LocalMarginalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(Y) / Q_(k-1)(Y)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation1()): Wrong local marginal constraint provided!");										
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
					String[] YNames = localConstraint.getVariableNames();
					int[] locs_in_parents = new int[localConstraint.getNumOfVariables()];
					int localConcept_loc_in_R = 0; //by default, C would be the first element in set Y
					if (numOfParentsInvolved > 0) { //|L|>0
						for (int i=0; i<localConstraint.getNumOfVariables(); i++) {
							boolean isFound = false;
							if (YNames[i].equals(localConceptName)) {
								locs_in_parents[i] = -1;
								localConcept_loc_in_R = i;
								isFound = true;
							}
							else {
								for (int j=0; j<parents.size(); j++) {
									if (YNames[i].equals(parentNames[j])) {
										locs_in_parents[i] = j;
										isFound = true;
										break;
									}
								}
							}
							if (!isFound) //note that L is a non-empty subset of Pi(C)
								throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation1()): Wrong local marginal constraint provided!");																		
						}
					}
					else { //|L|=0
						locs_in_parents = null;
						localConcept_loc_in_R = 0;
					}
					//Getting Q_(k-1)(Y), in the same storage order as R(Y)
					//Assume that the order of the states are the same for every node across R(Y) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					JointProbDistribution jpdOfY = new JointProbDistribution(localConstraint.getDistribution());
					NodeList nodeListY = new NodeList(net);
					for (int i=0; i<localConstraint.getNumOfVariables(); i++)
						nodeListY.addElement(net.getNode(YNames[i]));
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesInR = localConstraint.getDistribution().getIndices(i);
						jpdOfY.addProbEntry(indicesInR,net.getJointProbability(nodeListY,indicesInR));
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
						int[] stateIdxOfYVars = new int[localConstraint.getNumOfVariables()];
						if (locs_in_parents != null) { //|L|>0
							for (int t=0; t<localConstraint.getNumOfVariables(); t++) {
								stateIdxOfYVars[t] = indices_of_parents[locs_in_parents[t]];
							}
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfYVars[localConcept_loc_in_R] = t;	
							v2 = localConstraint.getDistribution().getProbEntry(stateIdxOfYVars); //fetching R(Y)
							v3 = jpdOfY.getProbEntry(stateIdxOfYVars); //fetching Q_(k-1)(Y)	
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
				//when C has no parents: |L|=0, Q_(k)(C) = Q_(k-1)(C) * R(C) / Q_(k-1)(C) -> Q_(k)(C) = R(C)
				else { 
					float[] newTable = new float[numOfNodeStates];
					float sum = 0;
					for (int i=0; i<numOfNodeStates; i++) {
						double v = localConstraint.getDistribution().getProbEntry(localConstraint.getDistribution().getIndices(i)); //getting R(C)
						newTable[i] = (float)v;
						sum += newTable[i];
					}
					for (int i=0; i<numOfNodeStates; i++) //normalization
						newTable[i] = newTable[i]/sum;					
					node.setCPTable("",newTable);
					net.compile();
				}				
			}
			//nonlocal marginal constraint R(Y): R(C1, C2, ..., Cn) and n>=2
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
				//calling IPFPOneR.java to get Q_(k)(Y,S)
				IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation1()): Wrong marginal constraint provided!");				
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
			//local marginal constraint R(Y): R(C) or R(C,L)
			if (scopeType.equals("local")) { 
				LocalMarginalConstraint localConstraint = (LocalMarginalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(Y) / Q_(k-1)(Y)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation2()): Wrong local marginal constraint provided!");										
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
					String[] YNames = localConstraint.getVariableNames();
					int[] locs_in_parents = new int[localConstraint.getNumOfVariables()];
					int localConcept_loc_in_R = 0; //by default, C would be the first element in set Y
					if (numOfParentsInvolved > 0) { //|L|>0
						for (int i=0; i<localConstraint.getNumOfVariables(); i++) {
							boolean isFound = false;
							if (YNames[i].equals(localConceptName)) {
								locs_in_parents[i] = -1;
								localConcept_loc_in_R = i;
								isFound = true;
							}
							else {
								for (int j=0; j<parents.size(); j++) {
									if (YNames[i].equals(parentNames[j])) {
										locs_in_parents[i] = j;
										isFound = true;
										break;
									}
								}
							}
							if (!isFound) //note that L is a non-empty subset of Pi(C)
								throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation2()): Wrong local marginal constraint provided!");																		
						}
					}
					else { //|L|=0
						locs_in_parents = null;
						localConcept_loc_in_R = 0;
					}
					//Getting Q_(k-1)(Y), in the same storage order as R(Y)
					//Assume that the order of the states are the same for every node across R(Y) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					JointProbDistribution jpdOfY = new JointProbDistribution(localConstraint.getDistribution());
					NodeList nodeListY = new NodeList(net);
					for (int i=0; i<localConstraint.getNumOfVariables(); i++)
						nodeListY.addElement(net.getNode(YNames[i]));
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesInR = localConstraint.getDistribution().getIndices(i);
						jpdOfY.addProbEntry(indicesInR,net.getJointProbability(nodeListY,indicesInR));
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
						int[] stateIdxOfYVars = new int[localConstraint.getNumOfVariables()];
						if (locs_in_parents != null) { //|L|>0
							for (int t=0; t<localConstraint.getNumOfVariables(); t++) {
								stateIdxOfYVars[t] = indices_of_parents[locs_in_parents[t]];
							}
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfYVars[localConcept_loc_in_R] = t;	
							v2 = localConstraint.getDistribution().getProbEntry(stateIdxOfYVars); //fetching R(Y)
							v3 = jpdOfY.getProbEntry(stateIdxOfYVars); //fetching Q_(k-1)(Y)	
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
				//when C has no parents: |L|=0, Q_(k)(C) = Q_(k-1)(C) * R(C) / Q_(k-1)(C) -> Q_(k)(C) = R(C)
				else { 
					float[] newTable = new float[numOfNodeStates];
					float sum = 0;
					for (int i=0; i<numOfNodeStates; i++) {
						double v = localConstraint.getDistribution().getProbEntry(localConstraint.getDistribution().getIndices(i)); //getting R(C)
						newTable[i] = (float)v;
						sum += newTable[i];
					}
					for (int i=0; i<numOfNodeStates; i++) //normalization
						newTable[i] = newTable[i]/sum;										
					node.setCPTable("",newTable);
					net.compile();
				}				
			}
			//nonlocal marginal constraint R(Y): R(C1, C2, ..., Cn) and n>=2
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
					IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
								System.out.println("*******"+newTable[0]);
								float[] temp = new float[55];
								System.out.println("*******"+node.getCPTable(indices_of_parents, temp)[0]);
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
				throw new IllegalArgumentException("Class BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation2()): Wrong marginal constraint provided!");				
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
			//local marginal constraint R(Y): R(C) or R(C,L)
			if (scopeType.equals("local")) { 
				LocalMarginalConstraint localConstraint = (LocalMarginalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(Y) / Q_(k-1)(Y)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation3()): Wrong local marginal constraint provided!");										
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
					String[] YNames = localConstraint.getVariableNames();
					int[] locs_in_parents = new int[localConstraint.getNumOfVariables()];
					int localConcept_loc_in_R = 0; //by default, C would be the first element in set Y
					if (numOfParentsInvolved > 0) { //|L|>0
						for (int i=0; i<localConstraint.getNumOfVariables(); i++) {
							boolean isFound = false;
							if (YNames[i].equals(localConceptName)) {
								locs_in_parents[i] = -1;
								localConcept_loc_in_R = i;
								isFound = true;
							}
							else {
								for (int j=0; j<parents.size(); j++) {
									if (YNames[i].equals(parentNames[j])) {
										locs_in_parents[i] = j;
										isFound = true;
										break;
									}
								}
							}
							if (!isFound) //note that L is a non-empty subset of Pi(C)
								throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation3()): Wrong local marginal constraint provided!");																		
						}
					}
					else { //|L|=0
						locs_in_parents = null;
						localConcept_loc_in_R = 0;
					}
					//Getting Q_(k-1)(Y), in the same storage order as R(Y)
					//Assume that the order of the states are the same for every node across R(Y) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					JointProbDistribution jpdOfY = new JointProbDistribution(localConstraint.getDistribution());
					NodeList nodeListY = new NodeList(net);
					for (int i=0; i<localConstraint.getNumOfVariables(); i++)
						nodeListY.addElement(net.getNode(YNames[i]));
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesInR = localConstraint.getDistribution().getIndices(i);
						jpdOfY.addProbEntry(indicesInR,net.getJointProbability(nodeListY,indicesInR));
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
						int[] stateIdxOfYVars = new int[localConstraint.getNumOfVariables()];
						if (locs_in_parents != null) { //|L|>0
							for (int t=0; t<localConstraint.getNumOfVariables(); t++) {
								stateIdxOfYVars[t] = indices_of_parents[locs_in_parents[t]];
							}
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfYVars[localConcept_loc_in_R] = t;	
							v2 = localConstraint.getDistribution().getProbEntry(stateIdxOfYVars); //fetching R(Y)
							v3 = jpdOfY.getProbEntry(stateIdxOfYVars); //fetching Q_(k-1)(Y)	
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
				//when C has no parents: |L|=0, Q_(k)(C) = Q_(k-1)(C) * R(C) / Q_(k-1)(C) -> Q_(k)(C) = R(C)
				else { 
					float[] newTable = new float[numOfNodeStates];
					float sum = 0;
					for (int i=0; i<numOfNodeStates; i++) {
						double v = localConstraint.getDistribution().getProbEntry(localConstraint.getDistribution().getIndices(i)); //getting R(C)
						newTable[i] = (float)v;
						sum += newTable[i];
					}
					for (int i=0; i<numOfNodeStates; i++) //normalization
						newTable[i] = newTable[i]/sum;										
					node.setCPTable("",newTable);
					net.compile();
				}				
			}
			//nonlocal marginal constraint R(Y): R(C1, C2, ..., Cn) and n>=2
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
				IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation3()): Wrong marginal constraint provided!");				
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
			//local marginal constraint R(Y): R(C) or R(C,L)
			if (scopeType.equals("local")) { 
				LocalMarginalConstraint localConstraint = (LocalMarginalConstraint)constraint;
				String localConceptName = localConstraint.getConceptName();	//getting C's name
				int numOfParentsInvolved = localConstraint.getNumOfParentsInvolved(); //getting |L|
				Node node = net.getNode(localConceptName);
				int numOfNodeStates = node.getNumStates(); //getting C's total number of states
				NodeList parents = node.getParents();
				//when C has some parents: Q_(k)(C|parents) = Q_(k-1)(C|parents) * R(Y) / Q_(k-1)(Y)
				if (parents.size() > 0) { 
					//wrong case
					if (numOfParentsInvolved > parents.size()) { //|L| > |Pi(C)|
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation4()): Wrong local marginal constraint provided!");										
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
					String[] YNames = localConstraint.getVariableNames();
					int[] locs_in_parents = new int[localConstraint.getNumOfVariables()];
					int localConcept_loc_in_R = 0; //by default, C would be the first element in set Y
					if (numOfParentsInvolved > 0) { //|L|>0
						for (int i=0; i<localConstraint.getNumOfVariables(); i++) {
							boolean isFound = false;
							if (YNames[i].equals(localConceptName)) {
								locs_in_parents[i] = -1;
								localConcept_loc_in_R = i;
								isFound = true;
							}
							else {
								for (int j=0; j<parents.size(); j++) {
									if (YNames[i].equals(parentNames[j])) {
										locs_in_parents[i] = j;
										isFound = true;
										break;
									}
								}
							}
							if (!isFound) //note that L is a non-empty subset of Pi(C)
								throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation4()): Wrong local marginal constraint provided!");																		
						}
					}
					else { //|L|=0
						locs_in_parents = null;
						localConcept_loc_in_R = 0;
					}
					//Getting Q_(k-1)(Y), in the same storage order as R(Y)
					//Assume that the order of the states are the same for every node across R(Y) and the given BBN.
					int numOfREntries = localConstraint.getDistribution().getNumOfEntries();
					JointProbDistribution jpdOfY = new JointProbDistribution(localConstraint.getDistribution());
					NodeList nodeListY = new NodeList(net);
					for (int i=0; i<localConstraint.getNumOfVariables(); i++)
						nodeListY.addElement(net.getNode(YNames[i]));
					for (int i=0; i<numOfREntries; i++) {
						int[] indicesInR = localConstraint.getDistribution().getIndices(i);
						jpdOfY.addProbEntry(indicesInR,net.getJointProbability(nodeListY,indicesInR));
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
						int[] stateIdxOfYVars = new int[localConstraint.getNumOfVariables()];
						if (locs_in_parents != null) { //|L|>0
							for (int t=0; t<localConstraint.getNumOfVariables(); t++) {
								stateIdxOfYVars[t] = indices_of_parents[locs_in_parents[t]];
							}
						}
						for (int t=0; t<numOfNodeStates; t++) {
							double v1, v2, v3 = 0;
							v1 = node.getCPTable(indices_of_parents,null)[t]; //getting Q_(k-1)(C|parents)
							stateIdxOfYVars[localConcept_loc_in_R] = t;	
							v2 = localConstraint.getDistribution().getProbEntry(stateIdxOfYVars); //fetching R(Y)
							v3 = jpdOfY.getProbEntry(stateIdxOfYVars); //fetching Q_(k-1)(Y)	
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
				//when C has no parents: |L|=0, Q_(k)(C) = Q_(k-1)(C) * R(C) / Q_(k-1)(C) -> Q_(k)(C) = R(C)
				else { 
					float[] newTable = new float[numOfNodeStates];
					float sum = 0;
					for (int i=0; i<numOfNodeStates; i++) {
						double v = localConstraint.getDistribution().getProbEntry(localConstraint.getDistribution().getIndices(i)); //getting R(C)
						newTable[i] = (float)v;
						sum += newTable[i];
					}
					for (int i=0; i<numOfNodeStates; i++) //normalization
						newTable[i] = newTable[i]/sum;										
					node.setCPTable("",newTable);
					net.compile();
				}				
			}
			//nonlocal marginal constraint R(Y): R(C1, C2, ..., Cn) and n>=2
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
					IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFPMarginalOneR.java (method variation4()): Wrong marginal constraint provided!");				
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
			//calling IPFPOneR.java to get Q_(k)(Y,S)
			IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
				IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
			IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
			for (int i = 0; i < numOfYSEntries; i++) {
				jpdOfYS_old.addProbEntry(jpdOfYS_old.getIndices(i), net.getJointProbability(nodeListYS,jpdOfYS_old.getIndices(i)));
			}
			double totalVariance = 0;
			do {
				//getting Q_(k-1)(Y',S)
				JointProbDistribution jpdOfYS = new JointProbDistribution(jpdOfYS_old);
				//calling IPFPOneR.java to get Q_(k)'(Y',S)
				IPFPOneR algo = new IPFPOneR(jpdOfYS, constraint.getDistribution());
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
							int[] indices_in_CPTDist = new int[nodeParents.size()+1];
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
							node.setCPTable(indices_of_parents, newTable);
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
						node.setCPTable("",newTable);
					}					
					//net.compile();
				}//end-for-i
				net.compile();		
				
				//getting Q_(k)(Y',S)
				for (int i = 0; i < numOfYSEntries; i++) {
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
	 * Gets the execution time of running the algorithm.
	 * 
	 * @return	algorithm execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}
	
	/**
	 * This method returns the revised BBN obtained.
	 * 
	 * @return Bayesian Net
	 */
	public Net getNet() {
		return net;
	}
	
}