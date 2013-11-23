package dictionary;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import comp4321.DataStruc;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class Dictionary {
	private RecordManager recman;
	private DataStruc wordTable;
	
	public Dictionary() throws IOException
	{
		recman = RecordManagerFactory.createRecordManager("/Library/Tomcat/apache-tomcat-6.0.37/webapps/comp4321/database/MyDictionary");
		wordTable = new DataStruc(recman, "dictionary");
	}
	
	public Vector<String> retrieveWordList(String input) throws IOException
	{
		if(input !=null && input.length() >0)
		{
			Trie trie = (Trie)wordTable.getEntry(""+input.charAt(0));
			if(trie == null)
				return new Vector<String>();
			
			List<String> list = trie.getWords(input);
			Vector<String> v = new Vector<String>();
			for(int i =0; i < 5 && i < list.size(); i++)
			{
				v.add(list.get(i));
			}
			return v;
		}
		else
			return new Vector<String>();
	}
	
	public static void main(String[] args) throws IOException {
		/*Trie trie[] = new Trie[26];
		File file = new File("dictionary.txt");
		Scanner sc = new Scanner(file);
		for (int i = 0; i < 26; i++) {
			trie[i] = new Trie();
		}
		while (sc.hasNext()) {
			// System.out.println(sc.next());
			String currentWord = sc.next();
			if (currentWord != null && currentWord.length() > 0)
				trie[currentWord.charAt(0) - 'a'].addWord(currentWord);
		}
		sc.close();
		System.out.println("Finish building trie");
		
		RecordManager recman = RecordManagerFactory
		    .createRecordManager("MyDictionary");
		DataStruc wordTable = new DataStruc(recman, "dictionary");		
		for(int i = 0;i < 26;i++) { 
			String key = ""+ (char)('a'+i);
			wordTable.addEntry(key, trie[i]); 
			} 
		recman.commit(); 
		recman.close();
		 */
		
		
		
		Dictionary dictionary = new Dictionary();
		Vector<String> wordList = dictionary.retrieveWordList("ca");
		
		for(String word : wordList)
		{
			System.out.println(word);
		}
	}
}
