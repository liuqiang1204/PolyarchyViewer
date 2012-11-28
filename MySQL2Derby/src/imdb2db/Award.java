package imdb2db;

public class Award {
	public String AwardName = "";
	public int year = 0;
	public String result = "";
	public String category = "";
	public String toFomarttedString(){
		String s=this.AwardName;
		s+="\t"+year;
		s+="\t"+result;
		s+="\t"+category;
		return s;
	}
}
