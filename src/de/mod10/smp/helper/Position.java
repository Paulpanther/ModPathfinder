package de.mod10.smp.helper;

import java.util.Objects;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Position {

	private int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void addToX(int x) {
		this.x += x;
	}

	public void addToY(int y) {
		this.y += y;
	}

	public static Position add(Position p1, Position p2) {
		return new Position(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return x == position.x &&
				y == position.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "Position{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
