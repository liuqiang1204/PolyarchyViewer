package project;

//import connector.Java_Connector;
//import extensions.QueryView;

/**
 * What we have here is the main class
 * This is where all of the elements are setup and allow the objects to see each other
 * This software deign pattern is known as the Model-View-Controller
 * @author Anthony Scata
 * @version 1.2
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
    	System.out.println("PolyArchy Viewer 0.24\n" +
    			"Source code is available at https://github.com/liuqiang1204/PolyarchyViewer\n" +
    			"If you have any suggestion, please send email to liuqiang1204@gmail.com\n");
    	
    	//call the login screen
        new LoginScreen();
        
        System.out.println("Thanks for using PolyArchy Viewer 0.24.");

    }
}