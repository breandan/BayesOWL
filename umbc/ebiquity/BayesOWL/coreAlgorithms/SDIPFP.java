/**
 * SDIPFP.java
 * 
 * @author Zhongli Ding (original)
 * 
 * Created on Mar. 01, 2005
 * Modified on Oct. 19, 2005, v0.4
 * Modified on Aug. 12, 2008
 *
 * See paper:
 *  A Bayesian Approach to Uncertainty Modeling in OWL Ontology
 *  Zhongli Ding, Yun Peng, Ron Pan
 *  AISTA 2004
 *  
 *  Modifying Bayesian Networks by Probability Constraints
 * 	Yun Peng, Zhongli Ding
 * 	UAI 2005
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;



import norsys.netica.*;

/**
 * This class implements the 'Simplified D-IPFP' algorithm based on the AISTA-2004 paper, which 
 * is a special case of the 'D-IPFP' algorithm in the UAI paper, only the simplest local 
 * constraints are allowed.<br>
 *<br>
 * See paper:<br>
 *  A Bayesian Approach to Uncertainty Modeling in OWL Ontology<br>
 *  Zhongli Ding, Yun Peng, Ron Pan<br>
 *  AISTA 2004  <br>
 *  Modifying Bayesian Networks by Probability Constraints<br>
 * 	Yun Peng, Zhongli Ding<br>
 * 	UAI 2005<br>
 */
public class SDIPFP {
	Environ env;
	Net net;
	SimpleConstraint[] constraints;
	HardEvidence[] hardEvidences;
	long timeElapsed;
	int loopsUsed;
	
	/**
	 * Constructor - 1: 
	 * Given the BBN, a set of simple constraints, and a set of hard evidences to be specified in the BBN.
	 * 
	 * @param n:	Bayesian Net
	 * @param r:	simple constraint
	 * @param e:	hard evidence
	 */
	public SDIPFP (Net n, SimpleConstraint[] r, HardEvidence[] e) {
		if (n == null || r == null || r.length == 0) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFP.java: Wrong BBN or simple constraints provided!");
		}
		else {
			net = n;
			constraints = new SimpleConstraint[r.length];
			for (int i = 0; i<r.length; i++) {
				constraints[i] = r[i];
			}
			if (e != null && e.length>0) {
				hardEvidences = new HardEvidence[e.length];
				for (int i=0; i<e.length; i++) 
					hardEvidences[i] = e[i];
			}
			timeElapsed = 0;
			loopsUsed = 0;
		}
	}
	
	/**
	 * Constructor - 2: 
	 * Given the BBN file name, a set of simple constraints, and a set of hard evidences to be specified in the BBN.
	 * 
	 * @param fname:	BN file name
	 * @param r:	simple constraint
	 * @param e:	hard evidence
	 */
	public SDIPFP (String fname, SimpleConstraint[] r, HardEvidence[] e) {
		if (fname == null || fname.equals("") || r == null || r.length == 0) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFP.java: Wrong BBN or simple constraints provided!");
		}
		else {
			try {
				env = new Environ("+PengY/UMarylandBC/120,310-2-A/27700");
				net = new Net(new Streamer(fname));
				constraints = new SimpleConstraint[r.length];
				for (int i = 0; i<r.length; i++) {
					constraints[i] = r[i];
				}
				if (e != null && e.length>0) {
					hardEvidences = new HardEvidence[e.length];
					for (int i=0; i<e.length; i++) 
						hardEvidences[i] = e[i];
				}
				timeElapsed = 0;
				loopsUsed = 0;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * The loop process of simplified de-composed iterative proportional fitting procedure (SDIPFP).
	 * 
	 * @param maxLoops if this procedure does not converge in 'maxLoops' number of loops, we think it will not converge at all. 
	 * @param threshold if the difference two iteration is smaller than the threshold we think the algorithm converges.
	 */
	public void run (int maxLoops, double threshold) {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			/* begin of procedure */			
			net.compile();
			int counter = 0;
			boolean success = true;
			double diff_sum = 1.0;
			do {
				if (counter > maxLoops){
					success = false;
					break;
				}
				net.setAutoUpdate(1);
				net.write(new Streamer("temp.dne"));
				Net net_orig = new Net(new Streamer("temp.dne"));
				for (int j=0; j<constraints.length; j++) { //iterate over all the given constraints one by one
					String constraintType = constraints[j].getConstraintType();
					if (constraintType.equals("marginal")) { // constraint with form R(V)
						SimpleMarginalConstraint thisR = (SimpleMarginalConstraint) constraints[j];
						SDIPFPMarginalOneR one_step = new SDIPFPMarginalOneR(net,thisR,hardEvidences);
						one_step.computation();
						net = one_step.getNet();
					}
					else if (constraintType.equals("conditional")) { //constraint with form R(V|P1,P2,...)
						SimpleConditionalConstraint thisR = (SimpleConditionalConstraint) constraints[j];
						SDIPFPConditionalOneR one_step = new SDIPFPConditionalOneR(net,thisR,hardEvidences);
						one_step.computation();
						net = one_step.getNet();						
					}
					else { //otherwise
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFP.java: Wrong simple constraints provided!");
					}
				} // end-for-j
				counter++;
				DiffBN diff = new DiffBN(net_orig,net,hardEvidences);
				diff_sum = diff.getDifference();
			}
			while (diff_sum>threshold);
			/* end of process */
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;
			loopsUsed = counter;
			if (!success)
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFP.java: Failure - The set of simple constraints will not be converged in " + maxLoops + " loops!");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the execution time of running the algorithm.
	 * 
	 * @return	algorithm execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}
	
	/**
	 * This method returns the number of iteration steps of running the algorithm.
	 * 
	 * @return	execution iteration
	 */
	public int getExecLoops() {
		return loopsUsed;
	}
		
	/**
	 * This method saves the revised BBN obtained into a file.
	 * 
	 * @param fname	BN file name
	 */
	public void saveNet (String fname) {
		try {
			if (fname == null || fname.equals("")) {
				throw new NullPointerException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.SDIPFP.java: Please give a valid file name to save with!");
			}
			else {
				net.setAutoUpdate(1);
				net.write(new Streamer(fname));
				net.finalize();   // not strictly necessary, but a good habit
				if (!(env==null))
					env.finalize();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}
	
}
