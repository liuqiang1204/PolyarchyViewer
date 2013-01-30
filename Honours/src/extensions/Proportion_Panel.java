package extensions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Proportion_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public Proportion_Panel(){
		super(true);
		this.setPreferredSize(new Dimension(50,50));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
		this.setBackground(Color.WHITE);
	}

	public void paint(Graphics g) {
		super.paint(g);
		this.setOpaque(true);
		int step = this.getWidth()/10;
		for(int i = 1;i<10;i++){
		g.drawLine(step*i, 0, step*i, 5);
		
		}
	}
	
}
