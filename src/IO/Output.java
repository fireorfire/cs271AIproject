package IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {

	public void writeBNrsltFile(String prePath, String bNrsltPostPath, int i,
			int numVar, int lenEvid, long numOfConnection, Double result, long timeinterval) throws IOException {
		File file = new File(prePath+Integer.toString(i)+bNrsltPostPath);
		FileWriter writer=new FileWriter(file);
		writer.write(numVar+"\n");
		writer.write(lenEvid+"\n");
		writer.write(numOfConnection+"\n");
		writer.write(result+"\n");
		writer.write(timeinterval+"\n");
		writer.close();
	}

}
