package perfume;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
	
	String MySQLDriver = "com.mysql.jdbc.Driver";
	Connection mysqlConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/perfume?user=root&password=admin";
	
	/**
	 * insert data after perfume.sql excuted.
	 */
	public void insertData(){
		try {
			Class.forName(MySQLDriver).newInstance();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);			
			Statement st = mysqlConn.createStatement();
			int count =0;
			String sql = "select * from perfumes";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				int perfumeID = rs.getInt("perfumeID");
				String perfumer = rs.getString("perfumer");
				String topNotes = rs.getString("TopNotes");
				String middleNotes = rs.getString("MiddleNotes");
				String baseNotes = rs.getString("BaseNotes");
				
//				if(!perfumer.trim().equals("")){
//					//insert perfumers
//					String [] pers = perfumer.split("/");
//					for(String str:pers){
//						Statement st1 = mysqlConn.createStatement();
//						String sql1 = "Select * from perfumer_hierarchy where Label = '" + str.trim()+"'";
//						ResultSet rs1 = st1.executeQuery(sql1);
//						int pid = -1;
//						if(rs1.next()){
//							pid = rs1.getInt("idhierarchy");
//						}
//						else{
//							int parid = PerfumeFromHtml.classifyName(str.trim());
//							sql1 = "insert into perfumer_hierarchy (parentid,Level,Label,isLeaf) values (" +
//									parid + ",2,'" + str.trim() + "',true)";
//							st1.execute(sql1);
//							sql1 = "Select * from perfumer_hierarchy where Label = '" + str.trim()+"'";
//							rs1 = st1.executeQuery(sql1);
//							rs1.next();
//							pid = rs1.getInt("idhierarchy");
//						}
//						sql1 = "insert into perfumes_perfumer(perfumeID, perfumerID, weighted_sum) values (" +
//								perfumeID + "," + pid + ",1.00)";
//						st1.execute(sql1);
//						st1.close();
//					}
//				}
				
				//topnotes
				String [] tnotes = topNotes.split("/");
				for(String tn:tnotes){
					Statement st1 = mysqlConn.createStatement();
					String sql1 = "Select * from topnotes_hierarchy where Label = '" + tn.trim()+"' and isLeaf=true";
					ResultSet rs1 = st1.executeQuery(sql1);
					int tnid = -1;
					if(rs1.next()) tnid = rs1.getInt("idhierarchy");
					
					if(tnid==-1)System.out.println((++count) + " TOP - "+perfumeID+" nid:" + tnid + "  name:"+tn.trim());
					else{
						sql1 = "insert into perfumes_topnotes (perfumeID, TopNotesID, Weighted_sum) values (" +
								perfumeID + "," + tnid + ",1.00)";
						st1.execute(sql1);
					}
					st1.close();
				}
				//middlenotes
				String [] mnotes = middleNotes.split("/");
				if(!middleNotes.equals(""))
				for(String mn:mnotes){
					Statement st1 = mysqlConn.createStatement();
					String sql1 = "Select * from middlenotes_hierarchy where Label = '" + mn.trim()+"' and isLeaf=true";
					ResultSet rs1 = st1.executeQuery(sql1);
					int mnid = -1;
					if(rs1.next()) mnid = rs1.getInt("idhierarchy");
					if(mnid==-1)System.out.println((++count) + " MID - "+perfumeID+" nid:" + mnid + "  name:"+mn.trim());
					else{
						sql1 = "insert into perfumes_middlenotes (perfumeID, MiddleNotesID, Weighted_sum) values (" +
								perfumeID + "," + mnid + ",1.00)";
						st1.execute(sql1);
					}
					st1.close();
				}
				
				//basenotes
				String [] bnotes = baseNotes.split("/");
				if(!baseNotes.equals(""))
				for(String bn:bnotes){
					Statement st1 = mysqlConn.createStatement();
					String sql1 = "Select * from basenotes_hierarchy where Label = '" + bn.trim()+"' and isLeaf=true";
					ResultSet rs1 = st1.executeQuery(sql1);
					int bnid = -1;
					if(rs1.next()) bnid = rs1.getInt("idhierarchy");
					if(bnid==-1)System.out.println((++count) + " base - "+perfumeID+" nid:" + bnid + "  name:"+bn.trim());
					else{
						sql1 = "insert into perfumes_basenotes (perfumeID, BaseNotesID, Weighted_sum) values (" +
								perfumeID + "," + bnid + ",1.00)";
						st1.execute(sql1);
					}
					st1.close();
				}
			}
			rs.close();
			mysqlConn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public static int classifyName(String name){
		int x = -1;
		String [] ns = name.split(" ");
		String surname = ns[ns.length-1];
		char c = surname.charAt(0);
		if((c>='a'&&c<='f')||(c>='A'&&c<='F'))x = 1;
		else if((c>='g'&&c<='l')||(c>='G'&&c<='L'))x = 2;
		else if((c>='m'&&c<='r')||(c>='M'&&c<='R'))x = 3;
		else if((c>='s'&&c<='x')||(c>='S'&&c<='X'))x = 4;
		else if((c>='y'&&c<='z')||(c>='Y'&&c<='Z'))x = 5;
		return x;
	}
	
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
//				System.out.println(count+" : "+line);
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
//			System.out.println(this.CompanySet.size());
//			System.out.println(this.LaunchedSet.size());
//			System.out.println(this.GenderSet.size());
//			System.out.println(this.PerfumerSet.size());
//			System.out.println(this.TopNotesSet.size());
//			System.out.println(this.MiddleNotesSet.size());
//			System.out.println(this.BaseNotesSet.size());
			br.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		new PerfumeFromHtml().fromHtml2CSV("D:/dbtmp/perfume/HTML/list1.txt", "D:/dbtmp/perfume/perfume.csv");
//		System.out.println("PARFUM D'HERMÈS (1984) BY HERMÈS");
//		String aaa = "";
//		String [] b = aaa.split("/");
//		System.out.println(b.length);
//		System.out.println(classifyName("ab z"));
		new PerfumeFromHtml().insertData();
	}

}
