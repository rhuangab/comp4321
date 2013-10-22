package comp4321;

import java.io.IOException;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import jdbm.RecordManager;

public class Title_sample {
	
	private DataStruc title;
	private RecordManager recman;
	
	public Title_sample(RecordManager _recman) throws IOException
	{		
		recman = _recman;
		title = new DataStruc(recman,"title");
	}
	
	public void insertTitle(String page_id, String url) throws ParserException, IOException
	{
		Parser parser = new Parser();
		parser.setURL(url);
		Node node = (Node)parser.extractAllNodesThatMatch(new TagNameFilter ("title")).elementAt(0);
		
		System.out.println(page_id + " " +  url + ":" + node.toPlainTextString());
		title.addEntry(page_id, node.toPlainTextString());
	}
	
	public DataStruc getTitles()
	{
		return title;
	}
}
