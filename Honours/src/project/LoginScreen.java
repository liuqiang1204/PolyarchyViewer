package project;

import java.awt.Dimension;
//import java.awt.Frame;
import java.awt.Insets;
//import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import connector.Java_Connector;
import extensions.QueryView;

/**
 * Dialog box to prompt for user id/password and the database to connect to.
 */


public class LoginScreen extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * The databases to connect to
	 */
	protected String[] databases = {"honours","movie_small","publication_small","sample","honours1","honours2"};
	protected boolean isValid = false;
	protected String dbName = "honours";
	
	JFrame frame = new JFrame("PasswordDemo");
	
  public LoginScreen() {
	 

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.getContentPane().setLayout(null);
    frame.setSize(403, 175);
//    frame.setLocation(150, 150);    
    
    JLabel1.setText("User ID:");
    frame.getContentPane().add(JLabel1);
    JLabel1.setBounds(12, 12, 48, 24);
    
    JLabel2.setText("Password:");
    frame.getContentPane().add(JLabel2);
    JLabel2.setBounds(12, 42, 72, 24);
    
    JLabel3.setText("Database Name:");
    frame.getContentPane().add(JLabel3);
    JLabel3.setBounds(12, 72, 96,  24);
   
    frame.getContentPane().add(_uid);    
    _uid.setBounds(110, 12, 200, 24);
    
    frame.getContentPane().add(_pwd);
    _pwd.setBounds(110, 42, 200, 24);
    
    frame.getContentPane().add(dbList);
    dbList.setBounds(110, 72, 200, 24);
    
    
    _ok.setText("OK");
    frame.getContentPane().add(_ok);
    _ok.setBounds(60, 100, 100, 24);
   
    _cancel.setText("Cancel");
    frame.getContentPane().add(_cancel);
    _cancel.setBounds(264, 100, 100, 24);
    //}}

    //{{REGISTER_LISTENERS
    SymAction lSymAction = new SymAction();
    _ok.addActionListener(lSymAction);
    _cancel.addActionListener(lSymAction);
    dbList.addActionListener(lSymAction);
    //}}
    
    frame.setVisible(true);
    
    
    /**
     * Set some default values -- Qiang
     */
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    _ok.setBounds(40, 100, 100, 24);
    _cancel.setBounds(180, 100, 100, 24);
    frame.setSize(340, 170);
    //for test only
    _uid.setText("root");
    _pwd.setText("admin");
  }

  
  public void addNotify() {
    // Record the size of the window prior to calling parents addNotify.
    Dimension size = getSize();

    super.addNotify();

    if (frameSizeAdjusted)
      return;
    frameSizeAdjusted = true;

    // Adjust size of frame according to the insets
    Insets insets = getInsets();
    setSize(insets.left + insets.right + size.width, insets.top
        + insets.bottom + size.height);
  }

  // Used by addNotify
  boolean frameSizeAdjusted = false;

  //{{DECLARE_CONTROLS
  javax.swing.JLabel JLabel1 = new javax.swing.JLabel();
  javax.swing.JLabel JLabel2 = new javax.swing.JLabel();
  javax.swing.JLabel JLabel3 = new javax.swing.JLabel();
  
  JComboBox dbList = new JComboBox(databases);
  
  /**
   * The user ID entered.
   */
  javax.swing.JTextField _uid = new javax.swing.JTextField();
  javax.swing.JPasswordField _pwd = new javax.swing.JPasswordField();
  //javax.swing.JTextField _dbName = new javax.swing.JTextField();

  javax.swing.JButton _cancel = new javax.swing.JButton();
  javax.swing.JButton _ok = new javax.swing.JButton();

  //}}

  class SymAction implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent event) {
      Object object = event.getSource();
     
      if (object == _ok)
        Ok_actionPerformed(event);
      else if (object == _cancel)
        Cancel_actionPerformed(event);
      else if (object == dbList)
    	  Choose_db(event);
    }
  }

  /**
   * Called when ok is clicked.
   * 
   * @param event
   */
  void Ok_actionPerformed(java.awt.event.ActionEvent event) {	 
	  
	  String userName = _uid.getText();
	  char[] password = _pwd.getPassword();
	  

		  frame.setVisible(false);
		
		  Java_Connector model	= new Java_Connector(userName, password, dbName);
      	  QueryView	query		= new QueryView();
      	  View		view		= new View(model);                   
      	 
		  @SuppressWarnings("unused")
		  Controller	controller	= new Controller(model, view, query);
	    
  }

  /**
   * Called when cancel is clicked.
   * 
   * @param event
   */
  void Cancel_actionPerformed(java.awt.event.ActionEvent event) {
	  	  
//    _uid.setText("");
//    _pwd.setText("");
//    frame.setVisible(false);
	  //change hide to exit app --qiang
	  System.exit(0);
  }
  
  void Choose_db(java.awt.event.ActionEvent event) {
	   int selectedIndex = dbList.getSelectedIndex();
	   dbName = databases[selectedIndex];	 	  
	  }
  
}
