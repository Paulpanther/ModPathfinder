package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Grid {

	private RobotHandler handler;

	private final int size_x = 100, size_y = 100;


	private Grid() {
		handler = new RobotHandler(this);
	}

	public boolean[] areNeighborsBlocked(Position pos) {
		boolean[] neighbors = new boolean[4];
		if (pos.getY() <= 6) {  // In Station

		} else {
			if (pos.getX() == 1)
				neighbors[0] = true;
			if (pos.getX() == size_x - 1)
				neighbors[2] = true;
			if (pos.getY() == size_y - 1)
				neighbors[1] = true;
		}

		return neighbors;
	}

	public static void main(String[] args) {
		new Grid();
	}
}
