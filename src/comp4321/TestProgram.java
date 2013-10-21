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
	private FileStruc pageID;
	private FileStruc pageInfo;
	private FileStruc wordTF;
	private FileStruc wordID;
	private FileStruc invertedWord;
	//private FileStruc childLinks;
	
	public TestProgram(String db) throws IOException
	{
		// open the database
		recman = RecordManagerFactory.createRecordManager(db);
		
		pageID = new FileStruc(recman,"pageID");
		pageInfo = new FileStruc(recman,"pageInfo");
		wordTF = new FileStruc(recman,"wordTF");
		wordID = new FileStruc(recman,"wordID");
		invertedWord = new FileStruc(recman, "invertedWord");
		//childLinks = new FileStruc(recman,"childLinks");
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
		HTree hashTable = pageID.getHash();
		FastIterator keys = hashTable.keys();
		String url = null;
		while((url = (String) keys.next()) != null) 
		{
			String pageid = (String) hashTable.get(url);
			PageInfoStruct pis = (PageInfoStruct) pageInfo.getEntry(pageid);
			Vector<String> wordLists = (Vector<String>) wordID.getEntry(pageid);
			pw.println(pis.getTitle());
			pw.println(url);
			pw.println("Last Modification: "+ pis.getLastModification() + ", Size:"+ pis.getPageSize());
			String wordListsToPrint = "";
			System.out.println(wordID.getSize());
			for(String word : wordLists)
			{
				Vector<Posting> postingList = (Vector<Posting>) wordTF.getEntry(word);
				for(Posting p : postingList)
				{
					if(p.page_id.equals(pageid))
					{
						wordListsToPrint += ", "+word+" "+p.freq;
						break;
					}
				}
			}
			wordListsToPrint = wordListsToPrint.substring(1); //cut the ',' at the first place.
			pw.println(wordListsToPrint);
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
