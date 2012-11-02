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
	private JTextArea txa_msg = new JTextArea();

	public Frame_Info() {
		super("Infomation Window");
		this.setLayout(new BorderLayout());
		this.setSize(400, 300);

		lbl_title.setText("");
		lbl_title.setHorizontalAlignment(JLabel.CENTER);
	
		jsp_middle = new JScrollPane(tbl_info);

		this.add(lbl_title, BorderLayout.NORTH);
		this.add(jsp_middle, BorderLayout.CENTER);

		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);

	}
	
	public void setValues(String title, ResultSet rs){
		this.lbl_title.setText(title);
		tbl_info.setModel(DbUtils.resultSetToTableModel(rs));
	}
	
	public void setValues(String title, String msg){
		this.lbl_title.setText(title);
		this.remove(jsp_middle);
		this.add(txa_msg,BorderLayout.CENTER);
		txa_msg.setEditable(false);
		txa_msg.setText(msg);
	}
}
