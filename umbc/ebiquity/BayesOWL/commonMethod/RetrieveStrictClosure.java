/**
 * RetrieveStrictClosure.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 18, 2005, v0.4
 * Modified on Aug. 11, 2008
 *
 */

package umbc.ebiquity.BayesOWL.commonMethod;

import java.util.*;
import norsys.netica.*;

/**
 * Given a set of random variables "Y = {V1, ..., Vn}" from a Bayesian Belief Network, 
 * retrieves the strict closure of "Y", which are defined as: <br>
 * 		Initially, "S = {Pi(V1), ..., Pi(Vn)}\Y";<br>
 * 		If some "Si" in "S" is a descendant of some "Yi" in "Y", then:<br>
 * 			(1) "Y = Y + {Si}",<br>
 * 			(2) "S = S - {Si} + Pi(Si)\Y".<br>
 * 		Repeat this process until such a "Si" does not exist any more.<br> 
 *
 */
public class RetrieveStrictClosure {

	Net net;
	String[] variables;
	Vector closure;
	Vector variablesUpdated;
	
	/**
	 * Constructs with a given Bayesian Belief Network and a given set of random variables from this belief network.
	 */
	public RetrieveStrictClosure(String[] vars, Net bbn) {
		if (vars == null || vars.length == 0 || bbn == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveStrictClosure.java: Invalid variables or BBN provided!");			
		}
		else {
			net = bbn;
			variablesUpdated = new Vector();
			variables = new String[vars.length];
			for (int i = 0; i < vars.length; i++) {
				variables[i] = vars[i];
				variablesUpdated.addElement(vars[i]);
			}
			closure = new Vector();
			retrieve();
		}
	}
	
	/**
	 * Retrieves the closure.
	 */
	private void retrieve() {
		try {
			//Gets the loose closure.
			for (int i = 0; i < variables.length; i++) {
				Node node = net.getNode(variables[i]);
				if (node == null) {
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveStrictClosure.java: The BBN does not contain a node named " + variables[i] + " !");
				}
				else {
					NodeList nodeList = node.getParents();
					int numOfParents = nodeList.size();
					for (int j=0; j<numOfParents; j++) {
						Node pnode = nodeList.getNode(j);
						String pname = pnode.getName();
						if (!(closure.contains(pname) || variablesUpdated.contains(pname)))
							closure.addElement(pname);						
					}
				}
			}
			//Gets the strict closure.
			Vector newVarsToBeProcessed = new Vector();
			for (int i = 0; i < variables.length; i++)
				newVarsToBeProcessed.addElement(variables[i]);
			do {
				Vector newClosureToBeProcessed = new Vector();
				Vector newAddedVarsToBeProcessed = new Vector();
				for (int i=0; i<closure.size(); i++) {
					String sname = (String)closure.get(i);
					boolean isDescendant = false;
					for (int j=0; j<newVarsToBeProcessed.size(); j++) {
						String yname = (String)newVarsToBeProcessed.get(j);
						if (isDescendantInDAG(sname,yname)) {
							//Y = Y + Si
							variablesUpdated.addElement(sname);				
							newAddedVarsToBeProcessed.addElement(sname);
							//S = S - {Si} + Pi(Si)\Y
							Node snode = net.getNode(sname);
							NodeList snodeList = snode.getParents();
							int numOfSParents = snodeList.size();
							for (int k=0; k<numOfSParents; k++) {
								Node spnode = snodeList.getNode(k);
								String spname = spnode.getName();
								if (!(closure.contains(spname) || variablesUpdated.contains(spname) || newClosureToBeProcessed.contains(spname)))
									newClosureToBeProcessed.addElement(spname);
							}
							isDescendant = true;
							break;
						}
					}
					if (!isDescendant) {
						newClosureToBeProcessed.addElement(sname);
					}
				}
				closure.clear();
				newVarsToBeProcessed.clear();
				for (int i=0; i<newClosureToBeProcessed.size(); i++)
					closure.addElement((String)newClosureToBeProcessed.get(i));
				for (int i=0; i<newAddedVarsToBeProcessed.size(); i++)
					newVarsToBeProcessed.addElement((String)newAddedVarsToBeProcessed.get(i));
			}
			while (newVarsToBeProcessed.size()>0);
		}
		catch (NeticaException e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveStrictClosure.java: " +
					"Error occurs when trying to retrieve the strict closure of a given set of random variables!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Test whether a given variable node is a descendant of another given variable node in the corresponding Bayesian Belief Network.
	 * 
	 * @param descendant	desdendant node
	 * @param ancestor	ancestor node
	 * @return	true: descendant is a descendant
	 * 			false: otherwise
	 */
	private boolean isDescendantInDAG(String descendant, String ancestor) {
		try {
			Node descendantNode = net.getNode(descendant);
			NodeList parentNodeList = descendantNode.getParents();
			int numOfParents = parentNodeList.size();
			if (numOfParents == 0) {//no parents at all
				return false;
			}
			else {
				for (int i = 0; i < numOfParents; i++) {
					Node pnode = parentNodeList.getNode(i);
					String pname = pnode.getName();
					if (ancestor.equals(pname)) {
						return true;
					}
					else {
						boolean isDescendant = isDescendantInDAG(pname,ancestor);
						if (isDescendant) return true;
					}
				}
				return false;
			}
		}
		catch (NeticaException e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveStrictClosure.java: Error occurs when trying to judge whether one node is an ancestor of another node in a given Bayesian Belief Networks!");
			e.printStackTrace();	
			return false;
		}
	}
	
	/**
	 * Gets the variables in the closure.
	 * 
	 * @return	variables
	 */
	public Enumeration getStrictClosure() {
		return closure.elements();
	}
	
	/**
	 * Gets the set of updated variables.
	 * 
	 * @return	updated variables
	 */
	public Enumeration getUpdatedVariables() {
		return variablesUpdated.elements();
	}

	/**
	 * Gets the number of variables in the closure.
	 * 
	 * @return	variable number
	 */
	public int getClosureSize() {
		return closure.size();
	}
	
	/**
	 * Gets the number of updated variables.
	 * 
	 * @return	updated variable number
	 */
	public int getUpdatedVariablesSize() {
		return variablesUpdated.size();
	}

}