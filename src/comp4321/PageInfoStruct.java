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
	private Date m_lastModification;
	private int m_size;

	public PageInfoStruct(String url, String pageId, int pageSize) throws ParserException, IOException
	{
	  m_url = url;
		m_pageId = pageId;
		m_size = pageSize;
		initialize();
	}
	
	public void initialize() throws ParserException, IOException
 {
		m_title = extractTitle(m_url);
		
		//set up a URL connection to retrieve the page information
		URLConnection hc;
		URL url = new URL(m_url);
		hc = url.openConnection();
		long date = hc.getLastModified();
		//If the page does not specify the last modification data, we use the sending data of the resources that URL referenced
		if (date == 0)
			date = hc.getDate();
		m_lastModification = new Date(date);
		// if the content length is unknown, which return -1, we use the length of
		// the extractedWords.
		if (hc.getContentLength() >= 0)
			m_size = hc.getContentLength();

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
	
	/** extract the content in the title tag from the html page. **/ 
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
