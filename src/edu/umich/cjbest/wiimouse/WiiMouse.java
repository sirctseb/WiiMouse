package edu.umich.cjbest.wiimouse;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import osxadapter.OSXAdapter;

import wiiremotej.IRSensitivitySettings;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiDeviceDiscoveredEvent;
import wiiremotej.event.WiiDeviceDiscoveryListener;

public class WiiMouse extends SingleFrameApplication implements WiiDeviceDiscoveryListener {
	
	private WiiRemote remote;
	WiiRemoteEventHandler remoteHandler;
	Robot robot;
	Rectangle bounds;
	Point2D history[];
	int history_length = 2;
	int history_index = 0;
	// log file writer
	FileWriter logFileWriter;
	
	void RemotePointed(Point2D location) {
		Point2D screenLocation = new Point2D.Double(
			bounds.getMinX() + location.getX() * bounds.width,
			bounds.getMinY() + (1-location.getY()) * bounds.height);
		System.out.println(screenLocation.toString());
		addPoint(screenLocation);
		Point2D avg = getAveragePoint();
		robot.mouseMove((int)avg.getX(), (int)avg.getY());
	}
	
	void TriggerPressed() {
		System.out.println("got trigger press, robot mouse pressing");
		// left mouse down on trigger
		robot.mousePress(InputEvent.BUTTON1_MASK);
		
		try {
			logFileWriter.write("mouse down at\t" + new Date().getTime() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void TriggerReleased() {
		System.out.println("got trigger release, robot mouse releasing");
		// left mouse up on trigger up
		robot.mouseRelease(InputEvent.BUTTON1_MASK);

		try {
			logFileWriter.write("mouse up at\t" + new Date().getTime() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void PlusPressed() {
		System.out.println("plus pressed, increasing avg length");
		history_length++;
	}
	void MinusPressed() {
		System.out.println("minus pressed, decreasing avg length");
		if(history_length > 1) {
			history_length--;
		}
	}

	@Override
	protected void startup() {
		// create robot
		try {
			//GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];
			bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1].getDefaultConfiguration().getBounds();
			GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			//bounds = screen.getDefaultConfiguration().getBounds();
			System.out.println(bounds.toString());
			robot = new Robot(screen);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// create remote event handler
		remoteHandler = new WiiRemoteEventHandler(this);
		// search for remotes
		WiiRemoteJ.findRemotes(this, 1);
		System.out.println("started finding remotes");
		
		// register for events
		registerForMacOSXEvents();
		
		// create log file
		try {
			logFileWriter = new FileWriter(new File("WiiMouseLog.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// initialize point history
		history = new Point2D.Double[100];
	}
	
	public boolean quitApp() {
		// TODO disconnect remote
		remote.disconnect();
		
		// close log file
		try {
			logFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		exit();
		
		return false;
	}
	
	/*
	 * MAC OS X HOOKS
	 */	
	private void registerForMacOSXEvents() {
		//if (Util.MAC_OS_X) {
	        try {
				OSXAdapter.setQuitHandler(this, WiiMouse.class.getDeclaredMethod("quitApp", (Class[])null));				
				/*if (!Util.INSIDE_APP_BUNDLE) {
					OSXAdapter.setAboutHandler(af, AboutWindow.class.getDeclaredMethod("about", (Class[])null));
				}
				OSXAdapter.setPreferencesHandler(pf, PreferencesWindow.class.getDeclaredMethod("preferences", (Class[])null));*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}
	
	void addPoint(Point2D point) {
		System.out.println(history_index);
		history[history_index] = point;
		history_index = (history_index+1) % history_length;
	}
	Point2D getAveragePoint() {
		Point2D.Double result = new Point2D.Double();
		double x = 0;
		double y = 0;
		for(int i = 0; i < history_length; i++) {
			if(history[i] == null) {
				history[i] = new Point2D.Double();
			}
			x += history[i].getX();
			y += history[i].getY();
		}
		x /= history_length;
		y /= history_length;
		result.setLocation(x, y);
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		Application.launch(WiiMouse.class, args);
	}

	// ---- WiiDeviceDiscoveryListener implementation ----
	
	@Override
	public void findFinished(int arg0) {
		System.out.println("finished finding devices");
		// NOOP
	}

	@Override
	public void wiiDeviceDiscovered(WiiDeviceDiscoveredEvent arg0) {
		if(arg0.getWiiDevice() instanceof WiiRemote) {
			System.out.println("discovered device");
			// store remote
			remote = (WiiRemote)arg0.getWiiDevice();
			remote.addWiiRemoteListener(remoteHandler);
			// max sensitivity according to http://wiibrew.org/index.php?title=Wiimote#Sensitivity_Settings 
			final byte[] MAX_SENSITIVITY_BLOCK1 = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x90, 0x00, 0x41 };
			final byte[] MAX_SENSITIVITY_BLOCK2 = new byte[] { 0x40, 0x00 };
			try {
				remote.setIRSensorEnabled(true, WRIREvent.BASIC, new IRSensitivitySettings(MAX_SENSITIVITY_BLOCK1, MAX_SENSITIVITY_BLOCK2));
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				System.out.println("Illegal argument fail enabling IR");
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				System.out.println("Illegal state fail enabling IR");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println("exception enabling IR");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//remote.setIRSensorEnabled(true, WRIREvent.BASIC, WWPreferences.SENSITIVITY_SETTINGS);
			System.out.println("added remote listener");
			try {
				remote.setLEDIlluminated(0, true);
				remote.setUseMouse(false);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("discovered non-WiiRemote");
		}
	}

}
