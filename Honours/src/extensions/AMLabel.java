package extensions;

import images.Roll_Images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import project.View;
import selectors.selectableColours;
import selectors.selectableFonts;


/**
 * This is my extension of the JLabel
 * Required because we need to store additional information with the label
 * Rather than store the information in a another data structure use the labels
 * We need them for operations so it make sense to keep the information together
 * @author Anthony Scata
 * @version 5.2
 *
 */
public class AMLabel extends JLabel {

	/**
	 * Some java serial
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The additional information we want to store for label display
	 */
	
	/**
	 * The long or primary text
	 */
	private String primaryText;
	
	/**
	 * The short or secondary text
	 */
	private String secondaryText;
	
	/**
	 * A unique id (needs to be a string for leading zero problems)
	 */
	private String uniqueID;
	
	/**
	 * The level that this labels is in
	 */
	private int level = 1;
	
	/**
	 * Labels position within the panel
	 */
	private int position = 0;
	
	/**
	 * If the label has a parent
	 */
	private boolean has_parent = false;

	/**
	 * The id of the labels parent if it has one
	 */
	private String parent_id = null;
	
	/**
	 * If the label has been clicked or not
	 */
	private boolean clicked = false;
	
	/**
	 * If this label has an associated bar
	 */
	private boolean has_bar = false;
	
	/**
	 * The bar that this label has, also a label for direct reference
	 */
	private AMLabel its_bar = null;
	
	/**
	 * If this label is a bar or not
	 */
	private boolean is_bar = false;
	
	/**
	 * The image icon that corresponds to this label
	 */
	private AMLabel its_image = null;
	
	/**
	 * If this label has an image label
	 */
	private boolean has_image = false;
	
	/**
	 * If the label is an image
	 */
	private boolean is_image = false;
	
	/**
	 * not available  = -1, collapsed = 0, tmp expanded = 1, expanded = 2
	 * the image changed when 1 and 2 --qiang
	 * 5 - highlighted
	 */
	private int status = 0;
	
	/**
	 * The overall count for a bar, only set for bars
	 */
	private double count = 0.00;
	
	/**
	 * The sub count for the bar, only set for bars
	 */
	private double sub_count = 0.00;
	
	/**
	 * If the label has been counted in the visualisation
	 */
	private boolean counted = false;
	
	/**
	 * The width we are going to set this bar
	 */
	private int bar_width;
	
	/**
	 * The height that the bar will be set to
	 */
	private int bar_height;
	
	/**
	 * Integers used to split the strings at the end
	 */
	private static int endIndex = 4;
	
	/**
	 * How much left padding to be added to the label
	 */
	private int left_padding = 0;
	
	/**
	 * How the count will be displayed
	 */
	private String count_option = "plain";
	
	/**
	 * If the image is a bullet or not
	 */
	private boolean bullet = false;
	
	/**
	 * The colour of the background, needed to clicking
	 */
	private Color view_background = Color.BLACK;
	
	/**
	 * The website for that label
	 */
	private String website = "";
	
	private boolean active = false;
	
	/**
	 * Status control:
	 * Behaviors: enter leave click doubleclick check 
	 * status: Normal/expand selected/- checked/unchecked
	 * Rewrite by Qiang
	 */
	//status controller --Qiang
	public boolean is_selected = false;
	public boolean is_checked = false;
	public boolean is_expanded = false;
	public AMLabel owner;
	public int limitSize=0;
	
//	public ArrayList<AMLabel> parents = new ArrayList<AMLabel>();
//	public ArrayList<AMLabel> children = new ArrayList<AMLabel>();
	
//	public boolean is_checkboxShowed = false;
	
//	private JCheckBox lbl_chk = new JCheckBox();
	
//	public AMLabel its_parent=null;
	
	
	/*CONSTRUCTOR*/
	
	/**
	 * Default constructor
	 * Everything is set to empty or 0
	 */
	public AMLabel() {
		super("");
		add_Contents("", "", "0", "", 1, 0);
	}
	
	
	/**
	 * This constructor sets all of the strings to the parameter 
	 * The secondary string is set to a substring of the parameter @param text
	 * @param text - The string to which all of the strings will be set
	 */
	public AMLabel(String text) {
		
		//Set the default text
		super(text);
		
		add_Contents(text, text_split_letter(text), "0", "", 1, 0);
	}

	
	/**
	 * This constructor specifies the text values
	 * No text operations are performed
	 * @param text - The text to be displayed
	 * @param primary - The primary text for the label
	 * @param secondary - The secondary text for the label
	 */
	public AMLabel(String text, String primary, String secondary) {
		super(text);
		add_Contents(primary, secondary, "0", "", 1, 0);
	}

	
	/**
	 * This constructor sets all of the values
	 * No text operations are performed
	 * @param text - The text to be displayed
	 * @param primary - The primary text for the label
	 * @param secondary - The secondary text for the label
	 * @param id - The unique id for the label
	 */
	public AMLabel(String text, String primary, String secondary, String id) {
		super(text);
		add_Contents(primary, secondary, id, "", 1, 0);
	}
	

	/**
	 * This constructor sets all of the values
	 * No text operations are performed
	 * @param text - The text to be displayed
	 * @param primary - The primary text for the label
	 * @param secondary - The secondary text for the label
	 * @param id - The unique id for the label
	 * @param this_level - The level of this label
	 */
	public AMLabel(String text, String primary, String secondary, String id, int this_level) {
		super(text);
		add_Contents(primary, secondary, id, "", level, 0);
	}

	
	/**
	 * This constructor set the display and secondary text to the same thing
	 * Also allows the id to be set
	 * @param text -The text to be used for the primary, display and secondary text
	 * @param id - The unique id to be used
	 */
	public AMLabel(String text, String id) {
		super(text);
		add_Contents(text, text, id, "", 1, 0);
	}
	
	
	/**
	 * This constructor allows an icon to be added to the label
	 * @param text - The text for the primary text and to be shortened for the display text
	 * @param id - The unique id of the label
	 * @param image - The icon to be added to the label
	 */
	public AMLabel(String text, String id, Icon image) {
		super(text_split_letter(text), image, SwingConstants.LEFT);
		add_Contents(text, text_split_letter(text), id, "", 1, 0);
	}

	
	/**
	 * This constructor allow for an icon to be added in a specific place
	 * @param text - The text for the primary text and to be shortened for the display text
	 * @param id - The unique id of the label
	 * @param image - The icon to be added to the label
	 * @param location - The positioning of the icon
	 */
	public AMLabel(String text, String id, Icon icon, int location) {
		super(text_split_letter(text), icon, location);
		add_Contents(text, text_split_letter(text), id, "", 1, 0);
	}

	
	/**
	 * This constructor allow only an image to be placed within the label
	 * @param image - The image to be used in the label
	 */
	public AMLabel(Icon image) {
		super(image);
	}


	/**
	 * This constructor allows the image to be set with its positioning
	 * @param icon - The image to be used in the label
	 * @param positioning - the positioning of the icon in the label
	 */
	public AMLabel(ImageIcon icon, int positioning) {
		super("", icon, positioning);
	}
	

	/**
	 * Add the details to the label
	 * This is more generic to have this in a method rather than in the constructors
	 * @param primary - the new primary text
	 * @param secondary - the new secondary text
	 * @param id - the new id
	 * @param web - the new website
	 * @param its_level - its new level
	 * @param its_position - its new position
	 */
	public void add_Contents(String primary, String secondary, String id, String web, int its_level, int its_position) {
		
		primaryText = primary;
		secondaryText = secondary;
		uniqueID = id;
		website = web;
		level = its_level;
		position = its_position;
		
		//set double buffered to be on
		setDoubleBuffered(true);
	}

	
	/*OVERIDDEN METHODS*/

	/**
	 * Used to paint the rectangle on top of the label
	 * Only set of labels that are defined as being a bar
	 * Set the count (total) and sub_count (comparison) before entering this method
	 */
	protected void paintComponent(Graphics g) {
    
		if(this.has_bar){
			this.sub_count = this.getIts_bar().sub_count;
			this.count = this.getIts_bar().count;
			this.its_image.count = count;
			this.its_image.sub_count = sub_count;
		}
		
		if(this.count<this.limitSize){
			this.setVisible(false);
			return;
		}
		else{
			this.setVisible(true);
		}
		//update tooltip --Qiang
		this.updateTooltip();
		//only paint after loaded to save time
	   if(View.loaded) {
		   super.paintComponent(g);
		   
		   setHorizontalAlignment(JLabel.LEFT);
		   
		   //if this is an image, then we need to constantly constrain the size
		   //this is a bug / oversight that the grid keeps changing the size so we must overwrite this
		   if(is_image) {
			   setSize(new Dimension(15, 15));
	    		
				//the new image
				ImageIcon image = null;
				
				//Determine the image on its status
				if(bullet) {
					
					//if it is a bullet, keep it that way
					image = Roll_Images.getBullet();
					
				} else if(!isVisible()) {
					
					//if its not visible then we want it to be closed
					status = 0;
					image = Roll_Images.getClosed();
					
				} else {
					
//					System.out.println("img label status-->"+this.status);
					
//					if(status == 1||status==2) {
//						
//						//If the status is 1 we want to open it (2???--qiang)
//						
//						image = Roll_Images.getOpen();
//					
//					} else {
//						
//						//otherwise close it
//						image = Roll_Images.getClosed();
//					}
					if(this.owner.is_expanded)image = Roll_Images.getOpen();
					else image = Roll_Images.getClosed();
				}
				
				//will always to opaque
				setOpaque(false);
				
				//Set the new image
				setIcon(image);
	    		
	    	} else if(is_bar) {
	    		
	    		//Cast the graphic to be a 2d graphic
		        Graphics2D g2 = (Graphics2D)g;
		        int bw = bar_width;
		        if(count_option.equals("numerical")) {
		        	
		        	//set the height
		        	bar_height = 10;
		    		
					//set the text to be the count
		        	//Round the sub_count when display --Qiang
		        	DecimalFormat df = new DecimalFormat("#.###");
		    		setText(df.format(sub_count)+ " / " +df.format(count));
		        	
		    		//set the border colour
		    		g2.setColor(selectableColours.getBorderBar());
		    		
		    		//add our own border, its better optimised this way
		    		g2.drawRect(0, 0, bw, bar_height-1);
		    		
				} else {
					
					//set the height
					bar_height = 5;

					//we now have no text
					setText(" ");
					
					//set the colour for the bar
					g2.setColor(selectableColours.getWholeBar());
					
					//a bar of the correct width and height
					g2.fillRect(0, 0, bw, bar_height);
//					System.out.println(this.getText()+"--Bar Width:" + this.getBar_width());
				} 
		        
		        //get metrics from the graphics
		        FontMetrics metrics = g.getFontMetrics(getFont());
		        
		        //find the width of the text
		        int adv = metrics.stringWidth(getText());
		        
		        //if the bar width is greater than the text then the text will fit 
		        if(getBar_width() > adv) {
		        	adv = getBar_width();
		        }

		        //Set the colour for the inside of the bar
		        g2.setColor(selectableColours.getInnerBar());	 
	
				//set the bar to a preferred size of the label that will allow the text to fit
				setPreferredSize(new Dimension(adv, bar_height));
	
				//always opaque
				setOpaque(true);        
		        
		        //The variable to hold the percentage
		        double percentage = 0;
		        
		        //If we have a count
		        if(count > 0) {
		        	
		        	//The percentage is equal to the sub count / the count or total
		        	//Needs to be a double for precision or no result will be calculated 
		        	
		        	if(sub_count >= count) {
		        		percentage = 1;
		        	} else {
		        		//??why count+1??? --qiang
//		        		percentage = (double)sub_count / (count + 1);

//		        		percentage = (double)sub_count / count;
		        		
		        		//change to log(length+1)
		        		percentage = Math.log(sub_count+1) / Math.log(count+1);
		        	}
		        }  else {
		        	
		        	//no count so have a very small percentage
		        	percentage = .01;
		        }
		        
		        //get the width we want to fill as a percentage of the total width 
		        int fill_width = (int) (getBar_width() * percentage);
		                
		        //if we are adding the inner bar from the count
		        if(!count_option.equals("numerical")) {
			        
		        	//Fill the label with a rectangle
			        g2.fillRect(0, (bar_height/2), fill_width, bar_height);
			        
//			        System.out.println(this.getText()+"--Bar red width:" + fill_width);
		        }
		        
	        }  else {
	        	
	        	
	        	//if clicked set the background to darker
	        	//if not clicked set the background to transparent
	        	
	        	//always opaque
	        	setOpaque(true);
	        	
	        	//The font to be used
				Font font = null;
	        	
	        	if(isClicked()) {
	 
	        		//Set the new background to be darker than the background underneath
	    			
	    			//Get the RBG components of the colour
	    		    int red = view_background.getRed();
	    		    int green = view_background.getGreen();
	    		    int blue = view_background.getBlue();
	    	
	    		    //Get the hue, saturation and brightness of the colour
	    		    float[] hsb = Color.RGBtoHSB(red, green, blue, null);
	    		    float hue = hsb[0]; 
	    		    float saturation = hsb[1];
	    		    float brightness = hsb[2];
	    		    
	    		    //Set the new colour to be darker than the previous
	    		    int rgb = Color.HSBtoRGB(hue, saturation, brightness-.10f);
	    			
	    		    //Set the background to the new colour
	    			setBackground(new Color(rgb));
	    			
	    			//Set the text as black
	    			setForeground(selectableColours.getClickColor());
	        		
	        	} else {
	 
	        		//we want a transparent background
	        		if(is_checked) setBackground(new Color(224,238,224));
	        		else setBackground(new Color(0, true));     
	    			
					//The font depends on the level that the label is on
					switch(getLevel()) {
					case 1:
						font = selectableFonts.getMediumFont();
						break;
					case 2:
						font = selectableFonts.getSmallFont();
						break;
					case 3:
						font = selectableFonts.getSmallestFont();
						break;
					default:
						font = selectableFonts.getSmallFont();
						break;
					}
	        	}
	        	
				if(active) {
					//The font depends on the level that the label is on and set the font
					switch(getLevel()) {
					case 1:
						font = selectableFonts.getMediumFontClick();
						break;
					case 2:
						font = selectableFonts.getSmallFontClick();
						break;
					case 3:
						font = selectableFonts.getSmallestFontClick();
						break;
					default:
						font = selectableFonts.getSmallFontClick();
						break;
					}
				}
	        	
	        	//set the font for the label
				setFont(font);
				
				//If it has been counted set the colour
	        	if(isCounted()) {
	        		setForeground(selectableColours.getOtherColor());
	        	} else {
	        		setForeground(selectableColours.getNormalColor());
	        	}
	        	
	        	/**
	        	 * add a check box for text lbl
	        	 */
//	        	if(!getText().trim().equals("")){
//		        	FontMetrics metrics = g.getFontMetrics(getFont());
//		        	int adv = metrics.stringWidth(getText());
//		    		this.add(this.lbl_chk);	    		
//		    		this.lbl_chk.setBounds(adv+10,0, 20, 18);
//		    		this.lbl_chk.setVisible(is_checkboxShowed);
//		    		this.lbl_chk.setVisible(true);
//		    		this.validate();
//	        	}
	        }
	   }
    }

    
    /*METHODS*/
	
	/**
	 * This method creates the secondary text
	 * Fundamentally it splits the string on spaces for abbreviated labels
	 * @param text - The text to be split and abbreviated
	 * @return - the text created by the method
	 */
	public static String text_split_letter(String text) {
		
		//If we have enough text
		if(text.length() >= endIndex) {
			
			//Split the words in the string
			String[] split = text.split(" ");
			String second = "";
			
			//now loop through the words and take the first letter to be used to shorten the word
			for(int i = 0; i < split.length; i++) {
				if(split[i].length() >= 2) {
					second += split[i].substring(0, 1);	
				}
			}
			
			//return the new text
			return second;
		} else {
			
			//the text is small so just return it
			return text;
		}
	}

	
	/**
	 * Set the parent of this label
	 * Required for knowing something about the hierarchical structure
	 * @param new_parent - The unique id of the parent for this label
	 */
	public void parent(String new_parent) {
		
		//Check to see if we have a parent id
		if(new_parent.length() > 0) {
			has_parent = true;
		} else {
			has_parent = false;
		}
		
		//set the parent id
		parent_id = new_parent;
	}
	
	
	/**
	 * Set the bar for this label
	 * Required for instant access to the bar
	 * The has_bar is set by this method
	 * @param new_bar - The bar for this label
	 */
	public void bar(AMLabel new_bar) {
		
		//Make sure we have sent an object
		if(new_bar != null) {
			
			//Set that this label has a bar and what it is
			has_bar = true;
			its_bar = new_bar;
		} else {
			
			//Otherwise we have no bar for this label
			has_bar = false;
		}
	}
	
	
	/**
	 * Set the image for this label
	 * Required so we can perform operations quickly on it
	 * The has_image is set by this method
	 * @param image - The image label
	 */
	public void image(AMLabel image) {
	
		//Make sure we have sent an object
		if(image != null) {
			
			//Set that this label has an image and what it is
			setHas_image(true);
			setIts_image(image);
			image.setIs_image(true);
		} else {
			
			//Otherwise we have no image for this label
			setHas_image(false);
		}
	}
	
	
	/**
	 * Cut off the text to be used for the display text
	 * @param - if the action was an entry or an exit
	 */
	public void cut_off(boolean entry) {
		
		//Set the cut off text length
		int cut_off = 65;
		
		//The text for the label
		String text = "";
		
		//If it was an entry use the primary text
		if(entry) {
			text = getPrimaryText();
		} else {
			text = getSecondaryText();
		}
		
		//Now cut the text if we need to
		if(text.length() > cut_off ) {
			setText(text.substring(0, cut_off));
		} else {
			setText(text);
		}
	}
	
	
	/**
	 * Changes the state of the click
	 * If it was clicked then it is removed
	 * If it wasn't clicked then it is
	 */
	public void clicked() {

		if(isClicked()) {
			setClicked(false);
		} else {
			setClicked(true);
		}
	}
	
	/**
	 * Change the visibility of a label
	 * @param visible - what the visibility will be set to
	 */
	public void change(boolean visible) {
		
		//Set the attributes of the label
		setVisible(visible);
		setEnabled(visible);
	}
	
	/**
	 * This method sets the labels text depending on the event type
	 * @param entry - If the event was an entry or an exit
	 */
	public void change_text(boolean entry) {

		//We don't want to change the text of a bar or image
		if(!isIs_bar() && !isIs_image()) {
			
			active = entry;
			
			//Cut the primary text for the text being shown
			cut_off(entry);
		}
	}
	
	/**
	 * Set the label so that when we repaint it it will have its original appearance
	 */
	public void set_appearance_original() {
		
		//Make sure we don't have a bar or image
		if(!isIs_bar() && !isIs_image()) {
		
			//this will make the appearance original when we repaint it
			setClicked(false);
			setCounted(false);
		}
	}
	
	/**
	 * We want to clear this label
	 * Some logic here depending on the level of the label and its contents
	 * @param label - The label to be cleared
	 * @param remove_count - if we want to remove the sub count 
	 */
	public void clear_just_this(boolean remove_count) {
		
		//if we want to remove the sub count then do it
		if(remove_count) {
			setSub_count(0);
		}
	
		//set the text
		set_appearance_original();

		//if it isn't in the top level then don't show it
		//if the label isn't showable then don't show it
		if(getLevel() >= 2) {
			
			//change the appearance
			change(false);
			
		} else {
		
			//anything in level 1 or above should be shown
			
			//set the status to closed
			setStatus(0);
			change(true);
		}
	}
	
	
	/**
	 * Clear this label and its corresponding labels
	 * If it has a bar that will be cleared as will the image
	 * @param remove_count - if we want to remove the count
	 */
	public void clearThisSet(boolean remove_count) {
		
		//clear this label
		clear_just_this(remove_count);
		
		if(has_bar) {
			
			//clear this bar
			its_bar.clear_just_this(remove_count);
		}
			
		if(has_image) {
			
			//clear thus image
			its_image.clear_just_this(remove_count);
		}
	}
	
	public void status(boolean changeIcon, int i) {

		if(changeIcon) {
			status = i;
		}
	}
	

	/*GETTERS AND SETTERS*/

	/**
	 * @return the primaryText
	 */
	public String getPrimaryText() {
		return primaryText;
	}


	/**
	 * @param primaryText the primaryText to set
	 */
	public void setPrimaryText(String primaryText) {
		this.primaryText = primaryText;
	}


	/**
	 * @return the secondaryText
	 */
	public String getSecondaryText() {
		return secondaryText;
	}


	/**
	 * @param secondaryText the secondaryText to set
	 */
	public void setSecondaryText(String secondaryText) {
		this.secondaryText = secondaryText;
	}


	/**
	 * @return the uniqueID
	 */
	public String getUniqueID() {
		return uniqueID;
	}


	/**
	 * @param uniqueID the uniqueID to set
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}


	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}


	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}


	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}


	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}


	/**
	 * @return the has_parent
	 */
	public boolean isHas_parent() {
		return has_parent;
	}


	/**
	 * @param hasParent the has_parent to set
	 */
	public void setHas_parent(boolean hasParent) {
		has_parent = hasParent;
	}


	/**
	 * @return the parent_id
	 */
	public String getParent_id() {
		return parent_id;
	}


	/**
	 * @param parentId the parent_id to set
	 */
	public void setParent_id(String parentId) {
		parent_id = parentId;
	}


	/**
	 * @return the clicked
	 */
	public boolean isClicked() {
		return clicked;
	}


	/**
	 * @param clicked the clicked to set
	 */
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}


	/**
	 * @return the has_bar
	 */
	public boolean isHas_bar() {
		return has_bar;
	}


	/**
	 * @param hasBar the has_bar to set
	 */
	public void setHas_bar(boolean hasBar) {
		has_bar = hasBar;
	}


	/**
	 * @return the its_bar
	 */
	public AMLabel getIts_bar() {
		return its_bar;
	}


	/**
	 * @param itsBar the its_bar to set
	 */
	public void setIts_bar(AMLabel itsBar) {
		its_bar = itsBar;
	}


	/**
	 * @return the is_bar
	 */
	public boolean isIs_bar() {
		return is_bar;
	}


	/**
	 * @param isBar the is_bar to set
	 */
	public void setIs_bar(boolean isBar) {
		is_bar = isBar;
	}


	/**
	 * @return the its_image
	 */
	public AMLabel getIts_image() {
		return its_image;
	}


	/**
	 * @param itsImage the its_image to set
	 */
	public void setIts_image(AMLabel itsImage) {
		its_image = itsImage;
	}


	/**
	 * @return the has_image
	 */
	public boolean isHas_image() {
		return has_image;
	}


	/**
	 * @param hasImage the has_image to set
	 */
	public void setHas_image(boolean hasImage) {
		has_image = hasImage;
	}


	/**
	 * @return the is_image
	 */
	public boolean isIs_image() {
		return is_image;
	}


	/**
	 * @param isImage the is_image to set
	 */
	public void setIs_image(boolean isImage) {
		is_image = isImage;
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/**
	 * @return the count
	 */
	public double getCount() {
		return count;
	}


	/**
	 * @param count the count to set
	 */
	public void setCount(double count) {
		this.count = count;
	}


	/**
	 * @return the sub_count
	 */
	public double getSub_count() {
		return sub_count;
	}


	/**
	 * @param subCount the sub_count to set
	 */
	public void setSub_count(double subCount) {
		sub_count = subCount;
	}


	/**
	 * @return the counted
	 */
	public boolean isCounted() {
		return counted;
	}


	/**
	 * @param counted the counted to set
	 */
	public void setCounted(boolean counted) {
		this.counted = counted;
	}


	/**
	 * @return the bar_width
	 */
	public int getBar_width() {
		return bar_width;
	}


	/**
	 * @param barWidth the bar_width to set
	 */
	public void setBar_width(int barWidth) {
		bar_width = barWidth;
	}


	/**
	 * @return the bar_height
	 */
	public int getBar_height() {
		return bar_height;
	}


	/**
	 * @param left_padding the left_padding to set
	 */
	public void setLeft_padding(int left_padding) {
		this.left_padding = left_padding;
	}


	/**
	 * @return the left_padding
	 */
	public int getLeft_padding() {
		return left_padding;
	}


	/**
	 * @return the count_option
	 */
	public String getCount_option() {
		return count_option;
	}


	/**
	 * @param countOption the count_option to set
	 */
	public void setBar_option(String countOption) {
		count_option = countOption;
	}


	/**
	 * @return the bullet
	 */
	public boolean isBullet() {
		return bullet;
	}


	/**
	 * @param bullet the bullet to set
	 */
	public void setBullet(boolean bullet) {
		this.bullet = bullet;
	}
	

	/**
	 * @return the view_background
	 */
	public Color getView_background() {
		return view_background;
	}


	/**
	 * @param viewBackground the view_background to set
	 */
	public void setView_background(Color viewBackground) {
		view_background = viewBackground;
	}


	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}


	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}
	
	/**
	 * Update tool-tips for the label
	 * @author Qiang Liu
	 */
	public void updateTooltip(){
		DecimalFormat df = new DecimalFormat("#.###");
		if(this.has_bar){
			double sub = this.getIts_bar().getSub_count();
			double total = this.getIts_bar().getCount();
			String s=df.format(sub)+ " / " +df.format(total);
			this.setToolTipText(s);
			this.getIts_bar().setToolTipText(s);
			if(this.has_image)this.getIts_image().setToolTipText(s);
		}
	}
}