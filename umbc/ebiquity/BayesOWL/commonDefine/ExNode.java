/**
 * Created on Sept. 11, 2008
 * @author Shenyong Zhang
 */
package umbc.ebiquity.BayesOWL.commonDefine;

import norsys.netica.NeticaException;

/**
 * ExNode.java is built for OWL2BN translation use.<br>
 * For translating OWL into BN, a tag of node <br>
 * (must be one of these:<br>
 *  COMPLEMENT, DISJOINT, EQUIVALENT, INTERSECTION, UNION.) <br>
 * is needed for CPT constructing use.<br>
 * <br>
 * See paper:<br>
 *  BayesOWL: Uncertainty Modeling in Semantic Web Ontologies<br>
 *  Zhongli Ding, Yun Peng, Rong Pan<br>
 *
 */
public class ExNode{
	public enum TAG {COMPLEMENT, DISJOINT, EQUIVALENT, INTERSECTION, UNION, NORMALNODE};
	private ExNode.TAG NodeTag;
	private String Name;
	/**
	 * Constructor.
	 * 
	 * @param tag	Node Tag
	 * @throws NeticaException
	 */
	public ExNode(String name, ExNode.TAG tag){
		// TODO Auto-generated constructor stub
		Name = name;
		this.NodeTag = tag;
	}
	
	/**
	 * Set node name.
	 * @param name
	 */
	public void setName(String name){
		Name = name;
	}
	
	/**
	 * Set node tag.
	 * @param tag
	 */
	public void setTag(ExNode.TAG tag){
		NodeTag = tag;
	}
	/**
	 * Get Node Name.
	 * @return	node name
	 */
	public String getName(){
		return this.Name;
	}
	/**
	 * Get Node Tag.
	 * @return	node tag
	 */
	public ExNode.TAG getNodeTag(){
		return NodeTag;
	}
}
