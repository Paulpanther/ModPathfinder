package de.mod10.smp;

import de.mod10.smp.config.Colors;
import de.mod10.smp.helper.Orientation;
import de.mod10.smp.helper.Position;
import de.mod10.smp.helper.PositionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GridView extends JFrame {

	private static final Dimension SIZE = new Dimension(900, 900);
	private static final int DRAW_SIZE_X = 810, DRAW_SIZE_Y = 810;

	private int timeMillis = 100;

	private ServerRobotHandler handler;

	private RobotHandler selected;
	private Position mouseOver;
	private boolean moving = false;
	private ScheduledExecutorService service;
	private ScheduledFuture future;


	private GridView() {
		handler = new ServerRobotHandler();
		// addRobot();

		setLayout(new GridBagLayout());

		DrawPane draw = new DrawPane();
		draw.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 2, true));
		draw.setPreferredSize(new Dimension(810, 810));
		add(draw, new GridBagConstraints());

		addKeyListener(new KeyHandler());

		service = Executors.newSingleThreadScheduledExecutor();

		getContentPane().setBackground(Colors.BACKGROUND);
		setSize(SIZE);
		setTitle("Robot Drive Simulator 3000");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void reset() {
		if (moving)
			toggleMove();
		selected = null;
		handler.getRobots().clear();
		repaint();
	}

	private void toggleMove() {
		moving = !moving;
		if (moving) {
			System.out.println(timeMillis);
			future = service.scheduleAtFixedRate(this::step, 0, timeMillis, TimeUnit.MILLISECONDS);
			System.out.println("Moving");
		} else {
			future.cancel(true);
			System.out.println("Stopped");
		}
	}

	private void step() {
		handler.step();
		repaint();
	}

	private void addDebugRobots1() {
		handler.addDebugRobot(new Position(6, 13), new Position(6, 12), Orientation.WEST, Robot.RobotState.WAYPOINT).setColor(getRandomRobotColor());
		handler.addDebugRobot(new Position(6, 12), new Position(1, 5), Orientation.EAST, Robot.RobotState.WAYPOINT).setColor(getRandomRobotColor());

		// handler.addDebugRobot(new Position(4, 12), null, Orientation.EAST, Robot.RobotState.CROSS_RIGHT_UP_LEFT).setColor(getRandomRobotColor());
		repaint();
	}

	private void addDebugRobots2() {
		handler.addDebugRobot(new Position(7, 16), new Position(7, 5), Orientation.SOUTH, Robot.RobotState.CROSS_RIGHT_UP_LEFT).setColor(getRandomRobotColor());
		handler.addDebugRobot(new Position(7, 15), new Position(12, 15), Orientation.EAST, Robot.RobotState.CROSS_RIGHT_UP_LEFT).setColor(getRandomRobotColor());
		handler.addDebugRobot(new Position(8, 16), new Position(3, 15), Orientation.WEST, Robot.RobotState.CROSS_RIGHT_UP_LEFT).setColor(getRandomRobotColor());
		handler.addDebugRobot(new Position(8, 15), new Position(9, 18), Orientation.NORTH, Robot.RobotState.CROSS_RIGHT_UP_LEFT).setColor(getRandomRobotColor());
		repaint();
	}

	private Color getRandomRobotColor() {
		Random r = new Random();
		return Colors.ROBOTS[r.nextInt(Colors.ROBOTS.length)];
	}

	private void addRobot() {
		handler.addRobot().setColor(getRandomRobotColor());
	}

	public static void main(String[] args) {
		new GridView();
	}

	private class DrawPane extends JPanel {

		private float ratio_x, ratio_y;

		public DrawPane() {
			ratio_x = DRAW_SIZE_X / Grid.SIZE_X;
			ratio_y = DRAW_SIZE_Y / Grid.SIZE_Y;

			addMouseListener(new MouseHandler());
			addMouseMotionListener(new MouseHandler());
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Colors.BLOCK);
			g.fillRect(0, 0, DRAW_SIZE_X, DRAW_SIZE_Y);

			for (int x = 0; x < Grid.SIZE_X; x++) {
				for (int y = 0; y < Grid.SIZE_Y; y++) {
					drawBlock(g, new Position(x, y));
				}
			}

			Graphics2D g2d = (Graphics2D) g;
			for (RobotHandler robot : handler.getRobots()) {
				if (robot.getTarget() != null)
					drawTargetLine(g2d, robot.pos(), robot.getTarget(), robot == selected);
			}
		}

		private void drawBlock(Graphics g, Position pos) {
			Graphics2D g2d = (Graphics2D) g;

			if (handler.getGrid().posType(pos) == PositionType.STATION) {
				fillBlock(g, Colors.STATION, pos);
			} if (handler.getGrid().posType(pos) == PositionType.BLOCK) {
				fillBlock(g, Colors.BLOCK, pos);
			} if (handler.getGrid().isFillBlock(pos)) {
				fillBlock(g, Colors.FILL, pos);
			} if (handler.getGrid().posType(pos) == PositionType.CROSSROADS) {
				fillBlock(g, Colors.CROSSROAD, pos);
			} if (handler.getGrid().posType(pos) == PositionType.WAYPOINT) {
				fillBlock(g, Colors.WAYPOINT, pos);
			} if (handler.getGrid().isDrop(pos)) {
				fillBlock(g, Colors.DROP, pos);
			} if (handler.getGrid().isBattery(pos)) {
				fillBlock(g, Colors.BATTERY, pos);
				drawBorder(g2d, Colors.BLOCK, pos, Orientation.NORTH);
				drawBorder(g2d, Colors.BLOCK, pos, Orientation.WEST);
				drawBorder(g2d, Colors.BLOCK, pos, Orientation.SOUTH);
			}

			RobotHandler robot = handler.getGrid().isRobot(pos);
			if (robot != null) {
				drawRobot(g, robot, pos);
			}

			if (mouseOver != null && mouseOver.equals(pos)) {
				if (handler.getGrid().isValidMove(pos))
					g.setColor(Colors.MOUSE_VALID);
				else
					g.setColor(Colors.MOUSE_INVALID);
				g.fillRect((int) ((pos.getX() + .29) * ratio_x), (int) (DRAW_SIZE_Y - (pos.getY()+.72) * ratio_y), (int) (ratio_x*.5), (int) (ratio_y*.5));
			}
		}

		private void drawRobot(Graphics g, RobotHandler robot, Position pos) {
			g.setColor(robot.getColor());

			double[] xs = new double[3];
			double[] ys = new double[3];
			switch (robot.posOrientation()) {
				case NORTH:
					xs = new double[]{0, .5f, 1};
					ys = new double[]{0, 1, 0};
					break;
				case WEST:
					xs = new double[]{1, 0, 1};
					ys = new double[]{0, .5f, 1};
					break;
				case EAST:
					xs = new double[]{0, 1, 0};
					ys = new double[]{1, .5f, 0};
					break;
				case SOUTH:
					xs = new double[]{1, .5f, 0};
					ys = new double[]{1, 0, 1};
					break;
			}

			int[] ixs = Arrays.stream(xs).mapToInt(x -> (int) ((pos.getX() + x) * ratio_x)).toArray();
			int[] iys = Arrays.stream(ys).mapToInt(y -> (int) (DRAW_SIZE_Y - (pos.getY() + y) * ratio_y)).toArray();

			g.fillPolygon(ixs, iys, 3);

			// Draw Selected
			if (selected != null && robot == selected) {
				g.setColor(Colors.SELECTED);
				g.fillRect((int) ((pos.getX() + .29) * ratio_x), (int) (DRAW_SIZE_Y - (pos.getY()+.72) * ratio_y), (int) (ratio_x*.5), (int) (ratio_y*.5));
			}
		}

		private void drawTargetLine(Graphics2D g, Position pos, Position target, boolean highlight) {
			if (highlight)
				g.setColor(Colors.LINE_SELECTED);
			else
				g.setColor(Colors.LINE);
			g.drawLine((int) ((target.getX() + .5) * ratio_x), (int) (DRAW_SIZE_Y - (target.getY()+.5) * ratio_y),
					(int) ((pos.getX() + .5) * ratio_x), (int) (DRAW_SIZE_Y - (pos.getY()+.5) * ratio_y));
		}

		private void fillRect(Graphics g, Position pos, Dimension size) {
			g.fillRect((int) (pos.getX() * ratio_x), (int) (DRAW_SIZE_Y - (pos.getY()+1) * ratio_y), (int) (size.getWidth() * ratio_x), (int) (size.getHeight() * ratio_y));
		}

		private void fillRect(Graphics g, Position pos) {
			fillRect(g, pos, new Dimension(1, 1));
		}

		private void fillBlock(Graphics g, Color color, Position pos) {
			g.setColor(color);
			fillRect(g, pos);
		}

		private void drawBorder(Graphics2D g, Color color, Position pos, Orientation o) {
			g.setColor(color);
			g.setPaintMode();

			int x = pos.getX();
			int y = pos.getY();
			int ax, ay, bx, by;
			ax = ay = bx = by = 0;
			switch (o) {
				case NORTH:
					ax = x;
					bx = x+1;
					ay = by = y+1;
					break;
				case WEST:
					ax = bx = x;
					ay = y;
					by = y+1;
					break;
				case EAST:
					ax = bx = x+1;
					ay = y;
					by = y+1;
					break;
				case SOUTH:
					ax = x;
					bx = x+1;
					ay = by = y;
					break;
			}

			g.draw(new Line2D.Float((int) (ax * ratio_x), (int) (DRAW_SIZE_Y - ay * ratio_y),
					(int) (bx * ratio_x), (int) (DRAW_SIZE_Y - by * ratio_y)));
		}
	}

	private class MouseHandler implements MouseListener, MouseMotionListener {

		private float ratio_x, ratio_y;

		public MouseHandler() {
			ratio_x = DRAW_SIZE_X / Grid.SIZE_X;
			ratio_y = DRAW_SIZE_Y / Grid.SIZE_Y;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			Position target = new Position((int) (e.getX() / ratio_x), (int) ((DRAW_SIZE_Y - e.getY()) / ratio_y));

			Grid grid = handler.getGrid();
			RobotHandler robot = grid.isRobot(target);
			if (robot != null) {
				selected = robot;

				System.out.println(robot.print());

				repaint();
			} else if (grid.isValidMove(target)) {
				if (selected != null) {
					selected.driveTo(target);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseOver = new Position((int) (e.getX() / ratio_x), (int) ((DRAW_SIZE_Y - e.getY()) / ratio_y));
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}
	}

	private class KeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				toggleMove();
			} else if (e.getKeyCode() == KeyEvent.VK_A) {
				addRobot();
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (moving)
					toggleMove();
				System.exit(0);
			} else if (e.getKeyCode() == KeyEvent.VK_R) {
				reset();
			} else if (e.getKeyCode() == KeyEvent.VK_X) {
				addDebugRobots1();
			} else if (e.getKeyCode() == KeyEvent.VK_Y) {
				addDebugRobots2();
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				changeSpeed(-1);
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				changeSpeed(1);
			}
		}

		private void changeSpeed(int dir) {
			boolean wasMoving = moving;
			if (moving)
				toggleMove();
			timeMillis = Math.max(10, timeMillis + 10 * dir);
			if (wasMoving)
				toggleMove();
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
}
