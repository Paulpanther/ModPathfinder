package de.mod10.smp.helper;

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

	@SuppressWarnings("Duplicates")
	public static Orientation rotateLeft(Orientation orientation) {
		switch (orientation) {
			case NORTH:
				return WEST;
			case WEST:
				return SOUTH;
			case SOUTH:
				return EAST;
			case EAST:
				return NORTH;
			default:
				throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("Duplicates")
	public static Orientation rotateRight(Orientation orientation) {
		switch (orientation) {
			case NORTH:
				return EAST;
			case WEST:
				return NORTH;
			case SOUTH:
				return WEST;
			case EAST:
				return SOUTH;
			default:
				throw new IllegalArgumentException();
		}
	}

	public static Direction getRelativeDirection(Orientation heading, Orientation target) {
		int diff = heading.getValue() - target.getValue();
		while (diff < 0)
			diff += 4;
		switch (diff) {
			case 0:
				return Direction.AHEAD;
			case 1:
				return Direction.LEFT;
			case 2:
				return Direction.BEHIND;
			case 3:
				return Direction.RIGHT;
			default:
				throw new IllegalStateException();
		}
	}
}
