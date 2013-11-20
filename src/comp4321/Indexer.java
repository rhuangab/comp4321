package comp4321;

import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;

public class Indexer {
	
	private RecordManager recman;
	/**add a private field**/
	private Word word;
	private PageInfo pageInfo;
	
	public Indexer(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		/**construct the private field**/
		pageInfo = new PageInfo(recman);
		word = new Word(recman);
	}
	
	/**
	 * index words, insert pageInfoStruct instance and adjust page_id and page_size
	 * 
	 * @param page_id
	 * @param url
	 * @param new_page
	 * @throws ParserException
	 * @throws IOException
	 */
	public void indexNewPage(String page_id, String url, PageInfoStruct new_page) throws ParserException, IOException
	{
		word.indexWordInfo(page_id, url);
		pageInfo.insertElement(page_id, new_page, word.getPageSize());
	}
	
	/**
	 * extract words from a given url
	 * @param url
	 * @return
	 * @throws ParserException
	 */
	public static Vector<String> extractWords(String url) throws ParserException
	{
		StringBean sb;
		Vector<String> v_str = new Vector<String>();
        boolean links = false;
        sb = new StringBean ();
        sb.setLinks (links);
        sb.setURL (url);
        /**
         * change here! change the way of spiting a long string
         */
        String temp = sb.getStrings();
        String[] words = temp.split("\\W+");

		for(int i = 0; i < words.length; i++)
			v_str.add(words[i].toLowerCase());
     
        return (v_str);
	}
	
	/**
	 * extract links from a given url
	 * @param url
	 * @return
	 * @throws ParserException
	 */
	public static Vector<String> extractLinks(String url) throws ParserException
	{
        Vector<String> v_link = new Vector<String>();
        LinkBean lb = new LinkBean();
        lb.setURL(url);
        URL[] URL_array = lb.getLinks();
        for(int i=0; i<URL_array.length; i++){
        	v_link.addElement(URL_array[i].toString());
        }
		return v_link;
	}
	
	/** extract the content in the title tag from the html page. **/ 
	public static String extractTitle(String url) throws ParserException
	{
		// extract title in url and return it
		Parser parser = new Parser();
		parser.setURL(url);
		//HtmlPage htmlPage = new HtmlPage(parser);
		//System.out.println((htmlPage.getTitle()));
		Node node = (Node)parser.extractAllNodesThatMatch(new TagNameFilter ("title")).elementAt(0);
		if(node != null)
			return node.toPlainTextString();
		else
			return null;
	}

}

