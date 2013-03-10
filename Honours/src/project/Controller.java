package project;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import utilities.Custom_Messages;
import connector.Java_Connector;
import extensions.AMLabel;
import extensions.AMPanel;
import extensions.Frame_Info;
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
	 * the info window
	 * 
	 * @author Qiang Liu
	 */
	public Frame_Info m_info = new Frame_Info();

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
	// private String connection_type;

	/* INTERACTION OBJECTS */

	/* Used for knowing where they are in the display */


	/**
	 * Store main column name to fetch from the database
	 */
	private String column;

	/**
	 * store current intersection of ids
	 * 
	 * @author Qiang Liu
	 */
	private HashSet<Integer> incIds = new HashSet<Integer>();
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

	/**
	 * for compute non-weighted values
	 */

	HashMap<AMLabel, HashSet<Integer>> labelIds = new HashMap<AMLabel, HashSet<Integer>>();
	
	/**
	 * for floating window
	 */
	MouseMotionListener_For_FloatingWin ml_floating = new MouseMotionListener_For_FloatingWin();
	
	Interaction_Mouse_Listener lbl_mouselistener = new Interaction_Mouse_Listener();

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

		// connection_type = (String) m_view.getConnection_options()
		// .getSelectedItem();

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
			query1 = "select * from atom where t_name = '"
					+ count_type.toLowerCase() + "'";
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

		/**
		 * Add some new listeners
		 */
		
		for (Hierarchy h : m_view.hierarchies) {
			//add listeners
			h.isWeighted.addActionListener(new cbx_isWeighted_Listener(h));
			h.txt_search.getDocument().addDocumentListener(new Txt_search_Listener(h));
			h.btn_clearTable.addActionListener(new Btn_clear_Listener(h));
			h.btn_collapseAll.addActionListener(new btn_collapseAll_Listener(h));
			h.searchingOpts.addMouseListener(new SelectionTableMouseAdapter(h));
			
			Hierarchy_Mouse_Listener hml = new Hierarchy_Mouse_Listener(h);
			h.getInnerhierarchy().addMouseListener(hml);
			
			//pass controller to hierarchy -- need modify later to split controller and UI
			h.m_controller = this;
			
			//Set default value for the Hierarchy UI
			h.getHierarchy().setDividerSize(5);
			h.getHierarchy().setDividerLocation(0.8d);
		}
		
		//add listener for floating window
		m_view.addMouseMotionListener(ml_floating);
		m_view.btn_showInfo.addMouseMotionListener(ml_floating);
		m_view.topPanel.addMouseMotionListener(ml_floating);
		m_view.lbl_floating.addMouseMotionListener(ml_floating);
		for (Hierarchy h : m_view.hierarchies) {
			h.addMouseMotionListener(ml_floating);
			h.getInnerhierarchy().addMouseMotionListener(ml_floating);
			h.pane_proportion.addMouseMotionListener(ml_floating);
			h.btn_collapseAll.addMouseMotionListener(ml_floating);
			h.m_slider.addMouseMotionListener(ml_floating);
			h.searchingOpts.addMouseMotionListener(ml_floating);
			h.txt_search.addMouseMotionListener(ml_floating);
			h.padding_label.addMouseMotionListener(ml_floating);
			h.cbx_visible.addMouseMotionListener(ml_floating);
			h.hierarchy.addMouseMotionListener(ml_floating);
			h.hierarchy_options.addMouseMotionListener(ml_floating);
			h.hierarchyScroll.addMouseMotionListener(ml_floating);
			h.panel_controller.addMouseMotionListener(ml_floating);
			h.panel_heading.addMouseMotionListener(ml_floating);
			h.panelHeading.addMouseMotionListener(ml_floating);
			h.searchingOpts.addMouseMotionListener(ml_floating);
			h.isWeighted.addMouseMotionListener(ml_floating);
		}

		m_info.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		m_info.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				m_info.setVisible(false);
				m_view.is_showInfo=false;
				m_view.btn_showInfo.setText("Show element details");				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
	
		});
		
		this.m_view.btn_showInfo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				if(m_view.is_showInfo){
					m_view.is_showInfo=false;
					m_view.btn_showInfo.setText("Show element details");
				}
				else{
					m_view.is_showInfo=true;
					m_view.btn_showInfo.setText("Hide element details");
				}		
				m_info.setVisible(m_view.is_showInfo);

			}

		});

		// make sure that we re pack the view because it will have changed now
		// all of the elements have been loaded
		m_view.pack();
	}

	public class cbx_isWeighted_Listener implements ActionListener {

		Hierarchy owner;

		public cbx_isWeighted_Listener(Hierarchy h) {
			super();
			owner = h;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Hierarchy h=owner;
			h.searchingItems.clear();
			h.refreshSearchingTable();			
			Alpha_table(m_view.hierarchies.indexOf(h)+1);
			collapseAll(h);
			h.m_slider.c_min=0;
			h.m_slider.c_max=100;
			h.m_slider.repaint();
//			double t_min = h.m_slider.getScaleMin();
//			double t_max = h.m_slider.getScaleMax();
//			h.setScaleRange(t_min, t_max);
//			h.repaint();
			h.setScaleRange(0, h.getLargest_top());
			h.repaint();
			h.getInnerhierarchy().repaint();
			perform_connection();			
		}
		
	}
	
	public class Btn_clear_Listener implements ActionListener {

		Hierarchy btn_owner;

		public Btn_clear_Listener(Hierarchy h) {
			super();
			btn_owner = h;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Hierarchy.btn_clear_clicked(btn_owner);
			perform_connection();
		}

	}
	
	public class btn_collapseAll_Listener implements ActionListener {

		Hierarchy owner;
		public btn_collapseAll_Listener(Hierarchy h){
			super();
			owner = h;
		}
		
		@Override
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == owner.btn_collapseAll){
				collapseAll(owner);
			}
		}
		
	}
	
	public class Txt_search_Listener implements DocumentListener {
	
		Hierarchy owner;
		public Txt_search_Listener(Hierarchy h){
			super();
			owner = h;
		}
		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}
	
		@Override
		public void insertUpdate(DocumentEvent arg0) {
			searchByKeyword(owner);
		}
	
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			searchByKeyword(owner);
		}
	
	}

	class SelectionTableMouseAdapter extends MouseAdapter{
		Hierarchy owner;
		public SelectionTableMouseAdapter(Hierarchy h){
			super();
			owner = h;
		}
		public void mouseClicked(MouseEvent e) {
			int row = owner.searchingOpts.getSelectedRow();
			if (owner.searchingOpts.getSelectedColumn() == 0) {
				owner.checkboxClicked(row);
				perform_connection();
			}
		}
	}
	
	public void collapseAll(Hierarchy h){
		HashMap<String, Integer> map = h.getMap();
		AMPanel panel = h.getInnerhierarchy();
		for(Integer s : map.values()){
			AMLabel lbl = (AMLabel) panel.getComponent(s);
			lbl.is_expanded=false;
			if(lbl.getLevel()==1){
				lbl.setVisible(true);
				lbl.getIts_bar().setVisible(true);
				lbl.getIts_image().setVisible(true);
			}
			else {
				lbl.setVisible(false);
				lbl.getIts_bar().setVisible(false);
				lbl.getIts_image().setVisible(false);
			}
		}
		panel.setVisible(false);
		panel.setVisible(true);
	}
	
	public void searchByKeyword(Hierarchy h){
		String keyword = h.txt_search.getText().trim();
		HashMap<String, Integer> map = h.getMap();
		AMPanel panel = h.getInnerhierarchy();
	
//		System.out.println(keyword);
		
		if(keyword.equals("")||keyword.equals("Input keyword for search:")){
			for(Integer s : map.values()){
				AMLabel lbl = (AMLabel) panel.getComponent(s);
				lbl.is_searchingResult=true;
				lbl.getIts_bar().is_searchingResult = true;
			}
		}
		else
		{	
			for(Integer s : map.values()){
				AMLabel lbl = (AMLabel) panel.getComponent(s);
				lbl.is_searchingResult=false;
				lbl.getIts_bar().is_searchingResult = false;

			}
			
			for(Integer i : map.values()){
				AMLabel lbl = (AMLabel) panel.getComponent(i);
				if(lbl.originalText.toLowerCase().contains(keyword.toLowerCase())){
					lbl.is_searchingResult=true;
					lbl.getIts_bar().is_searchingResult = true;

					AMLabel p = lbl;
					while(!p.isVisible()){						
						p = p.parent;
						if(p.isVisible()){
							p.is_searchingResult = true;
							p.getIts_bar().is_searchingResult = true;
							break;
						}
//						AMLabelInteractions(5,p);						
					}

				}
			}
			
		}
		//repaint: do not use repaint() to avoid bugs
		panel.setVisible(false);
		panel.setVisible(true);
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
			int x = 100 / m_view.hierarchies.size();
			int inc = x;

			for (int i = 0; i < m_view.hierarchies.size(); i++) {
				Alpha_table(i + 1);
				// m_view.hierarchies.get(i).setSliderBarListener();
				m_view.incrementWaiter(x += inc);
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
//		Clear_Button_Action clear_button = new Clear_Button_Action();

		// When a key is press in a search box
		KeyPress_Action keypress = new KeyPress_Action();

		// To interact with the combo box
		ComboBox_Action combo_box = new ComboBox_Action();

		// To control the scroll bar
		Scroll_adjust_listener listener = new Scroll_adjust_listener();

		// VIEW LISTENERS
		m_view.add_listeners(combo_box, listener, keypress);
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
			int level,boolean isLeaf, String parent, AMLabel par) {

		// Add the label to the visualisation and save the returned label
		// Send the hierarchy to which it belongs, the level, the name or
		// description and the FOR code
		AMLabel label = m_view.addLabel(hierarchy, level,isLeaf, name, id, parent);
		label.parent = par;
		if(par!=null)par.children.add(label);
		
		// Add the action listener to this label for interaction
		label.addMouseListener(lbl_mouselistener);
		label.getIts_bar().addMouseListener(lbl_mouselistener);
		label.getIts_image().addMouseListener(lbl_mouselistener);
		label.addMouseMotionListener(ml_floating);
		label.getIts_bar().addMouseMotionListener(ml_floating);
		label.getIts_image().addMouseMotionListener(ml_floating);

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
	public void Alpha_table(int hierarchy_num) {

		/**
		 * add some code for test --Qiang
		 */
//		System.out.println("Alpha_table : " + hierarchy_num + " N: "
//				+ tableName[hierarchy_num - 1]);

		// Specify which level this is
		int level = 1;

		// the count of the elements
		// float overal_count = 0;

		// the left padding to the label
		int left_padding = 15;

		// get the hierarchy that we will be adding it to
		Hierarchy hierarchy = m_view.decide_hierarchy(hierarchy_num);

		// get the index value used to fetch the correct tableName
		int index = hierarchy_num - 1;

		hierarchy.setMaxlevels(tableLevel[index]);
		
		// call the largest function
		largest(index);

		//deal with non-weighted values --qiang
		if(!hierarchy.isWeighted.isSelected()){			
			this.alpha_count_nonWeighted(0, index, 0, false, null);
			return;
		}
		// First get all of the top level values
		ResultSet data = m_model.getTopLevel(tableName[index]);
//		String query = "select * from " + tableName[index] + " where ParentID=0 order by Label";
//		ResultSet data = m_model.getMyQuery(query);

		if (hierarchy.isWeighted.isSelected()) {
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
					Integer x = hierarchy.getMap().get(alpha_code);
					AMLabel label;
					boolean isLeaf = data.getBoolean("isLeaf");
					if(x==null){						
						label = addLabel(alpha_code,
								data.getString("label"), hierarchy, level, isLeaf, "",null);
					}
					else{						
						label = (AMLabel) hierarchy.getInnerhierarchy().getComponent(x);
					}		 

					// now go and add the rest of the hierarchy to the
					// visualisation
					double sub_count = 0;

					if (!isLeaf) {
						// if there is another level, call function
						// alpha_codes_middle to fill the rest of the values
						sub_count = alpha_codes_middle(hierarchy, level + 1,
								alpha_code,label, left_padding + 15, index);
					} else {
						// if this the last level, call the count method
						// String string_sub_count = alpha_count(alpha_code,
						// index);
						// sub_count = Double.parseDouble(string_sub_count);
						sub_count = alpha_count(alpha_code, index);
						
						/**
						 * store count ids to labels
						 */
//						label.countIDs.clear();
						String sql = "SELECT * FROM  " + linkTableName[index]
								+ " WHERE " + columnName[index] + " = " + alpha_code.substring(3);
						ResultSet rr = m_model.getMyQuery(sql);
						while(rr.next()){
							label.countIDs.add(rr.getInt(2));
						}
						
					}

					// go an edit this labels bar
					m_view.bar_edit(sub_count, hierarchy, label);
//					System.out.println(label.getText()+"  --  "+sub_count);
					// the overall count needs to be updated
					// overal_count += sub_count;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Custom_Messages.display_error(m_view, tableName[0],
						"An error has occured trying to get the data for the top level of the "
								+ tableName[0]);
				e.printStackTrace();
			}
		}
		// return overal_count;
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
			String parent_id,AMLabel par, int left_padding, int index) {

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

				// add the label
				Integer x = hierarchy.getMap().get(alpha_code);
				AMLabel label;
				boolean isLeaf = data.getBoolean("isLeaf");
				if(x==null){
					label = addLabel(alpha_code, data.getString("label"),
							hierarchy, level,isLeaf, parent_id,par);					
				}
				else{
					label = (AMLabel) hierarchy.getInnerhierarchy().getComponent(x);
				}	
				// now go and add the rest of the hierarchy to the visualisation
				double sub_count;

				if (!isLeaf) {
					sub_count = alpha_codes_middle(hierarchy, level + 1,
							alpha_code,label, left_padding + 15, index);
					par.countIDs.addAll(label.countIDs);
				} else {
					// String string_sub_count = alpha_count(alpha_code, index);
					// sub_count = Double.parseDouble(string_sub_count);
					sub_count = alpha_count(alpha_code, index);
					/**
					 * store count ids to labels
					 */
//					label.countIDs.clear();
					String sql = "SELECT * FROM  " + linkTableName[index]
							+ " WHERE " + columnName[index] + " = " + alpha_code.substring(3);
					ResultSet rr = m_model.getMyQuery(sql);
					while(rr.next()){
						label.countIDs.add(rr.getInt(2));
					}
					par.countIDs.addAll(label.countIDs);
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

		// get the correct hierarchy
		Hierarchy hierachy = m_view.decide_hierarchy(index + 1);

		try {
			if (hierachy.isWeighted.isSelected()) {
				// Create a tmp table for optimization --Qiang
				/**
				 * Should use sum() query instead of loops.!!!
				 */
				m_model.exeMyQuery("Drop table tmpTable");
				String sql = "CREATE TABLE tmpTable AS (SELECT sum(weighted_sum) as t_sum,parentid FROM "
						+ linkTableName[index]
						+ " as t1 join "
						+ tableName[index]
						+ " as t2 on t2.idhierarchy=t1."
						+ columnName[index] + " group by parentid)";
				m_model.exeMyQuery(sql);

				// if level=1, then a single query is sufficient
				// selects weighted_sum for each value and finds the maximum
				// among
				// them
				if (tableLevel[index] == 1) {

					query = "select sum(weighted_sum) from "
							+ linkTableName[index] + " where "
							+ columnName[index] + " in (select idhierarchy "
							+ " from " + tableName[index] + " ) group by "
							+ columnName[index];


					ResultSet top_data = m_model.getMyQuery(query);

					while (top_data.next()) {
						float count = 0;
						count = top_data.getFloat(1);

						if (count > largest_top) {
							largest_top = count;
						}
					}
				}

				// if level=2, first find the parent, and for each parent find
				// the
				// max of sum(weighted_sum)
				else if (tableLevel[index] == 2) {

					String top_query = "select * from " + tableName[index]
							+ " where parentid = 0";
					ResultSet top_data = m_model.getMyQuery(top_query);
					// use select max from a tmp table --qiang
					String middle_query = "select sum(t_sum) from tmpTable where parentid in (";

					// Loop through the results to get parentids
					while (top_data.next()) {
						// get the columns we need
						middle_query += top_data.getString(1) + ",";
					}
					middle_query = middle_query.substring(0,
							middle_query.length() - 1)
							+ ") group by parentid";
					ResultSet middle_sum = m_model.getMyQuery(middle_query);
					float count = 0;
					while (middle_sum.next()) {
						count = middle_sum.getFloat(1);
						// finding the max value of all
						if (count > largest_top) {
							largest_top = count;
						}
					}
					middle_sum.close();
				}
				// if level=2, first find the parent, and for each parent find
				// the
				// child and for them find the max of sum(weighted_sum)
				else if (tableLevel[index] == 3) {

					String top_query = "select * from " + tableName[index]
							+ " where parentid = 0";
					ResultSet top_data = m_model.getMyQuery(top_query);

					// Loop through the results
					while (top_data.next()) {

						// get the columns we need
						String id = top_data.getString(1);

						String middle_query = "select * from "
								+ tableName[index] + " where parentid = " + id;
						ResultSet middle_data = m_model
								.getMyQuery(middle_query);

						// use select sum in tmp table --qiang
						String end_query = "select sum(t_sum) from tmpTable where parentid in (";
						// Loop through the results to get parentids
						while (middle_data.next()) {
							// get the columns we need
							end_query += middle_data.getString(1) + ",";
						}
						middle_data.close();
						end_query = end_query.substring(0,
								end_query.length() - 1) + ")";
						ResultSet end_sum = m_model.getMyQuery(end_query);
						float count = 0;
						while (end_sum.next()) {
							count = end_sum.getFloat(1);
							// finding the max value of all
							if (count > largest_top) {
								largest_top = count;
							}
						}
						end_sum.close();
					}

				}
			} else {
				int lvl = tableLevel[index];
				if (lvl == 1) {
					String sql = "select count(*) from " + linkTableName[index]
							+ " where " + columnName[index]
							+ " in (select idhierarchy " + " from "
							+ tableName[index] + " ) group by "
							+ columnName[index];
					ResultSet rs = m_model.getMyQuery(sql);
					while (rs.next()) {
						if (rs.getFloat(1) > largest_top)
							largest_top = rs.getFloat(1);
					}
					rs.close();
				}

				if (lvl > 1) {
					String sql = "select idhierarchy from " + tableName[index]
							+ " where parentid = 0";
					ResultSet rs = m_model.getMyQuery(sql);
					while (rs.next()) {
						int id = rs.getInt(1);
						sql = "select idhierarchy from " + tableName[index]
								+ " where parentid = " + id;
						for (int i = 0; i < lvl - 2; i++) {
							sql = "select distinct idhierarchy from "
									+ tableName[index] + " where parentid in ("
									+ sql + ") ";
						}
						String end_ids = "";
						ResultSet rs1 = m_model.getMyQuery(sql);
						while (rs1.next())
							end_ids += rs1.getString(1) + ",";
						rs1.close();
						if(end_ids.length()>0){
							end_ids = end_ids.substring(0, end_ids.length() - 1);
//							for(String s:this.tableName){
//								System.out.println(s);
//							}
//							for(String s:this.linkTableName){
//								System.out.println(s);
//							}
							
							sql = "select count(distinct " + column + ") from "
									+ linkTableName[index] + " where "
									+ columnName[index] + " in (" + end_ids + ")";
							rs1 = m_model.getMyQuery(sql);
							if (rs1.next()) {
								if (rs1.getFloat(1) > largest_top)
									largest_top = rs1.getFloat(1);
							}
						}						
						rs1.close();
					}
				}

			}

		} catch (SQLException e) {
			Custom_Messages.display_error(m_view, "Largest Code",
					"An error has occured getting the largest FOR code");
			e.printStackTrace();
		}

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

		id = id.substring(3);
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

	public HashSet<Integer> alpha_count_nonWeighted(int id, int index,int level,boolean isLeaf, AMLabel par){
		HashSet<Integer> ids=new HashSet<Integer>();
		if(isLeaf){
			String query = "SELECT " + column + " FROM  " + linkTableName[index]
					+ " WHERE " + columnName[index] + " = " + id;
			ResultSet rs = m_model.getMyQuery(query);
			try {
				while(rs.next()){
					ids.add(rs.getInt(1));
					par.countIDs.add(rs.getInt(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		else {
			Hierarchy hierarchy=m_view.hierarchies.get(index);
			int hierarchy_num = index+1;
			String lbl_str = hierarchy_num + "00" + id;
			ResultSet rs = m_model.getMiddleLevel(tableName[index], id+"");
			try {
				while(rs.next()){
					// get the data
					String alpha_code = rs.getString(1);

					alpha_code = hierarchy_num + "00" + alpha_code;

					Integer x = hierarchy.getMap().get(alpha_code);
					AMLabel label;
					if(x==null){
						label = addLabel(alpha_code, rs.getString("label"),
							hierarchy, level+1,rs.getBoolean("isLeaf"), lbl_str,par);
					}
					else{
						label = (AMLabel) hierarchy.getInnerhierarchy().getComponent(x);
					}

					HashSet<Integer> cids;

					double sub_count;
					
					cids = this.alpha_count_nonWeighted(rs.getInt(1), index, level+1,rs.getBoolean("isLeaf"), label);
					
					sub_count = cids.size();
					
					// edit the bar now we have the data
					m_view.bar_edit(sub_count, hierarchy, label);

					label.clearThisSet(false);
					
					ids.addAll(cids);
					if(par!=null)par.countIDs.addAll(cids);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ids;
		
	}
	/*
	 * INTERACTION FUNCTIONS
	 */

	
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
				if(index==null)return;
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
//	public String alpha_code(AMLabel label, int index) {
//
//		index--;
//
//		// get the code
//		String alpha_code = label.getUniqueID();
//		alpha_code = alpha_code.substring(3);
//		String query = "";
//		int c = tableLevel[index] - label.getLevel();
//
//		// changed by qiang
//		if (c == 0)
//			query = "SELECT idhierarchy FROM " + tableName[index]
//					+ " WHERE idhierarchy = " + alpha_code + " ";
//		// else if (c == 1)
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE parentid = " + alpha_code + " ";
//		else if (c > 0) {
//			query = alpha_code;
//			for (int i = 0; i < c; i++) {
//				query = "SELECT idhierarchy FROM " + tableName[index]
//						+ " WHERE parentid in (" + query + ") ";
//			}
//		}
//
//		// compact it --qiang
//		// if (tableLevel[index] == 1) {
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE idhierarchy = " + alpha_code + " ";
//		// }
//		//
//		// else if (tableLevel[index] == 2) {
//		//
//		// if (label.getLevel() == 1)
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE parentid = " + alpha_code + " ";
//		//
//		// else if (label.getLevel() >= 2)
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE idhierarchy = " + alpha_code + " ";
//		// }
//		//
//		// else if (tableLevel[index] == 3) {
//		//
//		// if (label.getLevel() == 1)
//		// // query = "SELECT idhierarchy FROM " + tableName[index] +
//		// // " WHERE parentid in (select idhierarchy from " + tableName[index]
//		// // +
//		// // " where parentid = " +alpha_code+ ") ";
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE parentid = " + alpha_code + " ";
//		// else if (label.getLevel() == 2)
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE parentid = " + alpha_code + " ";
//		//
//		// else if (label.getLevel() == 3)
//		// query = "SELECT idhierarchy FROM " + tableName[index]
//		// + " WHERE idhierarchy = " + alpha_code + " ";
//		// }
//		return query;
//	}

	/**
	 * Update the black bars after the count type has been changed for the top
	 * level
	 * 
	 * @param hierarchy
	 *            The hierarchy to be updated
	 */

//	public void recount_top(Hierarchy hierarchy) {
//
//		// Specify which level this is
//		int level = 1;
//
//		// Get the hashmap for this hierarchy
//		HashMap<String, Integer> map = hierarchy.getMap();
//
//		int t_index = hierarchy.getId() - 1;
//
//		// First get all of the top level FOR codes
//		ResultSet data = m_model.getTopLevel(tableName[t_index]);
//
//		// Loop through the results
//		try {
//
//			while (data.next()) {
//
//				// Get the data
//				String alpha_code = data.getString(1);
//
//				// adding the substring 100/200/300.. depending on the
//				// hierarchy_num to make
//				// indivdual idhierarchy unique
//				alpha_code = hierarchy.getId() + "00" + alpha_code;
//
//				if (map.containsKey(alpha_code)) {
//
//					if (level < tableLevel[t_index]) {
//						// now go and get the results
//						float count = recount_middle(hierarchy, level + 1,
//								alpha_code, map);
//						Integer index = map.get(alpha_code);
//						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
//								.getComponent(index);
//
//						// edit the bar now we have the count
//						m_view.bar_edit(count, hierarchy, label);
//					}
//
//					else {
//
//						// String string_count = alpha_count(alpha_code,
//						// t_index);
//						// double count = Double.parseDouble(string_count);
//
//						double count = alpha_count(alpha_code, t_index);
//
//						// edit the bar now
//						Integer index = map.get(alpha_code);
//
//						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
//								.getComponent(index);
//
//						m_view.bar_edit(count, hierarchy, label);
//					}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			Custom_Messages.display_error(m_view, tableName[0],
//					"An error has occured trying to get the data for the top level of the "
//							+ tableName[0]);
//			e.printStackTrace();
//		}
//	}

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
//	public float recount_middle(Hierarchy hierarchy, int level, String parent,
//			HashMap<String, Integer> map) {
//
//		float summary = 0;
//
//		int t_index = hierarchy.getId() - 1;
//
//		// stripping of the characters 100/200/300 that were added in the code
//		// for the next query
//		// storing it in a temporary variable parent
//		parent = parent.substring(3);
//
//		// First get all of the middle level FOR codes
//		ResultSet data = m_model.getMiddleLevel(tableName[t_index], parent);
//
//		// Loop through the results
//		try {
//
//			while (data.next()) {
//
//				// get the data
//				String alpha_code = data.getString(1);
//
//				// adding the substring 100/200/300.. depending on the
//				// hierarchy_num to make
//				// indivdual idhierarchy unique
//				alpha_code = hierarchy.getId() + "00" + alpha_code;
//
//				if (map.containsKey(alpha_code)) {
//
//					if (level < tableLevel[t_index]) {
//						// now go and get the results
//						float count = recount_middle(hierarchy, level + 1,
//								alpha_code, map);
//
//						Integer index = map.get(alpha_code);
//						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
//								.getComponent(index);
//
//						// edit the bar now we have the count
//						m_view.bar_edit(count, hierarchy, label);
//
//						summary += count;
//					} else {
//
//						// String string_count = alpha_count(alpha_code,
//						// t_index);
//						// double count = Double.parseDouble(string_count);
//
//						double count = alpha_count(alpha_code, t_index);
//
//						// edit the bar now
//						Integer index = map.get(alpha_code);
//
//						AMLabel label = (AMLabel) hierarchy.getInnerhierarchy()
//								.getComponent(index);
//
//						m_view.bar_edit(count, hierarchy, label);
//						summary += count;
//					}
//				}
//			}
//
//			return summary;
//
//		} catch (SQLException e) {
//			Custom_Messages.display_error(m_view, tableName[0],
//					"An error has occured get the middle level of the "
//							+ tableName[0]);
//			e.printStackTrace();
//		}
//		return summary;
//	}

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

				String query1 = "select * from atom where t_name = '"
						+ count_type + "'";
				ResultSet rs = m_model.getMyQuery(query1);
				rs.next();
				column = rs.getString(3);

				query1 = "select count(*) from "+ count_type;
				rs=m_model.getMyQuery(query1);
				int rowCount=0;
				if(rs.next())rowCount = rs.getInt(1);
				query1 = "select * from " + count_type;
				rs = m_model.getMyQuery(query1);

				int i = 0;

				linkTableName = new String[rowCount];
				columnName = new String[rowCount];

				while (rs.next()) {
					linkTableName[i] = rs.getString(2);
					columnName[i] = rs.getString(3);
					i++;
				}


				this.refresh_all();
				
				
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
	/**
	 * rewrite by Qiang
	 */
	class Interaction_Mouse_Listener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			AMLabel lbl = (AMLabel) e.getComponent();
			AMLabelInteractions(1, lbl);
		}

		public void mouseReleased(MouseEvent e) {
			AMLabel lbl = (AMLabel) e.getComponent();
			AMLabelInteractions(2, lbl);
		}

		public void mouseEntered(MouseEvent e) {
			AMLabel lbl = (AMLabel) e.getComponent();
			AMLabelInteractions(3, lbl);
		}

		public void mouseExited(MouseEvent e) {
			AMLabel lbl = (AMLabel) e.getComponent();
			AMLabelInteractions(4, lbl);
		}

		public void mouseClicked(MouseEvent e) {
			AMLabel lbl = (AMLabel) e.getComponent();

			if (e.getClickCount() == 1) {
				// left click
				if (SwingUtilities.isLeftMouseButton(e))
					AMLabelInteractions(5, lbl);
				// right click
				if (SwingUtilities.isRightMouseButton(e))
					AMLabelInteractions(6, lbl);
			}
			// double click
			if (e.getClickCount() == 2) {
				if (!(lbl.isIs_bar() || lbl.isIs_image())) {
					AMLabelInteractions(7, lbl);
				}
			}
		}

	}

	/**
	 * A new function to replace interaction() and perform_action()
	 * 
	 * @Author Qiang
	 */
	public void AMLabelInteractions(int status, AMLabel lbl) {
		// status: 1. mouse pressed 2.mouse released
		// 3. enter 4. exit 5 left clicked 6right clicked
		// 7. double clicked
		//Control img to be not selected
		boolean isIMG = lbl.isIs_image();
		boolean isBAR = lbl.isIs_bar();

		

		// image and text should be same action
		if (isIMG || isBAR)
			lbl = lbl.owner;
		
		String id = lbl.getUniqueID();
		Hierarchy hierarchy = m_view.findHierarchy(id);

		//deal with showing %
		if(status==3)hierarchy.SetDetailedLabel(true);
		if(status==4)hierarchy.SetDetailedLabel(false);
		
		// perform actions
		lbl.change_text(false);

		switch (status) {
		case 1: // press
			break;
		case 2: // release
			break;
		case 3: // enter
			
			if(!isIMG){
				hierarchy.addSearchingItem(lbl, 3);
				lbl.change_text(true);
				perform_connection();
			}
			break;
		case 4: // exit
			
			if(!isIMG){
				hierarchy.removeTempSearchingItem(lbl);
				perform_connection();
			}
			break;
		case 5: // left clicked
			if (lbl.is_expanded) {
				lbl.is_expanded = false;
			} else {
				lbl.is_expanded = true;
			}
			expand_collapse_label(hierarchy, lbl);

			//update searching result
			searchByKeyword(hierarchy);
			
			//updata proportion panel
			hierarchy.pane_proportion.clearAll();
			this.update_proportion(hierarchy);

			break;
		case 6: // right clicked
			int ls = 3;
			if (hierarchy.searchingItems.containsKey(lbl))
				ls = hierarchy.searchingItems.get(lbl);
			if (ls != 3)
				hierarchy.removeSearchingItem(lbl);
			else
				hierarchy.addSearchingItem(lbl, 1);

			hierarchy.getInnerhierarchy().setVisible(false);
			hierarchy.getInnerhierarchy().setVisible(true);

			break;
		case 7: // double clicked
			// showItemInfo(lbl);
			break;
		default:
			break;
		}

	}

	public void expand_collapse_label(Hierarchy hierarchy, AMLabel lbl) {
		for(AMLabel this_label:lbl.children){
			this_label.change(lbl.is_expanded);
			this_label.getIts_image().change(lbl.is_expanded);
			this_label.getIts_bar().change(lbl.is_expanded);

			if (!lbl.is_expanded && lbl != this_label) {
				this_label.is_expanded = false;
				this.expand_collapse_label(hierarchy, this_label);
			}
			
		}		
//		String query = "SELECT idhierarchy FROM "
//				+ tableName[hierarchy.getId() - 1] + " WHERE parentid = "
//				+ lbl.getUniqueID().substring(3) + " ";
//		ResultSet data = m_model.getMyQuery(query);
//		HashMap<String, Integer> position_map = hierarchy.getMap();
//		HashSet<Integer> list = new HashSet<Integer>();
//		try {
//			while (data.next()) {
//				ResultSetMetaData rsmd = data.getMetaData();
//				int NumOfCol = rsmd.getColumnCount();
//				for (int column_rows = 1; column_rows <= NumOfCol; column_rows++) {
//					String key = data.getString(1).toString();
//					key = hierarchy.getId() + "00" + key;
//					if (position_map.containsKey(key)) {
//						int index = position_map.get(key);
//						Integer tmp_index = new Integer(index);
//						list.add(tmp_index);
//					}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
//			Integer tindex = i.next();
//			AMLabel this_label = (AMLabel) hierarchy.getInnerhierarchy()
//					.getComponent(tindex);
//			this_label.change(lbl.is_expanded);
//			if (this_label.isHas_image())
//				this_label.getIts_image().change(lbl.is_expanded);
//			if (this_label.isHas_bar())
//				this_label.getIts_bar().change(lbl.is_expanded);
//			if (!lbl.is_expanded && lbl != this_label) {
//				this_label.is_expanded = false;
//				this.expand_collapse_label(hierarchy, this_label);
//			}
//		}
	}

	public ArrayList<String> intersectionOf(ArrayList<String> a,
			ArrayList<String> b) {
		ArrayList<String> r = new ArrayList<String>();
		for (String s : a) {
			if (b.contains(s))
				r.add(s);
		}
		return r;
	}

	public HashSet<Integer> intersectionOf(HashSet<Integer> a,
			HashSet<Integer> b) {
		HashSet<Integer> r = new HashSet<Integer>();
		for (int s : a) {
			if (b.contains(s))
				r.add(s);
		}
		return r;
	}

	public void refresh_all(){
		for(int i=0;i<m_view.hierarchies.size();i++){
			Hierarchy h=m_view.hierarchies.get(i);
			h.searchingItems.clear();
			h.refreshSearchingTable();
			this.Alpha_table(i+1);
			h.m_slider.c_min=0;
			h.m_slider.c_max=100;
			h.setScaleRange(0, h.getLargest_top());
			h.setVisible(false);
			h.setVisible(true);
		}
	}
	public void perform_connection() {
		// step 0:clear count, reserved ids, etc.
		for (Hierarchy h : m_view.hierarchies) {
			h.intersectionOfCountIds.clear();
			h.selectedItemIds.clear();
			h.totalWeightedValue.clear();
			h.clearCount();
			h.pane_proportion.clearAll();
		}
		// step 1: get the intersection of publictions and ids
		boolean has = false;
		HashSet<Integer> intersectionOfCountIds = new HashSet<Integer>();
		for (Hierarchy h : m_view.hierarchies) {
			if (h.isVisible() && !h.searchingItems.isEmpty()) {
				h.intersectionOfCountIds = getSearchingCountIdsForHierarchy(h,
						h.selectedItemIds);
				if (!has) {
					intersectionOfCountIds.addAll(h.intersectionOfCountIds);
					has = true;
				} else {
					intersectionOfCountIds = intersectionOf(
							intersectionOfCountIds, h.intersectionOfCountIds);
				}
			}
		}

		// store the ids
		this.incIds.clear();
		this.incIds.addAll(intersectionOfCountIds);
		this.updateInfo();

		// none intersection ids
		if (intersectionOfCountIds.isEmpty())
			return;
		// none selected item
		boolean allempty = true;
		for (Hierarchy h : m_view.hierarchies) {
			if (!h.searchingItems.isEmpty()) {
				allempty = false;
				break;
			}
		}
		if (allempty)
			return;

		// step 2: compute total weighted value for each hierarchy and each
		// count
		String idStr = "";
		if (!intersectionOfCountIds.isEmpty()) {
			idStr += " and " + this.column + " in (";
			for (Integer i : intersectionOfCountIds)
				idStr += i + ",";
			idStr = idStr.substring(0, idStr.length() - 1) + ")";
		}

		for (Hierarchy h : m_view.hierarchies) {
			if (h.isVisible() && !h.selectedItemIds.isEmpty())
				h.totalWeightedValue = getTotalWeightedValue(h, idStr,
						h.selectedItemIds);
		}

		// compute and display each entry
		for (Hierarchy h : m_view.hierarchies)
			if(h.isVisible())computeEntry(h, idStr, m_view.hierarchies);
		
		//update the proportion panel
		for (Hierarchy h : m_view.hierarchies)
			if(h.isVisible())this.update_proportion(h);
		
	}

	public void computeEntry(Hierarchy h, String idstr,
			ArrayList<Hierarchy> hlist) {
		int index = h.getId() - 1;
		String sql = "select " + this.column + "," + columnName[index]
				+ ",weighted_sum from " + linkTableName[index] + " where 1=1 "
				+ idstr;

		// The hashmap for this hierarchy
		HashMap<String, Integer> map = h.getMap();
		// Get the inner panel that contains the elements
		JPanel panel = h.getInnerhierarchy();
		String idpre = h.getId() + "00";

		ResultSet rs = m_model.getMyQuery(sql);

		this.labelIds.clear();
		try {
			while (rs.next()) {
				int cid = rs.getInt(1);
				String eid = rs.getString(2);
				double value;
				if (h.isWeighted.isSelected())
					value = rs.getDouble(3);
				else
					value = 1.0;

				for (Hierarchy t : hlist) {
					double x = 1.0;
					if (t != h) {
						if (t.totalWeightedValue.containsKey(cid))
							x = t.totalWeightedValue.get(cid);
						else
							x = 1.0;
						value = value * x;
					}
				}

				String lblid = idpre + eid;
				if (map.containsKey(lblid)) {
					// find the index for this unique ID
					int lblidx = map.get(lblid);
					// increment the count
					AMLabel lbl = (AMLabel) panel.getComponent(lblidx);
					if (h.isWeighted.isSelected())
						this.increment_count(lbl, value, panel, map, false);
					else
						increment_count_nonweighted(lbl, value, panel, map,
								false, cid);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void increment_count_nonweighted(AMLabel label, Double value,
			JPanel panel, HashMap<String, Integer> map, boolean colour_only,
			int cid) {

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
			if (label.isHas_parent()&&label.getParent_id().substring(3)!="0") {
				// gets its index from the hasmap
				Integer index = map.get(label.getParent_id());
				if(index==null)return;
				
				// get the parent label
				AMLabel parent = (AMLabel) panel.getComponent(index);

				if (labelIds.containsKey(parent)) {
					HashSet<Integer> ids = labelIds.get(parent);
					if (ids.contains(cid))
						return;
					else {
						ids.add(cid);
						labelIds.put(parent, ids);
						this.increment_count_nonweighted(parent, value, panel,
								map, colour_only, cid);
					}
				} else {
					HashSet<Integer> ids = new HashSet<Integer>();
					ids.add(cid);
					labelIds.put(parent, ids);
					this.increment_count_nonweighted(parent, value, panel, map,
							colour_only, cid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	// Base on the selections of Hierarchy entries,
	// get the searching string and selected entry ids
	public HashSet<Integer> getSearchingCountIdsForHierarchy(Hierarchy h,
			HashSet<Integer> itemids) {
		HashSet<Integer> cids = new HashSet<Integer>();

		for (Entry<AMLabel, Integer> item : h.searchingItems.entrySet()) {
			if(cids.size()==0)cids.addAll(item.getKey().countIDs);
			else cids = intersectionOf(cids,item.getKey().countIDs);
		}
		
//		ArrayList<String> lst = new ArrayList<String>();
//		ArrayList<HashSet<Integer>> rowids = new ArrayList<HashSet<Integer>>();
//		for (Entry<AMLabel, Integer> item : h.searchingItems.entrySet()) {
////			System.out.println(item.getKey().getText() + " : "
////					+ item.getValue());
//			if (item.getValue() != 0) {
//				AMLabel lbl = item.getKey();				
//				HashSet<Integer> row = new HashSet<Integer>();
//				rowids.add(row);				
//				String str = alpha_code(lbl, h.getId());
//				lst.add(str);
//				ResultSet trs = m_model.getMyQuery(str);
//				try {
//					while (trs.next()) {
//						itemids.add(trs.getInt(1));
//						row.add(trs.getInt(1));
//					}
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
////				}
//			}
//		}
//
//		String sql = "select " + this.column + " from "
//				+ linkTableName[h.getId() - 1] + " where 1=1 ";
//		boolean has = false;
//
//		for (HashSet<Integer> row : rowids) {
//			if (row.size() == 0)
//				continue;
//			String msql = sql + " and " + columnName[h.getId() - 1] + " in (";
//			for (int i : row) {
//				msql += i + ",";
//			}
//			msql = msql.substring(0, msql.length() - 1) + ")";
//			ResultSet mrs = m_model.getMyQuery(msql);
//			HashSet<Integer> rowcids = new HashSet<Integer>();
//			try {
//				while (mrs.next())
//					rowcids.add(mrs.getInt(1));
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if (has) {
//				cids = intersectionOf(cids, rowcids);
//			} else {
//				cids = rowcids;
//				has = true;
//			}
//		}

		return cids;
	}

	public HashMap<Integer, Double> getTotalWeightedValue(Hierarchy h,
			String idstr, HashSet<Integer> itemids) {
		HashMap<Integer, Double> r = new HashMap<Integer, Double>();
		int index = h.getId() - 1;
		// if it is not weighted then return. use 1.0 for the later computing.
		if (!h.isWeighted.isSelected())
			return r;

		// weighted
		String inputs = "where 1=1 ";
		if (idstr.equals(""))
			return r;
		else
			inputs += idstr;

		if (itemids.size() > 0) {
			inputs += " and " + this.columnName[index] + " in (";
			for (Integer i : itemids)
				inputs += i + ",";
			inputs = inputs.substring(0, inputs.length() - 1) + ")";
		} else
			return r;

//		System.out.println("idstr -- " + idstr);
//		System.out.println("My ip -- " + inputs);

		String sql = "select c." + column + ", sum(c.weighted_sum) "
				+ " from  " + linkTableName[index] + " as c " + inputs
				+ " group by  " + column;
		ResultSet rs = m_model.getMyQuery(sql);
		try {
			while (rs.next()) {
				r.put(rs.getInt(1), rs.getDouble(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;

	}


//	/**
//	 * The clear button action listener Perform the correct clear action when a
//	 * button is pressed Only clear the panel corresponding to the button
//	 * 
//	 * @author Anthony Scata
//	 * @version 2.0
//	 * 
//	 */
//	public class Clear_Button_Action implements ActionListener {
//
//		/**
//		 * Perform the action when the button is clicked
//		 */
//		public void actionPerformed(ActionEvent ae) {
//
//			// go and clear all of the panels
//			m_view.clear_button_event((JButton) ae.getSource());
//		}
//	}

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
			// if (e.getSource().equals(m_view.getHierarchy1().getSearchText()))
			// {
			// hierarchy = m_view.getHierarchy1();
			// } else if (e.getSource().equals(
			// m_view.getHierarchy2().getSearchText())) {
			// hierarchy = m_view.getHierarchy2();
			// } else if (e.getSource().equals(
			// m_view.getHierarchy3().getSearchText())) {
			// hierarchy = m_view.getHierarchy3();
			// }
			for (Hierarchy h : m_view.hierarchies) {
				if (e.getSource().equals(h.getSearchText())) {
					hierarchy = h;
					break;
				}
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

				// connection_type = name;
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

	/**
	 * This method is called when double clicked a label. It will display a
	 * popup window to show the detailed infomation of the item.
	 * 
	 * @author Qiang Liu
	 */

	public void updateInfo() {
		String ct = this.count_type.substring(0, count_type.length() - 8);
		String title = "Query results";
		
		//update result size for two place: top label & flowable pane near mouse cursor
		m_view.lbl_nResults.setText(incIds.size()+"");
		m_view.lbl_floating.setText(incIds.size()+"");
//		// not IMDB or carDB
//		if (!(ct.equalsIgnoreCase("films")||ct.equalsIgnoreCase("cars"))) {
//			String msg = "Number of " + ct + " : " + incIds.size() + "\nIDs : ";
//			for (int i : incIds)
//				msg += i + ", ";
//			msg = msg.substring(0, msg.length() - 1);
//			m_info.setValues(title, msg);
//			return;
//		}

		// IMDB or carDB
		if (this.incIds.isEmpty()) {
			m_info.setValues(title, "None!");
			return;
		}

		String sql = "select * from " + ct + " where " + this.column + " in (";
		for (int id : this.incIds) {
			sql += id + ",";
		}
		sql = sql.substring(0, sql.length() - 1) + ")";

		ResultSet rs = m_model.getMyQuery(sql);
		m_info.setValues(title + " : " + incIds.size(), rs);
	}
	
	class Hierarchy_Mouse_Listener extends MouseAdapter {
		
		Hierarchy h;
		
		public Hierarchy_Mouse_Listener(Hierarchy hir){
			h=hir;
		}
		public void mouseEntered(MouseEvent e) {
			h.SetDetailedLabel(true);
		}
		public void mouseExited(MouseEvent e) {
			h.SetDetailedLabel(false);
		}
	}
	
	class MouseMotionListener_For_FloatingWin implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			m_view.win_floating.setLocation(e.getXOnScreen()+10, e.getYOnScreen());
			
		}
		
	}
	
	public void update_proportion(Hierarchy h){
		HashMap<String, Integer> map = h.getMap();
		JPanel panel = h.getInnerhierarchy();
//		h.pane_proportion.clearAll();
		
		for(Integer s : map.values()){
			AMLabel lbl = (AMLabel) panel.getComponent(s);
			double v = lbl.getIts_bar().getSub_count()/lbl.getIts_bar().getCount();
			if(v<0.05&&v>0)v=0.05;
			int y = lbl.getLocation().y;
//			System.out.println("TTT:"+lbl.originalText+" :: "+lbl.isVisible()+" v:"+v+ " y:"+y);
			if(lbl.isVisible())h.pane_proportion.addPair(v, y);
		}
		h.pane_proportion.repaint();
	}
}