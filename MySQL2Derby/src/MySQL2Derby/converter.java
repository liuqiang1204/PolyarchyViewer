package MySQL2Derby;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class converter {

	/**
	 * @param args
	 */
	String MySQLDriver = "com.mysql.jdbc.Driver";
	String DerbyDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	Connection mysqlConn;
	Connection derbyConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/honours?user=root&password=admin";
	String derbyUrl = "jdbc:derby:database/honours;create=true;user=root;password=admin";	
	
//	String d_path;
	public converter(){
		try {
			Class.forName(MySQLDriver).newInstance();
			Class.forName(DerbyDriver).newInstance();
			this.getDBConnStr();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);
			derbyConn = DriverManager.getConnection(this.derbyUrl);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void getDBConnStr() throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please input/select the Mysql connString:");
		System.out.println("1 - " + this.mysqlUrl);
		System.out.println("2 - " + this.mysqlUrl.replaceAll("honours", "sample"));
		System.out.println("3 - " + this.mysqlUrl.replaceAll("honours", "movie_small"));
		System.out.println("4 - " + this.mysqlUrl.replaceAll("honours", "publication_small"));
		System.out.println("5 - " + this.mysqlUrl.replaceAll("honours", "IMDB"));
		System.out.println("6 - " + this.mysqlUrl.replaceAll("honours", "CarDB"));
		System.out.println("7 - " + this.mysqlUrl.replaceAll("honours", "research_data"));
		System.out.println("8 - " + this.mysqlUrl.replaceAll("honours", "perfume"));
		
		String is = br.readLine().trim();
		if(is.equals("2")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "sample");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "sample");
		}
		else if(is.equals("3")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "movie_small");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "movie_small");
		}
		else if(is.equals("4")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "publication_small");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "publication_small");
		}
		else if(is.equals("5")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "IMDB");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "IMDB");
		}
		else if(is.equals("6")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "CarDB");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "CarDB");
		}
		else if(is.equals("7")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "research_data");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "research_data");
		}
		else if(is.equals("8")){
			this.mysqlUrl = this.mysqlUrl.replaceAll("honours", "perfume");
			this.derbyUrl = this.derbyUrl.replaceAll("honours", "perfume");
		}
		else if(is.equals("1")||is.equals("")){
			
		}
		else
		{
			this.mysqlUrl = is;
			System.out.println("Please input the Derby connString:");
			System.out.println("e.g. "+ this.derbyUrl);
			is = br.readLine().trim();
			if(!is.equals("")) this.derbyUrl = is;
		}
		
		
		
		System.out.println("From : " + mysqlUrl);
		System.out.println("To : " + derbyUrl);
	}
	
	String mySQLCreateTableStr2Derby(String msql)
	{
		String t = msql;
		t = t.replaceAll("COMMENT.*,", ",");
		t = t.replaceAll("COMMENT=.*$","");
		t = t.replaceAll("ENGINE=.*$","");
		t = t.replaceAll("`", "");
		t = t.replaceAll(" int[(]"," Decimal(");
		t = t.replaceAll(" text ", " Varchar(3000) ");
		t = t.replaceAll(" text,", " Varchar(3000),");
		t = t.replaceAll("TABLE connection ","TABLE d_connection");
		t = t.replaceAll(" tinyint[(]1[)]", " Integer");
		
//index???here remove it
//		if(t.contains(",\n  KEY")){
//			int keyid = t.indexOf(",\n  KEY");
//			String idxstr = t.substring(keyid+7,t.length()-1);
//			System.out.println("---"+idxstr);
//		}
		t = t.replaceAll("  KEY .*[)],\n", "");
		t = t.replaceAll("  KEY .*[)]\n", "");
		t = t.replaceAll("[)],\n[)]",")\n)");
		t = t.replaceAll("AUTO_INCREMENT","");
		t = t.replaceAll("set[(]'y','n'[)]","Varchar(10)");
		t = t.replaceAll("  current ","  \"current\" ");
		//only for honours and publication_small db
		t = t.replaceAll("Decimal[(]11[)] NOT NULL DEFAULT '0',", "Decimal(11) NOT NULL DEFAULT 0,");
		return t;
		
	}
	
	void transfer(){	
		ArrayList<String> tables = new ArrayList<String>();	
		try {
			Statement ms = mysqlConn.createStatement();
			Statement ds = derbyConn.createStatement();
			//get all table names
			String sql = "show tables";			
			ResultSet rs = ms.executeQuery(sql);			
					
			while(rs.next()){
				tables.add(rs.getString(1));
//				System.out.println(rs.getString(1));
			}
			
			//create each table at derby database
			for(String tn:tables){
				if(tn.equalsIgnoreCase("TmpTable"))continue;
				sql = "show create table " + tn;
				rs = ms.executeQuery(sql);
				rs.next();
				String cstr = rs.getString(2);
				cstr = this.mySQLCreateTableStr2Derby(cstr);
				System.out.println(cstr+";");
				ds.execute(cstr);
			}
			
			//insert data
			for(String tn:tables){
				//derby preserve "connection" as keyword
				if(tn.equalsIgnoreCase("connection"))
					sql = "select * from d_" + tn;
				else
					sql = "select * from " + tn;
				rs = ms.executeQuery(sql);
				if(rs.next()){
					int colcount = rs.getMetaData().getColumnCount();
					String insertSql = "insert into " + tn + " values (";
					for(int i=0;i<colcount;i++) insertSql += "?,";
					insertSql = insertSql.substring(0,insertSql.length()-1) + ")";
					System.out.println(insertSql);
					
					derbyConn.setAutoCommit(false);
					PreparedStatement dds = derbyConn.prepareStatement(insertSql);					
					do{						
						for(int i=0;i<colcount;i++){
							dds.setObject(i+1, rs.getObject(i+1));			
							
						}
						dds.addBatch();
					}while(rs.next());				
					
					dds.executeBatch();
					derbyConn.commit();
					derbyConn.setAutoCommit(true);
					rs.last();
					System.out.println("insert records: " + rs.getRow());
				}				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Total transferred " + tables.size() + " tables. \nAll done!");
	}
	
	void closeConn()
	{
		try {
			mysqlConn.close();
			derbyConn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		converter c = new converter();
		c.transfer();
		c.closeConn();
	}

}
