package de.mod10.smp;

/**
 * @author Paul
 * @since 04.07.2018
 */
public class Robot implements ISensorInfo, IRobotActorInfo {

	private SensorData data = null;


	public void driveTo(Position pos) {
		if (data != null) {

		}
	}

	@Override
	public void unloaded() {

	}

	@Override
	public void sensorEvent(SensorData data) {
		this.data = data;
	}
}
