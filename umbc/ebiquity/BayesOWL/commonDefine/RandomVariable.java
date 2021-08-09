/**
 * RandomVariable.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 05, 2004
 * Modified on Sept. 08, 2005, v0.4
 * Modified on Aug. 07, 2008, comments added
 *  
 */

package umbc.ebiquity.BayesOWL.commonDefine;

import java.util.*;

/**
 * This class implements a random variable in the classic discrete probability theory, 
 * which includes: 
 * 	(1) a name for this random variable
 *	(2) a set of possible states this random variable can take
 *
 */
public class RandomVariable {
	String rndVarName;			//the name of the random variable
	String[] rndVarStates;		//the possible states of the random variable	
	Hashtable sname_idx_map; 	//key: a specific state name, value: the index of this state in the 'rndVarStates' array 
	
	/**
	 * Constructor.
	 * Constructs a new random variable with the specified name and states.
	 * 
	 * @param vname	variable name
	 * @param vstates	variable states
	 */
	public RandomVariable (String vname, String[] vstates) {
		setName(vname);
		if (vstates == null || vstates.length == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: A random variable has at least one state!");
		}
		else { 
			rndVarStates = new String[vstates.length];
			sname_idx_map = new Hashtable();
			for (int i=0; i<vstates.length; i++) {
				setState(vstates[i],i);
				Object previousValue = sname_idx_map.put(vstates[i], new Integer(i));
				if (previousValue != null) {
					throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: Two states can not have the same name!");
				}
			}
		}
	}

	/**
	 * Constructor.
	 * Copy a random variable.
	 * 
	 * @param rv	random variable
	 */
	public RandomVariable (RandomVariable rv){
		if (rv == null) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: NULL is provided!");
		}
		else {
			setName(rv.getName());
			rndVarStates = new String[rv.getNumOfStates()];
			sname_idx_map = new Hashtable();
			for (int i=0; i<rv.getNumOfStates(); i++) {
				setState(rv.getState(i),i);
				sname_idx_map.put(rv.getState(i), new Integer(i));
			}
		}
	}
	
	/**
	 * This method sets the name of the random variable.
	 * 
	 * @param vname
	 */
	public void setName (String vname) {
		if (vname == null || vname.equals("")) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: The name can not be an empty string!");
		}
		else {
			rndVarName = vname;
		}
	}

	/**
	 * This method sets one possible state of the random variable in the specified index.
	 * 
	 * @param vstate	variable state
	 * @param idx	state index
	 */
	public void setState (String vstate, int idx) {
		if (vstate == null || vstate.equals("")) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: A state can not be an empty string!"); 
		}
		else if (idx < 0 || idx >= rndVarStates.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: State index out of bounds!");
		}
		else {
			rndVarStates[idx] = vstate;
		}
	}

	/**
	 * This methods gets the name of the random variable.
	 * 
	 * @return	variable name
	 */
	public String getName () {
		return rndVarName;
	}

	/**
	 * This method gets one possible state of the random variable given specified index.
	 * 
	 * @param idx	state index
	 * @return	state
	 */
	public String getState (int idx) {
		if (idx < 0 || idx >= rndVarStates.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: State index out of bounds!");
		} else {
			return rndVarStates[idx];
		}
	}

	/**
	 * This method gets all the possible states of the random variable.
	 * 
	 * @return	states
	 */
	public String[] getStates () {
		return rndVarStates;
	}
	
	/**
	 * This method gets the index of a specific state of this random variable.
	 * 
	 * @param vstate	variable state
	 * @return	state index
	 */
	public int getStateIndex (String vstate) {
		Integer obj = (Integer)sname_idx_map.get(vstate);
		if (obj == null){
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.RandomVariable.java: Wrong state name provided!");			
		}
		else {
			return obj.intValue();
		}
	}
	
	/**
	 * This method gets the number of possible states of the random variable.
	 * 
	 * @return	variable state number
	 */
	public int getNumOfStates () {
		return rndVarStates.length;
	}

	/**
	 * Override method toString
	 * Gets a string representation of the random variable.
	 */
	public String toString() {
		String s = "Random Variable::\tName: " + rndVarName;		
		for (int i=0; i<rndVarStates.length; i++) {
			s =  s + "\tState " + i + ": " + rndVarStates[i];
		}
		s = s + "\n";
		return s;
	}	
}
