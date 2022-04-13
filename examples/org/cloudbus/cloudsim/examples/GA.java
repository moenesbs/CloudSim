package org.cloudbus.cloudsim.examples;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.time.Instant;

import org.cloudbus.cloudsim.Chromosomes;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletCreator;
import org.cloudbus.cloudsim.DataCenterCreator;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Gene;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmsCreator;
import org.cloudbus.cloudsim.core.CloudSim;

import org.cloudbus.cloudsim.GenBroker;

public class GA {

	private static List<Cloudlet> cloudletList;
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

	// //////////////////////// STATIC METHODS ////////////////////////////////////

	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample6...");

		try {
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);

        	@SuppressWarnings("unused")
			Datacenter datacenter0 = DCcreate("Datacenter_0");


			GenBroker broker = createBroker();
			int brokerId = broker.getId();
			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId,ramVM,PEVM);
			//vmlist = createVM(brokerId, 5); // creating 5 vms
			
			cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId,PEC,Task);
			//cloudletList = createCloudlet(brokerId, 200); // creating 200 cloudlets
			
			List<Cloudlet> sortedList = new ArrayList<Cloudlet>();
			for(Cloudlet cloudlet:cloudletList){
				sortedList.add(cloudlet);
			}
			int numCloudlets=sortedList.size();
			for(int i=0;i<numCloudlets;i++){
				Cloudlet tmp=sortedList.get(i);
				int idx=i;
				for(int j=i+1;j<numCloudlets;j++)
				{
					if(sortedList.get(j).getCloudletLength()<tmp.getCloudletLength())
					{
						idx=j;
						tmp=sortedList.get(j);
					}
				}
				Cloudlet tmp2 = sortedList.get(i);
				sortedList.set(i, tmp);
				sortedList.set(idx,tmp2);
			}
			
			ArrayList<Vm> sortedListVm = new ArrayList<Vm>();
		ArrayList<Vm> toBeUsedVm = new ArrayList<Vm>();
		ArrayList<Vm> leftOutVm = new ArrayList<Vm>();
		for(Vm vm:vmlist){
			sortedListVm.add(vm);
		}
		int numVms=sortedListVm.size();
		
		for(int i=0;i<numVms;i++){
			Vm tmp=sortedListVm.get(i);
			int idx=i;
			if(i<numCloudlets)
				toBeUsedVm.add(tmp);
			else
				leftOutVm.add(tmp);
			for(int j=i+1;j<numVms;j++)
			{
				if(sortedListVm.get(j).getMips()>tmp.getMips())
				{
					idx=j;
					tmp=sortedListVm.get(j);
				}
			}
			Vm tmp2 = sortedListVm.get(i);
			sortedListVm.set(i, tmp);
			sortedListVm.set(idx,tmp2);
		}
		ArrayList<Chromosomes> initialPopulation = new ArrayList<Chromosomes>();
		for(int j=0;j<numCloudlets;j++)
		{
			ArrayList<Gene> firstChromosome = new ArrayList<Gene>();
			
			for(int i=0;i<numCloudlets;i++)
			{
				int k=(i+j)%numVms;
				k=(k+numCloudlets)%numCloudlets;
				Gene geneObj = new Gene(sortedList.get(i),sortedListVm.get(k));
				firstChromosome.add(geneObj);
			}
			Chromosomes chromosome = new Chromosomes(firstChromosome);
			initialPopulation.add(chromosome);
		}
		
		int populationSize=initialPopulation.size();
		System.out.println("population"+populationSize);
		Random random = new Random();
		for(int itr=0;itr<20;itr++)
		{
			int index1,index2;
			index1=random.nextInt(populationSize) % populationSize;
			index2=random.nextInt(populationSize) % populationSize;
			ArrayList<Gene> l1= new ArrayList<Gene>();
			l1=initialPopulation.get(index1).getGeneList();
			Chromosomes chromosome1 = new Chromosomes(l1);
			ArrayList<Gene> l2= new ArrayList<Gene>();
			l2=initialPopulation.get(index2).getGeneList();
			Chromosomes chromosome2 = new Chromosomes(l2);
			double rangeMin = 0.0f;
		    double rangeMax = 1.0f;
		    Random r = new Random();
		    double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			if(crossProb<0.5)
			{
				int i,j;
				i=random.nextInt(numCloudlets) % numCloudlets;
				j=random.nextInt(numCloudlets) % numCloudlets;
				Vm vm1 = l1.get(i).getVmFromGene();
				Vm vm2 = l2.get(j).getVmFromGene();
				chromosome1.updateGene(i, vm2);
				chromosome2.updateGene(j, vm1);
				initialPopulation.set(index1, chromosome1);
				initialPopulation.set(index2, chromosome2);
			}
			double mutProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			if(mutProb<0.5)
			{
				int i;
				i=random.nextInt(populationSize) % populationSize;
				ArrayList<Gene> l= new ArrayList<Gene>();
				l=initialPopulation.get(i).getGeneList();
				Chromosomes mutchromosome = new Chromosomes(l);
				int j;
				j=random.nextInt(numCloudlets) % numCloudlets;
				Vm vm1 = sortedListVm.get(0);
				mutchromosome.updateGene(j,vm1);
			}
		}
		int fittestIndex=0;
		double time=1000000;
		
		for(int i=0;i<populationSize;i++)
		{
			ArrayList<Gene> l= new ArrayList<Gene>();
			l=initialPopulation.get(i).getGeneList();
			double sum=0;
			for(int j=0;j<numCloudlets;j++)
			{
				Gene g = l.get(j);
				Cloudlet c = g.getCloudletFromGene();
				Vm v = g.getVmFromGene();
				double temp = c.getCloudletLength()/v.getMips();
				sum+=temp;
			}
			if(sum<time)
			{
				time=sum;
				fittestIndex=i;
			}
		}
		
		ArrayList<Gene> result = new ArrayList<Gene>();
		result = initialPopulation.get(fittestIndex).getGeneList();
		
			List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
			List<Vm> finalvmlist = new ArrayList<Vm>();
			
			
			
			
			for(int i=0;i<result.size();i++)
			{
				finalcloudletList.add(result.get(i).getCloudletFromGene());
				finalvmlist.add(result.get(i).getVmFromGene());
				Vm vm=result.get(i).getVmFromGene();
			}
			
			broker.submitVmList(finalvmlist);
			broker.submitCloudletList(finalcloudletList);
			
			Instant start = Instant.now();
			CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();
			Instant end = Instant.now();
			Duration dur = Duration.between(start, end);

			printCloudletList(newList,vmlist);

			Log.printLine("CloudSimExample6 finished!");
			Log.printLine("Temps total de simulation " + dur.toMillis() + "MS");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	
	private static Datacenter DCcreate(String name)
	{
		Datacenter datacenter=new DataCenterCreator().createUserDatacenter(name,reqVms,nb,ramDC,mips);		
		return datacenter;
	}

	private static GenBroker createBroker() {

		GenBroker broker = null;
		try {
			broker = new GenBroker("Broker");
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