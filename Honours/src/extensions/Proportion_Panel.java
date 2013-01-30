package extensions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import project.GlobalConstants;

public class Proportion_Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Double> valuelist = new ArrayList<Double>();
	private ArrayList<Integer> posylist = new ArrayList<Integer>();
	
	public Proportion_Panel(){
		super(true);
		this.setPreferredSize(new Dimension(50,50));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
//		this.setBackground(Color.WHITE);
	}
	
	public void addPair(Double v,Integer y){
		this.valuelist.add(v);
		this.posylist.add(y);
	}

	public void clearAll(){
		this.valuelist.clear();
		this.posylist.clear();
		this.repaint();
	}
	public void paint(Graphics g) {
		super.paint(g);
		this.setOpaque(true);
		int step = this.getWidth()/10;
		g.setColor(Color.LIGHT_GRAY);
		for(int i = 1;i<10;i++){
			g.drawLine(step*i, 0, step*i, getHeight());		
		}
		
		int thickness = 9;
		g.setColor(GlobalConstants.bar_foreColor);
//		System.out.println("PPP:"+valuelist.size());
		for(int i=0;i<this.valuelist.size();i++){
			double value = valuelist.get(i);
			int posy = posylist.get(i);
			int w = (int) (getWidth()*value);
			int sx = getWidth()-w;
//			DecimalFormat df1 = new DecimalFormat("#.##");
//			g.drawString(df1.format(value*100)+"%", sx, posy-10);
			g.fillRect(sx, posy+15, w, thickness);
			
//			System.out.println("PPP:"+(getWidth()-w)+","+ (posy+15)+","+ w+","+ thickness);
		}
//		for(Double i:list){
//			int w = (int) (this.getWidth()*i);
//			int sx = this.getWidth()-w;
//			g.setColor(GlobalConstants.bar_foreColor);
//			g.fillRect(sx, sy, w, bh);
//			sy+=24;
//		}
	}
	
}
