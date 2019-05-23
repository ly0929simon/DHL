package com.dhl.sop.label;

import java.util.Comparator;
import java.util.HashMap;

public class InternalServiceCodeComparator implements Comparator<String>{
	
	private static HashMap<String,Integer> productMap;
	
	public InternalServiceCodeComparator() {
		if (productMap == null) {
			productMap = new HashMap<String, Integer>();
			productMap.put("C", new Integer(1));
	    	productMap.put("COD", new Integer(2));
	    	productMap.put("EXW", new Integer(3));
	    	productMap.put("EPU", new Integer(4));
	    	productMap.put("BB", new Integer(5));
	    	productMap.put("DG", new Integer(6));
	    	productMap.put("SX", new Integer(7));
	    	productMap.put("MT", new Integer(8));
	    	productMap.put("DTP", new Integer(11));
	    	productMap.put("DVU", new Integer(12));
	    	productMap.put("NDS", new Integer(13));
	    	productMap.put("RPA", new Integer(14));
	    	productMap.put("IMP", new Integer(15));
	    	productMap.put("RET", new Integer(16));
	    	productMap.put("NSR", new Integer(17));
	    	productMap.put("NPA", new Integer(18));
	    	productMap.put("SIG", new Integer(19));
	    	productMap.put("ECO", new Integer(20));
		}
	}
	
	public int compare(String iSC1, String iSC2){
			if (productMap.containsKey(iSC1) && productMap.containsKey(iSC2)) 
			{
				Integer prodFeatureCode1 = productMap.get(iSC1);      
		        Integer prodFeatureCode2 = productMap.get(iSC2); 
            	return prodFeatureCode1.compareTo(prodFeatureCode2);	
			} 
			else if (!productMap.containsKey(iSC1) && productMap.containsKey(iSC2))
			{
				return 1; //if iSC1 is null, then it should be sorted later
			} 
			else if (productMap.containsKey(iSC1) && !productMap.containsKey(iSC2))
			{
				return -1;
			}
			else 
			{
				return 0;
			}
		}
}
