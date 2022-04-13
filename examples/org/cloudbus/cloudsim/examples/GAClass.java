package org.cloudbus.cloudsim.examples;


import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Chromosomes;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletCreator;
import org.cloudbus.cloudsim.DataCenterCreator;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.GenBroker;
import org.cloudbus.cloudsim.Gene;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmsCreator;
import org.cloudbus.cloudsim.core.CloudSim;

public class GAClass {

			
			private   List<Cloudlet> cloudletList;
			private   List<Vm> vmlist;
			
			private   int reqTasks;
			private   int reqVms;
			
			//////// Cloudlet
			private   int pesNumber;
			private   long[]length;

			///// VM
			private   int ram;
			private   int PEVM;
			
			////// Data center
			private   int nb;
			private   int ramDC;
			private   int mips;
			
			
			public GAClass(int reqTasks, int reqVms, int pesNumber, long[] length,int ram, int PEVM, int nb, int ramDC, int mips)
			{
				 this.reqTasks = reqTasks ;
				 this.reqVms = reqVms;
				 this.pesNumber = pesNumber;
				 this.length = length;
				 this.ram = ram;
				 this.PEVM = PEVM;
				 this.nb = nb;
				 this.ramDC = ramDC;
				 this.mips = mips;
				
				
			}
			
			public void  simulate()
			{
				
				Log.printLine("Starting Genetic...");

				try {
					int num_user = 1; // number of grid users
					Calendar calendar = Calendar.getInstance();
					boolean trace_flag = false; // mean trace events
					CloudSim.init(num_user, calendar, trace_flag);

					@SuppressWarnings("unused")
					Datacenter datacenter0 = DCcreate("Datacenter_0");
					

					GenBroker broker = createBroker();
					int brokerId = broker.getId();
					vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId,ram,PEVM);
					//vmlist = createVM(brokerId, 5); // creating 5 vms
					
					cloudletList = new CloudletCreator().createUserCloudlet(reqTasks, brokerId,pesNumber,length);
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

					printCloudletList(newList);

					Log.printLine("Genetic finished!");
					Log.printLine("Temps total de simulation " + dur.toMillis() + "MS");
				} catch (Exception e) {
					e.printStackTrace();
					Log.printLine("The simulation has been terminated due to an unexpected error");
				}
			}

		
			
			private   Datacenter DCcreate(String name)
			{
				Datacenter datacenter=new DataCenterCreator().createUserDatacenter(name, reqVms,nb,ramDC, mips);
				return datacenter;
			}

			private   GenBroker createBroker() {

				GenBroker broker = null;
				try {
					broker = new GenBroker("Broker");
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return broker;
			}

		    private   void printCloudletList(List<Cloudlet> list) {
		        int size = list.size();
		        Cloudlet cloudlet;

		        String indent = "    ";
		        Log.printLine();
		        Log.printLine("========== OUTPUT Genetic ==========");
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
