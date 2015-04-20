package entity;

public class Evidence {
	private int id;
	private int value;
	
	public Evidence(int id, int value){
		this.id = id;
		this.value = value;
	}
	public int getId(){
		return this.id;
	}
	public int getValue(){
		return this.value;
	}
	
}
