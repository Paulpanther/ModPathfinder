package de.mod10.smp.tests;

import de.mod10.smp.Grid;
import de.mod10.smp.helper.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Paul
 * @since 05.07.2018
 */
class GridTest {

	@Test
	void areNeighborsBlocked() {
		Grid grid = new Grid();
		// Test in Station
		assertArrayEquals(new boolean[]{true, false, false, true}, grid.areNeighborsBlocked(new Position(1, 0)), "In Station");
		// Test in Battery
		assertArrayEquals(new boolean[]{true, true, false, true}, grid.areNeighborsBlocked(new Position(3, 3)), "In Battery");
		// Test above drop
		assertArrayEquals(new boolean[]{false, false, false, true}, grid.areNeighborsBlocked(new Position(6, 9)), "Above Drop");
		// Test at top left edge
		assertArrayEquals(new boolean[]{true, true, false, false}, grid.areNeighborsBlocked(new Position(1, 99)), "Top Left Edge");
		// Test left of drop
		assertArrayEquals(new boolean[]{false, false, true, false}, grid.areNeighborsBlocked(new Position(2, 8)), "Left of Drop");
		// Test right of drop
		assertArrayEquals(new boolean[]{true, false, false, false}, grid.areNeighborsBlocked(new Position(4, 8)), "Right of Drop");
		// Test beneath drop
		assertArrayEquals(new boolean[]{false, true, false, false}, grid.areNeighborsBlocked(new Position(3, 7)), "Beneath Drop");

	}
}