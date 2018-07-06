package de.mod10.smp;

import java.util.List;

public class ServerRobotHandler {

	private Grid grid;

	public ServerRobotHandler() {
		grid = new Grid();
	}

	public void addRobot() {
		RobotHandler robot = grid.registerRobotHandler(new Position(2, 5));
		robot.driveTo(new Position(30, 30));
	}

	public void step() {
		for (RobotHandler robot : grid.getRobots())
			robot.step();
	}

	public List<RobotHandler> getRobots() {
		return grid.getRobots();
	}

	public Grid getGrid() {
		return grid;
	}
}
