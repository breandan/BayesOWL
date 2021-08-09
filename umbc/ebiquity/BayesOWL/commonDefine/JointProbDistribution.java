/**
 * JointProbDistribution.java
 *
 * @author Zhongli Ding(original)
 * 
 * Created on Dec. 05, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 05, 2008, comments added 
 */

package umbc.ebiquity.BayesOWL.commonDefine;

import java.util.*;

/**
 * This class implements a full joint probability distribution table, which includes:<br>
 * 	(1) An array of n random variables {vi}, i = 1 to n, each variable vi has di states<br>
 * 	(2) A n-dimensional array with size "d1 x d2 x ... x dn" to store all the probability values<br>
 * e.g.<br>
 * given two binary variables A, B, with P(A,B)={0.1, 0.3, 0.4, 0.2}, True: 0, False: 1<br>
 * 	rndVars[0] = A, rndVars[1] = B <br>
 * 	probEntries[0][0]= 0.1		P(A=True,B=True)<br>
 *	probEntries[0][1]= 0.3		P(A=True,B=False)<br>
 *	probEntries[1][0]= 0.4		P(A=False,B=True)<br>
 *	probEntries[1][1]= 0.2		P(A=False,B=False)<br>
 *	            |  |<br>
 *	            A  B<br>
 */
public class JointProbDistribution extends ProbDistribution {

	RandomVariable[] rndVars;			//an array of random variables related to this joint probability distribution
	MultiDimensionalArray probEntries;	//a multi-dimensional array to store the probability values, in the same order as the random variable array
	Hashtable vname_dim_map;			//a mapping between random variable name and its index (or dimension) in the random variable array 

	/**
	 * Constructor.
	 * Constructs a new joint probability distribution table for a given set of random variables.
	 * Allocates storage spaces for P(X), and set all initial probability values as zero.
	 * 
	 * @param argus	array of random variables
	 */
	public JointProbDistribution (RandomVariable[] argus) {
		super("JPD");
		if (argus == null || argus.length == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: No random variables specified!");
		}
		else {
			rndVars = new RandomVariable[argus.length];
			vname_dim_map = new Hashtable();
			int[] dims = new int[argus.length];
			for (int i=0; i<argus.length; i++) {
				rndVars[i] = argus[i];
				vname_dim_map.put(argus[i].getName(), new Integer(i));
				dims[i] = argus[i].getNumOfStates();		
			}
			probEntries = new MultiDimensionalArray(dims);
		}
	}

	/**
	 * Constructor.
	 * Constructs a new joint probability distribution table for a given JPD.
	 * 
	 * @param jpd	joint probability distribution
	 */
	public JointProbDistribution(JointProbDistribution jpd) {
		super("JPD");
		if (jpd == null || jpd.getNumOfVariables() == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: No joint probability distribution specified!");
		}
		else {
			rndVars = new RandomVariable[jpd.getNumOfVariables()];
			vname_dim_map = new Hashtable();
			int[] dims = new int[jpd.getNumOfVariables()];
			for (int i=0; i<jpd.getNumOfVariables(); i++) {
				rndVars[i] = new RandomVariable(jpd.getVariable(i));
				vname_dim_map.put(rndVars[i].getName(), new Integer(i));
				dims[i] = rndVars[i].getNumOfStates();		
			}
			probEntries = new MultiDimensionalArray(dims);
			for (int i=0; i<jpd.getNumOfEntries(); i++)
				probEntries.putElement(jpd.getIndices(i),jpd.getProbEntry(jpd.getIndices(i)));
		}
	}
	
	/**
	 * This method returns the random variable in the specified dimension.
	 * 
	 * @param dim	dimension of a random variable wanted
	 * @return	random variable
	 */
	public RandomVariable getVariable (int dim) {
		if (dim <0 || dim >= rndVars.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: Dimension of random variable is out of bound!");
		}
		else {
			return rndVars[dim];
		}
	}

	/**
	 * This method returns the index of a given random variable in the array "rndVars", that is, 
	 * which dimension this random variable corresponding with in the multi-dimensional array.
	 * 
	 * @param vname	name of the random variable
	 * @return	dimension
	 */
	public int getDimension(String vname) {
		Integer obj = (Integer)vname_dim_map.get(vname);
		if (obj == null) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: Wrong random variable name provided!");
		}
		else {
			return obj.intValue();
		}
	}

	/**
	 * This method tests whether the given random variable is involved in this joint probability distribution.
	 * 
	 * @param vname	random variable name
	 * @return	true: variable is involved in this JPD
	 * 			false: other
	 */
	public boolean containsVariable (String vname) {
		return vname_dim_map.containsKey(vname);
	}

	/**
	 * This method gets the number of variables involved.
	 * 
	 * @return	number of variables
	 */
	public int getNumOfVariables () {
		return rndVars.length;
	}

	/**
	 * This method returns the number of entries of this joint probability distribution table.
	 * 
	 * @return number of entries
	 */
	public int getNumOfEntries () {
		return probEntries.getNumOfEntries();
	}

	/**
	 * This method takes an integer which represents the position in the distribution entries 
	 * and computes the corresponding 'indices' in the multi-dimensional array.
	 * 
	 * @param offset	position of entry(integer)
	 * @return	entry index
	 */
	public int[] getIndices (int offset) {
		return probEntries.lookupIndices(offset);
	}
	
	/**
	 * This method puts one probability value to the the specified 'indices' in the JPD table. 
	 * 
	 * @param indices	entry index
	 * @param pv	entry value
	 */
	public void addProbEntry (int[] indices, double pv) {
		probEntries.putElement(indices,pv);
	}

	/**
	 * This method returns one probability value from the specified 'indices' in the JPD table.
	 * 
	 * @param indices	entry index
	 * @return	entry value
	 */
	public double getProbEntry (int[] indices) {
		return probEntries.getElement(indices);
	}

	/**
	 * This method checks whether this is a valid JPD table.
	 * 
	 * @return	true: is a valid JPD
	 * 			false: other
	 */
	public boolean isValid () {
		boolean valid = true;
		for (int i = 0; i < probEntries.getNumOfEntries(); i++) {
			double value = probEntries.getElement(getIndices(i));
			if (value<0.0 || value>1.0) {
				System.out.println("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: Any probability value should be in [0.0,1.0]!");
				valid = false;
				break;
			}
		}
		if (valid) {
			double s = sum();
			if (s>1.01 || s<0.99)
				valid = false;
		}
		return valid;
	}
	
	/**
	 * This method returns the sum of all the probability entries in the JPD table.
	 * 
	 * @return
	 */
	private double sum () {
		return probEntries.sum();
	}
		
	/**
	 * Override the toString method. 
	 */
	public String toString () {
		String s = "";
		s = s + "\n*************** Begin: Joint Probability Distribution ***************\n";
		for (int i = 0; i < rndVars.length; i++) 
			s = s + rndVars[i].toString();
		s = s + "\n";
		s = s + probEntries.toString();
		s = s + "\n*************** End:   Joint Probability Distribution ***************\n";
		return s;
	}
	
	/**
	 * This method gets the marginal probability distribution of a set of given random variables.
	 * Assume the given random variables are all DIFFERENT, no duplicates, and involved in the full distribution.
	 * 
	 * @param randVariables	variables involved in marginal probability distribution
	 * @return	marginal distribution
	 */	
	public JointProbDistribution getMarginalDist(RandomVariable[] randVariables){
		if (randVariables == null || randVariables.length>rndVars.length) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: Wrong random variables provided!");
		}
		else {
			JointProbDistribution marginalDist = new JointProbDistribution(randVariables);
			int numOfMargVars = randVariables.length;
			String[] margVarNames = new String[numOfMargVars];  //collect the involved variable names
			int[] margVarDims = new int[numOfMargVars];         //collect the involved variable dimensions in the full distribution
			for (int i=0; i<numOfMargVars; i++) {
				margVarNames[i] = randVariables[i].getName();
				margVarDims[i] = this.getDimension(margVarNames[i]);
			}
			for (int i=0; i<this.getNumOfEntries(); i++) {
				int[] indices = new int[this.getNumOfVariables()];
				indices = this.getIndices(i);
				int[] indicesInMargDist = new int[numOfMargVars];
				for (int j=0; j<numOfMargVars; j++) {
					indicesInMargDist[j] = indices[margVarDims[j]];
				}
				double probValue = marginalDist.getProbEntry(indicesInMargDist);
				probValue = probValue + this.getProbEntry(indices);
				marginalDist.addProbEntry(indicesInMargDist,probValue);				
			}
			return marginalDist;
		}
	}

	/**
	 * This methods gets the conditional probability distribution of a set of given prior and condition random variables.
	 * Assume the given prior or condition random variables are all DIFFERENT, no duplicates, and involved in the full distribution.
	 * Assume the priors and conditions are disjoint with each other. 
	 * 
	 * @param priorVars	prior variables
	 * @param condVars	variables involved in CPT
	 * @return	conditional probability distribution
	 */
	public CondProbDistribution getMarginalCondDist (RandomVariable[] priorVars, RandomVariable[] condVars) {
		if (priorVars == null || priorVars.length == 0)  {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: No prior random variables specified!");
		}
		else if (condVars == null || condVars.length == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.JointProbDistribution.java: No condition random variables specified!");
		}
		else {
			CondProbDistribution marginalCondDist = new CondProbDistribution(priorVars,condVars);
			RandomVariable[] bothVars = new RandomVariable[priorVars.length+condVars.length];
			for (int i=0; i<condVars.length; i++){
				bothVars[i] = condVars[i];
			}
			for (int i=0; i<priorVars.length; i++) {
				bothVars[condVars.length+i] = priorVars[i];
			}
			JointProbDistribution marginal1 = this.getMarginalDist(bothVars);
			JointProbDistribution marginal2 = this.getMarginalDist(condVars);
			for (int i=0; i<marginalCondDist.getNumOfEntries(); i++) {
				int[] indices = marginalCondDist.getIndices(i);
				int[] indicesCond = new int[condVars.length];
				for (int j=0; j<condVars.length; j++) {
					indicesCond[j] = indices[j];
				}
				double pv_both = marginal1.getProbEntry(indices);
				double pv_cond = marginal2.getProbEntry(indicesCond);
				if (pv_cond > 0.0 && pv_both > 0.0) {
					double pv_marginal = pv_both / pv_cond;
					marginalCondDist.addCondProbEntry(indices,pv_marginal);
				}
			}
			return marginalCondDist;
		}
	}
	
	
	/**
	 * This method prints all entries partitioned by a " " 
	 * 
	 * @author shenyong zhang Nov. 30 2007
	 * @category for experiment analyze use 
	 * @return just entry data
	 */
	private String probEntriestoString() {
		String s = "";
		s = s + probEntries.toString();
		return s;
	}
	
	/**
	 * This method gets variables involved in JPD
	 *
	 * @author shenyong
	 * Created Nov. 12 2007
	 * @return random variables
	 */
	public RandomVariable[] getRandomVariable()
	{
		return rndVars;
	}
}