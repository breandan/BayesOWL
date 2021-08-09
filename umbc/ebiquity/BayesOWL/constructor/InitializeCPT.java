/**
 * Created on Sept. 11, 2008<br>
 * Modified on Dec. 18, 2008<br>
 * @author Shenyong Zhang
 */
package umbc.ebiquity.BayesOWL.constructor;

import umbc.ebiquity.BayesOWL.commonDefine.ExNode;
import norsys.netica.*;
import norsys.neticaEx.NodeListEx;

/**
 * Initialize Conditional Probability Tables when building BN structure.
 *
 */
public class InitializeCPT {
	
	/**
	 * Constructor.
	 */
	public InitializeCPT(){
	}
	
	/**
	 * This method fill node's CPT according to different node type.
	 * 
	 * @param net
	 * @param exNode
	 * @throws NeticaException
	 */
	public void fillCPT(Net net, ExNode exNode) throws NeticaException{
		Node node = net.getNode(exNode.getName());
		NodeList parentNodes = node.getParents();
		float[][] cpTable = 
			new float[(int)NodeListEx.sizeCartesianProduct(node.getParents())][node.getNumStates()];

		cpTable = computeCPTable(net, exNode, cpTable);
		int[] parentStates = new int[parentNodes.size()];
		int cpTableRow = 0;
		while(true){
			node.setCPTable(parentStates, cpTable[cpTableRow]);
			cpTableRow++;
			if(NodeListEx.nextStates(parentStates, parentNodes))
				break;
		}
	}
	
	/**
	 * Compute CPTable for all nodes in BN net.
	 * @param oNode	ExNode
	 * @param table	non-initialed CPTable 
	 * @return	node's CPTable
	 */
	private float[][] computeCPTable(Net net, ExNode oNode, float[][] table){
		try{
			switch(oNode.getNodeTag()){
			case COMPLEMENT:
				// Node state is false iff Ci are all true or are all false
				for(int i = 0; i < table.length; i++){
					table[i][0] = 1.0f;
					table[i][1] = 0.0f;
				}
				table[0][0] = 0.0f; table[0][1] = 1.0f;
				table[table.length - 1][0] = 0.0f; 
				table[table.length - 1][1] = 1.0f;
				
				// set L-Node as hard evidence
				net.getNode(oNode.getName()).enterFinding("True");
				break;
			case DISJOINT:
				// Node state is false iff Ci are all true
				for(int i = 0; i < table.length; i++){
					table[i][0] = 1.0f;
					table[i][1] = 0.0f;
				}
				table[0][0] = 0.0f; table[0][1] = 1.0f;
				
				// set L-Node as hard evidence
				net.getNode(oNode.getName()).enterFinding("True");
				break;
			case EQUIVALENT:
				// Node state is true iff Ci are all true or false
				for(int i = 0; i < table.length; i++){
					table[i][0] = 0.0f;
					table[i][1] = 1.0f;
				}
				table[0][0] = 1.0f; table[0][1] = 0.0f;
				table[table.length - 1][0] = 1.0f; 
				table[table.length - 1][1] = 0.0f;
				
				// set L-Node as hard evidence
				net.getNode(oNode.getName()).enterFinding("True");
				break;
			case INTERSECTION:
				for(int i = 0; i < (table.length / 2); i++){
					table[i][0] = 0.0f;
					table[i][1] = 1.0f;
				}
				table[0][0] = 1.0f;
				table[0][1] = 0.0f;
				for(int i = (table.length / 2); i < table.length; i++){
					table[i][0] = 1.0f;
					table[i][1] = 0.0f;
				}
				table[table.length / 2][0] = 0.0f;
				table[table.length / 2][1] = 1.0f;
				
				// set L-Node as hard evidence
				net.getNode(oNode.getName()).enterFinding("True");
				break;
			case UNION:
				for(int i = 0; i < (table.length / 2); i++){
					table[i][0] = 1.0f;
					table[i][1] = 0.0f;
				}
				table[table.length / 2 - 1][0] = 0.0f;
				table[table.length / 2 - 1][1] = 1.0f;
				for(int i = (table.length / 2); i < table.length; i++){
					table[i][0] = 0.0f;
					table[i][1] = 1.0f;
				}
				table[table.length - 1][0] = 1.0f;
				table[table.length - 1][1] = 0.0f;
				// set L-Node as hard evidence
				net.getNode(oNode.getName()).enterFinding("True");
				break;
			case NORMALNODE:
				if(net.getNode(oNode.getName()).getParents().size() == 0){
					for(int i = 0; i < table.length; i++){
						table[i][0] = 0.5f;
						table[i][1] = 0.5f;
					}
				}else{
					for(int i = 0; i < table.length; i++){
						table[i][0] = 0.0f;
						table[i][1] = 1.0f;
					}
					table[0][0] = 0.5f;
					table[0][1] = 0.5f;
				}
				break;
			}
		}
		catch(NeticaException ne){
			ne.printStackTrace();
		}
		return table;
	}
}
