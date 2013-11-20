package comp4321;

import java.io.IOException;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;
/**
 * 
 * @author HUANG Richeng
 * This table stores (page_id -> pageInfoStruct)
 */
public class PageInfo {
	
	private DataStruc pageInfo;
	private RecordManager recman;
	private DataStruc pageId;
	
	public PageInfo(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		pageInfo = new DataStruc(recman,"pageInfo");
		pageId = new DataStruc(recman,"pageID");
	}
	
	/** Given PageInfoStruct object, insert into hashtable*/
	public void insertElement(String page_id, PageInfoStruct new_page, long page_size) throws IOException
	{
		new_page.setPageSize(page_size);				// set page_size if not set
		if( new_page.setPageId(page_id) )				// set page_id if not set
		{
			pageInfo.addEntry(page_id, new_page);		// add entry only if page_id is set successfully
		}
	}
	
	public long getLastModificationLong(String url) throws IOException
	{
		String page_id = (String)pageId.getEntry(url);
		PageInfoStruct pi = (PageInfoStruct) pageInfo.getEntry(page_id);
		if(pi != null)
			return pi.getLastModificationLong();
		else
			return 0;
	}
	
	public DataStruc getName()
	{
		return pageInfo;
	}
}

