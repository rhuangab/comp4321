package comp4321;

import java.io.IOException;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;

public class PageInfo {
	
	private FileStruc pageInfo;
	private RecordManager recman;
	
	public PageInfo(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		pageInfo = new FileStruc(recman,"pageInfo");
	}
	
	public void insertElement(String page_id, String url, int pageSize) throws ParserException, IOException
	{
		PageInfoStruct newPageInfo = new PageInfoStruct(url,page_id,pageSize);
		if(pageInfo.getEntry(page_id) !=null)
		{
			//PageInfo dbPageInfo = (PageInfo) pageInfoTable.getEntry(page_id);
			pageInfo.delEntry(page_id);
			pageInfo.addEntry(page_id, newPageInfo);
		}
		pageInfo.addEntry(page_id, newPageInfo);
	}
	
	public Date getLastModification(String page_id) throws IOException
	{
		PageInfoStruct pi = (PageInfoStruct) pageInfo.getEntry(page_id);
		if(pi != null)
			return pi.getLastModification();
		else
			return null;
	}
	
	public FileStruc getName()
	{
		return pageInfo;
	}
}

