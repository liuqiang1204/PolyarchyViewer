package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Parse a csv file that has some incorrect columns
 * Good for adding zeros to integers
 * @author Anthony Scata
 * @version 1.0
 *
 */
public class Writer {

	private static OutputStreamWriter out;
	private static File f;
	private static FileReader fr;
	private static BufferedReader br;

	/**
	 * Print the file header information
	 */
	public static void main(String [] args) {

		//Need to surround with try / catch when writing to a file 
		try {
			
			//Create the output stream with the Latin encoding
		    out = new OutputStreamWriter(new FileOutputStream("proj_for_researcher_numbs_2.csv"), "8859_1");
		    
		    //Create the file for reading
		    f = new File("proj_for_researcher_numbs.csv");
		    fr = new FileReader(f);
		    br = new BufferedReader(fr);

		    //Get the first line
		    String eachLine = br.readLine();
		    
		    //Get the second line
		    eachLine = br.readLine();

		    while (eachLine != null) {
		    	
		    	//Split the string on commas
		    	String [] tmp = eachLine.split(",");
		    	
		    	int a = Integer.parseInt(tmp[1]);
		    	
		    	//if the first string doesn't have enough characters
		    	if(tmp[0].length() != 6) {
		    		
		    		//add a zero to the front
		    		tmp[0] = 0 + "" +tmp[0];
		    	}
		      
		    	//write the line to a file
		    	out.write(tmp[0] + ","+ a + "," + tmp[2] + "\n");
		      
			    //flush the file when we have finished
				out.flush();
		    	
		    	//read the next line
		    	eachLine = br.readLine();
		    }
			
			//close the file we were writing
			out.close();

		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}