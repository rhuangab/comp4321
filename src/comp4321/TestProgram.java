package comp4321;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

public class TestProgram {
	private static RecordManager recman;
	private DataStruc pageID;
	private DataStruc pageInfo;
	private DataStruc wordTF;
	private DataStruc wordID;
	private DataStruc invertedWord;
	private DataStruc wordIDToWord;
	private DataStruc childLinks;
	
	public TestProgram(String db) throws IOException
	{
		// open the database
		recman = RecordManagerFactory.createRecordManager(db);
		
		pageID = new DataStruc(recman,"pageID");  // url -> page_id
		pageInfo = new DataStruc(recman,"pageInfo"); //page_id -> pageInfoStruct
		wordID = new DataStruc(recman,"wordID"); // word -> word_id
		wordTF = new DataStruc(recman,"wordTF"); //word_id -> list(page_id, wordTF)
		invertedWord = new DataStruc(recman, "invertedWord");  //page_id -> list(word_id)
		wordIDToWord = new DataStruc(recman, "word"); //word_id -> word
		childLinks = new DataStruc(recman,"childLink"); //page_id -> list(child_page_id)
	}
	
	public void finalize() throws IOException
	{
		// close the database
		recman.commit();
		recman.close();
	}
	
	public void print() throws IOException
	{
		PrintWriter pw = new PrintWriter("spider_result.txt");
		FastIterator keys = pageID.getIterator();
		String url = null;
		while((url = (String) keys.next()) != null) 
		{
			//get the page_id given an url.
			String pageid = (String) pageID.getEntry(url);
			PageInfoStruct pis = (PageInfoStruct) pageInfo.getEntry(pageid);
			//get word_id list given page_id
			Vector<String> wordIDLists = (Vector<String>) invertedWord.getEntry(pageid);
			pw.println(pis.getTitle());
			pw.println(url);
			pw.println("Last Modification: "+ pis.getLastModification() + ", Size:"+ pis.getPageSize());
			String wordListsToPrint = "";
			// get word given word_id, and word term frequency given word_id and page_id
			for(String word_id : wordIDLists)
			{
				String word = (String) wordIDToWord.getEntry(word_id);
				Vector<Posting> postingList = (Vector<Posting>) wordTF.getEntry(word_id);
				for(Posting p : postingList)
				{
					if(p.page_id.equals(pageid))
					{
						wordListsToPrint += ", "+word+" "+p.freq;
						break;
					}
				}
			}
			if(wordListsToPrint.length() >= 2)
				wordListsToPrint = wordListsToPrint.substring(2); //cut the ', ' at the first place.
			pw.println(wordListsToPrint);
			
			//retrieve list of child_ids given page_id, and use the child_id(page_id) to retrieve url.
			Vector<String> childIDs = (Vector<String>) childLinks.getEntry(pageid);
			for(String childID : childIDs)
			{
				PageInfoStruct pis2 = (PageInfoStruct) pageInfo.getEntry(childID);
				pw.println(pis2.getURL());
			}
			
			pw.println("------------------------------------------------------------");
		}
		pw.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestProgram tp = new TestProgram("comp4321");
		tp.print();
		tp.finalize();

	}

}
