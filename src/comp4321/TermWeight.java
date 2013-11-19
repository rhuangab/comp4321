package comp4321;

import java.io.IOException;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.htree.HTree;

public class TermWeight {
	DataStruc bodyWord;
	DataStruc titleWord;
	DataStruc pageInfo;
	
	private RecordManager recman;
	
	public TermWeight(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		bodyWord = new DataStruc(recman,"bodyWord");
		titleWord = new DataStruc(recman,"titleWord");
		pageInfo = new DataStruc(recman, "pageInfo");
	}
	
	double getWeight(String pageID, String word, boolean isBody) throws IOException
	{	
		HTree hashtable;
		if(isBody)
			hashtable = bodyWord.getHash();
		else
			hashtable = titleWord.getHash();
		
		Vector<Posting> postingList = (Vector<Posting>) hashtable.get(word);
		int tf = 0;
		for(Posting p:postingList)
		{
			if(p.page_id.equals(pageID))
			{
				
				tf = p.freq;
				break;
			}
		}
		System.out.print(tf+"\n");
		int df = postingList.size();
		
		double idf = Math.log(pageInfo.getSize() * 1.0 / df) / Math.log(2);
		
		return tf * idf; 
	}
	
}
