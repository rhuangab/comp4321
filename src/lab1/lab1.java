package lab1;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

/*
 * RecordManager can be interpreted as a manager of database, it responses for the communication with the hard disk, 
 * under which the objects created will not be lost after powering off. The content stored in recman is object, including
 * hashtable. Hashtable is only one implementation of indexing (others like Btree), it can store the records into memory but cannot write them into hard disk,
 * and it needs the help of record manager. That is why hash table has to be provided with an argument of record manager
 * during the creation. After being created, hash table will automatically be stored in the corresponding record manager.  */
class Posting implements Serializable {
	public String doc;
	public int freq;

	Posting(String doc, int freq) {
		this.doc = doc;
		this.freq = freq;
	}
}

public class lab1 {
	private RecordManager recman;
	private HTree hashtable;

	lab1(String recordmanager, String objectname) throws IOException {
		// recman = RecordManagerFactory.createRecordManager(recordmanager);
		recman = RecordManagerFactory.createRecordManager("./database");
		long recid = recman.getNamedObject(objectname);

		if (recid != 0) // If the object has already been recorded in record
						// manager;
			hashtable = HTree.load(recman, recid);

		else // If not, create a new hashtable;
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject("ht1", hashtable.getRecid()); // Store object
																// hashtable ht1
																// into recman
																// as the name
																// "ht1";
		}
	}

	public void finalize() throws IOException {
		recman.commit();
		recman.close();
	}

	public void addEntry(String word, int x, int y) throws IOException {
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		// Test if a term has already existed in the inverted index file. If so, stop insertion and return directly.
		// If not, insert to the inverted file;
		// If there is no the following "if" block, the posting lists will accumulate after several time's running the code.
		if (hashtable.get(word)!=null && ((String) hashtable.get(word)).contains( "doc" + x+ " " + y))
		{
			return;
		}
		String new_entry = "doc" + x + " " + y + " ";
		// Since "put" will cover the previous insertion, it is necessary to
		// extract the existed data first;
		String existed_entry = "";
		if (hashtable.get(word) != null) {
			existed_entry = (String) hashtable.get(word);
		}

		hashtable.put(word, existed_entry + new_entry);
	    recman.commit();
	}

	public void delEntry(String word) throws IOException {
		// Delete the word and its list from the hashtable
		// ADD YOUR CODES HERE
		hashtable.remove(word);
	}

	public void printAll() throws IOException {
		// Print all the data in the hashtable
		// ADD YOUR CODES HERE
		FastIterator iter = hashtable.keys();
		String key;
		while ((key = (String) iter.next()) != null) {
			System.out.println(key + ": " + hashtable.get(key));
		}
	}

	public static void main(String[] args) {
		try {
			lab1 index = new lab1("lab1", "ht1");

			index.addEntry("cat", 2, 6);
			index.addEntry("dog", 1, 33);
			System.out.println("First print");
			index.printAll();

			index.addEntry("cat", 8, 3);
			index.addEntry("dog", 6, 73);
			index.addEntry("dog", 8, 83);
			index.addEntry("dog", 10, 5);
			index.addEntry("cat", 11, 106);

			System.out.println("Second print");
			index.printAll();

			index.delEntry("dog");
			System.out.println("Third print");
			index.printAll();
			index.finalize();
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}

	}
}
