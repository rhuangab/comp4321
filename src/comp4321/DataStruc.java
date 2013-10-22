package comp4321;

import java.io.IOException;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

public class DataStruc {

	private HTree hashtable;
	
	public DataStruc(RecordManager recman, String objectname) throws IOException
	{
		// Constructor, get the hashtable from db, build a new one if it does not exist
		long recid = recman.getNamedObject(objectname);
		//System.out.println(objectname + recid);
		if (recid != 0)
		{
			hashtable = HTree.load(recman, recid);
		}
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject(objectname, hashtable.getRecid());
		}
	}
	
	public Object getEntry(String key) throws IOException
	{
		// return key's content from the hashtable, it may be null
		return hashtable.get(key);
	}
	
	public void addEntry(String key, Object x) throws IOException
	{
		// insert the entry into hashtable by the key index
		hashtable.put(key, x);
	}
	
	public void delEntry(String key) throws IOException
	{
		// Delete the word and its content from the hashtable
		hashtable.remove(key);
	}
	
	public HTree getHash() throws IOException
	{
		// return the hashtable reference for complex operations
		return hashtable;
	}
	
	public int getSize() throws IOException
	{
		// return the hashtable size
		FastIterator iter = hashtable.keys();
		int count = 0;
		while( iter.next() != null)
			count++;
		return count;
	}
	
	public FastIterator getIterator() throws IOException
	{
		return getHash().keys();
	}
	
	public void clear() throws IOException{
		FastIterator iter = hashtable.keys();
		
		//String temp = (String) iter.next();
		
		this.delEntry("0003");
		/*while(temp != null)
		{
			System.out.println(temp);
			//this.delEntry("0003");
			temp = (String) iter.next();
		}*/
	}
	
	
}
