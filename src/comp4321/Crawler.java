package comp4321;
import java.net.URL;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

public class Crawler
{
	private String url;
	public Crawler(String _url)
	{
		url = _url;
	}
	
	public String extractTitle() throws ParserException
	{	
		// extract title in url and return it
		Parser parser = new Parser();
		parser.setURL(url);
		Node node = (Node)parser.extractAllNodesThatMatch(new TagNameFilter ("title")).elementAt(0);

		return node.toPlainTextString();
	}
	
	public String[] extractWords() throws ParserException
	{
		// extract words in url and return them
        StringBean sb = new StringBean ();
        sb.setLinks(false);
        sb.setURL (url);
        //System.out.println(sb.getStrings());
		return sb.getStrings().split("\\W+");
	}
	
	public URL[] extractLinks() throws ParserException
	{
		// extract links in url and return them
	    LinkBean lb = new LinkBean();
	    lb.setURL(url);
	    
	    return lb.getLinks();
	}

	/*
	public static void main(String[] args)
	{
		 Crawler crawler = new Crawler("http://www.cse.ust.hk");
		 try {
	    System.out.println();
    }
    catch (ParserException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}*/
}