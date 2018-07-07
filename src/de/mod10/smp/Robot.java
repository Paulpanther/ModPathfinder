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
				if (!data.blockedLeft())
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

	/*public void step() {
		if (target == null || target.equals(data.pos()))
			return;

		boolean moved = false;

		PositionType type = data.posType();
		if (type == PositionType.CROSSROADS && state == RobotState.DEFAULT)
			state = RobotState.FIRST_STEP_ON_CROSS;
		else if (type == PositionType.WAYPOINT && state == RobotState.ON_CROSS)
			state  = RobotState.DEFAULT;
		if (type == PositionType.STATION && state == RobotState.DEFAULT) {
			Position delta = getDeltaPosition();

			while (data.posOrientation() != Orientation.SOUTH)
				actor.turnLeft();

			if (delta.getX() < 0) {
				state = RobotState.TO_BATTERY;
			} else {
				state = RobotState.STATION;
			}
		}

		if (state != RobotState.TO_BATTERY && state != RobotState.STATION && state != RobotState.FROM_BATTERY) {

			if (isTargetBeneath(getDeltaPosition())) {
				Direction dir = Orientation.getRelativeDirection(data.posOrientation(), Orientation.SOUTH);
				moved = move(dir);
				if (target.equals(data.pos())) {
					move(Direction.LEFT);
				}
			} else {

				Position subTarget = getSubTarget();

				Direction dir = targetDirection(subTarget);
				moved = move(dir);
			}
		} else
			stationControll();

		if (state == RobotState.FIRST_STEP_ON_CROSS && moved)
			state = RobotState.ON_CROSS;
	}

	private boolean move(Direction dir) {
		boolean moved = false;
		switch (dir) {
			case AHEAD:
				if (!data.blockedFront()) {
					actor.driveForward();
					moved = true;
				}
				break;
			case LEFT:
				actor.turnLeft();
				break;
			case BEHIND:
				actor.turnRight();
			case RIGHT:
				actor.turnRight();
				break;
		}
		return moved;
	}

	public void stationControll() {
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

	public Position getSubTarget() {
		Position current = data.pos();
		Position delta = getDeltaPosition();

		if ((state == RobotState.DEFAULT || state == RobotState.ON_CROSS) && data.posType() == PositionType.WAYPOINT) {
			Orientation orient = targetOrientation(delta);
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), orient);
			return Position.add(calculateNextDeltaWayPoints(data.posOrientation())[dir.getValue()], data.pos());
		} else if (state == RobotState.FIRST_STEP_ON_CROSS) {
			Orientation toTarget = targetOrientation(delta);
			Direction dir = Orientation.getRelativeDirection(data.posOrientation(), toTarget);
			switch (dir) {
				case AHEAD:
				case RIGHT:
					return Position.add(getNextDeltaInOrientation(toTarget), current);
				default:
					return Position.add(getNextDeltaInOrientation(toTarget), current);
			}
		} else if ((state == RobotState.DEFAULT || state == RobotState.ON_CROSS) && data.posType() == PositionType.CROSSROADS) {
			return Position.add(getNextDeltaInOrientation(targetOrientation(delta)), current);
		}

		throw new IllegalStateException();
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

	private Position[] calculateNextDeltaWayPoints(Orientation orient) {
		switch (orient) {
			case NORTH:
				return new Position[]{
						new Position(0, 3),
						new Position(1, 1),
						new Position(-1, 0),
						new Position(-2, 2)
				};
			case WEST:
				return new Position[]{
						new Position(-3, 0),
						new Position(-1, 1),
						new Position(0, -1),
						new Position(-2, -2),
				};
			case SOUTH:
				return new Position[]{
						new Position(0, -3),
						new Position(-1, -1),
						new Position(1, 0),
						new Position(2, -2)
				};
			case EAST:
				return new Position[]{
						new Position(3, 0),
						new Position(1, -1),
						new Position(0, 1),
						new Position(2, 2)
				};
			default:
				throw new IllegalStateException();
		}
	}

	public Direction targetDirection(Position target) {
		Position current = data.pos();
		int delta_x = target.getX() - current.getX();
		int delta_y = target.getY() - current.getY();
		Position delta = new Position(delta_x, delta_y);

		switch (state) {
			case DEFAULT:
				if (Math.abs(delta.getX() + delta.getY()) != 1)
					return Direction.AHEAD;
				// continue
			case ON_CROSS:
				Orientation orient = targetOrientation(delta);
				return Orientation.getRelativeDirection(data.posOrientation(), orient);
			case FIRST_STEP_ON_CROSS:
				orient = targetOrientation(delta);
				Direction dir = Orientation.getRelativeDirection(data.posOrientation(), orient);

				switch (dir) {
					case AHEAD:
					case RIGHT:
						return dir;
					default:
						return Direction.AHEAD;
				}
			default:
				throw new IllegalStateException();
		}
	}

	private boolean isTargetBeneath(Position delta) {
		return delta.getX() == 0 && delta.getY() == -1;
	}

	public Orientation targetOrientation(Position delta) {

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
		IDLE, DEFAULT, FIRST_STEP_ON_CROSS, ON_CROSS, STATION, TO_BATTERY, FROM_BATTERY
	}*/

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

	@Override
	public void sensorEvent(SensorData data) {
		this.data = data;
	}
}
