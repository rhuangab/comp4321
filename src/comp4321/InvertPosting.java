package comp4321;

import java.io.Serializable;

public class InvertPosting implements Serializable {
	public String word_id;
	public int freq;

	InvertPosting(String id, int freq) {
		this.word_id = id;
		this.freq = freq;
	}
}