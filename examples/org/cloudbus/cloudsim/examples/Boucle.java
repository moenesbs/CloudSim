package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Constants;
import org.cloudbus.cloudsim.Log;
public class Boucle {
	public static void main(String[] args) {
		
		
		////// Cloulets
		final int reqTasks = 200;
		final int reqVms = 4;
		final int min = 10;
		final int max = 99;
    	final int pesNumberTASK=1;
    	final long[] Task = RandomTasks(reqTasks,min,max);
    	
    	////// VMs
    	final int ram = 512;   // 512 - 1024
    	final int PEVM = 1 ;
    	
    	
    	////// Data Center
    	final int mips = 1000*reqVms;
    	final int ramDC = 2048; /// 2048 -  4096
    	final int nb = 1;   /// NB PE /// 1  - 4 - 8 
    	final int nbDC = 1 ;
    	
    	
    	//// PSO CTE
    //	final int population
    	final int popluation = 10;
    	
    	Constants PSO_CTE = new Constants(reqTasks,nbDC,popluation);
    	PSO_CTE.set_DC(nbDC);
    	PSO_CTE.set_popu(popluation);
    	PSO_CTE.set_tasks(reqTasks);
    	
    	
    	///////// Static
    	
    	Log.printLine("Static Simple ALGOS");
    	Log.printLine(" ");
    	Log.printLine(" ");

		SJFClass SJF= new SJFClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); /// Shortest Job First Algorithm
	
		FCFSClass FCFS =  new FCFSClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); /// First Come First Serve Algorithm

		GAClass GA = new GAClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); // Genetic Algorithm
	
		MaxminClass MaxMin = new MaxminClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips);  // Max Min Algorithm

		MinminClass Minmin = new MinminClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); // Min Min Algorithm

		RoundRobinCLass RR = new RoundRobinCLass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); // Round Robin Algorithm

		Log.printLine(" ");
		SJF.simulate();
		Log.printLine(" ");
		FCFS.simulate();
		Log.printLine(" ");
		GA.simulate();
		Log.printLine(" ");
		MaxMin.simulate();
		Log.printLine(" ");
		Minmin.simulate();
		Log.printLine(" ");
		RR.simulate();
		Log.printLine(" ");
	
  
///////////////////////////////// Meta Heuristics
		/*
		Log.printLine(" ");
		Log.printLine("//////////////////////////////////////////////////////// ");
    	Log.printLine("Meta Heuristic  ALGOS");
    	Log.printLine(" ");
    	Log.printLine(" ");



		FPAClass FPA = new FPAClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips);
		HoneyBee HoneyBee = new HoneyBee(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips);
		Acotrue ACO = new Acotrue(Task.length,reqVms, pesNumberTASK,Task,ram, PEVM,nb,ramDC,mips);
		SelClass Sel = new SelClass(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips);
		PSO PSO = new PSO(Task.length,reqVms,pesNumberTASK,Task,ram,PEVM,nb,ramDC,mips); 
		
		FPA.simulate();
		Log.printLine(" ");
		Sel.simulate();
		Log.printLine(" ");
		PSO.simulate();
		Log.printLine(" ");
		ACO.simulate();
		Log.printLine(" ");
		HoneyBee.simulate();
		Log.printLine(" ");
		
		*/

	}
	
	static long[] RandomTasks(int nbTask,int min,int max)
	{
		long[] Tasks ;
		Tasks = new long[nbTask];
		for (int i = 0; i < nbTask; i++) {
			int x =(int)Math.floor(Math.random()*(max-min+1)+min);
			Tasks[i]= x;
		}
		
		return Tasks ;
			
	
	}
}