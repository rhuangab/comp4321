package comp4321;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import IRUtilities.Porter;

public class StopStem {

	Porter porter;
	static Vector<String> stopWords = new Vector<String>();
	public StopStem(){
		porter = new Porter();
		readStopWords(stopWords);
	}
	
	private void readStopWords(Vector<String> stopWords) {
		File f = new File("stopwords.txt");
    	if(!f.exists())
    	{
    		System.out.println("Stopwords.txt missing.");
    		return;
    	}
    	
    	try {
			Scanner in = new Scanner(f);
			
			while(in.hasNext())
			{
				String word = in.next();
				stopWords.add(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * get stem given a string
	 * @param s
	 * @return
	 */
	String getStem(String s)
	{
		return porter.stripAffixes(s);
	}
	
	private boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	private boolean isStopword(String s)
	{
		return stopWords.contains(s);
	}
	
	String processing(String s)
	{
		if(isNumeric(s) || isStopword(s))
			return null;
		else
			return getStem(s);

	}
}
