package de.mod10.smp;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class RobotHandler implements IRobotActors, SensorData {

	private Robot robot;
	private Grid grid;

	private Position pos;
	private Orientation orientation;


	public RobotHandler(Grid grid, Position initPos) {
		this.grid = grid;
		this.pos = initPos;

		robot = new Robot();
		robot.sensorEvent(this);
	}

	public void driveTo(Position pos) {
		robot.driveTo(pos);
	}

	@Override
	public void driveForward() {
		if (blockedFront())
			throw new IllegalStateException("Front is blocked");
		switch (orientation) {
			case EAST:
				pos.addToX(-1);
				break;
			case WEST:
				pos.addToX(1);
				break;
			case NORTH:
				pos.addToY(1);
				break;
			case SOUTH:
				pos.addToY(-1);
				break;
		}
	}

	@Override
	public void turnLeft() {
		orientation = Orientation.rotateLeft(orientation);
	}

	@Override
	public void turnRight() {
		orientation = Orientation.rotateRight(orientation);
	}

	@Override
	public void startUnload() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public Position pos() {
		return pos;
	}

	@Override
	public PositionType posType() {
		return grid.posType(pos);
	}

	@Override
	public Orientation posOrientation() {
		return orientation;
	}

	@Override
	public boolean blockedFront() {
		boolean[] neighbors = grid.areNeighborsBlocked(pos);
		return getRotatedNeighbor(neighbors, Direction.AHEAD, orientation);
	}

	@Override
	public boolean blockedLeft() {
		boolean[] neighbors = grid.areNeighborsBlocked(pos);
		return getRotatedNeighbor(neighbors, Direction.LEFT, orientation);
	}

	@Override
	public boolean blockedRight() {
		boolean[] neighbors = grid.areNeighborsBlocked(pos);
		return getRotatedNeighbor(neighbors, Direction.RIGHT, orientation);
	}

	@Override
	public boolean blockedWaypointFront() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public boolean blockedWaypointLeft() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public boolean blockedWaypointRight() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public boolean blockedCrossroadFront() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public boolean blockedCrossroadRight() {
		// TODO
		throw new NotImplementedException();
	}

	public static boolean getRotatedNeighbor(boolean[] neighbors, Direction dir, Orientation orientation) {
		int dirIndex = dir.getValue();
		int orientIndex = orientation.getValue();
		return neighbors[(dirIndex + orientIndex) % 4];
	}
}