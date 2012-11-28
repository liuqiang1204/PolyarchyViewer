package imdb2db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Film {
	public String imdbURL = ""; //
	public String filmName = ""; //-
	public String languages = ""; //-
	public String plot = ""; //-
	
	public String country = ""; //
	public int year = 0; //
	public int rank = -1; //
	public int filmID = -1; //
	public String director = ""; //
	public ArrayList<String> actors = new ArrayList<String>(); //
	public ArrayList<String> genres = new ArrayList<String>(); //
	public ArrayList<Award> awards = new ArrayList<Award>();
	public String toFormattedString(){
		String s = "";
		s += "Rank:"+this.rank + "\n";
		s += "URL:"+this.imdbURL + "\n";		
		s += "Film name:"+this.filmName + "\n";
		
		s += "Genres:";
		for(String str:genres)s+=str+",";
		s = s.substring(0, s.length()-1)+"\n";
		
		s += "Year:"+this.year + "\n";
		s += "Languages:"+this.languages + "\n";
		s += "Country:" + this.country + "\n";		
		s += "Plot:"+this.plot+"\n";
		s += "Director:"+this.director;
		
		s += "Actors:";
		for(String str:actors)s+=str+",";
		s = s.substring(0, s.length()-1)+"\n";
		
		s += "Awards(Name,Year,Result,Category):\n";
		for(Award a:awards)s+=a.toFomarttedString()+"\n";		
		
		return s;
		
	}
	public void getInfoFromIMDB(){
		if(imdbURL.equals(""))return;
		try {
			URL url = new URL(imdbURL);
			URLConnection uc = url.openConnection();
			uc.setRequestProperty("User-Agent", "Mozilla/4.5");
			uc.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String line="";
			while ((line = in.readLine()) != null){
				//get film name & year
				if(line.trim().equalsIgnoreCase("<h1 class=\"header\" itemprop=\"name\">")){
					line = in.readLine();
					this.filmName = line.trim();					
					continue;
				}			
				//get year
				if(line.startsWith("(<a href=\"/year/")){
					int sp = line.indexOf("\">")+2;
					int ep = line.indexOf("</a>");
					this.year = Integer.valueOf(line.substring(sp, ep));
					continue;
				}
				//get genres
				if(line.equals("<h4 class=\"inline\">Genres:</h4>")){
					do{
					line = in.readLine();
					if(line.startsWith("    >")){
							int sp = line.indexOf(">")+1;
							int ep = line.indexOf("</a>");
							this.genres.add(line.substring(sp,ep));
					}
					}while(!line.startsWith("</div>"));
										
					continue;
				}
				//get director
				if(line.contains("itemprop=\"director\"")){
					line = in.readLine();
					int sp = line.indexOf(">")+1;
					int ep = line.indexOf("</a>");
					this.director = line.substring(sp,ep);
					continue;
				}				
				
				//get actors
				if(line.startsWith("        <a    onclick=\"(new Image()).src='/rg/tt-cast/cast")){
					int sp = line.indexOf('>')+1;
					int ep = line.indexOf("</a>");
					if(ep!=-1){
						this.actors.add(line.substring(sp,ep));
					}
					continue;
				}
				
				//get country
				if(line.contains("href=\"/country/")){
					int sp = line.indexOf(">")+1;
					int ep = line.indexOf("</a>");
					this.country = line.substring(sp,ep);
					continue;
				}
				
				//get languages
				if(line.contains("itemprop=\"inLanguage\"")){
					line = in.readLine();
					int sp = line.indexOf(">")+1;
					int ep = line.indexOf("</a>");
					if(this.languages.equals(""))this.languages = line.substring(sp,ep);
					else this.languages += ","+line.substring(sp,ep);
					continue;
				}
				
				//get plot
				if(line.equalsIgnoreCase("<p><p itemprop=\"description\">")){
					line = in.readLine();
					this.plot = line;
					continue;
				}
				
				//break for the rest useless data
				if(line.equalsIgnoreCase("<h4 class=\"inline\">Release Date:</h4>")){
					break;
				}
            }
			in.close();
			
			//get awards
			URL url1 = new URL(imdbURL+"awards");
			URLConnection uc1 = url1.openConnection();
			uc1.setRequestProperty("User-Agent", "Mozilla/4.5");
			uc1.connect();
			
			in = new BufferedReader(new InputStreamReader(uc1.getInputStream()));
			line=in.readLine();
			//find the table
			while(line!=null&&!line.startsWith("  <table style"))line = in.readLine();
			//get data from this <table>
			int c_year=0;
			String c_awardname = "";
			String c_result = "";
			while((line=in.readLine())!=null){
				if(line.equals("  </table>"))break;
				//get year
				if(line.startsWith("          <a href=\"/Sections/Awards")){
					int sp = line.indexOf(">")+1;
					int ep = line.indexOf("</a>");
					c_year = Integer.valueOf(line.substring(sp,ep).trim());
					continue;
				}
				//get name and result
				if(line.startsWith("        <td rowspan=")){
					if(line.contains("<b>")){
						int sp = line.indexOf("<b>")+3;
						int ep = line.indexOf("</b>");
						c_result = line.substring(sp,ep);
					}
					else if(line.contains("/td>")){
						int sp = line.indexOf(">")+1;
						int ep = line.indexOf("</td>");
						c_awardname = line.substring(sp,ep);
					}
					continue;
				}
				//get category
				if(line.equals("        <td valign=\"top\">")){
					line = in.readLine();
					Award a = new Award();
					a.AwardName = c_awardname;
					a.result = c_result;
					a.year = c_year;
					a.category = line.trim().replaceAll("<br>", "");
					if(a.category.equals(""))a.category="----";
					this.awards.add(a);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
