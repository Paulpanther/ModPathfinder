package de.mod10.smp;

import de.mod10.smp.helper.*;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Robot implements ISensorInfo, IRobotActorInfo {

	private SensorData data;
	private RobotState state = RobotState.WAYPOINT;
	private Position target = null;
	private IRobotActors actor;


	public Robot(IRobotActors actor) {
		this.actor = actor;
	}

	public void driveTo(Position target) {
		this.target = target;

		// TODO Uncomment this for Tests

//		if (data != null) {
//			while (!target.equals(data.pos())) {
//				step();
//			}
//		}
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
			if (delta.getX() + delta.getY() == -1 && delta.getX() * delta.getY() == 0 || dir == Direction.BEHIND) {
				actor.turnLeft();
				if (!data.blockedFront())
					actor.driveForward();
				actor.turnLeft();
			} else {
				if (!data.blockedFront()) {
					actor.driveForward();
					state = RobotState.CROSS_RIGHT_UP_LEFT;
				}
			}
		} else if (state == RobotState.CROSS_RIGHT_UP_LEFT) {
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), targetOrientation());
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

	private boolean inStationState() {
		return state == RobotState.STATION || state == RobotState.TO_BATTERY || state == RobotState.FROM_BATTERY || state == RobotState.ON_BATTERY;
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
		WAYPOINT, CROSS_RIGHT_UP_LEFT, CROSS_LEFT_UP, STATION, TO_BATTERY, ON_BATTERY, FROM_BATTERY
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
}
