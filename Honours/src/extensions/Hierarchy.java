package extensions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import listener.Link_Label_Action;
import project.Controller.Clear_Button_Action;
import project.Controller.KeyPress_Action;
import project.Controller.Scroll_adjust_listener;
import project.View.Control_Listener;
import selectors.selectableFonts;

/**
 * Generic hierarchy class Contains all of the elements that are required for a
 * hierarchy in the display This allows multiple hierarchy's to be added easily
 * with all of the elements Also a change can be easily made for all hierarchies
 * 
 * @author Anthony Scata
 * @version 5.4
 * 
 */
public class Hierarchy extends JPanel {

	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A unique number for the hierarchy that allows it to be identified
	 */
	private int id;

	/* SCROLL CONSTAINTS */

	/**
	 * the unit size for a scroll
	 */
	private final int unitInc = 40;

	/**
	 * the block size for a scroll
	 */
	private final int blockInc = 150;

	/* PANELS */

	/**
	 * Panel that holds the heading information
	 */
	private JPanel panel_heading;

	/**
	 * The heading panel for the hierarchy
	 */
	private JLabel panelHeading;

	/**
	 * The split pane for the hierarchy
	 */
	private JSplitPane hierarchy;

	/**
	 * The panel that contains some options for the user
	 */
	private JPanel hierarchy_options;

	/**
	 * The panel that contains the intractable elements
	 */
	private AMPanel innerhierarchy;

	/**
	 * A scroll able panel
	 */
	private AMScrollPane hierarchyScroll;

	/**
	 * The array list that contains the clicked elements for this hierarchy
	 */
	private HashSet<Integer> click;

	/* SEARCH BOX */

	/**
	 * Search panel
	 */
	private JPanel search;

	/**
	 * Label for the search
	 */
	private JLabel searchLabel;

	/**
	 * The text to be shown during a search
	 */
	private JTextField searchText;

	/**
	 * The result of the search is shown in this label
	 */
	private JLabel searchResult;

	/**
	 * This button clear the search
	 */
	private JButton clear;

	/**
	 * Information that can be shown to the user after interaction on a label
	 */
	private JPanel information;

	/* THE TOP COTROLER */

	/**
	 * Controls for the panel
	 */
	private Panel_Controller panel_controller;

	/* CONSTRAINTS FOR THE PANEL */

	/**
	 * The constraints on the panel
	 */
	private GridBagConstraints constraints = new GridBagConstraints();

	/**
	 * An empty label used for padding
	 */
	private AMLabel padding_label;

	/**
	 * The maximum number of levels in this hierarchy
	 */
	private int maxlevels = 3;

	/**
	 * The largest top for this hierarchy, used for comparison
	 */
	private float largest_top;

	/**
	 * The largest for the rest, used for comparison
	 */
	private int largest_rest = 0;

	/**
	 * An index for the panels This is very important because we better
	 * optimisation Use the unique ID to get the labels location
	 */
	private HashMap<String, Integer> map;

	/**
	 * --Qiang
	 */
	private JTable searchingOpts;
	// label,status(0-unselected 1-selected 3-tmp added)
	public HashMap<AMLabel, Integer> searchingItems = new HashMap<AMLabel, Integer>();

	public JCheckBox isWeighted = new JCheckBox("Weighted");
	public JScrollBar jsb_filter = new JScrollBar();
	public JCheckBox cbx_visible;
	
	//the position in the panel
	public int zorder =0;

	/**
	 * for connection computing
	 * @author Qiang Liu
	 */
	//the intersection of selected count ids
	public HashSet<Integer> intersectionOfCountIds = new HashSet<Integer>();
	//the selected entry ids (the last level)
	public HashSet<Integer> selectedItemIds = new HashSet<Integer>();
	// to record the publication(people) ids with the total weighted value (countid,value)
	public HashMap<Integer, Double> totalWeightedValue = new HashMap<Integer, Double>();

	/* CONSTRUCTORS */

	/**
	 * Default constructor Sets the id to be a random number
	 */
	public Hierarchy() {
		super();

		// give the id some random number
		Random r = new Random();

		id = r.nextInt();

		add_elements("");
	}

	/**
	 * Constructor that allows the id to be set
	 * 
	 * @param this_id
	 *            - this id for this panel
	 * @param string
	 *            - the heading for this hierarchy
	 */
	public Hierarchy(int this_id, String heading) {
		super();

		id = this_id;

		add_elements(heading);
	}

	/* METHODS */

	/**
	 * Add the required elements to this panel
	 */
	public void add_elements(String heading) {

		// Get the font to be used for the heading
		Font headingFont = selectableFonts.getHeading1Font();

		// Set the layout for the panel to be a box that lays out elements on
		// the y axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Add a border around the panel
		setBorder(BorderFactory.createTitledBorder(""));

		/* HEADING */

		// Create the heading for the panel
		panelHeading = new JLabel(heading);
		panelHeading.setFont(headingFont);
//		panelHeading.setBorder(new CompoundBorder(BorderFactory
//				.createTitledBorder(""), new EmptyBorder(0, 0, 0, 0)));

		// Create panel to place this in
		panel_heading = new JPanel();
		panel_heading.setLayout(new BoxLayout(panel_heading, BoxLayout.X_AXIS));

		panel_heading.add(Box.createHorizontalGlue());

		// The options for the panel
		panel_controller = new Panel_Controller(id + "", 1);
		panel_controller.setToolTipText(panelHeading.getText());

		// add the heading
		panel_heading.add(panelHeading);

		panel_heading.add(Box.createRigidArea(new Dimension(5, 0)));

		panel_heading.add(panel_controller);

		panel_heading.add(Box.createHorizontalGlue());

		panel_heading.setToolTipText(panelHeading.getText());

		this.add(panel_heading);

		/* INNER PANELS */

		// this panel will hold the label for the interaction
		innerhierarchy = new AMPanel(true);
		innerhierarchy.setName(id + "");

		// this allows the panel to be scrolled as it will become too large
		hierarchyScroll = new AMScrollPane(innerhierarchy);

		// scrolling
		hierarchyScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		hierarchyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// set the scrolling constants
		hierarchyScroll.getVerticalScrollBar().setUnitIncrement(unitInc);
		hierarchyScroll.getVerticalScrollBar().setBlockIncrement(blockInc);

		// The options for the hierarchy
		hierarchy_options = new JPanel();
		
		// add the contents
		hierarchy = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hierarchyScroll,
				hierarchy_options);

		// Set some attributes of the panel
		hierarchy.setContinuousLayout(true);

		// Used for allowing the panel to be resized with a button
		hierarchy.setOneTouchExpandable(true);
		hierarchy.setResizeWeight(1);

		// add the split pane to the frame
		add(hierarchy);

		// set the layout of the inner to be a gridbag so that we can manipulate
		// the location of the labels
		innerhierarchy.setLayout(new GridBagLayout());

		// Change the constraints used for the grids
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;

		// The search panel and all of the required objects
		hierarchy_options.add(addSearch());

		// set the hash map
		map = new HashMap<String, Integer>(1000);

		/* array used to quickly switch between items */
		click = new HashSet<Integer>();

		// align the panel so that they fill the entire panel
		panel_controller.setAlignmentX((float) 0.0);
		panel_heading.setAlignmentX((float) 0.0);
		panel_controller.setAlignmentX((float) 0.0);
	}

	/**
	 * Add the contents to the search panel for hierarchy 1
	 * 
	 * @return - the panel that will contain the objects
	 */
	/**
	 * rewrite by Qiang *
	 * 
	 * @return
	 */

	/**
	 * add a listener for cbx_visible
	 */
	public void add_cbx_visible_actionListener(){
		final Hierarchy h = this;
		this.cbx_visible.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==cbx_visible){
					h.setVisible(cbx_visible.isSelected());
				}
			}
			
		});
	}
	/**
	 * Perform the action when the clear button is clicked
	 */
	public static void btn_clear_clicked(Hierarchy h) {
		for (AMLabel l : h.searchingItems.keySet())
			l.is_checked = false;
		h.searchingItems.clear();
		h.refreshSearchingTable();
		h.innerhierarchy.setVisible(false);
		h.innerhierarchy.setVisible(true);
	}

	static class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		ArrayList<Color> rowColors = new ArrayList<Color>();

		public MyTableModel(Object[][] data, String[] columns) {
			super(data, columns);
		}

		public MyTableModel() {
			super();
			addColumn("");
			addColumn("Item");
			addColumn("");
		}

		public void addRow(Object[] rowData, Color bc) {
			this.addRow(rowData);
			rowColors.add(bc);
			this.setRowColour(this.getRowCount() - 1, bc);
		}

		public void setRowColour(int row, Color c) {
			rowColors.set(row, c);
			fireTableRowsUpdated(row, row);
		}

		public Color getRowColour(int row) {
			return rowColors.get(row);
		}

		public boolean isCellEditable(int row, int column) {
			if (column == 0)
				return true;
			if (column == 2)
				return true;
			return false;
		}

		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0)
				return Boolean.class;
			if (columnIndex == 2)
				return ButtonRenderer.class;
			else
				return Object.class;
			// return getValueAt(0, columnIndex).getClass();
		}
	}

	public static class MyTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			MyTableModel model = (MyTableModel) table.getModel();
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			c.setForeground(Color.black);
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setBackground(model.getRowColour(row));
			}
			return c;
		}
	}

	public static class BooleanRenderer extends JCheckBox implements
			TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public BooleanRenderer() {
			this.setOpaque(true);
			this.setHorizontalAlignment(CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			MyTableModel model = (MyTableModel) table.getModel();
			Boolean v = (Boolean) value;
			this.setSelected(v);
			if (isSelected) {
				// this.setForeground(table.getSelectionForeground());
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setBackground(model.getRowColour(row));
			}
			return this;
		}
	}

	class ButtonRenderer extends AbstractCellEditor implements
			TableCellRenderer, TableCellEditor, ActionListener {
		private static final long serialVersionUID = 1L;

		JButton renderButton;
		JButton editButton;

		private AMLabel owner = null;

		public AMLabel getOwner() {
			return owner;
		}

		public ButtonRenderer() {
			renderButton = new JButton();
			editButton = new JButton();

			renderButton.setOpaque(true);
			renderButton.setText("Delete");
			// this.setIcon(Controller_Images.getBtnDelete());

			editButton.setFocusPainted(false);
			editButton.addActionListener(this);
		}

		public Component getTableCellRendererComponent(final JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			MyTableModel model = (MyTableModel) table.getModel();
			owner = (AMLabel) value;
			if (isSelected) {
				renderButton.setBackground(table.getSelectionBackground());
			} else {
				renderButton.setBackground(model.getRowColour(row));
			}
			return renderButton;
		}

		@Override
		public Object getCellEditorValue() {
			return "delete";
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			owner = (AMLabel) value;
			editButton.setText("delete");
			return editButton;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("removeSearchingItem");
			removeSearchingItem(owner);
			// innerhierarchy.repaint();
		}
	}

	public void addSearchingItem(AMLabel lbl, int status) {
		if (status == 3 && searchingItems.containsKey(lbl)) {
		} else {
			if (status == 1) {
				lbl.is_checked = true;
				lbl.getIts_image().is_checked = true;
			}
			searchingItems.put(lbl, status);
			refreshSearchingTable();
		}
	}

	public void removeSearchingItem(AMLabel lbl) {
		searchingItems.remove(lbl);
		lbl.is_checked = false;
		lbl.getIts_image().is_checked = false;
		refreshSearchingTable();
		this.innerhierarchy.setVisible(false);
		this.innerhierarchy.setVisible(true);

	}

	public void removeTempSearchingItem(AMLabel lbl) {
		if(searchingItems.containsKey(lbl)){
			int status = searchingItems.get(lbl);
			if (status == 3) {
				searchingItems.remove(lbl);
				lbl.is_checked = false;
				lbl.getIts_image().is_checked = false;
				refreshSearchingTable();
			}
		}
	}

	public void checkboxClicked(int row) {
		MyTableModel tm = (MyTableModel) searchingOpts.getModel();
		boolean ischecked = (Boolean) tm.getValueAt(row, 0);
		AMLabel item = (AMLabel) tm.getValueAt(row, 2);
		if (ischecked) {
			searchingItems.put(item, 1);
			item.is_checked = true;
			item.setVisible(false);
			item.setVisible(true);
		} else {
			searchingItems.put(item, 0);
			item.is_checked = false;
			item.setVisible(false);
			item.setVisible(true);
		}
		System.out.println(">>>" + item.getText() + " -- " + ischecked);
	}
	
	public void setSliderBarRange(int max){
		jsb_filter.setModel(new DefaultBoundedRangeModel(0,1,0,max));
		
	}
	
	public void setSliderBarListener(){
		jsb_filter.addAdjustmentListener(new AdjustmentListener(){

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				JScrollBar js = (JScrollBar) e.getSource();
				js.setToolTipText(e.getValue()+"");
				AMPanel ih = getInnerhierarchy();
				HashMap<String,Integer> im = getMap();
				for(int id:im.values()){
					AMLabel l = (AMLabel)ih.getComponent(id);
					l.limitSize = e.getValue();
					l.getIts_bar().limitSize = l.limitSize;
					l.getIts_image().limitSize = l.limitSize;
				}
				ih.setVisible(false);
				ih.setVisible(true);
			}
			
		});
	}

	public void refreshSearchingTable() {
		MyTableModel tm = new MyTableModel();
		searchingOpts.setModel(tm);
		searchingOpts.setDefaultRenderer(Object.class,
				new MyTableCellRenderer());
		searchingOpts.setDefaultRenderer(Boolean.class, new BooleanRenderer());
		searchingOpts.setDefaultRenderer(ButtonRenderer.class,
				new ButtonRenderer());
		searchingOpts.getColumnModel().getColumn(2)
				.setCellEditor(new ButtonRenderer());
		searchingOpts.setShowGrid(true);
		// System.out.println("-----"+tm.getColumnClass(0));
		for (Map.Entry<AMLabel, Integer> entry : searchingItems.entrySet()) {
			AMLabel key = entry.getKey();
			int value = entry.getValue();

			Object row[] = new Object[3];
			Color bc = Color.WHITE;
			row[0] = new Boolean(true);
			switch (value) {
			case 0:
				row[0] = new Boolean(false);
				break;
			case 3:
				bc = Color.GREEN;
				break;

			default:
				break;
			}
			if (value == 0)
				row[0] = new Boolean(false);
			else
				row[0] = new Boolean(true);

			row[1] = key.getText();
			row[2] = key;
			tm.addRow(row, bc);
		}

	}

	public JPanel addSearch() {
		search = new JPanel();
		search.setLayout(new BorderLayout());

		searchingOpts = new JTable();

		MouseAdapter adapter = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = searchingOpts.getSelectedRow();
				if (searchingOpts.getSelectedColumn() == 0) {
					checkboxClicked(row);
				}
			}
		};
		searchingOpts.addMouseListener(adapter);

		MyTableModel tm = new MyTableModel();
		searchingOpts.setModel(tm);

		JScrollPane jsp = new JScrollPane(searchingOpts);
		information = new JPanel();

		// JPanel text = new JPanel();
		// searchLabel = new JLabel("Search:");
		// searchText = new JTextField(20);

		clear = new JButton("Clear");
		// clear.addActionListener(new btn_clear_clicked());
		// clear.setIcon(Controller_Images.getBtnDelete());

		// text.add(searchLabel);
		// text.add(searchText);
		// text.add(clear);

		information.setSize(200, 22);
		information.setBorder(new EmptyBorder(0, 20, 0, 20));
		information.setLayout(new BoxLayout(information,BoxLayout.X_AXIS));
		isWeighted.setSelected(true);
		
		jsb_filter.setOrientation(JScrollBar.HORIZONTAL);
		jsb_filter.setPreferredSize(new Dimension(100,18));
		jsb_filter.setSize(100, 20);
		jsb_filter.setToolTipText("0");
		information.add(jsb_filter);
		information.add(isWeighted);
		information.add(clear);

		search.setSize(120, 50);
		// search.add(text,BorderLayout.NORTH);
		search.add(jsp, BorderLayout.CENTER);
		search.add(information, BorderLayout.NORTH);

		return search;
	}

	/*
	 * public JPanel addSearch() {
	 * 
	 * //Setup the panel's for the elements to be added search = new JPanel();
	 * information = new JPanel();
	 * 
	 * //Set the inner panels JPanel text = new JPanel(); JPanel result = new
	 * JPanel();
	 * 
	 * //Set the layout for the search box search.setLayout(new
	 * BoxLayout(search, BoxLayout.Y_AXIS));
	 * 
	 * //set its name search.setName("search" + id);
	 * 
	 * //add the elements to the search panel search.add(text);
	 * 
	 * search.add(result);
	 * 
	 * search.add(information);
	 * 
	 * information.setName("information");
	 * 
	 * searchLabel = new JLabel("Search:"); searchText = new JTextField(20);
	 * 
	 * searchResult = new JLabel("Results");
	 * 
	 * clear = new JButton("Clear");
	 * 
	 * text.add(searchLabel); text.add(searchText); text.add(clear);
	 * 
	 * result.add(searchResult, BorderLayout.LINE_START);
	 * 
	 * information.setVisible(true);
	 * 
	 * searchResult.setVisible(false);
	 * 
	 * //return the new panel return search; }
	 */

	/**
	 * Add the listeners to the hierarchy
	 * 
	 * @param c_l
	 *            - the listener for the controls on top of the panel
	 * @param scroll_listener
	 *            - the scroll listener
	 * @param clear_button_listener
	 *            - clear button
	 * @param keypress_listener
	 *            - keypress on the text box
	 */
	public void add_listeners(Control_Listener c_l,
			Scroll_adjust_listener scroll_listener,
			Clear_Button_Action clear_button_listener,
			KeyPress_Action keypress_listener) {

		// Add the listener to the controller objects
		getPanel_controller().setListeners(c_l);

		// Add the listener to the vertical scroll bars
		getHierarchyScroll().getVerticalScrollBar().addAdjustmentListener(
				scroll_listener);

		// Clear button listener, perform action on click
		// getClear().addActionListener(clear_button_listener);

		// Key event listener for when text is typed into the box
		// getSearchText().addKeyListener(keypress_listener);
	}

	/**
	 * Change the heading for the panel This allows an objects to be placed in
	 * any panel and we can identify it
	 * 
	 * @param name
	 *            - the new heading for the panel
	 */
	public void change_heading(String name) {
		JLabel panel = panelHeading;
		panel.setText(name);
	}

	/**
	 * Remove / hide the results panel
	 */
	public void removeResult() {
		searchResult.setText("");
		searchResult.setVisible(false);
		searchText.setText("");
	}

	/**
	 * Add some information to the search text
	 * 
	 * @param result
	 *            - the string to be added
	 */
	public void addResults(String result) {
		searchText.setText(result);
		searchText.setVisible(true);
	}

	/**
	 * Remove the labels with the information panel Used once a user has
	 * unclicked the label
	 */
	public void removeInformation() {
		// Remove all of the contents of the panel
		information.removeAll();

		// Don't display the panel
		information.setVisible(false);
	}

	/**
	 * Clear this specific level Loop through all of the elements and perform
	 * the operation
	 * 
	 * @param remove_count
	 *            - if we want to remove the count or not
	 */
	public void clearlevel(boolean remove_count) {

		// loop through all of the elements and clear it
		for (int i = 0; i < innerhierarchy.getComponentCount(); i++) {
			((AMLabel) innerhierarchy.getComponent(i))
					.clear_just_this(remove_count);
		}

		innerhierarchy.repaint();

		// Move the scroll panel so that we are at the top
		JViewport viewport = (JViewport) innerhierarchy.getParent();
		Rectangle r = viewport.getViewRect();
		Point p = r.getLocation();
		p.y = 0;

		// set the position of the view
		viewport.setViewPosition(p);
	}

	/**
	 * Clear the count for this panel
	 */
	public void clearCount() {

		// loop through all of the elements and clear it
		for (int i = 0; i < innerhierarchy.getComponentCount(); i++) {
			AMLabel label = (AMLabel) innerhierarchy.getComponent(i);

			// remove the sub count
			label.setSub_count(0);
			label.setCounted(false);
		}

		innerhierarchy.repaint();
	}

	/**
	 * Show the details of the clicked item to the user Used when a user clicked
	 * a label and new information needs to be displayed
	 * 
	 * @param name
	 *            - The name of the large e.g. First Name
	 * @param contents
	 *            - The contents of the label e.g. Anthony
	 * @param website
	 *            - The website that we will like to
	 * @return the contents label for the possibility of an action listener to
	 *         be added
	 */
	public void showDetails(String name, String contents, String website) {

		// before we place new information remove the old
		removeInformation();

		// Create the two new labels
		JLabel labelName = new JLabel(name);
		JLabel labelContents = new JLabel(contents);

		// This allows the text to be underlined
		labelContents.setText("<html><u>" + contents + "</u></html>");
		labelContents.setName(website);

		// Add the labels to the display
		information.add(labelName);
		information.add(labelContents);

		// Allow the panel to be visible
		information.setVisible(true);

		// allow the user to click the element as a hyperlink
		labelContents.addMouseListener(new Link_Label_Action());

	}

	/**
	 * Decide the action to be performed on this label We are concern here with
	 * expanding and collapsing, nothing else Also want to change the image
	 * according to the action This is generic and works for all levels and
	 * panel
	 * 
	 * @param original_label
	 *            - the label that the interaction occurred on
	 * @param comparison_label
	 *            - the label that we are comparing to
	 * @param entry
	 *            - if we are expanding or not
	 * @param hover
	 *            - if it was a hover or a click
	 * @param is_left_up
	 *            - if the event was caused by a left and up motion
	 */
	public void expand_collapse_decision(AMLabel original_label,
			AMLabel comparison_label, boolean entry, boolean hover,
			boolean is_left_up) {
		/**
		 * rewrite by Qiang NOT FINISHED!!!
		 */
		int comp_level = comparison_label.getLevel();
		int orig_level = original_label.getLevel();
		//
		boolean change_icon = true;
		//
		// //we only want to change the image on click and exit
		// if(!entry || !hover) {
		// change_icon = true;
		// }

		boolean tmp_entry = entry;

		if (original_label.isIs_image()) {

			if (is_left_up && hover) {
				entry = false;
			} else if (!is_left_up) {
				entry = true;
			}

			if (!hover) {
				if (original_label.isClicked()) {
					if (comparison_label.isIs_image()) {
						comparison_label.setClicked(true);
					}
					entry = true;
				} else if (!original_label.isClicked()) {
					if (comparison_label.isIs_image()) {
						comparison_label.setClicked(false);
					}
					entry = false;
				}
			}

			if (entry
					&& (comparison_label.getStatus() != 1 || comparison_label
							.isBullet())) {

				/*
				 * ENTRY EVENTS what happens on entry
				 */

				if (comparison_label.isIs_image()) {
					System.out.println(change_icon);
					System.out.println(orig_level + " " + comp_level);
					// its an image
					if (orig_level >= comp_level) {
						comparison_label.status(change_icon, 1);
						comparison_label.change(true);
					} else if (orig_level + 1 == comp_level) {
						if (comparison_label.isBullet()) {
							comparison_label.change(true);
						} else {
							comparison_label.status(change_icon, 0);
							comparison_label.change(true);
						}
					} else {
						comparison_label.status(change_icon, 1);
						comparison_label.change(true);
					}
				} else {

					// its anything else
					if (orig_level >= comp_level - 1) {
						comparison_label.change(true);
					} else {
						comparison_label.change(false);
					}
				}

			} else if (!entry) {
				if (!original_label.isClicked()) {
					if (comparison_label.isIs_image()) {
						if (comp_level == orig_level) {
							comparison_label.setStatus(0);
							comparison_label.change(true);
						} else if (comp_level > orig_level) {
							comparison_label.setStatus(0);
							comparison_label.change(false);
						} else {// above, cannot assume this
							comparison_label.setStatus(1);
							comparison_label.change(true);
						}
					} else {

						// any other plain label
						if (comp_level <= orig_level) {
							comparison_label.change(true);
						} else {
							comparison_label.change(false);
						}
					}
				}
			}

			entry = tmp_entry;

			// if this label has a bar recursively call this method
			if (comparison_label.isHas_bar()) {
				expand_collapse_decision(original_label,
						comparison_label.getIts_bar(), entry, hover, is_left_up);
			}

			// if this label has an image then recursively call this method
			if (comparison_label.isHas_image()) {
				expand_collapse_decision(original_label,
						comparison_label.getIts_image(), entry, hover,
						is_left_up);
			}

		} else if (comparison_label.isClicked()
				&& comparison_label.isIs_image() && entry) {
			comparison_label.setStatus(0);
			comparison_label.setClicked(false);
		}
		comparison_label.repaint();

		// int comp_level = comparison_label.getLevel();
		// int orig_level = original_label.getLevel();
		//
		// boolean change_icon = false;
		//
		// //we only want to change the image on click and exit
		// if(!entry || !hover) {
		// change_icon = true;
		// }
		//
		// boolean tmp_entry = entry;
		//
		// //The new label must be showable and the original be an image
		// if(original_label.isIs_image()) {
		//
		// if(is_left_up && hover) {
		// entry = false;
		// } else if (!is_left_up && !entry) {
		// change_icon = true;
		// entry = true;
		// }
		//
		//
		// if(!hover) {
		// if(original_label.isClicked() || (original_label.isClicked() &&
		// original_label.getStatus() == 1)) {
		// if(comparison_label.isIs_image()) {
		// comparison_label.setClicked(true);
		// }
		// entry = true;
		// } else if(!original_label.isClicked()){
		// if(comparison_label.isIs_image()) {
		// comparison_label.setClicked(false);
		// }
		// entry = false;
		// }
		// }
		//
		// if(entry && (comparison_label.getStatus() != 1 ||
		// comparison_label.isBullet())) {
		//
		// /*
		// * ENTRY EVENTS
		// * what happens on entry
		// */
		//
		// //we don't want click to occur and now label isn't clicked, that is a
		// collapse
		// if(comparison_label.isIs_bar()) {
		//
		// //its a bar
		// if(orig_level >= comp_level || orig_level + 1 == comp_level) {
		// comparison_label.change(true);
		// } else {
		// comparison_label.change(false);
		// }
		// } else if(comparison_label.isIs_image()) {
		// System.out.println(change_icon);
		// System.out.println(orig_level + " " + comp_level);
		// //its an image
		// if(orig_level >= comp_level) {
		// comparison_label.status(change_icon, 1);
		// comparison_label.change(true);
		// } else if(orig_level + 1 == comp_level) {
		// if(comparison_label.isBullet()) {
		// comparison_label.change(true);
		// } else {
		// comparison_label.status(change_icon, 0);
		// comparison_label.change(true);
		// }
		// }else {
		// comparison_label.status(change_icon, 1);
		// comparison_label.change(false);
		// }
		// } else {
		//
		// //its anything else
		// if(orig_level >= comp_level || orig_level + 1 == comp_level) {
		// comparison_label.change(true);
		// } else {
		// comparison_label.change(false);
		// }
		// }
		//
		// } else if(!entry) {
		// //should remove hover!!!
		// if((!hover && !original_label.isClicked()) || (hover &&
		// !original_label.isClicked())) {
		//
		// if(comparison_label.isIs_bar()) {
		// if(comp_level <= orig_level)
		// comparison_label.change(true);
		// else {
		// comparison_label.change(false);
		// }
		// }
		// if(comparison_label.isIs_image()) {
		// if(comp_level == orig_level) {
		// comparison_label.setStatus(0);
		// comparison_label.change(true);
		// } else if(comp_level > orig_level) {
		// comparison_label.setStatus(0);
		// comparison_label.change(false);
		// } else {//above, cannot assume this
		// comparison_label.setStatus(1);
		// comparison_label.change(true);
		// }
		// } else {
		//
		// //any other plain label
		// if(comp_level <= orig_level) {
		// comparison_label.change(true);
		// } else {
		// comparison_label.change(false);
		// }
		// }
		// }
		// }
		//
		// entry = tmp_entry;
		//
		// //if this label has a bar recursively call this method
		// if(comparison_label.isHas_bar()) {
		// expand_collapse_decision(original_label,
		// comparison_label.getIts_bar(), entry, hover, is_left_up);
		// }
		//
		// //if this label has an image then recursively call this method
		// if(comparison_label.isHas_image()) {
		// expand_collapse_decision(original_label,
		// comparison_label.getIts_image(), entry, hover, is_left_up);
		// }
		//
		// //comparison_label.repaint();
		// } else if(comparison_label.isClicked() &&
		// comparison_label.isIs_image() && entry) {
		// comparison_label.setStatus(0);
		// comparison_label.setClicked(false);
		// }
		// comparison_label.repaint();
	}

	/**
	 * Check to see if this label is at the bottom of the vis by checking it
	 * against the max levels
	 * 
	 * @return - true if the label is at the bottom, false it isn't
	 */
	public boolean isBottom(AMLabel label) {

		// if we are at the bottom level, return true
		if (label.getLevel() == maxlevels) {
			return true;
		}
		return false;
	}

	/**
	 * Set the dimensions of some part of the hierarchy
	 * 
	 * @param screenSize
	 *            - assumed to be the size of the view above, this way we don't
	 *            paint components bigger than that
	 */
	public void setDimensions(Dimension screenSize) {

		// Get the height of the view
		int height = (int) (screenSize.height * .5);

		hierarchyScroll.setPreferredSize(new Dimension(30, height));
	}

	/* PANEL BUFFER TO PUSH EVERYTHING UP */

	/**
	 * Adds a buffer label to the panel Used for expanding and collapsing the
	 * size
	 */
	public void add_buffer() {

		// The constraints for the labels
		GridBagConstraints constraint = getConstraints();

		// The actual panel it will be added to
		JPanel panel = getInnerhierarchy();

		// the label will be placed below the other labels
		constraint.gridy++;
		constraint.insets = new Insets(0, 0, 1000, 0);

		// create the new label to be added as padding
		padding_label = new AMLabel(" ");

		GridBagLayout gridbag = (GridBagLayout) panel.getLayout();

		// add the new label to the panel
		gridbag.setConstraints(padding_label, constraint);
		panel.add(padding_label);

		padding_label.repaint();

		// set the level to the original
		// clearlevel(true);

		// now edit the buffer
		edit_buffer();
	}

	/**
	 * Edits the buffer label according to the size of the first and last
	 * components
	 */
	public void edit_buffer() {

		// validate the panel to ensure that everything is in the right place
		innerhierarchy.repaint();

		// Now set the thread to sleep so that we can check the panel has
		// updated
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		// Get the top label position
		AMLabel label = (AMLabel) innerhierarchy.getComponent(0);
		Point location_top = label.getLocationOnScreen();

		// Get the last components number
		int last = innerhierarchy.getComponentCount() - 1;

		// Make sure that it is valid and we can retreive it
		if (last >= 0) {

			// Get the bottom label position
			label = (AMLabel) innerhierarchy.getComponent(last);
			Point location_bottom = label.getLocationOnScreen();

			// find the height that has been used by the labels
			int its_height = (int) Math.abs(location_bottom.getY()
					- location_top.getY());

			// get the height of the scroll pane
			int height = hierarchyScroll.getHeight();

			// if its height is larger than the objects have used
			if (its_height < height && its_height > 0) {

				// Pad out the extra space with an empty label
				int add = (height - its_height) + 20;

				// get the constraints
				GridBagLayout layout = (GridBagLayout) innerhierarchy
						.getLayout();
				GridBagConstraints gbc06 = getConstraints();
				gbc06.insets = new Insets(0, 0, add, 0);

				// reset the constraints for the padding label
				layout.setConstraints(padding_label, gbc06);

				// validate the panel to ensure that everything is in the right
				// place
				innerhierarchy.repaint();
			}
		}
	}

	/**
	 * Set the bar option for the labels within the panel
	 * 
	 * @param option
	 *            - the option for the bar
	 */
	public void setBar_option(String option) {

		// Loop through all of the labels in the panel
		for (int i = 0; i < innerhierarchy.getComponentCount(); i++) {

			// Set the option
			AMLabel label = (AMLabel) innerhierarchy.getComponent(i);

			if (label.isIs_bar()) {

				// only set this if it is a bar
				label.setBar_option(option);
			}
		}

		// now repaint the whole thing
		innerhierarchy.repaint();
	}

	/**
	 * Change the alignment of the heading according to the hierarchies
	 * visibility
	 * 
	 * @param visible
	 *            - the the hierarchy is visible or not
	 */
	public void change_heading(boolean visible) {

		// set the alignment
		panel_heading.setLayout(new BoxLayout(panel_heading, BoxLayout.X_AXIS));

		this.panelHeading.setVisible(visible);
		// change the icon
		panel_controller.change_icon(visible);
	}

	/* GETTERS AND SETTERS */

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the hierarchy
	 */
	public JSplitPane getHierarchy() {
		return hierarchy;
	}

	/**
	 * @param hierarchy
	 *            the hierarchy to set
	 */
	public void setHierarchy(JSplitPane hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * 
	 * @return the sidehierarchy
	 */
	public JPanel getSidehierarchy() {

		return hierarchy_options;
	}

	/**
	 * @param sidehierarchy
	 *            the sidehierarchy to set
	 */
	public void setSidehierarchy(JPanel sidehierarchy) {

		this.hierarchy_options = sidehierarchy;
	}

	/**
	 * @return the innerhierarchy
	 */
	public AMPanel getInnerhierarchy() {
		return innerhierarchy;
	}

	/**
	 * @param innerhierarchy
	 *            the innerhierarchy to set
	 */
	public void setInnerhierarchy(AMPanel innerhierarchy) {
		this.innerhierarchy = innerhierarchy;
	}

	/**
	 * @return the hierarchyScroll
	 */
	public AMScrollPane getHierarchyScroll() {
		return hierarchyScroll;
	}

	/**
	 * @param hierarchyScroll
	 *            the hierarchyScroll to set
	 */
	public void setHierarchyScroll(AMScrollPane hierarchyScroll) {
		this.hierarchyScroll = hierarchyScroll;
	}

	/**
	 * @return the search
	 */
	public JPanel getSearch() {
		return search;
	}

	/**
	 * @param search
	 *            the search to set
	 */
	public void setSearch(JPanel search) {
		this.search = search;
	}

	/**
	 * @return the searchLabel
	 */
	public JLabel getSearchLabel() {
		return searchLabel;
	}

	/**
	 * @param searchLabel
	 *            the searchLabel to set
	 */
	public void setSearchLabel(JLabel searchLabel) {
		this.searchLabel = searchLabel;
	}

	/**
	 * @return the searchText
	 */
	public JTextField getSearchText() {
		return searchText;
	}

	/**
	 * @param searchText
	 *            the searchText to set
	 */
	public void setSearchText(JTextField searchText) {
		this.searchText = searchText;
	}

	/**
	 * @return the searchResult
	 */
	public JLabel getSearchResult() {
		return searchResult;
	}

	/**
	 * @param searchResult
	 *            the searchResult to set
	 */
	public void setSearchResult(JLabel searchResult) {
		this.searchResult = searchResult;
	}

	/**
	 * @return the clear
	 */
	public JButton getClear() {
		return clear;
	}

	/**
	 * @param clear
	 *            the clear to set
	 */
	public void setClear(JButton clear) {
		this.clear = clear;
	}

	/**
	 * @return the information
	 */
	public JPanel getInformation() {
		return information;
	}

	/**
	 * @param information
	 *            the information to set
	 */
	public void setInformation(JPanel information) {
		this.information = information;
	}

	/**
	 * @return the constraints
	 */
	public GridBagConstraints getConstraints() {
		return constraints;
	}

	/**
	 * @return the maxlevels
	 */
	public int getMaxlevels() {
		return maxlevels;
	}

	/**
	 * @param maxlevels
	 *            the maxlevels to set
	 */
	public void setMaxlevels(int maxlevels) {
		this.maxlevels = maxlevels;
	}

	/**
	 * @return the unitInc
	 */
	public int getUnitInc() {
		return unitInc;
	}

	/**
	 * @return the blockInc
	 */
	public int getBlockInc() {
		return blockInc;
	}

	/**
	 * @param map
	 *            the map to set
	 */
	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}

	/**
	 * @return the map
	 */
	public HashMap<String, Integer> getMap() {
		return map;
	}

	/**
	 * @param largest_top
	 *            the largest_top to set
	 */
	public void setLargest_top(float largest_top) {
		this.largest_top = largest_top;
	}

	/**
	 * @return the largest_top
	 */
	public float getLargest_top() {
		return largest_top;
	}

	/**
	 * @param largest_rest
	 *            the largest_rest to set
	 */
	public void setLargest_rest(int largest_rest) {
		this.largest_rest = largest_rest;
	}

	/**
	 * @return the largest_rest
	 */
	public int getLargest_rest() {
		return largest_rest;
	}

	/**
	 * @return the click
	 */
	public HashSet<Integer> getClick() {
		return click;
	}

	/**
	 * @param click
	 *            the click to set
	 */
	public void setClick(HashSet<Integer> click) {
		this.click = click;
	}

	/**
	 * @return the panel_controller
	 */
	public Panel_Controller getPanel_controller() {
		return panel_controller;
	}

	/**
	 * @param panelController
	 *            the panel_controller to set
	 */
	public void setPanel_controller(Panel_Controller panelController) {
		panel_controller = panelController;
	}
}