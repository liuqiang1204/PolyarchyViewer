package utilities;

import java.util.StringTokenizer;

/**
 * Class used to replace long words with their shorter abbreviation
 * The class is static so you can call it without the need for objects
 * @author Anthony Scata
 * @version 2.1
 *
 */
public class Replacer {

	/*VARIABLES*/
	
	/**
	 * The list of words and their abbreviation
	 */
	private static final String[][] words = {
			{"Mathematical", "MATH"},
			{"Mathematics", "Math"},
			{"Department", "Dept."},
			{"Science", "sci"},
			{"Sciences", "SCI"},
			{"Statistics", "STAT"},
			{"Physics", "phys"},
			{"Physical", "PHYS"},
			{"Chemical", "CHEM"},
			{"Environmental", "ENVIRO"},
			{"Information", "Info"},
			{"Technology", "Tech"},
			{"Faculty", "FAC"},
			{"Education", "EDU"},
			{"Centre", "Ctr"},
			{"Center", "Ctr"},
			{"Engineering", "Eng"},
	};
	
	/**
	 * The delimiters that determine what separates words in a string
	 */
	private static String delimiters = 	"+-*/(),. ";
	
	
	/*METHODS*/

	/**
	 * This method replaces all of the words in the string with their abbreviated form
	 * The words are checked against an array and substituted with their abbreviated form
	 * Checks the whole string
	 * @param original - The string to be checked
	 * @return - The new string that has been abbreviated
	 */
	public static String replaceAllWords(String original) {
		
		//Create a string that is as large as the original
	    StringBuilder result = new StringBuilder(original.length());
	    
	    //The tokenizer that allows the string to be split
	    StringTokenizer st = new StringTokenizer(original, delimiters, true);
	    
	    //Loop through the string, delimited word at a time
	    while (st.hasMoreTokens()) {
	    	
	    	//Get the word
	        String word = st.nextToken();
	        boolean found = false;
	        
	        //Loop through the array of abbreviations until we find the string
			for(int i = 0; i < words.length && !found; i++) {
				
				//if it is found then append it to the string
				if(word.equals(words[i][0])) {
					result.append(words[i][1]);
					found = true;
				}
			}
			
			//If we did not find anything then just append the original word
			if(!found) {
				result.append(word);
			}
	    }
	    
	    //return the new string that has been made
	    return result.toString();
	}
}