package edu.umich.cjbest.wiimouse;

import java.awt.geom.Point2D;

import wiiremotej.IRLight;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;
import javax.media.jai.PerspectiveTransform;

public class WiiRemoteEventHandler extends WiiRemoteAdapter {
	
	PerspectiveTransform transform;
	WiiMouse delegate;
	
	WiiRemoteEventHandler(WiiMouse delegate) {
		this.delegate = delegate;
	}
	
	// clamp function
	static double clamp(double value, double low, double high) {
		return value < low ? low : (value > high ? high : value);
	}
	
	@Override
	public void IRInputReceived(WRIREvent evt) {
		// get lights
		IRLight[] lights = evt.getIRLights();
		// check for all four lights
		if(lights[0] != null && lights[1] != null
				&& lights[2] != null && lights[3] != null) {
			System.out.println("found four lights");
			// get lights in correct order
			lights = orderLights(lights);
			
			// generate transform
			transform = PerspectiveTransform.getQuadToQuad(
					lights[0].getX(), lights[0].getY(),
					lights[1].getX(), lights[1].getY(),
					lights[2].getX(), lights[2].getY(),
					lights[3].getX(), lights[3].getY(),
					0,1,
					1,1,
					1,0,
					0,0);
			
			// transform midpoint
			Point2D half = new Point2D.Double(0.5, 0.5);
			Point2D midpoint = new Point2D.Double();
			transform.transform(half, midpoint);
			System.out.println(midpoint.toString());
			
			// clamp to [0,1]
			midpoint.setLocation(clamp(midpoint.getX(), 0, 1), clamp(midpoint.getY(), 0, 1));
			// notify delegate
			delegate.RemotePointed(midpoint);
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
		delegate.StatusReported(e);
	}
	
	@Override
	public void buttonInputReceived(WRButtonEvent e) {
		//System.out.println(e.toString());
		// inform delegate of trigger press
		if(e.wasPressed(WRButtonEvent.B)) {
			delegate.TriggerPressed();
		}
		if(e.wasReleased(WRButtonEvent.B)) {
			delegate.TriggerReleased();
		}
		if(e.wasPressed(WRButtonEvent.PLUS)) {
			delegate.PlusPressed();
		}
		if(e.wasPressed(WRButtonEvent.MINUS)) {
			delegate.MinusPressed();
		}
		if(e.wasPressed(WRButtonEvent.A)) {
			delegate.APressed();
		}
		if(e.wasPressed(WRButtonEvent.ONE)) {
			delegate.OnePressed();
		}
	}
}
