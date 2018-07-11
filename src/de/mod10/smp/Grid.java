package de.mod10.smp;

import de.mod10.smp.helper.Direction;
import de.mod10.smp.helper.Orientation;
import de.mod10.smp.helper.Position;
import de.mod10.smp.helper.PositionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Grid {

	private List<RobotHandler> handler;

	public static final int SIZE_X = 30, SIZE_Y = 30;


	public Grid() {
		handler = new ArrayList<>();
	}

	public RobotHandler registerRobotHandler(Position initPos, int id) {
		RobotHandler robot = new RobotHandler(this, initPos, id);
		handler.add(robot);
		return robot;
	}

	public RobotHandler addDebugRobot(Position pos, Position target, Orientation orient, Robot.RobotState state, int id) {
		RobotHandler robot = new RobotHandler(this, pos, id, orient, state);
		robot.driveTo(target);
		handler.add(robot);
		return robot;
	}

	public boolean blockedWaypoint(Position pos, Orientation orient, Direction dir) {
		if (posType(pos) == PositionType.WAYPOINT) {
			Position cross = nextCrossroad(pos, orient);
			Orientation to = Orientation.getRotatedOrientation(orient, dir);
			Position waypoint = crossroadWaypointIn(cross, to);
			return isRobot(waypoint) != null;

		} else if (posType(pos) == PositionType.CROSSROADS) {
			Position cross = onCrossroad(pos);
			Orientation to = Orientation.getRotatedOrientation(orient, dir);
			Position waypoint = crossroadWaypointOut(cross, to);
			return isRobot(waypoint) != null;
		}
		return false;
	}

	public boolean blockedCrossroadFront(Position pos, Orientation orient) {
		Position cross = nextCrossroad(pos, orient);

		for (Position crossPosDelta : crossroadDeltaPositions()) {
			Position crossPos = Position.add(crossPosDelta, cross);
			if (!crossPos.equals(pos) && isRobot(crossPos) != null)
				return true;
		}
		return false;
	}

	private Position[] crossroadDeltaPositions() {
		return new Position[] {
				new Position(-1, 0),
				new Position(-1, 1),
				new Position(0, 1),
				new Position(0, 0)
		};
	}

	private Position crossroadWaypointIn(Position cross, Orientation orient) {
		Position[] delta = {
				new Position(-2, 0),
				new Position(-1, 2),
				new Position(1, 1),
				new Position(0, -1)
		};
		return Position.add(cross, delta[orient.getValue()]);
	}

	private Position crossroadWaypointOut(Position cross, Orientation orient) {
		Position[] delta = {
				new Position(-2, 1),
				new Position(0, 2),
				new Position(1, 0),
				new Position(-1, -1)
		};
		return Position.add(cross, delta[orient.getValue()]);
	}

	private Position onCrossroad(Position pos) {
		return new Position(pos.getX() / 3 * 3 + 2, pos.getY() / 3 * 3);
	}

	private Position nextCrossroad(Position pos, Orientation orient) {
		Position next = nextPosition(pos, orient);
		return new Position(next.getX() / 3 * 3 + 2, next.getY() / 3 * 3);
	}

	public Position nextPosition(Position pos, Orientation orient) {
		Position delta;
		switch (orient) {
			case NORTH:
				delta = new Position(0, 1);
				break;
			case WEST:
				delta = new Position(-1, 0);
				break;
			case EAST:
				delta = new Position(1, 0);
				break;
			case SOUTH:
				delta = new Position(0, -1);
				break;
			default:
				throw new IllegalStateException();
		}
		return Position.add(delta, pos);
	}

	public boolean[] areNeighborsBlocked(Position pos) {
		boolean[] neighbors = new boolean[]{
				false, false, false, false
		};

		// Common Cases
		if (pos.getX() % 3 == 1 && pos.getY() % 3 == 2)
			neighbors[0] = true;
		if (pos.getX() % 3 == 0 && pos.getY() % 3 == 1)
			neighbors[1] = true;
		if (pos.getX() % 3 == 2 && pos.getY() % 3 == 2)
			neighbors[2] = true;
		if (pos.getX() % 3 == 0 && pos.getY() % 3 == 0)
			neighbors[3] = true;

		// Override Values in special cases
		if (pos.getY() < 6) {  // In Station
			if (pos.getY() == 5 || pos.getY() == 4) {
				neighbors[0] = true;
				neighbors[2] = true;
			} else if (pos.getY() < 4 && pos.getY() > 0) {
				if (pos.getX() % 3 == 1 || pos.getX() % 3 == 2)
					neighbors[2] = true;
				if (pos.getX() % 3 == 1)
					neighbors[0] = false;
				if (pos.getX() % 3 == 2)
					neighbors[0] = true;
				if (pos.getX() % 3 == 0) {
					neighbors[0] = true;
					neighbors[1] = true;
					neighbors[2] = false;
					neighbors[3] = true;
				}
			} else if (pos.getY() == 0) {
				neighbors[3] = true;
				if (pos.getX() % 3 == 1)
					neighbors[0] = true;
				if (pos.getX() % 3 == 2)
					neighbors[2] = true;
			}
		}
		if (pos.getX() == 1)
			neighbors[0] = true;
		if (pos.getX() == SIZE_X - 1)
			neighbors[2] = true;
		if (pos.getY() == SIZE_Y - 1)
			neighbors[1] = true;

		return neighbors;
	}

	public PositionType posType(Position pos) {
		if (pos.getX() == 0 || (pos.getY() == 5 || pos.getY() == 4 || pos.getY() == 0) && pos.getX() % 3 == 0 ||
				(pos.getY() > 6 && pos.getY() % 3 == 2 && pos.getX() % 3 == 0))
			return PositionType.BLOCK;
		if (pos.getY() < 6 && pos.getX() % 3 != 2 || pos.getY() < 5)
			return PositionType.STATION;
		if (pos.getX() % 3 == 0 || pos.getY() % 3 == 2)
			return PositionType.WAYPOINT;
		if (pos.getY() == 5 && pos.getX() % 3 == 2)
			return PositionType.WAYPOINT;
		return PositionType.CROSSROADS;
	}

	public boolean isBattery(Position pos) {
		return pos.getY() < 4 && pos.getY() > 0 && pos.getX() % 3 == 0;
	}

	public boolean isDrop(Position pos) {
		return pos.getY() > 6 && pos.getY() % 3 == 2 && pos.getX() % 3 == 0 && pos.getX() != 0;
	}

	public boolean isFillBlock(Position pos) {
		return pos.getY() == 5 && pos.getX() % 3 == 0 && pos.getX() != 0;
	}

	public boolean isFillPosition(Position pos) {
		return pos.getY() == 5 && pos.getX() % 3 == 2;
	}

	public boolean isStationStart(Position pos) {
		return pos.getY() == 5 && pos.getX() % 3 == 1;
	}

	public RobotHandler isRobot(Position pos) {
		for (RobotHandler robot : handler) {
			if (robot.pos().equals(pos))
				return robot;
		}
		return null;
	}

	public int getBatteryCount() {
		return (int) Math.ceil(SIZE_X / 3f) * 3;
	}

	public RobotHandler spawnRobotOnBattery(int bat, int id) {
		Position batPos = getBatteryPosition(bat);
		return registerRobotHandler(batPos, id);
	}

	public void moveRobotToBattery(int bat, RobotHandler robot) {
		Position station = getStationOfBattery(bat);
		robot.driveInStation(station, getBatteryPosition(bat));
	}

	public void moveRobotToFill(int fill, RobotHandler robot) {
		Position station = getStationOfFill(fill);
		robot.driveInStation(station, getFillPosition(fill));
	}

	public void moveRobotToDrop(int drop, RobotHandler robot) {
		robot.driveTo(getDropPosition(drop));
	}

	public int getDropCount() {
		return dropsPerColumn() * dropsPerRow();
	}

	public Position getDropPosition(int drop) {
		return new Position(drop / dropsPerColumn() * 3 + 3, (drop % dropsPerColumn()) * 3 + 9);
	}

	public Position getDropBlock(int drop) {
		return new Position(drop / dropsPerColumn() * 3 + 3, (drop % dropsPerColumn()) * 3 + 8);
	}

	private int dropsPerColumn() {
		return (int) (Math.ceil(Math.max(SIZE_Y - 9, 0) / 3f));
	}

	private int dropsPerRow() {
		return (int) (Math.ceil(SIZE_X / 3f) - 1);
	}

	public int getFillCount() {
		return (int) Math.ceil(SIZE_X / 3f) - 1;
	}

	public Position getFillPosition(int fill) {
		return new Position(fill * 3 + 2, 5);
	}

	public int getFillByPosition(Position pos) {
		return pos.getX() / 3;
	}

	private Position getStationOfFill(int fill) {
		return new Position(fill * 3 + 1, 5);
	}

	private Position getBatteryPosition(int bat) {
		return new Position(bat / 3 * 3, (bat % 3) + 1);
	}

	private Position getStationOfBattery(int bat) {
		Position batPos = getBatteryPosition(bat);
		return new Position(batPos.getX() + 1, 5);
	}

	public boolean isValidMove(Position pos) {
		return isStationStart(pos) || isBattery(pos) || posType(pos) == PositionType.WAYPOINT;
	}

	public List<RobotHandler> getRobots() {
		return handler;
	}
}
