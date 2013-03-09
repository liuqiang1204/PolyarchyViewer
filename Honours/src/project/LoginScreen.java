package project;

import java.awt.Dimension;
//import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
//import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import connector.Java_Connector;
import extensions.QueryView;

/**
 * Dialog box to prompt for user id/password and the database to connect to.
 */

/**
 * add support to sqlite3
 * 
 * @author Qiang Liu
 * 
 */

public class LoginScreen extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * The databases to connect to
	 */
	protected boolean isValid = false;
	protected String dbName = "sample";
	public String propfilepath = "";

	JFrame frame = new JFrame("Polyarchy Viewer - Login");

	// Used by addNotify
	boolean frameSizeAdjusted = false;

	// {{DECLARE_CONTROLS
	javax.swing.JLabel JLabel1 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel2 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel3 = new javax.swing.JLabel();
	javax.swing.JLabel lbl_host = new javax.swing.JLabel();
	javax.swing.JLabel lbl_dbtype = new javax.swing.JLabel("Database type:");

	DefaultComboBoxModel dbtype_model = new DefaultComboBoxModel();
	JComboBox dbtype = new JComboBox(dbtype_model);
	DefaultComboBoxModel dbList_model = new DefaultComboBoxModel();
	JComboBox dbList = new JComboBox(dbList_model);

	/**
	 * The user ID entered.
	 */
	javax.swing.JTextField _uid = new javax.swing.JTextField();
	javax.swing.JPasswordField _pwd = new javax.swing.JPasswordField();
	javax.swing.JTextField _host = new javax.swing.JTextField();
	// javax.swing.JTextField _dbName = new javax.swing.JTextField();

	javax.swing.JButton _cancel = new javax.swing.JButton();
	javax.swing.JButton _ok = new javax.swing.JButton();

	public LoginScreen(String propfile) {

		Properties prop = new Properties();
		propfilepath = propfile;
		
		try {
			FileInputStream in = new FileInputStream(propfilepath);
			prop.load(in);
			for(String str : prop.getProperty("databaseTypes").split(",")){
				dbtype_model.addElement(str);
			}
			String ss = dbtype.getSelectedItem().toString().trim()+"DBList";
			for(String str : prop.getProperty(ss).split(",")){
				dbList_model.addElement(str);
			}
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.getContentPane().setLayout(null);

			lbl_host.setText("Host:");
			frame.getContentPane().add(lbl_host);

			JLabel1.setText("User ID:");
			JLabel2.setText("Password:");
			JLabel3.setText("Database Name:");
			_ok.setText("OK");
			_cancel.setText("Cancel");

			frame.getContentPane().add(JLabel1);
			frame.getContentPane().add(JLabel2);
			frame.getContentPane().add(JLabel3);
			frame.getContentPane().add(_host);
			frame.getContentPane().add(lbl_dbtype);
			frame.getContentPane().add(_uid);
			frame.getContentPane().add(_pwd);
			frame.getContentPane().add(dbtype);
			frame.getContentPane().add(dbList);
			frame.getContentPane().add(_ok);
			frame.getContentPane().add(_cancel);

			frame.setSize(340, 200);
			lbl_host.setBounds(12, 10, 100, 24);
			JLabel1.setBounds(12, 35, 100, 24);
			JLabel2.setBounds(12, 60, 100, 24);
			lbl_dbtype.setBounds(12, 85, 100, 24);
			JLabel3.setBounds(12, 110, 100, 24);

			_host.setBounds(110, 10, 200, 24);
			_uid.setBounds(110, 35, 200, 24);
			_pwd.setBounds(110, 60, 200, 24);
			dbtype.setBounds(110, 85, 200, 24);
			dbList.setBounds(110, 110, 200, 24);
			_ok.setBounds(40, 145, 100, 24);
			_cancel.setBounds(180, 145, 100, 24);

			// {{REGISTER_LISTENERS
			SymAction lSymAction = new SymAction();
			_ok.addActionListener(lSymAction);
			_cancel.addActionListener(lSymAction);
			dbList.addActionListener(lSymAction);
			dbtype.addActionListener(lSymAction);
			// }}

			/**
			 * Set some default values -- Qiang
			 */
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
			_host.setText(prop.getProperty("defaultHost"));
			_uid.setText(prop.getProperty("defaultUID"));
			_pwd.setText(prop.getProperty("defaultPWD"));

			in.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

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

	// }}

	class SymAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
			Object object = event.getSource();

			if (object == _ok)
				Ok_actionPerformed(event);
			else if (object == _cancel)
				Cancel_actionPerformed(event);
			else if (object == dbList)
				Choose_db(event);
			else if (object == dbtype)
				Choose_dbtype(event);
		}

	}

	/**
	 * Called when ok is clicked.
	 * 
	 * @param event
	 */
	void Ok_actionPerformed(java.awt.event.ActionEvent event) {

		String userName = _uid.getText();
		String host = _host.getText();
		char[] password = _pwd.getPassword();

		frame.setVisible(false);
		String dbtp = this.dbtype.getSelectedItem().toString();
		dbName = this.dbList.getSelectedItem().toString();
		Java_Connector model = new Java_Connector(host, dbtp, userName,
				password, dbName);
		QueryView query = new QueryView();
		View view = new View(model);

		@SuppressWarnings("unused")
		Controller controller = new Controller(model, view, query);
	}

	/**
	 * Called when cancel is clicked.
	 * 
	 * @param event
	 */
	void Cancel_actionPerformed(java.awt.event.ActionEvent event) {

		// _uid.setText("");
		// _pwd.setText("");
		// frame.setVisible(false);
		// change hide to exit app --qiang
		System.exit(0);
	}

	void Choose_db(java.awt.event.ActionEvent event) {

	}
	

	void Choose_dbtype(ActionEvent event) {
		String dbt = dbtype.getSelectedItem().toString();
		String ss = dbt+"DBList";
		
		Properties prop = new Properties();
		
		try {
			FileInputStream in = new FileInputStream(propfilepath);
			prop.load(in);
			dbList_model.removeAllElements();
			for(String str : prop.getProperty(ss).split(",")){
				dbList_model.addElement(str);
			}
			in.close();
		}
		catch(Exception e){
			System.exit(-2);
		}
	}

}
