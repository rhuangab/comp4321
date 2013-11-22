package favorite;

import java.io.IOException;
import java.util.Vector;

import comp4321.DataStruc;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class ChangeFavorite {
	private RecordManager recman;
	private DataStruc favoriteTable;
	public ChangeFavorite() throws IOException {
		recman = RecordManagerFactory.createRecordManager("/Library/Tomcat/apache-tomcat-6.0.37/webapps/comp4321/MyDatabase");
		//recman = RecordManagerFactory.createRecordManager("MyDatabase");
		favoriteTable = new DataStruc(recman,"favoriteFromUsernameToPageIDList");
  }
	
	public void changeFavoriteStatus(String username,String pageID) throws IOException{
		Vector<String> favortieList = (Vector<String>) favoriteTable.getEntry(username);
		if(favortieList == null)
		{
			favortieList = new Vector<String>();
			favortieList.add(pageID);
			favoriteTable.addEntry(username, favortieList);
		}
		else
		{
			if(!favortieList.contains(pageID))
			{
				favortieList.add(pageID);
				favoriteTable.addEntry(username, favortieList);
			}
			else //if(favortieList.contains(pageID))
			{
				favortieList.remove(pageID);
				if(favortieList.size() == 0)
					favoriteTable.delEntry(username);
				else
					favoriteTable.addEntry(username, favortieList);
			}
		}
	}
	
	public Vector<String> getFavoriteList(String username) throws IOException
	{
		return (Vector<String>) favoriteTable.getEntry(username);
	}
	
	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ChangeFavorite changeFavorite = new ChangeFavorite();
		/*for(int i =0;i< 10;i++)
		{
			changeFavorite.setFavorite("rhuangab", String.format("%04d", i));
		}*/
		Vector<String> favoriteList = changeFavorite.getFavoriteList("rhuangab");
		for(String pageID: favoriteList)
		{
			System.out.println(pageID);
		}
		changeFavorite.finalize();
	}

}