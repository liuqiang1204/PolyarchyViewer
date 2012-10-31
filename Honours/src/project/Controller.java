package project;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
//import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JViewport;

import utilities.Custom_Messages;
import connector.Java_Connector;
import extensions.AMLabel;
import extensions.Hierarchy;
import extensions.QueryView;

/**
 * The controller is the main class This is where all of the real work happens
 * It creates the view and decides what will be added All interaction takes
 * place through this class
 * 
 * @author Anthony Scata
 * @version 9.0
 * 
 */
/**
 * Made some improvements and fixed some bugs 1. From_to(): add c. g. to all
 * fields 2. connection(): use "in" to replace multiple "OR" statements 3.
 * Largest()::tbl[3], middle_query should be end_query 4. alpha_count(): change
 * query, use = to replace like; compact the queries. 5. largest(): use a temp
 * table to avoid multiple 'join' queries 6. connection(): remove some
 * duplicated queries 7. change some select in select to join : interactions in
 * a reasonable time. 8. round the sub_count to X.XX 9. remove all round
 * operation in the controller.java. Do the round operation in the label paint()
 * 10. It is the publications_linking table with hierarchy table matching error.
 * Change anyone to fix the db error. 11. when 3 levels, interaction of level
 * error!!! fixed
 * 
 * 12. Interaction:should use tmp join table! But lose the meaning of data
 * structure! And need to use triggers to keep data same.s 13. label bugs:
 * Movie--Asia cannot be 100% ?????? 14. duplicated queries: connection() when
 * list>1, index is different, but it is same query.
 * 
 * @author Qiang Liu
 * 
 */
public class Controller {

	/* REQUIRED OBJECTS */

	/**
	 * The database connector that is used to execute queries
	 */
	private Java_Connector m_model;

	/**
	 * The view that shows the information to the user
	 */
	private View m_view;

	/**
	 * The advanced query view that the user can use
	 */
	private QueryView m_query;

	/**
	 * The current count type being used
	 */
	private String count_type;

	/**
	 * The current connection type being used
	 */
	private String connection_type;

	/* INTERACTION OBJECTS */

	/* Used for knowing where they are in the display */

	/**
	 * Hierarchy of the first table
	 */
	private int alpha_code = 1;

	/**
	 * Hierarchy of the second table
	 */
	private int beta_code = 2;

	/**
	 * Hierarchy of the third table
	 */
	private int gamma_code = 3;

	/**
	 * Store main column name to fetch from the database
	 */
	private String column;

	/**
	 * String array to store names of the table from the 'Hierarchy' table
	 * (fetched from the database)
	 */
	String[] tableName;

	/**
	 * String array to store names of the link table (fetched from the database)
	 */
	String[] linkTableName;

	/**
	 * String array to store names of the column corresponding to the link
	 * tables (fetched from the database)
	 */
	String[] columnName;

	/**
	 * int array to store level of the tables (fetched from the database)
	 */
	int[] tableLevel;

	// ALTER TABLE `grants_researcher_link` ADD INDEX (`researcher_id`)
	// ALTER TABLE publications_researcher_link ENGINE = MyISAM
	// ALTER TABLE mytable DROP PRIMARY KEY, ADD PRIMARY KEY(col1,col2);

	// TODO
	// clear not working on the staff during interaction, we sometimes it is
	// sometimes it doesn't, must be a thread problem
	// change the pubs and grants bar length to be CEILING (POW(SUM(
	// two.`weighted_sum`),2) / POW(COUNT(two.`researcher_id`),2) )
	// need plan for clearAll on load, this is taking the most time and if can
	// reduce will get load down to 10 seconds
	// stop the flickering, not sure what caused it or how to stop it
	// make the buffer work 100%, not working for themes, if you perform some
	// actions it starts to work
	// split the class into lables, images and bars if time

	/* CONSTUCTOR */

	/**
	 * The constructor for the controller It sets up the visualisation
	 * 
	 * @param model
	 *            - The one model used for this visualisation, namely the
	 *            connector
	 * @param view
	 *            - The view or interface that the user sees
	 * @param query
	 *            - The custom query view that can be created by the user
	 */
	public Controller(Java_Connector model, View view, QueryView query) {

		System.out.println("Controller running...");

		// set the model and connect to the database
		m_model = model;

		// the initial view that the user can see
		m_view = view;

		// the query view that is shown externally to the main frame
		m_query = query;

		connection_type = (String) m_view.getConnection_options()
				.getSelectedItem();

		// assign values to tableName

		try {

			// create a query to fetch values from the hierarchy table
			String query1 = "select * from hierarchy";

			ResultSet rs = m_model.getMyQuery(query1);

			// get the number of rows fetched to create the arrays
			rs.last();
			int rowCount = rs.getRow();

			rs.beforeFirst();
			int i = 0;

			tableName = new String[rowCount];
			tableLevel = new int[rowCount];

			// filling the values of the table names and their corresponding
			// level
			while (rs.next()) {
				tableName[i] = rs.getString(2);
				tableLevel[i] = rs.getInt(3);
				i++;
			}

			// based on the count type selected, creating the name of the
			// linking table
			count_type = (String) m_view.getCount_options().getSelectedItem();
			count_type = count_type + "_linking";

			// fetch the name of the column based on the linking table selected
			// eg. for movies, column will be movieId, people -> peopleId
			query1 = "select * from atom where t_name = '" + count_type.toLowerCase() + "'";
			rs = m_model.getMyQuery(query1);
			rs.next();
			column = rs.getString(3);

			// creating query to fetch the linking tables for the selected count
			// type
			query1 = "select * from " + count_type;
			rs = m_model.getMyQuery(query1);
			rs.last();

			rowCount = rs.getRow();

			rs.beforeFirst();
			i = 0;

			// filling the array for linking tables and their corresponding
			// column names
			// eg movies_genre and genreId
			linkTableName = new String[rowCount];
			columnName = new String[rowCount];

			while (rs.next()) {
				linkTableName[i] = rs.getString(2);
				columnName[i] = rs.getString(3);
				i++;
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}

		// Check if the connection has been made with the database
		if (!m_model.isConnected()) {

			// We haven't connected so display an error message
			Custom_Messages.display_error(m_view, "Database Connection Error",
					"Problem has occured connecting to the database \n "
							+ "No Operations can occur \n " + "Exiting now");
			System.exit(0);
		}

		// Add the contents
		if (!add_contents_to_view()) {

			Custom_Messages.display_error(m_view, "Adding Contents",
					"ERROR:An error has occured adding the contents to the visualisation \n"
							+ "The system will not exit");
			System.exit(0);
		}

		// make sure that we re pack the view because it will have changed now
		// all of the elements have been loaded
		m_view.pack();
	}

	/* ADDING CONTENTS */

	/**
	 * Add the contents to the panels These defines what part of the
	 * visualisation goes where While doing this we also make sure that
	 * interaction can not occur by placing a panel over the frame
	 * 
	 * @return - The result of the adding of the contents, indicating an error
	 *         or success
	 */
	public boolean add_contents_to_view() {

		/**
		 * Adding some code for testing
		 */

		long startMemory = Runtime.getRuntime().totalMemory();
		long startTime = System.currentTimeMillis();

		System.out.println("Start adding contents ...");

		// Check the result with this variable
		// double result = 0; //remove by Qiang

		try {
			// turn on the glass that prevents interaction while loading
			m_view.startGlass(true);

			// Firstly add the action listeners to all of the panel objects
			add_listeners();

			// increment the status bar
			m_view.incrementWaiter(25);
			int x = 25;

			// The value to decide which hierarchy we are adding the next set of
			// objects to
			int hierarchy_num = 1;

			// Add the table information into the correct hierarchy

			while (hierarchy_num < 4) {
				// call the main function to fill in the values into the screen
				// result = Alpha_table(hierarchy_num);
				Alpha_table(hierarchy_num);

				m_view.incrementWaiter(x += 25);

				// increment the hierarchy
				hierarchy_num++;
			}

			long endTime = System.currentTimeMillis();
			long endMemory = Runtime.getRuntime().totalMemory();
			System.out.println("time spent " + (endTime - startTime)
					+ "ms, memory used " + ((endMemory - startMemory) / 1024)
					+ "kB");

			// we have almost finished the visualisation preparation
			m_view.incrementWaiter(99);

			// just make sure that the initial elements are in the correct
			// position
			m_view.add_buffer();

		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {

			// turn off the glass to allow interaction again
			m_view.endGlass(false);
		}

		// All of the visualisation has been loaded
		m_view.setLoaded(true);

		long endTime = System.currentTimeMillis();
		long endMemory = Runtime.getRuntime().totalMemory();
		System.out.println("time spent " + (endTime - startTime)
				+ "ms, memory used " + ((endMemory - startMemory) / 1024)
				+ "kB");

		// Nothing bad has happened, return the good result
		return true;

	}

	/* LISTENERS ADDED */

	/**
	 * Add the listeners to the objects in the view that allow interaction These
	 * include the search box, combo box and panel operations
	 */
	public void add_listeners() {

		/* LISTENERS REQUIRED */

		// For the clear buttons
		Clear_Button_Action clear_button = new Clear_Button_Action();

		// When a key is press in a search box
		KeyPress_Action keypress = new KeyPress_Action();

		// To interact with the combo box
		ComboBox_Action combo_box = new ComboBox_Action();

		// To control the scroll bar
		Scroll_adjust_listener listener = new Scroll_adjust_listener();

		// VIEW LISTENERS
		m_view.add_listeners(combo_box, listener, clear_button, keypress);
	}

	/*
	 * FUNCTIONS TO ADD THE ITEMS TO THE HIERARCHIES
	 */

	/**
	 * Generic adder function Takes away some of the duplicate code that is
	 * required to add contents to the different hierarchies
	 * 
	 * @param id
	 *            - the unique id of the object
	 * @param name
	 *            - the name of the object
	 * @param hierarchy
	 *            - the hierarchy that it belongs to
	 * @param level
	 *            - the level in that hierarchy
	 * @param parent
	 *            - the parent id of this object
	 * @return - the label that is created
	 */
	public AMLabel addLabel(String id, String name, Hierarchy hierarchy,
			int level, String parent) {

		// Add the label to the visualisation and save the returned label
		// Send the hierarchy to which it belongs, the level, the name or
		// description and the FOR code
		AMLabel label = m_view.addLabel(hierarchy, level, name, id, parent);

		// Add the action listener to this label for interaction
		label.addMouseListener(new Interaction_Mouse_Listener());

		// If we have an image then add the listener to that to
		if (label.isHas_image()) {
			label.getIts_image().addMouseListener(
					new Interaction_Mouse_Listener());
		}

		// the label that was created
		return label;
	}

	/**
	 * Add the top level values to the visualisation Calls the
	 * Alpha_codes_middle method to add the rest of the visualisation
	 * 
	 * @param hierarchy_num
	 *            - the hierarchy that it will be placed in
	 * @return - true if everything is added properly
	 */
	public double Alpha_table(int hierarchy_num) {

		/**
		 * add some code for test --Qiang
		 */
		System.out.println("Alpha_table : " + hierarchy_num + " N: " + tableName[hierarchy_num-1]);

		// Specify which level this is
		int level = 1;

		// the count of the elements
		float overal_count = 0;

		// the left padding to the label
		int left_padding = 15;

		// get the hierarchy that we will be adding it to
		Hierarchy hierachy = m_view.decide_hierarchy(hierarchy_num);

		// get the index value used to fetch the correct tableName
		int index = hierarchy_num - 1;

		// call the largest function
		largest(index);

		// First get all of the top level values
		ResultSet data = m_model.getTopLevel(tableName[index]);

		// Loop through the results
		try {

			while (data.next()) {

				// Get the data
				String alpha_code = data.getString(1);

				// adding the substring 100/200/300.. depending on the
				// hierarchy_num to make
				// alpha_code unique. Necessary as they are used to uniquely
				// identify a
				// particular label in the display
				alpha_code = hierarchy_num + "00" + alpha_code;

				// add the label
				AMLabel label = addLabel(alpha_code, data.getString("label"),
						hierachy, level, "");

				// now go and add the rest of the hierarchy to the visualisation
				double sub_count;

				if (level < tableLevel[index]) {
					// if there is another level, call function
					// alpha_codes_middle to fill the rest of the values
					sub_count = alpha_codes_middle(hierachy, level + 1,
							alpha_code, left_padding + 15, index);
				} else {
					// if this the last level, call the count method
					// String string_sub_count = alpha_count(alpha_code, index);
					// sub_count = Double.parseDouble(string_sub_count);
					sub_count = alpha_count(alpha_code, index);
				}

				// go an edit this labels bar
				m_view.bar_edit(sub_count, hierachy, label);

				// the overall count needs to be updated
				overal_count += sub_count;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Custom_Messages.display_error(m_view, tableName[0],
					"An error has occured trying to get the data for the top level of the "
							+ tableName[0]);
			e.printStackTrace();
		}

		return overal_count;
	}

	/**
	 * Add the contents to the seconds level of the hierarchy If there are more
	 * levels, then the function calls recursively. For the last level it calls
	 * the count function
	 * 
	 * @param hierarchy
	 *            - the hierarchy that it belongs t0
	 * @param level
	 *            - its level in the hierarchy
	 * @param parent_id
	 *            - its parent's id
	 * @param left_padding
	 *            - the left padding to be added to the label
	 * @param index
	 *            - index for the correct tableName
	 * @return - the number of objects that were counted
	 */
	public double alpha_codes_middle(Hierarchy hierarchy, int level,
			String parent_id, int left_padding, int index) {

		float overal_count = 0;
		int hierarchy_num = index + 1;

		// stripping of the characters 100/200/300 that were added in the code
		// for the next query
		// storing it in a temporary variable parent
		String parent = parent_id.substring(3);

		// Fetch the data for the 'id'
		ResultSet data = m_model.getMiddleLevel(tableName[index], parent);

		// Loop through the results
		try {

			while (data.next()) {

				// get the data
				String alpha_code = data.getString(1);

				// adding the substring 100/200/300.. depending on the
				// hierarchy_num to make
				// indivdual idhierarchy unique
				alpha_code = hierarchy_num + "00" + alpha_code;

				AMLabel label = addLabel(alpha_code, data.getString("label"),
						hierarchy, level, parent_id);

				// now go and add the rest of the hierarchy to the visualisation
				double sub_count;

				if (level < tableLevel[index]) {
					sub_count = alpha_codes_middle(hierarchy, level + 1,
							alpha_code, left_padding + 15, index);
				} else {
					// String string_sub_count = alpha_count(alpha_code, index);
					// sub_count = Double.parseDouble(string_sub_count);
					sub_count = alpha_count(alpha_code, index);
				}

				// edit the bar now we have the data
				m_view.bar_edit(sub_count, hierarchy, label);

				overal_count += sub_count;

				label.clearThisSet(false);
			}

		} catch (SQLException e) {
			Custom_Messages.display_error(m_view, tableName[0],
					"An error has occured get the middle level of the "
							+ tableName[0]);
			e.printStackTrace();
		}
		return overal_count;
	}

	/*
	 * FUNCTIONS TO COMPUTE THE LARGEST OF SOMETHING
	 */

	/**
	 * Gets the code with the largest count We are only interested in the number
	 * not the actual code A different query is formed based on the number of
	 * levels the table has
	 */
	public void largest(int index) {
		/**
		 * rewrite by Qiang
		 */

		// Store the temporary largest
		float largest_top = 0;

		String query;

		try {

			// Create a tmp table for optimization --Qiang
			/**
			 * Should use sum() query instead of loops.!!!
			 */
			m_model.exeMyQuery("Drop table tmpTable");
			String sql = "CREATE TABLE tmpTable AS (SELECT sum(weighted_sum) as t_sum,parentid FROM "
					+ linkTableName[index]
					+ " as t1 join "
					+ tableName[index]
					+ " as t2 on t2.idhierarchy=t1." + columnName[index] + " group by parentid)";
			m_model.exeMyQuery(sql);

			// if level=1, then a single query is sufficient
			// selects weighted_sum for each value and finds the maximum among
			// them
			if (tableLevel[index] == 1) {

				query = "select sum(weighted_sum) from " + linkTableName[index]
						+ " where " + columnName[index]
						+ " in (select idhierarchy " + " from "
						+ tableName[index] + " ) group by " + columnName[index];

				ResultSet top_data = m_model.getMyQuery(query);

				while (top_data.next()) {
					float count = 0;
					count = top_data.getFloat(1);

					if (count > largest_top) {
						largest_top = count;
					}
				}
			}

			// if level=2, first find the parent, and for each parent find the
			// max of sum(weighted_sum)
			else if (tableLevel[index] == 2) {

				String top_query = "select * from " + tableName[index]
						+ " where parentid = 0";
				ResultSet top_data = m_model.getMyQuery(top_query);
				//use select max from a tmp table --qiang
				String middle_query = "select sum(t_sum) from tmpTable where parentid in (";
				// Loop through the results to get parentids
				while (top_data.next()) {
					// get the columns we need
					middle_query +=top_data.getString(1)+",";
				}
				middle_query = middle_query.substring(0, middle_query.length()-1)+")";
				ResultSet middle_sum = m_model.getMyQuery(middle_query);
				float count = 0;
				if(middle_sum.next())count = middle_sum.getFloat(1);
				middle_sum.close();
				// finding the max value of all
				if (count > largest_top) {
					largest_top = count;
				}
			}
			// if level=2, first find the parent, and for each parent find the
			// child and for them find the max of sum(weighted_sum)
			else if (tableLevel[index] == 3) {

				String top_query = "select * from " + tableName[index]
						+ " where parentid = 0";
				ResultSet top_data = m_model.getMyQuery(top_query);

				// Loop through the results
				while (top_data.next()) {

					// get the columns we need
					String id = top_data.getString(1);

					String middle_query = "select * from " + tableName[index]
							+ " where parentid = " + id;
					ResultSet middle_data = m_model.getMyQuery(middle_query);
					
					//use select sum in tmp table --qiang
					String end_query = "select sum(t_sum) from tmpTable where parentid in (";
					// Loop through the results to get parentids
					while (middle_data.next()) {
						// get the columns we need
						end_query +=middle_data.getString(1)+",";
					}
					middle_data.close();
					end_query = end_query.substring(0, end_query.length()-1)+")";
					ResultSet end_sum = m_model.getMyQuery(end_query);
					float count = 0;
					if(end_sum.next())count = end_sum.getFloat(1);
					end_sum.close();
					// finding the max value of all
					if (count > largest_top) {
						largest_top = count;
					}
				}

			}
		} catch (SQLException e) {
			Custom_Messages.display_error(m_view, "Largest Code",
					"An error has occured getting the largest FOR code");
			e.printStackTrace();
		}

		// get the correct hierarchy
		Hierarchy hierachy = m_view.decide_hierarchy(index + 1);

		// go and set the largest variables
		hierachy.setLargest_top(largest_top);
		
//		System.out.println(index + " - largest : " + largest_top);

	}

	/**
	 * A generic count for the codes We have the id of what we are searching and
	 * we will return its count
	 * 
	 * @param id
	 *            - the id we are counting
	 * @param index
	 *            - the index for the tableName
	 * @return - the integer count value
	 */
	public double alpha_count(String id, int index) {

		// String add = "";
		//
		// id = id.substring(3);
		// //Why use like ???? --Qiang
		// add = " WHERE " + columnName[index] + " LIKE '" +id + "%' ";
		//
		// String query = "";
		//
		// query = "SELECT sum(weighted_sum) as count" +
		// " FROM  " + linkTableName[index] +
		// add;

		id = id.substring(3);

//		 String query = "SELECT weighted_sum FROM  " + linkTableName[index]
//		 + " WHERE " + columnName[index] + " = " + id;
		String query = "SELECT sum(weighted_sum) FROM  " + linkTableName[index]
				+ " WHERE " + columnName[index] + " = " + id;

		ResultSet rs = m_model.getMyQuery(query);
		Double count = new Double(0);
		try {
			if (rs.next()) {
				count = rs.getDouble(1);
			}
		} catch (SQLException e) {

		}
		return count.doubleValue();

	}

	/*
	 * INTERACTION FUNCTIONS
	 */

	/**
	 * This method takes care of the interaction that will take place We decide
	 * here if we are expanding or collapsing We also decide if we will
	 * highlight the items or not
	 * 
	 * @param is_hover
	 *            - if the event was a hover or not (not is a click event)
	 * @param is_entry
	 *            - if it was an entry (hover or click) or an exit (hover or
	 *            click)
	 * @param is_left_up
	 *            - if the interaction was left or up, necessary for the exit
	 *            conditions
	 * @param label
	 *            - the label that was interacted with
	 */
	public void interaction(boolean is_hover, boolean is_entry,
			boolean is_left_up, AMLabel label) {

		// to see if this interaction was an image or not, images expand and
		// collapse
		boolean is_image = false;

		// get the information about this label
		String id = label.getUniqueID();

		// find the hierarchy using the label id
		Hierarchy hierarchy = m_view.findHierarchy(id);
		/* CHECK WHAT WAS INTERACTED WITH */

		// if the label is an image, we are expanding or collapsing
		if (label.isIs_image()) {

			// no interaction to be performed on expand or collapse
			is_image = true;

		} else {

			// if the item was clicked show its details
			if (!is_hover) {
				if (!label.isClicked()) {
					hierarchy.showDetails(label.getPrimaryText(), id,
							label.getWebsite());
				} else {
					hierarchy.removeInformation();
				}
			}

			// now change the text of the interacted label so the user can see
			// what was interacted with
			label.change_text(is_entry);
		}

		/* PERFORM THE OPERATIONS */

		// Depending on which panel the interaction originated from forces a
		// different type of query
		String query = "";
		query = alpha_code(label, hierarchy.getId());

		// go and perform the actions
		perform_action(hierarchy, label, query, is_image, is_hover, is_entry,
				is_left_up);

		// check to see if we need to exit the buffer is the labels have moved
		// TODO need to get this working
		// hierarchy.edit_buffer();
	}

	/**
	 * Perform a generic action of this label This is made so that any function
	 * can call this method
	 * 
	 * @param hierarchy
	 *            - the hierarchy that the label lives in
	 * @param label
	 *            - the label that was interacted with
	 * @param query
	 *            - the query that needs to be executed
	 * @param is_image
	 *            - if we interacted with an image or not
	 * @param is_hover
	 *            - if it was a hover event or click
	 * @param is_entry
	 *            - if it was an entry onto the label or an exit
	 * @param is_left_up
	 *            - if the interaction was left or up, necessary for the exit
	 *            conditions
	 */
	public void perform_action(Hierarchy hierarchy, AMLabel label,
			String query, boolean is_image, boolean is_hover, boolean is_entry,
			boolean is_left_up) {

		/*
		 * PART ONE Get the data
		 */

		// Get the result
		ResultSet data = m_model.getMyQuery(query);

		// The hashmap for this panel
		HashMap<String, Integer> position_map = hierarchy.getMap();

		// This array holds the clicked elements
		HashSet<Integer> clicked_elements = hierarchy.getClick();

		// This array holds the just hovered elements
		HashSet<Integer> hovered_elements = new HashSet<Integer>();

		// loop through the results
		try {

			while (data.next()) {

				// Find how many rows we have
				ResultSetMetaData rsmd = data.getMetaData();
				int NumOfCol = rsmd.getColumnCount();

				// loop through the rows
				for (int column_rows = 1; column_rows <= NumOfCol; column_rows++) {

					// get the key
					String key = data.getString(1).toString();

					// adding the substring 100/200/300.. depending on the
					// hierarchy_num to make
					// indivdual idhierarchy unique
					key = hierarchy.getId() + "00" + key;

					// if this item exists within out proper map
					if (position_map.containsKey(key)) {

						// find the index for this unique ID
						int index = position_map.get(key);

						// get the label
						AMLabel tmp_label = (AMLabel) hierarchy
								.getInnerhierarchy().getComponent(index);

						// Need this to be an Integer not an int or we will
						// perform the wrong remove
						Integer tmp_index = new Integer(index);

						// if it was a click and on something that wasn't an
						// image
						if (!is_hover && !is_image) {

							// set the label to be clicked
							tmp_label.clicked();

							// add or remove it from the click array
							if (clicked_elements.contains(tmp_index)) {
								clicked_elements.remove(tmp_index);
							} else {
								clicked_elements.add(tmp_index);
							}
						} else {

							// it was a hover so add it to the elements of the
							// hover
							hovered_elements.add(tmp_index);
						}

					}
				}
			}

		} catch (SQLException e) {
			Custom_Messages.display_error(m_view, "Interaction Error",
					"An error has occured getting the required information for the interaction\n"
							+ "Please try again");
			e.printStackTrace();
		}

		// set the clicked attributed of the hierarchy
		hierarchy.setClick((HashSet<Integer>) clicked_elements);

		/*
		 * PART TWO use the data
		 */

		// The list to hold the unique elements
		HashSet<Integer> list = new HashSet<Integer>();

		// Decide if we want to use the click array or not
		// This is so that when we expand we don't use the clicked elements as
		// well
		if (!is_image) {

			// If its not an image then we are counting
			// On entry we want the clicked and hovered but on exit only the
			// clicked as the hovered elements should disappear
			if (is_entry) {
				list.addAll(hovered_elements);
			}

			// add all of the clicked elements
			list.addAll(clicked_elements);

			// send all of the elements to the connection, this minimises the
			// queries we have to make to only one
			connection(list, hierarchy);

		} else {

			// If it was not a hover, therefore a click, set it
			if (!is_hover) {
				label.clicked();
			}

			// add all of the hovered elements because we are only interested in
			// working with them
			list.addAll(hovered_elements);

			// Loop through the array
			for (Iterator<Integer> i = list.iterator(); i.hasNext();) {

				// get the index
				Integer index = i.next();

				// get the label we are interested in
				AMLabel this_label = (AMLabel) hierarchy.getInnerhierarchy()
						.getComponent(index);

				// if it was an image then we perform a expand or collapse
				hierarchy.expand_collapse_decision(label, this_label, is_entry,
						is_hover, is_left_up);
			}
			hierarchy.getInnerhierarchy().repaint();
		}

	}

	/**
	 * This method makes the connection between the levels This way if we
	 * interact with one panel then the rest will light up and show data As we
	 * have three panels, just need to perform the operations on the other two
	 * 
	 * @param list
	 *            - The list containing unique elements
	 * @param h3
	 *            - The panel that it originated from
	 */
	public void connection(HashSet<Integer> list, Hierarchy h3) {

		// bug : multiple 'inputs' remove, list?? --qiang

		try {

			m_view.setWaitCursor(true);

			// Variables required for first panel
			StringBuffer query1 = null;
			Hierarchy h1 = null;

			// Variable required for second panel
			StringBuffer query2 = null;
			Hierarchy h2 = null;

			StringBuffer query3 = null;

			String column = "";
			// boolean one = true;

			// Column name for the where clause, fetched from the database
			int cIndex = h3.getId() - 1;
			// column = columnName[cIndex];
			// add a temp table name for column. Use it for future join queries
			column = "<UnknownTable>." + columnName[cIndex];

			// The string to hold all of the elements we want
			StringBuffer inputs = new StringBuffer(" WHERE ");
			boolean first = true;
			String id = "";

			ResultSet rs;
			/**
			 * Remove duplications --Qiang 1. use the pids set to record the
			 * label levels to avoid multiple queries for the same value 2. use
			 * the input set to avoid generating same statements in the 'inputs'
			 */
			HashSet<String> inputSet = new HashSet<String>();
			HashSet<String> pids = new HashSet<String>();
			
//			System.out.println("<<<<<<<<" + list.size());
			
			for (Iterator<Integer> i = list.iterator(); i.hasNext();) {

				Integer tindex = i.next();
				AMLabel this_label = (AMLabel) h3.getInnerhierarchy()
						.getComponent(tindex);

//				System.out.println(tindex + "-------> " + this_label.getLevel()
//						+ " tbl--->" + tableLevel[h3.getId() - 1]);

				if (this_label.getLevel() == 2
						&& tableLevel[h3.getId() - 1] == 3) {
					String pid = this_label.getParent_id().substring(3);
					
					if(!pids.contains(pid)){
						pids.add(pid);
						try {
							String query = "SELECT idhierarchy FROM "
									+ tableName[h3.getId() - 1]
									+ " WHERE parentid in (select idhierarchy from "
									+ tableName[h3.getId() - 1]
									+ " where parentid = "
									+ this_label.getParent_id().substring(3) + ") ";
	
							rs = m_model.getMyQuery(query);
							while (rs.next())
								inputSet.add(rs.getString(1));
							
						} catch (SQLException se) {
							System.out.println("SE Exception");
							se.printStackTrace();
						}
					}
				}

				else {
					id = this_label.getUniqueID().substring(3);
					inputSet.add(id);
				}

			} // end of for

			for (Iterator<String> i = inputSet.iterator(); i.hasNext();) {
				if (connection_type.equalsIgnoreCase("OR")) {
					if (first) {
						inputs.append(column + " in (" + i.next());
						first = false;
					} else
						inputs.append(", " + i.next());
				} else {
					if (!first) {
						inputs.append(" " + connection_type + " ");
					} else
						first = false;

					inputs.append(i.next());
				}
			}

			if (connection_type.equalsIgnoreCase("OR"))
				inputs.append(") ");

			/*
			 // loop through the array and add the information to the string to be queried
			 // this allows us to use one query to get multiple results saving time
			 for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
			
			 Integer tindex = i.next();
			 AMLabel this_label = (AMLabel) h3.getInnerhierarchy()
			 .getComponent(tindex);
			
			 // use tmpstr to remove duplicated values--Qiang
			 // tmpstr = "";
			
			 // if(!first) {
			 // inputs.append(" " + connection_type + " ");
			 // }
			
			 System.out.println(tindex + "-------> " + this_label.getLevel() +
			 " tbl--->"+ tableLevel[h3.getId() - 1] );
			
			 if (this_label.getLevel() == 2
			 && tableLevel[h3.getId() - 1] == 3) {
			
			 try {
			 String query = "SELECT idhierarchy FROM " +
			 tableName[h3.getId()-1] +
			 " WHERE parentid in (select idhierarchy from " +
			 tableName[h3.getId()-1] +
			 " where parentid = "
			 +this_label.getParent_id().substring(3)+ ") ";
			
			 rs = m_model.getMyQuery(query);
			 while(rs.next())inputSet.add(rs.getString(1));
			 // rs.last();
			 // int rowCount = rs.getRow();
			 //
			 // rs.beforeFirst();
			 // rs.next();
			 // id = rs.getString(1);
			 // // --qiang
			 // // inputs.append( column + " = '" + id + "' ");
			 // // inputs.append(column + " in('" + id +"'");
			 // tmpstr += column + " in('" + id + "'";
			 //
			 // for (int j = 0; j < rowCount - 1; j++) {
			 // rs.next();
			 // id = rs.getString(1);
			 // // inputs.append(" OR " + column + " = '" + id +
			 // // "' ");
			 // // inputs.append(",'" + id +"'");
			 // tmpstr += ",'" + id + "'";
			 // }
			 //
			 // // inputs.append(") ");
			 // tmpstr += ") ";
			
			 // one = false;
			 } catch (SQLException se) {
			 System.out.println("SE Exception");
			 se.printStackTrace();
			
			 }
			 }
			
			 else {
			 id = this_label.getUniqueID().substring(3);
			 // inputs.append(column + " = '" + id + "' ");
			 // tmpstr += column + " = '" + id + "' ";
			 inputSet.add(id);
			 }
			
			 // first = false;
			 // inputSet.add(tmpstr);
			 } //end of for
			
			 for (Iterator<String> i = inputSet.iterator(); i.hasNext();) {
			 if(connection_type.equalsIgnoreCase("OR")){
			 if(first){
			 inputs.append(column + " in (" + i.next());
			 first = false;
			 }
			 else inputs.append(", "+i.next());
			 }
			 else{
			 if (!first) {
			 inputs.append(" " + connection_type + " ");
			 } else
			 first = false;
			
			 inputs.append(i.next());
			 }
			 }
			
			 if(connection_type.equalsIgnoreCase("OR"))inputs.append(") ");
			 
			 */

			// TODO could tidy up the parts below if time, works fine
			// Check which queries we want to run

			try {

				// panel 1 to other panels
				if (h3.getId() == alpha_code) {

					query1 = new StringBuffer(from_to(inputs, linkTableName[0],
							linkTableName[0]));

					query2 = new StringBuffer(from_to(inputs, linkTableName[0],
							linkTableName[1]));
					h1 = m_view.decide_hierarchy(beta_code);

					query3 = new StringBuffer(from_to(inputs, linkTableName[0],
							linkTableName[2]));
					h2 = m_view.decide_hierarchy(gamma_code);
				}

				// panel 2 to other panels
				else if (h3.getId() == beta_code) {

					query1 = new StringBuffer(from_to(inputs, linkTableName[1],
							linkTableName[1]));

					query2 = new StringBuffer(from_to(inputs, linkTableName[1],
							linkTableName[0]));
					h1 = m_view.decide_hierarchy(alpha_code);

					query3 = new StringBuffer(from_to(inputs, linkTableName[1],
							linkTableName[2]));
					h2 = m_view.decide_hierarchy(gamma_code);

				}

				// panel 3 to other panels
				else if (h3.getId() == gamma_code) {
					query1 = new StringBuffer(from_to(inputs, linkTableName[2],
							linkTableName[2]));

					query2 = new StringBuffer(from_to(inputs, linkTableName[2],
							linkTableName[0]));
					h1 = m_view.decide_hierarchy(alpha_code);

					query3 = new StringBuffer(from_to(inputs, linkTableName[2],
							linkTableName[1]));
					h2 = m_view.decide_hierarchy(beta_code);
				}

				else {
					Custom_Messages.display_error(m_view, "Interaction Error",
							"An error has occured deciding on the column for the interaction\n"
									+ "Please try again");
				}

			}

			catch (Exception e) {
				e.printStackTrace();
			}
			// clear the hierarchies
			h1.clearCount();
			h2.clearCount();
			h3.clearCount();

			// Check to see if we actually have elements we want to interact
			// with
			if (!list.isEmpty()) {

				// run the loop through the connection

				looper(h1, query2.toString());

				looper(h2, query3.toString());

				looper(h3, query1.toString());

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// now set the cursor back even if a problem occurs with the
			// connection
			m_view.setWaitCursor(false);
		}
	}

	/**
	 * Decides which query to be run when we have a starting set (the from) and
	 * the table we want to get to (the to) We decide which query we will use
	 * depending on the count type that is currently being used
	 * 
	 * @param inputs
	 *            - the elements that we want to find
	 * @param from
	 *            - the table we are starting from
	 * @param to
	 *            - the table we are ending with
	 * @return - the query that needs to be run to find the correct elements,
	 *         with the count
	 */
	public StringBuffer from_to(StringBuffer inputs, String from, String to) {

		// 1.fix a bug, adding 'g' & 'c --qiang
		// 2.inputs query???
		// 3.Merge queries -- Michael
		// 4.Still have some bugs. Need to analyze the processing formulas.
		// 5.Change select in select to join

//		System.out.println("from_to (inputs): " + inputs);

		if (!inputs.equals("")) {

			// forming the query, 9 queries in total for each possible
			// combination

			int fromIndex = -1;
			int toIndex = -1;
			int tableLimit = 3;
			for (int i = 0; i < tableLimit; ++i) {
				if (from.equals(linkTableName[i])) {
					fromIndex = i;
				}

				if (to.equals(linkTableName[i])) {
					toIndex = i;
				}
			}

			if (fromIndex == toIndex) {
				// Same Table.

				String queryN = "select c." + columnName[toIndex]
						+ ", sum(c.weighted_sum) " + " from  "
						+ linkTableName[toIndex] + " as c"
						+ inputs.toString().replaceAll("<UnknownTable>", "c")
						+ " group by  " + columnName[toIndex];
				//
				// String queryO = "select " + columnName[toIndex] +
				// ", sum(weighted_sum) " +
				// " from  " + linkTableName[toIndex] +
				// " where " + column + " in " +
				// "(select  " + column + " from " + linkTableName[toIndex] +
				// inputs +
				// " ) group by  " + columnName[toIndex];

				return new StringBuffer(queryN);

			} else {
				// String queryN = "select c." + columnName[toIndex] +
				// " , g.weighted_sum*c.weighted_sum " +
				// " from " + linkTableName[toIndex] + " as c, (select " +
				// column + ", weighted_sum from " + linkTableName[fromIndex] +
				// inputs +
				// " ) as g " +
				// "WHERE c." + column + " = g." + column +
				// "  group by g." + column + " ,c." + columnName[toIndex];

				// String queryO = "select c." + columnName[toIndex] +
				// " , g.weighted_sum*c.weighted_sum " +
				// " from " + linkTableName[toIndex] + " as c, " +
				// linkTableName[fromIndex] + " as g " +
				// " where g." + column +
				// " in (select " + column + " from " + linkTableName[fromIndex]
				// + inputs +
				// " ) and c." + column + " = g." + column +
				// "  group by g." + column + " ,c." + columnName[toIndex];

				String queryO = "select c." + columnName[toIndex]
						+ " , g.weighted_sum*c.weighted_sum " + " from "
						+ linkTableName[toIndex] + " as c join "
						+ linkTableName[fromIndex] + " as g on c." + column
						+ " = g." + column
						+ inputs.toString().replaceAll("<UnknownTable>", "g");
				// + "  group by g." + column + " ,c."+ columnName[toIndex];
				// why use group by? there is no sum().

				// use the 'join table' instead of join operation 
				// --only for honours db
				// should find a suitable way for all dbs --qiang
				if(m_model.database.toLowerCase().endsWith("honours")){
					queryO = "select id1, wsum from join_"
						+ linkTableName[toIndex]+"_"+linkTableName[fromIndex] + " "
						+ inputs.toString().replaceAll("<UnknownTable>.idhierarchy", "id2");
				}
				return new StringBuffer(queryO);

			}
		}

		/*
		 * if (from.equals(linkTableName[0])) { if(to.equals(linkTableName[0])){
		 * String query = "select " + columnName[0] + ", sum(weighted_sum) " +
		 * " from  " + linkTableName[0] + " where " + column + " in " +
		 * "(select  " + column + " from " + linkTableName[0] + inputs +
		 * " ) group by  " + columnName[0];
		 * 
		 * return new StringBuffer(query);
		 * 
		 * } else if(to.equals(linkTableName[1])){
		 * 
		 * String query = "select c." + columnName[1] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[1] +
		 * " as c, " + linkTableName[0] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[0] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[1];
		 * 
		 * return new StringBuffer(query); } else
		 * if(to.equals(linkTableName[2])){
		 * 
		 * String query = "select c." + columnName[2] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[2] +
		 * " as c, " + linkTableName[0] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[0] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[2];
		 * 
		 * return new StringBuffer(query); } }
		 * 
		 * 
		 * 
		 * if (from.equals(linkTableName[1])) { if(to.equals(linkTableName[1])){
		 * 
		 * String query = "select " + columnName[1] + ", sum(weighted_sum) " +
		 * " from  " + linkTableName[1] + " where " + column + " in " +
		 * "(select  " + column + " from " + linkTableName[1] + inputs +
		 * " ) group by  " + columnName[1];
		 * 
		 * return new StringBuffer(query);
		 * 
		 * } else if(to.equals(linkTableName[0])){
		 * 
		 * String query = "select c." + columnName[0] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[0] +
		 * " as c, " + linkTableName[1] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[1] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[0];
		 * 
		 * return new StringBuffer(query); }
		 * 
		 * else if(to.equals(linkTableName[2])){
		 * 
		 * String query = "select c." + columnName[2] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[2] +
		 * " as c, " + linkTableName[1] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[1] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[2]; return new StringBuffer(query); } }
		 * 
		 * 
		 * 
		 * if (from.equals(linkTableName[2])) { if(to.equals(linkTableName[2])){
		 * String query = "select " + columnName[2] + ", sum(weighted_sum) " +
		 * " from  " + linkTableName[2] + " where " + column + " in " +
		 * "(select  " + column + " from " + linkTableName[2] + inputs +
		 * " ) group by  " + columnName[2];
		 * 
		 * return new StringBuffer(query);
		 * 
		 * //select sum(weighted_sum) from p_r where p_id in (select p_id from
		 * p_r where r_id = 2) group by r_id; } else
		 * if(to.equals(linkTableName[0])){
		 * 
		 * String query = "select c." + columnName[0] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[0] +
		 * " as c, " + linkTableName[2] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[2] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[0];
		 * 
		 * return new StringBuffer(query); } else
		 * if(to.equals(linkTableName[1])){
		 * 
		 * 
		 * String query = "select c." + columnName[1] +
		 * " , g.weighted_sum*c.weighted_sum " + " from " + linkTableName[1] +
		 * " as c, " + linkTableName[2] + " as g " + " where g." + column +
		 * " in (select " + column + " from " + linkTableName[2] + inputs +
		 * " ) and c." + column + " = g." + column + "  group by g." + column +
		 * " ,c." + columnName[1];
		 * 
		 * return new StringBuffer(query); } }
		 * 
		 * }
		 */
		// return an empty string
		return new StringBuffer("");

	}

	/**
	 * Generic looper function Used to loop through the results for the
	 * connection between the panels Then calls the increment function to update
	 * the panel
	 * 
	 * @param hierarchy
	 *            - the hierarchy that we are looking at
	 * @param query
	 *            - the query to be executed
	 */
	public void looper(Hierarchy hierarchy, String query) {

		// if the hierarchy is not visible then don't query it
		if (hierarchy.isVisible()) {

			if (query.equals("")) {
				// Custom_Messages.display_error(m_view, "Incorrect Count",
				// "An incorrect count has been used and no query was selected");
			}

			else {

				// Get the results of the query
				ResultSet data = m_model.getMyQuery(query);

				// The hashmap for this hierarchy
				HashMap<String, Integer> map = hierarchy.getMap();

				// Get the inner panel that contains the elements
				JPanel panel = hierarchy.getInnerhierarchy();
				String idpre = hierarchy.getId() + "00";
				// loop through the results
				try {
					while (data.next()) {
						// get the result
						String id = data.getString(1);
						id = idpre + id;

						// if it is not a zero result and is contained in the
						// map
						if (map.containsKey(id)) {
							// find the index for this unique ID
							int index = map.get(id);
							// increment the count
							increment_count(
									(AMLabel) panel.getComponent(index),
									data.getDouble(2), panel, map, false);
						}
					}
//					data.last();
//					System.out.println("Records num: " + data.getRow());

				} catch (SQLException e) {
					Custom_Messages
							.display_error(m_view, "ERROR",
									"An error has occured looping through the connection between the panels");
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("Found exception! --Qiang");
					e.printStackTrace();
				}
			}
		}

		hierarchy.getInnerhierarchy().repaint();
	}

	/**
	 * Increment the sub_count of a bar
	 * 
	 * @param label
	 *            - The label we want to increment
	 * @param value
	 *            - The value we want the bar to be set to
	 * @param panel
	 *            - The panel that it belongs to
	 * @param map
	 *            - The hashmap that it is contained within
	 * @param colour_only
	 *            - If we only want to colour the parents and not increment the
	 *            bar
	 */
	public void increment_count(AMLabel label, Double value, JPanel panel,
			HashMap<String, Integer> map, boolean colour_only) {

		try {
			// Get the labels bar
			AMLabel bar = label.getIts_bar();

			// now it is set to counted

			String bar_type = (String) m_view.getBar_options()
					.getSelectedItem();
			if (bar_type.equals("numerical")) {
				label.setCounted(true);
			}

			if (!colour_only) {

				// set the count
				// value = Math.round(value * 100) / 100.0d;
				bar.setSub_count(bar.getSub_count() + value);
			}

			// if this label has a parent, then we need to do the same thing
			if (label.isHas_parent()) {
				// gets its index from the hasmap
				Integer index = map.get(label.getParent_id());

				// get the parent label
				AMLabel parent = (AMLabel) panel.getComponent(index);

				// recursively call this method again with this parent,
				// incrementing its bar until we have no more parents in the
				// tree
				increment_count(parent, value, panel, map, colour_only);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the correct query for the codes
	 * 
	 * @param label
	 *            - the label that was interacted with
	 * @param index
	 *            - index for the correct tableName
	 * @return - the query to be executed
	 */
	public String alpha_code(AMLabel label, int index) {

		index--;

		// get the code
		String alpha_code = label.getUniqueID();
		alpha_code = alpha_code.substring(3);
		String query = "";

		// changed by qiang
		if (tableLevel[index] == label.getLevel())
			query = "SELECT idhierarchy FROM " + tableName[index]
					+ " WHERE idhierarchy = " + alpha_code + " ";
		else
			query = "SELECT idhierarchy FROM " + tableName[index]
					+ " WHERE parentid = " + alpha_code + " ";

		// compact it --qiang
		// if (tableLevel[index] == 1) {
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE idhierarchy = " + alpha_code + " ";
		// }
		//
		// else if (tableLevel[index] == 2) {
		//
		// if (label.getLevel() == 1)
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE parentid = " + alpha_code + " ";
		//
		// else if (label.getLevel() >= 2)
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE idhierarchy = " + alpha_code + " ";
		// }
		//
		// else if (tableLevel[index] == 3) {
		//
		// if (label.getLevel() == 1)
		// // query = "SELECT idhierarchy FROM " + tableName[index] +
		// // " WHERE parentid in (select idhierarchy from " + tableName[index]
		// // +
		// // " where parentid = " +alpha_code+ ") ";
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE parentid = " + alpha_code + " ";
		// else if (label.getLevel() == 2)
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE parentid = " + alpha_code + " ";
		//
		// else if (label.getLevel() == 3)
		// query = "SELECT idhierarchy FROM " + tableName[index]
		// + " WHERE idhierarchy = " + alpha_code + " ";
		// }
		return query;
	}

	/**
	 * Update the black bars after the count type has been changed for the top
	 * level
	 * 
	 * @param hierarchy
	 *            The hierarchy to be updated
	 */

	public void recount_top(Hierarchy hierarchy) {

		// Specify which level this is
		int level = 1;

		// Get the hashmap for this hierarchy
		HashMap<String, Integer> map = hierarchy.getMap();

		int t_index = hierarchy.getId() - 1;

		// First get all of the top level FOR codes
		ResultSet data = m_model.getTopLevel(tableName[t_index]);

		// Loop through the results
		try {

			while (data.next()) {

				// Get the data
				String alpha_code = data.getString(1);

				// adding the substring 100/200/300.. depending on the
				// hierarchy_num to make
				// indivdual idhierarchy unique
				alpha_code = hierarchy.getId() + "00" + alpha_code;

				if (map.containsKey(alpha_code)) {

					if (level < tableLevel[t_index]) {
						// now go and get the results
						float count = recount_middle(hierarchy, level + 1,
								alpha_code, map);
						Integer index = map.get(alpha_code);
						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
								.getComponent(index);

						// edit the bar now we have the count
						m_view.bar_edit(count, hierarchy, label);
					}

					else {

						// String string_count = alpha_count(alpha_code,
						// t_index);
						// double count = Double.parseDouble(string_count);

						double count = alpha_count(alpha_code, t_index);

						// edit the bar now
						Integer index = map.get(alpha_code);

						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
								.getComponent(index);

						m_view.bar_edit(count, hierarchy, label);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Custom_Messages.display_error(m_view, tableName[0],
					"An error has occured trying to get the data for the top level of the "
							+ tableName[0]);
			e.printStackTrace();
		}
	}

	/**
	 * Count the middle level hierarchy. If more levels exist, function is
	 * called recursively
	 * 
	 * @param hierarchy
	 *            - the hierarchy that they reside in
	 * @param level
	 *            - the level for the table
	 * @param parent
	 *            - the parent for the for codes
	 * @param map
	 *            - the Hashmap
	 * @return - the number of elements counted
	 */
	public float recount_middle(Hierarchy hierarchy, int level, String parent,
			HashMap<String, Integer> map) {

		float summary = 0;

		int t_index = hierarchy.getId() - 1;

		// stripping of the characters 100/200/300 that were added in the code
		// for the next query
		// storing it in a temporary variable parent
		parent = parent.substring(3);

		// First get all of the middle level FOR codes
		ResultSet data = m_model.getMiddleLevel(tableName[t_index], parent);

		// Loop through the results
		try {

			while (data.next()) {

				// get the data
				String alpha_code = data.getString(1);

				// adding the substring 100/200/300.. depending on the
				// hierarchy_num to make
				// indivdual idhierarchy unique
				alpha_code = hierarchy.getId() + "00" + alpha_code;

				if (map.containsKey(alpha_code)) {

					if (level < tableLevel[t_index]) {
						// now go and get the results
						float count = recount_middle(hierarchy, level + 1,
								alpha_code, map);

						Integer index = map.get(alpha_code);
						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
								.getComponent(index);

						// edit the bar now we have the count
						m_view.bar_edit(count, hierarchy, label);

						summary += count;
					} else {

						// String string_count = alpha_count(alpha_code,
						// t_index);
						// double count = Double.parseDouble(string_count);

						double count = alpha_count(alpha_code, t_index);

						// edit the bar now
						Integer index = map.get(alpha_code);

						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
								.getComponent(index);

						m_view.bar_edit(count, hierarchy, label);
						summary += count;
					}
				}
			}

			return summary;

		} catch (SQLException e) {
			Custom_Messages.display_error(m_view, tableName[0],
					"An error has occured get the middle level of the "
							+ tableName[0]);
			e.printStackTrace();
		}
		return summary;
	}

	/* CHANGE THE COUNTER FUNCTIONS */

	/**
	 * Update the counter / bar on all of the panels This occurs when an event
	 * is fired that changes the type of count As this is global all of the
	 * counts will be the same
	 * 
	 * @param option
	 *            - The option provided by the user
	 */
	public void count(String option) {

		try {

			// Make sure that we don;t double run this
			if (!count_type.equals(option)) {

				// set the count
				count_type = option;
				count_type = count_type + "_linking";

				String query1 = "select * from atom where name = '"
						+ count_type + "'";
				ResultSet rs = m_model.getMyQuery(query1);
				rs.next();
				column = rs.getString(3);

				query1 = "select * from " + count_type;
				rs = m_model.getMyQuery(query1);

				rs.last();
				int rowCount = rs.getRow();

				rs.beforeFirst();
				int i = 0;

				linkTableName = new String[rowCount];
				columnName = new String[rowCount];

				while (rs.next()) {
					linkTableName[i] = rs.getString(2);
					columnName[i] = rs.getString(3);
					i++;
				}

				// Panel used to store the elements
				Hierarchy hierarchy;

				// start the transparent glass
				m_view.startGlass(true);

				m_view.incrementWaiter(0);

				// count the globals here

				largest(0);
				largest(1);
				largest(2);

				m_view.incrementWaiter(25);

				/* edit the for codes count */
				hierarchy = m_view.decide_hierarchy(alpha_code);
				recount_top(hierarchy);

				m_view.incrementWaiter(50);

				/* edit the themes count */
				hierarchy = m_view.decide_hierarchy(beta_code);
				recount_top(hierarchy);

				m_view.incrementWaiter(75);

				/* edit the staff count */
				hierarchy = m_view.decide_hierarchy(gamma_code);
				recount_top(hierarchy);

				// we have no finished
				m_view.incrementWaiter(100);

				// set the cursor back
				m_view.setWaitCursor(false);

				// remove the glass so they can interact again
				m_view.endGlass(true);
			}
		}

		catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/*
	 * INNER CLASSES USED AS LISTENERS FOR THE OBJECTS IN THE INTERFACE
	 */

	/**
	 * The mouse listener that controls the mouse clicking and hovering
	 * 
	 * @author Anthony Scata
	 * @version 2.1
	 * 
	 */
	class Interaction_Mouse_Listener extends MouseAdapter {

		/**
		 * Event when the mouse is pressed
		 */
		public void mousePressed(MouseEvent e) {

			// interact with the label that was interacted with
			interaction(false, true, false, (AMLabel) e.getComponent());
		}

		/**
		 * When the mouse button is released No events required
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/**
		 * Hover events When a mouse moves over a new object that can be
		 * interacted with
		 */
		public void mouseEntered(MouseEvent e) {

			// if the control button is down then no interaction will take place
			// this stops events from firing when the user is moving across the
			// screen
			if (!e.isControlDown()) {

				// now perform the interaction
				interaction(true, true, false, (AMLabel) e.getComponent());
			}
		}

		/**
		 * When the mouse leaves an object For now this is turned off
		 */
		public void mouseExited(MouseEvent e) {

			// if the control button is down the we don't want to perform any
			// actions
			if (!e.isControlDown()) {

				// get the label that was interacted with
				AMLabel label = (AMLabel) e.getComponent();

				// only perform an exit operation in an image that controls the
				// collapsing and expanding
				// otherwise do nothing
				if (label.isIs_image()) {

					/* SLEEP SO WE CAN FIND SOME NEW INFORMATION */
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
					}

					// get the labels x and y positions
					int label_x = e.getXOnScreen();
					int label_y = e.getYOnScreen();

					// Now get the mouse x and y positions
					PointerInfo pointerInfo = MouseInfo.getPointerInfo();
					int mouse_x = pointerInfo.getLocation().x;
					int mouse_y = pointerInfo.getLocation().y;

					// Check to see if the mouse has moved left
					boolean left_up = false;
					if (label_x - 3 > mouse_x) {
						left_up = true;
					}

					// Check to see if the mouse has moved up
					if (label_y > mouse_y) {
						left_up = true;
					}

					// on a mouse interaction if the mouse has moved up or left
					// on exit then collapse the element, this is a mouse
					// gesture
					interaction(true, false, left_up, label);

				} else {
					interaction(true, false, false, label);
				}
			}
		}

		/**
		 * Event for when the mouse is clicked??other buttons Not required yet
		 */
		public void mouseClicked(MouseEvent e) {
		}
	}

	/**
	 * The clear button action listener Perform the correct clear action when a
	 * button is pressed Only clear the panel corresponding to the button
	 * 
	 * @author Anthony Scata
	 * @version 2.0
	 * 
	 */
	public class Clear_Button_Action implements ActionListener {

		/**
		 * Perform the action when the button is clicked
		 */
		public void actionPerformed(ActionEvent ae) {

			// go and clear all of the panels
			m_view.clear_button_event((JButton) ae.getSource());
		}
	}

	/**
	 * Listen to the keyboard and if a button is pressed then perform an action
	 * This deals with the search box and making sure action are performed when
	 * keys are typed Also we have the ability to move the panel up and down
	 * 
	 * @author Anthoy Scata
	 * @version 3.1
	 * 
	 */
	public class KeyPress_Action implements KeyListener {

		/**
		 * Handle the key typed event from the text field.
		 * */
		public void keyTyped(KeyEvent e) {
		}

		/**
		 * Handle the key-pressed event from the text field.
		 * */
		public void keyPressed(KeyEvent e) {
			perform_action(e);
		}

		/**
		 * Handle the key-released event from the text field.
		 * */
		public void keyReleased(KeyEvent e) {
			perform_action(e);
		}

		/**
		 * Perform the action after the event has been fired
		 * 
		 * @param e
		 *            - the key that was pressed
		 */
		private void perform_action(KeyEvent e) {

			// get the text field that fired the event
			JTextField in = (JTextField) e.getSource();

			// Get the text
			String s = in.getText();

			Hierarchy hierarchy = null;

			// Get the objects depending on what text box was interacted with
			if (e.getSource().equals(m_view.getHierarchy1().getSearchText())) {
				hierarchy = m_view.getHierarchy1();
			} else if (e.getSource().equals(
					m_view.getHierarchy2().getSearchText())) {
				hierarchy = m_view.getHierarchy2();
			} else if (e.getSource().equals(
					m_view.getHierarchy3().getSearchText())) {
				hierarchy = m_view.getHierarchy3();
			}

			// if we have pressed on of the up or down buttons on the keypad
			// We then want to move the panel
			if (e.getKeyCode() == 38 || e.getKeyCode() == 40
					|| e.getKeyCode() == 37 || e.getKeyCode() == 39
					|| e.isControlDown()) {

				Hierarchy tmp_hierarchy = null;
				JPanel panel = null;

				// Move a specific panel depending on the mouse position

				// Get the mouse current location on screen
				Point mouse_location = MouseInfo.getPointerInfo().getLocation();

				// Now decide the places where we will move
				int point_a = m_view.getMain_panel().getComponent(0).getWidth() + 5;
				int point_b = (m_view.getMain_panel().getComponent(2)
						.getWidth() + 5)
						+ point_a;

				// If their is nothing in the search box that is focused
				// Then decide which panel will be scrolled
				if (s.length() <= 0) {
					if (mouse_location.x < point_a) {
						tmp_hierarchy = ((Hierarchy) m_view.getMain_panel()
								.getComponent(0));
					} else if (mouse_location.x < point_b) {
						tmp_hierarchy = ((Hierarchy) m_view.getMain_panel()
								.getComponent(1));
					} else {
						tmp_hierarchy = ((Hierarchy) m_view.getMain_panel()
								.getComponent(2));
					}
				}

				panel = tmp_hierarchy.getInnerhierarchy();

				// This is an up button, move up
				if (e.getKeyCode() == 38) {

					JViewport viewport = (JViewport) panel.getParent();
					Rectangle r = viewport.getViewRect();
					int inc = hierarchy.getUnitInc();
					Point p = r.getLocation();
					p.y = (p.y - inc >= 0) ? p.y - inc : 0;
					viewport.setViewPosition(p);

				} else if (e.getKeyCode() == 40) {

					// This is a down button, move down
					JViewport viewport = (JViewport) panel.getParent();
					Rectangle r = viewport.getViewRect();
					int inc = hierarchy.getUnitInc();
					Point p = r.getLocation();
					int maxY = viewport.getView().getHeight() - r.height;
					p.y = (p.y + inc <= maxY) ? p.y + inc : maxY;
					viewport.setViewPosition(p);

				}

			} else {

				// make sure the event doesn't fire when we are moving with
				// control down
				if (!e.isControlDown()) {
					// search(hierarchy, s);
				}
			}
		}
	}

	/**
	 * This listens to the combo box's and performs an action
	 * 
	 * @author Anthony Scata
	 * @version 3.0
	 * 
	 */
	public class ComboBox_Action implements ActionListener {

		/**
		 * When the combo box is changed Update the way the bars are counted
		 */
		public void actionPerformed(ActionEvent e) {

			// get the combo box
			JComboBox combobox = (JComboBox) e.getSource();

			// get the name of the element that was clicked
			String name = (String) combobox.getSelectedItem();

			if (combobox.equals(m_view.getBar_options())) {

				// Set the bar
				m_view.setBar_options(name);

			} else if (combobox.equals(m_view.getCount_options())) {

				if (name.equals("NEW")) {
					// if we have decided new, show the window
					m_query.setVisible(true);
				} else {

					try {
						// set the wait cursor
						m_view.setWaitCursor(true);

						// otherwise change the count
						count(name);
					} finally {

						// set the cursor back even if a problem occurs
						m_view.setWaitCursor(false);
					}
				}
			}

			else if (combobox.equals(m_view.getLook_options())) {

				m_view.lookANDfeel(combobox.getSelectedIndex());

			} else if (combobox.equals(m_view.getConnection_options())) {

				connection_type = name;
			}

			// set the focusable off for the combo boxes
			m_view.getBar_options().setFocusable(false);
			m_view.getLook_options().setFocusable(false);
			m_view.getConnection_options().setFocusable(false);
		}
	}

	/**
	 * This method is called whenever the value of a scrollbar is changed,
	 * either by the user or automatically.
	 * 
	 * @author Anthony Scata
	 */
	public class Scroll_adjust_listener implements AdjustmentListener {

		public void adjustmentValueChanged(AdjustmentEvent evt) {

			// If the scroll bar is moving then place the glassbox on top to
			// stop interaction
			if (evt.getValueIsAdjusting()) {
				// The scroll bar moving
				m_view.startGlass(false);
			} else {
				// the scroll bar has stopped so remove the glass
				m_view.endGlass(false);
			}
		}
	}
}