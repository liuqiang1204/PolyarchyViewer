/**
 * 
 */
package extensions;

import java.awt.BorderLayout;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import utilities.DbUtils;

/**
 * @author Qiang Liu
 * 
 */
public class Frame_Info extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel lbl_title = new JLabel();
	private JTable tbl_info = new JTable();
	private JScrollPane jsp_middle;
	private JScrollPane jsp_middle1;
	private JTextArea txa_msg = new JTextArea();

	public Frame_Info() {
		super("Infomation Window");
		this.setLayout(new BorderLayout());
		this.setSize(400, 300);

		lbl_title.setText("");
		lbl_title.setHorizontalAlignment(JLabel.CENTER);
	
		jsp_middle = new JScrollPane(tbl_info);
		jsp_middle1 = new JScrollPane(txa_msg);
		this.add(lbl_title, BorderLayout.NORTH);
		this.add(jsp_middle, BorderLayout.CENTER);
		this.add(jsp_middle1,BorderLayout.CENTER);
		txa_msg.setLineWrap(true);
		txa_msg.setWrapStyleWord(true);
		txa_msg.setEditable(false);
		jsp_middle1.setVisible(false);
		
		this.setLocationRelativeTo(null);
//		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(false);

	}
	
	public void setValues(String title, ResultSet rs){
		this.lbl_title.setText(title);
		tbl_info.setModel(DbUtils.resultSetToTableModel(rs));
		jsp_middle.setVisible(true);
		jsp_middle1.setVisible(false);
		this.add(jsp_middle,BorderLayout.CENTER);
	}
	
	public void setValues(String title, String msg){
		this.lbl_title.setText(title);
		this.jsp_middle.setVisible(false);
		jsp_middle1.setVisible(true);		
		txa_msg.setText(msg);
		this.add(jsp_middle1,BorderLayout.CENTER);
	}
}
