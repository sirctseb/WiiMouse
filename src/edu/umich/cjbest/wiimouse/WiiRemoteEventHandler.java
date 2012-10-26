package edu.umich.cjbest.wiimouse;

import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;

public class WiiRemoteEventHandler extends WiiRemoteAdapter {
	
	WiiMouse delegate;
	
	WiiRemoteEventHandler(WiiMouse delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void IRInputReceived(WRIREvent evt) {
		System.out.println("IRInputReceived");
	}
	
	@Override
	public void statusReported(WRStatusEvent e) {
		System.out.println(e.toString());
	}
	
	@Override
	public void buttonInputReceived(WRButtonEvent e) {
		System.out.println(e.toString());
	}
}
