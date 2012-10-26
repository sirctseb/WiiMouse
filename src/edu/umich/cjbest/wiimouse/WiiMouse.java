package edu.umich.cjbest.wiimouse;

import org.jdesktop.application.SingleFrameApplication;

import wiiremotej.WiiRemote;
import wiiremotej.event.WiiDeviceDiscoveredEvent;
import wiiremotej.event.WiiDeviceDiscoveryListener;

public class WiiMouse extends SingleFrameApplication implements WiiDeviceDiscoveryListener {
	
	private WiiRemote remote;

	@Override
	protected void startup() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
	}

}
