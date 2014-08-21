package com.tec.fontsize.measurement;

public class Point {

	private final float _eyeDistance;

	private final float _deviceDistance;

	public Point(final float eyeDistance, final float deviceDistance) {
		_eyeDistance = eyeDistance;
		_deviceDistance = deviceDistance;
	}

	/**
	 * in mm
	 * 
	 * @return
	 */
	public float getEyeDistance() {
		return _eyeDistance;
	}

	/**
	 * in mm
	 * 
	 * @return
	 */
	public float getDeviceDistance() {
		return _deviceDistance;
	}
}
