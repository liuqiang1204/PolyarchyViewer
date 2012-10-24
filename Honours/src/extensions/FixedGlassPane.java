package extensions;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import selectors.selectableFonts;

// Based in part on code from the Java Tutorial for glass panes (java.sun.com).
// This version handles both mouse events and focus events.  The focus is
// held on the panel so that key events are also effectively ignored.  (But
// a KeyListener could still be attached by the program activating this pane.)

public class FixedGlassPane extends JPanel implements MouseListener, MouseMotionListener, FocusListener {
  
	/**
	 * Generated serial for java
	 */
	private static final long serialVersionUID = -8674627363778957987L;

	/**
	 *  helpers for redispatch logic
	 */
	Toolkit toolkit;

	JMenuBar menuBar;

	Container contentPane;

	boolean inDrag = false;

	/**
	 * trigger for redispatching (allows external control)
	 */
	boolean needToRedispatch = false;
	
	/**
	 * The label of information on the glass pane
	 */
	private JLabel control;
	
	/**
	 * The progress bar on the glass pane
	 */
	private JProgressBar waiter = new JProgressBar(0, 100);

	
	/**
	 * Constructor for the pane
	 * @param mb - the menu bar
	 * @param cp - the container
	 */
	public FixedGlassPane(JMenuBar mb) {
		toolkit = Toolkit.getDefaultToolkit();
		menuBar = mb;
		
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		((JComponent) contentPane).setOpaque(false);
        control = new JLabel("Please wait...");
        control.setFont(selectableFonts.getHeading1Font());
        contentPane.add(control);
        contentPane.add(waiter);
		
		
        setLayout(new GridLayout(0, 1));
        setOpaque(false);
        add(new JLabel()); // padding...
        add(new JLabel());
        add(contentPane);
    
        add(new JLabel());
        add(new JLabel());
		

		
		addMouseListener(this);
		addMouseMotionListener(this);
		addFocusListener(this);
	}

	public void setVisible(boolean v) {
		// Make sure we grab the focus so that key events don't go astray.
		if (v) {
			requestFocus();
		}
		super.setVisible(v);
	}

	// Once we have focus, keep it if we're visible
	public void focusLost(FocusEvent fe) {
		if (isVisible())
			requestFocus();
	}


	public void focusGained(FocusEvent fe) {
	}

	// We only need to redispatch if we're not visible, but having full control
	// over this might prove handy.
	public void setNeedToRedispatch(boolean need) {
		needToRedispatch = need;
	}

  /*
   * (Based on code from the Java Tutorial) We must forward at least the mouse
   * drags that started with mouse presses over the check box. Otherwise, when
   * the user presses the check box then drags off, the check box isn't
   * disarmed -- it keeps its dark gray background or whatever its L&F uses to
   * indicate that the button is currently being pressed.
   */
  public void mouseDragged(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseMoved(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseClicked(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseEntered(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseExited(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mousePressed(MouseEvent e) {
    if (needToRedispatch)
      redispatchMouseEvent(e);
  }

  public void mouseReleased(MouseEvent e) {
    if (needToRedispatch) {
      redispatchMouseEvent(e);
      inDrag = false;
    }
  }

  private void redispatchMouseEvent(MouseEvent e) {
    boolean inButton = false;
    boolean inMenuBar = false;
    Point glassPanePoint = e.getPoint();
    Component component = null;
    Container container = getContentPane();
    Point containerPoint = SwingUtilities.convertPoint(this,
        glassPanePoint, contentPane);
    int eventID = e.getID();

    if (containerPoint.y < 0) {
      inMenuBar = true;
      container = menuBar;
      containerPoint = SwingUtilities.convertPoint(this, glassPanePoint,
          menuBar);
      testForDrag(eventID);
    }

    if(container == null) {
    	return;
    } else {
        component = SwingUtilities.getDeepestComponentAt(container,
                containerPoint.x, containerPoint.y);
    }


    if (component == null) {
      return;
    } else {
      inButton = true;
      testForDrag(eventID);
    }

    if (inMenuBar || inButton || inDrag) {
      Point componentPoint = SwingUtilities.convertPoint(this,
          glassPanePoint, component);
      component.dispatchEvent(new MouseEvent(component, eventID, e
          .getWhen(), e.getModifiers(), componentPoint.x,
          componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
    }
  }
  
  	/**
  	 * Test to see if the mouse is being dragged
  	 * @param eventID
  	 */
	private void testForDrag(int eventID) {
		if (eventID == MouseEvent.MOUSE_PRESSED) {
    		inDrag = true;
		}
	}

	/**
	* @return the contentPane
	*/
	public Container getContentPane() {
		return contentPane;
	}

	/**
	 * @param contentPane the contentPane to set
	 */
	public void setContentPane(Container contentPane) {
		this.contentPane = contentPane;
	}

	/**
	 * @param control the control to set
	 */
	public void setControl(JLabel control) {
		this.control = control;
	}
	
	/**
	 * @return the control
	 */
	public JLabel getControl() {
		return control;
	}
	
	/**
	 * @param waiter the waiter to set
	 */
	public void setWaiter(JProgressBar waiter) {
		this.waiter = waiter;
	}
	
	/**
	 * @return the waiter
	 */
	public JProgressBar getWaiter() {
		return waiter;
	}
}