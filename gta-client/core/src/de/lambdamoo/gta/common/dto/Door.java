package de.lambdamoo.gta.common.dto;

public class Door {

	private Room exit1 = null;
	private int exitDoorType = Room.VAL_NULL;
	private Room exit2 = null;

	private int tileX = -1;
	private int tileY = -1;
	private int tileCountX = -1;
	private int tileCountY = -1;

	public int getTileX() {
		return tileX;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
	}

	public int getTileCountX() {
		return tileCountX;
	}

	public void setTileCountX(int tileCountX) {
		this.tileCountX = tileCountX;
	}

	public int getTileCountY() {
		return tileCountY;
	}

	public void setTileCountY(int tileCountY) {
		this.tileCountY = tileCountY;
	}

	public Room getExit1() {
		return exit1;
	}

	public void setExit1(Room exit1) {
		this.exit1 = exit1;
	}

	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Door(Room exit1, Room exit2, int exitDoorType, int tileX, int tileY, int tileCountX, int tileCountY) {
		super();
		this.exit1 = exit1;
		this.exitDoorType = exitDoorType;
		this.exit2 = exit2;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileCountX = tileCountX;
		this.tileCountY = tileCountY;
	}

	public Room getOtherExit(Room self) {
		if (self != null) {
			if (exit1.equals(self)) {
				return exit2;
			} else {
				return exit1;
			}
		} else {
			return null;
		}

	}

	public int getDoorType() {
		return exitDoorType;
	}

	public void setDoorType(int exitDoorType) {
		this.exitDoorType = exitDoorType;
	}

	public Room getExit2() {
		return exit2;
	}

	public void setExit2(Room exit2) {
		this.exit2 = exit2;
	}

}
