package comp4321;

import java.io.Serializable;

public class ResultInfo implements Serializable {
	
	public String query;
	public double time;
	public int numberOfResult;
	
	public ResultInfo(String q, double t, int n)
	{
		query = q;
		time = t;
		this.numberOfResult = n;
	}

}
