package edu.umich.cjbest.wiimouse;

import wiiremotej.IRLight;
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
		// get lights
		IRLight[] lights = evt.getIRLights();
		// check for all four lights
		if(lights.length == 4) {
			// get lights in correct order
			lights = orderLights(lights);
			
			// TODO process perspective
		}
	}
	
	IRLight[] orderLights(IRLight[] lights) {
		// create return array
		IRLight[] ordered = new IRLight[4];
		
		// top left
		ordered[0] = findMax(lights, -1, 1);
		// top right
		ordered[1] = findMax(lights, 1, 1);
		// bottom right
		ordered[2] = findMax(lights, 1, -1);
		// bottom left
		ordered[3] = findMax(lights, -1, -1);
		
		// TODO check that they are unique
		
		return ordered;
	}
	
	IRLight findMax(IRLight[] lights, int xdir, int ydir) {
		IRLight max = lights[0];
		double maxdot = max.getX() * xdir + max.getY() * ydir;
		double dot;
		for(int i = 1; i < 4; i++) {
			dot = lights[i].getX() * xdir + lights[i].getY()*ydir;
			if(dot > maxdot) {
				maxdot = dot;
				max = lights[i];
			}
		}
		
		return max;
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
