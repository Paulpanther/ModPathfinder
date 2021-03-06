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

	private Position pos, from;
	private Orientation orientation = Orientation.EAST;
	private Position target, station;
	private boolean driving = false;


	public RobotHandler(Grid grid, Position initPos, int id, Orientation orientation, Robot.RobotState state) {
		this.orientation = orientation;
		this.grid = grid;
		this.pos = initPos;

		robot = new Robot(this, id, state);
		robot.sensorEvent(this);
	}

	public RobotHandler(Grid grid, Position initPos, int id) {
		this.grid = grid;
		this.pos = initPos;

		robot = new Robot(this, id);
		robot.sensorEvent(this);
	}

	public Position getTarget() {
		return target;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void driveTo(Position pos) {
		target = pos;
		from = this.pos.copy();
		robot.driveTo(pos);
		driving = true;
	}

	public boolean isDriving() {
		return driving;
	}

	public void step() {
		if (pos.equals(station)) {
			driveTo(target);
			station = null;
		}
		if (pos.equals(target)) {
			driving = false;
		}
		robot.step();
	}

	public void driveInStation(Position station, Position next) {
		driving = true;
		this.station = station;
		driveTo(station);
		this.target = next;
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

	public String print() {
		StringBuilder out = new StringBuilder();
		out.append("Robot:\n");
		out.append("\tPos: ").append(pos).append("\n");
		out.append("\tTarget: ").append(target).append("\n");
		out.append(robot.print());
		out.append("\tOrientation: ").append(orientation).append("\n");
		out.append("\tOn Pos Type: ").append(posType()).append("\n");
		if (posType() == PositionType.WAYPOINT)
			out.append("\tWaypoints: ").append(blockedWaypointLeft()).append(" ")
					.append(blockedWaypointFront()).append(" ").append(blockedWaypointRight()).append("\n");
		out.append("\n");
		return String.valueOf(out);
	}
}
