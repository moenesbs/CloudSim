package org.cloudbus.cloudsim.HoneyBee;

import java.util.ArrayList;

public class lotteryTicket {
	public double cpuTime;
	public int vmid;
	public ArrayList<Integer> tickets;
	public int numofTickets;
	public lotteryTicket(double cpu, int vm)
	{
		this.cpuTime = cpu;
		this.vmid = vm;
		tickets = new ArrayList<Integer>() ;
	}
}