package extensions;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import selectors.selectableFonts;

/**
 * This query view allows advanced users to have advanced queries of the data
 * At this stage it is not properly implemented
 * @author Anthony Scata
 * @version 1.1
 *
 */
public class QueryView extends JFrame {

	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = -4098357908987672314L;
	
	JPanel tally;
	JPanel measure;
	JPanel option;
	JPanel construct;
	JPanel send;
	
	JLabel tally_label;
	JComboBox tally_options;
	
	JLabel measure_label;
	JCheckBox publications;
	JSlider publicationsWeight;
	JCheckBox grants;
	JSlider grantsWeight;
	JCheckBox people;
	JSlider peopleWeight;
	JCheckBox subItems;
	JSlider subItemsWeight;
	JCheckBox hdr;
	JSlider hdrWeight;
	
	JLabel option_label;
	JComboBox option_options;
	
	JLabel construct_label;
	JLabel formula_label;
	JTextField formula_field;
	JLabel custom_label;
	JTextField custom_field;
	
	JButton send_button;
	
	String [] tally_available = {"Max", "Min", "Average", "Sum"};
	String [] options_available = {"AND", "OR", "NOT", "XOR"};
	
	public QueryView() {
		
		int min = 0;
		int max = 100;
		int initial = 50;
		int tickMajor = 10;
		int tickMinor = 5;
		boolean tick = true;
		boolean labels = true;
		
		Border b1 = BorderFactory.createTitledBorder("");
		Font heading = selectableFonts.getHeading1Font();
		
		
		tally = new JPanel();
		measure  = new JPanel();
		option  = new JPanel();
		construct  = new JPanel();
		send  = new JPanel();
		
		tally.setBorder(b1);
		measure.setBorder(b1);
		option.setBorder(b1);
		construct.setBorder(b1);
		send.setBorder(b1);
		
		tally.setLayout(new BoxLayout(tally, BoxLayout.Y_AXIS));
		measure.setLayout(new BoxLayout(measure, BoxLayout.PAGE_AXIS));
		option.setLayout(new BoxLayout(option, BoxLayout.PAGE_AXIS));
		construct.setLayout(new BoxLayout(construct, BoxLayout.PAGE_AXIS));
		send.setLayout(new BoxLayout(send, BoxLayout.PAGE_AXIS));
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		getContentPane().add(tally);
		getContentPane().add(measure);
		getContentPane().add(option);
		getContentPane().add(construct);
		getContentPane().add(send);
		
		tally_label = new JLabel("Tally");
		tally_label.setFont(heading);
		tally_options = new JComboBox(tally_available);
		tally_options.setSelectedIndex(0);
		
		tally.add(tally_label);
		tally.add(tally_options);
		
		Check_Box_Action cba = new Check_Box_Action();
		
		measure_label = new JLabel("Measure");
		measure_label.setFont(heading);
		
		publications = new JCheckBox("Publications");
	    publications.setMnemonic(KeyEvent.VK_P); 
	    publications.setSelected(false);
	    publications.setName("publications");
	    publications.addItemListener(cba);
	    
	    publicationsWeight = new JSlider(JSlider.HORIZONTAL, min, max, initial);
	    publicationsWeight.setMajorTickSpacing(tickMajor);
	    publicationsWeight.setMinorTickSpacing(tickMinor);
	    publicationsWeight.setPaintTicks(tick);
	    publicationsWeight.setPaintLabels(labels);
	    publicationsWeight.setVisible(false);
	    publicationsWeight.addChangeListener(new Slider_Action());

	    grants = new JCheckBox("Grants");
	    grants.setMnemonic(KeyEvent.VK_G);
	    grants.setName("grants");
	    grants.setSelected(false);
	    grants.addItemListener(cba);
	    
	    grantsWeight = new JSlider(JSlider.HORIZONTAL, min, max, initial);
	    grantsWeight.setMajorTickSpacing(tickMajor);
	    grantsWeight.setMinorTickSpacing(tickMinor);
	    grantsWeight.setPaintTicks(tick);
	    grantsWeight.setPaintLabels(labels);
	    grantsWeight.setVisible(false);
	    grantsWeight.addChangeListener(new Slider_Action());
	    
	    people = new JCheckBox("People");
	    people.setMnemonic(KeyEvent.VK_E);
	    people.setName("people");
	    people.setSelected(false);
	    people.addItemListener(cba);

	    peopleWeight = new JSlider(JSlider.HORIZONTAL, min, max, initial);
	    peopleWeight.setMajorTickSpacing(tickMajor);
	    peopleWeight.setMinorTickSpacing(tickMinor);
	    peopleWeight.setPaintTicks(tick);
	    peopleWeight.setPaintLabels(labels);
	    peopleWeight.setVisible(false);
	    peopleWeight.addChangeListener(new Slider_Action());
	    
	    subItems = new JCheckBox("SubItems");
	    subItems.setMnemonic(KeyEvent.VK_S);
	    subItems.setName("subItems");
	    subItems.setSelected(false);
	    subItems.addItemListener(cba);
	    
	    subItemsWeight = new JSlider(JSlider.HORIZONTAL, min, max, initial);
	    subItemsWeight.setMajorTickSpacing(tickMajor);
	    subItemsWeight.setMinorTickSpacing(tickMinor);
	    subItemsWeight.setPaintTicks(tick);
	    subItemsWeight.setPaintLabels(labels);
	    subItemsWeight.setVisible(false);
	    subItemsWeight.addChangeListener(new Slider_Action());
	    
	    hdr = new JCheckBox("HDR");
	    hdr.setMnemonic(KeyEvent.VK_H);
	    hdr.setName("hdr");
	    hdr.setSelected(false);
	    hdr.addItemListener(cba);

	    hdrWeight = new JSlider(JSlider.HORIZONTAL, min, max, initial);
	    hdrWeight.setMajorTickSpacing(tickMajor);
	    hdrWeight.setMinorTickSpacing(tickMinor);
	    hdrWeight.setPaintTicks(tick);
	    hdrWeight.setPaintLabels(labels);
	    hdrWeight.setVisible(false);
	    hdrWeight.addChangeListener(new Slider_Action());
	    
	    measure.add(measure_label);
	    measure.add(publications);
	    measure.add(publicationsWeight);
	    measure.add(grants);
	    measure.add(grantsWeight);
	    measure.add(people);
	    measure.add(peopleWeight);
	    measure.add(subItems);
	    measure.add(subItemsWeight);
	    measure.add(hdr);
	    measure.add(hdrWeight);
		
		JLabel option_label;
		JComboBox option_options;
		
		option_label = new JLabel("Options");
		option_label.setFont(heading);
		option_options = new JComboBox(options_available);
		option_options.setSelectedIndex(0);
		
		option.add(option_label);
		option.add(option_options);
		
		construct_label = new JLabel("Construct Query");
		construct_label.setFont(heading);
		formula_label = new JLabel("Formula");
		formula_label.setFont(heading);
		formula_field = new JTextField();
		custom_label = new JLabel("Custom Query");
		custom_label.setFont(heading);
		custom_field = new JTextField();
		
		construct.add(construct_label);
		construct.add(formula_label);
		construct.add(formula_field);
		construct.add(custom_label);
		construct.add(custom_field);
		
		send_button = new JButton("Construct");
		
		send.add(send_button);
		
        //Set the look and feel of the frame
        lookANDfeel();
            
        //setFocusable(true);
		//this.setSize(height, width);
    
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        //The final attributes of the display
		pack();
		//setVisible(true);
		
		
	}
	
	public void lookANDfeel() {
		String plaf = "";
		//The look and feel we want
		plaf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		//Try and set the look and feel and this might error
		try {
			UIManager.setLookAndFeel(plaf);
			SwingUtilities.updateComponentTreeUI(this);
		}
		//Catch the error that occurs and display some information
		catch (Exception e) {
			System.out.println("An error has occured when changing the look and feel of the display");
			e.printStackTrace();
		}
	}
	
	//TODO
	//need a way of controlling or setting them
	//that way you cannot change its value
	public void slider_control(JSlider source, boolean action) {
	
		//counter variables required
	    int total = 0;
	    int num = 0;
	    int set = 0;
	    int average = 0;
	    
	    //if the slider has stopped moving
	    if (!source.getValueIsAdjusting()) {
	        
	    	//get the total for this slider
	    	int its_total = (int)source.getValue();
	    		    	
	        //add it to the total
	        //total+= its_total;
	        	        
		    //get the other slider values that are visible
		    if(publicationsWeight.isVisible()) {
    			total += publicationsWeight.getValue();
    			num++;
    			
    			if(!publicationsWeight.isEnabled()) {
    				set++;
    			}
    			
		    }
		    
		    if(grantsWeight.isVisible()) {
    			total += grantsWeight.getValue();
    			num++;
    			
    			if(!grantsWeight.isEnabled()) {
    				set++;
    			}
    			
		    }
		    
		    if(peopleWeight.isVisible()) {
    			total += peopleWeight.getValue();
    			num++;
    			
    			if(!peopleWeight.isEnabled()) {
    				set++;
    			}
    			
		    }
		    
		    if(subItemsWeight.isVisible()) {
    			total += subItemsWeight.getValue();
    			num++;
    			
    			if(!subItemsWeight.isEnabled()) {
    				set++;
    			}
    			
		    }
		    
		    if(hdrWeight.isVisible()) {
    			total += hdrWeight.getValue();
    			num++;
    			
    			if(!hdrWeight.isEnabled()) {
    				set++;
    			}
    			
		    }
		    
		    System.out.println("TOTAL " +total);
		    	
		    //the total must be equal to zero 
		    if(total!= 100 && (num - 1) != 0) {
		    	
		    	average = (100 - its_total) / (num - 1);
		    	
		    	System.out.println("AVEERAGE " +average + " NUM " +num+ " SET " +set);
		    	
		    	 //get the other slider values that are visible
			    if(publicationsWeight.isVisible() && (!source.equals(publicationsWeight) || !action) && publicationsWeight.isEnabled()) {
	    			
			    	publicationsWeight.setValueIsAdjusting(true);
			    	/*if(total < 100) {
			    		int value = publicationsWeight.getValue() + average;
			    		publicationsWeight.setValue(value);
			    	} else {*/
			    		publicationsWeight.setValue(average);
			    	//}
			    	//publicationsWeight.setValueIsAdjusting(false);
			    	
			    } 
			    
			    if(grantsWeight.isVisible()&& (!source.equals(grantsWeight) || !action) && grantsWeight.isEnabled()) {
			    	grantsWeight.setValueIsAdjusting(true);
			    	grantsWeight.setValue(average);
			    	//grantsWeight.setValueIsAdjusting(false);
			    	
			    }
			    
			    if(peopleWeight.isVisible() && (!source.equals(peopleWeight) || !action) && peopleWeight.isEnabled()) {
			    	peopleWeight.setValueIsAdjusting(true);
			    	peopleWeight.setValue(average);
			    	//peopleWeight.setValueIsAdjusting(false);
			    }
			    
			    if(subItemsWeight.isVisible()&& (!source.equals(subItemsWeight) || !action) && subItemsWeight.isEnabled()) {
			    	subItemsWeight.setValueIsAdjusting(true);
			    	subItemsWeight.setValue(average);
			    	//subItemsWeight.setValueIsAdjusting(false);
			    }
			    
			    if(hdrWeight.isVisible()&& (!source.equals(hdrWeight) || !action) && hdrWeight.isEnabled()) {
			    	hdrWeight.setValueIsAdjusting(true);
			    	hdrWeight.setValue(average);
			    	//hdrWeight.setValueIsAdjusting(false);
			    }
		    	
		    }
	    }
		
	}
	
	/**
	 * Perform an action when the check boxes are used
	 * @author Anthony Scata
	 * @version 1.0
	 *
	 */
	class Check_Box_Action implements ItemListener {

		/**
		 * Perform an event when a check box item is selected or deselected
		 */
		public void itemStateChanged(ItemEvent e) {
			
			//get the check box that was interacted with
		    JCheckBox source = (JCheckBox) e.getItemSelectable();

		    //if the box was selected or not
		    boolean selected = false;
		    
		    //if the it was delected then we want that to reflect the change
		    if(e.getStateChange() == ItemEvent.DESELECTED) {
		    	selected = false;
		    } else {
		    	selected = true;
		    }
		    
		    JSlider slider = null;
		    
		    //find the source of the interaction and show the appropriate slider
		    if(source.getName().equals("publications")) {
    			slider = publicationsWeight;
    		} else if(source.getName().equals("grants")) {
    			slider = grantsWeight;
        	} else if(source.getName().equals("people")) {
        		slider = peopleWeight;
    		} else if(source.getName().equals("subItems")) {
    			slider = subItemsWeight;
    		} else if(source.getName().equals("hdr")) {
    			slider = hdrWeight;
    		}	
		    
		    slider.setVisible(selected);
		    
		    slider_control(slider, false);
		}
		
	}

	/**
	 * Controls the actions of all of the sliders
	 * @author Anthony Scata
	 * @version 1.0
	 *
	 */
	class Slider_Action implements ChangeListener {

		/**
		 * When the state is changed on the slider perform an action
		 * We look at the percentage and then decrement the others to get a balance
		 * Cannot have more than 100% as the total
		 */
		public void stateChanged(ChangeEvent e) {
				
			//the source of the interaction
		    slider_control((JSlider)e.getSource(), true);
		}
	}
	
	class send_Action implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO
			//need to get the data from all of the other components
			//then send it through to another method that will perform the operations
			
		}
		
	}
}
