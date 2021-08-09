/**
 * EIPFP.java
 *
 * @author Zhongli Ding(original)
 * 
 * Created on 2005-3-15
 * Modified on Sept. 09, 2005, v0.4
 * Modified on Aut. 11, 2008
 * 
 * See paper:
 * 	Modifying Bayesian Networks by Probability Constraints
 * 	Yun Peng, Zhongli Ding
 * 	UAI 2005
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import java.util.*;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;



import norsys.netica.*;

/**
 * This class implements the 'EIPFP' algorithm from our UAI-05 paper entitled "Modifying Bayesian Networks by Probability Constraints".
 * 
 * See paper:
 * 	Modifying Bayesian Networks by Probability Constraints
 * 	Yun Peng, Zhongli Ding
 * 	UAI 2005
 */
public class EIPFP{
	Environ env;
	Net net;
	ProbDistribution[] cons;
	long timeElapsed;
	int loopsUsed;
	
	/**
	 * Constructor - 1: 
	 * Given an initial BN and a set of constraints (can be either joint or conditional).
	 * 
	 * @param n		Bayesian net
	 * @param rs	constraints 
	 */
	public EIPFP (Net n, ProbDistribution[] rs) {
		if (n == null || rs == null || rs.length == 0) {
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Wrong BN or constraints provided!");
		}
		else {
			net = n;
			cons = new ProbDistribution[rs.length];
			for (int i = 0; i<rs.length; i++)
				cons[i] = rs[i];
			timeElapsed = 0;
			loopsUsed = 0;
		}
	}

	/**
	 * Constructor - 2:
	 * Given an initial BN file name and a set of constraints (can be either joint or conditional).
	 * 
	 * @param fname	file name
	 * @param rs	constraints
	 */
	public EIPFP (String fname, ProbDistribution[] rs) {
		if (fname == null || fname.equals("") || rs == null || rs.length == 0) { 
			throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Wrong BN file name or constraints provided!");
		}
		else {
			try {
				env = new Environ("+PengY/UMarylandBC/120,310-2-A/27700");
				net = new Net(new Streamer(fname));
				cons = new ProbDistribution[rs.length];
				for (int i = 0; i<rs.length; i++)
					cons[i] = rs[i];
				timeElapsed = 0;
				loopsUsed = 0;
			}
			catch (Exception ex) {
				System.out.println("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Invalid BN provided!");
				ex.printStackTrace();
			}		
		}
	}

	/**
	 * Implements the main idea of the E-IPFP algorithm.
	 * 
     * @param maxLoops if this procedure does not converge in 'maxLoops' number of loops, we think it will not converge at all. 
   	 * @param threshold if the difference between two iterations is smaller than the threshold we think the algorithm converges.
	 */
	public void run (int maxLoops, double threshold) {
		try {
			// trace the time
			Date startDate = new Date();
			long startTime = startDate.getTime();
			/* begin of procedure */
			double totalVariance = 1.0;
			int counter = 0;
			boolean success = true;
			do {
				if (counter>maxLoops){
					success = false;
					break;
				}
				// 1. convert the Bayesian net into a joint probability distribution
				BN2JPD convertor_b2j = new BN2JPD(net);
				JointProbDistribution jpd = convertor_b2j.getJPD();
				JointProbDistribution jpd_orig = new JointProbDistribution(jpd);				
				// 2. run the CIPFP algorithm over all constraints one by one, once
				for (int j=0; j<cons.length; j++) { //iterate over all the given constraints one by one, once
					String distributionType = cons[j].getDistributionType();
					if (distributionType.equals("JPD")) { 		// constraint with form R(Si)
						JointProbDistribution thisR = (JointProbDistribution) cons[j];
						IPFPOneR one_step = new IPFPOneR(jpd,thisR);
						one_step.computation();
						jpd = one_step.getDistribution();
					}
					else if (distributionType.equals("CPD")) {	//constraint with form R(Si|Li)
						CondProbDistribution thisR = (CondProbDistribution) cons[j];
						CIPFPOneR one_step = new CIPFPOneR(jpd,thisR);
						one_step.computation();
						jpd = one_step.getDistribution();
					}
					else { //otherwise
						throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Wrong constraints provided!");
					}
				} 		
				// 3. revise the CPT of the original BN, based on the new distribution obtained, say, P1
				JPD2BN j2bconvertor = new JPD2BN(jpd,net);
				net = j2bconvertor.getNet();
				// 4. convert this revised Bayesian net into a further new distribution, say, P2
				BN2JPD b2jconvertor = new BN2JPD(net);
				JointProbDistribution jpd_new = b2jconvertor.getJPD();
				// 5. compute the total variance between P1 and P2 to judge whether it converges
				TotalVariance obj = new TotalVariance(jpd_orig,jpd_new);
				totalVariance = obj.getTotalVariance();
				// 6. increase the counter 
				counter++;
			}
			while (totalVariance>threshold);
			/* end of procedure */
			// trace the time
			Date endDate = new Date();
			long endTime = endDate.getTime();
			timeElapsed = endTime - startTime;		
			loopsUsed = counter;
			if (!success)
				throw new IllegalArgumentException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Failure - The set of constraints will not be converged in " + maxLoops + " loops!");
		}
		catch (Exception rex) {
			System.out.println("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Invalid BN provided!");
			rex.printStackTrace();
		}
	}

	/**
	 * Returns the execution time of running the algorithm. 
	 * 
	 * @return	algorithm execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}
	
	/**
	 * Returns the number of iteration steps of running the algorithm.
	 * 
	 * @return	execution iteration number
	 */
	public int getExecLoops() {
		return loopsUsed;
	}
		
	/**
	 * Saves the revised BBN obtained into a file.
	 * 
	 * @param fname	file name
	 */
	public void saveNet (String fname) {
		try {
			if (fname == null || fname.equals("")) {
				throw new NullPointerException("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Please give a valid BN file name to save with!");
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
			System.out.println("Class umbc.ebiquity.BayesOWL.coreAlgorithms.EIPFP.java: Error in saving the BN to file!");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the revised BBN obtained.
	 * 
	 * @return	Bayesian Net
	 */
	public Net getNet () {
		return net;
	}
	
}