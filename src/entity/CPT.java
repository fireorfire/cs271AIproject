package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import entity.Variable;

public class CPT {
	private ArrayList<Variable> varSeq = new ArrayList<Variable>();
	//Variable. First one is one element's id, the rest is its dependencies' id.
	private ArrayList<Double> prob = new ArrayList<Double>();
	//Double. probability according to domain and elements
	
	public CPT(){
		
	}
	public CPT(Variable variable, Variable[] var) {
		int id = variable.getId();
		for(int i = 0; i < variable.getLenDepen(); i++){
			varSeq.add(var[variable.getDependency(i)]);
		}
		for(int i = 0; i < variable.getLenProb(); i++){
			prob.add(new Double(variable.getProbability(i)));
		}
		varSeq.add(variable);
	}
	public CPT(CPT cpt) {
		varSeq 	= new ArrayList<Variable>(cpt.getVarSeq());
		prob 	= new ArrayList<Double>();
		for(int i = 0; i < cpt.getProbSize();i++)
			prob.add(new Double(cpt.getProb(i)));
	}
	private Collection<Double> getProb() {
		return prob;
	}
	public void insertVarSeq(Variable variable){
		this.varSeq.add(variable);
	}
	public int getProbSize(){
		return prob.size();
	}
	public void insertProb(double probability){
		this.prob.add(new Double(probability));
	}
	public int getVarSeqSize() {
		return varSeq.size();
	}
	public Variable getVarSeq(int j) {
		return (Variable)varSeq.get(j);
	}
	public ArrayList<Variable> getVarSeq() {
		return varSeq;
	}
	public void removeEvid(int id, int value) {
		int seq = 0;
		for(seq = 0; seq < varSeq.size();seq++){
			if(varSeq.get(seq).getId() == id)break;
		}
	}
	public void condenseEvid(Variable variable, int value) {
		int i ;
		for(i = 0; i < varSeq.size(); i++){
			if(varSeq.get(i) == variable)break;
		}
		if(i == varSeq.size()){
			System.out.println(variable.getId()+" couldn't find evid variable in CPT");
			System.exit(0);
			return;
		}
		// lowStep is the production of all domain of variable following varSeq.get(i).
		int lowStep = 1;
		for(int j = i+1; j < varSeq.size(); j++){
			lowStep *= varSeq.get(j).getDomainSize();
		}
		int oldsize = prob.size();
//		System.out.println(i+"'s lowStep:"+lowStep);
//		System.out.println(i+"'s prob's size:"+prob.size());
		// upStep is the production of lowStep and varSeq.get(i)'s domainSize
		int upStep = lowStep * variable.getDomainSize();
		
		// remove probabilities within each upStep span (let's call it base)
		for(int base = prob.size()-upStep; base >= 0; base -= upStep){
			// keep those value fits the evidence, and remove the rest
			// probabilities that consecutively fit the value are lowStep in total amount.
			
			// remove the probability that value is greater than evidence's value
			for(int j = base + upStep - 1; j >= base + (value+1)*lowStep; j--){
				prob.remove(j);
			}
			// remove the probability that value is smaller than evidence's value
			for(int j = base + value * lowStep - 1; j >= base ; j--){
				prob.remove(j);
			}
		}
		if(prob.size()==oldsize){
			System.out.println("id:"+varSeq.get(i).getId()+" didn't condense. prob size:"+prob.size());
		}
		
		varSeq.remove(i);
	}
	public double getProb(int i) {
		return prob.get(i);
	}
	public Double setProb(int i, double d) {
		return prob.set(i, d);
	}
	public Double removeProb(int j) {
		return prob.remove(j);
	}
	public void addProb(int i, Double d) {
		prob.add(i, d);
	}
	public void removeVarSeq(int index) {
		varSeq.remove(index);
	}
}
