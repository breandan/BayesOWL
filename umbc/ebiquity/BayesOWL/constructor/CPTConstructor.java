/**
 * Created on Sept. 15, 2008<br>
 * Modified on Nov. 24, 2008<br>
 * @author Shenyong Zhang
 */

package umbc.ebiquity.BayesOWL.constructor;

import norsys.netica.*;
import umbc.ebiquity.BayesOWL.coreAlgorithms.DIPFP;
import umbc.ebiquity.BayesOWL.commonDefine.Constraint;

/**
 * CPTConstructor implements BayesOWL's Conditional Probability Table Constructor.<br>
 * It takes probability constraints and a target BN as inputs.<br>
 * To have input constraints, go to see class
 * umbc.ebiquity.BayesOWL.commonDefine.Constraint.java 
 *
 */
public class CPTConstructor {
	Net net;
	Constraint[] constraint;
	
	/**
	 * Constructor.
	 * 
	 * @param oNet object BN
	 * @param oConstraint
	 */
	public CPTConstructor(Net oNet, Constraint[] oConstraint){
		if(oNet == null){
			System.out.println("BayesOWL Reasoner: Wrong BN Input!!");
		}else if(oConstraint.length < 1){
			System.out.println("BayesOWL Reasoner: Wrong Constraint Input!!");
		}else{
			net = oNet;
			constraint = oConstraint;
		}
	}
	
	/**
	 * Run reasoner.
	 * 
	 * @param maxLoops	algorithm max iteration steps
	 * @param threshold
	 */
	public void run(int maxLoops, double threshold){
		if(threshold < 0 || threshold > 1.0){
			System.out.println("BayesOWL Reasoner: Wrong threshold input!!");
		}else{
			DIPFP dIPFP = new DIPFP(net, constraint);
			dIPFP.run(maxLoops, threshold);
		}		
	}
	
	/**
	 * Get result net.
	 * 
	 * @return	BN
	 */
	public Net getResultNet(){
		return net;
	}
	
	/**
	 * Save result BN.
	 * 
	 * @param filePath file path with file extension
	 */
	public void saveBNNet(String filePath){
		try{
			Streamer outStreamer = new Streamer(filePath);
			net.write(outStreamer);
		}catch(NeticaException ne){
			ne.printStackTrace();
		}
	}
}
