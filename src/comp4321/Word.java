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

	Posting(String doc, int freq) {
		this.page_id = doc;
		this.freq = freq;
	}
	
}
public class Word {
	private DataStruc wordID;
	private DataStruc wordTF;
	private DataStruc invertedWord;
	private DataStruc word;
	private RecordManager recman;
	int wordCount;
	private int pageSize;
	
	/**constructor
	 * **/
	public Word(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		wordID = new DataStruc(recman,"wordID");
		wordTF = new DataStruc(recman,"wordTF");
		invertedWord = new DataStruc(recman, "invertedWord");
		word = new DataStruc(recman, "word");
		wordCount = wordID.getSize();
	}
	
	/**insert a new word**/
	public String insertWord(String newWord) throws IOException
	{
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
	
	
	/**if the list exist. add the new word to the end, else create a new list**/
	public void insertWordTF(String word_id, String page_id, int word_tf) throws IOException
	{
		//System.out.println(word_id + ": (" + page_id + "," + word_tf + ")");	
		if(wordTF.getEntry(word_id) != null)
		{
			Vector<Posting> postingList = (Vector<Posting>) wordTF.getEntry(word_id);
			postingList.add(new Posting(page_id,word_tf));
			wordTF.addEntry(word_id, postingList);
			//((Vector<Posting>) wordTF.getEntry(word_id)).add(new Posting(page_id,word_tf));
		}
		else
		{
			Vector<Posting> postingList = new Vector<Posting>();
			postingList.add(new Posting(page_id,word_tf));
			wordTF.addEntry(word_id, postingList);
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
	
	/** the main function for this class
	 *  record the term_freq table, word_id table, word_inverted table**/
	public void indexWordInfo(String page_id, String url) throws ParserException, IOException
	{
		Vector<String> words = Indexer.extractWords(url);
		pageSize = words.size();
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
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
			
			/**
			 * everytime encounter a word
			 * update the corresponding term frequency
			 * and insert to the inverted talbe if it is the first time.
			 */
			int tf;
			if(temp.get(word_id) == null)
			{
				tf = 1;
				insertInvertedWord(page_id, word_id);
			}
			else
				tf = temp.get(word_id) + 1;
			
			temp.put(word_id, tf);
		}
		
		/**
		 * insert the result above to term frequency table
		 * */
		Iterator it = temp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	        insertWordTF(pairs.getKey(), page_id, pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public int getPageSize()
	{
		return pageSize;
	}

}
