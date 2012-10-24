package images;

/**
 * Abstract image class that defines the location of the images
 * All of the class that have images will extend this class
 * @author Anthony Scata
 * @version 1.0
 *
 */
public abstract class Abstract_Image {

	/**
	 * The folder that contains the image
	 */
	protected static String folder = "images/";

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		Abstract_Image.folder = folder;
	}
}
