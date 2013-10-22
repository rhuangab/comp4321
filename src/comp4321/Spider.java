package comp4321;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

public class Spider {
	
	/**
	 * the basic information
	 */
	private static RecordManager recman;
	private static Indexer indexer;
	private DataStruc pageID;
	private int pageCount;
	private static PageRank pageRank;
	
	/**
	 * constructor
	 * @param recordmanager
	 * @throws IOException
	 */
	public Spider(String recordmanager) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		
		indexer = new Indexer(recman);
		pageID = new DataStruc(recman,"pageID");
		pageCount = pageID.getSize();
		pageRank = new PageRank(recman);
	}
	
	public void finalize() throws IOException
	{
		// save and close database
		recman.commit();
		recman.close();				
	}
	
	/**
	 * insert a new page
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String insertPage(String url) throws IOException
	{
		if(pageID.getEntry(url) != null)
			return (String) pageID.getEntry(url);
		String id = String.format("%04d", pageCount++);
		pageID.addEntry(url, id);
		return id;
	}
	
	/**
	 * get page Id given a url
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getPageID(String url) throws IOException
	{
		if(pageID.getEntry(url) == null)
			return null;
		
		return  (String) pageID.getEntry(url);
	}
	
	/**
	 * @Param url, maxPage
	 * @return
	 * Start from url, traverse all appeared urls until no pages left or the number of visited pages reach maxPage
	 */
	public void indexing(String url, int maxPage) throws IOException, ParserException
	{
		// Queue of to be processed and processed urls.
		Queue<String> cands = new LinkedList<String>();
		Queue<String> processed = new LinkedList<String>();
		
		cands.add(url);
		
		while(cands.size() != 0 && processed.size() < maxPage)
		{
			String indexURL = cands.remove();
			
			// if the file is already processed, continue
			if(processed.contains(indexURL))
				continue;
			
			
			String page_id = insertPage(indexURL);
			
			//System.out.println("indexing "+ page_id);
			//System.out.println("before"+cands.size());
			indexer.indexNewPage(page_id, indexURL);
			
			// For all children links, add to the candidates, insertPage
			Vector<String> links_dup = indexer.extractLinks(indexURL);
			Vector<String> ids = new Vector<String>();
			
			Vector<String> links = new Vector<String>(new LinkedHashSet<String>(links_dup));
			
			String oneID;
			for(String oneLink:links) 
			{
				// if either pageCount not reach max, or this page has already been discovered
				if(pageCount < maxPage || getPageID(oneLink)!=null) 	
				{
					oneID = insertPage(oneLink);		// get id if already exists, or insert and get id.
					ids.add(oneID);						// add the id of the child to ids vector
					cands.add(oneLink);					// add the child to the candidates
				}
			}
			
			pageRank.addChildLink(page_id, ids);
			//pageRank.addParentLink(page_id, ids);
			
			//System.out.println("after"+cands.size());
			processed.add(url);
		
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
		/*
		DataStruc wordID = new DataStruc(recman,"wordID");
		DataStruc wordTF = new DataStruc(recman,"wordTF");
		DataStruc invertedWord = new DataStruc(recman, "invertedWord");
		DataStruc word = new DataStruc(recman,"word");
		DataStruc pageInfo = new DataStruc(recman, "pageInfo");
		DataStruc childLink = new DataStruc(recman,"childLink");
		*/
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
		/*
		HTree hashtable = childLink.getHash();			
		FastIterator iter = hashtable.keys();
		String keyword = null;
		while( (keyword=(String)iter.next()) != null )
		{
			Vector<String> temp = (Vector<String>) hashtable.get(keyword);
			
			System.out.println("\n" + ((PageInfoStruct)pageInfo.getEntry(keyword)).getURL() + " " + temp.size() + ":");
			for(int i=0;i<temp.size();i++)
			{
				PageInfoStruct pis = (PageInfoStruct) pageInfo.getEntry(temp.elementAt(i));
				System.out.println("Child" + (i+1) +": "+pis.getURL());
			}
				
		}*/
		
		spider.finalize();
		
		long t2 = System.currentTimeMillis();
		
		System.out.println("Total Time : " + (t2 - t1)/1000.0 + " Seconds");
				
	}
	
}
