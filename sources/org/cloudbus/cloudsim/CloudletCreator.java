package org.cloudbus.cloudsim;

import java.util.ArrayList;

public class CloudletCreator {

	
	//cloudlet creator
	public ArrayList<Cloudlet> createUserCloudlet(int reqTasks,int brokerId,int pesNumber, long[]length){
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
		
    	//Cloudlet properties
    	int id = 0;
    	//int pesNumber=1;
    	//long[] length = {5000, 6000, 7000, 2000, 3000, 4000, 2500, 3500, 5500, 4500, 3000, 5000, 6000, 1000};
    	long fileSize = 300;
    	long outputSize = 300;
    	UtilizationModel utilizationModel = new UtilizationModelStochastic(10);
    	   	
    	
    	for(id=0;id<reqTasks;id++){
    		
    		Cloudlet task = new Cloudlet(id, length[id], pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
    		task.setUserId(brokerId);
    		
    		
    		//System.out.println("Task"+id+"="+(task.getCloudletLength()));
    		
    		//add the cloudlets to the list
        	cloudletList.add(task);
    	}

 //   	System.out.println("SUCCESSFULLY Cloudletlist created :)");

		return cloudletList;
		
		
		
	}


}
