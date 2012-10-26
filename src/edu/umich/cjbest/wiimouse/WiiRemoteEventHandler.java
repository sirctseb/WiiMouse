package edu.umich.cjbest.wiimouse;

import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class WiiRemoteEventHandler extends WiiRemoteAdapter {
	
	WiiMouse delegate;
	
	WiiRemoteEventHandler(WiiMouse delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void IRInputReceived(WRIREvent evt) {
		
	}
}
