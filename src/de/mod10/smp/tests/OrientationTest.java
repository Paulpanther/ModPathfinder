package de.mod10.smp.tests;

import de.mod10.smp.helper.Direction;
import de.mod10.smp.helper.Orientation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrientationTest {

	@Test
	void getRelativeDirection() {
		Assertions.assertEquals(Direction.AHEAD, Orientation.getRelativeDirection(Orientation.NORTH, Orientation.NORTH));
		assertEquals(Direction.LEFT, Orientation.getRelativeDirection(Orientation.NORTH, Orientation.WEST));
		assertEquals(Direction.BEHIND, Orientation.getRelativeDirection(Orientation.NORTH, Orientation.SOUTH));
		assertEquals(Direction.RIGHT, Orientation.getRelativeDirection(Orientation.NORTH, Orientation.EAST));
		assertEquals(Direction.RIGHT, Orientation.getRelativeDirection(Orientation.WEST, Orientation.NORTH));
		assertEquals(Direction.AHEAD, Orientation.getRelativeDirection(Orientation.WEST, Orientation.WEST));
		assertEquals(Direction.LEFT, Orientation.getRelativeDirection(Orientation.WEST, Orientation.SOUTH));
		assertEquals(Direction.BEHIND, Orientation.getRelativeDirection(Orientation.WEST, Orientation.EAST));
		assertEquals(Direction.LEFT, Orientation.getRelativeDirection(Orientation.EAST, Orientation.NORTH));
		assertEquals(Direction.LEFT, Orientation.getRelativeDirection(Orientation.SOUTH, Orientation.EAST));
	}
}