package org.cloudbus.cloudsim.examples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cloudbus.cloudsim.Log;

public class Test {
	
	
	public static void main(String[] args) {
		 Map<Integer, Integer> dict = new HashMap<Integer, Integer>();
	
		
		for (int i = 0; i < 10; i++)
		{
			dict.put(i,0);
			
		}
		
	
		
		Random generator = new Random(10);
		int randomNum = 0;
		for (int i = 0; i < 10; i++) {
			randomNum = 100 + generator.nextInt(999 - 100 + 1);
			dict.put(i, dict.get(i) + randomNum);
			
		}
		
		
		Random x = new Random(10);
		int j = 0;
		for (int i = 0; i < 10; i++) {
			j = 100 + x.nextInt(999 - 100 + 1);
			dict.put(i, dict.get(i) - j);
			
		}
		
		Log.printLine(dict);
		
		
	
	
	
	
	}
}
	
	
		
	    
	    
	

