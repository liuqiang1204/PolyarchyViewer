package extensions;

import images.Controller_Images;

import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import project.View.Control_Listener;

/**
 * The panel controller has the buttons that can be used by the user to move panels and do other things
 * At the moments the movement is actually taken care by the view
 * This is because the panel has no idea where it is within the frame
 * The frame can be vertical or horizontal, depending on the action performed or user choice
 * @author Anthony Scata
 * @version 3.2
 *
 */
public class Panel_Controller extends JPanel {
	
	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Label to allow the user to show the panel
	 */
	private JLabel hide_show_option;
	
	/**
	 * Label to allow the user to move the panel below left
	 */
	private JLabel move_left;
	
	/**
	 * label to allow the user to move the panel below right
	 */
	private JLabel move_right;
	
	/**
	 * The string id for this panel, corresponds to its position	 * 
	 */
	private String name;
	
	/**
	 * The orientation of the panel
	 * 1 = horizontal, 0 = vertical
	 */
	private int orientation;
	
	/*CONSTRUCTORS*/
	
	/**
	 * Default constructor
	 * Sets the name to empty and adds the contents
	 */
	public Panel_Controller() {
		super();
	    
	    name = "";
	    orientation = 1;
		
		adder();
	}
	
	/**
	 * Constructor that allows the name to be set
	 * @param new_name - the unique name for this panel
	 */
	public Panel_Controller(String new_name) {
		super();
		
	    name = new_name;
	    orientation = 1;
		
		adder();
	}
	
	/**
	 * Constructor that allows the orientation to be set
	 * @param new_orientation - the orientation of the panel, 1 or 0
	 */
	public Panel_Controller(int new_orientation) {
		super();
		
	    name = "";
	    orientation = new_orientation;
		
		adder();
	}
	
	/**
	 * Constructor that allows the name to be set
	 * @param new_name - the unique name for this panel
	 * @param new_orientation - the orientation of the panel, 1 or 0
	 */
	public Panel_Controller(String new_name, int new_orientation) {
		super();
		
	    name = new_name;
	    orientation = new_orientation;
		
		adder();
	}
	
	/*METHODS*/
	
	/**
	 * Adds the JLabels to the panel
	 * Also sets the names so that they can be identified by the view
	 */
	public void adder() {
		
		//set the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
	    this.add(hide_show_option = new JLabel(Controller_Images.getHide()));
	    hide_show_option.setName("hide - " + name);
	    
	    this.add(move_left = new JLabel(Controller_Images.getMove_left()));
	    move_left.setName("move left - " + name);
	    
	    this.add(move_right = new JLabel(Controller_Images.getMove_right()));
	    move_right.setName("move right - " + name);
	}
	
	/**
	 * Allow the action listeners to be set in a central location
	 * @param c_l - the listeners for the labels
	 */
	public void setListeners(Control_Listener c_l) {
		hide_show_option.addMouseListener(c_l);
		move_left.addMouseListener(c_l);
		move_right.addMouseListener(c_l);
	}
	
	
	/**
	 * Override the paint method so that it always show the panel
	 * Will be extended to the orientation of the panel can be adjusted 
	 */
	protected void paintComponent(Graphics g) {
	
		super.paintComponent(g); 
		 
		 int align = 0;
		 
		 //Change the looks depending on the orientation
		 if(orientation == 1) {
		
			 //align on the x axis
			 align = BoxLayout.X_AXIS;
			 
		 } else if(orientation == 0) {
			 
			 //align on the y axis
			 align = BoxLayout.Y_AXIS;
		 }
		 
		 //set the alignment for the panel
		 this.setLayout(new BoxLayout(this, align));
	 }
	
	
	/**
	 * Changes the icon for the hide and show label
	 * This way if the hierarchy is visible then the icon is a hide
	 * If the hierarchy is not visible then the icon should be a show
	 * @param visible - if the hierarchy is visble or not
	 */
	public void change_icon(boolean visible) {
		
		//The new icon
		ImageIcon icon = null;
		
		//The new text
		String text = "";
		if(visible) {
		
			//if visible then the icon is hide
			icon = Controller_Images.getHide();
			text = "hide - " + name;
			//set the orientation to vertical
			orientation = 1;
		} else {
			
			//if not visible then the icon is show
			icon = Controller_Images.getShow();
			text = "show - " + name;
			//set the orientation to horizontal
			orientation = 0;
		}
		
		//set the variables
		hide_show_option.setName(text);
		
		hide_show_option.setIcon(icon);
			
		repaint();
	}

	
	/*GETTERS AND SETTERS*/
	
	/**
	 * @return the show_option
	 */
	public JLabel getHide_Show_option() {
		return hide_show_option;
	}

	/**
	 * @return the move_left
	 */
	public JLabel getMove_left() {
		return move_left;
	}

	/**
	 * @return the move_right
	 */
	public JLabel getMove_right() {
		return move_right;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}
	
	/**
	 * Set Tooltips
	 * @author Qiang Liu
	 */
	public void setTooltips(String heading){
		this.setTooltips(heading);
		this.hide_show_option.setToolTipText("H/S - "+heading);
		this.move_left.setToolTipText("Move left - " + heading);
		this.move_right.setToolTipText("Move right - " +heading);
		
	}
}