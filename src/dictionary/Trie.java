package dictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import comp4321.DataStruc;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class Trie implements Serializable {
	private TrieNode root;
	private RecordManager recman;

	/**
	 * Constructor
	 * @throws IOException 
	 */
	public Trie() {
		root = new TrieNode();
	}

	/**
	 * Adds a word to the Trie
	 * 
	 * @param word
	 */
	public void addWord(String word) {
		if (word != null && word.length() > 0)
			root.addWord(word.toLowerCase());
	}

	/**
	 * Get the words in the Trie with the given prefix
	 * 
	 * @param prefix
	 * @return a List containing String objects containing the words in the Trie
	 *         with the given prefix.
	 */
	public List<String> getWords(String prefix) {
		// Find the node which represents the last letter of the prefix
		TrieNode lastNode = root;
		for (int i = 0; i < prefix.length(); i++) {
			lastNode = lastNode.getNode(prefix.charAt(i));

			// If no node matches, then no words exist, return empty list
			if (lastNode == null)
				return new ArrayList<String>();
		}

		// Return the words which eminate from the last node
		return lastNode.getWords();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
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
		/*
		 * for(int i = 0;i < 26;i++) { String key = ""+ (char)('a'+i);
		 * wordTable.addEntry(key, trie[i]); } recman.commit(); recman.close();
		 */
		/*
		while (true) {
			System.out.println("Please input prefix to search:");
			Scanner b = new Scanner(System.in);
			String input = b.next();
			if (input != null && input.length() > 0) {
				String newKey = "" + input.charAt(0);
				Trie newTrie = (Trie)wordTable.getEntry(newKey);
				List<String> list = newTrie.getWords(input);
				if(list != null && list.size() > 0){
				for (String word : list) {
					System.out.println(word);
				}
				}
			}
		}*/
	}
}
