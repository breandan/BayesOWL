/**
 * DIPFP.java<br>
 * <br>
 * @author Zhongli Ding (original)<br>
 * <br>
 * Created on Oct 19, 2005, v0.4<br>
 * Modified on Aug. 11, 2008<br>
 * Modified on Oct. 13, 2008 (inconsistent situation)<br>
 * <br>
 * See classes DIPFPConditionalOneR.java and DIPFPMarginalOneR.java<br>
 */

package umbc.ebiquity.BayesOWL.coreAlgorithms;

import umbc.ebiquity.BayesOWL.commonDefine.*;
import umbc.ebiquity.BayesOWL.commonMethod.*;

import norsys.netica.*;

/**
 * This class implements the 'DIPFP' algorithm from our UAI-05 paper entitled "Modifying Bayesian Networks by Probability Constraints".<br>
 * For experimental purpose, we have eight(8) variations of implementation, please refer 
 * to "DIPFPMarginalOneR.java" and "DIPFPConditionalOneR.java" for details.<br>
 */
public class DIPFP {
	Net net;
	Constraint[] constraints;
	int choice;
	long timeElapsed;
	int loopsUsed;

	/**
	 * Constructor - 1: 
	 * Given an initial BN, a set of constraints (can be either marginal or conditional), and a choice of implementation variation.
	 * 
	 * @param bbn	Bayesian Net
	 * @param r		Constraint
	 */
//	public DIPFP(Net bbn, Constraint[] r, int variation) {
//		if (bbn == null || r == null || r.length == 0 || variation>8 || variation<1) {
	public DIPFP(Net bbn, Constraint[] r) {
		if (bbn == null || r == null || r.length == 0) {
			throw new IllegalArgumentException("Wrong BN or Constraints or Implementation Choice provided!");
		}
		else {
			try{
				net = bbn;
				constraints = new Constraint[r.length];
				for (int i = 0; i < r.length; i++) {
					constraints[i] = r[i];
				}
				//choice = variation;	// 
				choice = 2;
				timeElapsed = 0;
				loopsUsed = 0;
			}catch(Exception ne){
				System.out.println("Invalid BBN provided!");
				ne.printStackTrace();
			}
		}		
	}

	/**
	 * Constructor - 2: 
	 * Given an initial BN file name, a set of constraints (can be either marginal or conditional), and a choice of implementation variation.
	 * 
	 * @param fname	file name
	 * @param r		constraint
	 */
//	public DIPFP (String fname, Constraint[] r, int variation) {
//		if (fname == null || fname.equals("") || r == null || r.length == 0 || variation>8 || variation<1) { 
	public DIPFP (String fname, Constraint[] r) {
		if (fname == null || fname.equals("") || r == null || r.length == 0) { 			
			throw new IllegalArgumentException("Wrong BBN file name or Constraints or Implementation Choice provided!");
		}
		else {
			try {
				net = new Net(new Streamer(fname));
				constraints = new Constraint[r.length];
				for (int i = 0; i < r.length; i++)
					constraints[i] = r[i];
				//choice = variation;	//
				choice = 2;
				timeElapsed = 0;
				loopsUsed = 0;
			}
			catch (NeticaException ex) {
				System.out.println("Invalid BBN provided!");
				ex.printStackTrace();
			}		
		}
	}

	/**
	 * Implements the main idea of the D-IPFP algorithm.	 * 
     * If this procedure does not converge in 'maxLoops' number of loops, we think it will not converge at all. 
   	 * If the difference between two iterations is smaller than the threshold we think the algorithm converges.
   	 * 
	 * @param maxLoops	max iteration loops
	 * @param threshold	threshold
	 */
	public void run (int maxLoops, double threshold) {
		try {
			/* begin of procedure */
			net.compile();
			int counter = 0;
			boolean success = true;
			//double diff_sum = 1.0;
			double tv_prev = 0.0, tv_abs = 1.0;
			double tv = 1.0;
			
			//step 1	(added by Shenyong, Oct. 13, 2008)
			do {
				//System.out.println("Start: " + counter);
				if (counter > maxLoops){
					success = false;
					break;
				}

				// 1. remember down the net before this iteration of updating
				net.setAutoUpdate(1);
				net.write(new Streamer("temp.dne"));
				Net net_orig = new Net(new Streamer("temp.dne"));
				// 2. process each constraint one by one for this iteration
				for (int j = 0; j < constraints.length; j++) { 
					String constraintType = constraints[j].getConstraintType();
					//constraint with form R(Y), Y={C1, C2, ..., Cn}
					if (constraintType.equals("marginal")) { 
						MarginalConstraint thisR = (MarginalConstraint) constraints[j];
						DIPFPMarginalOneR one_step = new DIPFPMarginalOneR(net,thisR,choice);
						one_step.computation();
						net = one_step.getNet();
						timeElapsed = timeElapsed + one_step.getExecTime();
					}
					//constraint with form R(A|B), A and B are disjoint, A={C1,C2,...,Cn), B={P1, P2, ..., Pm}
					else if (constraintType.equals("conditional")) {
						ConditionalConstraint thisR = (ConditionalConstraint) constraints[j];
						DIPFPConditionalOneR one_step = new DIPFPConditionalOneR(net,thisR,choice);
						one_step.computation();
						net = one_step.getNet();						
						timeElapsed = timeElapsed + one_step.getExecTime();
					}
					//otherwise
					else {
						throw new IllegalArgumentException("Wrong constraints provided!");
					}
				}
				//end-for-j
				// 3. compute the difference between old BBN and modified BBN after this interation				
				DiffBN diff = new DiffBN(net_orig, net, null);
				//diff_sum = diff.getDifference();
				tv = diff.getTotalVariance();
				
				tv_abs = Math.abs(tv - tv_prev);
				tv_prev = tv;
				// 4. increase the counter 
				counter++;
				
/**				
				//set marginal constraint
				//@add Dec. 04 2008, syzhang
				JointProbDistribution jpdBN = new BN2JPD(net).getJPD();
				String[] varNames = constraint.getVariableNames();
				RandomVariable[] rvVar = new RandomVariable[varNames.length];
				for(int i = 0; i < varNames.length; i++){
					Node tempNode = net.getNode(varNames[i]);
					int nodeNumStates = tempNode.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j = 0; j < nodeNumStates; j++)
						stateNames[j] = tempNode.state(j).getName();
					rvVar[i] = new RandomVariable(varNames[i], stateNames);
				}
				constraint_after = new LocalMarginalConstraint(jpdBN.getMarginalDist(rvVar), ((LocalMarginalConstraint)constraint).getConceptName());
				constraint_after.setConstraintType(constraint.getConstraintType());
				constraint_after.setScopeType(constraint.getScopeType());
				//end add

				//set conditional constraint
				//@add Dec. 04 2008, syzhang
				JointProbDistribution jpdOfY = new BN2JPD(net).getJPD();
				String[] priorNames = constraint.getPriorVariableNames();
				String[] condNames = constraint.getCondVariableNames();
				RandomVariable[] rvPrior = new RandomVariable[priorNames.length];
				RandomVariable[] rvCond = new RandomVariable[condNames.length];
				for(int i = 0; i < priorNames.length; i++){
					Node node = net.getNode(priorNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j = 0; j < nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					rvPrior[i] = new RandomVariable(priorNames[i], stateNames);
				}
				for(int i = 0; i < condNames.length; i++){
					Node node = net.getNode(condNames[i]);
					int nodeNumStates = node.getNumStates();
					String[] stateNames = new String[nodeNumStates];
					for (int j = 0; j < nodeNumStates; j++)
						stateNames[j] = node.state(j).getName();
					rvCond[i] = new RandomVariable(condNames[i], stateNames);
				}
				constraint_after = new NonlocalConditionalConstraint(jpdOfY.getMarginalCondDist(rvPrior, rvCond));
				constraint_after.setConstraintType(constraint.getConstraintType());
				constraint_after.setScopeType(constraint.getScopeType());
				//end add
*/
			}
			//while (diff_sum>threshold);
			while (tv_abs > threshold);
			/* end of procedure */
			loopsUsed = counter;
			
			//step 2	(added by Shenyong, Dec. 04, 2008)
			
			if (!success)
				throw new IllegalArgumentException("Failure - The set of constraints will not be converged in " + maxLoops + " loops!");
		}
		catch (NeticaException rex) {
			System.out.println("Invalid BBN provided!");
			rex.printStackTrace();
		}
	}

	/**
	 * Gets the execution time of running the algorithm.
	 * 
	 * @return	algorithm execution time
	 */
	public long getExecTime() {
		return timeElapsed;
	}
	
	/**
	 * Gets the number of iteration steps of running the algorithm.
	 * 
	 * @return	iteration used
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
				throw new NullPointerException("Invalid BBN file name");
			}
			else {
				net.setAutoUpdate(1);
				net.write(new Streamer(fname));
				net.finalize();   // not strictly necessary, but a good habit
			}
		}
		catch (Exception e) {
			System.out.println("Error in saving the BBN to file!");
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