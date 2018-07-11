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


	public Robot(IRobotActors actor, int id, RobotState state) {
		this.actor = actor;
		this.id = id;
		this.state = state;
	}

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
			Position delta = getDeltaPosition(data.pos(), target);

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
			Position delta = getDeltaPosition(data.pos(), target);
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation(data.pos(), target));

			// U-Turn
			if (delta.getX() + delta.getY() == -1 && delta.getX() * delta.getY() == 0 || dir == Direction.BEHIND) {
				if (!data.blockedLeft()) {
					actor.turnLeft();
					actor.driveForward();
					actor.turnLeft();
				} else if (!data.blockedFront()) {
					actor.driveForward();
					state = RobotState.CROSS_RIGHT_UP_LEFT;
				}
			} else { // Normal behaviour
				if (!data.blockedFront() && /*!data.blockedCrossroadFront() &&*/ isCrossroadOpen()) {
					actor.driveForward();
					state = RobotState.CROSS_RIGHT_UP_LEFT;
				}
			}
 		} else if (state == RobotState.CROSS_RIGHT_UP_LEFT) {
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation(data.pos(), target));

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
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation(data.pos(), target));
			if (dir == Direction.AHEAD) {
				if (!data.blockedFront()) {
					actor.driveForward();
					state = RobotState.WAYPOINT;
				}
			} else {
				if (!data.blockedLeft()) {
					actor.turnLeft();
					actor.driveForward();
				}
			}
		}
	}

	private boolean isCrossroadOpen() {
		boolean left = data.blockedWaypointLeft();
		boolean ahead = data.blockedWaypointFront();
		boolean right = data.blockedWaypointRight();

		Direction next = getNextDirection();

		if (data.posOrientation() == Orientation.EAST && next != Direction.RIGHT && data.blockedCrossroadFront() && right)
			return false;

		return next != Direction.LEFT || !data.blockedWaypointFront() &&
				((!left && !ahead && !right) || data.posOrientation() == Orientation.NORTH) ;
	}

	private Direction getNextDirection() {
		Position next = Position.add(data.pos(), getNextDeltaInOrientation(data.posOrientation()));
		return Orientation.getRelativeDirection(data.posOrientation(), targetOrientation(next, target));
	}

	private Position getNextDeltaInOrientation(Orientation orient) {
		switch (orient) {
			case NORTH:
				return new Position(0, 1);
			case WEST:
				return new Position(-1, 0);
			case EAST:
				return new Position(1, 0);
			case SOUTH:
				return new Position(0, -1);
			default:
				throw new IllegalStateException();
		}
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
		Position delta = getDeltaPosition(data.pos(), target);

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

	public Orientation targetOrientation(Position current, Position target) {
		Position delta = getDeltaPosition(current, target);

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

	private Position getDeltaPosition(Position current, Position target) {
		return new Position(target.getX() - current.getX(), target.getY() - current.getY());
	}

	public enum RobotState {
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
