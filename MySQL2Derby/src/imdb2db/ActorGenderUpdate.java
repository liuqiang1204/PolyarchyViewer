package imdb2db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

public class ActorGenderUpdate {
	
	String MySQLDriver = "com.mysql.jdbc.Driver";
	Connection mysqlConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/IMDB?user=root&password=admin";
	
	public void updateActorGenderByFilms(String filmfile){
		try {
			//get film urls from file
			System.out.println("get film urls from file : "+filmfile);
			BufferedReader br = new BufferedReader(new FileReader(filmfile));
			ArrayList<String> filmurls = new ArrayList<String>();
			String line = "";
			while ((line=br.readLine()) != null) {
				filmurls.add(line.split(",")[0]);
			}
			br.close();
			
			//get actor name and url from imdb.com
			System.out.println("get actor name and url from imdb.com");
			HashMap<String,String> actorurls = new HashMap<String,String>(); //name,url
			int ct = 1;
			for(String imdbURL:filmurls){
				System.out.println(ct + " : "+imdbURL);
				ct++;
				URL url = new URL(imdbURL);
				URLConnection uc = url.openConnection();
				uc.setRequestProperty("User-Agent", "Mozilla/4.5");
				uc.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				line="";
				while ((line = in.readLine()) != null){
					if(line.startsWith("        <a    onclick=\"(new Image()).src='/rg/tt-cast/cast")){
						String aname="";
						String aurl = "http://m.imdb.com";
						
						int sp = line.indexOf('>')+1;
						int ep = line.indexOf("</a>");
						if(ep==-1)continue;
						aname = line.substring(sp,ep);
						
						sp = line.indexOf("href=")+6;
						ep = line.indexOf("\"    >");
						aurl += line.substring(sp,ep);						
						actorurls.put(aname, aurl);
						System.out.println("---->"+aname+" - "+aurl);
					}
					//break for the rest useless data
					if(line.equalsIgnoreCase("<h4 class=\"inline\">Release Date:</h4>")){
						break;
					}
				}
			}
			
			//get actor genders from m.imdb.com
			System.out.println("get actor genders from m.imdb.com...");
			HashMap<String,Integer> actorgenders=new HashMap<String,Integer>();
			ct=1;
			for(String actor:actorurls.keySet()){
				String murl = actorurls.get(actor);
				int gender = 1; //1 male 2 female
				
				URL url = new URL(murl);
				URLConnection uc = url.openConnection();
				uc.setRequestProperty("User-Agent", "Mozilla/4.5");
				uc.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				line="";
				while ((line = in.readLine()) != null){
					if(line.contains("<div class=\"detail\">")){
						if(line.contains("Actor")){
							gender = 1;
							break;
						}
						else if(line.contains("Actress")){
							gender = 2;
							break;
						}
					}
				}
				
				actorgenders.put(actor, gender);
				System.out.println(ct+" , " + actor + " , "+gender);
				ct++;
			}
			
			//update database
			System.out.println("update database...");
			Class.forName(MySQLDriver).newInstance();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);

			String sql = "UPDATE actors_hierarchy SET parentid=? WHERE Label = ? ";
			PreparedStatement st = mysqlConn.prepareStatement(sql);
			mysqlConn.setAutoCommit(false);
			for(String ac:actorgenders.keySet()){
				int g = actorgenders.get(ac);
				st.setInt(1, g);
				st.setString(2, ac);
				st.addBatch();
			}
			st.executeBatch();
			mysqlConn.commit();
			mysqlConn.setAutoCommit(true);
			st.close();
			mysqlConn.close();
			System.out.println("Done!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	public void updateActorGenderByActors(String actorfile){
		try {
			//get film urls from file
			System.out.println("get actor urls from file : "+actorfile);
			BufferedReader br = new BufferedReader(new FileReader(actorfile));
			HashMap<String,String> actorurls = new HashMap<String,String>(); //name,url
			
			String line = "";
			while ((line=br.readLine()) != null) {
				if(line.startsWith("---->")){
					String str = line.substring(5,line.length());
					String [] ss=str.split(" - ");
					actorurls.put(ss[0], ss[1]);
				}
				
			}
			br.close();
			
			System.out.println("NUMBER of ACTORS:"+actorurls.size());
			
			//get actor genders from m.imdb.com
			System.out.println("get actor genders from m.imdb.com...");
			HashMap<String,Integer> actorgenders=new HashMap<String,Integer>();
			int ct=1;
			for(String actor:actorurls.keySet()){
				String murl = actorurls.get(actor);
				int gender = 1; //1 male 2 female
				boolean flag = false;
				while(!flag){
					try{
						URL url = new URL(murl);
						URLConnection uc = url.openConnection();
						uc.setRequestProperty("User-Agent", "Mozilla/4.5");
						uc.connect();
						BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
						line="";
						while ((line = in.readLine()) != null){
							if(line.contains("<div class=\"detail\">")){
								if(line.contains("Actor")){
									gender = 1;
									break;
								}
								else if(line.contains("Actress")){
									gender = 2;
									break;
								}
							}
						}
						flag=true;
					}
					catch(IOException e){
						System.out.println(e.getMessage());
						flag=false;
					}
					System.out.println(ct+" , " + actor + " , "+gender+" , "+flag);
				}
				
				actorgenders.put(actor, gender);				
				ct++;
			}
			
			//update database
			System.out.println("update database...");
			Class.forName(MySQLDriver).newInstance();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);

			String sql = "UPDATE actors_hierarchy SET parentid=? WHERE Label = ? ";
			PreparedStatement st = mysqlConn.prepareStatement(sql);
			mysqlConn.setAutoCommit(false);
			for(String ac:actorgenders.keySet()){
				int g = actorgenders.get(ac);
				st.setInt(1, g);
				st.setString(2, ac);
				st.addBatch();
			}
			st.executeBatch();
			mysqlConn.commit();
			mysqlConn.setAutoCommit(true);
			st.close();
			mysqlConn.close();
			System.out.println("Done!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ActorGenderUpdate ag = new ActorGenderUpdate();
//		ag.updateActorGenderByFilms("./database/IMDB_TOP250_URL.txt");
		ag.updateActorGenderByActors("./database/actors.txt");
		
//		URL url = new URL("http://m.imdb.com/name/nm0000209/");
//		URLConnection uc = url.openConnection();
//		uc.setRequestProperty("User-Agent", "Mozilla/4.5");
//		uc.connect();
//		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//		String line="";
//		while ((line = in.readLine()) != null){
//			System.out.println(line);
//		}
	}

}
