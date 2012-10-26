package connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

/**
 * This class is used to connect to a database All of the information required
 * to access the database is located in this file Any query to the database
 * needs to use an object from this class Only java functions are used but still
 * requires the mysql-connector to connect to the database
 * 
 * @author Anthony Scata
 * @version 2.0
 */

/**
 * Add support to derby database
 * 
 * @author Qiang Liu
 * 
 */
public class Java_Connector {

	/* VARIABLES */

	/**
	 * Let's note our database connection properties the JDBC driver we'll be
	 * using
	 */
	private final String driver = "com.mysql.jdbc.Driver";

	/**
	 * The Internet address of the database server
	 */
	private final String host = "localhost";

	/**
	 * The name of the database to connect to
	 */
	public String database;

	/**
	 * The user name required to login to the database
	 */
	protected String user;

	/**
	 * The password required for login
	 */
	protected String password;

	/**
	 * Create a database connection object
	 */
	protected static Connection connection = null;

	/**
	 * If the database has been connected to or not
	 */
	private boolean connected = false;

	/* CONSRUCTORS */

	/**
	 * The default constructor Connects to the database
	 */
	public Java_Connector(String user, char[] password, String database) {

		this.user = user;
		this.password = new String(password);
		this.database = database;
		connected = connect();
	}

	/**
	 * The constructor foe the connector class This constructor takes an integer
	 * as an option To be extended later
	 * 
	 * @param option
	 *            - whether to connect or not
	 */
	public Java_Connector(int option) {
		if (option == 1) {
			connected = connect();
		}
	}

	/* METHODS */

	/**
	 * This method connects the object to the database Required before any
	 * queries can be executed on the database
	 * 
	 * @return - the result of the connection
	 */
	public boolean connect() {
		// add support to derby --Qiang
		try {
			if (this.database.startsWith("Derby:")) {
				// e.g.
				// jdbc:derby:database/honours;create=true;user=root;password=admin
				String url = "jdbc:derby:database/"
						+ this.database.substring(6) + ";user=" + user
						+ ";password=" + password;
				System.out.println(url);

				Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
						.newInstance();
				connection = DriverManager.getConnection(url);

			} else {
				// Set the url needed to connect to the database
				String url = "jdbc:mysql://" + host + "/" + database;

				// Try and connect to the database
				Class.forName(driver).newInstance();
				connection = DriverManager.getConnection(url, user, password);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to the database!", 
					"Database connection error", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			System.exit(0);
			return false;
		}
		return true;
	}

	/**
	 * Disconnect from the database To be extended later
	 * 
	 * @return - the result of the disconnection
	 */
	public boolean disconnect() {

		// Only disconnect if we are connected or we might get an error
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Database connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}

		connected = false;

		return true;
	}

	/**
	 * Run a SQL on the database without return value To create/drop tmp table
	 * BY Qiang Liu
	 */

	public void exeMyQuery(String query) {
		// The variables that hold information for the query
		Statement statement = null;

		System.out.println("exeMyQuery:: " + query);
		String sql = query;

		// Try and execute the query
		try {
			statement = connection.createStatement();
			// for derby create as select ... need end with "with no data" then
			// insert data.
			if (this.database.startsWith("Derby:")
					&& sql.toLowerCase().startsWith("create table")) {
				sql += " with no data";
				statement.execute(sql);
				sql = query.substring(26, query.length() - 1);
				ResultSet rs = statement.executeQuery(sql);
				PreparedStatement ps = connection
						.prepareStatement("Insert into tmpTable Values (?,?)");
				connection.setAutoCommit(false);
				while (rs.next()) {
					ps.setObject(1, rs.getObject(1));
					ps.setObject(2, rs.getObject(2));
					ps.addBatch();
				}
				ps.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
			} else
				statement.execute(sql);
		} catch (SQLException e) {
			System.err.println("Error executing query: " + query);
			System.err.println("Error message: " + e.getMessage());
			System.err.println("Error number: " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("The databse has dissconnected");
		}
	}

	/**
	 * Run a specific SQL query on the database Required for some composite or
	 * unique queries
	 * 
	 * @param query
	 *            - the query to be executed
	 * @return - the result set that contains the items returned by the query
	 */
	public ResultSet getMyQuery(String query) {

		// The variables that hold information for the query
		Statement statement = null;
		ResultSet result_set = null;

		long st = System.currentTimeMillis();
		System.out.println("getMyQuery:: " + query);

		// Try and execute the query
		try {
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			result_set = statement.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Error executing query: " + query);
			System.err.println("Error message: " + e.getMessage());
			System.err.println("Error number: " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("The databse has dissconnected");
		}

		// return the result of the query
		System.out.println("Time use(ms) :: "
				+ (System.currentTimeMillis() - st));
		return result_set;
	}

	/* GENERIC GET QUERIES */

	/**
	 * Get the top level values
	 * 
	 * @return - the set
	 */
	public ResultSet getTopLevel(String tableName) {

		String query = "select * from " + tableName + " where ParentID=0 "
				+ " and origin_parent is not null";
		return getMyQuery(query);
	}

	/**
	 * Get the middle and low level values
	 * 
	 * @param parent
	 *            - the parent of the codes we want
	 * @return - the set
	 */
	public ResultSet getMiddleLevel(String tableName, String parent) {

		String query = "select * from " + tableName + " where "
				+ " parentid = " + parent;

		return getMyQuery(query);
	}

	/**
	 * @return the connected status
	 */
	public boolean isConnected() {
		return connected;
	}
}