package de.mod10.smp;

import de.mod10.smp.helper.*;

import java.awt.*;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class RobotHandler implements IRobotActors, SensorData {

	private Robot robot;
	private Grid grid;
	private Color color = Color.RED;

	private Position pos;
	private Orientation orientation = Orientation.NORTH;


	public RobotHandler(Grid grid, Position initPos) {
		this.grid = grid;
		this.pos = initPos;

		robot = new Robot(this);
		robot.sensorEvent(this);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void driveTo(Position pos) {
		robot.driveTo(pos);
	}

	public void step() {
		robot.step();
	}

	@Override
	public void driveForward() {
		if (blockedFront())
			throw new IllegalStateException("Front is blocked");
		switch (orientation) {
			case WEST:
				pos.addToX(-1);
				break;
			case EAST:
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
		System.out.println("Unload");
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
		return getRotatedNeighbor(neighbors, Direction.AHEAD, orientation) ||
				grid.isRobot(grid.nextPosition(pos, orientation)) != null;
	}

	@Override
	public boolean blockedLeft() {
		boolean[] neighbors = grid.areNeighborsBlocked(pos);
		return getRotatedNeighbor(neighbors, Direction.LEFT, orientation) ||
				grid.isRobot(grid.nextPosition(pos, Orientation.rotateLeft(orientation))) != null;
	}

	@Override
	public boolean blockedRight() {
		boolean[] neighbors = grid.areNeighborsBlocked(pos);
		return getRotatedNeighbor(neighbors, Direction.RIGHT, orientation) ||
				grid.isRobot(grid.nextPosition(pos, Orientation.rotateRight(orientation))) != null;
	}

	@Override
	public boolean blockedWaypointFront() {
		return grid.blockedWaypoint(pos, orientation, Direction.AHEAD);
	}

	@Override
	public boolean blockedWaypointLeft() {
		return grid.blockedWaypoint(pos, orientation, Direction.LEFT);
	}

	@Override
	public boolean blockedWaypointRight() {
		return grid.blockedWaypoint(pos, orientation, Direction.RIGHT);
	}

	@Override
	public boolean blockedCrossroadFront() {
		return grid.blockedCrossroadFront(pos, orientation);
	}

	@Override
	public boolean blockedCrossroadRight() {
		return grid.blockedCrossroadFront(pos, Orientation.rotateRight(orientation));
	}

	private static boolean getRotatedNeighbor(boolean[] neighbors, Direction dir, Orientation orientation) {
		int dirIndex = dir.getValue();
		int orientIndex = orientation.getValue();
		return neighbors[(dirIndex + orientIndex) % 4];
	}
}
