/**
 * MultiDimensionalArray.java
 * 
 * @author Zhongli Ding (original)
 *
 * Created on Dec. 06, 2004
 * Modified on Sept. 08, 2005, v0.4
 * Modified on Aug. 07, 2008, comments added.
 * 
 */

package umbc.ebiquity.BayesOWL.commonDefine;

import java.util.*;

/**
 * This class simulates a multi-dimensional array by wrapping an one-dimensional array, 
 * which is used specifically to store 'double' values.<br>
 * For an arbitrary n-dimensional array "A" with dimensions "d1, d2, ..., dn",<br>
 * we use an one-dimensional array "B" of size "m = d1 * d2 * ... * dn" to simulate it.<br>
 * By taking a row-major layout, the corresponding position k of B[k] in "B" versus the 
 * element A[idx1 ][idx2 ]...[idxn] in "A" is:<br> 
 * 	k = idx1 * (d2 * ... * dn) + idx2 * (d3 * ... * dn) + ... + idxn * 1<br>   
 * which can be further summarized as:<br>
 * 	(1) k = idx1 * f1 + idx2 * f2 + ... + idxn * fn = sum(idxj * fj), j = 1 to n<br>
 *	(2) if j = n, fj = 1<br>
 * 	(3) if 1 =< j < n, fj = dj+1 * ... * dn = prod(dk), k = j+1 to n<br> 
 * Note in real Java implementation, all the indices are starting from 0, that is,  k is from 0 to m-1, idx1 is from 0 to d1-1, idx2 is from 0 to d2-1, ... idxn is from 0 to dn-1<br>    
 * On the other side, given k, we can compute "idx1, idx2, ..., idxn" as follows:<br>
 * 	idxn = k % dn, let k' = k / dn<br>
 *	idxn-1 = k' % dn-1, let k'' = k' / dn-1,<br>
 *	repeat this process until idx0 is computed.<br>
 *
 */
public class MultiDimensionalArray {

	int[] dimensions;	//an array of length n, where n is the number of dimensions, used to store the values of "d1, d2, ..., dn" 
	int[] factors;		//an array of length n, used to store the values of fj, j=1 to n
	double[] data;		//an one-dimensional array of length "m = d1 * d2 * ... * dn", used to hold the elements of the simulated multi-dimensional array in row-major order
	int numOfRows;		//the number of data stored in this multiarray, i.e., the value of 'm'
	int numOfDims;		//the number of dimensions of this multiarray, i.e., the value of 'n'
	Hashtable pos_indices_map;	//a mapping between position in the one-dimensional array and its corresponding indices in the multiarray   
	Hashtable indices_pos_map;	//a mapping between indices in the multiarray and its corresponding position in the one-dimensional array 
	
	/**
	 * Constructor.
	 * 
	 * Takes an array of ints which represents the dimensions of the multi-dimensional array as argument,
	 * copies the dimensions of the array into the "dimensions" array, computes the "factors" array, and 
	 * allocates an one-dimensional array of length "m = d1 * d2 * ... * dn". 
	 * 
	 * For example, to create a three-dimensional array of size 3x5x7, the constructor can be invoked like this: 
	 * 	MultiDimensionalArray a = new MultiDimensionalArray (new int[] {3, 5, 7});
	 * 
	 * @param argus	dimension of the array
	 */
	public MultiDimensionalArray (int[] argus) {
		if (argus == null || argus.length == 0) {
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: Wrong array of ints for dimensions!");
		}
		else {
			numOfDims = argus.length;
			dimensions = new int[numOfDims];
			factors = new int[numOfDims];
			int production = 1;
			for (int i = numOfDims-1; i >= 0; --i) {
				if (argus[i] <= 0) {
					throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: The dimension size must be a positive integer!");
				}
				else {
					dimensions[i] = argus[i];
					factors[i] = production;
					production *= dimensions[i];
				}
			}
			data = new double[production];
			numOfRows = production;
			pos_indices_map = new Hashtable();
			indices_pos_map = new Hashtable();
			generateMap();
		}
	}

	/**
	 * This method computes and stores the two hashtable mappings between a position k of 
	 * the one-dimensional array and the corresponding indices "idx1, idx2, ..., idxn" of 
	 * the simulated multi-dimensional array.
	 */
	private void generateMap () {
		for (int i=0; i<numOfRows; i++) {
			int[] indices = new int[numOfDims];
			indices = getIndices(i);
			pos_indices_map.put(new Integer(i),indices);
			indices_pos_map.put(intArray2String(indices),new Integer(i));
		}
	}

	/**
	 * This method takes an int which represents the position in the one-dimensional arrary as argument and 
	 * computes the corresponding indices in the simulated multi-dimensional array.
	 * 
	 * @param offset	position of the one-dimensional array
	 * @return	multi-dimensional array index
	 */
	private int[] getIndices (int offset) {
		if (offset < 0 || offset >= data.length) {
			throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: Offset provided is out of bound!");
		}
		else {
			int[] indices = new int[numOfDims];
			int offset_tmp = offset;
			for (int i = numOfDims-1; i >= 0; --i) {
				indices[i] = offset_tmp % dimensions[i];
				offset_tmp = offset_tmp / dimensions[i];
			}
			return indices;
		}
	}
	
	/**
	 * This method takes an array of integers and outputs a string, with separator '#'.
	 * 
	 * @param ints	multi-dimensional array index
	 * @return	string of the index
	 */
	private String intArray2String (int[] ints) {
		String str = "";
		for (int i = 0; i < ints.length; i++)
			str += ints[i] + "#";
		return str;
	}

	/**
	 * This method gets a 'double' element of the multi-dimensional array in the specified indices.
	 * 
	 * @param indices	array index
	 * @return	value
	 */
	public double getElement (int[] indices) {
		return data[lookupOffset(indices)];
	}
		
	/**
	 * This method stores a 'double' element of the multi-dimensional array to the specified indices.
	 * 
	 * @param indices	multi-dimensional array index
	 * @param value		value to be stored
	 */
	public void putElement (int[] indices, double value) {
		data[lookupOffset(indices)] = value;
	}

	/**
	 * Takes an array of ints which represents one 'indices' of the multi-dimensional array as argument,
	 * and returns the corresponding offset in the one-dimensional array, by checking the hashtable.
	 * 
	 * @param indices	multi-dimensional array index
	 * @return	one-dimensional array position
	 */
	private int lookupOffset (int[] indices) {
		Integer obj = (Integer)indices_pos_map.get(intArray2String(indices));
		if (obj == null) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: Wrong indices provided!");
		}
		else {
			return obj.intValue();
		}
	}

	/**
	 * Takes an array of ints which represents one 'indices' of the multi-dimensional array as argument,
	 * and computes the corresponding offset in the one-dimensional array, without using the hashtable.
	 * 
	 * @param indices	multi-dimensional array index
	 * @return	one-dimensional array position
	 */
	private int getOffset (int[] indices) {
		if (indices.length != numOfDims) { 
			throw new IllegalArgumentException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: Wrong number of dimensions!");
		}
		else {
			int offset = 0;
			for (int i=0; i<numOfDims; ++i) {
				if (indices[i] < 0 || indices[i] >= dimensions[i]) {
					throw new IndexOutOfBoundsException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java:  Offset provided is out of bound!");
				}
				else {
					offset += factors[i] * indices[i];
				}
			}
			return offset;
		}
	}
		
	/**
	 * Takes an int which represents the offset in the one-dimensional arrary as argument and 
	 * returns the corresponding 'indices' in the multi-dimensional array, by checking the hashtable.
	 * 
	 * @param offset	one-dimensional array position
	 * @return	multi-dimensional array index
	 */
	public int[] lookupIndices (int offset) {
		Object obj = pos_indices_map.get(new Integer(offset));
		if (obj == null) {
			throw new NullPointerException("Class BayesOWL.IPFP.DiscreteProb.MultiDimensionalArray.java: Wrong offset provided!");
		}
		else {
			int[] indices = new int[numOfDims];
			indices = (int[])obj;
			return indices;
		}
	}
	
	/**
	 * This method gets the number of dimensions of this multi-dimensional array.
	 */
	public int getNumOfDimensions () {
		return numOfDims;
	}
	
	/**
	 * This method gets the number of data entries stored in this multi-dimensional array. 
	 */
	public int getNumOfEntries (){
		return numOfRows;
	}

	/**
	 * This method returns the sum of all the data entries stored.
	 */
	public double sum() {
		double s = 0.0;
		for (int i=0; i<numOfRows; i++)
			s += data[i];
		return s;
	}

	/**
	 * Returns the sum of the entries whose "idx1 idx2 ... idxn" values are match with the given information.  
	 * 
	 * "infoDimIdx" is a two-dimensional array, the sum of elements in the 2nd dimension is always "2", e.g.
	 * 		infoDimIdx[j][0] = u;
	 * 		infoDimIdx[j][1] = v;
	 * 		means the index of u'th dimension in this multi-array is v. 
	 * 
	 * @param infoDimIdx	
	 * @return
	 */
	public double getSum (int[][] infoDimIdx) {
		double sum = 0.0;
		int[] indices = new int[numOfDims];
		for (int i=0; i<numOfRows; i++) {
			indices = lookupIndices(i);
			boolean isMatch = true;
			for (int j=0; j<infoDimIdx.length; j++) {
				if (indices[infoDimIdx[j][0]] != infoDimIdx[j][1] ) {
					isMatch = false;
					break;
				}
			}
			if (isMatch) {
				sum += data[i];
			} 
		}
		return sum;
	}

	/**
	 * Override toString method.
	 * Returns a string representation of the multi-dimensional array.
	 */
	public String toString() {
		String s = "";
		for (int i=0; i<numOfRows; i++) {
			/*******************original code**************************
			int[] indices = new int[numOfDims];
			indices = lookupIndices(i);
			for (int j=0; j<numOfDims; j++) {
				s = s + "[" + indices[j] + "]";
			}
			s = s + " = " + data[i] + "\n";
			*/
			
			/****************Shenyong Nov. 30 2007**************************/
			s = s + data[i] + " ";
			/***************************************************************/
		}
		return s;
	}	
}