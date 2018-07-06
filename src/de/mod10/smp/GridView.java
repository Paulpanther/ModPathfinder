package de.mod10.smp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GridView extends JFrame {

	private static final Dimension SIZE = new Dimension(900, 900);
	private static final int DRAW_SIZE_X = 810, DRAW_SIZE_Y = 810;

	private ServerRobotHandler handler;


	private GridView() {
		handler = new ServerRobotHandler();
		addRobot();

		setLayout(new GridBagLayout());

		DrawPane draw = new DrawPane();
		draw.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
		draw.setPreferredSize(new Dimension(810, 810));
		add(draw, new GridBagConstraints());

		getContentPane().setBackground(new Color(42, 42, 42));
		setSize(SIZE);
		setTitle("Robot Drive Simulator 3000");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(this::step, 0, 100, TimeUnit.MILLISECONDS);
	}

	private void step() {
		handler.step();
		repaint();
	}

	private void addRobot() {
		handler.addRobot();
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
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, DRAW_SIZE_X, DRAW_SIZE_Y);

			for (int x = 0; x < Grid.SIZE_X; x++) {
				for (int y = 0; y < Grid.SIZE_Y; y++) {
					drawBlock(g, new Position(x, y));
				}
			}
		}

		private void drawBlock(Graphics g, Position pos) {
			Graphics2D g2d = (Graphics2D) g;

			if (handler.getGrid().posType(pos) == PositionType.STATION) {
				fillBlock(g, new Color(155, 212, 255), pos);
			} if (handler.getGrid().posType(pos) == PositionType.BLOCK) {
				fillBlock(g, Color.BLACK, pos);
			}  if (handler.getGrid().isFill(pos)) {
				fillBlock(g, Color.MAGENTA, pos);
			} if (handler.getGrid().posType(pos) == PositionType.CROSSROADS) {
				fillBlock(g, new Color(220, 220, 220), pos);
			} if (handler.getGrid().posType(pos) == PositionType.WAYPOINT) {
				fillBlock(g, new Color(240, 240, 240), pos);
			} if (handler.getGrid().isDrop(pos)) {
				fillBlock(g, Color.ORANGE, pos);
			} if (handler.getGrid().isBattery(pos)) {
				fillBlock(g, Color.YELLOW, pos);
				drawBorder(g2d, Color.BLACK, pos, Orientation.NORTH);
				drawBorder(g2d, Color.BLACK, pos, Orientation.WEST);
				drawBorder(g2d, Color.BLACK, pos, Orientation.SOUTH);
			}

			RobotHandler robot = handler.getGrid().isRobot(pos);
			if (robot != null) {
				drawRobot(g, robot, pos);
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
		}

		private void fillBlock(Graphics g, Color color, Position pos) {
			g.setColor(color);
			g.fillRect((int) (pos.getX() * ratio_x), (int) (DRAW_SIZE_Y - (pos.getY()+1) * ratio_y), (int) ratio_x, (int) ratio_y);
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

	private class MouseHandler implements MouseListener {

		private float ratio_x, ratio_y;

		public MouseHandler() {
			ratio_x = DRAW_SIZE_X / Grid.SIZE_X;
			ratio_y = DRAW_SIZE_Y / Grid.SIZE_Y;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			Position target = new Position((int) (e.getX() / ratio_x), (int) ((DRAW_SIZE_Y - e.getY()) / ratio_y));
			handler.getRobots().get(0).driveTo(target);
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
