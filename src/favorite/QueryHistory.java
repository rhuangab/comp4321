package favorite;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import comp4321.DataStruc;
import comp4321.ResultInfo;

public class QueryHistory {
	private DataStruc queryHistory;
	private RecordManager recman; 
	
	public QueryHistory() throws IOException
	{
	//recman = RecordManagerFactory.createRecordManager("C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\webapps\\COMP4321Beta1\\MyDatabase");
			recman = RecordManagerFactory.createRecordManager("/Library/Tomcat/apache-tomcat-6.0.37/webapps/comp4321/database/MyDatabase");
			//recman = RecordManagerFactory.createRecordManager("MyDatabase");
			queryHistory = new DataStruc(recman,"queryHisory");
	}
	
	public void addQueryHistory(String username,ResultInfo info) throws IOException{
		Vector<ResultInfo> queryList = (Vector<ResultInfo>) queryHistory.getEntry(username);
		if(queryList == null)
		{
			queryList = new Vector<ResultInfo>();
			queryList.add(info);
			queryHistory.addEntry(username, queryList);
		}
		else
		{
			queryList.add(info);
			queryHistory.addEntry(username, queryList);
		}
		finalize();
	}
	
	public Vector<ResultInfo> getQueryList(String username) throws IOException
	{
		Vector<ResultInfo> result = null;
		if(queryHistory.getEntry(username) != null)
			result =  (Vector<ResultInfo>) queryHistory.getEntry(username);
		else 
			result =  new Vector<ResultInfo>();
		Collections.reverse(result);
		return result;
	}
	
	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
