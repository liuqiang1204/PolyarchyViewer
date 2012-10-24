package selectors;

import java.awt.Color;

/**
 * The selectable colours to be used in the program
 * Wraps them all up into one class to be called when necessary
 * @author Anthony Scata
 * @version 1.3
 *
 */
public class selectableColours {

	/*VARIABLES*/
	
	/**
	 * The generic colour
	 */
	private static Color normalColor = new Color(0,0,0);
	
	/**
	 * The colour used an an object is clicked
	 */
	private static Color clickColor = new Color(100, 100, 255);
	
	/**
	 * The colour used when an object is interacted with or queried
	 */
	private static Color otherColor = new Color(220, 50, 50);
	
	/**
	 * The colour on a link
	 */
	private static Color normalLink = Color.BLACK;
	
	/**
	 * The colour of a link when it is hovered over
	 */
	private static Color hoverLink = Color.RED;
	
	/**
	 * The colour of a link when it is clicked
	 */
	private static Color clickedLink = Color.BLUE;
	
	/**
	 * The colour that the bar will be set too
	 */
	private static Color wholeBar = Color.BLACK;
	
	/**
	 * The colour that the bars border will be
	 */
	private static Color borderBar = Color.GRAY;
	
	/**
	 * The colour that the inside of the bar will be
	 */
	//TODO
	//experiment with this
	private static Color innerBar = new Color(220, 50, 50);
	
	
	/*GETTERS*/
	
	/**
	 * @return the normalColor
	 */
	public static Color getNormalColor() {
		return normalColor;
	}

	/**
	 * @return the clickColor
	 */
	public static Color getClickColor() {
		return clickColor;
	}

	/**
	 * @return the otherColor
	 */
	public static Color getOtherColor() {
		return otherColor;
	}

	/**
	 * @return the normalLink
	 */
	public static Color getNormalLink() {
		return normalLink;
	}

	/**
	 * @return the hoverLink
	 */
	public static Color getHoverLink() {
		return hoverLink;
	}

	/**
	 * @return the clickedLink
	 */
	public static Color getClickedLink() {
		return clickedLink;
	}

	/**
	 * @return the wholeBar
	 */
	public static Color getWholeBar() {
		return wholeBar;
	}

	/**
	 * @return the innerBar
	 */
	public static Color getBorderBar() {
		return borderBar;
	}
	
	/**
	 * @return the innerBar
	 */
	public static Color getInnerBar() {
		return innerBar;
	}
}