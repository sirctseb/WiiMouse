package edu.umich.cjbest.wiimouse.gui;

import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
		
		setLayout(new FlowLayout());
		
		// create radio buttons for monitors
		for (GraphicsDevice s : screens) {
			Rectangle bounds = s.getDefaultConfiguration().getBounds();
			JRadioButton newButton = new JRadioButton(String.format("%d, %d", bounds.width, bounds.height));
			monitorButtons.add(newButton);
			this.add(newButton);
		}
	}
}
