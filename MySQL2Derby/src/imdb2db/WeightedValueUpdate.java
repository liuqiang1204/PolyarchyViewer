package imdb2db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WeightedValueUpdate {

	String MySQLDriver = "com.mysql.jdbc.Driver";
	Connection mysqlConn;
	String mysqlUrl = "jdbc:mysql://localhost:3306/IMDB?user=root&password=admin";
	
	public void update(){
		try {
			Class.forName(MySQLDriver).newInstance();
			mysqlConn = DriverManager.getConnection(this.mysqlUrl);			
			Statement st = mysqlConn.createStatement();
			
			
			String sql = "SELECT t_name FROM films_linking";			
			ResultSet rs = st.executeQuery(sql);			
			ArrayList<String> tnames = new ArrayList<String>();
			while(rs.next()){
				String tn = rs.getString(1);
				tnames.add(tn);
				System.out.println(tn);
			}
			rs.close();
			
			sql = "SELECT filmID FROM films";
			rs = st.executeQuery(sql);
			ArrayList<String> filmIDs = new ArrayList<String>();
			while(rs.next()){
				String id = rs.getString(1);
				filmIDs.add(id);
				System.out.println(id);
			}
			rs.close();
			
			for(String tn:tnames){
				for(String fid:filmIDs){
					sql = "SELECT count(*) FROM " +	tn + " where filmid=" + fid;
					rs=st.executeQuery(sql);
					rs.next();
					int ct = rs.getInt(1);
					double value=0.0;
					if(ct!=0) value = 1.00000000/ct;					
					System.out.println(tn + " - " + fid + " - " +value);
					
					sql = "UPDATE " + tn +
							" SET weighted_sum=" + value +
							" WHERE filmid=" + fid;
					st.execute(sql);
				}
			}
			mysqlConn.close();
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WeightedValueUpdate wu = new WeightedValueUpdate();
		wu.update();

	}

}
