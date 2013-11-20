package comp4321;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

class wordList{
	int size;
	LinkedList<InvertPosting> words;
	wordList(int s)
	{
		size = s;
		words = new LinkedList<InvertPosting>();
	}
	
	void add(InvertPosting p)
	{
		if(words.isEmpty())
			words.add(p);
		else
		{
			for(int i = 0; i < words.size(); i++)
			{
				InvertPosting current = words.get(i);
				if(i == words.size() - 1)
				{
					if(p.freq >= current.freq)
					{
						words.add(i, p);
						if(words.size() > size)
							words.removeLast();
						return;
					}
				}
				else
				{
					InvertPosting next = words.get(i);
					if(p.freq >= next.freq && p.freq < current.freq)
					{
						words.add(i+1, p);
						if(words.size() > size)
							words.removeLast();
						return;
					}
					else if(p.freq >= current.freq)
					{
						words.add(i, p);
						if(words.size() > size)
							words.removeLast();
						return;
					}
				}
			}
			
		}
	}
	
	LinkedList<InvertPosting> getWordList()
	{
		return words;
	}
}
public class SimilarPage {
	
	DataStruc bodyWord;
	DataStruc titleWord;
	DataStruc pageInfo;
	DataStruc invertedWord;
	
	private RecordManager recman;
	
	public SimilarPage(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		bodyWord = new DataStruc(recman,"bodyWord");
		titleWord = new DataStruc(recman,"titleWord");
		pageInfo = new DataStruc(recman, "pageInfo");
		invertedWord = new DataStruc(recman, "invertedBodyWord");
	}

	LinkedList<InvertPosting> getTopWords(String page_id) throws IOException
	{
		wordList candidates = new wordList(5);
		HTree wordList = invertedWord.getHash();
		Vector<InvertPosting> temp = (Vector<InvertPosting>) wordList.get(page_id);
		if(temp == null)
			return new LinkedList<InvertPosting>();
		for(InvertPosting w: temp)
			candidates.add(w);
		
		return candidates.getWordList();
	}

}
