/**
 * Constraint.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Oct 17, 2005, v0.4
 * Modified on Aug. 09, 2008
 */

package umbc.ebiquity.BayesOWL.commonDefine;

/**
 * Constraint is an abstract class.
 * Super class of "MarginalConstraint" and "ConditionalConstraint".
 */
public abstract class Constraint {	
	String constraintType;
	
	/**
	 * Constructor.
	 * Constructs a probability constraint with the specific type, i.e., marginal or conditional.
	 * 
	 * @param ctype	constraint type
	 */
	public Constraint (String ctype) {
		setConstraintType(ctype);
	}
	
	/**
	 * Sets the constraint type of this constraint.
	 * 
	 * @param ctype	constraint type
	 */
	public void setConstraintType (String ctype) {
		if (ctype == null || ctype.equals("")) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.ExtendedIPFP.DIPFP.Constraint.java: A constraint's type can not be an empty string!");
		}
		else {
			constraintType = ctype;	
		}
	}
	
	/**
	 * Gets the constraint type of this constraint.
	 * 
	 * @return	constraint type
	 */
	public String getConstraintType () {
		return constraintType;
	}

}
