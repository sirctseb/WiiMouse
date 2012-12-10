package edu.umich.cjbest.wiimouse.gui;

import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.umich.cjbest.wiimouse.WiiMouse;

import java.util.List;

public class PrefsPanel extends JPanel {
	public WiiMouse delegate;

	// radio buttons to choose which monitor
	List<MonitorButton> monitorButtons = new LinkedList<MonitorButton>();
	ButtonGroup monitorGroup = new ButtonGroup();
	
	private class MonitorButton extends JRadioButton {
		int screen;
		public MonitorButton(String label, int screen) {
			super(label);
			this.screen = screen;
			
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						delegate.selectMonitor(MonitorButton.this.screen);
					}
				}
			});
		}
	}
	
	public PrefsPanel() {
		super(null);
		
		final GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		setLayout(new FlowLayout());
		
		// create radio buttons for monitors
		int screenIndex = 0;
		for (GraphicsDevice s : screens) {
			Rectangle bounds = s.getDefaultConfiguration().getBounds();
			MonitorButton newButton = new MonitorButton(String.format("%d, %d", bounds.width, bounds.height), screenIndex++);
			monitorButtons.add(newButton);
			monitorGroup.add(newButton);
			this.add(newButton);
		}
	}
}
