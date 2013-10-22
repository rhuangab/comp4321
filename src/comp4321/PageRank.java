package comp4321;
 
import java.util.*;
import java.io.*;

import jdbm.RecordManager;


public class PageRank {
	
	private RecordManager recman;
	private DataStruc parentLink;
	private DataStruc childLink;
	private DataStruc hubWeight;
	private DataStruc authWeight;
	
	public PageRank(RecordManager _recman) throws IOException 
	{
		recman = _recman;
		parentLink = new DataStruc(recman, "parentLink");
		childLink = new DataStruc(recman, "childLink");
		hubWeight = new DataStruc(recman, "hubWeight");
		authWeight = new DataStruc(recman, "authWeight");
	}
	
	/**
	 * Called by Spider when meet unvisited url, add page_id as the parent id of all its children ids
	 * @param url, links
	 * @throws IOException 
	 */
	public void addParentLink(String page_id, Vector<String> child_ids) throws IOException 
	{
		Vector<String> oneParent;
		for(String oneId: child_ids) 
		{		
			System.out.println("oneId:"+oneId);
			if(parentLink.getEntry(oneId)==null) 
			{
				oneParent = new Vector<String>();							// create new Vector if this child doesn't have an entry.
			}else{
				oneParent = (Vector<String>) parentLink.getEntry(oneId);
				if(oneParent.contains(page_id)) continue;					// if it already has this parent, do nothing
			}
			parentLink.addEntry(oneId, oneParent.add(page_id));				// overwrite the previous entry or add a new one.
		}
	}
	
	/**
	 * Called by Spider when meet unvisited url, add the children ids of this page_id
	 * @param url, links
	 * @throws IOException 
	 */
	public void addChildLink(String page_id, Vector<String> child_ids) throws IOException 
	{
		if( childLink.getEntry(page_id)==null ){
			childLink.addEntry(page_id, child_ids);
		}
	}
	
	public DataStruc computeHub (float dumplingFactor) throws IOException
	{
		
		return hubWeight;
	}
	
	public DataStruc computeAuth (float dumplingFactor) throws IOException
	{
		
		return authWeight;
	}
	
}