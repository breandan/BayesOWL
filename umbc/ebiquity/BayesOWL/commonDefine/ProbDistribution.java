/**
 * ProbDistribution.java
 *
 * @author Zhongli Ding(original)
 * 
 * Created on Dec. 08, 2004
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aug. 05, 2008, comments added
 */
package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * * 
 * ProbDistribution is an abstract class.
 * Super class of "JointProbDistribution" and "CondProbDistribution".
 *
 */
public abstract class ProbDistribution {
	String distributionType; // distribution type

	/**
	 * Constructor.
	 * Constructs a probability distribution with the specific type, i.e., joint (JPD) or conditional (CPD).
	 * @param dtype	is the type of a distribution
	 */
	public ProbDistribution (String dtype) {
		setDistributionType(dtype);
	}
	
	/**
	 * This method sets the distribution type of this probability distribution.
	 * @param dtype	distribution type
	 */
	public void setDistributionType (String dtype) {
		if (dtype == null || dtype.equals("")) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.ProbDistribution.java: A distribution's type can not be an empty string!");
		}
		else {
			distributionType = dtype;
		}
	}
	
	/**
	 * This method returns the distribution type of this probability distribution.
	 * @return	distribution type
	 */
	public String getDistributionType () {
		return distributionType;
	}	
}