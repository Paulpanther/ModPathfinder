//package de.mod10.smp;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RobotTest {
//
//	private Robot robot;
//	private Position pos;
//	private Orientation posOrientation;
//
//	@BeforeEach
//	void setUp() {
//		robot = new Robot(null);
//		robot.sensorEvent(new SensorHandler());
//	}
//
//	@Test
//	void targetDirectionTest() {
//		pos = new Position(8, 11);
//		posOrientation = Orientation.NORTH;
//		assertEquals(Direction.AHEAD, robot.targetDirection(new Position(6, 13)));
//
//		pos = new Position(8, 12);
//		posOrientation = Orientation.NORTH;
//		assertEquals(Direction.AHEAD, robot.targetDirection(new Position(6, 13)));
//
//		pos = new Position(8, 13);
//		posOrientation = Orientation.NORTH;
//		assertEquals(Direction.LEFT, robot.targetDirection(new Position(6, 13)));
//
//		pos = new Position(9, 15);
//		posOrientation = Orientation.WEST;
//		assertEquals(Direction.AHEAD, robot.targetDirection(new Position(6, 13)));
//	}
//
//	private class SensorHandler implements SensorData {
//
//		@Override
//		public Position pos() {
//			return pos;
//		}
//
//		@Override
//		public PositionType posType() {
//			return null;
//		}
//
//		@Override
//		public Orientation posOrientation() {
//			return posOrientation;
//		}
//
//		@Override
//		public boolean blockedFront() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedLeft() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedRight() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedWaypointFront() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedWaypointLeft() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedWaypointRight() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedCrossroadFront() {
//			return false;
//		}
//
//		@Override
//		public boolean blockedCrossroadRight() {
//			return false;
//		}
//	}
//}