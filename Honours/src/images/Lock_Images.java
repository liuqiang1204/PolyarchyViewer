package images;

import javax.swing.ImageIcon;

/**
 * The Image class contains the images that are required by the classes
 * Allows one class to be changed instead of many
 * The variables here are static so you don't need to create the object  
 * @author Anthony Scata
 * @version 1.1
 * 
 */
public class Lock_Images extends Abstract_Image {
	
	/*VARIABLES*/
	
	/**
	 * Large open lock image
	 */
	private static ImageIcon open_lock = new ImageIcon(folder+"lock-off-64x64.png");
	
	/**
	 * Large closed lock image
	 */
	private static ImageIcon closed_lock = new ImageIcon(folder+"lock-64x64.png");
	
	/**
	 * Medium open lock image
	 */
	private static ImageIcon open_medium_lock = new ImageIcon(folder+"open_lock-24x24.png");
	
	/**
	 * Medium closed lock image
	 */
	private static ImageIcon closed_medium_lock = new ImageIcon(folder+"closed_lock-24x24.png");
	
	/**
	 * Small open lock image
	 */
	private static ImageIcon open_small_lock = new ImageIcon(folder+"open_lock.png");
	
	/**
	 * Small closed lock image
	 */
	private static ImageIcon closed_small_lock = new ImageIcon(folder+"closed_lock.png");
		
	
	/*GETTERS*/

	/**
	 * @return the open_lock
	 */
	public static ImageIcon getOpen_lock() {
		return open_lock;
	}

	/**
	 * @return the closed_lock
	 */
	public static ImageIcon getClosed_lock() {
		return closed_lock;
	}

	/**
	 * @return the open_small_lock
	 */
	public static ImageIcon getOpen_small_lock() {
		return open_small_lock;
	}

	/**
	 * @return the closed_small_lock
	 */
	public static ImageIcon getClosed_small_lock() {
		return closed_small_lock;
	}

	/**
	 * @return the open_medium_lock
	 */
	public static ImageIcon getOpen_medium_lock() {
		return open_medium_lock;
	}

	/**
	 * @return the closed_mdedium_lock
	 */
	public static ImageIcon getClosed_medium_lock() {
		return closed_medium_lock;
	}
}