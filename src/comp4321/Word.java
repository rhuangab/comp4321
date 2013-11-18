package comp4321;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.htmlparser.util.ParserException;

import jdbm.RecordManager;

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


public class Word {
	private DataStruc wordID;
	private DataStruc bodyWord;
	private DataStruc titleWord;
	private DataStruc invertedWord;
	private DataStruc word;
	private RecordManager recman;
	int wordCount;
	private int pageSize;
	private StopStem stopstem;
	
	/**constructor
	 * **/
	public Word(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		wordID = new DataStruc(recman,"wordID");
		bodyWord = new DataStruc(recman,"bodyWord");
		titleWord = new DataStruc(recman,"titleWord");
		invertedWord = new DataStruc(recman, "invertedWord");
		word = new DataStruc(recman, "word");
		wordCount = wordID.getSize();
		stopstem = new StopStem();
	}
	
	/**insert a new word**/
	public String insertWord(String newWord) throws IOException
	{
		String temp = newWord;
		newWord = stopstem.processing(newWord);
		//System.out.println(temp + " " + newWord);
		
		if(newWord == null || newWord =="")
			return null;
		
		if(wordID.getEntry(newWord) != null)
			return (String) wordID.getEntry(newWord);
		
		String id = String.format("%08d", wordCount++);
		wordID.addEntry(newWord, id);
		word.addEntry(id, newWord);
		return id;
	}
	
	/**get word_id**/
	public String getWordID(String word) throws IOException
	{
		if(wordID.getEntry(word) == null)
			return null;
		
		return  (String) wordID.getEntry(word);
	}
	
	
	/**if the list exist. add the new word to the end, else create a new list
	 * @param word_pos **/
	public void insertWordTF(String word_id, String page_id, int word_tf, Vector<Integer> word_pos, boolean isBody) throws IOException
	{
		//System.out.println(word_id + ": (" + page_id + "," + word_tf + ")");	
		if(isBody)
		{
			if(bodyWord.getEntry(word_id) != null)
			{
				Vector<Posting> postingList = (Vector<Posting>) bodyWord.getEntry(word_id);
				postingList.add(new Posting(page_id,word_tf,word_pos));
				bodyWord.addEntry(word_id, postingList);
				//((Vector<Posting>) wordTF.getEntry(word_id)).add(new Posting(page_id,word_tf));
			}
			else
			{
				Vector<Posting> postingList = new Vector<Posting>();
				postingList.add(new Posting(page_id,word_tf,word_pos));
				bodyWord.addEntry(word_id, postingList);
			}
		}
		else
		{
			if(titleWord.getEntry(word_id) != null)
			{
				Vector<Posting> postingList = (Vector<Posting>) titleWord.getEntry(word_id);
				postingList.add(new Posting(page_id,word_tf,word_pos));
				titleWord.addEntry(word_id, postingList);
				//((Vector<Posting>) wordTF.getEntry(word_id)).add(new Posting(page_id,word_tf));
			}
			else
			{
				Vector<Posting> postingList = new Vector<Posting>();
				postingList.add(new Posting(page_id,word_tf,word_pos));
				titleWord.addEntry(word_id, postingList);
			}
		}

	}
	
	/**if the list exist. add the new word to the end, else create a new list**/
	public void insertInvertedWord(String page_id, String word) throws IOException
	{
		if(invertedWord.getEntry(page_id) != null)
		{
			Vector<String> list = (Vector<String>) invertedWord.getEntry(page_id);
			list.add(word);
			invertedWord.addEntry(page_id, list);
			//((Vector<String>) invertedWord.getEntry(page_id)).add(word);
		}
		else
		{
			Vector<String> temp = new Vector<String>();
			temp.add(word);
			invertedWord.addEntry(page_id, temp);
		}
		/*
		System.out.print(page_id + ":");
		Vector<String> temp = (Vector<String>) invertedWord.getEntry(page_id);
		for(String i: temp)
			System.out.print("(" + i + ") " );	
		System.out.println();*/
		
	}
	
	public void build(String page_id, String url, boolean isBody) throws ParserException, IOException
	{
		Vector<String> words = new Vector<String>();
		if(isBody)
		{
			words = Indexer.extractWords(url);
			pageSize = words.size();
			/*
			for(String i:words)
				System.out.print(i + " ");
			System.out.println("==================================");*/
		}
		else
		{
			String title = Indexer.extractTitle(url);
	        String[] temp = title.split("\\W+");

			for(int i = 0; i < temp.length; i++)
				words.add(temp[i].toLowerCase());
		}
		
		HashMap<String, Integer> word_tf = new HashMap<String, Integer>();
		HashMap<String, Vector<Integer> > word_pos = new HashMap<String, Vector<Integer> >();
		for(int i = 0; i < words.size(); i++)
		{
			/**
			 * Create a new word_id when the word appears first time
			 * otherwise, get the existing word_id**/
			String word_id;
			if(getWordID(words.get(i)) == null)
				word_id = insertWord(words.get(i));
			else
				word_id = getWordID(words.get(i));
			
			if(word_id == null)
				continue;
			/**
			 * everytime encounter a word
			 * update the corresponding term frequency
			 * and insert to the inverted talbe if it is the first time.
			 */
			
			int tf;
			Vector<Integer> pos;
			if(word_tf.get(word_id) == null)
			{
				tf = 1;
				pos = new Vector<Integer>();
				pos.add(i);
				insertInvertedWord(page_id, word_id);
			}
			else
			{
				tf = word_tf.get(word_id) + 1;
				pos = word_pos.get(word_id);
				pos.add(i);
			}
			
			word_tf.put(word_id, tf);
			word_pos.put(word_id, pos);
		}
		
		/**
		 * insert the result above to term frequency table
		 * */
		Iterator it = word_tf.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	        insertWordTF(pairs.getKey(), page_id, pairs.getValue(), word_pos.get(pairs.getKey()), isBody);
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	/** the main function for this class
	 *  record the term_freq table, word_id table, word_inverted table**/
	public void indexWordInfo(String page_id, String url) throws ParserException, IOException
	{
		//body
		build(page_id, url, true);
		
		//title
		build(page_id, url, false);
	}
	
	public int getPageSize()
	{
		return pageSize;
	}

}
