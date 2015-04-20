package IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import entity.Evidence;
import entity.Variable;

public class Input {
	private int numVar;
	private Variable[] var;
	private int lenEvid;
	private Evidence[] evid;
	
	public void readBNfile(String prePath, String bNfilePostPath, int bN_num) throws IOException{
		File file 		= new File(prePath+Integer.toString(bN_num)+bNfilePostPath);//D:\\firework\\project\\AI2014\\Pe\\data\\BN_7.erg");
		FileReader fr 	= new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String s 		= null;
		//read the total number of variable
		s = br.readLine();
		int numVar = Integer.parseInt(s);
		System.out.println("number of Variable:"+numVar);
		this.setNumVar(numVar);
		
		//read the domain size for each variable and create new object
		s = br.readLine();
		String[] domainSizeStr = s.split(" ");
		for(int i = 0; i < domainSizeStr.length; i++){
			int domainSize = Integer.parseInt(domainSizeStr[i]);
			var[i] = new Variable(i, domainSize);
		}
		
		//insert dependency
		for(int i = 0; i < numVar; i++){
			s 				= br.readLine();
			String[] splitS = s.split(" ");
			int lenDepen 	= Integer.parseInt(splitS[0]);
			var[i].setLenDepen(lenDepen);
			int countDepen 	= 0;
			for(int j = 1; j < splitS.length; j++){
				if(splitS[j].equals(""))continue;
				var[i].setDependency(countDepen, Integer.parseInt(splitS[j]));
				//System.out.print(var[i].getDependency(countDepen)+"-");
				countDepen++;
			}
		}
		
		//insert probability
		while(true){
			s = br.readLine();
			//System.out.println(s);
			if(Pattern.compile("\\/\\* Probabilities \\*\\/").matcher(s).find())
				break;
		}
		for(int i = 0; i < numVar; i++){
			while(true){
				s = br.readLine();
				if(!s.equals(""))break;
			}
			int lenProb = Integer.parseInt(s);
			var[i].setLenProb(lenProb);
			int numLine 	= lenProb/var[i].getDomainSize();
			int countProb 	= 0;
			//System.out.println(i+":");
			for(int j = 0; j < numLine; j++){
				s = br.readLine();
				String[] splitS = s.split(" ");
				for(int k = 0; k < splitS.length; k++){
					if(splitS[k].equals(""))continue;
					var[i].setProbability(countProb, Double.valueOf(splitS[k]).doubleValue());
					//System.out.print(var[i].getProbability(countProb)+" ");
					countProb++;
				}
			}
		}
		
		//insert Names
		while(true){
			s = br.readLine();
			//System.out.println(s);
			if(Pattern.compile("\\/\\* Names   \\*\\/").matcher(s).find())
				break;
		}
		for(int i = 0; i < numVar; i++){
			s = br.readLine();
			var[i].setName(s);
			//System.out.println(i+"'s name:"+var[i].getName());
		}
		
		//insert Labels
		while(true){
			s = br.readLine();
			//System.out.println(s);
			if(Pattern.compile("\\/\\* Labels  \\*\\/").matcher(s).find())
				break;
		}
		for(int i = 0; i < numVar; i++){
			//System.out.println(i+":");
			s = br.readLine();
			String[] splitS = s.split(" ");
			int domainSize 	= var[i].getDomainSize();
			for(int j = 0; j < domainSize; j++){
				var[i].setLabel(j, splitS[j]);
				//System.out.println(var[i].getLabel(j)+" ");
			}
		}
		
		while(true){
			s = br.readLine();
			if(s==null)break;
			//System.out.println(s);
		}
	}
	public void setNumVar(int numVar) {
		this.numVar = numVar;
		this.var 	= new Variable[numVar];
	}

	public void readBNevidFile(String prePath, String bNevidPostPath, int bN_num) throws IOException {
		// TODO Auto-generated method stub
		File file 		= new File(prePath+Integer.toString(bN_num)+bNevidPostPath);
		FileReader fr 	= new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String s 		= null;
		
		//insert Evidence
		while(true){
			s = br.readLine();
			//System.out.println(s);
			if(Pattern.compile("\\/\\* Evidence \\*\\/").matcher(s).find())
				break;
		}
		s = br.readLine();
		this.setLenEvid(Integer.parseInt(s));
		for(int i = 0; i < this.lenEvid; i++){
			s 				= br.readLine();
			String[] splitS = s.split(" ");
			int count 	= 0;
			int id 		= -1;
			int value 	= -1;
			for(int j = 0; j < splitS.length; j++){
				if(splitS[j].equals(""))continue;
				if(count == 0)id = Integer.parseInt(splitS[j]);
				else if(count == 1)value = Integer.parseInt(splitS[j]);
				count++;
			}
			evid[i] = new Evidence(id, value);
			//System.out.println(i+":"+evid[i].getId()+"-"+evid[i].getValue());
		}
	}
	private void setLenEvid(int lenEvid) {
		this.lenEvid 	= lenEvid;
		this.evid 		= new Evidence[lenEvid];
	}
	public int getNumVar() {
		return this.numVar;
	}
	public int getLenEvid(){
		return this.lenEvid;
	}
	public Variable[] getVar(){
		return this.var;
	}
	public Evidence[] getEvid(){
		return this.evid;
	}
}
