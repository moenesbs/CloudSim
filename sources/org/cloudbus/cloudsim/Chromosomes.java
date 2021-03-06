package org.cloudbus.cloudsim;


import java.util.ArrayList;

public class Chromosomes{
	
	protected ArrayList<Gene> geneList;
	
	public Chromosomes(ArrayList<Gene> geneList){
		this.geneList=geneList;		
	}
	
	public ArrayList<Gene> getGeneList(){
		return this.geneList;
	}
	
	public void updateGene(int index,Vm vm){
		Gene gene=this.geneList.get(index);
		gene.setVmForGene(vm);
		this.geneList.set(index, gene);
	}
}