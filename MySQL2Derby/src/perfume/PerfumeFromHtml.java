package perfume;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;

public class PerfumeFromHtml {

	public static final String columns = "Name,Company,Launched,Gender,Perfumer,TopNotes,MiddleNotes,BaseNotes";
	public HashSet<String> CompanySet = new HashSet<String>();
	public HashSet<String> LaunchedSet = new HashSet<String>();
	public HashSet<String> GenderSet = new HashSet<String>();
	public HashSet<String> PerfumerSet = new HashSet<String>();
	public HashSet<String> TopNotesSet = new HashSet<String>();
	public HashSet<String> MiddleNotesSet = new HashSet<String>();
	public HashSet<String> BaseNotesSet = new HashSet<String>();
	public void fromHtml2CSV(String listfilepath,String targetfile){	
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(listfilepath));
			
		    FileOutputStream fos = new FileOutputStream(targetfile); 
		    OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
			BufferedWriter bw = new BufferedWriter(out);//new FileWriter(targetfile));
			
			//write title
			bw.write(columns);
			bw.newLine();
			
			int count = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				count++;
				System.out.println(count+" : "+line);
				//Name, Company, Launched, Gender, Perfumer, Top Notes, Middle Notes, Base Notes
				String Name="", Company="", Launched="", Gender="", 
						Perfumer="", TopNotes="", MiddleNotes="", BaseNotes="";
				
				BufferedReader brhtml = new BufferedReader(
						   new InputStreamReader(
				                      new FileInputStream(line), "ISO-8859-1"));
//				BufferedReader brhtml = new BufferedReader(new FileReader(line));
				String hl = "";
//				if(count!=671)continue;
				while ((hl = brhtml.readLine()) != null) {
//					System.out.println(hl);
					//name
					if(hl.contains("<span><img src=\"/images/small/")){
						int sp = hl.indexOf("align=right>")+12;
						int ep = hl.indexOf("<span style=");
						Name = hl.substring(sp,ep).trim();
					}
					//company
					if(hl.contains("<br>by <a href")){
						int ss =hl.indexOf("<br>by <a href=")+15;
						String ts = hl.substring(ss);
						int sp = ts.indexOf(">")+1;
						int ep = ts.indexOf("</a>");
						Company = ts.substring(sp,ep).trim();
						this.CompanySet.add(Company);
					}
					//Launched,Gender,perfumer
					if(hl.startsWith("<ul><li><b>Launched: </b>")){
						int sp = hl.indexOf("<ul><li><b>Launched: </b><a href=")+33;
						int ep=-1;
						String ts = hl.substring(sp);
						sp = ts.indexOf(">")+1;
						ep = ts.indexOf("</a></li><li><b>Gender: </b>");
						Launched = ts.substring(sp,ep).trim();
						if(Launched.equalsIgnoreCase("Let us know"))Launched="Unknown";
						this.LaunchedSet.add(Launched);
						
						sp = ep+28;
						ep = ts.indexOf("</li><li><b>Availability:</b>");
						Gender = ts.substring(sp,ep).trim();
						this.GenderSet.add(Gender);
						
						sp = hl.indexOf("<b>Perfumer: </b>")+17;
						ts = hl.substring(sp);
						while(ts.contains("<a href=\"http://www.basenotes.net/fragrancedirectory/?perfumer=")){
							sp = ts.indexOf(">")+1;
							ep = ts.indexOf("</a>");
							String tmp = ts.substring(sp,ep).trim();
							Perfumer += tmp + " / ";
							this.PerfumerSet.add(tmp);
							ts = ts.substring(ep+4);
						}
						if(Perfumer.endsWith(" / "))Perfumer = Perfumer.substring(0,Perfumer.length()-3);						
					}
					
					//notes
					if(hl.contains("<b>Top Notes</b>")){
						hl = brhtml.readLine();
						hl = brhtml.readLine();
						String ts = hl.replaceAll("<li>", "")
								.replaceAll("</li>","")
								.replaceAll("<br />", "")
								.replaceAll(",","");
						String [] tss = ts.split("</a>");
						for(String str:tss){
							if(str.contains("<a href=")){
							int sp = str.indexOf(">")+1;
							TopNotes += str.substring(sp).trim()+" / ";
							this.TopNotesSet.add(str.substring(sp).trim());
							}
						}
						if(TopNotes.endsWith(" / "))TopNotes = TopNotes.substring(0,TopNotes.length()-3);
					}
					if(hl.contains("<b>Middle Notes</b>")){
						hl = brhtml.readLine();
						hl = brhtml.readLine();
						String ts = hl.replaceAll("<li>", "")
								.replaceAll("</li>","")
								.replaceAll("<br />", "")
								.replaceAll(",","");
						String [] tss = ts.split("</a>");
						for(String str:tss){
							if(str.contains("<a href=")){
							int sp = str.indexOf(">")+1;
							MiddleNotes += str.substring(sp).trim()+" / ";
							this.MiddleNotesSet.add(str.substring(sp).trim());
							}
						}
						if(MiddleNotes.endsWith(" / "))MiddleNotes = MiddleNotes.substring(0,MiddleNotes.length()-3);
					}
					if(hl.contains("<b>Base Notes</b>")){
						hl = brhtml.readLine();
						hl = brhtml.readLine();
						String ts = hl.replaceAll("<li>", "")
								.replaceAll("</li>","")
								.replaceAll("<br />", "")
								.replaceAll(",","");
						String [] tss = ts.split("</a>");
						for(String str:tss){
							if(str.contains("<a href=")){
							int sp = str.indexOf(">")+1;
							BaseNotes += str.substring(sp).trim()+" / ";
							this.BaseNotesSet.add(str.substring(sp).trim());
							}
						}
						if(BaseNotes.endsWith(" / "))BaseNotes = BaseNotes.substring(0,BaseNotes.length()-3);
					}
					
					
				}
//				System.out.println(count+" : "+ Name+", "+Company + ", "+Launched
//						+", "+Gender+ ", "+Perfumer);
//				System.out.println("TN:" + TopNotes);
//				System.out.println("MN:" + MiddleNotes);
//				System.out.println("BN:" + BaseNotes);
				if(!TopNotes.trim().equals("")){
					String rc = "\"" + Name+"\","
							+Company + ","
							+Launched + ","
							+Gender + ","
							+Perfumer + ","
							+TopNotes + ","
							+MiddleNotes + ","
							+BaseNotes+"\n";
//					rc = removeHtml(rc);	
					bw.write(rc);
					
					System.out.println(rc);
				}
				
				brhtml.close();
			}
			System.out.println(this.CompanySet.size());
			System.out.println(this.LaunchedSet.size());
			System.out.println(this.GenderSet.size());
			System.out.println(this.PerfumerSet.size());
			System.out.println(this.TopNotesSet.size());
			System.out.println(this.MiddleNotesSet.size());
			System.out.println(this.BaseNotesSet.size());
			br.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PerfumeFromHtml().fromHtml2CSV("D:/dbtmp/perfume/HTML/list1.txt", "D:/dbtmp/perfume/perfume.csv");
//		System.out.println("PARFUM D'HERMÈS (1984) BY HERMÈS");
	}

}
