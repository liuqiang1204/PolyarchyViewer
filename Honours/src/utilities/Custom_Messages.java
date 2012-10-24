package utilities;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * This class allows multiple messages to be shown to the user
 * Error messages, questions, warning, anything that needs to be shown to the user can be executed from here
 * No need to create an object, just call the methods explicitly
 * @author Anthony Scata
 * @version 1.0
 *
 */
public class Custom_Messages {

	/*METHODS*/
	
	/**
	 * Display an error message to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_error(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a question to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_question(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.QUESTION_MESSAGE);
	}
	
	/**
	 * Display information to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_information(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Display a warning message to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_warning(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Display a okay, no, cancel message to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_answer(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.YES_NO_CANCEL_OPTION);
	}
	
	/**
	 * Display a generic message to the user
	 * @param component - the view that we want to show the error message on
	 * @param reason - the reason for the error
	 * @param message - the message to be shown to the user
	 */
	public static void display_message(Component component, String reason, String message) {
		JOptionPane.showMessageDialog(component,
				message,
			    reason,
			    JOptionPane.PLAIN_MESSAGE);
	}
}