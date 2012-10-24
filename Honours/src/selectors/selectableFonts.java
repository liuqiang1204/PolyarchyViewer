package selectors;

import java.awt.Font;

/**
 * The selectable fonts to be used in the program
 * Wraps them all up into one class to be called when necessary
 * @author Anthony Scata
 * @version 1.3
 *
 */
public class selectableFonts {

	/*VARIABLES*/
	
	/**
	 * The size of the font to use for the bar
	 */
	private static int sizeBar = 8;
	
	/**
	 * The smallest font size
	 */
	private static int size1 = 10;
	
	/**
	 * The second smallest font size
	 */
	private static int size2 = 11;
	
	/**
	 * The middle sized font size
	 */
	private static int size3 = 13;
	
	/**
	 * The second largest font size
	 */
	private static int size4 = 16;
	
	/**
	 * The largest font size
	 */
	private static int size5 = 20;
	
	/**
	 * Bar font
	 */
	private static Font barFont = new Font("Tahoma", Font.PLAIN, sizeBar);
	
	/**
	 * Smallest font normal
	 */
	private static Font smallestFont = new Font("Tahoma", Font.PLAIN, size1);
	
	/**
	 * Smallest font bold
	 */
	private static Font smallestFontClick = new Font("Tahoma", Font.BOLD, size1);
	
	
	/**
	 * Small font normal
	 */
	private static Font smallFont = new Font("Tahoma", Font.PLAIN, size2);
	
	/**
	 * Small font bold
	 */
	private static Font smallFontClick = new Font("Tahoma", Font.BOLD, size2);
	

	/**
	 * Medium font normal
	 */
	private static Font mediumFont = new Font("Tahoma", Font.PLAIN, size3);
	
	/**
	 * Medium font bold
	 */
	private static Font mediumFontClick = new Font("Tahoma", Font.BOLD, size3);
	
	
	/**
	 * Large font normal
	 */
	private static Font largeFont = new Font("Tahoma", Font.PLAIN, size4);
	
	/**
	 * Large font bold
	 */
	private static Font largeFontClick = new Font("Tahoma", Font.BOLD, size4);
	
	/**
	 * Heading font normal
	 */
	private static Font heading1Font = new Font("Tahoma", Font.PLAIN, size5);
	
	/**
	 * Heading font click
	 */
	private static Font heading1FontClick = new Font("Tahoma", Font.BOLD, size5);
	
	
	/*GETTERS*/
	
	/**
	 * @return the barFont
	 */
	public static Font getBarFont() {
		return barFont;
	}
	
	/**
	 * @return the smallestFont
	 */
	public static Font getSmallestFont() {
		return smallestFont;
	}
	
	/**
	 * @return the smallestFontClick
	 */
	public static Font getSmallestFontClick() {
		return smallestFontClick;
	}
	
	/**
	 * @return the smallFont
	 */
	public static Font getSmallFont() {
		return smallFont;
	}
	
	/**
	 * @return the smallFontClick
	 */
	public static Font getSmallFontClick() {
		return smallFontClick;
	}
	
	/**
	 * @return the mediumFont
	 */
	public static Font getMediumFont() {
		return mediumFont;
	}
	
	/**
	 * @return the mediumFontClick
	 */
	public static Font getMediumFontClick() {
		return mediumFontClick;
	}
	
	/**
	 * @return the largeFont
	 */
	public static Font getLargeFont() {
		return largeFont;
	}
	
	/**
	 * @return the largeFontClick
	 */
	public static Font getLargeFontClick() {
		return largeFontClick;
	}
	
	/**
	 * @return the heading1Font
	 */
	public static Font getHeading1Font() {
		return heading1Font;
	}

	/**
	 * @return the heading1FontClick
	 */
	public static Font getHeading1FontClick() {
		return heading1FontClick;
	}
}
