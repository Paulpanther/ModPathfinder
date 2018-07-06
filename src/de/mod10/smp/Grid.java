package de.mod10.smp;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Grid {

	private List<RobotHandler> handler;

	public static final int SIZE_X = 30, SIZE_Y = 30;


	public Grid() {
		handler = new ArrayList<>();
	}

	public RobotHandler registerRobotHandler(Position initPos) {
		RobotHandler robot = new RobotHandler(this, initPos);
		handler.add(robot);
		return robot;
	}

	public boolean[] areNeighborsBlocked(Position pos) {
		boolean[] neighbors = new boolean[]{
				false, false, false, false
		};

		// Common Cases
		if (pos.getX() % 3 == 1 && pos.getY() % 3 == 2)
			neighbors[0] = true;
		if (pos.getX() % 3 == 0 && pos.getY() % 3 == 1)
			neighbors[1] = true;
		if (pos.getX() % 3 == 2 && pos.getY() % 3 == 2)
			neighbors[2] = true;
		if (pos.getX() % 3 == 0 && pos.getY() % 3 == 0)
			neighbors[3] = true;

		// Override Values in special cases
		if (pos.getY() < 6) {  // In Station
			if (pos.getY() == 5 || pos.getY() == 4) {
				neighbors[0] = true;
				neighbors[2] = true;
			} else if (pos.getY() < 4 && pos.getY() > 0) {
				if (pos.getX() % 3 == 1 || pos.getX() % 3 == 2)
					neighbors[2] = true;
				if (pos.getX() % 3 == 1)
					neighbors[0] = false;
				if (pos.getX() % 3 == 2)
					neighbors[0] = true;
				if (pos.getX() % 3 == 0) {
					neighbors[0] = true;
					neighbors[1] = true;
					neighbors[2] = false;
					neighbors[3] = true;
				}
			} else if (pos.getY() == 0) {
				neighbors[3] = true;
				if (pos.getX() % 3 == 1)
					neighbors[0] = true;
				if (pos.getX() % 3 == 2)
					neighbors[2] = true;
			}
		}
		if (pos.getX() == 1)
			neighbors[0] = true;
		if (pos.getX() == SIZE_X - 1)
			neighbors[2] = true;
		if (pos.getY() == SIZE_Y - 1)
			neighbors[1] = true;

		return neighbors;
	}

	public PositionType posType(Position pos) {
		if (pos.getX() == 0 || (pos.getY() == 5 || pos.getY() == 4 || pos.getY() == 0) && pos.getX() % 3 == 0)
			return PositionType.BLOCK;
		if (pos.getY() < 6 && pos.getX() % 3 != 2 || pos.getY() < 5)
			return PositionType.STATION;
		if (pos.getX() % 3 == 0 || pos.getY() % 3 == 2)
			return PositionType.WAYPOINT;
		if (pos.getY() == 5 && pos.getX() % 3 == 2)
			return PositionType.WAYPOINT;
		return PositionType.CROSSROADS;
	}

	public boolean isBattery(Position pos) {
		return pos.getY() < 4 && pos.getY() > 0 && pos.getX() % 3 == 0;
	}

	public boolean isDrop(Position pos) {
		return pos.getY() > 6 && pos.getY() % 3 == 2 && pos.getX() % 3 == 0 && pos.getX() != 0;
	}

	public boolean isFill(Position pos) {
		return pos.getY() == 5 && pos.getX() % 3 == 0 && pos.getX() != 0;
	}

	public RobotHandler isRobot(Position pos) {
		for (RobotHandler robot : handler) {
			if (robot.pos().equals(pos))
				return robot;
		}
		return null;
	}

	public List<RobotHandler> getRobots() {
		return handler;
	}
}
