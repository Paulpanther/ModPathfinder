package de.mod10.smp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GridView extends JFrame {

	private static final Dimension SIZE = new Dimension(900, 900);

	private ServerRobotHandler handler;


	private GridView() {
		handler = new ServerRobotHandler();
		addRobot();

		setLayout(new GridBagLayout());

		DrawPane draw = new DrawPane();
		draw.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
		draw.setPreferredSize(new Dimension(800, 800));
		add(draw, new GridBagConstraints());


		setSize(SIZE);
		setTitle("Robot Drive Simulator 3000");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(this::step, 0, 1, TimeUnit.SECONDS);
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
		private int size_x = 800, size_y = 800;

		public DrawPane() {
			ratio_x = size_x / Grid.SIZE_X;
			ratio_y = size_y / Grid.SIZE_Y;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 800, 800);

			for (int x = 0; x < Grid.SIZE_X; x++) {
				for (int y = 0; y < Grid.SIZE_Y; y++) {
					drawBlock(g, new Position(x, y));
				}
			}
		}

		private void drawBlock(Graphics g, Position pos) {
			Graphics2D g2d = (Graphics2D) g;

			if (handler.getGrid().posType(pos) == PositionType.STATION) {
				fillBlock(g, Color.BLUE, pos);
			} if (handler.getGrid().posType(pos) == PositionType.BLOCK) {
				fillBlock(g, Color.BLACK, pos);
			}  if (handler.getGrid().isFill(pos)) {
				fillBlock(g, Color.MAGENTA, pos);
			} if (handler.getGrid().posType(pos) == PositionType.CROSSROADS) {
				fillBlock(g, Color.GRAY, pos);
			} if (handler.getGrid().posType(pos) == PositionType.WAYPOINT) {
				fillBlock(g, Color.LIGHT_GRAY, pos);
			} if (handler.getGrid().isDrop(pos)) {
				fillBlock(g, Color.ORANGE, pos);
			} if (handler.getGrid().isBattery(pos)) {
				fillBlock(g, Color.YELLOW, pos);
				drawBorder(g2d, Color.BLACK, pos, Orientation.NORTH);
				drawBorder(g2d, Color.BLACK, pos, Orientation.WEST);
				drawBorder(g2d, Color.BLACK, pos, Orientation.SOUTH);
			}
		}

		private void fillBlock(Graphics g, Color color, Position pos) {
			g.setColor(color);
			g.fillRect((int) (pos.getX() * ratio_x), (int) (size_y - (pos.getY()+1) * ratio_y), (int) ratio_x, (int) ratio_y);
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

			g.draw(new Line2D.Float((int) (ax * ratio_x), (int) (size_y - ay * ratio_y),
					(int) (bx * ratio_x), (int) (size_y - by * ratio_y)));
		}
	}
}
