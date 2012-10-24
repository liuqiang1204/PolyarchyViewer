package extensions;

import java.awt.Graphics;

import javax.swing.JPanel;

import project.View;

/**
 * A custom panel
 * Used to speed up painting on load
 * @author Anthony Scata
 * @version 2.0
 *
 */
public class AMPanel extends JPanel{

	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = 1L;

	/*CONSTRUCTORS*/
	
	/**
	 * Default constructor
	 */
	public AMPanel() {
		super();
	}
	
	/**
	 * Constructor that allows double differing to be set or not
	 * @param double_buffer - if we want double buffering to be on or not
	 */
	public AMPanel(boolean double_buffer) {
		super(double_buffer);
	}

	
	/*OVERWRITE METHODS*/

	/**
	 * Override the update method so that we paint straightway
	 * This speeds up the painting
	 * We will not update until the view is fully loaded
	 */
    public void update(Graphics g) {
    	
    	//Paint only when the view if fully loaded
    	if(View.loaded) {
    		paint(g);
    	}
    }
    
    /**
	 * Override the paint method so that we wont paint before the view is loaded
	 * This saves time on load 
	 */
	public void paint(Graphics g) {
		
		//Paint only when the view if fully loaded
		if(View.loaded) {
			super.paint(g);
		}
	}
}