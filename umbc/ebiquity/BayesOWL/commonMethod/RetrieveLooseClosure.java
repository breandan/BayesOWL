/**
 * RetrieveLooseClosure.java
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
 * retrieves the loose closure of "Y", which are defined as: "S = {Pi(V1), ..., Pi(Vn)}\Y".
 *
 */
public class RetrieveLooseClosure {

	Net net;
	String[] variables;
	Vector closure;
	
	/**
	 * Constructor.
	 * Constructs with a given Bayesian Belief Network and a given set of random variables from this belief network.
	 * 
	 * @param vars	variables
	 * @param bbn	Bayesian Net
	 */
	public RetrieveLooseClosure(String[] vars, Net bbn) {
		if (vars == null || vars.length == 0 || bbn == null) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveLooseClosure.java: Invalid variables or BBN provided!");			
		}
		else {
			net = bbn;
			variables = new String[vars.length];
			for (int i=0; i<vars.length; i++) {
				variables[i] = vars[i];
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
			//Gets the union set of Pi(Vi), with i from 1 to n, and no duplicates.
			for (int i=0; i<variables.length; i++) {
				Node node = net.getNode(variables[i]);
				if (node == null) {
					throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveLooseClosure.java: The BBN does not contain a node named " + variables[i] + " !");
				}
				else {
					NodeList nodeList = node.getParents();
					int numOfParents = nodeList.size();
					for (int j=0; j<numOfParents; j++) {
						Node pnode = nodeList.getNode(j);
						String pname = pnode.getName();
						if (!closure.contains(pname))
							closure.addElement(pname);
					}
				}
			}
			//Removes those variables in {Pi(Vi)} which are also in the set of provided variables.
			for (int i=0; i<variables.length; i++) {
				if (closure.contains(variables[i]))
					closure.removeElement(variables[i]);
			}
		}
		catch (NeticaException e) {
			System.out.println("Class umbc.ebiquity.BayesOWL.commonMethod.RetrieveLooseClosure.java: Error occurs when trying to retrieve the loose closure of a given set of random variables!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the variables in the closure.
	 * 
	 * @return	variables
	 */
	public Enumeration getLooseClosure() {
		return closure.elements();
	}
	
	/**
	 * Gets the number of variables in the closure. 
	 * 
	 * @return	number of variables
	 */
	public int getClosureSize() {
		return closure.size();
	}
	
}