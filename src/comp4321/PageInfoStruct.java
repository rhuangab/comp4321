package comp4321;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

public class PageInfoStruct implements Serializable{
	private String m_url;
	private String m_pageId;
	private String m_title;
	private Date m_lastModification;
	private int m_size;

	public PageInfoStruct(String url, String pageId, int pageSize) throws ParserException
	{
	  m_url = url;
		m_pageId = pageId;
		m_size = pageSize;
		initialize();
	}
	
	public void initialize() throws ParserException
	{
		m_title = extractTitle(m_url);
		URLConnection hc;
    try {
    	URL url = new URL(m_url);
	    hc = url.openConnection();
	    long date = hc.getLastModified();
			if(date == 0)
				date = hc.getDate();
			m_lastModification = new Date(date);
			//if the content length is unknown, which return -1, we use the length of the extractedWords.
			if(hc.getContentLength() >= 0)
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
	
	public String getURL()
	{
		return m_url;
	}
	
	public String extractTitle(String url) throws ParserException
	{
		// extract title in url and return it
		Parser parser = new Parser();
		parser.setURL(url);
		//HtmlPage htmlPage = new HtmlPage(parser);
		//System.out.println((htmlPage.getTitle()));
		Node node = (Node)parser.extractAllNodesThatMatch(new TagNameFilter ("title")).elementAt(0);
		return node.toPlainTextString();
	}
}
