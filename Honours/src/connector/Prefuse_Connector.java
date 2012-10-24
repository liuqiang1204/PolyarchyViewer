//HAS TO BE COMMENTED OUT BECAUSE THE JAR FILE HAS BEEN REMOVED
//package connector;
//
//import java.sql.SQLException;
//
//import prefuse.data.io.DataIOException;
//import prefuse.data.io.sql.ConnectionFactory;
//import prefuse.data.io.sql.DatabaseDataSource;
//import prefuse.data.Table;
//	
///**
// * This class is used to connect to a database
// * All of the information required to access the databases located in this file
// * Any query to the database needs to use an object from this class
// * @author Anthony Scata
// * @version 1.1
// */
//public class Prefuse_Connector {
//	
//	//Let's note our database connection properties the JDBC driver we'll be using
//	private final String driver = "com.mysql.jdbc.Driver";
//
//	//The Internet address of the database server
//	private final String host = "localhost";
//
//	//The name of the database to connect to
//	private final String database = "honours";
//	
//	//The user name required to login to the database
//	private final String user = "java";
//	
//	//The password PUsc98zxxmtMSyJF
//	private final String password = "prefuse";
//	
//	//Create a database connection object
//	protected static DatabaseDataSource datasrc = null;
//	
//	/**
//	 * The constructor foe the connector class
//	 * This constructor takes an integer as an option
//	 * To be extended later
//	 * @param option
//	 */
//	public Prefuse_Connector(int option) {
//		if(option == 1) {
//			boolean sucess = connect();
//			if(sucess) {
//				System.out.println("Sucessfull connected to the database");
//			}
//		} 
//	}
//	
//	/**
//	 * The default constructor
//	 * Connects to the database
//	 */
//	public Prefuse_Connector() {
//		boolean sucess = connect();
//		if(sucess) {
//			System.out.println("Sucessfull connected to the database");
//		}
//	}
//	
//	/**
//	 * This method connects the object to the database
//	 * Required before any queries can be executed on the database
//	 * @return
//	 */
//	public boolean connect() {
//		
//		// now lets build the connection URL
//		String url = "jdbc:mysql://"+host+"/"+database;
//		
//		// try and set the database connector or catch the error
//		try {
//			setDatasrc(ConnectionFactory.getDatabaseConnection(driver, url, user, password));
//		} catch (SQLException e) {
//		    // There was an error connecting to the database
//			System.out.println("An error has occured connecting to the database\nPlease check you have a connection");
//		    e.printStackTrace();
//		    return false;
//		} catch (ClassNotFoundException e) {
//		    // The database driver class was not found
//			System.out.println("An error has occured connecting to the database because of a database frover error\nPlease check the database drivers");
//		    e.printStackTrace();
//		    return false;
//		}
//		return true;
//	}
//	
//	/**
//	 * Disconnect from the database
//	 * To be extended later
//	 * @return
//	 */
//	public boolean disconnect() {
//		setDatasrc(null);
//		return true;
//	}
//	
//	/*
//	 * Get the themes within the database
//	 * Returns the result in a prefuse table
//	 */
//	public Table getThemes() {
//		String table = "themes";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get all of the sub themes
//	 * Returns the result in a prefuse table
//	 */
//	public Table getSub_Themes() {
//		String table = "sub_themes";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get all of the researchers
//	 * Returns the result in a prefuse table
//	 */
//	public Table getReseachers() {
//		String table = "researchers";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get all of the research centres
//	 * Returns the result in a prefuse table
//	 */
//	public Table getCentres() {
//		String table = "centres";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get all of the FOR codes
//	 * Do not parse this information yet
//	 * Just return the table
//	 * Returns the result in a prefuse table
//	 */
//	public Table getFOR_codes() {
//		String table = "FOR_Codes";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get all of the publications
//	 * Returns the result in a prefuse table
//	 */
//	public Table getPublications() {
//		String table = "publications";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get the link table between themes and researchers
//	 * Needs to get all of the information
//	 * At the moment just returns the table
//	 * Returns the result in a prefuse table
//	 */
//	public Table getThemes_researcher_link() {
//		String table = "themes_researcher_link";
//		Table data = getTable(table);
//		if(data == null) {
//			System.out.println("An error has occured getting the table data");
//			return null;
//		} else {
//			return data;
//		}
//	}
//	
//	/*
//	 * Get a specific table from the database
//	 * The table name is given in the arguments
//	 * Returns the result in a prefuse table
//	 */
//	public Table getTable(String table) {
//		String query  = "select * from " + table;
//		Table data = null;
//		try {
//			data = getDatasrc().getData(query);
//		} catch (DataIOException e) {
//			System.out.println("An error has occured trying to get the " + table + " data from the database\n" +
//					"Please try again later");
//			e.printStackTrace();
//		}
//		return data;
//	}
//	
//	/*
//	 * Run a specific SQL query on the database
//	 * Required for some composite or unique queries
//	 * Returns the result in a prefuse table
//	 */
//	public Table getMyQuery(String query) {
//		Table data = null;
//		try {
//			data = getDatasrc().getData(query);
//		} catch (DataIOException e) {
//			System.out.println("An error has occured executing the command " + query + " on the database\n" +
//					"Please try again later");
//			e.printStackTrace();
//		} catch(NullPointerException e) {
//			e.printStackTrace();
//		}
//		return data;
//	}
//	
//	/*
//	 * Get the database object
//	 */
//	public static DatabaseDataSource getDatasrc() {
//		return datasrc;
//	}
//
//	/*
//	 * Set the database object
//	 */
//	public static void setDatasrc(DatabaseDataSource datasrc) {
//		Prefuse_Connector.datasrc = datasrc;
//	}
//}