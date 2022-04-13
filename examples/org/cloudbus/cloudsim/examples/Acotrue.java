
package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.time.Duration;
import java.time.Instant;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletCreator;
import org.cloudbus.cloudsim.DataCenterCreator;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmsCreator;
import org.cloudbus.cloudsim.core.CloudSim;

public class Acotrue {

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
		Log.printLine("Starting ACO...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);


			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			

			

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId,ramVM,PEVM);
			
			
			cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId, PEC,Task);
			
			int numVMs=vmlist.size();
			int numCloudlets=cloudletList.size();
			List<Vm> finalvmlist = new ArrayList<Vm>();
			
			
			double factor=-100000;
			int T_MAX=10;
			int c=5;
			double[][][] T = new double[numVMs+1][numCloudlets+1][T_MAX+1];
			int[] M = new int[numVMs+1];
			double a=.3;
			double Q=10;
			//double[] tabu = new double[numVMs+1];
			int m=numVMs;
			
			for(int i=0;i<numVMs;i++)
			{
				for(int j=0;j<numCloudlets;j++)
				{
					for(int k=0;k<=T_MAX;k++)
					{
						T[i][j][k]=c;
					}
				}
			}
			
			for(int i=0;i<numVMs;i++)
				M[i]=i;
			
			for(int t=0;t<T_MAX;t++)
			{
				double[] taskProb = new double[numCloudlets+1];
				int[] taskVm = new int[numCloudlets+1];
				
				for(int i=0;i<numCloudlets;i++)
				{
				taskProb[i]=-10000000;
				taskVm[i]=0;
				}
				
				for(int k=0;k<m;k++)
				{
					boolean[] tabu = new boolean[numVMs+1];
					for(int i=0;i<m;i++)
						tabu[i]=false;
					for(int task=0;task<numCloudlets;task++)
					{
						int vmIndex=0;
						double ansProb=0;
						double reqd=1;
						for(int vm=0;vm<numVMs;vm++)
						{
							if(tabu[vm]==false)
							{
								double tempProb=0;
								double dij=cloudletList.get(task).getCloudletLength();
								dij=dij/(vmlist.get(vm).getMips()*vmlist.get(vm).getNumberOfPes());
								tempProb=Math.pow(T[vm][task][t], a);
								tempProb/=dij;
								if(tempProb>ansProb)
								{
									ansProb=tempProb;
									vmIndex=vm;
									reqd=dij;
								}
							}
						}
						if(taskProb[task]<ansProb){
							taskProb[task]=ansProb;
							taskVm[task]=vmIndex;
						}
						tabu[vmIndex]=true;
						double delta=Q/reqd;
						T[vmIndex][task][t]+=delta;
					}
				}
				double tmpfactor=0;
				for(int i=1;i<=numCloudlets;i++)
				{
					tmpfactor+=(taskProb[i]);
				}
				if(tmpfactor>factor)
				{
					for(int i=1;i<numCloudlets;i++)
					{
						finalvmlist.add(vmlist.get(taskVm[i]));
					}
				}
			}
			
			
			broker.submitVmList(finalvmlist);
			broker.submitCloudletList(cloudletList);

			// Fifth step: Starts the simulation
			Instant start = Instant.now();
			CloudSim.startSimulation();
			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();
        	Instant end = Instant.now();
        	Duration time = Duration.between(start, end);

			printCloudletList(newList,finalvmlist);

			Log.printLine("ACO finished!");
			Log.printLine("Temps total de simulation " + time.toMillis() + "MS");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	
	private static Datacenter createDatacenter(String name){
		Datacenter datacenter=new DataCenterCreator().createUserDatacenter(name, reqVms,nb,ramDC, mips);			

        return datacenter;
	}
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
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
