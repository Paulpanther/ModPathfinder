package de.mod10.smp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

	private Robot robot;
	private Position pos;
	private Orientation posOrientation;

	@BeforeEach
	void setUp() {
		robot = new Robot();
		robot.sensorEvent(new SensorHandler());
	}

	@Test
	void targetDirection() {
		pos = new Position(8, 12);
		posOrientation = Orientation.NORTH;
		robot.driveTo(new Position(6, 13));
		assertEquals(Direction.L robot.targetDirection();
	}

	@Test
	void targetOrientation() {
	}

	private class SensorHandler implements SensorData {

		@Override
		public Position pos() {
			return pos;
		}

		@Override
		public PositionType posType() {
			return null;
		}

		@Override
		public Orientation posOrientation() {
			return posOrientation;
		}

		@Override
		public boolean blockedFront() {
			return false;
		}

		@Override
		public boolean blockedLeft() {
			return false;
		}

		@Override
		public boolean blockedRight() {
			return false;
		}

		@Override
		public boolean blockedWaypointFront() {
			return false;
		}

		@Override
		public boolean blockedWaypointLeft() {
			return false;
		}

		@Override
		public boolean blockedWaypointRight() {
			return false;
		}

		@Override
		public boolean blockedCrossroadFront() {
			return false;
		}

		@Override
		public boolean blockedCrossroadRight() {
			return false;
		}
	}
}