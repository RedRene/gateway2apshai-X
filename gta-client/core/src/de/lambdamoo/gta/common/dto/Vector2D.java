package de.lambdamoo.gta.common.dto;

public class Vector2D {
	private int x = 0;
	private int y = 0;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Vector2D(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

}
