package comp4321;

public class Score{
	public String page_id;
	public double vsScore;
	public double pageRank;
	
	public Score(String id, double vs, double pr){
		page_id = id;
		vsScore = vs;
		pageRank = pr;
	}
}
