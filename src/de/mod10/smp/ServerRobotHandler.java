package de.mod10.smp;

import de.mod10.smp.helper.Orientation;
import de.mod10.smp.helper.Position;
import de.mod10.smp.helper.PositionType;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ServerRobotHandler {

	private Grid grid;
	private int[] fillQueue;
	private int lastID = 0;

	public ServerRobotHandler() {
		grid = new Grid();
		fillQueue = new int[grid.getFillCount()];
	}

	public RobotHandler addDebugRobot(Position pos, Position target, Orientation orient, Robot.RobotState state) {
		return grid.addDebugRobot(pos, target, orient, state, lastID++);
	}

	public RobotHandler addRobot() {
		return grid.spawnRobotOnBattery(0, lastID++);
	}

	public void step() {
		for (RobotHandler robot : grid.getRobots()) {
			if (!robot.isDriving()) {
				Position robPos = robot.pos();
				if (grid.isFillPosition(robPos) || grid.posType(robPos) == PositionType.STATION) {

					if (grid.isFillPosition(robPos)) {
						int fill = grid.getFillByPosition(robPos);
						fillQueue[fill] = Math.min(fillQueue[fill]-1, 0);
					}

					int drop = getRandomDrop();
					grid.moveRobotToDrop(drop, robot);
				} else {
					int fill = getRandomFill();
					grid.moveRobotToFill(fill, robot);
				}
			}
		}

		for (RobotHandler robot : grid.getRobots())
			robot.step();
	}

	public List<RobotHandler> getRobots() {
		return grid.getRobots();
	}

	public Grid getGrid() {
		return grid;
	}

	private int getRandomDrop() {
		Random r = new Random();
		return r.nextInt(grid.getDropCount());
	}

	private int getRandomFill() {
		int[] available = IntStream.range(0, fillQueue.length).filter(i -> fillQueue[i] < 6).toArray();
		Random r = new Random();
		return available[r.nextInt(available.length)];
	}
}
