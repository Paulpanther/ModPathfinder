package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Robot implements ISensorInfo, IRobotActorInfo {

	private SensorData data = null;
	private RobotState state = RobotState.IDLE;
	private Position target = null;


	public void driveTo(Position target) {
		this.target = target;
		state = RobotState.DEFAULT;

		if (data != null) {
			while (!target.equals(data.pos())) {
				step();
			}
		}
	}

	public void step() {

	}

	public Direction targetDirection() {
		Position current = data.pos();
		int delta_x = target.getX() - current.getX();
		int delta_y = target.getY() - current.getY();
		Position delta = new Position(delta_x, delta_y);

		if (isTargetBeneath(delta)) {
			return Orientation.getRelativeDirection(data.posOrientation(), Orientation.SOUTH);
		}
		Orientation orient = targetOrientation(delta);
		return Orientation.getRelativeDirection(data.posOrientation(), orient);
	}

	private boolean isTargetBeneath(Position delta) {
		return delta.getX() == 0 && delta.getY() == -1;
	}

	public Orientation targetOrientation(Position delta) {

		if (state == RobotState.DEFAULT) {
			if (delta.getY() > 1)
				return Orientation.NORTH;
			else if (delta.getX() < 0)
				return Orientation.WEST;
			else if (delta.getX() > 0 )
				return Orientation.EAST;
			else if (delta.getY() < -1)
				return Orientation.SOUTH;
		}

		throw new IllegalStateException();
	}

	@Override
	public void unloaded() {

	}

	@Override
	public void sensorEvent(SensorData data) {
		this.data = data;
	}


	private enum RobotState {
		IDLE, DEFAULT
	}
}
