package com.tec.fontsize.messages;

public class MeasurementStepMessage {

	private float _currentAvgEyeDistance;
	private float _confidence;
	private float _distToFace;
	private float _processTimeForLastFrame;
	private float _eyesDistance;
	private int _measurementsLeft;
	private float _measuredPositionWithoutMistake;

	public float getCurrentAvgEyeDistance() {
		return _currentAvgEyeDistance;
	}

	public void setCurrentAvgEyeDistance(final float currentAvgEyeDistance) {
		_currentAvgEyeDistance = currentAvgEyeDistance;
	}

	public float getConfidence() {
		return _confidence;
	}

	public void setConfidence(final float confidence) {
		_confidence = confidence;
	}

	public float getDistToFace() {
		return _distToFace;
	}

	public void setDistToFace(final float distToFace) {
		_distToFace = distToFace;
	}

	public float getProcessTimeForLastFrame() {
		return _processTimeForLastFrame;
	}

	public void setProcessTimeForLastFrame(final float processTimeForLastFrame) {
		_processTimeForLastFrame = processTimeForLastFrame;
	}

	public float getEyesDistance() {
		return _eyesDistance;
	}

	public void setEyesDistance(final float eyesDistance) {
		_eyesDistance = eyesDistance;
	}

	public int getMeasurementsLeft() {
		return _measurementsLeft;
	}

	public void setMeasurementsLeft(final int measurementsLeft) {
		_measurementsLeft = measurementsLeft;
	}

	public float getMeasuredPositionWithoutMistake() {
		return _measuredPositionWithoutMistake;
	}

	public void setMeasuredPositionWithoutMistake(
			float measuredPositionWithoutMistake) {
		_measuredPositionWithoutMistake = measuredPositionWithoutMistake;
	}

}
