package comp4321;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import IRUtilities.Porter;

public class StopStem {

	static Porter porter;
	static Vector<String> stopWords = new Vector<String>();
	
	static
	{
		porter = new Porter();
		readStopWords(stopWords);
	}
	
	
	private static void readStopWords(Vector<String> stopWords) {
		File f = new File("C:\\Users\\XIE Min\\workspace\\comp4321\\stopwords.txt");
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
	static String getStem(String s)
	{
		return porter.stripAffixes(s);
	}
	
	private static boolean isNumeric(String str)  
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
	
	private static boolean isStopword(String s)
	{
		return stopWords.contains(s);
	}
	
	static String processing(String s)
	{
		if(isNumeric(s) || isStopword(s))
			return null;
		else
			return getStem(s);

	}
}
