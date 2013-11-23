package comp4321;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;


public class Query {

	HTree wordIDHash;
	HTree bodyWordHash;
	HTree invertedBodyWordHash;
	HTree pageIDHash;
	TermWeight termWeight;
	PageRank PR;
	
	private RecordManager recman;
	
	public Query() throws IOException
	{		
		recman = RecordManagerFactory.createRecordManager("C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\webapps\\COMP4321Beta1\\MyDatabase");
		//recman = RecordManagerFactory.createRecordManager("MyDatabase");
		DataStruc wordID = new DataStruc(recman,"wordID");
		DataStruc bodyWord = new DataStruc(recman,"bodyWord");
		DataStruc invertedBodyWord = new DataStruc(recman, "invertedBodyWord");
		DataStruc pageID =new DataStruc(recman,"pageID");;
		PR = new PageRank(recman);
		wordIDHash = wordID.getHash();
		bodyWordHash = bodyWord.getHash();
		pageIDHash = pageID.getHash();
		termWeight = new TermWeight(recman);
	}
	
	public Vector<Score> getScore(String query) throws IOException
	{
		HashMap<String, Double> vsScores = new HashMap<String, Double>();
		
		String[] word = query.split(" ");
		for(int i = 0; i < word.length; i++)
		{
			word[i] = StopStem.processing(word[i]);
			//get wordid
			String word_id = (String) wordIDHash.get(word[i]);
			
			Vector<Posting> list = (Vector<Posting>) bodyWordHash.get(word_id);
			for(Posting p:list)
			{
				String page_id = p.page_id;
				double partialScore = termWeight.getWeight(p.page_id, word_id, true);
				if(vsScores.get(page_id) != null)
				{
					partialScore = vsScores.get(page_id) + partialScore;
				}
				vsScores.put(page_id, partialScore);
			}
		}
		
		Vector<Score> result = new Vector<Score>();
		Iterator it = vsScores.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Double> pairs = (Map.Entry<String, Double>)it.next();
	        result.add(new Score(pairs.getKey(), pairs.getValue(), PR.getPageRank(pairs.getKey())));
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    Collections.sort(result);
	    
	    return result;
	}
	/*
	public static void main(String[] arg) throws IOException, ParserException
	{

		Query r = new Query();
		Vector<Score> result = r.getScore("award");
		for(Score i: result)
		{
			System.out.println( i.page_id + " " + i.vsScore + " " + i.pageRank +  " " + i.overall);
		}
		
		System.out.print(String.format("%.2f", 1.24342));
	}*/
}
