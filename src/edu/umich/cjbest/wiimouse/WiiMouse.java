package edu.umich.cjbest.wiimouse;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

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
	
	void RemotePointed(Point2D location) {
		Point2D screenLocation = new Point2D.Double(
			location.getX() * bounds.width,
			(1-location.getY()) * bounds.height);
		robot.mouseMove((int)screenLocation.getX(), (int)screenLocation.getY());
	}

	@Override
	protected void startup() {
		// create robot
		try {
			GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			bounds = screen.getDefaultConfiguration().getBounds();
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
