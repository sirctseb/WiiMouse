package edu.umich.cjbest.wiimouse;

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
		// TODO Auto-generated method stub
		WiiRemoteJ.findRemotes(this, 1);
		// create remote event handler
		remoteHandler = new WiiRemoteEventHandler(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(WiiMouse.class, args);
	}

	// ---- WiiDeviceDiscoveryListener implementation ----
	
	@Override
	public void findFinished(int arg0) {
		// NOOP
	}

	@Override
	public void wiiDeviceDiscovered(WiiDeviceDiscoveredEvent arg0) {
		// store remote
		remote = (WiiRemote)arg0.getWiiDevice();
		remote.addWiiRemoteListener(remoteHandler);
	}

}
