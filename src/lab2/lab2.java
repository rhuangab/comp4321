package lab2;
import java.util.Arrays;
import java.util.Vector;

import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.StringTokenizer;

import org.htmlparser.beans.LinkBean;

import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import org.htmlparser.beans.HTMLLinkBean;

import java.util.regex.Pattern; 
import java.util.regex.Matcher; 

public class lab2 {
	private String url;
	lab2(String _url)
	{
		url = _url;
	}
	public Vector<String> extractWords() throws ParserException
//	public void extractWords() throws ParserException
	{
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		StringBean sb;
		Vector<String> v_str = new Vector<String>();
        boolean links = false;
        sb = new StringBean ();
        sb.setLinks (links);
        sb.setURL (url);
        String temp = sb.getStrings();
        StringTokenizer st = new StringTokenizer(temp, "\n");
        while(st.hasMoreTokens()){
        	v_str.add(st.nextToken());
        }       
        return (v_str);
	}
	public Vector<String> extractLinks() throws ParserException

	{
		// extract links in url and return them
		// ADD YOUR CODES HERE

        Vector<String> v_link = new Vector<String>();
        LinkBean lb = new LinkBean();
        lb.setURL(url);
        URL[] URL_array = lb.getLinks();
        for(int i=0; i<URL_array.length; i++){
        	v_link.addElement(URL_array[i].toString());
        }
		return v_link;
	}
	
	public static void main (String[] args)
	{
		String input = "abcde";
		System.out.println("[a-z] " + Arrays.toString(input.split("[a-z]")));
		System.out.println("\\w " + Arrays.toString(input.split("\\w")));
		System.out.println("\\w*? " + Arrays.toString(input.split("\\w*?")));
		System.out.println("\\w+ " + Arrays.toString(input.split("\\w+")));


		try
		{
			lab2 crawler = new lab2("http://www.cs.ust.hk/");


			Vector<String> words = crawler.extractWords();			
			System.out.println("Words in "+crawler.url+":");
			for(int i = 0; i < words.size(); i++)
				System.out.print(words.get(i)+" ");
			System.out.println("\n\n");
			

	
			Vector<String> links = crawler.extractLinks();
			System.out.println("Links in "+crawler.url+":");
			for(int i = 0; i < links.size(); i++)		
				System.out.println(links.get(i));
			System.out.println("");
			
		}
		catch (ParserException e)
            	{
                	e.printStackTrace ();
            	}

	}
}