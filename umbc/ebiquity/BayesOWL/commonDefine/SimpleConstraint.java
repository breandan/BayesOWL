/**
 * SimpleConstraint.java
 *
 * @author Zhongli Ding (original) 
 * 
 * Created on Nov. 22, 2004
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 * 
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * This class is an abstract class.
 * Super class of "SimpleMarginalConstraint" and "SimpleConditionalConstraint".
 *
 */
public abstract class SimpleConstraint {
	
	String constraintType;
	
	/**
	 * Constructor.
	 * 
	 * @param ctype	constraint type
	 */
	public SimpleConstraint(String ctype) {
		setConstraintType(ctype);
	}
	
	/**
	 * This method sets the constraint type of this simple constraint.
	 * 
	 * @param ctype	constraint type
	 */
	public void setConstraintType(String ctype) {
		if (ctype == null || ctype.equals("")) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.SDIPFP.SimpleConstraint.java: A constraint's type can not be an empty string!");
		}
		else {
			constraintType = ctype;	
		}
	}
	
	/**
	 * This method returns the constraint type of this constraint.
	 * 
	 * @return	constraint type
	 */
	public String getConstraintType() {
		return constraintType;
	}

}
