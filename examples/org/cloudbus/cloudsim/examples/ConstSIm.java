package org.cloudbus.cloudsim.examples;

public class ConstSIm {
	
	/////// Tasks
    public static  int reqTasks =30	;
    public static  int min = 10 ;
    public static  int max = 99 ; 
    public static int PEC = 1;
    
    
//////VM
    public static int reqVms = 4;
    public static int ramVM = 1024;
    public static int PEVM = 8;
	
	
	////// DC
    public static int mips = 1000	*reqVms;
    public static int nbDC = 8;
    public static int ramDC = 4096;
}
