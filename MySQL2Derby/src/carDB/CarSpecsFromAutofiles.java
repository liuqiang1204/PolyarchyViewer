package carDB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CarSpecsFromAutofiles {

//	public static final String columns = 
//			"Make," +
//			"Year," +
//			"Name of model," +
//			"Made in," +
//			"Body type," +
//			"Weight," +
//			"Max power," +
//			"Max torque," +
//			"cylinders";
	
	public static final String columns = 
			"Make," +
			"Year," +
			"Name of model," +
			"Made in," +
			"Type," +
			"Door," +
			"Seat," +
			"Weight," +
			"Max power," +
			"Max torque," +
			"Number Cylinders," + 
			"Formation";
	
	public static void fromHtml2CSV(String listfilepath,String targetfile){
		
		int total = 15332;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(listfilepath));
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetfile));
			
			//write title
			bw.write(columns);
			bw.newLine();
			
			//write records
			int count =0;
			String line;
			String 	make="",
					year="",
					model="",
					madein="",
					body="",
					weight="",
					power="",
					torque="",
					cylinders="";
			
			//read from html
			while ((line = br.readLine()) != null) {
				count++;
				
				//----Get Make by the folder name (!!!When change folder, change it!!!);
				make = line.split("/")[2].trim();			
				
				//read the html file
				BufferedReader brhtml = new BufferedReader(new FileReader(line));
				String hl = "";
				
				while ((hl = brhtml.readLine()) != null) {
					//year, model
					if(hl.startsWith("<h1 class=\"model_title\">")){
						String s = hl.replaceFirst("<h1 class=\"model_title\">", "");
						year = s.substring(0, 4);
						try{
							Integer.parseInt(year);
						}
						catch(NumberFormatException e)
						{
							year = "n/a";
						}
						
						if(!year.equals("n/a")) s = s.substring(5+make.length());
						else s = s.substring(make.length());
						
						model = s.replaceFirst(" Technical Specifications</h1></h1>", "").trim();
					}
					
					//made in
					if(hl.startsWith(" <table cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"line-height: 100%;\">")){
						int sp = hl.indexOf(".html\">")+7;
						int ep = hl.indexOf("</a></span></td></tr></table></td>");
						madein = hl.substring(sp,ep).replaceFirst("Made in", "").trim();
						
						if(madein.equalsIgnoreCase("Korea, Republic of")) madein = "Republic of Korea";
						
						if(make.equalsIgnoreCase("Aston Martin")								
								||make.equalsIgnoreCase("Bentley"))
							madein="British";
						else if(make.equalsIgnoreCase("Rolls-Royce")
								||make.equalsIgnoreCase("Lotus")
								||make.equalsIgnoreCase("Land Rover")
								||make.equalsIgnoreCase("Jaguar"))
							madein="England";						
						
					}
					
					//body
					if(hl.startsWith("<tr><td><u>Body Type:")){
						body = hl.replaceFirst("<tr><td><u>Body Type:</u></td><td><span id=\"body_str\">", "")
								.replaceFirst("</span></td></tr>", "").trim();
					}
					
					//weight
					if(hl.startsWith("<tr><td><u>Weight:</u></td><td>")){
						weight = hl.replaceFirst("<tr><td><u>Weight:</u></td><td>", "")
								.replaceFirst("</td></tr>", "").trim();
					}
					//power
					if(hl.startsWith("<tr><td><u>Max Power:</u></td><td>")){
						power = hl.replaceFirst("<tr><td><u>Max Power:</u></td><td>", "")
								.replaceFirst("</td></tr>", "").trim();
					}
					//torque
					if(hl.startsWith("<tr><td><u>Max Torque:</u></td><td>")){
						torque = hl.replaceFirst("<tr><td><u>Max Torque:</u></td><td>", "")
								.replaceFirst("</td></tr>", "").trim();
					}
					//Cylinders
					if(hl.startsWith("<tr><td><u>Cylinders:</u></td><td>")){
						cylinders = hl.replaceFirst("<tr><td><u>Cylinders:</u></td><td>", "")
								.replaceFirst("</td></tr>", "").trim();
						
						//find all required fields,break reading html
						break;
					}
//					System.out.println(hl);
				}				
				
				System.out.println(count+"/"+total+" : "+line);
//				System.out.println("Make:\t"+make);
//				System.out.println("Year:\t"+year);
//				System.out.println("Model:\t"+model);
//				System.out.println("MadeIn:\t"+madein);
//				System.out.println("Body:\t"+ body);
//				System.out.println("Weight:\t"+weight);
//				System.out.println("Power:\t"+power);
//				System.out.println("Torque:\t"+torque);
//				System.out.println("Cylinders:\t"+cylinders);
				
				
				//split body, power, torque,cylinders
				String type= "",door= "",seat= "",ncy = "",formation= "";
				int idoor = body.indexOf("doors")-3;
				int iseat = body.indexOf("seats")-3;
				if(idoor>=0) door = body.substring(idoor, idoor+2).trim();
				if(iseat>=0) seat = body.substring(iseat, iseat+2).trim();
				
				if(idoor>=0)type = body.substring(0, idoor).trim();
				else if(iseat>=0)type = body.substring(0, iseat).trim();
				else type = body;
				
				int ipower = power.indexOf("(");
				if(ipower>=0)power=power.substring(0,ipower).trim();
				
				int itorque = torque.indexOf("@");
				if(itorque>=0)torque=torque.substring(0,itorque).trim();
				
				if(cylinders.equalsIgnoreCase("n/a")){
					ncy = cylinders;
					formation = cylinders;
				}
				else
				{
					int i= cylinders.indexOf(" ");
					if(i==-1)ncy = cylinders;
					else{
						ncy = cylinders.substring(0,i).trim();
						formation = cylinders.substring(i).trim();
					}
				}
				
				
				//write to file
//				String rs = make+","
//						+year+","
//						+model+","
//						+madein+","
//						+body+","
//						+weight+","
//						+power+","
//						+torque+","
//						+cylinders;
				
				String rs = make+","
						+year+","
						+model+","
						+madein+","
						+type+","
						+door+","
						+seat+","
						+weight+","
						+power+","
						+torque+","
						+ncy+","
						+formation;
				
				bw.write(rs);
				bw.newLine();
				brhtml.close();
				
				System.out.println(rs);
//				break;
			}
			
			System.out.println("\n\nAll done!");
			bw.flush();
			br.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public static void printfile(String fp){
		try {
			BufferedReader br = new BufferedReader(new FileReader(fp));
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CarSpecsFromAutofiles.fromHtml2CSV("f:/carfiles/files.txt","f:/carfiles/carDB.csv");
		
//		CarSpecsFromAutofiles.printfile("F:/carfiles/Hyundai/hyundai-getz-1-4_19085.html");
	}

}
