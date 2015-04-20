package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Variable {
	private int id;
	private String name;
	private int domainSize;
	private String[] label;
	private int lenDepen;
	private int[] dependency;
	private int lenProb;
	private double[] probability;
	
	public Variable(int id, int domainSize){
		this.id = id;
		this.domainSize = domainSize;
		label = new String[domainSize];
	}
//	public Variable(Variable variable) {
//		this.id = variable.getId();
//		this.name = variable.getName();
//		this.domainSize = variable.getDomainSize();
//		this.label = variable.getLabel();
//		this.lenDepen = variable.getLenDepen();
//		this.dependency = variable.getDependency().clone();
//		this.lenProb = variable.getLenProb();
//		this.probability = variable.getProbability().clone();
//		this.neighbor = new HashSet<Variable>(variable.getNeighbor());
//		this.relatedCPT = new ArrayList<CPT>(variable.getRelatedCPT());
//	}
//	private ArrayList<CPT> getRelatedCPT() {
//		return relatedCPT;
//	}
	private double[] getProbability() {
		return probability;
	}
	private int[] getDependency() {
		return dependency;
	}
	private String[] getLabel() {
		return this.label;
	}
	public void setLabel(int index, String lab){
		if(index < domainSize && index >= 0)
			this.label[index] = lab;
	}
	public String getLabel(int index){
		if(index >= this.domainSize)return "";
		return this.label[index];
	}
	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setLenDepen(int lenDepen){
		this.lenDepen = lenDepen;
		dependency = new int[lenDepen];
	}
	public int getLenDepen(){
		return this.lenDepen;
	}
	public void setDependency(int index, int id){
		if(index < this.lenDepen && index >= 0)
			this.dependency[index] = id;
	}
	public int getDependency(int index){
		if(index >= this.lenDepen)return -1;
		return dependency[index];
	}
	public void setLenProb(int lenProb){
		this.lenProb = lenProb;
		probability = new double[lenProb];
	}
	public void setProbability(int index, double prob){
		if(index < this.lenProb && index >= 0)
			this.probability[index] = prob;
	}
	public double getProbability(int index){
		if(index >= this.lenProb)return -1.0;
		return this.probability[index];
	}
	public int getDomainSize() {
		return domainSize;
	}
	public int getLenProb() {
		return this.lenProb;
	}
//	public void insertRelatedCPT(CPT cpt) {
//		relatedCPT.add(cpt);
//	}
//	public void insertNeighbor(Variable variable) {
//		neighbor.add(variable);
//	}
//	public HashSet<Variable> getNeighbor() {
//		return neighbor;
//	}
//	public void condenseEvid(int value) {
//		//for every CPT in relatedCPT, keep the probabilities that fit the value
//		//		delete 1. the probability whose value doesn't fit in CPT.
//		//			   2. the variable in varSeq of Evidence
//		for(int i = 0; i < relatedCPT.size(); i++){
//			CPT cpt = relatedCPT.get(i);
//			cpt.condenseEvid(this, value);
//		}
//	}
//	public void clearNeigh() {
//		neighbor.clear();
//	}
//	public void clearCPT() {
//		relatedCPT.clear();
//	}
}
