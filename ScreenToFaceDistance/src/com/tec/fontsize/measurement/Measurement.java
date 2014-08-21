package com.tec.fontsize.measurement;

import java.util.ArrayList;
import java.util.List;

public class Measurement {

	/**
	 * The probants name
	 */
	private String _probantName;

	/**
	 * Which device was used
	 */
	private String _deviceName;

	/**
	 * The used resolution of the front camera during this measurement
	 */
	private String _camResolution;

	/**
	 * The amount of measurements which are combined into one measurement point
	 */
	private int _averageNumber;

	/**
	 * The position of the device in the 'real world' when the calibration is
	 * done
	 */
	private float _calibrationPosition;

	/**
	 * The used calibration distance, e.g. an A4-paper (29.7 cm)
	 */
	private float _calibrationDistance;

	/**
	 * All measurement points during this measurement
	 */
	private final List<MeasurementPoint> _measurementPoints = new ArrayList<MeasurementPoint>();

	/**
	 * Returns the measured failure occured while the Head is for example not
	 * exactly at Point 0 at the calibration
	 * 
	 * @return
	 */
	public float getMeasuredFailure() {
		return _calibrationPosition - _calibrationDistance;
	}

	public boolean addPoint(final MeasurementPoint p) {
		return _measurementPoints.add(p);
	}

	public List<MeasurementPoint> getMeasurementPoints() {
		return _measurementPoints;
	}

	public String getProbantName() {
		return _probantName;
	}

	public void setProbantName(final String probantName) {
		_probantName = probantName;
	}

	public String getDeviceName() {
		return _deviceName;
	}

	public void setDeviceName(final String deviceName) {
		_deviceName = deviceName;
	}

	/**
	 * The used resolution of the front camera during this measurement
	 */
	public String getCamResolution() {
		return _camResolution;
	}

	/**
	 * The used resolution of the front camera during this measurement
	 */
	public void setCamResolution(final String camResolution) {
		_camResolution = camResolution;
	}

	/**
	 * The amount of measurements which are combined into one measurement point
	 */
	public int getAverageNumber() {
		return _averageNumber;
	}

	/**
	 * The amount of measurements which are combined into one measurement point
	 */
	public void setAverageNumber(final int averageNumber) {
		_averageNumber = averageNumber;
	}

	/**
	 * The position of the device in the 'real world' in cm when the calibration
	 * is done
	 */
	public float getCalibrationPosition() {
		return _calibrationPosition;
	}

	/**
	 * The position of the device in the 'real world' in cm when the calibration
	 * is done
	 */
	public void setCalibrationPosition(final float calibrationPosition) {
		_calibrationPosition = calibrationPosition;
	}

	/**
	 * The used calibration distance, e.g. an A4-paper (294 mm)
	 */
	public float getCalibrationDistance() {
		return _calibrationDistance;
	}

	/**
	 * The used calibration distance, e.g. an A4-paper (294 mm)
	 */
	public void setCalibrationDistance(final float calibrationDistance) {
		_calibrationDistance = calibrationDistance;
	}

}
