package extensions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;

public class CustomSlider extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double c_min = 0.0;
	public double c_max = 100.0;
	
	public double min_scalevalue;
	public double max_scalevalue;
	
	public boolean isMinPressed = false;
	public boolean isMaxPressed = false;
	//the postion of min/max btn controlled by paint() and mouse
	private int min_posx = 0;
	private int min_posy = 0;
	private int max_posx = 0;
	private int max_posy = 0;
	
	
	int xpos_start = 5;
	int xpos_end = (int) (getWidth()*0.98 - xpos_start);
	int line_length = xpos_end-xpos_start; 
	int line1_ypos = (int) (this.getHeight()*0.5)-10;
	int line2_ypos = line1_ypos+10;
	
	Hierarchy owner_hierarchy;
	
	public CustomSlider(double minx,double maxx,Hierarchy h){
		this.min_scalevalue = minx;
		this.max_scalevalue = maxx;
		M_Listener ml = new M_Listener(this);
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
		this.setPreferredSize(new Dimension(60,50));
		this.setSize(60,50);
		this.setBorder(new EmptyBorder(40, 0, 0, 0));
		this.owner_hierarchy = h;		
	}
	
	public void setScaleValue(double minx,double maxx){
		this.min_scalevalue = minx;
		this.max_scalevalue = maxx;
		this.repaint();
	}
	
	public double getScaleMin(){
		return c_min*(max_scalevalue-min_scalevalue)/100;
	}
	
	public double getScaleMax(){
		return c_max*(max_scalevalue-min_scalevalue)/100;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		DecimalFormat df = new DecimalFormat("#.##");
		Font f = this.getFont();
		FontMetrics fm = this.getFontMetrics(f);
		
		//draw line at center
		xpos_start = 25;
		xpos_end = this.getWidth()-xpos_start+3;
		line_length = xpos_end-xpos_start; 
		line1_ypos = (int) (this.getHeight()*0.5)-10;
		line2_ypos = line1_ypos+20;
		

		g.drawLine(xpos_start, line1_ypos, xpos_end, line1_ypos);
		g.fillRect(xpos_start, line1_ypos-2, 4, 4);
		g.fillRect(xpos_end-2, line1_ypos-2, 4, 4);
		
		//draw text for center line
		String str = df.format(min_scalevalue);		
		g.drawChars(str.toCharArray(), 0, str.length(), xpos_start, line1_ypos-5);
		
		str = df.format(max_scalevalue);
		int strl = SwingUtilities.computeStringWidth(fm, str);
		g.drawChars(str.toCharArray(), 0, str.length(), xpos_end-strl, line1_ypos-5);
		//draw bottom scale line
		int tmpx=(xpos_start+xpos_end)/2; //50%
		int tmpx1 = (xpos_start+tmpx)/2; //25%
		int tmpx2 = (xpos_end+tmpx)/2; //75%
		g.drawLine(xpos_start, line2_ypos, xpos_end, line2_ypos);
		g.drawLine(xpos_start, line2_ypos, xpos_start, line2_ypos+5);
		g.drawLine(xpos_end, line2_ypos, xpos_end, line2_ypos+5);
		g.drawLine(tmpx, line2_ypos, tmpx, line2_ypos+5);
		g.drawLine(tmpx1, line2_ypos, tmpx1, line2_ypos+3);
		g.drawLine(tmpx2, line2_ypos, tmpx2, line2_ypos+3);
		 
		//draw text for bottom scale
		str = df.format(this.getScaleMin());
		g.drawChars(str.toCharArray(), 0, str.length(), xpos_start, line2_ypos+12);
		
		str = df.format(this.getScaleMax());		
		strl = SwingUtilities.computeStringWidth(fm, str); 
		g.drawChars(str.toCharArray(), 0, str.length(), xpos_end-strl, line2_ypos+12);
		
		//draw filled shape
		Polygon p = new Polygon();
		p.addPoint(xpos_start, line2_ypos);
		p.addPoint(xpos_end, line2_ypos);
		p.addPoint((int) (xpos_start+c_max*line_length/100), line2_ypos-19);
		p.addPoint((int) (xpos_start+c_min*line_length/100), line2_ypos-19);
		g.setColor(Color.LIGHT_GRAY);
		g.fillPolygon(p);
		
		//place min max slider btn
		Polygon p1 = new Polygon();
		int px = (int) (xpos_start+c_min*line_length/100);
		int py = line1_ypos;
		if(this.isMinPressed){
			g.setColor(Color.BLUE);			
		}
		else{			
			g.setColor(Color.BLACK);
		}
		this.min_posx = px;
		this.min_posy = py;
		p1.addPoint(px,py);
		p1.addPoint(px-8,py-10);
		p1.addPoint(px,py-10);
		g.fillPolygon(p1);
		
		
		Polygon p2 = new Polygon();
		px = (int) (xpos_start+c_max*line_length/100);
		py = line1_ypos;		
		if(this.isMaxPressed){
			g.setColor(Color.BLUE);
		}
		else {
			g.setColor(Color.BLACK);
		}
		this.max_posx = px;
		this.max_posy = py;
		p2.addPoint(px,py);
		p2.addPoint(px,py-10);
		p2.addPoint(px+8,py-10);
		g.fillPolygon(p2);

	}
	
	
	private class M_Listener implements MouseInputListener{

		CustomSlider owner;
		public M_Listener(CustomSlider cs){
			this.owner = cs;
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent me) {
			// TODO Auto-generated method stub
			int x = me.getX();
			int y = me.getY();
			
			//in min area
			if(x>owner.min_posx-10&&x<owner.min_posx
					&&y<owner.min_posy+5&&y>owner.min_posy-15){
				owner.isMinPressed = true;
				owner.repaint();
			}
			//in max area
			if(x>owner.max_posx&&x<owner.max_posx+10
					&&y<owner.max_posy+5&&y>owner.max_posy-15){
				owner.isMaxPressed = true;
				owner.repaint();
			}			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if(owner.isMinPressed||owner.isMaxPressed){
				owner.isMinPressed=false;		
				owner.isMaxPressed=false;
				owner.repaint();
				double t_min = owner.getScaleMin();
				double t_max = owner.getScaleMax();
				owner.owner_hierarchy.setScaleRange(t_min, t_max);
				owner.repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent me) {
//			System.out.println("DDD:"+me.getX());
			if(owner.isMinPressed){
				int x = me.getX();
				if(x>=owner.max_posx)x=owner.max_posx-1;
				if(x<owner.xpos_start)x=owner.xpos_start;
				owner.c_min = (x-owner.xpos_start)*100/owner.line_length;
				owner.repaint();
			}		
			if(owner.isMaxPressed){
				int x = me.getX();
				if(x<=owner.min_posx)x=owner.min_posx+1;
				if(x>owner.xpos_end)x=owner.xpos_end;
				owner.c_max = (x-owner.xpos_start)*100/owner.line_length;
				owner.repaint();
			}
			
		}

		@Override
		public void mouseMoved(MouseEvent me) {

		}
		
	}

}
