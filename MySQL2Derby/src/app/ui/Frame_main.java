/**
 * 
 */
package app.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * @author Qiang Liu
 *
 */
public class Frame_main extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//toolbar
	private JToolBar m_toolbar = new JToolBar();
	private JButton m_btn_About = new JButton("About");
	private JPanel m_pane_src = new JPanel();
	private JPanel m_pane_dest = new JPanel();
	
	public Frame_main(){
		super("MySQL2Derby");
		
		//tool bar
		m_toolbar.setFloatable(false);
		m_toolbar.setPreferredSize(new Dimension(600,20));
		m_toolbar.add(m_btn_About);
		
		//source - mysql panel (left)
		m_pane_src.setBorder(BorderFactory.createTitledBorder("MySQL"));
		m_pane_src.setPreferredSize(new Dimension(400,580));
		//dest (right)
		m_pane_dest.setBorder(BorderFactory.createTitledBorder("Derby"));
		//add all panel to frame
		this.setLayout(new BorderLayout());
		this.add(m_toolbar,BorderLayout.NORTH);
		this.add(m_pane_src,BorderLayout.WEST);
		this.add(m_pane_dest,BorderLayout.CENTER);
		
		//set default panel layout
		this.setPreferredSize(new Dimension(800,600));		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
