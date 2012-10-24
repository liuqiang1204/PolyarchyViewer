package extensions;

import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import project.View;

/**
 * A custom scroll panel
 * Used to speed up the painting on load
 * @author Anthony Scata
 * @version 2.0
 *
 */
public class AMScrollPane extends JScrollPane{
	
	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = 1L;
	
	
	/*CONSTRUCTORS*/

	/**
	 * Default constructor
	 */
	public AMScrollPane() {
		super();
	}
	
	/**
	 * Constructor that allows a JPanel to be passed which it will scroll
	 * @param panel - the panel that it will scroll
	 */
	public AMScrollPane(JPanel panel) {
		
		//add the panel
		super(panel);
		
		//set double buffering to be on to add speed
		setDoubleBuffered(true);
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
    		super.paint(g);
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