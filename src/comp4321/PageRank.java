package comp4321;
 
import java.util.*;
import java.io.*;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;


public class PageRank {
	
	private RecordManager recman;
	//private DataStruc pageID;
	private DataStruc parentLink;
	private DataStruc childLink;
	private DataStruc hubWeight;
	private DataStruc authWeight;
	private DataStruc pageRank;					//11.19
	
	public PageRank(RecordManager _recman) throws IOException 
	{
		recman = _recman;
		//pageID = new DataStruc(recman, "pageID");
		parentLink = new DataStruc(recman, "parentLink");
		childLink = new DataStruc(recman, "childLink");
		hubWeight = new DataStruc(recman, "hubWeight");
		authWeight = new DataStruc(recman, "authWeight");
		pageRank = new DataStruc(recman, "pageRank");
	}
	
	/**
	 * Called by Spider when meet unvisited url, add page_id as the parent id of all its children ids. 
	 * child->parent links
	 * 
	 * @param url, links
	 * @throws IOException 
	 */
	public void addParentLink(String page_id, Vector<String> child_ids) throws IOException 
	{
		Vector<String> parents;
		for(String oneChild: child_ids) 
		{		
			if(parentLink.getEntry(oneChild)==null) 
			{
				parents = new Vector<String>();							// create new Vector if this child doesn't have an entry.
			}else{
				//System.out.println(parentLink.getEntry(oneChild));
				parents = (Vector<String>) parentLink.getEntry(oneChild);
				if(parents.contains(page_id)) continue;					// if it already has this parent, do nothing
			}
			parents.add(page_id);
			parentLink.addEntry(oneChild, parents);		// overwrite the previous entry or add a new one.
		}
	}
	
	/**
	 * Called by Spider when meet unvisited url, add the children ids of this page_id.
	 * parent->child links
	 * 
	 * @param url, links
	 * @throws IOException 
	 */
	public void addChildLink(String page_id, Vector<String> child_ids) throws IOException 
	{
		Vector<String> children;
		if(childLink.getEntry(page_id)==null) 
		{
			children = child_ids;
		}else{
			children = (Vector<String>) childLink.getEntry(page_id);
			children.addAll(child_ids);
			Collection noDup = new LinkedHashSet(children);
			children.clear();
			children.addAll(noDup);
		}
		childLink.addEntry(page_id, children);
	}
	
	/**
	 * Called by Spider to compute Hub Authority weights. Initial values are all 1.
	 * 
	 * @param _iterNum how many iterations before stop, by default 10
	 * @throws IOException
	 */
	public void computeHubAuth (int..._iterNum) throws IOException
	{
		int iterNum;
		if(_iterNum.length==0) iterNum=10;
		else iterNum = _iterNum[0];
		
		// variable declaration
		Hashtable<String, Object> hubPrev = new Hashtable<String, Object>();
		Vector<String> oneVector;
		double oneWeight;
		double totalHub;
		double totalAuth;
		double totalHubPrev;
		double totalAuthPrev;
		FastIterator iter;
		String keyword = null;		
		HTree hubHash;
		HTree authHash;		
		
		// initialize all the weights with 1
		totalHub = 0;
		totalAuth = 0;
		//iter = pageID.getHash().keys();
		iter = parentLink.getHash().keys();
		while( (keyword=(String)iter.next()) != null)
		{
			hubWeight.addEntry(keyword, 1.0);
			authWeight.addEntry(keyword, 1.0);
			totalHub += 1.0 * 1.0;
			totalAuth += 1.0 * 1.0;
		}
		System.out.println("Intialization");
		printHubAuth();		
		
 		hubHash = hubWeight.getHash();
 		authHash = authWeight.getHash();
 		
 		//System.out.println(hubHash.keys()==authHash.keys());
 		
 		// iteratively compute the weights
		for(int i = 0; i < iterNum; i++)
		{
			// store the previous state of hub and authority
			iter = hubHash.keys();
			while( (keyword=(String)iter.next()) != null)
				hubPrev.put(keyword, hubHash.get(keyword));
	 		totalHubPrev = Math.sqrt(totalHub);
	 		totalAuthPrev = Math.sqrt(totalAuth);
			
			// compute hubs from normalized previous authWeight, hubWeight
	 		totalHub = 0;
			iter = hubHash.keys();
	 		//iter = pageID.getHash().keys();
			while( (keyword=(String)iter.next()) != null)
			{
				oneVector = (Vector<String>) childLink.getEntry(keyword);
				oneWeight = 0;
				for(String oneId: oneVector) 
					oneWeight += (double)authHash.get(oneId);
				oneWeight /= totalHubPrev;
				totalHub += oneWeight * oneWeight;
				hubWeight.addEntry(keyword, oneWeight);
			}
			
			// System.out.println(hubHash==hubWeight.getHash());
			
			// compute authorities
			totalAuth = 0;
			iter = authHash.keys();
			while( (keyword=(String)iter.next()) != null)
			{
				oneVector = (Vector<String>) parentLink.getEntry(keyword);
				oneWeight = 0;
				for(String oneId: oneVector)
					oneWeight += (double)hubPrev.get(oneId);
				oneWeight /= totalAuthPrev;
				totalAuth += oneWeight * oneWeight;
				authWeight.addEntry(keyword, oneWeight);
			}
			
			System.out.println("Iteration: "+i);
			printHubAuth();
		}
		
		// normalize hub and authority weights
		totalHubPrev = Math.sqrt(totalHub);
 		totalAuthPrev = Math.sqrt(totalAuth);
 		iter = parentLink.getHash().keys();
 		while( (keyword=(String)iter.next()) != null)
		{
			oneWeight = (double) hubWeight.getEntry(keyword);
			hubWeight.addEntry(keyword, oneWeight/totalHubPrev);
			oneWeight = (double) authWeight.getEntry(keyword);
			authWeight.addEntry(keyword, oneWeight/totalAuthPrev);
		}
 		printHubAuth();
	}
	
	/**
	 * Called by Spider to compute Page Rank values. Initial values are all 1.
	 * 
	 * @param d damping factor
	 * @param _iterNum how many iterations before stop, by default 10
	 * @throws IOException
	 */
	public void compPageRank(double d, int..._iterNum) throws IOException
	{
		int iterNum;
		if(_iterNum.length==0) iterNum=10;
		else iterNum = _iterNum[0];
		
		// variable declaration
		Hashtable<String, Object> rankPrev = new Hashtable<String, Object>();
		Vector<String> oneParents;
		Vector<String> oneChildren;
		double oneWeight;
		FastIterator iter;
		String keyword = null;		
		HTree rankHash;
		
		// initialize all the weights with 1
		iter = childLink.getHash().keys();
		while( (keyword=(String)iter.next()) != null)
			pageRank.addEntry(keyword, 1.0);
		
		System.out.println("After Initialization");
		printPageRank();
		
		rankHash = pageRank.getHash();
		
		// iteratively compute page rank
		for(int i = 0; i < iterNum; i++)
		{
			// store the previous page rank values
			iter = rankHash.keys();
			while( (keyword=(String)iter.next()) != null)
				rankPrev.put(keyword, rankHash.get(keyword));
			
			// compute page rank value from the previous state
			iter = rankHash.keys();
			while( (keyword=(String)iter.next()) != null)
			{
				oneParents = (Vector<String>) parentLink.getEntry(keyword);
				oneWeight = 0;
				for(String oneId: oneParents) 
				{
					oneChildren = (Vector<String>) childLink.getEntry(oneId);
					oneWeight += ((double)rankPrev.get(oneId)) / oneChildren.size();
				}
				oneWeight = (1-d) + d * oneWeight;
				pageRank.addEntry(keyword, oneWeight);
			}
			
			System.out.println("Iteration: "+i);	
			printPageRank();
		}
		
	}
	
	double getPageRank (String page_id) throws IOException
	{
		return (double)pageRank.getEntry(page_id);
	}
	
	int getHubWeight (String page_id) throws IOException
	{
		return (int)hubWeight.getEntry(page_id);
	}
	
	int getAuthWeight (String page_id) throws IOException
	{
		return (int)authWeight.getEntry(page_id);
	}
	
	public void printHubAuth() throws IOException
	{
		FastIterator iter;
		String keyword;
		
		System.out.println("-----------------Hub-----------------");
		iter = hubWeight.getHash().keys();
		while( (keyword=(String)iter.next()) != null)
		{
			System.out.println(keyword + ":" + hubWeight.getEntry(keyword));
		}
		
		System.out.println("-----------------Auth-----------------");
		iter = authWeight.getHash().keys();
		while( (keyword=(String)iter.next()) != null)
		{
			System.out.println(keyword + ":" + authWeight.getEntry(keyword));
		}		
		
		System.out.println("End");
	}
	
	public void printPageRank() throws IOException
	{
		FastIterator iter;
		String keyword;
		
		System.out.println("-----------------PageRank-----------------");
		iter = pageRank.getHash().keys();
		while( (keyword=(String)iter.next()) != null)
		{
			System.out.println(keyword + ":" + pageRank.getEntry(keyword));
		}
		
		System.out.println();
	}
}