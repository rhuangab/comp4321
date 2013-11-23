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
	HTree titleWordHash;
	HTree invertedBodyWordHash;
	HTree invertedTitleWordHash;
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
		DataStruc titleWord = new DataStruc(recman,"bodyWord");
		DataStruc pageID =new DataStruc(recman,"pageID");
		DataStruc invertedBodyWord = new DataStruc(recman, "invertedBodyWord");
		DataStruc invertedTitleWord = new DataStruc(recman, "invertedTitleWord");
		PR = new PageRank(recman);
		wordIDHash = wordID.getHash();
		bodyWordHash = bodyWord.getHash();
		titleWordHash = titleWord.getHash();
		pageIDHash = pageID.getHash();
		invertedBodyWordHash = invertedBodyWord.getHash();
		invertedTitleWordHash = invertedTitleWord.getHash();
		termWeight = new TermWeight(recman);
	}
	
	public Vector<Score> getScore(String query) throws IOException
	{
		HashMap<String, partialScore> vsScores = new HashMap<String, partialScore>();
		
		String[] word = query.split(" ");
		for(int i = 0; i < word.length; i++)
		{
			word[i] = StopStem.processing(word[i]);
			
			if(word[i] == null || word[i] == "")
				continue;
			
			//get wordid
			String word_id = (String) wordIDHash.get(word[i]);
			
			if(word_id == null || word_id == "")
				continue;
			
			
			Vector<Posting> bodylist = (Vector<Posting>) bodyWordHash.get(word_id);
			Vector<Posting> titlelist = (Vector<Posting>) titleWordHash.get(word_id);
			
			if(bodylist != null)
			{
				for(Posting p:bodylist)
				{
					String page_id = p.page_id;
					double partialScore = termWeight.getWeight(p.page_id, word_id, true);
					if(vsScores.get(page_id) != null)
					{
						partialScore = vsScores.get(page_id).body + partialScore;
						double titleScore = vsScores.get(page_id).title;
						vsScores.put(page_id, new partialScore(partialScore,titleScore));
					}
					else
						vsScores.put(page_id, new partialScore(partialScore,0));
				}
			}
			
			if(titlelist != null)
			{
				for(Posting p:titlelist)
				{
					String page_id = p.page_id;
					double partialScore = termWeight.getWeight(p.page_id, word_id, false);
					if(vsScores.get(page_id) != null)
					{
						partialScore = vsScores.get(page_id).title + partialScore;
						//System.out.println(vsScores.get(page_id).title + " " + word_id + " " + partialScore);
					}
					double bodyScore = vsScores.get(page_id).body;
					vsScores.put(page_id, new partialScore(bodyScore,partialScore));
				}
			}
		}
		
		Vector<Score> result = new Vector<Score>();
		Iterator it = vsScores.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, partialScore> pairs = (Map.Entry<String, partialScore>)it.next();
	        String page_id = pairs.getKey();
	        partialScore partialSum = pairs.getValue();

	        //to approximate the document length
	        Vector<InvertPosting> temp = (Vector<InvertPosting>) invertedBodyWordHash.get(page_id);
	        double bodyLength =  Math.sqrt(Math.sqrt(temp.size()*1.0));
	        temp = (Vector<InvertPosting>) invertedTitleWordHash.get(page_id);
	        double titleLength =  Math.sqrt(Math.sqrt(temp.size()*1.0));

	        result.add(new Score(page_id, partialSum.body / bodyLength, partialSum.title / titleLength, PR.getPageRank(page_id)));
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    Collections.sort(result);
	    
	    return result;
	}
	
	
	public static void main(String[] arg) throws IOException, ParserException
	{

		Query r = new Query();
		Vector<Score> result = r.getScore("Spitfire Grill");
		for(Score i: result)
		{
			System.out.println( i.page_id + " " + i.vsScoreBody + " " + i.vsScoreTitle + " " + i.pageRank +  " " + i.overall);
		}
		
		System.out.print(String.format("%.2f", 1.24342));
	}
}
