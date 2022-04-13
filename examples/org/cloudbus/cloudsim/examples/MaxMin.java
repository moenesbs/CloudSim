package org.cloudbus.cloudsim.examples;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.Duration;
import java.time.Instant;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import org.cloudbus.cloudsim.CloudletCreator;
import org.cloudbus.cloudsim.MaxMinBroker;
import org.cloudbus.cloudsim.DataCenterCreator;
import org.cloudbus.cloudsim.VmsCreator;

public class MaxMin {


	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;


	private static int reqTasks = ConstSIm.reqTasks;
	private static int min = ConstSIm.min;
	private static int max = ConstSIm.max;	
	private static int PEC = ConstSIm.PEC;
	static final long[] Task = RandomTasks(reqTasks,min,max);

////// VM
	private static int reqVms = ConstSIm.reqVms;
	private static int ramVM = ConstSIm.ramVM;
	private static int PEVM = ConstSIm.PEVM;
	
	
	////// DC
	private static int mips = ConstSIm.mips;
	private static int nb = ConstSIm.nbDC;
	private static int ramDC = ConstSIm.ramDC;
	
	

	

	public static void main(String[] args) {

		Log.printLine("Starting Max-Min...");

	        try {
	        	// First step: Initialize the CloudSim package. It should be called
	            	// before creating any entities.
	            	int num_user = 1;   // number of cloud users
	            	Calendar calendar = Calendar.getInstance();
	            	boolean trace_flag = false;  // mean trace events

	            	// Initialize the CloudSim library
	            	CloudSim.init(num_user, calendar, trace_flag);

	            	// Second step: Create Datacenters
	            	//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
	            	@SuppressWarnings("unused")
					Datacenter datacenter0 = createDatacenter("Datacenter_0");

	            	//Third step: Create Broker
	            	MaxMinBroker broker = createBroker();
	            	int brokerId = broker.getId();

	            	//Fourth step: Create one virtual machine
	            	//vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId);
	            	
	            	//Max-min outperform min-min
	            	vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId,ramVM,PEVM);


	            	//submit vm list to the broker
	            	broker.submitVmList(vmlist);


	            	//Fifth step: Create two Cloudlets
	            	//cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId);
	            	
	            	//Max-min outperform min-min
	            	cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId,PEC,Task);
	            	
	            	//Min-min outperform max-min
	            	//cloudletList = new CloudletCreator2().createUserCloudlet(reqTasks, brokerId);
      	
	            	//submit cloudlet list to the broker
	            	broker.submitCloudletList(cloudletList);
	            	
    	
	            	//call the scheduling function via the broker
	            	broker.scheduleTaskstoVms();
   	
            	
	            	// Sixth step: Starts the simulation
	            	Instant start = Instant.now();
	            	CloudSim.startSimulation();


	            	// Final step: Print results when simulation is over
	            	List<Cloudlet> newList = broker.getCloudletReceivedList();

	            	CloudSim.stopSimulation();
	            	Instant end = Instant.now();
	            	Duration time = Duration.between(start, end);
	            			

	         	printCloudletList(newList,vmlist);

	            	Log.printLine("EnhancedMaxMin finished!");
	            	Log.printLine("Temps total de simulation " + time.toMillis() + "MS");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            Log.printLine("The simulation has been terminated due to an unexpected error");
	        }
	    }

		private static Datacenter createDatacenter(String name){
			Datacenter datacenter=new DataCenterCreator().createUserDatacenter(name,reqVms,nb,ramDC,mips);			

	        return datacenter;

	    }

	    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	    //to the specific rules of the simulated scenario
	    private static MaxMinBroker createBroker(){

	    	MaxMinBroker broker = null;
	        try {
			broker = new MaxMinBroker("Broker");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	    	return broker;
	    }


	    
	    private static void printCloudletList(List<Cloudlet> list, List<Vm> liste) {
	    	
	    	Map<Integer, Double> dictTimeVM = new HashMap<Integer, Double>();
	    	Map<Integer, Double> dictCPUVM = new HashMap<Integer, Double>();
	        int size = list.size();
	        Cloudlet cloudlet;
	        double S = 0;
	        

	        String indent = "    ";
	        Log.printLine();
	        Log.printLine("========== OUTPUT  ACO ==========");
	        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time"+ indent + "Waiting Time" + indent + "Resource Use" );
	        double waitTimeSum = 0.0;
	        double CPUTimeSum = 0.0;
	        int totalValues=0;
	        DecimalFormat dft = new DecimalFormat("###,##");
	        for (int i = 0; i < size; i++) {
	            cloudlet = list.get(i);
	            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

	            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
	                Log.print("SUCCESS");
	                CPUTimeSum = CPUTimeSum + cloudlet.getActualCPUTime();
	                waitTimeSum = waitTimeSum + cloudlet.getWaitingTime();
	                totalValues++;
	            	Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
	                     indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + indent + dft.format(cloudlet.getExecStartTime())+
	                         indent + indent +indent  + indent + dft.format(cloudlet.getFinishTime())+ indent + indent + dft.format(cloudlet.getWaitingTime()) 
	                         + indent + indent + indent + indent+ cloudlet.getUtilizationOfCpu(cloudlet.getActualCPUTime())
	                         + indent);
	         
	     
	            		S = S +cloudlet.getUtilizationOfCpu(cloudlet.getActualCPUTime());
	
	            }
	            int test =-1;
	            
	            for (int key : dictTimeVM.keySet())
	            {
	            	if (key == cloudlet.getVmId() )
	            		test = 0;
	            		
	            }
	            if (test == 0)
	            {
	            	dictTimeVM.put(cloudlet.getVmId(),dictTimeVM.get(cloudlet.getVmId()) + cloudlet.getActualCPUTime());
	            }
	            else 
	            {	
	            	dictTimeVM.put(cloudlet.getVmId(),cloudlet.getActualCPUTime());
	            }
	            
	            ///////////////////// CPU ////////
	            
	            int teste = -1;
	            for (int key : dictCPUVM.keySet())
	            {
	            	if (key == cloudlet.getVmId() )
	            		teste = 0;
	            		
	            }
	            if (teste == 0)
	            {
	            	dictCPUVM.put(cloudlet.getVmId(),dictCPUVM.get(cloudlet.getVmId()) + cloudlet.getUtilizationOfCpu(cloudlet.getActualCPUTime()));
	            }
	            else 
	            {	
	            	dictCPUVM.put(cloudlet.getVmId(),cloudlet.getUtilizationOfCpu(cloudlet.getActualCPUTime()));
	            }
	            
	    
	                  
	        
	        }
	       
	        Log.printLine();
	        Log.printLine();
	        Log.printLine("TotalCPUTime : " + CPUTimeSum);
	        Log.printLine("TotalWaitTime : " + waitTimeSum);
	        Log.printLine("TotalCloudletsFinished : " + totalValues);
	        Log.printLine("CPU total USE : " + S);
	        Log.printLine("CPU % USE : " + (S/list.size()*100));
	        Log.printLine();
	        Log.printLine();
	        

	        Log.printLine(dictTimeVM);
	        
	        /// NB DE CPU UTILIISE PAR VM
	        Log.printLine(dictCPUVM);
	        double avg = 0.0;
	        
	        for (double val : dictCPUVM.values()) 
	        {
	        	avg = avg + val;
	        	
	        }
	        
	      
	        double avgT = 0.0;
	        List<Double> time = new ArrayList<>();
	        for (double val : dictTimeVM.values())
	        {
	        	time.add(val);
	        	avgT = avgT + val;
	        }
	        avgT = avgT /4 ;
	        
	        

	        
	        
	        Log.printLine("Average CPU USE per VM " + avg/reqVms );
	        Log.printLine("Imbalance Degree  " + ((Collections.max(time) + Collections.min(time))/ avgT ) );
	        
	        
	    
	    }
	    
		static long[] RandomTasks(int nbTask,int min,int max)
		{
			long[] Tasks ;
			Tasks = new long[nbTask];
			Random generator = new Random(10);
			int randomNum = 0;
			for (int i = 0; i < nbTask; i++) {
				randomNum = min + generator.nextInt(max - min + 1);
				Tasks[i]= randomNum;
			
				
			}
			
			return Tasks ;
				
		
		}
}