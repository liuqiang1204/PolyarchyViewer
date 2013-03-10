package project;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
//import project.Controller.Clear_Button_Action;
import project.Controller.ComboBox_Action;
import project.Controller.KeyPress_Action;
import project.Controller.Scroll_adjust_listener;
import selectors.selectableFonts;
import utilities.Custom_Messages;
import connector.Java_Connector;
import extensions.AMLabel;
import extensions.FixedGlassPane;
import extensions.Hierarchy;

/**
 * This class is the view This is what the user sees and can interact with All
 * of the objects that can be seen by the user are stored here Or at least are
 * extanisated here
 * 
 * @author Anthony Scata
 * @version 4.3
 * 
 */
public class View extends JFrame {

	/* VARIABLES */

	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = 1L;

	/* TOP LEVEL COMPONENTS */

	/**
	 * The split pane that is the top most panel
	 */
	// private JSplitPane window;

	/**
	 * The transparent glass that is sometimes on top of the window
	 */
	private FixedGlassPane glass;

	/* ELEMENTS INSIDE THE WINDOW */

	/**
	 * The pane with the hierarchies on the top
	 */
	private JPanel main_panel;

	/**
	 * The pane with some global options on the bottom
	 */
	private JPanel global_options;

	/**
	 * ComboBox with the options
	 */
	private JComboBox count_options;

	/**
	 * ComboBox with the options
	 */
	private JComboBox bar_options;

	/**
	 * ComboBox with the look options
	 */
	private JComboBox look_options;

	/**
	 * ComboBox with the type of connection
	 */
	private JComboBox connection_options;

	/* HIERARCHIES */
	/**
	 * Rewrite to support multiple hierarchies
	 * 
	 * @author Qiang Liu
	 */

	public ArrayList<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
	public ArrayList<Integer> zorders = new ArrayList<Integer>();
	public JToolBar topPanel;
	public JButton btn_showInfo;
	public boolean is_showInfo;
	public JLabel lbl_nResults;
	public JLabel lbl_foreColor;
	public JLabel lbl_backColor;
	public JLabel lbl_proportionBarColor;
	public JWindow win_floating;
	public JLabel lbl_floating;
	

	/**
	 * The right padding on all labels and panels
	 */
	private int default_left_padding = 0;

	/* VIS INFO */

	/**
	 * has the visualisation been loaded
	 */
	public static boolean loaded = false;

	/**
	 * counters to be used
	 */
	String[] count_options_available; // = new String[3];

	/**
	 * the look of the bar
	 */
	private String[] bar_options_available = { "numerical", "plain" };

	/**
	 * looks that can be used
	 */
	private String[] look_options_available = { "NimbusLookAndFeel",
			"MetalLookAndFeel", "WindowsLookAndFeel",
			"WindowsClassicLookAndFeel", "MotifLookAndFeel" };

	/**
	 * connection options to be used
	 */
	private String[] connection_options_available = { "OR", "AND" };

	/* DATABASE CONNECTOR */

	/**
	 * the object to be used to connect to the database, used here if we want to
	 * update data on the database
	 */
//	@SuppressWarnings("unused")
//	private Java_Connector m_model;

	/* UTILITIES */

	/**
	 * Object used to move the mouse cursor
	 */
	private Robot robot;

	
	
	/* CONSTRUCTOR */

	/**
	 * The constructor used for this class Sets up the whole display
	 * 
	 * @param model
	 *            - Takes the model containing the data. For this example it is
	 *            the database connector
	 */
	public View(Java_Connector model) {

		addContents(model);
		
		//init floating window		
		lbl_floating = new JLabel("0");		
		lbl_floating.setOpaque(true);		
		lbl_floating.setBackground(GlobalConstants.floating_Color);		
		lbl_floating.setPreferredSize(new Dimension(30,20));
		lbl_floating.setBorder(BorderFactory.createLineBorder(Color.black));
		lbl_floating.setHorizontalAlignment(JLabel.CENTER);
		
		win_floating = new JWindow();
		win_floating.setAlwaysOnTop(true);
		win_floating.add(lbl_floating);		
//		win_floating.setBackground(GlobalConstants.floating_Color);
		win_floating.pack();
		win_floating.setVisible(true);
	}

	/**
	 * Add the contents to the view
	 * 
	 * @param model
	 *            - the java database model
	 */
	public void addContents(Java_Connector model) {

		//Set window caption
		ResultSet rs = model.getMyQuery("select * from dbinfo");
		try {
			if(rs.next())this.setTitle("Polyarchy Viewer - "+rs.getString(2));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/* THE TOP MOST ELEMENT */

		// window = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		// window.setOneTouchExpandable(true);
		// window.setResizeWeight(0.98);

		// Add the panel to the frame
		// getContentPane().add(window, BorderLayout.CENTER);

		/* GLASS PANE EXTRAS */

		glass = new FixedGlassPane(getJMenuBar());
		setGlassPane(glass);

		/* ELEMENTS INSIDE THE WINDOW */

		// Create the main panel
		main_panel = new JPanel();

		// Create the global options panel
		global_options = new JPanel();
		global_options
				.setLayout(new BoxLayout(global_options, BoxLayout.Y_AXIS));
		
		//get the screen resolution
		Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		//set size
		global_options.setPreferredSize(new Dimension(screen_size.width, 25));

		// window.setRightComponent(global_options);

		JPanel interaction_options = new JPanel();
		interaction_options.setLayout(new GridBagLayout());

		/* HIERARCHY PANELS */

		String query = "select * from hierarchy";

		ResultSet main_table = model.getMyQuery(query);

		try {
			while (main_table.next()) {
				// change "XXX_hierarchy" to XXX
				String hd = main_table.getString(2);
				hd = hd.toLowerCase().replaceAll("_hierarchy", "");
				Hierarchy h = new Hierarchy(main_table.getInt(1), hd);
				h.cbx_visible = new JCheckBox(hd+"    ");
				h.cbx_visible.setToolTipText("Hide "+h.cbx_visible.getText().trim()+ " hierarchy");
				h.cbx_visible.setSelected(true);
				h.add_cbx_visible_actionListener();
				hierarchies.add(h);
				h.zorder = hierarchies.size();
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Custom_Messages
					.display_error(this, "Adding Hierarchy",
							"ERROR: An error has occurred adding the Hierarchy to the display");
		}

		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.X_AXIS));
		// window.setLeftComponent(main_panel);

		// The tool bar --qiang
		topPanel = new JToolBar();
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		btn_showInfo = new JButton("Show element details");
		btn_showInfo.setOpaque(true);
		is_showInfo = false;
		topPanel.add(this.btn_showInfo);
		
		lbl_nResults = new JLabel("");
		lbl_nResults.setPreferredSize(new Dimension(40,18));
		lbl_nResults.setText("0");
		lbl_nResults.setForeground(Color.yellow);
		lbl_nResults.setBackground(Color.black);
		lbl_nResults.setOpaque(true);
		lbl_nResults.setHorizontalAlignment(JLabel.CENTER);
		lbl_nResults.setToolTipText("Number of query results");
		topPanel.add(lbl_nResults);
		topPanel.addSeparator(new Dimension(10, 20));

		// add hierarchy panels
		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.NORTH);
		this.add(global_options, BorderLayout.SOUTH);
		this.add(main_panel, BorderLayout.CENTER);
		for (Hierarchy h : this.hierarchies) {
			main_panel.add(h);
			main_panel.setComponentZOrder(h, h.zorder - 1);
			topPanel.add(h.cbx_visible);
		}

		/* ELEMENTS INSIDE OF GLOBAL OPTIONS */

		/* Combobox */

		// options for the count

		// filling out the options of the count_type from the database
		try {
			query = "select displayName from atom";
			main_table = model.getMyQuery(query);

			main_table.last();
			int rowCount = main_table.getRow();
			count_options_available = new String[rowCount];

			main_table.beforeFirst();
			int i = 0;

			while (main_table.next()) {
				count_options_available[i] = main_table.getString(1);
				i++;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		count_options = new JComboBox(count_options_available);
		count_options.setSelectedIndex(0);

		// options for the bar
		bar_options = new JComboBox(bar_options_available);
		bar_options.setSelectedIndex(1);

		// the look of the view
		look_options = new JComboBox(look_options_available);
		look_options.setSelectedIndex(0);

		// the look of the view
		connection_options = new JComboBox(connection_options_available);
		connection_options.setSelectedIndex(0);

		// Add the boxes and some details
		interaction_options.add(new JLabel(" Count options: "));
		interaction_options.add(count_options);
		interaction_options.add(new JLabel(" Bar options: "));
		interaction_options.add(bar_options);
		interaction_options.add(new JLabel(" Look options: "));
		interaction_options.add(look_options);
		
		// init color items
		lbl_foreColor = new JLabel("          ");		
		lbl_foreColor.setOpaque(true);
		lbl_foreColor.setSize(40, 18);
		lbl_foreColor.setBorder(BorderFactory.createLineBorder(Color.black));
		lbl_foreColor.setBackground(GlobalConstants.bar_foreColor);
		lbl_foreColor.addMouseListener(new ColorMouseListener());

		lbl_backColor = new JLabel("          ");		
		lbl_backColor.setOpaque(true);
		lbl_backColor.setSize(40, 18);
		lbl_backColor.setBorder(BorderFactory.createLineBorder(Color.black));
		lbl_backColor.setBackground(GlobalConstants.bar_backColor);		
		lbl_backColor.addMouseListener(new ColorMouseListener());
		
		lbl_proportionBarColor = new JLabel("          ");
		lbl_proportionBarColor.setOpaque(true);
		lbl_proportionBarColor.setSize(40, 18);
		lbl_proportionBarColor.setBorder(BorderFactory.createLineBorder(Color.black));
		lbl_proportionBarColor.setBackground(GlobalConstants.proporion_barColor);		
		lbl_proportionBarColor.addMouseListener(new ColorMouseListener());
		
		interaction_options.add(new JLabel(" Bar forecolor:"));
		interaction_options.add(lbl_foreColor);
		interaction_options.add(new JLabel(" Bar backcolor:"));
		interaction_options.add(lbl_backColor);
		interaction_options.add(new JLabel(" Proportion bar color:"));
		interaction_options.add(lbl_proportionBarColor);
		

		// removed --qiang
		// interaction_options.add(new JLabel(" Connection options: "));
		// interaction_options.add(connection_options);

		// add this to the options panel
		global_options.add(interaction_options);

		/* DATABASE CONNECTOR */

		// the object to be used to connect to the database
//		m_model = model;

		/* VISUAL EFFECTS */

		// Set the look and feel of the frame
		lookANDfeel(look_options.getSelectedIndex());

		// Exit the program when the frame closes
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the size of the panel
		this.setPreferredSize(new Dimension(screen_size.width, screen_size.height - 25));
		setSize(screen_size.width, screen_size.height - 25);

		// Go and set the size of the hierarchies [must do this!otherwise will affect the bar enlarge]
		for (Hierarchy h : hierarchies)
			h.setDimensions(screen_size);

		try {
			robot = new Robot();
		} catch (AWTException e) {
		}

		// The final attributes of the display
		setVisible(true);
	}

	class ColorMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel lbl = (JLabel) e.getSource();
			Color bgColor = JColorChooser.showDialog(null,"Choose Color",lbl.getBackground());
		    if (bgColor != null){
		    	lbl.setBackground(bgColor);
		    	if(lbl==lbl_foreColor){
		    		GlobalConstants.bar_foreColor = lbl.getBackground();
		    	}
		    	
		    	if(lbl==lbl_backColor){
		    		GlobalConstants.bar_backColor = lbl.getBackground();
		    	}
		    	
		    	if(lbl==lbl_proportionBarColor){
		    		GlobalConstants.proporion_barColor = lbl.getBackground();
		    	}
		    	
	    		for(Hierarchy h:hierarchies){
	    			h.getInnerhierarchy().setVisible(false);
	    			h.getInnerhierarchy().setVisible(true);
	    			h.pane_proportion.repaint();
	    		}
		    }
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}		
	}

	/* METHODS */

	/**
	 * Add listeners to the objects in the view
	 * 
	 * @param combo_box
	 *            - the combo box listener from the controller
	 * @param scroll_listener
	 *            - the listener for the scroll bar
	 * @param clear_listener
	 *            - the listener for the clear button
	 * @param keypress
	 *            - the keypress for the search box
	 */
	public void add_listeners(ComboBox_Action combo_box,
			Scroll_adjust_listener scroll_listener,
			//Clear_Button_Action clear_listener, 
			KeyPress_Action keypress) {

		/* LISTENER OBJECTS */

		// To control the panels
		Control_Listener c_l = new Control_Listener();

		/* THE LISTENERS FOR ALL HIERARCHIES */

		for (Hierarchy h : hierarchies)
			h.add_listeners(c_l, scroll_listener, keypress);

		/* GLOBAL */

		// Change the count with combo box

		count_options.addActionListener(combo_box);

		bar_options.addActionListener(combo_box);

		look_options.addActionListener(combo_box);

		connection_options.addActionListener(combo_box);
	}

	/**
	 * This creates a label to be added to a panel in a specific level
	 * 
	 * @param hierarchy
	 *            - defines which hierarchy the label's will be added to
	 * @param level
	 *            - Its level with the hierarchy, 1, 2 or 3
	 * @param name
	 *            - The text to be used for the label
	 * @param id
	 *            - The unique id of the label
	 * @param its_parent
	 *            - the parent id for this label
	 * @return - the Label that was created
	 */
	public AMLabel addLabel(Hierarchy hierarchy, int level,boolean isLeaf, String name,
			String id, String its_parent) {

		// remove any of the white space in the string
		name = name.trim();

		// remove all non ASCII characters, can make the display ugly and cause
		// problem
		name = name.replaceAll("[^\\p{ASCII}]", "");

		// add the abbreviations to the string
		// String text = Replacer.replaceAllWords(name);
		String text = name;

		// Cut the string if it is too large
		int cut_off = 50;
		if (text.length() > cut_off) {
			text = text.substring(0, cut_off);
		}

		// Create the label
		AMLabel label = new AMLabel(text, name, text, id, level);
		label.setOpaque(true);

		// set its parent because this will be needed later
		label.parent(its_parent);

		// add the label to the correct position
		addLabel(level, isLeaf, label, hierarchy);

		// return the new label
		return label;
	}

	/**
	 * This actually adds the label First we need to check that the label will
	 * be placed in the correct position Level 1 labels at the top Level 2
	 * labels on the second row Level 3 labels from row 3 - max rows
	 * 
	 * @param this_level
	 *            - Which level in the hierarchy it will be added to
	 * @param label
	 *            - The label to be added
	 * @param this_hierarchy
	 *            - The hierarchy that the label will be added to
	 */
	public void addLabel(int this_level, boolean isLeaf, AMLabel label, Hierarchy this_hierarchy) {

		// The padding required externally for the label
		int padding = 0;

		// The font to be used for the label
//		Font font = null;

		/* Get the information we need for adding the labels */

		// The constraints for the labels
		GridBagConstraints constraint = this_hierarchy.getConstraints();

		// The actual panel it will be added to
		JPanel panel = this_hierarchy.getInnerhierarchy();

		// The hasmap for quick positioning
		HashMap<String, Integer> map = this_hierarchy.getMap();

		// Determine the font for the label depending on the level
		switch (this_level) {
		case 1:
			// level 1 at the top
			padding = 0;
//			font = selectableFonts.getMediumFont();
			break;
		case 2:
			// level 2 on the second row
			padding = 10;
//			font = selectableFonts.getSmallFont();
			break;
		case 3:
			// level 3 anywhere but not the top 2
			padding = 20;
//			font = selectableFonts.getSmallestFont();
			break;
		default:
			// any other level, be safe and put it in the middle
			padding = 10;
//			font = selectableFonts.getSmallFont();
			break;
		}

		// add the default left padding
		padding += default_left_padding;

		// the next element is to the left of the previous element
		constraint.gridy++;

		// Create the image label with the image appearing on the left
		AMLabel image = new AMLabel(" ");

		// Set the information for the image
		image.setHorizontalAlignment(SwingConstants.LEFT);
		image.setUniqueID(label.getUniqueID());
		image.setPrimaryText(label.getPrimaryText());
		image.setSecondaryText(label.getSecondaryText());
		image.setLevel(this_level);
		image.setHorizontalAlignment(JLabel.LEFT);

		// set the amount of left padding
		image.setLeft_padding(padding);

		// set the image to be collapsed
		image.setStatus(0);

		// set image parent
		image.parent(label.getParent_id());

//		// The bottom level is a bullet, the others are normal roll images
//		if (this_level != this_hierarchy.getMaxlevels()) {
//
//			image.setBullet(false);
//
//		} else {
//			image.setBullet(true);
////			padding += 5;
//		}
		image.setBullet(isLeaf);

		// insert some padding on the side so make it consistent
		constraint.insets = new Insets(0, padding, 0, panel.getWidth()
				- (padding + 15));		

		// Add this image to the label for reference
		label.image(image);
		// specify the image label owner -- qiang
		image.owner = label;

		// Add the image to the panel
		panel.add(image, constraint);

		// add more padding after the image
		padding += 15;

		// the constraints for the other two labels
		constraint.insets = new Insets(0, padding, 0, 0);

		// set the font of this label and place on left size
//		label.setFont(font);

		// set the alignment
		label.setHorizontalAlignment(JLabel.LEFT);

		// now add the label to the panel in the correct position
		panel.add(label, constraint);

		// set the labels position in the component for use later
		label.setPosition(panel.getComponentCount() - 1);

		// set the level where this label sits
		label.setLevel(this_level);
		label.setOpaque(true);

		// Now create the bar for the label
		AMLabel bar = new AMLabel(" ", label.getUniqueID());

		// Set the information for the bar
		bar.setLevel(this_level);
		bar.setFont(selectableFonts.getBarFont());

		// Set the attributes to be the same as its parent
		bar.setPrimaryText(label.getPrimaryText());
		bar.setSecondaryText(label.getSecondaryText());

		// Set some properties of a bar
		bar.setIs_bar(true);

		// set the view background so that we can use it later
		label.setView_background(getBackground());

		// Add the bar to the label
		label.bar(bar);
		// specify the bar label owner -- qiang
		bar.owner = label;

		// The bar will be added to the level below the label
		constraint.gridy++;
		panel.add(bar, constraint);

		// add this id to the hashtable
		Integer result = map.put(label.getUniqueID(), label.getPosition());

		// make sure that we don't already has this id, if we do problems will
		// occur
		if (result != null) {
			duplicate_key(label.getUniqueID(), label.getPosition(), result);
		}

		// Set the padding for the other to labels
		label.setLeft_padding(padding);
		bar.setLeft_padding(padding);
	}

	/**
	 * Find which hierarchy an element will be added to or is in
	 * 
	 * @param hierarchy_num
	 *            - the hierarchy number, starting from 0
	 * @return - the hierarchy in that position
	 */
	public Hierarchy decide_hierarchy(int hierarchy_num) {
		
		for (Hierarchy h : hierarchies) {
			if (h.getId() == hierarchy_num)
				return h;
		}
		return null;
	}

	/**
	 * When a duplicate key has been detected we should alert somebody that this
	 * has happened
	 * 
	 * @param key
	 *            - The key for the hastable
	 * @param new_value
	 *            - The new value to be placed at that key
	 * @param old_value
	 *            - The previous value which is being replaced
	 */
	public void duplicate_key(String key, Integer new_value, Integer old_value) {
		Custom_Messages.display_error(this, "Duplicate Key",
				"ERROR: Duplicate key: " + key + "\n" + " new value: "
						+ new_value + " old value: " + old_value);
	}

	/**
	 * Add some buffer to all of the hierarchies
	 */
	public void add_buffer() {
		for (Hierarchy h : hierarchies)
			h.add_buffer();
		// hierarchy1.add_buffer();
		// hierarchy2.add_buffer();
		// hierarchy3.add_buffer();
	}

	/**
	 * Start the transparent glass over the frame
	 * 
	 * @param override
	 *            - if we want to override the load and show the status
	 */
	public void startGlass(boolean override) {

		// stops events from occurring through the panel
		glass.setNeedToRedispatch(false);

		// show the panel on top, we cannot see it because it is like glass
		glass.setVisible(true);

		// if it vis is loaded and we don't override it then don't show the
		// progress bar
		if (loaded && !override) {
			glass.getControl().setVisible(false);
			glass.getWaiter().setVisible(false);
		} else {
			glass.getControl().setVisible(true);
			glass.getWaiter().setVisible(true);
		}
	}

	/**
	 * End the transparent glass over the frame
	 * 
	 * @param override
	 *            - if we want to override glass and remove it
	 */
	public void endGlass(boolean override) {

		// if it is now loaded or overridden
		if (loaded || override) {

			// remove the glass
			glass.setVisible(false);

			// Again, manually control our 1.2/1.3 bug workaround
			glass.setNeedToRedispatch(true);

			// see the progress back to 0
			glass.getWaiter().setValue(0);
		}
	}

	/**
	 * Increment the value of the waiter for the progress bar Without a value we
	 * increment the waiter by 1
	 * 
	 * @param value
	 *            - the new value of the waiter
	 */
	public void incrementWaiter(int value) {

		// if we are not given a value just increment it by one
		if (value == 0) {
			glass.getWaiter().setValue(glass.getWaiter().getValue() + 1);
		} else {

			// otherwise then set it to the value
			glass.getWaiter().setValue(value);
		}
	}

	/**
	 * This sets the look and feel of the view This is a global setting and will
	 * change the look of every GUI element inside the window
	 * 
	 * @param look
	 *            - the look we want as an integer
	 */
	public void lookANDfeel(int look) {

		// The string that will hold the look
		String plaf = "";

		// The look and feel we want
		switch (look) {
		case 0:
			plaf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			break;
		case 1:
			plaf = "javax.swing.plaf.metal.MetalLookAndFeel";
			break;
		case 2:
			plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			break;
		case 3:
			plaf = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
			break;
		case 4:
			plaf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			break;
		default:
			// something else has been specified, give an error message
			Custom_Messages.display_error(this, "ERROR",
					"ERROR: An invalid look has been selected\n"
							+ "The valid range is from 1 to "
							+ (look_options_available.length + 1) + "\n"
							+ "A generic widows look will be used");
			plaf = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
			break;
		}

		// Try and set the look and feel and this might error
		try {
			UIManager.setLookAndFeel(plaf);
			SwingUtilities.updateComponentTreeUI(this);
		}
		// Catch the error that occurs and display some information
		catch (Exception e) {
			Custom_Messages
					.display_error(this, "ERROR",
							"An error has occured when changing the look and feel of the display");
			e.printStackTrace();
		}
	}

	/**
	 * Setup the bar with new information This method is required as we now have
	 * information to add into the bar that we previously didn't have Also used
	 * to change the count of the bar when we decide to count a different
	 * category
	 * 
	 * @param count
	 *            - The count for this bar
	 * @param hierarchy
	 *            - that the bar belongs to
	 * @param label
	 *            - The label that the bar belongs to
	 * @param left_padding
	 *            - The padding to be added to the left of the bar for
	 *            consistent spacing
	 */
	public void bar_edit(double count, Hierarchy hierarchy, AMLabel label) {

		// if this label has a bar
		if (label.isHas_bar()) {

			// get the bar
			AMLabel bar_label = label.getIts_bar();

			// get the inner panel that it sits in
			JPanel panel = hierarchy.getInnerhierarchy();

			// get the width of the upper panel because we want that to
			// determine the size
			int width = hierarchy.getHierarchyScroll().getWidth();

			float largest_count = 0;

			largest_count = hierarchy.getLargest_top();

			// System.out.println("======>"+largest_count);
			// how we are going to compare the count
			// if(label.getLevel() == 1) {
			// largest_count = hierarchy.getLargest_top();
			// } else {
			// largest_count = hierarchy.getLargest_rest();
			// }

			// get the constraints so we can place the label in the correct
			// position
			GridBagLayout layout = (GridBagLayout) panel.getLayout();
			GridBagConstraints constraint = layout.getConstraints(bar_label);

			// //set the count for this label now that we have it
			// //bar_label.setCount((int) Math.rint(count));
			// //rounding it off to two digits
			// count = Math.round(count*100)/100.0d;
			// doing the round operation when paint it --qiang
			bar_label.setCount(count);
			label.setCount(count);

			int left_padding = label.getLeft_padding();

			// set the external constraints and padding for this label with the
			// required lengths
			constraint.insets = decideSpace(count, largest_count, width,
					left_padding, bar_label);

			// System.out.println("Bar_Label width : "+bar_label.getWidth()+
			// "  Panel Width: " + width);
			// reset the constraints on this label with the new padding
			layout.setConstraints(bar_label, constraint);
			
			//record the largest bar
			if(count>=largest_count) {
				hierarchy.max_bar = bar_label;
			}
		}
	}

	/**
	 * Decide the external padding that is required for this object to be
	 * properly spaced The bar size should be relative to its size and to the
	 * largest The largest bar should reach all the way to the other side of the
	 * panel
	 * 
	 * @param count
	 *            - The count for the label
	 * @param largest
	 *            - The largest to be compared with
	 * @param panel_width
	 *            - The width of the panel above the bar
	 * @param left_padding
	 *            - The padding to be added
	 * @param bar
	 *            - The bar itself
	 * @return - the inset that has the padding added to it ------- Change to
	 *         log(length+1) --removed
	 * @author Qiang Liu
	 */
	public Insets decideSpace(double count, float largest, float panel_width,
			int left_padding, AMLabel bar) {

		// now we try and find the width of this bar
		// we want the largest bar to fill the screen and the others to be
		// relative
		if (bar.isIs_bar()) {

			// subtract the left padding on both sides so that we have a buffer
			panel_width -= (left_padding * 1.5);
			// find the width of this bar compared to the largest, this is a
			// percentage
			double this_width_per = count / largest;
			// double this_width_per = Math.log(count+1) / Math.log(largest+1);

			// we don't want the label to exceed the size of the panel with the
			// left padding being added
			double size = (panel_width * this_width_per);

			// find the correct amount of right padding
			double padding = panel_width - size;

			// set the sizes of the bar
			bar.setBar_width((int) size);

			// System.out.println("W: "+panel_width+" count:" + count +
			// " Largest:" + largest + " Size:"+size);
			// add this padding to the right hand side with a pixel on the
			// bottom
			return new Insets(0, left_padding, 0, (int) padding);
		}

		// if it is not a bar then return a constant rather than null
		return new Insets(0, 0, 0, 0);
	}

//	/**
//	 * Clear all of the panels with information Also clear the corresponding
//	 * information box We can only clear the panels if they are not locked
//	 */
//	public void clearAll() {
//		// clear the level and information
//		for (Hierarchy h : hierarchies) {
//			h.clearlevel(true);
//			h.removeInformation();
//		}
//		// //clear the level and information
//		// hierarchy1.clearlevel(true);
//		// hierarchy1.removeInformation();
//		//
//		// //If the second panel isn't locked
//		// hierarchy2.clearlevel(true);
//		// hierarchy2.removeInformation();
//		//
//		// hierarchy3.clearlevel(true);
//		// hierarchy3.removeInformation();
//	}

//	/**
//	 * Action to be performed with the click button is pressed
//	 * 
//	 * @param source_button
//	 *            - the button that was pressed
//	 */
//	public void clear_button_event(JButton source_button) {
//
//		// variables required for generic clearing for a level
//		Hierarchy hierarchy = null;
//
//		// if we have the first clear button
//		// if(source_button.equals(hierarchy1.getClear())) {
//		//
//		// hierarchy = hierarchy1;
//		//
//		// } else if(source_button.equals(hierarchy2.getClear())) {
//		//
//		// hierarchy = hierarchy2;
//		//
//		// } else if(source_button.equals(hierarchy3.getClear())) {
//		//
//		// hierarchy = hierarchy3;
//		//
//		// }
//		for (Hierarchy h : hierarchies) {
//			if (source_button.equals(h.getClear())) {
//				hierarchy = h;
//				break;
//			}
//		}
//
//		// clear the level, set back to the normal display
//		hierarchy.clearlevel(true);
//
//		// remove the information panel
//		hierarchy.removeInformation();
//
//		// remove the result panel and the text
//		hierarchy.removeResult();
//
//	}

	/* CONTROLLING THE HIERARCHIES */

	/**
	 * We are moving a panel one position We will decide in which direction it
	 * will move and to where
	 * 
	 * @param label
	 *            - the label where the interaction started Rewrite for multiple
	 *            hierarchies
	 * @author Qiang Liu
	 */
	public void move_left_right(JLabel label) {
		// the label is in Panel_Controller.java split by " - " to get the name
		// which is the id of hierarchy
		int index = Integer.valueOf(label.getName().split(" - ")[1].trim());
		// the panel we are going to move
		Hierarchy panel = decide_hierarchy(index);

		if (label.equals(panel.getPanel_controller().getMove_left())) {
			if (panel.zorder == 1) {
				for (Hierarchy h : hierarchies)
					h.zorder--;
				panel.zorder = hierarchies.size();
			} else {
				for (Hierarchy h : hierarchies) {
					if (h.zorder == panel.zorder - 1) {
						h.zorder++;
						panel.zorder--;
						break;
					}
				}
			}
		} else if (label.equals(panel.getPanel_controller().getMove_right())) {
			if (panel.zorder == hierarchies.size()) {
				for (Hierarchy h : hierarchies)
					h.zorder++;
				panel.zorder = 1;
			} else {
				for (Hierarchy h : hierarchies) {
					if (h.zorder == panel.zorder + 1) {
						h.zorder--;
						panel.zorder++;
						break;
					}
				}
			}
		}

		for (Hierarchy h : hierarchies) {
			getMain_panel().setComponentZOrder(h, h.zorder - 1);
		}

		// //the panel we are going to move
		// Hierarchy panel = null;
		//
		// //the location of the hierarchies
		// int one = 0;
		// int two = 1;
		// int three = 2;
		//
		// //Check which hierarchy the label has came from, need to get the last
		// character from the label's name as an integer
		// int index =
		// Character.getNumericValue(label.getName().charAt(label.getName().length()
		// -1));
		// panel = decide_hierarchy(index);
		//
		// //Get all of the panels in the correct position
		// Hierarchy p1 = (Hierarchy) getMain_panel().getComponent(one);
		// Hierarchy p2 = (Hierarchy) getMain_panel().getComponent(two);
		// Hierarchy p3 = (Hierarchy) getMain_panel().getComponent(three);
		//
		// //Perform a left action
		// if(label.equals(panel.getPanel_controller().getMove_left())) {
		//
		// //Decide how to move the panels depending on what was moved
		//
		// if(panel.equals(p1)) {
		//
		// //wrap around
		// one = left(one, total);
		// two = left(two, total);
		// three = left(three, total);
		//
		// } else if(panel.equals(p2)) {
		//
		// one++;
		// two--;
		// } else if(panel.equals(p3)) {
		// two++;
		// three--;
		// }
		//
		// //Perform a right action
		// } else if(label.equals(panel.getPanel_controller().getMove_right()))
		// {
		//
		// if(panel.equals(p1)) {
		//
		// one++;
		// two--;
		//
		// } else if(panel.equals(p2)) {
		//
		// two++;
		// three--;
		//
		// } else if(panel.equals(p3)) {
		//
		// one = right(one, total);
		// two = right(two, total);
		// three = right(three, total);
		//
		// }
		// }
		//
		// //Reset the panels in the position according to the variables
		// getMain_panel().setComponentZOrder(p1, one);
		// getMain_panel().setComponentZOrder(p2, two);
		// getMain_panel().setComponentZOrder(p3, three);
	}

	// /**
	// * Wrap the count left
	// *
	// * @param value
	// * - the position
	// * @param total
	// * - the total number of panels
	// * @return - the panels new position
	// */
	// public int left(int value, int total) {
	// if (value == 0) {
	// return total - 1;
	// }
	// return (value - 1) % total;
	// }
	//
	// /**
	// * Wrap the count right
	// *
	// * @param value
	// * - the position
	// * @param total
	// * - the total number of panels
	// * @return - the panels new position
	// */
	// public int right(int value, int total) {
	// return (value + 1) % total;
	// }

	/**
	 * Perform some action on the global panel This can move, show, hide the
	 * panel
	 * 
	 * @param label
	 *            - the option label that was interacted with
	 */
	public void move_action(JLabel label) {

//		System.out.println("---" + label.getName());

		// Decide the action
		if (label.getName().startsWith("show")) {

			// Show the panel
			hide_show_panel(label, true);

		} else if (label.getName().startsWith("hide")) {

			// Hide the panel
			hide_show_panel(label, false);

		} else if (label.getName().startsWith("move")) {

			// Move the panel
			move_left_right(label);
		}

		// repack the objects as they have probably moved
		/**
		 * Cause re-layout removed by Qiang
		 */
//		pack();
	}

	/**
	 * Set the cursor to waiting
	 * 
	 * @param set
	 *            - if we want the cursor to be waiting or normal
	 */
	public void setWaitCursor(boolean set) {

		if (set) {
			// Set the cursor to be a wait, just for some visual feedback
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			// now set the cursor back
			setCursor(Cursor.getDefaultCursor());
		}

		invalidate();
	}

	/**
	 * Hide or show a hierarchy depending on the label where the interaction
	 * started If we hide the panel we want the other elements to be smaller to
	 * save space When showing the panel it should be set back
	 * 
	 * @param label
	 *            - the label that was interacted with
	 * @param visible
	 *            - if we want it to be visible or not
	 */
	public void hide_show_panel(JLabel label, boolean visible) {

		// Get the hierarchy that this label belongs to
		// int index = Character.getNumericValue(label.getName().charAt(
		// label.getName().length() - 1));
		int index = Integer.valueOf(label.getName().split(" - ")[1].trim());
		Hierarchy hierarchy = decide_hierarchy(index);

		// Get the splitpane that we need by using this label
		JSplitPane panel = hierarchy.getHierarchy();

		// Set its visibility accordingly
		panel.setVisible(visible);

		// change the heading of the panel
		hierarchy.change_heading(visible);
	}

	/**
	 * Find a specific hierarchy depending on the string id we are given Check
	 * this against the hasmap's
	 * 
	 * @param id
	 *            - the unique id
	 * @return - the hierarchy that contains that id
	 */
	public Hierarchy findHierarchy(String id) {

		for (Hierarchy h : hierarchies)
			if (h.getMap().containsKey(id))
				return h;

		return null;
		// if (getHierarchy1().getMap().containsKey(id)) {
		// return getHierarchy1();
		// }
		//
		// if (getHierarchy2().getMap().containsKey(id)) {
		// return getHierarchy2();
		// }
		//
		// if (getHierarchy3().getMap().containsKey(id)) {
		// return getHierarchy3();
		// }
		//
		// // nothing has been found
		// return null;
	}

	/**
	 * Change the bar option for all of the hierarchies
	 * 
	 * @param option
	 *            - the option we want to change to
	 */
	public void setBar_options(String option) {

		for (Hierarchy h : hierarchies)
			h.setBar_option(option);
		// hierarchy1.setBar_option(option);
		// hierarchy2.setBar_option(option);
		// hierarchy3.setBar_option(option);
	}

	/* LISTENERS */

	/**
	 * This listens to the labels that are on top of the panel and perform
	 * actions on the panel
	 * 
	 * @author Anthony Scata
	 * @version 1.3
	 * 
	 */
	public class Control_Listener extends MouseAdapter {
		/**
		 * Change the lock when the label is pressed
		 */
		public void mousePressed(MouseEvent e) {
			
			// Get the label that started the event
			JLabel label = (JLabel) e.getComponent();

			// raise the border to give the impression it is intractable
			label.setBorder(BorderFactory.createLoweredBevelBorder());

		}

		/**
		 * When you mouse over the label with the cursor, make the label raised
		 */
		public void mouseEntered(MouseEvent e) {

			// Get the label that started the event
			JLabel label = (JLabel) e.getComponent();

			// raise the border to give the impression it is intractable
			label.setBorder(BorderFactory.createRaisedBevelBorder());
		}

		/**
		 * When you leave the label, set the border back to the original
		 */
		public void mouseExited(MouseEvent e) {

			// Get the label that started the event
			JLabel label = (JLabel) e.getComponent();

			// remove its border
			label.setBorder(null);
		}

		/**
		 * When you release the mouse click Perform the event now as that is
		 * what usually happens with these types of events
		 */
		public void mouseReleased(MouseEvent e) {

			// Get the label that started the event
			JLabel label = (JLabel) e.getComponent();

			// get the position of the mouse on the screen
			// just in case the label we interacted with has moved
			Point old_label_position = label.getLocationOnScreen();

			// lower the border giving the impression it was pushed
			label.setBorder(null);

			// perform the lock change only on a press
			move_action(label);

			// move into the middle of the label
			int width = label.getWidth() / 2;

			// get the labels new position on the screen as it may have moved
			// vertically during the interaction
			// this causes problems with the cursor then hovering over a new
			// label and creating new interaction
			Point new_label_position = label.getLocationOnScreen();

			// if the label has moved vertically past the size of the label
			if (new_label_position.y != old_label_position.y
					|| new_label_position.x != old_label_position.x) {
				robot.mouseMove(new_label_position.x + width,
						new_label_position.y + 2);
			}
		}
	}

	/* GETTERS AND SETTERS */

	/**
	 * @return the main_panel
	 */
	public JPanel getMain_panel() {
		return main_panel;
	}

	/**
	 * @return the count_options
	 */
	public JComboBox getCount_options() {
		return count_options;
	}

	/**
	 * @param countOptions
	 *            the count_options to set
	 */
	public void setCount_options(JComboBox countOptions) {
		count_options = countOptions;
	}

	// /**
	// * @return the hierarchy1
	// */
	// public Hierarchy getHierarchy1() {
	// return hierarchies.get(0);
	// // return hierarchy1;
	// }
	//
	// /**
	// * @return the hierarchy2
	// */
	// public Hierarchy getHierarchy2() {
	// return hierarchies.get(1);
	//
	// // return hierarchy2;
	// }
	//
	// /**
	// * @return the hierarchy3
	// */
	// public Hierarchy getHierarchy3() {
	// return hierarchies.get(2);
	//
	// // return hierarchy3;
	// }

	// /**
	// * @return the total
	// */
	// public int getTotal() {
	// return total;
	// }
	//
	// /**
	// * @param total
	// * the total to set
	// */
	// public void setTotal(int total) {
	// this.total = total;
	// }

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @param loaded
	 *            the loaded to set
	 */
	public void setLoaded(boolean loaded) {
		View.loaded = loaded;
	}

	/**
	 * @return the optionsAvailable
	 */
	public String[] getOptionsAvailable() {
		return count_options_available;
	}

	/**
	 * @param optionsAvailable
	 *            the optionsAvailable to set
	 */
	public void setOptionsAvailable(String[] optionsAvailable) {
		this.count_options_available = optionsAvailable;
	}

	/**
	 * @return the bar_options
	 */
	public JComboBox getBar_options() {
		return bar_options;
	}

	/**
	 * @param barOptions
	 *            the bar_options to set
	 */
	public void setBar_options(JComboBox barOptions) {
		bar_options = barOptions;
	}

	/**
	 * @return the bar_options_available
	 */
	public String[] getBar_options_available() {
		return bar_options_available;
	}

	/**
	 * @param barOptionsAvailable
	 *            the bar_options_available to set
	 */
	public void setBar_options_available(String[] barOptionsAvailable) {
		bar_options_available = barOptionsAvailable;
	}

	/**
	 * @return the look_options
	 */
	public JComboBox getLook_options() {
		return look_options;
	}

	/**
	 * @return the look_options_available
	 */
	public String[] getLook_options_available() {
		return look_options_available;
	}

	/**
	 * @return the connection_options
	 */
	public JComboBox getConnection_options() {
		return connection_options;
	}
}