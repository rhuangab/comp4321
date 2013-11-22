package result;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

import comp4321.InvertPosting;
import comp4321.SimilarPage;
import comp4321.Word;
import comp4321.DataStruc;
import comp4321.PageInfoStruct;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;


class Posting implements Serializable {
	public String page_id;
	public int freq;
	public Vector<Integer> position;

	Posting(String doc, int freq, Vector<Integer> position) {
		this.page_id = doc;
		this.freq = freq;
		this.position = position;
	}
}

public class Result {
	
	/**
	 * @param args
	 */
	private RecordManager recman;
	private Vector<String> query;
	private DataStruc pageIDTable;
	private DataStruc wordIDTable;
	private DataStruc bodyWordTable;
	private DataStruc titleWordTable;
	private DataStruc invertedBodyWordTable;
	private DataStruc wordTable;
	private DataStruc pageInfoTable;
	private DataStruc childLinksTable;
	private DataStruc parentLinksTable;
	
	public Result(String rawQuery) throws IOException
	{
		/*ParseQuery(rawQuery);
		if(query!= null && query.size() > 0 )
		{
			int pageCount=0;
			String pageID = String.format("%04d", pageCount++);
			initialize();
		}*/
		initialize();
	}
	
	public void initialize() throws IOException
	{
		recman = RecordManagerFactory.createRecordManager("/Library/Tomcat/apache-tomcat-6.0.37/webapps/comp4321/MyDatabase");
		pageIDTable = new DataStruc(recman,"pageID");
		wordIDTable = new DataStruc(recman,"wordID");
		bodyWordTable = new DataStruc(recman,"bodyWord");
		titleWordTable = new DataStruc(recman,"titleWord");
		invertedBodyWordTable = new DataStruc(recman, "invertedBodyWord");
		wordTable = new DataStruc(recman,"word");
		pageInfoTable = new DataStruc(recman, "pageInfo");
		childLinksTable = new DataStruc(recman,"childLink");
		parentLinksTable = new DataStruc(recman, "parentLink");
	}
	
	public String getUrl(String pageID) throws IOException
	{
		FastIterator urls = pageIDTable.getIterator();
		String url = null;
		while((url = (String) urls.next()) != null) 
		{
			//get the page_id given an url.
			String pageid = (String) pageIDTable.getEntry(url);
			if(pageid.equals(pageID))
				return url;
		}
		return null;
	}
	
	public String getTitle(String pageID) throws IOException
	{
		return ((PageInfoStruct)pageInfoTable.getEntry(pageID)).getTitle();
	}
	
	public String getLastModification(String pageID) throws IOException
	{
		long ldate = ((PageInfoStruct)pageInfoTable.getEntry(pageID)).getLastModificationLong();
		Date date = new Date(ldate);
		return date.toString();
	}
	
	public long getPageSize(String pageID) throws IOException
	{
		return ((PageInfoStruct)pageInfoTable.getEntry(pageID)).getPageSize();
	}
	
	public Vector<WordAndFrequencyStruct> getTopWordList(String pageID) throws IOException
	{
		SimilarPage similar = new SimilarPage(recman);
		LinkedList<InvertPosting> wordIdAndFrequencyList = similar.getTopWords(pageID);
		Vector<WordAndFrequencyStruct> wordAndFrequencyList = new Vector<WordAndFrequencyStruct>();
		for(InvertPosting wordIdAndFrequency : wordIdAndFrequencyList)
		{
			//Find Word Name
			String word = (String)wordTable.getEntry(wordIdAndFrequency.word_id);
			wordAndFrequencyList.add(new WordAndFrequencyStruct(word, wordIdAndFrequency.freq));
		}
		return wordAndFrequencyList;
	}
	
	public Vector<String> getChildLinks(String pageID) throws IOException
	{
		Vector<String> childIDs = (Vector<String>) childLinksTable.getEntry(pageID);
		Vector<String> childUrls = new Vector<String>();
		for(String childID : childIDs)
		{
			childUrls.add(((PageInfoStruct) pageInfoTable.getEntry(childID)).getURL());
		}
		return childUrls;
	}
	
	public Vector<String> getParentLinks(String pageID) throws IOException
	{
		Vector<String> parentIDs = (Vector<String>) parentLinksTable.getEntry(pageID);
		Vector<String> parentUrls = new Vector<String>();
		for(String parentID : parentIDs)
		{
			parentUrls.add(((PageInfoStruct) pageInfoTable.getEntry(parentID)).getURL());
		}
		return parentUrls;
	}
	
	public Vector<String> ParseQuery(String rawQuery)
	{
		return null;
	}
	
	public String getTest()
	{
		return "I am so happy";
	}
	

}
