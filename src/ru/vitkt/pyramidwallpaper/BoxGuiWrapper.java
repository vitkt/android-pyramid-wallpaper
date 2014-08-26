package ru.vitkt.pyramidwallpaper;

import org.jbox2d.dynamics.Body;

public class BoxGuiWrapper {
	private Body body;
	private int color;
	public Body getBody() {
		return body;
	}
	public void setBody(Body body) {
		this.body = body;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	public BoxGuiWrapper(Body _body, int _color) {
		setBody(_body);
		setColor(_color);
	}
}
