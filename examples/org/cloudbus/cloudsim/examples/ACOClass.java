package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletCreator;
import org.cloudbus.cloudsim.DataCenterCreator;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ACOBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmsCreator;
import org.cloudbus.cloudsim.core.CloudSim;

public class ACOClass {


			private static List<Cloudlet> cloudletList;
			private static List<Vm> vmlist;
			
			private static int reqTasks;
			private static int reqVms;
			
			
			//Cloudlets
			private static int pesNumber;
			private static long[]length;
			
			
			///// VMM
			private static int ram;
			private static int PEVM;
			
			////// Data center
			private static int nb;
			private static int ramDC;
			private static int mips;
			
			
			///// ACO Prams
			private static int m;
			private static double Q;
			private static double alpha;
			private static double beta;
			private static double gamma;
			private static double rho;
			
			
			
			public ACOClass(int reqTasks, int reqVms, int pesNumber, long[] length,int ram, int PEVM, int nb, int ramDC, int mips,
					int m, double Q, double alpha, double beta, double gamma, double rho) ///// Ant Param
			{
				ACOClass.reqTasks = reqTasks ;
				ACOClass.reqVms = reqVms;
				ACOClass.pesNumber = pesNumber;
				ACOClass.length = length;
				ACOClass.ram = ram;
				ACOClass.PEVM = PEVM;
				ACOClass.nb = nb;
				ACOClass.ramDC = ramDC;
				ACOClass.mips = mips;
				ACOClass.m = m;
				ACOClass.Q = m;
				ACOClass.alpha = alpha;
				ACOClass.beta = beta;
				ACOClass.gamma = gamma;
				ACOClass.rho = rho;	
			}
			
			public void  simulate()
			{
				
				Log.printLine("Starting ACO...");

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
		            	ACOBroker broker = createBroker( m,  Q,  alpha,  beta,  gamma,  rho);
		            	int brokerId = broker.getId();

		            	//Fourth step: Create one virtual machine
		            	vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId,ram,PEVM);


		            	//submit vm list to the broker
		            	broker.submitVmList(vmlist);


		            	//Fifth step: Create two Cloudlets
		            	//cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId);
		            	cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId,pesNumber,length);
		  	
		            	//submit cloudlet list to the broker
		            	broker.submitCloudletList(cloudletList);
		            	
			
		            	//call the scheduling function via the broker
		            	//broker.scheduleTaskstoVms();
			
		        	
		            	// Sixth step: Starts the simulation
		            	Instant start = Instant.now();
		            	CloudSim.startSimulation();


		            	// Final step: Print results when simulation is over
		            	List<Cloudlet> newList = broker.getCloudletReceivedList();

		            	CloudSim.stopSimulation();
		            	Instant end = Instant.now();
		            	Duration time = Duration.between(start, end);

		            	printCloudletList(newList);
		            	

		            	Log.printLine("ACO Finished!");
		            	Log.printLine("Temps total de simulation " + time.toMillis() + "MS");
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		            Log.printLine("The simulation has been terminated due to an unexpected error");
		        }
			}

			
			private static Datacenter createDatacenter(String name){
				Datacenter datacenter=new DataCenterCreator().createUserDatacenter(name, reqVms,nb,ramDC, mips);		

		        return datacenter;

		    }
			
		    private static ACOBroker createBroker(int m, double Q, double alpha, double beta, double gamma, double rho){

		    	ACOBroker broker = null;
		        try {
		        	
				broker = new ACOBroker("Broker",m,  Q,  alpha,  beta,  gamma,  rho);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
		    	return broker;
		    }

		    private static void printCloudletList(List<Cloudlet> list) {
		        int size = list.size();
		        Cloudlet cloudlet;

		        String indent = "    ";
		        Log.printLine();
		        Log.printLine("========== OUTPUT  ACO ==========");
		        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
		                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time"+ indent + "Waiting Time");
		        double waitTimeSum = 0.0;
		        double CPUTimeSum = 0.0;
		        int totalValues=0;
		        DecimalFormat dft = new DecimalFormat("###.##");
		        for (int i = 0; i < size; i++) {
		            cloudlet = list.get(i);
		            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

		            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
		                Log.print("SUCCESS");
		                CPUTimeSum = CPUTimeSum + cloudlet.getActualCPUTime();
		                waitTimeSum = waitTimeSum + cloudlet.getWaitingTime();
		                totalValues++;
		            	Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
		                     indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
	                             indent + indent + dft.format(cloudlet.getFinishTime())+ indent + indent + dft.format(cloudlet.getWaitingTime()));
		            }
		        }
		        Log.printLine();
		        Log.printLine();
		        Log.printLine("TotalCPUTime : " + CPUTimeSum);
		        Log.printLine("TotalWaitTime : " + waitTimeSum);
		        Log.printLine("TotalCloudletsFinished : " + totalValues);
		        Log.printLine();
		        Log.printLine();
		        
		    }
		}