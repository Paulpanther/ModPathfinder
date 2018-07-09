package de.mod10.smp;

import de.mod10.smp.helper.*;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Robot implements ISensorInfo, IRobotActorInfo {

	private SensorData data;
	private RobotState state = RobotState.FROM_BATTERY;
	private Position target = null;
	private IRobotActors actor;
	private int id;


	public Robot(IRobotActors actor, int id) {
		this.actor = actor;
		this.id = id;
	}

	public void driveTo(Position target) {
		this.target = target;
	}

	public void step() {
		if (target == null || target.equals(data.pos()))
			return;

		// Set States
   		PositionType type = data.posType();
		if (type == PositionType.WAYPOINT)
			state  = RobotState.WAYPOINT;
		if (type == PositionType.STATION && state == RobotState.WAYPOINT) {
			Position delta = getDeltaPosition();

			while (data.posOrientation() != Orientation.SOUTH)
				actor.turnLeft();

			if (delta.getX() < 0) {
				state = RobotState.TO_BATTERY;
			} else {
				state = RobotState.STATION;
			}
		}

		if (inStationState()) {
			stationControl();
		} else if (state == RobotState.WAYPOINT) {
			Position delta = getDeltaPosition();
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation());

			// U-Turn
			if (delta.getX() + delta.getY() == -1 && delta.getX() * delta.getY() == 0 || dir == Direction.BEHIND) {
				if (!data.blockedLeft()) {
					actor.turnLeft();
					actor.driveForward();
					actor.turnLeft();
				}


			} else { // Normal behaviour
				if (!data.blockedFront() && /*!data.blockedCrossroadFront() &&*/ isCrossroadOpen()) {
					actor.driveForward();
					state = RobotState.CROSS_RIGHT_UP_LEFT;
				}
			}
 		} else if (state == RobotState.CROSS_RIGHT_UP_LEFT) {
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation());

			if (!blockedWaypoint(dir)) {
				if (dir == Direction.RIGHT) {
					actor.turnRight();
					if (!data.blockedFront()) {
						actor.driveForward();
						state = RobotState.WAYPOINT;
					}
				} else {
					if (!data.blockedFront()) {
						actor.driveForward();
						state = RobotState.CROSS_LEFT_UP;
					}
				}
			}
		} else if (state == RobotState.CROSS_LEFT_UP) {
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation());
			if (dir == Direction.AHEAD) {
				if (!data.blockedFront()) {
					actor.driveForward();
					state = RobotState.WAYPOINT;
				}
			} else {
				actor.turnLeft();
				if (!data.blockedFront()) {
					actor.driveForward();
				}
			}
		}
	}

	private boolean isCrossroadOpen() {
		boolean left = data.blockedWaypointLeft();
		boolean ahead = data.blockedWaypointFront();
		boolean right = data.blockedWaypointRight();

		if (!left && !ahead && !right) {
			return true;
		} else if (left && ahead && right) {
			Orientation orient = data.posOrientation();
			return orient == Orientation.NORTH;
		} else return !right;
	}

	private boolean blockedWaypoint(Direction dir) {
		switch (dir) {
			case AHEAD:
				return data.blockedWaypointFront();
			case LEFT:
				return data.blockedWaypointLeft();
			case RIGHT:
				return data.blockedWaypointRight();
			default:
				return false;
		}
	}

	private boolean inStationState() {
		return state == RobotState.STATION || state == RobotState.TO_BATTERY || state == RobotState.FROM_BATTERY;
	}

	private void stationControl() {
		Position delta = getDeltaPosition();

		if (state == RobotState.TO_BATTERY) {
			if (delta.getX() == -1 && delta.getY() == 0 && data.posOrientation() != Orientation.WEST) {
				actor.turnRight();
			} else if (!data.blockedFront()) {
				actor.driveForward();
				if (target.equals(data.pos())) {
					actor.turnRight();
					actor.turnRight();
					state = RobotState.FROM_BATTERY;
				}
			}
		} else if (state == RobotState.FROM_BATTERY) {
			if (!data.blockedFront()) {
				actor.driveForward();
			} else if (!data.blockedRight()) {
				actor.turnRight();
				state = RobotState.STATION;
			}
		} else {
			if (!data.blockedFront()) {
				actor.driveForward();
			} else if (!data.blockedLeft()) {
				actor.turnLeft();
			}
		}
	}

	public Orientation targetOrientation() {
		Position delta = getDeltaPosition();

		if (delta.getY() > 0)
			return Orientation.NORTH;
		else if (delta.getX() < 0)
			return Orientation.WEST;
		else if (delta.getX() > 0 )
			return Orientation.EAST;
		else if (delta.getY() < 0)
			return Orientation.SOUTH;
		else
			throw new IllegalStateException();
	}

	private Position getDeltaPosition() {
		Position current = data.pos();
		return new Position(target.getX() - current.getX(), target.getY() - current.getY());
	}

	private enum RobotState {
		WAYPOINT, CROSS_RIGHT_UP_LEFT, CROSS_LEFT_UP, STATION, TO_BATTERY, FROM_BATTERY
	}

	@Override
	public void unloaded() {

	}

	public void unload() {
		actor.startUnload();
	}

	public void stop() {
		target = null;
	}

	@Override
	public void sensorEvent(SensorData data) {
		this.data = data;
	}

	public String print() {
		StringBuilder out = new StringBuilder();
		out.append("\tID: ").append(id).append("\n");
		out.append("\tState: ").append(state).append("\n");
		if (state == RobotState.WAYPOINT)
			out.append("\tWaypoints Open: ").append(isCrossroadOpen()).append("\n");
		return String.valueOf(out);
	}
}
