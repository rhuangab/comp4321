package comp4321;

public class Score implements Comparable<Score>{
	public String page_id;
	public String vsScore;
	public String pageRank;
	public String overall;
	
	public Score(String id, double vs, double pr){
		page_id = id;
		vsScore = String.format("%.2f", vs);
		pageRank = String.format("%.2f", pr);
		overall = String.format("%.2f", vs * pr);

	}

	@Override
	public int compareTo(Score o) {
		// TODO Auto-generated method stub
		if(Double.parseDouble(this.overall) > Double.parseDouble(o.overall))
			return -1;
		else if(Double.parseDouble(this.overall) == Double.parseDouble(o.overall))
			return 0;
		else
			return 1;
	}


}
