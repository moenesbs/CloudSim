package org.cloudbus.cloudsim.HoneyBee;


public class IntervalCalculation {
	public double starttime;
	public double endtime;
	public int cloudletID;
	public IntervalCalculation(double start, double end)
	{
		starttime = start;
		endtime = end;
	}
	public IntervalCalculation()
	{
		// do nothing
	}
}