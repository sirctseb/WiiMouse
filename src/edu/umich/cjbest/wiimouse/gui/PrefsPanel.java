package edu.umich.cjbest.wiimouse.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.util.List;

public class PrefsPanel extends JPanel {

	// radio buttons to choose which monitor
	List<JRadioButton> monitorButtons = new LinkedList<JRadioButton>();
	//List<JText> monitorButtonLabels;
	
	public PrefsPanel() {
		super(null);
		
		final GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		// create radio buttons for monitors
		for (GraphicsDevice s : screens) {
			s.getDefaultConfiguration().getBounds();
			JRadioButton newButton = new JRadioButton("What's up?");
			monitorButtons.add(newButton);
			this.add(newButton);
		}
	}
}
