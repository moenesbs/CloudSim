package org.cloudbus.cloudsim;

public class Constants {
    public  static int NO_OF_TASKS  ;// number of Cloudlets;
    public  static int NO_OF_DATA_CENTERS ; // number of Datacenters;
    public  static  int POPULATION_SIZE ; // Number of Particles.
    
    
    public Constants(int NO_OF_TASKS,int NO_OF_DATA_CENTERS,int POPULATION_SIZE)
    {
    	Constants.NO_OF_TASKS=NO_OF_TASKS;
    	Constants.NO_OF_DATA_CENTERS=NO_OF_DATA_CENTERS;
    	Constants.POPULATION_SIZE=POPULATION_SIZE;
    }
    
    public static  void set_tasks(int n)
    {
    	NO_OF_TASKS=n;
    }
    
    public  static void set_DC(int n)
    {
    	NO_OF_DATA_CENTERS=n;
    }
    public static  void set_popu(int n)
    {
    	POPULATION_SIZE=n;
    }
    
    public static int get_population()
    {
    	return POPULATION_SIZE;
    }
    
    public static int get_NBDC()
    {
    	return NO_OF_DATA_CENTERS;
    }
    
    public  static int get_nbTask()
    {
    	return NO_OF_TASKS;
    }
    
    
    
    
    
}