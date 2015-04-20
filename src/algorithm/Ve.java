package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import entity.CPT;
import entity.Evidence;
import entity.Variable;

import IO.Input;

public class Ve {

	private double result = 1.0;
	private HashSet<CPT> sCPT = new HashSet<CPT>();
	//CPT conditional probability table
	private int numVar;
	private Variable[] var;
	private int lenEvid;
	private Evidence[] evid;
	private ArrayList<HashSet<Variable>> neighbor = new ArrayList<HashSet<Variable>>();
	private ArrayList<ArrayList<CPT>> relatedCPT = new ArrayList<ArrayList<CPT>>();
	
	public Ve(){
	}
	public Ve(Input in){
		this.numVar 	= in.getNumVar();
		this.var 		= in.getVar();
		this.lenEvid 	= in.getLenEvid();
		this.evid 		= in.getEvid();
		//build CPT list
		this.buildCPT();
	}
	private void buildCPT() {
		for(int i = 0; i < numVar; i++){
			neighbor.add(new HashSet<Variable>());
			relatedCPT.add(new ArrayList<CPT>());
		}
		for(int i = 0; i < this.numVar; i++){
			CPT cpt = new CPT(var[i], var);
			sCPT.add(cpt);
			//insert cpt to variable's relatedCPT 
			//set variable's neighbor
			ArrayList<Variable> varseq = cpt.getVarSeq();
			for(int j = 0; j < varseq.size(); j++){
				Variable v = cpt.getVarSeq(j);
				relatedCPT.get(v.getId()).add(cpt);
				for(int k = 0; k < varseq.size(); k++){
					if(varseq.get(k) != v)
						neighbor.get(v.getId()).add(varseq.get(k));
				}
			}
		}
//		for(int i = 0; i < this.numVar; i++){
//			HashSet<Variable> hs = neighbor.get(i);
//			System.out.print(var[i].getId()+"'s neighbor ");
//			for(Iterator<Variable> it = hs.iterator();it.hasNext();){
//				System.out.print(it.next().getId()+" ");
//			}System.out.println();
//		}
		
	}
	public double calculate(){
		System.out.println("Start Variable Elimination Calculation");
		System.out.println("number of variables:"+this.numVar);
		System.out.println("number of Evidences:"+this.lenEvid);
		
		//distinguish evidence and non-evidence with two lists
		ArrayList<Variable> lEvid 		= new ArrayList<Variable>();
		ArrayList<Variable> lNonEvid 	= new ArrayList<Variable>();
		
		int evidPointer = 0;
		for(int i = 0; i < this.numVar; i++){
			int id = this.var[i].getId();
			if(evidPointer < this.lenEvid && id == evid[evidPointer].getId()){
				lEvid.add(this.var[i]);
				evidPointer++;
			}else{
				lNonEvid.add(this.var[i]);
			}
		}
		//delete all evidence in CPT
		for(int i = 0; i < this.lenEvid; i++ ){
			int id = this.evid[i].getId();
			int value = this.evid[i].getValue();
			this.condenseEvid(id, value);
			this.removeEvidNeighbor(id);
		}
		
		//ordering for non-evidence list
		System.out.println("original ordering of nonevid:");
		this.showArrayListVariableOrder(lNonEvid);
		ArrayList<Variable> orderNonEvid = this.orderingMinFill(lNonEvid);
		System.out.println("min-fill heuristic ordering of nonevid:");
		this.showArrayListVariableOrder(orderNonEvid);
		
		//production of CPT for every non-evidence
		this.variableElimination(orderNonEvid);

		System.out.println("sCPT size:"+sCPT.size());
		for(Iterator<CPT> itcpt = sCPT.iterator(); itcpt.hasNext();){
			CPT c = itcpt.next();
			System.out.print("var size:"+c.getVarSeqSize());
			System.out.print(" p:"+c.getProbSize());
			System.out.println(" porb:"+c.getProb(0));
		}
		//if CPT is condensed into a single value, result *= this value
		result = this.probabilityProduction();
		
		return result;
	}
	private double probabilityProduction() {
		double result = 1.0;
		for(Iterator<CPT> it = sCPT.iterator(); it.hasNext();){
			CPT cpt = it.next();
			if(cpt.getProbSize()!=1){
				System.out.println(cpt.getVarSeqSize()+" "+cpt.getProbSize());
				System.out.println("cpt is not condensed to one value");
				System.exit(0);
			}
			result *= cpt.getProb(0);
		}
		return result;
	}
	private void variableElimination(ArrayList<Variable> orderNonEvid) {
		// variable eliminination by CPT production
		// relatedCPT, sCPT, orderNonEvid
		// 1. visit every node in orderNonEvid, find all CPT in its relatedCPT
		// 2. multiple every CPT into one, multiple newCPT and nextCPT each time and save as newCPT(varSeq & probability)
		// 3. sum out its new CPT by condensing the node in orderNonEvid(sum out prob and delete the node in varSeq)
		// 4. remove all CPT in relatedCPT, its copy in sCPT and its copy in its varSeq's relatedCPT
		// 5. add the new CPT to its varSeq's relatedCPT and sCPT
		// in the end sCPT is a list of CPT whose varSeq is empty, that means only one probability.

		System.out.println("eliminate variable:");
		for(Iterator<Variable> it = orderNonEvid.iterator(); it.hasNext();){
			Variable v = it.next();
			System.out.print(v.getId()+" ");
			CPT newCPT = null;
			for(Iterator<CPT> itCPT = relatedCPT.get(v.getId()).iterator();
					itCPT.hasNext();){
				CPT cpt = itCPT.next();
				if(newCPT == null) newCPT = new CPT(cpt);
				else{
					mergeCPT(newCPT, cpt);
				}
			}
//			if(v.getId()==12){
//				int count = 0;
//				System.out.println("relatecpt in 12 size:"+relatedCPT.get(v.getId()).size());
//				for(Iterator<CPT> it2 = sCPT.iterator(); it2.hasNext();){
//					CPT c = it2.next();
//					for(int i = 0; i < c.getVarSeqSize(); i++){
//						if(c.getVarSeq(i).getId()==12)
//							count++;
//					}
//				}
//				System.out.println("count:"+count);
//			}
			sumoutCPT(v, newCPT);
			removeCPT(v);
			addNewCPT(newCPT);
		}
		System.out.println();
	}
	private void addNewCPT(CPT newCPT) {
		for(Iterator<Variable> it = newCPT.getVarSeq().iterator(); it.hasNext();){
			Variable v = it.next();
			relatedCPT.get(v.getId()).add(newCPT);
		}
		sCPT.add(newCPT);
	}
	private void removeCPT(Variable v) {
		for(Iterator<CPT> it = relatedCPT.get(v.getId()).iterator();it.hasNext();){
			CPT cpt = it.next();
			//remove CPT in relatedCPT
			for(int i = 0 ; i < cpt.getVarSeqSize(); i++){
				Variable temV = cpt.getVarSeq(i);
				if(temV == v)continue;
				if(!relatedCPT.get(temV.getId()).remove(cpt)){
					System.out.println("canot remove CPT in node id:"+temV.getId());
					System.exit(0);
				}
			}
			//remove CPT in sCPT;
			it.remove();
			sCPT.remove(cpt);
		}
	}
	private void sumoutCPT(Variable v, CPT newCPT) {
		int index;
		for(index = 0; index < newCPT.getVarSeqSize(); index++){
			if(v==newCPT.getVarSeq(index))break;
		}
		if(index>= newCPT.getVarSeqSize()){
			System.out.println("canot find id:"+v.getId()+" when summing out");
			System.exit(0);
		}
		int oldsize = newCPT.getProbSize();
		// lowStep is the production of all domain of variable following varSeq.get(i).
		int lowStep = 1;
		for(int j = index+1; j < newCPT.getVarSeqSize(); j++){
			lowStep *= newCPT.getVarSeq(j).getDomainSize();
		}
		// upStep is the production of lowStep and varSeq.get(i)'s domainSize
		int upStep = lowStep * v.getDomainSize();
		for(int i = newCPT.getProbSize()-upStep; i >= 0; i-= upStep){
			for(int j = i+upStep-1; j >= i+lowStep; j--){
				newCPT.setProb(j-lowStep,newCPT.getProb(j)+newCPT.getProb(j-lowStep));
				newCPT.removeProb(j);
			}
		}
		newCPT.removeVarSeq(index);
		//verify the size condensation, delete the node from newCPT
		if(newCPT.getProbSize()==oldsize){
			System.out.println("id:"+v.getId()+" didn't sumout. prob size:"+oldsize);
		}
	}
	private void mergeCPT(CPT newCPT, CPT cpt) {
		//multiple newCPT and next CPT in relatedCPT each time to update newCPT(varSeq & probability)
		int[] lowStep = new int[cpt.getVarSeqSize()];
		lowStep[cpt.getVarSeqSize()-1] = 1;
		for(int i = cpt.getVarSeqSize()-2; i>=0;i--){
			lowStep[i] = lowStep[i+1] * cpt.getVarSeq(i).getDomainSize();
		}
		for(int i = newCPT.getProbSize()-1; i >= 0; i--){
			int[] preValue = new int[newCPT.getVarSeqSize()];
			int left = i;
			for(int j = preValue.length-1; j>=0; j--){
				preValue[j] = left % newCPT.getVarSeq(j).getDomainSize();
				left /= newCPT.getVarSeq(j).getDomainSize();
			}
			Double preProb = newCPT.getProb(i);
			newCPT.removeProb(i);
			multipleCPT(newCPT, cpt, lowStep, preValue, 0, 0, i, preProb);
		}
		for(int i = 0; i < cpt.getVarSeqSize();i++){
			if(!newCPT.getVarSeq().contains(cpt.getVarSeq(i))){
				newCPT.insertVarSeq(cpt.getVarSeq(i));
			}
		}
	}
	private int multipleCPT(	CPT newCPT, CPT cpt, 
								int[] lowStep, int[] preValue, 
								int level, int base, 
								int preIndex, Double preProb) {
		if(level == cpt.getVarSeqSize()){
			//insert new element to newCPT
			//return next index that should be inserted to newCPT
			Double newProb = preProb * cpt.getProb(base);
			//System.out.println(""+"preIndex:"+preIndex+" probSIZE:"+newCPT.getProbSize());
			newCPT.addProb(preIndex, newProb);
			return preIndex+1;
		}
		int dupIndex;
		for(dupIndex = 0; dupIndex < newCPT.getVarSeqSize(); dupIndex++){
			if(newCPT.getVarSeq(dupIndex)==cpt.getVarSeq(level))break;
		}
		if(dupIndex != newCPT.getVarSeqSize()){
			//duplicate variable
//			for(int i = base+preValue[dupIndex]*lowStep[level]; 
//					i < base+(preValue[dupIndex]+1)*lowStep[level];
//					i++){
			preIndex = multipleCPT(	newCPT, cpt, 
									lowStep, preValue, 
									level+1, base+preValue[dupIndex]*lowStep[level], 
									preIndex, preProb);
//			}
		}else{
			//new variable
			for(int i = base; 
			i < base + cpt.getVarSeq(level).getDomainSize()*lowStep[level];
			i += lowStep[level]){
				preIndex = multipleCPT(	newCPT, cpt, 
										lowStep, preValue, 
										level+1, i, 
										preIndex, preProb);
			}
		}
		return preIndex;
	}
	private void showArrayListVariableOrder(ArrayList<Variable> lNonEvid) {
		for(Iterator<Variable> it = lNonEvid.iterator();it.hasNext();)
			System.out.print(it.next().getId()+" ");
		System.out.println();
	}
	private ArrayList<Variable> orderingMinFill(ArrayList<Variable> lNonEvid) {
		//if we delete one node, all of its neighbor should be connected to each other
		//we should minimize the new edges to be made by putting the node that fills the least at the first place.
		//1. visit every node
		//2. check whether each two neighbors are connected. if not, count++
		//3. put the least count node at the end of orderNonEvid.
		//4. add edge among the node's neighbors
		//5. delete the node from its neighbor's neighbor list
		if(lNonEvid.isEmpty())return lNonEvid;
		ArrayList<Variable> copylNonEvid = new ArrayList<Variable>(lNonEvid);
		ArrayList<Variable> orderNonEvid = new ArrayList<Variable>();
		ArrayList<HashSet<Variable>> lNeighFill = new ArrayList<HashSet<Variable>>();
		for(int i = 0; i < neighbor.size(); i++){
			lNeighFill.add(new HashSet<Variable>(neighbor.get(i)));
		}
		int looptimes = copylNonEvid.size();
		for(int i = 0; i < looptimes; i++){
			//find the node adds least edges using min fill heuristic
			int minIndex = nodeMinFill(copylNonEvid, lNeighFill);	
			//move the node to orderNonEvid, add edges to lNeighFill, delete the node from copylNonEvid and lNeighFill 
			Variable v = copylNonEvid.get(minIndex);
			orderNonEvid.add(v);
			HashSet<Variable> neighOfMin = lNeighFill.get(v.getId());
			for(Iterator<Variable> it1 = neighOfMin.iterator(); it1.hasNext();){
				Variable v1 = it1.next();
				for(Iterator<Variable> it2 = neighOfMin.iterator(); it2.hasNext();){
					Variable v2 = it2.next();
					if(v1 == v2)continue;
					if(!lNeighFill.get(v1.getId()).contains(v2))
						lNeighFill.get(v1.getId()).add(v2);
				}
			}
			for(Iterator<Variable> it = neighOfMin.iterator(); it.hasNext();){
				Variable vNeigh = it.next();
				if(!lNeighFill.get(vNeigh.getId()).remove(v)){
					System.out.println("remove node from lNeighFill fail");
					System.exit(0);
				}
			}
			copylNonEvid.remove(minIndex);
		}
		return orderNonEvid;
	}
	private int nodeMinFill(ArrayList<Variable> copylNonEvid,
			ArrayList<HashSet<Variable>> lNeighFill) {
		if(copylNonEvid.isEmpty()){
			System.out.println("copylNonEvid is empty");
			System.exit(0);
		}
		int minCount = -1;
		int minIndex = 0;
		for(int i = 0; i < copylNonEvid.size(); i++){
			int count = 0;
			Variable v = copylNonEvid.get(i);
			for(Iterator<Variable> it1 = lNeighFill.get(v.getId()).iterator(); it1.hasNext();){
				Variable v1 = it1.next();
				for(Iterator<Variable> it2 = lNeighFill.get(v.getId()).iterator(); it2.hasNext();){
					Variable v2 = it2.next();
					if(v1 == v2)continue;
					if(!neighbor.get(v1.getId()).contains(v2))
						count++;
				}
			}
//			Variable[] l = new Variable[lNeighFill.get(v.getId()).size()];
//			lNeighFill.get(v.getId()).toArray(l);
//			System.out.println("	"+v.getId()+"'s count is: "+count);
//			for(int x = 0; x < l.length; x++)System.out.print(l[x].getId()+" ");System.out.println();
			if(minCount == -1 || minCount > count){
				minCount = count;
				minIndex = i;
			}
		}
//		System.out.println("id: "+copylNonEvid.get(minIndex).getId() +" minIndex: "+minIndex+" minCount:"+minCount);
		return minIndex;
	}
	private void removeEvidNeighbor(int id) {
		for(Iterator<Variable> it = neighbor.get(id).iterator(); it.hasNext();){
			Variable v = it.next();
			boolean succRemove = neighbor.get(v.getId()).remove(var[id]);
			if(!succRemove){
				System.out.println("id:"+v.getId()+" cannot remove neighbor:"+id+" because it couldn't find it");
				System.exit(0);
			}
		}
	}
	public void condenseEvid(int id, int value) {
		//for every CPT in relatedCPT, keep the probabilities that fit the value
		//		delete 1. the probability whose value doesn't fit in CPT.
		//			   2. the variable in varSeq of Evidence
		for(int i = 0; i < relatedCPT.get(id).size(); i++){
			CPT cpt = relatedCPT.get(id).get(i);
			cpt.condenseEvid(var[id], value);
		}
	}
	public long getNeighCount() {
		long count = 0;
		for(int i = 0; i < neighbor.size();i++){
			count += neighbor.get(i).size();
		}
		return count;
	}
}
