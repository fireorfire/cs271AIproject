package Demo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import algorithm.Ve;

import IO.Input;
import IO.Output;

import entity.*;

public class Demo {
	private Input in;
	private Ve ve;
	private Output out;
	
	public Demo(){
		this.in = new Input();
		this.ve = new Ve();
		this.out = new Output();
	}
	public static void main(String[] args){
		
		Demo demo = new Demo();
		String prePath = "../data/BN_";
		String BNfilePostPath = ".erg";
		String BNevidPostPath = ".erg.evid";
		String BNrsltPostPath = ".erg.rslt";
		int []BN_num = {0, 1, 3, 4, 5, 6, 7, 10, 11};//index:2 7 8 9 12 13 14 15 16 17 18 19 20 21
		//read BN file and BN evidence file
		try {
			for(int i = 0; i < BN_num.length; i++){
				System.out.println("BN file number:"+BN_num[i]);
				demo.callReadBNfile(prePath, BNfilePostPath, BN_num[i]);
				demo.callReadBNevidFile(prePath, BNevidPostPath, BN_num[i]);
				//call algorithm variable elimination
				demo.createVe();
				long numOfConnection = demo.getNeighCount()/2;
				System.out.println("number of connection: "+numOfConnection);
				long starttime = System.currentTimeMillis();
				Double result = demo.callVe();
				long endtime = System.currentTimeMillis();
				long timeinterval = (endtime-starttime);
				System.out.println("computing time cost: "+timeinterval+"ms");
				System.out.println("the result of probability: "+result);
				
				demo.callWriteBNrsltFile(prePath, BNrsltPostPath, BN_num[i], 
						demo.getInNumVar(), demo.getInLenEvid(), numOfConnection, result, timeinterval);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private long getNeighCount() {
		return ve.getNeighCount();
	}
	private int getInLenEvid() {
		return in.getLenEvid();
	}
	private int getInNumVar() {
		return in.getNumVar();
	}
	private void callWriteBNrsltFile(String prePath, String bNrsltPostPath,
			int i, int numVar, int lenEvid, long numOfConnection, Double result, long timeinterval) throws IOException {
		out.writeBNrsltFile(prePath, bNrsltPostPath, i, numVar, lenEvid, numOfConnection, result, timeinterval);
	}
	private void createVe() {
		this.ve = new Ve(this.in);
	}
	private double callVe() {
		return this.ve.calculate();
	}
	private void callReadBNevidFile(String prePath, String bNevidPostPath, int bN_num) throws IOException {
		this.in.readBNevidFile(prePath, bNevidPostPath, bN_num);
	}
	private void callReadBNfile(String prePath, String bNfilePostPath, int bN_num) throws IOException {
		this.in.readBNfile(prePath, bNfilePostPath, bN_num);
	}

}
