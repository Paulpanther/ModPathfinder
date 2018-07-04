package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class RobotHandler implements IRobotActors, SensorData {

	private Robot robot;
	private SensorData data;
	private Grid grid;

	private Position pos;
	private Orientation orientation;


	public RobotHandler(Grid grid) {
		this.grid = grid;

		robot = new Robot();
		robot.sensorEvent(this);
	}

	@Override
	public void driveForward() {

	}

	@Override
	public void turnLeft() {

	}

	@Override
	public void turnRight() {

	}

	@Override
	public void startUnload() {

	}

	@Override
	public Position pos() {
		return pos;
	}

	@Override
	public PositionType posType() {
		// TODO
		return null;
	}

	@Override
	public Orientation posOrientation() {
		return orientation;
	}

	@Override
	public boolean blockedFront() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedLeft() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedRight() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedWaypointFront() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedWaypointLeft() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedWaypointRight() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedCrossroadFront() {
		// TODO
		return false;
	}

	@Override
	public boolean blockedCrossroadRight() {
		// TODO
		return false;
	}
}
