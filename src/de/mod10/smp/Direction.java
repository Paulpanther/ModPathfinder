package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public enum Direction {
	AHEAD(0), BEHIND(2), RIGHT(1), LEFT(3);

	private final int dir;
	Direction(int dir) { this.dir = dir; }
	public int getValue() { return dir; }
}
