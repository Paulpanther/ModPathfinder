package de.mod10.smp;

import de.mod10.smp.helper.Position;

import java.util.List;

public class ServerRobotHandler {

	private Grid grid;

	public ServerRobotHandler() {
		grid = new Grid();
	}

	public RobotHandler addRobot() {
		return grid.registerRobotHandler(new Position(2, 5));
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
