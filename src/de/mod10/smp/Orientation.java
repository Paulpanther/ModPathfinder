package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public enum Orientation {
	NORTH(1), EAST(2), SOUTH(3), WEST(0);

	private final int orientation;


	Orientation(int orientation) {
		this.orientation = orientation;
	}

	public int getValue() {
		return orientation;
	}

	public static Orientation rotateLeft(Orientation orientation) {
		return turn(orientation, EAST, NORTH, WEST, SOUTH);
	}

	public static Orientation rotateRight(Orientation orientation) {
		return turn(orientation, WEST, SOUTH, EAST, NORTH);
	}

	private static Orientation turn(Orientation orientation, Orientation west, Orientation south, Orientation east, Orientation north) {
		switch (orientation) {
			case NORTH:
				return west;
			case WEST:
				return south;
			case SOUTH:
				return east;
			case EAST:
				return north;
			default:
				throw new IllegalArgumentException();
		}
	}
}