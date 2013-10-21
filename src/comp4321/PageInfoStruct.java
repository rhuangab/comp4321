package comp4321;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.htmlparser.util.ParserException;

public class PageInfoStruct implements Serializable{
	private String m_url;
	private String m_pageId;
	private String m_title;
	private Date m_lastModification;
	private int m_size;
	
	public PageInfoStruct(String url, String pageId) throws ParserException
	{
	  m_url = url;
		m_pageId = pageId;
		initialize();
	}
	
	public void initialize() throws ParserException
	{
		Crawler crawler = new Crawler(m_url);
		m_title = crawler.extractTitle();
		URLConnection hc;
    try {
    	URL url = new URL(m_url);
	    hc = url.openConnection();
	    long date = hc.getLastModified();
			if(date == 0)
				date = hc.getDate();
			m_lastModification = new Date(date);
			m_size = hc.getContentLength();
    }
    catch (IOException e) {
	    // TODO Auto-generated catch block
    	System.out.println("Fail to load the url.");
	    e.printStackTrace();
    }
	}
	
	public Date getLastModification()
	{
		return m_lastModification;
	}
	
	public int getPageSize()
	{
		return m_size;
	}
	
	public String getTitle()
	{
		return m_title;
	}
}
