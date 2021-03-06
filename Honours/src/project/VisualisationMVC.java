package project;

/**
 * What we have here is the main class
 * This is where all of the elements are setup and allow the objects to see each other
 * This software deign pattern is known as the Model-View-Controller
 * @author Anthony Scata
 * @version 1.2
 */
/**
 * Add app version info
 * @author Qiang Liu
 *
 */
public class VisualisationMVC {
	
    /**
     * Create model, view, and controller.
     * Need to create them here so that we have only one copy of each
     * Allowing the data to be modified by the view and the modification of the view
     * At the moment we are not worried about updating data from the view
     * but it is here just in case we want it in the future
     * @param args - command line arguments have no effect on the program, yet
     */
    public static void main(String[] args) {
    	System.out.println("Polyarchy Viewer 0.40\n" +
    			"Source code is available at https://github.com/liuqiang1204/PolyarchyViewer\n" +
    			"If you have any suggestion, please send email to liuqiang1204@gmail.com\n");
    	
    	//call the login screen
        new LoginScreen("database/databases.ini");
       
        System.out.println("Thanks for using Polyarchy Viewer 0.40.");

    }
}