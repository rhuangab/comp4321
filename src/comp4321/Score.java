package comp4321;

public class Score implements Comparable<Score>{
	public String page_id;
	public String vsScoreBody;
	public String vsScoreTitle;
	public String pageRank;
	public String overall;
	
	public Score(String id, double vs_body, double vs_title, double pr){
		page_id = id;
		vsScoreBody = String.format("%.2f", vs_body);
		vsScoreTitle = String.format("%.2f", vs_title);
		pageRank = String.format("%.2f", pr);
		double vs = vs_body + 1.5 * vs_title; 
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
