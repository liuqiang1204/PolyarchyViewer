package images;

import javax.swing.ImageIcon;

/**
 * The images that are used for the expanding and collapsing
 * Contained within one class
 * The variables here are static so you don't need to create the object  
 * @author Anthony Scata
 * @version 1.2
 * 
 */
public class Roll_Images extends Abstract_Image {
	
	/*VARIABLES*/
	
	/**
	 * Image shows it is open / expanded
	 */
	private static ImageIcon open = new ImageIcon(folder+"roll_open.png");
	
	/**
	 * Image shows it is closed / collapsed
	 */
	private static ImageIcon closed = new ImageIcon(folder+"roll_closed.png");
	
	/**
	 * Image shows a bullet
	 */
	private static ImageIcon bullet = new ImageIcon(folder+"bullet.png");
	
	
	/*GETTERS*/
	
	/**
	 * @return the open
	 */
	public static ImageIcon getOpen() {
		return open;
	}
	
	/**
	 * @return the closed
	 */
	public static ImageIcon getClosed() {
		return closed;
	}
	
	/**
	 * @return the bullet
	 */
	public static ImageIcon getBullet() {
		return bullet;
	}
}