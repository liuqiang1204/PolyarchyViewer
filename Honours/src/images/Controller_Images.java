package images;

import javax.swing.ImageIcon;

/**
 * This class contains all of the images for the controller panel
 * Allows only one class to be imported and for easy manipulation of the images
 * The variables here are static so you don't need to create the object  
 * @author Anthony Scata
 * @version 1.2
 *
 */
public class Controller_Images extends Abstract_Image {

	/*VARIABLES*/
	
	/**
	 * Image that allows a panel to be showed
	 */
	private static ImageIcon show = new ImageIcon(folder+"show.png");
	
	/**
	 * Image that allows a panel to be hidden
	 */
	private static ImageIcon hide = new ImageIcon(folder+"hide.png");
		
	/**
	 * Image that allows a panel to be moved left
	 */
	private static ImageIcon move_left = new ImageIcon(folder+"move_left.png");
	
	/**
	 * Image that allows a panel to be moved right
	 */
	private static ImageIcon move_right = new ImageIcon(folder+"move_right.png");
	
	/**
	 * Image for the delete icon in tables.
	 */

	private static ImageIcon btn_delete = new ImageIcon(folder+"delete_icon.png");
	
	/**
	 * images for buttons
	 * @author Qiang Liu
	 */
	public static ImageIcon btn_search = new ImageIcon(folder+"search.png");
	public static ImageIcon btn_clearSearch = new ImageIcon(folder+"ClearSearch.png");
	public static ImageIcon btn_clearTable = new ImageIcon(folder+"ClearTable.png");
	public static ImageIcon btn_collapseAll = new ImageIcon(folder+"icon_collapse_all.png");
	/*GETTERS*/
	
	public static ImageIcon getBtnDelete() {
		return btn_delete;
	}
	
	
	/**
	 * @return the accept
	 */
	public static ImageIcon getShow() {
		return show;
	}
	
	/**
	 * @return the close
	 */
	public static ImageIcon getHide() {
		return hide;
	}
	
	/**
	 * @return the backward
	 */
	public static ImageIcon getMove_left() {
		return move_left;
	}
	
	/**
	 * @return the forward
	 */
	public static ImageIcon getMove_right() {
		return move_right;
	}
}