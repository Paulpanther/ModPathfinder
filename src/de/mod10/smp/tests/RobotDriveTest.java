package de.mod10.smp.tests;

import de.mod10.smp.Grid;
import de.mod10.smp.helper.Position;
import de.mod10.smp.RobotHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Paul
 * @since 05.07.2018
 */
public class RobotDriveTest {

	private Grid grid;

	@BeforeEach
	void setUp() {
		grid = new Grid();
	}

	@Test
	void testDriveFromStationStartToStationEnd() {
		RobotHandler handler = grid.registerRobotHandler(new Position(1, 5));
		Position end = new Position(2, 5);
		handler.driveTo(end);
		assertEquals(end, handler.pos());
	}

	@Test
	void testDriveFromStationStartToBatterie() {
		RobotHandler handler = grid.registerRobotHandler(new Position(4, 5));
		Position end = new Position(3, 2);
		handler.driveTo(end);
		assertEquals(end, handler.pos());
	}

	@Test
	void testDriveFromBatterieToStationEnd() {
		RobotHandler handler = grid.registerRobotHandler(new Position(4, 5));
		handler.driveTo(new Position(3, 3));
		Position end = new Position(5, 5);
		handler.driveTo(end);
		assertEquals(end, handler.pos());
	}

	@Test
	void testDriveFromStationStartToDropNorthThanEast() {
		// RobotHandler handler = grid.registerRobotHandler(new Position(5, 5));
		RobotHandler handler = grid.registerRobotHandler(new Position(5, 11));
		Position end = new Position(9, 12);
		handler.driveTo(end);
		assertEquals(end, handler.pos());
	}

	@Test
	void testDriveFromStationStartToDropNorthThanWest() {
		RobotHandler handler = grid.registerRobotHandler(new Position(8, 5));
		Position end = new Position(3, 12);
		handler.driveTo(end);
		assertEquals(end, handler.pos());
	}
}
