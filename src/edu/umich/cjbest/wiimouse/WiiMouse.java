package edu.umich.cjbest.wiimouse;

import java.io.IOException;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WiiDeviceDiscoveredEvent;
import wiiremotej.event.WiiDeviceDiscoveryListener;

public class WiiMouse extends SingleFrameApplication implements WiiDeviceDiscoveryListener {
	
	private WiiRemote remote;
	WiiRemoteEventHandler remoteHandler;

	@Override
	protected void startup() {
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
