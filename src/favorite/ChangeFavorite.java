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
		recman = RecordManagerFactory.createRecordManager("C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\webapps\\COMP4321Beta1\\MyDatabase");
		//recman = RecordManagerFactory.createRecordManager("/Library/Tomcat/apache-tomcat-6.0.37/webapps/comp4321/MyDatabase");
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
		if(favoriteTable.getEntry(username) != null)
			return (Vector<String>) favoriteTable.getEntry(username);
		else 
			return new Vector<String>();
	}
	
	public boolean isFavorite(String username, String page_id) throws IOException
	{
		Vector<String> favoriteList = getFavoriteList(username);

		return favoriteList.contains(page_id);
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
