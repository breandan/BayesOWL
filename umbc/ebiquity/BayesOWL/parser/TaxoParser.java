/**
 * Created on Sept. 20, 2008
 * Last Modified on Dec.05, 2008
 * @author Yi Sun
 */
package umbc.ebiquity.BayesOWL.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import umbc.ebiquity.BayesOWL.commonDefine.*;

import com.hp.hpl.jena.ontology.BooleanClassDescription;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * TaxoParser.java is built for extracting taxonomies from an OWL ontology.<br>
 * To use OWL2BN, the directory of the OWL file is needed.<br>
 * <br>
 * See paper:<br>
 *  BayesOWL: Uncertainty Modeling in Semantic Web Ontologies<br>
 *  Zhongli Ding, Yun Peng, Rong Pan<br>
 *
 */

public class TaxoParser 
{ 	
	private int union;	//record the number of different operations, for L-node naming use
	private int intersection; 
	private int complement; 
	private int disjoint; 
	private int equivalent; 
	
	private OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null ); 
	
	/**
	 * Constructor.
	 * 
	 * @param OwlFile	Owl File Directory
	 * @throws FileNotFoundException
	 */
	public TaxoParser(String OwlFile) 
	{
		try 
		{
			if(!OwlFile.contains("http"))
			{
				m.read(new FileInputStream(OwlFile), "");
			}
			else
			{
				m.read(OwlFile);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get Number of Nodes.
	 * @return	number of nodes
	 */
	public int getLength() 
	{
		int length = getNames().length;
		
		return length;
	}
	
	/**
	 * Get All the Node Names.
	 * @return	all the node names
	 */
	public String[] getNames() 
	{
		union = 0;
		intersection = 0;
		complement = 0;
		disjoint = 0;
		equivalent = 0;
		
		ArrayList<String> names = new ArrayList<String>();
        
        for (Iterator i = m.listClasses(); i.hasNext(); ) 
        {
            String cName = getCName((OntClass) i.next());
            if(cName != null)
            {
            	names.add(cName);
            }
        }
        
        for (Iterator i = m.listClasses(); i.hasNext(); ) 
        {
            String lName = getLName((OntClass) i.next());
            
        	if(lName != null)
            {
            	names.add(lName);
            }
        }
     
        return (String[]) names.toArray(new String[0]);
	}
	
	/**
	 * Get All the Node Tags.
	 * @return	all the node tags
	 */
	public ExNode.TAG[] getTags() 
	{
		int length = getLength();
		String[] names = getNames();
		ExNode.TAG[] tags = new ExNode.TAG[length];;
		
		for(int i = 0; i < length; i++)
	    {
	    	if(!names[i].contains("LNode"))
	    	{       		
	    		tags[i] = ExNode.TAG.NORMALNODE;
	    	}
	    	else if(names[i].contains("Disjoint"))
	    	{
	    		tags[i] = ExNode.TAG.DISJOINT;
	    	}
	    	else if(names[i].contains("Equivalent"))
	    	{
	    		tags[i] = ExNode.TAG.EQUIVALENT;
	    	}
	    	else if(names[i].contains("Union"))
	    	{
	    		tags[i] = ExNode.TAG.UNION;
	    	}
	    	else if(names[i].contains("Intersection"))
	    	{
	    		tags[i] = ExNode.TAG.INTERSECTION;
	    	}
	    	else if(names[i].contains("Complement"))
	    	{
	    		tags[i] = ExNode.TAG.COMPLEMENT;
	    	}
	    }
		
		return tags;
	}
	
	/**
	 * Get All the Node Parents.
	 * for LNode, its first parent is its operation result.
	 * @return	all the node parents
	 */
	public String[][] getParents()
	{
		int length = getLength();
		String[] names = getNames();
		String[][] parents = new String[length][length];
		String[][] parents1 = new String[length][length];
		String[][] parents2 = new String[length][];
		
        for (Iterator i = m.listClasses();  i.hasNext(); ) 
        {
	        parents1 = getSubClasses((OntClass) i.next(), length, names, parents);
	        parents = parents1;
        }     
        for (Iterator i = m.listClasses();  i.hasNext(); ) 
        {
            parents1 = getOperations((OntClass) i.next(), length, names, parents);
            parents = parents1;
        }      
        for(int j = 0; j < length; j++)
        {
        	for(int k = 0; k <length; k++)
        	{
        		if(parents[j][k] == null)
        		{
        			parents2[j] = new String[k];
        			break;
        		}
        	}
        }
        for(int j = 0; j < length; j++)
        {
        	for(int k = 0; k <parents2[j].length; k++)
        	{
        		parents2[j][k] = parents[j][k];
        	}
        }
        return parents2;
	}
	

	
    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================
	
	/**
	 * Get CNode Name.
	 * @param c, ontology class
	 * @return	cName, cNode name
	 */
	private String getCName(OntClass c)
	{
    	String cName = null;
    	if(c.getURI() != null)
    	{
    		cName = c.getURI().substring(c.getURI().indexOf("#")+1); 
    		cName = cName.replaceAll("-", "_");
    	}
				
    	if(cName != null && cName.length()>30)
    	{
    		cName = cName.substring(0, 29);
    	}
		return cName;
	}
	
	/**
	 * Get LNode Name.
	 * @param c, ontology class
	 * @return	lName, lNode name
	 */
    private String getLName(OntClass c)
    {          	   		
    	String lName = null;
    	
        if (c.getDisjointWith() != null) 
        {
        	disjoint++;
            lName = "LNode" + "Disjoint" + "_" + Integer.toString(disjoint);
        }
        else if (c.getEquivalentClass() != null) 
        {
        	equivalent++;
        	lName = "LNode" + "Equivalent" + "_" + Integer.toString(equivalent);
        }
        else if (c.isUnionClass()) 
        {
        	union++;
        	lName = "LNode" + "Union" + "_" + Integer.toString(union);
        }
        else if (c.isIntersectionClass()) 
        {
        	intersection++;
        	lName = "LNode" + "Intersection" + "_" + Integer.toString(intersection);
        }
        else if (c.isComplementClass()) 
        {
        	complement++;
        	lName = "LNode" + "Complement" + "_" + Integer.toString(complement);
        }
        
        if(lName != null && lName.length()>30)
        {
        	lName.substring(0, 29);
        }
        return lName;
    }
    
	/**
	 * Get All the Parents in SubClass Relations.
	 * @param c, ontology class; length, number of nodes; names, all the node names; parents, all the node parents
	 * @return	parents
	 */
	private String[][] getSubClasses(OntClass c, int length, String[] names, String[][] parents) 
    {
    	int j;
    	int k;
		String x = null;
    	String y = null;
    	String z = null;
    	
    	if(c.getURI() != null)
    	{
	    	x = c.getURI().substring(c.getURI().indexOf("#")+1);
	    	x = x.replaceAll("-", "_");
			for(j = 0; j < length; j++)
	   		{
	   		    if(names[j].equalsIgnoreCase(x))
	   		    {
	   		    	break;
	   		    }
	   		}
	    	
	    	for (Iterator i = c.listSuperClasses( true );i.hasNext(); ) 
	        { 		
	    		z = ((OntClass) i.next()).getURI();
	    		if(z != null)
	    		{
		    		y = z.substring(c.getURI().indexOf("#")+1);
		        	y = y.replaceAll("-", "_");
		        	
		    		for(k = 0; k < length; k++)
		    		{
		    			if(parents[j][k] == null)
		    			{
		    	    		parents[j][k] = y;
		    				break;
		    			}
		    		}
	    		}
	        }
    	}
    	return parents;
    }
	
	/**
	 * Get All the Parents in SubClass Relations.
	 * @param c, ontology class
	 * @return	parents
	 */
    private String[][] getOperations(OntClass c, int length, String[] names, String[][] parents) 
    {   
    	if (c.getDisjointWith() != null) 
        {
            parents = getDisjointEquivalent("Disjoint", c.getDisjointWith(), c, length, names, parents);
        }
        else if (c.getEquivalentClass() != null) 
        {
        	parents = getDisjointEquivalent("Equivalent", c.getEquivalentClass(), c, length, names, parents);
        }
    	else if (c.isUnionClass()) 
        {
            parents = getUnion(c.asUnionClass(),c, length, names, parents);
        }
        else if (c.isIntersectionClass()) 
        {
        	parents = getIntersection(c.asIntersectionClass(),c, length, names, parents);
        }
        else if (c.isComplementClass()) 
        {
        	parents = getComplement(c.asComplementClass(),c, length, names, parents);
        }
    	return parents;
    }
    
	/**
	 * Get All the Parents in Disjoint and Equivalent Relations.
	 * @param op, operation name; c, the first ontology class; d, the second ontology class; length, number of nodes; names, all the node names; parents, all the node parents
	 * @return	parents
	 */
    private String[][] getDisjointEquivalent(String op, OntClass c, OntClass d, int length, String[] names, String[][] parents) 
    {
    	int i;
    	   		 
    	for(i = 0; i < length; i++)
    	{
    		if( names[i].contains(op)&&parents[i][0]==null)
    		{
    	    	if(c.getURI() != null)
    	    	{
	    			parents[i][0] = c.getURI().substring(c.getURI().indexOf("#")+1);
	    	    	parents[i][0] = parents[i][0].replaceAll("-", "_");
	    	    	if(d.getURI() != null)
	    	    	{
		    	    	parents[i][1] = d.getURI().substring(d.getURI().indexOf("#")+1);
		    	    	parents[i][1] = parents[i][1].replaceAll("-", "_");
	    	    	}
    	    	}
    	    	break;
    		}
    	}
  	  
        return parents;
    }
    
	/**
	 * Get All the Parents in Union Relations.
	 * @param boolClass, boolean class description; d, the ontology class of operation result; length, number of nodes; names, all the node names; parents, all the node parents
	 * @return	parents
	 */
    private String[][] getUnion(BooleanClassDescription boolClass, OntClass d, int length, String[] names, String[][] parents) 
    {
    	int j;
    	int k;
    	int p;
    	int q;
    	String x = null;
    	String y = null;
    		
    	for(j=0; j<length; j++)
    	{
    		if(names[j].contains("Union")&&parents[j][0]==null)
    		break;
    	}
    				
    	k = 0;
    	//add the operation result class as the first parent of LNode
    	if(d.getURI() != null)
    	{
	    	x = d.getURI().substring(d.getURI().indexOf("#")+1);
	    	x = x.replaceAll("-", "_");
	    	parents[j][k] = x;
			k++;
				
		    for (Iterator i = boolClass.listOperands(); i.hasNext(); ) 
		    {
		        
		        y = ((OntClass) i.next()).getURI();
		        if(y != null)
		        {
			        y = y.substring(y.indexOf("#")+1) ;
			        y = y.replaceAll("-", "_");
			    	parents[j][k] = y;
		            k++;
		            
		            //add links between operation result class and the original class
		            for(p = 0; p < length; p++)
		            {
		            	if(names[p].equalsIgnoreCase(y))
		            	{
		            		break;
		            	}
		            }
		            for(q = 0; q < length; q++)
		            {
		            	if(parents[p][q] != null && parents[p][q].equalsIgnoreCase(x))
		            	{
		            		break;
		            	}
		            	else if(parents[p][q] == null)
		            	{
		            		parents[p][q] = x;
		            		break;
		            	}
		            }
		        }
		    }
    	}
    	
        return parents;
    }
    
	/**
	 * Get All the Parents in Intersection Relations.
	 * @param boolClass, boolean class description; d, the ontology class of operation result; length, number of nodes; names, all the node names; parents, all the node parents
	 * @return	parents
	 */
    private String[][] getIntersection(BooleanClassDescription boolClass, OntClass d, int length, String[] names, String[][] parents) 
    {
    	int j;
    	int k;
    	int p;
    	int q;
    	String x = null;
    	String y = null;
    		
    	for(j = 0;j < length;j++)
    	{
    		if(names[j].contains("Intersection") && parents[j][0]==null)
    		break;
    	}
    	
    	k = 0;
    	//add the operation result class as the first parent of LNode
    	if(d.getURI() != null)
    	{
	    	x = d.getURI().substring(d.getURI().indexOf("#")+1);
	    	x = x.replaceAll("-", "_");
			parents[j][k] = x;
			k++;
			
	        for(p = 0; p < length; p++)
	        {
	        	if(names[p].equalsIgnoreCase(x))
	        	{
	        		break;
	        	}
	        }
				
		    for (Iterator i = boolClass.listOperands(); i.hasNext(); ) 
		    {
		        
		        y = ((OntClass) i.next()).getURI();
		        if(y != null)
		        {
			        y = y.substring(y.indexOf("#")+1) ;   
			        y = y.replaceAll("-", "_");
		            parents[j][k] = y;
		            k++;
		            
		          //add links between operation result class and the original class
		            for(q = 0; q < length; q++)
		            {
		            	if(parents[p][q] != null && parents[p][q].equalsIgnoreCase(y))
		            	{
		            		break;
		            	}
		            	else if(parents[p][q] == null)
		            	{
		            		parents[p][q] = y;
		            		break;
		            	}
		            }
		        }
		    }
    	}
        return parents;
    }
    
	/**
	 * Get All the Parents in Complement Relations.
	 * @param boolClass, boolean class description; d, the ontology class of operation result; length, number of nodes; names, all the node names; parents, all the node parents
	 * @return	parents
	 */
    private String[][] getComplement(BooleanClassDescription boolClass, OntClass d, int length, String[] names, String[][] parents) 
    {
    	int j;
    	int k;
    	String x = null;
    	String y = null;
    		
    	for(j = 0; j < length; j++)
    	{
    		if(names[j].contains("Complement") && parents[j][0] == null)
    		break;
    	}
    	
    	k = 0;
    	//add the operation result class as the first parent of LNode
    	if(d.getURI() != null)
    	{
	    	x = d.getURI().substring(d.getURI().indexOf("#")+1);
	    	x = x.replaceAll("-", "_");
			parents[j][k] = x;
			k++;
				
		    for (Iterator i = boolClass.listOperands(); i.hasNext(); ) 
		    {
		        y = ((OntClass) i.next()).getURI();
		        if(y != null)
		        {
			        y = y.substring(y.indexOf("#")+1) ;
			        y = y.replaceAll("-", "_");
		            parents[j][k] = y;
		            k++;
		        }
		    }
    	}
	 
        return parents;
    }
    
}