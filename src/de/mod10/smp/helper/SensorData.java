package de.mod10.smp.helper;

/**
 * @author Paul
 * @since 04.07.2018
 */
public interface SensorData {

	Position pos();
	PositionType posType();
	Orientation posOrientation();
	boolean blockedFront();
	boolean blockedLeft();
	boolean blockedRight();
	boolean blockedWaypointFront();
	boolean blockedWaypointLeft();
	boolean blockedWaypointRight();
	boolean blockedCrossroadFront();
	boolean blockedCrossroadRight();
}
	

