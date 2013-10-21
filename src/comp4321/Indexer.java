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
	private Title_sample title_sample;
	/**add a private field**/
	private Word word;
	private PageInfo pageInfo;
	
	public Indexer(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		//title_sample = new Title_sample(recman);
		/**construct the private field**/
		pageInfo = new PageInfo(recman);
		word = new Word(recman);
	}
	
	public void indexNewPage(String page_id, String url) throws ParserException, IOException
	{
		//title_sample.insertTitle(page_id, url);
		/**the only function you need to call in order to index the word-related information**/
		word.indexWordInfo(page_id, url);
		pageInfo.insertElement(page_id, url,word.getPageSize());
		//word.indexWordInfo("0030","http://ihome.ust.hk/~mxieaa/");
	}
	 
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

}

