package comp4321;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
/*
 *  This is the Data Structure class stores the (url, title, pageId, pageSize, last modification date).
 */
public class PageInfoStruct implements Serializable{
	private String m_url;
	private String m_pageId;
	private String m_title;
	private long m_lastModification;
	private long m_size;

	public PageInfoStruct(String url, String pageId) throws ParserException, IOException
	{
		m_url = url;
		m_pageId = pageId;
		//m_size = pageSize;
		initialize();
	}
	
	public PageInfoStruct(String url) throws ParserException, IOException
	{
		m_url = url;
		m_pageId = null;
		m_size = 0;
		initialize();
	}
	
	public void initialize() throws ParserException, IOException
	{
		m_title = Indexer.extractTitle(m_url);
		
		//set up a URL connection to retrieve the page information
		URLConnection hc;
		URL url = new URL(m_url);
		hc = url.openConnection();
		m_lastModification = hc.getLastModified();
		
		//If the page does not specify the last modification data, we use the sending data of the resources that URL referenced
		if (hc.getLastModified() == 0)
			m_lastModification = hc.getDate();
		
		// if the content length is unknown, which return -1, we use the length of
		// the extractedWords.
		if (hc.getContentLengthLong() > 0)
			m_size = hc.getContentLength();

	}
	
	public long getLastModificationLong()
	{
		return m_lastModification;
	}

	public Date getLastModification()
	{
		if( m_lastModification == 0 ) return null;
		Date date = new Date(m_lastModification);
		return date;
	}
	
	public long getPageSize()
	{
		return m_size;
	}
	
	public String getTitle()
	{
		return m_title;
	}
	
	public String getURL()
	{
		return m_url;
	}
	
	public String getPageId()
	{
		return m_pageId;
	}
	
	public boolean setPageId(String page_id)
	{
		if(m_pageId == null || m_pageId == page_id)
		{
			m_pageId = page_id;
			return true;
		}else{
			return false;			
		}
	}
	
	public boolean setPageSize(long page_size)
	{
		if(m_size <= 0)
		{
			m_size = page_size;
			return true;
		}
		return false;
	}

}
