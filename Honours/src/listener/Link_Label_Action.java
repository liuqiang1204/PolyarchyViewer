package listener;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.JLabel;

import selectors.selectableColours;
import utilities.Custom_Messages;

/**
 * Link listeners allows click able links to be opened in a web page
 * Also change the label to look like a hyperlink when it is hovered and clicked
 * That way people get the feeling that they can click it
 * @author Anthony Scata
 * @version 2.1
 *
 */
public class Link_Label_Action extends MouseAdapter {
	
	/*VARIABLES*/
	
	/**
	 * Variable used to see if the label has been clicked yet or not
	 */
	private boolean clicked = false;

	
	/*MOUSE LISTENERS METHODS*/
	
	/**
	 * Perform an action when the link is clicked
	 */
	public void mouseClicked(MouseEvent evt) {
		
		//get the clicked object
		JLabel label = (JLabel) evt.getSource();
		
		//set the click
		clicked = true;
		
		//The contents of the web address
		String website = label.getName();
		
		try {
			if(website != "") {
				
				//only open the address if we have one
				Desktop.getDesktop().browse(URI.create(website));
				label.setForeground(selectableColours.getClickedLink());
			} else {
				//if an error occurs alert the user
				Custom_Messages.display_error(label, "Link Error", "No address is contained in the field");
			}
		} catch (IOException e) {
			//if an error occurs alert the user
			Custom_Messages.display_error(label, "Cannot open the web browser", e.getMessage());
		}

	}
 
	/**
	 * When the user hovers over the link
	 */
	public void mouseEntered(java.awt.event.MouseEvent evt) {
		
		//set the colour to red if it has not been clicked
		if(!clicked) {
			((Component) evt.getSource()).setForeground(selectableColours.getHoverLink());
		}
	}
 
	/**
	 * When the hover is exited
	 */
	public void mouseExited(java.awt.event.MouseEvent evt) {
		//set the colour back if it has not been clicked
		if(!clicked) {
			((Component) evt.getSource()).setForeground(selectableColours.getNormalLink());
		}
	}
 
	/**
	 * When the mouse is released after the click 
	 */
	public void mouseReleased(java.awt.event.MouseEvent evt) {
		
		//set the colour to blue if it has not been clicked
		if(!clicked) {
			((Component) evt.getSource()).setForeground(selectableColours.getClickedLink());
		}
	}
}