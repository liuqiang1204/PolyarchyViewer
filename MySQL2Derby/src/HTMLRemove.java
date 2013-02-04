import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HTMLRemove {
	String MySQLDriver = "com.mysql.jdbc.Driver";
	String DerbyDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	Connection mysqlConn;
	Connection derbyConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/imdb?user=root&password=admin";
	String derbyUrl = "jdbc:derby:database/imdb;user=root;password=admin";
	public HTMLRemove(){
		try {
			Class.forName(MySQLDriver).newInstance();
			Class.forName(DerbyDriver).newInstance();
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
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, Exception {
//		BufferedReader br;
//		HashMap<String,String> codes = new HashMap<String,String>();
//		try {
//			br = new BufferedReader(new FileReader("./database/htmlencoding.txt"));
//			String line = br.readLine();
//			while (line != null) {
//				String [] ss = new String[2];
//				System.out.println(line);
//				ss = line.split("\t");
//				codes.put(ss[1], ss[0]);
//				System.out.println(ss[1]+"   " +ss[0]);
//				line = br.readLine();
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//recover data...
		HTMLRemove h = new HTMLRemove();
		Statement ms = h.mysqlConn.createStatement();
		Statement ds = h.derbyConn.createStatement();
		String sql = "SELECT idhierarchy, Label FROM actors_hierarchy";
		ResultSet rs = ds.executeQuery(sql);
		while(rs.next()){
			sql = "update actors_hierarchy set Label = '"+ rs.getString(2)+"' where idhierarchy = "+rs.getString(1)+";";
//			System.out.println(rs.getString(1)+"\t"+rs.getString(2));
			System.out.println(sql);
		}
		
	}

}

