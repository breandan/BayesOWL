/**
 * CondProbDistribution.java
 *
 * @author Zhongli Ding (original)
 * 
 * Created on Dec. 08, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 07, 2008, comments added
 */

package umbc.ebiquity.BayesOWL.commonDefine;

import java.util.*;

/**
 * This class implements a full conditional probability distribution, which includes:<br>
 * 	(1) An array of n prior random variables {vi}, i = 1 to n, each vi has vdi states<br>
 *	(2) An array of m condition random variables {ci}, i = 1 to m, each ci has cdi states<br>
 *	(3) A m+n-dimensional array with size "cd1 x cd2 x ... x cdm x vd1 x vd2 x ... x vdn" <br>
 *		to store all the probability values<br>
 * e.g.<br>
 * 	binary variables: A, B with P(A|B)={{0.7, 0.3},{0.4, 0.6}}, True: 0, False: 1<br>
 * 	rndVarsPrior[0] = A, rndVarsCond[0]= B <br>
 * 	probEntries[0][0]= 0.7		P(A=True|B=True)<br>
 * 	probEntries[0][1]= 0.3		P(A=False|B=True)<br>
 * 	probEntries[1][0]= 0.4		P(A=True|B=False)<br>
 * 	probEntries[1][1]= 0.6		P(A=False|B=False)<br>
 * 	            |  |<br>
 * 	order is    B  A<br>
 *
 */
public class CondProbDistribution extends ProbDistribution {
	RandomVariable[] rndVarsPrior;		//an array of prior random variables related to this conditional probability distribution
	RandomVariable[] rndVarsCond;		//an array of condition random variables related to this conditional probability distribution
	MultiDimensionalArray probEntries;	//a multi-dimensional array to store the probability values, in the same order as the random variable arrays
	Hashtable pvname_dim_map;			//a mapping between prior random variable name and its index (or dimension) in the prior random variable array 
	Hashtable cvname_dim_map;			//a mapping between condition random variable name and its index (or dimension) in the condition random variable array 

	/**
	 * Constructs a new conditional probability distribution table, given a set of prior and condition variables.
	 * Assume 'argusPrior' and 'argusCond' are disjoint with each other.
	 * Allocates storage spaces for P(Priors|Conds), and set all initial probability values as zero.
	 * 
	 * @param argusPrior	prior random variables
	 * @param argusCond		conditional random variables
	 */
	public CondProbDistribution (RandomVariable[] argusPrior, RandomVariable[] argusCond) {
		super("CPD");
		if (argusPrior == null || argusPrior.length == 0)  {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: No prior random variables specified!");
		}
		else if (argusCond == null || argusCond.length == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: No condition random variables specified!");
		}
		else {
			rndVarsPrior = new RandomVariable[argusPrior.length];
			rndVarsCond = new RandomVariable[argusCond.length];
			int[] dims = new int[argusCond.length+argusPrior.length];
			pvname_dim_map = new Hashtable();
			cvname_dim_map = new Hashtable();
			for (int i=0; i<argusCond.length; i++) {
				rndVarsCond[i] = new RandomVariable(argusCond[i]);
				dims[i] = argusCond[i].getNumOfStates();
				cvname_dim_map.put(argusCond[i].getName(), new Integer(i));
			}
			for (int i=0; i<argusPrior.length; i++) {
				rndVarsPrior[i] = new RandomVariable(argusPrior[i]);
				dims[argusCond.length+i] = argusPrior[i].getNumOfStates();
				pvname_dim_map.put(argusPrior[i].getName(), new Integer(i));
			}
			probEntries = new MultiDimensionalArray(dims);
		}
	}

	/**
	 * Copy Constructor.
	 * Assume 'argusPrior' and 'argusCond' are disjoint with each other.
	 * 
	 * @param cpd	conditional PD
	 */
	public CondProbDistribution (CondProbDistribution cpd) {
		super("CPD");
		if (cpd == null || cpd.getNumOfPriorVariables() == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: No prior random variables specified!");
		}
		else if (cpd == null || cpd.getNumOfCondVariables() == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: No condition random variables specified!");
		}
		else {
			int numOfPriors = cpd.getNumOfPriorVariables();
			int numOfConds = cpd.getNumOfCondVariables();
			rndVarsPrior = new RandomVariable[numOfPriors];
			rndVarsCond = new RandomVariable[numOfConds];
			int[] dims = new int[numOfPriors+numOfConds];
			pvname_dim_map = new Hashtable();
			cvname_dim_map = new Hashtable();
			for (int i=0; i<numOfConds; i++) {
				rndVarsCond[i] = new RandomVariable(cpd.getCondVariable(i));
				dims[i] = rndVarsCond[i].getNumOfStates();
				cvname_dim_map.put(rndVarsCond[i].getName(), new Integer(i));
			}
			for (int i=0; i<numOfPriors; i++) {
				rndVarsPrior[i] = new RandomVariable(cpd.getPriorVariable(i));
				dims[numOfConds+i] = rndVarsPrior[i].getNumOfStates();
				pvname_dim_map.put(rndVarsPrior[i].getName(), new Integer(i));
			}
			probEntries = new MultiDimensionalArray(dims);
			for (int i=0; i<cpd.getNumOfEntries(); i++)
				probEntries.putElement(cpd.getIndices(i),cpd.getCondProbEntry(cpd.getIndices(i)));
		}
	}

	/**
	 * This method gets the prior random variable in the specified dimension.
	 * 
	 * @param dim	dimension
	 * @return	prior random variable
	 */
	public RandomVariable getPriorVariable (int dim) {
		if (dim < 0 || dim >= rndVarsPrior.length) { 
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: Dimension of the prior random variables is out of bound!");
		}
		else {
			return rndVarsPrior[dim];
		}
	}
	
	/**
	 * This method gets the condition random variable in the specified dimension.
	 * 
	 * @param dim	dimension
	 * @return	condition variable
	 */
	public RandomVariable getCondVariable (int dim) {
		if (dim<0 || dim>=rndVarsCond.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.CondProbDistribution.java: Dimension of the condition random variables is out of bound!");
		}
		else {
			return rndVarsCond[dim];
		}
	}

	/**
	 * s the number of prior random variables involved.
	 * 
	 * @return	number of prior variables
	 */
	public int getNumOfPriorVariables () {
		return rndVarsPrior.length;
	}

	/**
	 * This method gets the number of condition random variables involved.
	 * 
	 * @return	number of condition variables
	 */
	public int getNumOfCondVariables () {
		return rndVarsCond.length;
	}

	/**
	 * This method gets the number of entries in this conditional probability distribution table.
	 * 
	 * @return	number of entries
	 */
	public int getNumOfEntries () {
		return probEntries.getNumOfEntries();
	}

	/**
	 * This method tests whether the given random variable is involved in the 
	 * "prior" part of this conditional probability distribution.
	 * 
	 * @param vname	variable name
	 * @return	true: variable involved
	 * 			false: else
	 */
	public boolean containsPriorVariable (String vname) {
		return pvname_dim_map.containsKey(vname);
	}

	/**
	 * This method tests whether the given random variable is involved in the 
	 * "condition" part of this conditional probability distribution.
	 * 
	 * @param vname	variable name
	 * @return	true: variable involved
	 * 			false: else
	 */
	public boolean containsCondVariable (String vname) {
		return cvname_dim_map.containsKey(vname);
	}
	
	/**
	 * Puts one conditional probability value to the specified 'indices' in the 
	 * conditional probability distribution table.
	 * 
	 * @param indices	variable index
	 * @param pv	probability value
	 */
	public void addCondProbEntry (int[] indices, double pv) {
		probEntries.putElement(indices,pv);
	}

	/**
	 * This method gets one conditional probability value from the specified 'indices' 
	 * in the conditional probability distribution table.
	 * 
	 * @param indices	variable index
	 * @return	entry
	 */
	public double getCondProbEntry (int[] indices) {
		return probEntries.getElement(indices);
	}
			
	/**
	 * Takes an int which represents the position in the distribution entries and 
	 * computes the corresponding 'indices' in the multi-dimensional array.
	 * 
	 * @param offset	entry position
	 * @return	entry index
	 */
	public int[] getIndices (int offset) {
		return probEntries.lookupIndices(offset);
	}
	
	/**
	 * Override toString method.
	 * Returns a string representation of the conditional probability distribution.
	 */
	@Override
	public String toString () {
		String s = "";
		s = s + "\n*************** Begin: Conditional Probability Distribution ***************\n";
		s = s + "Priors\n";
		for (int i=0; i<rndVarsPrior.length; i++) 
			s = s + rndVarsPrior[i].toString();
		s = s + "\nConditions\n";
		for (int i=0; i<rndVarsCond.length; i++) 
			s = s + rndVarsCond[i].toString();
		s = s + "\n";
		s = s + probEntries.toString();
		s = s + "\n*************** End:   Conditional Probability Distribution ***************\n";
		return s;
	}

}
