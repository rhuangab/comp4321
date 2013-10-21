package comp4321;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

public class Spider {
	
	private static RecordManager recman;
	private static Indexer indexer;
	private FileStruc pageID;
	private int pageCount;
	
	public Spider(String recordmanager) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		
		indexer = new Indexer(recman);
		pageID = new FileStruc(recman,"pageID");
		pageCount = pageID.getSize();
	}
	
	public void finalize() throws IOException
	{
		// save and close database
		recman.commit();
		recman.close();				
	}
	
	public void insertPage(String url) throws IOException
	{
		if(pageID.getEntry(url) != null)
			return;
		String id = String.format("%04d", pageCount++);
		pageID.addEntry(url, id);
	}
	
	public String getPageID(String url) throws IOException
	{
		if(pageID.getEntry(url) == null)
			return null;
		
		return  (String) pageID.getEntry(url);
	}
	
	public void indexing(String url, int maxPage) throws IOException, ParserException
	{
		Queue<String> cands = new LinkedList<String>();
		
		cands.add(url);
		
		while(cands.size() != 0 && pageCount < maxPage)
		{
			String indexURL = cands.remove();
			
			if(getPageID(indexURL) != null)
				continue;
			
			insertPage(indexURL);
			String page_id = getPageID(indexURL);
			
			indexer.indexNewPage(page_id, indexURL);
			
			Vector<String> links = indexer.extractLinks(indexURL);
			
			for(int i = 0; i < links.size(); i++)
				cands.add(links.get(i));
		}
	}
	
	public static void main(String[] arg) throws IOException, ParserException
	{
		String db = "comp4321";
		String startUrl = "http://www.cse.ust.hk/";
		
		final int maxPage = 30;
		
		long t1 = System.currentTimeMillis();
		

		Spider spider = new Spider(db);

		spider.indexing(startUrl, maxPage);
		
		/** You can uncomment the following codes to see the outputs **/
		
		FileStruc wordID = new FileStruc(recman,"wordID");
		FileStruc wordTF = new FileStruc(recman,"wordTF");
		FileStruc invertedWord = new FileStruc(recman, "invertedWord");
		FileStruc word = new FileStruc(recman,"word");
		FileStruc pageInfo = new FileStruc(recman, "pageInfo");
		
		/**print word_id -> word**/
		/*
		HTree hashtable = word.getHash();
		FastIterator iter = hashtable.keys();
		String keyword = null;
		while( (keyword=(String)iter.next()) != null)
		{
			String temp = (String) hashtable.get(keyword);
			System.out.println(keyword + " : " + temp);
		}*/
		
		/**print word -> word_id**/
		/*
		HTree hashtable = wordID.getHash();
		FastIterator iter = hashtable.keys();
		String keyword = null;
		while( (keyword=(String)iter.next()) != null)
		{
			String temp = (String) hashtable.get(keyword);
			System.out.println(keyword + " : " + temp);
		}*/
		
		/**print word_id -> list(page_id, term_freq)**/
		/*
		HTree hashtable = wordTF.getHash();
		System.out.print(wordTF.getSize());
		FastIterator iter = hashtable.keys();
		String keyword = null;
		while( (keyword=(String)iter.next()) != null)
		{
			Vector<Posting> temp = (Vector<Posting>) hashtable.get(keyword);
			System.out.print(keyword + ":");
			for(Posting i: temp)
				System.out.print("(" + i.page_id + ", " + i.freq + ") " );
			
			System.out.println();
		}*/
		
		/**print page_id->list(word_id)**/
		/*
		HTree hashtable = invertedWord.getHash();
		FastIterator iter = hashtable.keys();
		String keyword = null;
		while( (keyword=(String)iter.next()) != null)
		{
			Vector<String> temp = (Vector<String>) hashtable.get(keyword);
			System.out.print(keyword + " " + temp.size() + ":");
			for(String i: temp)
				System.out.print("(" + i + ") " );
			
			System.out.println();
		}*/
		
		/** print child_link **/
		
		spider.finalize();
		
		long t2 = System.currentTimeMillis();
		
		System.out.println("Total Time : " + (t2 - t1)/1000.0 + " Seconds");
				
	}
	
}
