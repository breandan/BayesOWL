/**
 * Created on Sept. 15, 2008<br>
 * @author Shenyong Zhang
 */
package umbc.ebiquity.BayesOWL.constructor;

import umbc.ebiquity.BayesOWL.commonDefine.ExNode;
import norsys.netica.*;

/**
 * BNConstructor is BayesOWL's structure constructor.<br>
 * It takes taxonomy parsing result as inputs (see constructBN method). 
 * 
 */
public class BNConstructor {
	private Net net;
	private ExNode[] exNode;
	
	/**
	 * Constructor.
	 * 
	 */
	public BNConstructor(){		
	}
	
	/**
	 * Method to construct BN structure.
	 * 
	 * @param nodeNames	String[]
	 * @param tags	ExNode.TAG[]
	 * @param relationship	String[][]
	 */
	public void constructBN(String[] nodeNames, ExNode.TAG[] tags, String[][] relationship){
		if(nodeNames.length == tags.length && tags.length  == relationship.length){
			try{
				// create ExNode (with tags)
				exNode = new ExNode[nodeNames.length]; 
				createExNodes(nodeNames, tags);
				
				// create BN net
				net = new Net();
				createBNNodes(nodeNames);
				addLinks(nodeNames, relationship);
				for(int i = 0; i < exNode.length; i++){			
					InitializeCPT initCPT = new InitializeCPT();
					initCPT.fillCPT(net, exNode[i]);
				}
				net.compile();
				net.setAutoUpdate(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println("BNTranslator: WRONG INPUTS FOR TRANSLATING!!");
		}
	}
	
	/**
	 * Create ExNodes.
	 * @param nodes	nodes name list
	 * @param tags	nodes tag list
	 */
	private void createExNodes(String[] nodes, ExNode.TAG[] tags){
		for(int i = 0; i < nodes.length; i++){
			exNode[i] = new ExNode(nodes[i], tags[i]);
		}
	}
	
	/**
	 * Create BN nodes.
	 * @param names	nodes name list
	 */
	private void createBNNodes(String[] names){
		try{
			for(int i = 0; i < names.length; i++){
				new Node(names[i], "True, False", net);
			}
		}catch(NeticaException ne){
			ne.printStackTrace();
		}
	}
	
	/**
	 * Get ExNodes.
	 * @return	exNode
	 */
	public ExNode[] getNodes(){
		return exNode;
	}
	
	/**
	 * Add links in BN.
	 * @param names	node name list
	 * @param relations	parent-child relationship
	 */
	private void addLinks(String[] names, String[][] relations){
		try{
			for(int i = 0; i < relations.length; i++){
				if(relations[i].length > 0){
					if(relations[i][0] != null){
						Node node = net.getNode(names[i]);
						for(int j = 0; j < relations[i].length; j++){
							if(relations[i][j] != null){
								Node pNode = net.getNode(relations[i][j]);
								node.addLink(pNode);
							}
						}
					}
				}
			}
		}catch(NeticaException ne){
			ne.printStackTrace();
		}
	}
	
	/**
	 * Get constructed net.
	 * 
	 * @return BN net
	 */
	public Net getNet(){
		return net;
	}

	/**
	 * Save the BN net.
	 * @param filePath	file path with file extension
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
