/**
 * Created on Oct. 20, 2008
 * Last Modified on Dec.05, 2008
 * @author Yi Sun
 */
package umbc.ebiquity.BayesOWL.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import norsys.netica.Net;
import norsys.netica.NeticaException;
import norsys.netica.NodeList;

import umbc.ebiquity.BayesOWL.commonDefine.*;

/**
 * ProbParser.java is built for parsing probability from a probability file.<br>
 * To use ProbParser, the directory of the OWL file is needed.<br>
 * <br>
 * See paper:<br>
 *  BayesOWL: Uncertainty Modeling in Semantic Web Ontologies<br>
 *  Zhongli Ding, Yun Peng, Rong Pan<br>
 *
 */
public class ProbParser {	
	
	public class Class 
	{
		String variable;
		String className;
	}

	public class Proposition 
	{
		String proposition;
		String variable;
		int state;
	}
	
	public class CondCollection 
	{
		String condCollectionName;
		String condition;
	}
	
	public class PropCollection 
	{
		String propCollectionName;
		String proposition;
	}
	
	public class ConPro 
	{
		String condition;
		String proposition;
		double value;
	}
	
	public class Probability 
	{
		String condition;
		String proposition;
		int[] state;
		double value;
	}
	
	public class ConProList 
	{
		String condition;
		String proposition;
	}
	
	/**
	 * Deal With the Structure like
	 * <owl:Variable rdf:ID="VariableName">
	 * <hasClass>ClassName</hasClass>
	 * </owl:Variable>
	 * Get All the Variable and Corresponding ClassName Sets.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of Class
	 */
	public Class[] getClass(String OwlFile) throws IOException 
	{  	
    	int i;
    	int j;
    	String data = null;
    	ArrayList<Class> classList = new ArrayList<Class>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(OwlFile)));
    	
    	while((data = br.readLine())!=null)
    	{
    		if(data.contains("<owl:Variable"))
    		{
    			i = data.indexOf("\"");
    			j = data.lastIndexOf("\"");

    			Class className = new Class();
    			
    			//Get Variable for ClassList
    			className.variable = data.substring(i+1, j);
    			
    			while(!(data = br.readLine()).contains("</owl:Variable>"))
    			{
    				if(data.contains("<hasClass>"))
    				{
    					i = data.indexOf("<hasClass>");
    					j = data.indexOf("</hasClass>");
    					//Get ClassName for ClassList
    					className.className = data.substring(i+10, j).trim();
    				}
    			}
    			classList.add(className);
    		}
    	}
    	return (Class[]) classList.toArray(new Class[0]);
    }
	
	/**
	 * Deal With the Structure like
	 * <owl:Proposition rdf:ID="PropositionName">
	 * <hasVariable>Variable</hasVariable>
	 * <hasState>State</hasState>
	 * </owl:Proposition>
	 * Get All the Proposition Name, Variable and State Sets.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of Proposition
	 */
	public Proposition[] getProposition(String OwlFile) throws IOException 
	{  	
    	int i;
    	int j;
    	String data = null;
    	ArrayList<Proposition> propositionList = new ArrayList<Proposition>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(OwlFile)));
    	
    	while((data = br.readLine())!=null)
    	{
    		if(data.contains("<owl:Proposition"))
    		{
    			i = data.indexOf("\"");
    			j = data.lastIndexOf("\"");

    			Proposition proposition = new Proposition();
    			
    			//Get Proposition for PropositionList
    			proposition.proposition = data.substring(i+1, j);
    			
    			while(!(data = br.readLine()).contains("</owl:Proposition>"))
    			{
    				if(data.contains("<hasVariable>"))
    				{
    					i = data.indexOf("<hasVariable>");
    					j = data.indexOf("</hasVariable>");
    					//Get Variable for PropositionList
    					proposition.variable = data.substring(i+13, j).trim();
    				}
    				
    				else if(data.contains("<hasState>"))
    				{
    					i = data.indexOf("<hasState>");
    					j = data.indexOf("</hasState>");
    					//Get State for PropositionList
    					if(data.substring(i+10, j).trim().equals("True"))
    					{
    						
    						proposition.state = 0;
    					}
    					else if(data.substring(i+10, j).trim().equals("False"))
    					{
    						proposition.state = 1;
    					}
    				}
    			}
    			propositionList.add(proposition);
    		}
    	}
    	return (Proposition[]) propositionList.toArray(new Proposition[0]);
    }
	
	/**
	 * Deal With the Structure like
	 * <owl:CondCollection rdf:ID="CondCollectionName">
	 * <hasCondition>Condition</hasCondition>
	 * </owl:CondCollection>
	 * Get All the Condition Collection Name and Corresponding Condition Collection.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of CondCollection
	 */
	public CondCollection[] getCondCollection(String OwlFile) throws IOException 
	{  	
    	int i;
    	int j;
    	String data = null;
    	ArrayList<CondCollection> condCollectionList = new ArrayList<CondCollection>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(OwlFile)));
    	
    	while((data = br.readLine())!=null)
    	{
    		if(data.contains("<owl:CondCollection"))
    		{
    			i = data.indexOf("\"");
    			j = data.lastIndexOf("\"");

    			CondCollection condCollection = new CondCollection();
    			
    			//Get ConditionCollectionName for CondCollectionList
    			condCollection.condCollectionName = data.substring(i+1, j);
    			
    			while(!(data = br.readLine()).contains("</owl:CondCollection>"))
    			{
    				if(data.contains("<hasCondition>"))
    				{
    					i = data.indexOf("<hasCondition>");
    					j = data.indexOf("</hasCondition>");
    					//Get Condition for ConditionList
    					condCollection.condition = data.substring(i+14, j).trim();
    				}
    			}
    			condCollectionList.add(condCollection);
    		}
    	}
    	return (CondCollection[]) condCollectionList.toArray(new CondCollection[0]);
	}
	
	/**
	 * Deal With the Structure like
	 * <owl:PropCollection rdf:ID="PropCollectionName">
	 * <hasProposition>Proposition</hasProposition>
	 * </owl:PropCollection>
	 * Get All the Proposition Collection Name and Corresponding Proposition Collection.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of PropCollection
	 */
	public PropCollection[] getPropCollection(String OwlFile) throws IOException 
	{  	
    	int i;
    	int j;
    	String data = null;
    	ArrayList<PropCollection> propCollectionList = new ArrayList<PropCollection>();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(OwlFile)));
    	
    	while((data = br.readLine())!=null)
    	{
    		if(data.contains("<owl:PropCollection"))
    		{
    			i = data.indexOf("\"");
    			j = data.lastIndexOf("\"");

    			PropCollection propCollection = new PropCollection();
    			
    			//Get PropositionCollectionName for PropCollectionList
    			propCollection.propCollectionName = data.substring(i+1, j);
    			
    			while(!(data = br.readLine()).contains("</owl:PropCollection>"))
    			{
    				if(data.contains("<hasProposition>"))
    				{
    					i = data.indexOf("<hasProposition>");
    					j = data.indexOf("</hasProposition>");
    					//Get Proposition for PropositionList
    					propCollection.proposition = data.substring(i+16, j).trim();
    				}
    			}
    			propCollectionList.add(propCollection);
    		}
    	}
    	return (PropCollection[]) propCollectionList.toArray(new PropCollection[0]);
    }
	
	/**
	 * Deal With the Structure like
	 * <owl:Probability rdf:ID="P(Proposition|Condition)">
	 * <hasCondCollection>Condition</hasCondCollection>
	 * <hasPropCollection>Proposition</hasPropCollection>
	 * <hasValue>Value</hasValue>
	 * </owl:Probability>
	 * Get All the Condition, Proposition and Value Sets.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of ConPro
	 */
	public ConPro[] getConPro(String OwlFile) throws IOException 
	{
    	int i;
    	int j;
    	int k;
    	String data = null;
    	String  cpString;
    	ConPro conPro;
    	ArrayList<ConPro> conProList = new ArrayList<ConPro>();
    	PropCollection[] propCollectionArray = getPropCollection(OwlFile);
    	CondCollection[] condCollectionArray = getCondCollection(OwlFile);
    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(OwlFile)));
       	
    	while((data = br.readLine())!=null)
    	{
    		if(data.contains("<owl:Probability"))
    		{
    			conPro = new ConPro();
    			
    			while(!(data = br.readLine()).contains("</owl:Probability>"))
    			{  				
    				if(data.contains("<hasPropCollection>"))
    				{
    					i = data.indexOf("<hasPropCollection>");
    					j = data.indexOf("</hasPropCollection>");
    			
    					//Get Proposition for ConProList
    					cpString = data.substring(i+19, j).trim();
    					
    					for(k = 0; k < propCollectionArray.length; k++)
    					{
    						if(propCollectionArray[k].propCollectionName.equals(cpString))
    						{
    							conPro.proposition = propCollectionArray[k].proposition;
    						}
    					}
    				} 				
    				
    				else if(data.contains("<hasCondCollection>"))
    				{
    					i = data.indexOf("<hasCondCollection>");
    					j = data.indexOf("</hasCondCollection>");
    			
    					//Get Condition for ConProList
    					cpString = data.substring(i+19, j).trim();

    					for(k = 0; k < condCollectionArray.length; k++)
    					{
    						if(condCollectionArray[k].condCollectionName.equals(cpString))
    						{
    							conPro.condition = condCollectionArray[k].condition;
    						}
    					}
    				}
    				
    				else if(data.contains("<hasValue>"))
    				{
    					i = data.indexOf("<hasValue>");
    					j = data.indexOf("</hasValue>");
    					//Get Value for ConProList
    					conPro.value = new Double(data.substring(i+10, j).trim());
    				}
    			}
    			conProList.add(conPro);
    		}
    	}

    	return (ConPro[]) conProList.toArray(new ConPro[0]);
    }

	/**
	 * Get All the Condition, Proposition, State and Value Sets.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of Probability
	 */
	public Probability[] getProbability(String OwlFile) throws IOException 
	{
    	int i;
    	int j;
    	Probability probability;
    	String condition = null;
    	String proposition = null;
    	int conditionState = 0;
    	int propositionState = 0;
    	int[] state = null;
    	ConPro[] conProArray = getConPro(OwlFile);
    	Proposition[] propositionArray = getProposition(OwlFile);
    	Probability[] probabilityArray = new Probability[conProArray.length];
      	
    	for(i = 0;i < conProArray.length; i++)
    	{
    		probability = new Probability();
    		//Get Condition for ProbabilityList
    		if(conProArray[i].condition != null)
    		{   			
    			for(j = 0;j < propositionArray.length; j++)
    			{
    				if(propositionArray[j].proposition.equals(conProArray[i].condition))
    				{
    					condition = propositionArray[j].variable;
    					conditionState = propositionArray[j].state;
    				}
    			}
    		}
    		
    		//Get Proposition for ProbabilityList			
			for(j = 0; j < propositionArray.length; j++)
			{
				if(propositionArray[j].proposition.equals(conProArray[i].proposition))
				{
					proposition = propositionArray[j].variable;
					propositionState = propositionArray[j].state;
				}
			}
    		
    		//Get State for ProbabilityList
    		if(conProArray[i].condition != null)
			{
    			state = new int[2];
    			state[0] = conditionState;
    			state[1] = propositionState;
			}
    		else
    		{
    			state = new int[1];
    			state[0] = propositionState;
    		}
    		 
    		probability.condition = condition;
    		probability.proposition = proposition;
    		probability.state = state;
    		//Get Value for ProbabilityList
    		probability.value = conProArray[i].value;
    		probabilityArray[i]=probability;
    	}   	
    	
    	return probabilityArray;
    }
	
	/**
	 * Make a Condition Proposition List for the Probability File.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of ConProList
	 */
	public ConProList[] getConProList(String OwlFile) throws IOException
	{
		int i;
		int flag;
		ConProList cpList1;
		ConProList cpList2;
		Probability[] probabilityArray = getProbability(OwlFile);
		ArrayList<ConProList> conProList = new ArrayList<ConProList>();
		
		for(i = 0;i < probabilityArray.length; i++)
		{
			cpList1 = new ConProList();
			cpList1.condition = probabilityArray[i].condition;
			cpList1.proposition = probabilityArray[i].proposition;
			
			if(conProList.isEmpty())
			{
				conProList.add(cpList1);
			}
			else
			{
				flag = 0;
				Iterator<ConProList> iter = conProList.iterator();
				while(iter.hasNext())
				{
					cpList2 = new ConProList();
					cpList2 = iter.next();
					if((cpList2.condition != null) && (cpList1.condition != null))
					{
						if(cpList2.condition.equals(cpList1.condition) && cpList2.proposition.equals(cpList1.proposition))
						{
							flag = 1;
							break;					
						}
					}
					else if((cpList2.condition == null) && (cpList1.condition == null))
					{
						if(cpList2.proposition.equals(cpList2.proposition))
						{
							flag = 1;
							break;
						}
					}
				}
				
				if(flag == 0)
				{
					conProList.add(cpList1);
				}
			}
		}
		
		return (ConProList[]) conProList.toArray(new ConProList[0]);		
	}
	
	/**
	 * Construct the CPT based on the Probability File.
	 * @param OwlFile	Owl File Directory
	 * @throws IOException
	 * @return	Array of Constraint
	 * @throws NeticaException 
	 */
	public Constraint[] getConstraint(String OwlFile, Net net) throws IOException, NeticaException
	{
		int i;
		int j;
		ArrayList<Constraint> cons = new ArrayList<Constraint>();
		ConProList[] conProListArray = getConProList(OwlFile);
		Probability[] probabilityArray = getProbability(OwlFile);
		
		for(i = 0; i < conProListArray.length; i++)
		{
			if(conProListArray[i].condition == null)
			{
				RandomVariable[] rvs0 = new RandomVariable[1];
				rvs0[0] = new RandomVariable(conProListArray[i].proposition, new String[]{"True", "False"});;
								
				JointProbDistribution con0 = new JointProbDistribution(rvs0);
				
				double value0 = -1;
				double value1 = -1;
				
				for(j = 0; j < probabilityArray.length; j++)
				{	
					if((probabilityArray[j].condition == null) && conProListArray[i].proposition.equals(probabilityArray[j].proposition))
					{
						if(probabilityArray[j].state[0] == 0)
						{
							value0 = probabilityArray[j].value;
							if(value0 < 0 || value0 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
						else 
						{
							value1 = probabilityArray[j].value;
							if(value1 < 0 || value1 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
					}
				}
				
				if(value0 == -1)
				{
					value0 = 1 - value1;
				}
				else if(value1 == -1)
				{
					value1 = 1 - value0;
				}			
				else if(value0 + value1 != 1)
				{
					System.out.print("Probability of ");
					System.out.print(conProListArray[i].proposition);
					System.out.print(" ");					
					System.out.print("and Probability of NOT ");
					System.out.print(conProListArray[i].proposition);
					System.out.print(" ");
					System.out.println("should add to 1.");
					return null;
				}
				
				con0.addProbEntry(new int[]{0}, value0);
				con0.addProbEntry(new int[]{1}, value1);
				cons.add(new LocalMarginalConstraint(con0, conProListArray[i].proposition));
			}
				
			else
			{
				RandomVariable[] rvs1Prior = new RandomVariable[1];
				rvs1Prior[0] = new RandomVariable(conProListArray[i].proposition, new String[]{"True", "False"});;
				
				RandomVariable[] rvs1Cond = new RandomVariable[1];
				rvs1Cond[0] = new RandomVariable(conProListArray[i].condition, new String[]{"True", "False"});;
				
				CondProbDistribution con = new CondProbDistribution(rvs1Prior, rvs1Cond);
				
				double value11 = -1;
				double value01 = -1;
				double value10 = -1;
				double value00 = -1;
				
				for(j = 0; j < probabilityArray.length; j++)
				{
					if(conProListArray[i].condition.equals(probabilityArray[j].condition) && conProListArray[i].proposition.equals(probabilityArray[j].proposition))
					{
						if((probabilityArray[j].state[0] == 1) && (probabilityArray[j].state[1] == 1))
						{
							value11 = probabilityArray[j].value;
							
							if(value11 < 0 || value11 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
						else if((probabilityArray[j].state[0] == 0) && (probabilityArray[j].state[1] == 1))
						{
							value01 = probabilityArray[j].value;
							if(value01 < 0 || value01 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
						else if((probabilityArray[j].state[0] == 1) && (probabilityArray[j].state[1] == 0))
						{
							value10 = probabilityArray[j].value;
							if(value10 < 0 || value10 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
						else if((probabilityArray[j].state[0] == 0) && (probabilityArray[j].state[1] == 0))
						{
							value00 = probabilityArray[j].value;
							if(value00 < 0 || value00 > 1)
							{
								System.out.print("Probability of ");
								System.out.print(probabilityArray[j].proposition);
								System.out.print(" ");
								System.out.println("should be between 0 and 1.");
								return null;
							}
						}
					}
				}
				
				if((value00 == -1) && (value10 == -1))
				{
					System.out.print("Probability of (");
					System.out.print(conProListArray[i].proposition);
					System.out.print("|");				
					System.out.print(conProListArray[i].condition);
					System.out.print(")or Probability of (NOT ");
					System.out.print(conProListArray[i].proposition);
					System.out.print("|");				
					System.out.print(conProListArray[i].condition);
					System.out.print(") ");
					System.out.println("should be given.");
					return null;
				}
				else if(value00 == -1)
				{
					value00 = 1 - value10;
				}
				else if(value10 == -1)
				{
					value10 = 1 - value00;
				}
				else if((value00 + value10 != 1))
				{
					System.out.print("Probability of (");
					System.out.print(conProListArray[i].proposition);
					System.out.print("|");				
					System.out.print(conProListArray[i].condition);
					System.out.print(")or Probability of (NOT ");
					System.out.print(conProListArray[i].proposition);
					System.out.print("|");				
					System.out.print(conProListArray[i].condition);
					System.out.print(") ");
					System.out.println("should add to 1.");
					return null;
				}

				norsys.netica.Node node = net.getNode(conProListArray[i].proposition);
				NodeList nodeList = node.getParents();
				
				if(nodeList.contains(net.getNode(conProListArray[i].condition)))
				{
					if(value01 == -1)
					{
						value01 = 0;
					}
					else if(value01 != 0)
					{
						System.out.print("Probability of (");
						System.out.print(conProListArray[i].proposition);
						System.out.print("| NOT ");				
						System.out.print(conProListArray[i].condition);
						System.out.print(") ");
						System.out.println("should be 0.");
						return null;
					}
					
					if(value11 == -1)
					{
						value11 = 1;
					}
					else if(value11 != 1)
					{
						System.out.print("Probability of (NOT ");
						System.out.print(conProListArray[i].proposition);
						System.out.print("|");				
						System.out.print(conProListArray[i].condition);
						System.out.print(") ");
						System.out.println("should be 1.");
						return null;
					}	
				}
				else
				{
					if((value01 == -1) && (value11 == -1))
					{
						System.out.print("Probability of (");
						System.out.print(conProListArray[i].proposition);
						System.out.print("|NOT ");				
						System.out.print(conProListArray[i].condition);
						System.out.print(")or Probability of (NOT ");
						System.out.print(conProListArray[i].proposition);
						System.out.print("|NOT ");				
						System.out.print(conProListArray[i].condition);
						System.out.print(") ");
						System.out.println("should be given.");
						return null;
					}
					else if(value01 == -1)
					{
						value01 = 1 - value11;
					}
					else if(value11 == -1)
					{
						value11 = 1 - value01;
					}
					else if((value01 + value11 != 1))
					{
						System.out.print("Probability of (");
						System.out.print(conProListArray[i].proposition);
						System.out.print("|NOT ");				
						System.out.print(conProListArray[i].condition);
						System.out.print(")or Probability of (NOT ");
						System.out.print(conProListArray[i].proposition);
						System.out.print("|NOT ");				
						System.out.print(conProListArray[i].condition);
						System.out.print(") ");
						System.out.println("should add to 1.");
						return null;
					}
				}
				
				con.addCondProbEntry(new int[]{1,1}, value11);	
				//original
				//con.addCondProbEntry(new int[]{0,1}, value01);	
				//con.addCondProbEntry(new int[]{1,0}, value10);
				//changed by Shenyong, Dec. 15, 2008
				con.addCondProbEntry(new int[]{1,0}, value01);	
				con.addCondProbEntry(new int[]{0,1}, value10);	
				
				con.addCondProbEntry(new int[]{0,0}, value00);	
				cons.add(new LocalConditionalConstraint(con)); 
			}
		}
		
		return (Constraint[]) cons.toArray(new Constraint[0]);	
	}
	
}