package comp4321;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import jdbm.RecordManager;

public class Phrase {

	private RecordManager recman;
	private String[] words;
	private DataStruc pageInfo;
	private DataStruc wordID;
	private DataStruc invertedBodyWord;
	private DataStruc invertedTitleWord;
	private Hashtable<String, Integer> bodydf;				// <bodyWord, df>
	private Hashtable<String, Integer> titledf;				// <titleWord, df>
	private Hashtable<String, Integer> bodyFreqs;			// <pageId, phraseFreq>
	private Hashtable<String, Integer> titleFreqs;			// <pageId, phraseFreq>
	private Hashtable<String, Double> bodyWeight;			// <pageId, phraseWeight>
	private Hashtable<String, Double> titleWeight;			// <pageId, phraseWeight>
		
	public Phrase(RecordManager _recman, String _words) throws IOException
	{
		recman = _recman;
		pageInfo = new DataStruc(recman, "pageInfo");
		wordID = new DataStruc(recman,"wordID");
		invertedBodyWord = new DataStruc(recman, "bodyWord");
		invertedTitleWord = new DataStruc(recman, "titleWord");	
		bodydf = new Hashtable<String, Integer>();
		titledf = new Hashtable<String, Integer>();
		bodyFreqs = new Hashtable<String, Integer>();
		titleFreqs = new Hashtable<String, Integer>();
		bodyWeight = new Hashtable<String, Double>();
		titleWeight = new Hashtable<String, Double>();
		words = _words.split(" ");
		for(int i = 0; i<words.length; i++) 
		{
			String wordStem = StopStem.processing(words[i]);
			words[i] = (String) wordID.getEntry(wordStem);
		}
		compWeight();
		System.out.println(_words);
	}
	
	public Hashtable<String, Double> getWeight(boolean isBody)
	{
		if(isBody) return bodyWeight;
		else return titleWeight;
	}
	
	public void compWeight() throws IOException
	{
		compOneWeight(bodydf, bodyFreqs, bodyWeight);
		compOneWeight(titledf, titleFreqs, titleWeight);
	}
	
	/**
	 * Compute body/title weights of the phrases of the pages
	 * @throws IOException
	 */
	public void compOneWeight(Hashtable<String, Integer> df, 
			Hashtable<String, Integer> tf, Hashtable<String, Double> weight ) throws IOException
	{
		double idf = 0.0;
		String pageId;
		
		compFreq();
		
		// compute idf of this phrase
		for(String oneWord: words)
			idf += Math.log(pageInfo.getSize() * 1.0 / df.get(oneWord)) / Math.log(2);
		
		Enumeration<String> e = tf.keys();
		while(e.hasMoreElements())
		{
			pageId = e.nextElement();
			weight.put(pageId, (idf * (double)tf.get(pageId)) );
		}
	}
	
	public void compFreq() throws IOException
	{
		findPosts(invertedBodyWord, bodyFreqs, bodydf);
		findPosts(invertedTitleWord, titleFreqs, titledf);
	}
	
	/**
	 * find words' postings from "word" hash
	 * @return
	 * @throws IOException 
	 */
	public void findPosts(DataStruc invertedWord, Hashtable<String, Integer> freqs, Hashtable<String, Integer> dfs ) throws IOException
	{
		Vector<Vector<Posting>> posts = new Vector<Vector<Posting>>();
		Vector<Integer> indices = new Vector<Integer>();
		
		// initialize posts with word posts
		Vector<Posting> vp;
		for(String oneWord: words)
		{
			vp = (Vector<Posting>) invertedWord.getEntry(oneWord);
			posts.add(vp);
			if(vp == null) dfs.put(oneWord, 0);
			else dfs.put(oneWord, vp.size());
			indices.add(0);
		}
		
		// find the posts where all words exists
		Hashtable<String, Vector<Posting>> occur = new Hashtable<String, Vector<Posting>>();
		Vector<Posting> cnt;
		String pageId;
		for(Vector<Posting> onePosts: posts )
		{
			for(Posting onePost: onePosts)
			{
				pageId = onePost.page_id;
				if(occur.containsKey(pageId)) cnt = occur.get(pageId);
				else 
				{
					cnt = new Vector<Posting>();
					cnt.add(onePost);
				}
				occur.put(pageId, cnt);
			}
		}
		
		String tmpKey;				// page id
		Vector<Posting> tmpVec;		// postings of one page id
		int freq;
		Enumeration<String> e = occur.keys();
		while(e.hasMoreElements())
		{
			tmpKey = e.nextElement();
			tmpVec = occur.get(tmpKey);
			if( tmpVec.size() == 3 ) 
			{
				freq = findFreq(tmpVec);
				freqs.put(tmpKey, freq);
			}
		}
		
	}
	
	/**
	 * Given posts of same page_id, find the frequency that the phrase words appear together.
	 * @param posts
	 * @return
	 */
	public int findFreq(Vector<Posting> posts)
	{
		int freq = 0;
		Vector<Vector<Integer>> positions = new Vector<Vector<Integer>>();	// vector of position lists
		//Vector<Iterator<Integer>> iters = new Vector<Iterator<Integer>>();
		//Iterator<Iterator<Integer>> it;
		Vector<Integer> indices= new Vector<Integer>();						// vector of pointers to the position lists
		
		// initialize positions and iters, get position lists from postings
		for(Posting onePost: posts)
		{
			positions.add(onePost.position);
			indices.add(0);
		}
		
		Vector<Integer> v1;
		Vector<Integer> v2;
		Integer i1;
		Integer i2;
		int i;
		boolean flag;
		for(;indices.elementAt(0) < positions.firstElement().size(); indices.setElementAt(0, indices.elementAt(0)+1))
		{
			i = 0;
			flag = false;
			for(; i < indices.size()-1; i++)
			{
				// compare if indices[i]@positions[i] is adjacent  to indices[i+1]@positions[i+1]
				v1 = positions.elementAt(i);
				v2 = positions.elementAt(i+1);
				i1 = indices.elementAt(i);
				i2 = indices.elementAt(i+1);
				while( (i2 < v2.size()) && (v2.elementAt(i2) < v1.elementAt(i1)) ) i2++;
				indices.setElementAt(i+1, i2);
				if(v2.elementAt(i2) == (v1.elementAt(i1)+1)) 
					flag = true;
				else 
					break;
			}
			
			if(flag) freq++;
		}
		
		
		return freq;
	}
	
}
