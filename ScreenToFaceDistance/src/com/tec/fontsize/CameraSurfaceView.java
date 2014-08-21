package com.tec.fontsize;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.tec.fontsize.measurement.Point;
import com.tec.fontsize.messages.MeasurementStepMessage;
import com.tec.fontsize.messages.MessageHUB;
import com.tec.fontsize.utils.Util;

public class CameraSurfaceView extends SurfaceView implements Callback,
		Camera.PreviewCallback {

	/**
	 * Represents the standard height of a peace of a4 paper e.g. 29.7cm
	 */
	public static final int CALIBRATION_DISTANCE_A4_MM = 294;

	public static final int CALIBRATION_MEASUREMENTS = 10;

	public static final int AVERAGE_THREASHHOLD = 5;

	/**
	 * Measured distance at calibration point
	 */
	private float _distanceAtCalibrationPoint = -1;

	private float _currentAvgEyeDistance = -1;

	// private int _facesFoundInMeasurement = -1;

	/**
	 * in cm
	 */
	private float _currentDistanceToFace = -1;

	private final SurfaceHolder mHolder;

	private Camera mCamera;

	private Face _foundFace = null;

	private int _threashold = CALIBRATION_MEASUREMENTS;

	private FaceDetectionThread _currentFaceDetectionThread;

	private List<Point> _points;

	protected final Paint _middlePointColor = new Paint();
	protected final Paint _eyeColor = new Paint();

	private Size _previewSize;

	// private boolean _measurementStartet = false;
	private boolean _calibrated = false;
	private boolean _calibrating = false;
	private int _calibrationsLeft = -1;

	public CameraSurfaceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		_middlePointColor.setARGB(100, 200, 0, 0);
		_middlePointColor.setStyle(Paint.Style.FILL);
		_middlePointColor.setStrokeWidth(2);

		_eyeColor.setColor(Color.GREEN);

		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public void setCamera(final Camera camera) {
		mCamera = camera;

		if (mCamera != null) {
			requestLayout();

			Camera.Parameters params = mCamera.getParameters();
			camera.setDisplayOrientation(90);
			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);
			}
		}
	}

	/**
	 * Variables for the onDraw method, in order to prevent variable allocation
	 * to slow down the sometimes heavily called onDraw method
	 */
	private final PointF _middlePoint = new PointF();
	private final Rect _trackingRectangle = new Rect();

	private final static int RECTANGLE_SIZE = 20;
	private boolean _showEyes = false;
	private boolean _showTracking = true;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(final Canvas canvas) {
		// super.onDraw(canvas);

		if (_foundFace != null) {

			_foundFace.getMidPoint(_middlePoint);

			Log.i("Camera", _middlePoint.x + " : " + _middlePoint.y);

			// portrait mode!
			float heightRatio = canvas.getHeight() / (float) _previewSize.width;
			float widthRatio = canvas.getWidth() / (float) _previewSize.height;

			Log.i("Drawcall", _middlePoint.x + " : " + _middlePoint.y);

			int realX = (int) (_middlePoint.x * widthRatio);
			int realY = (int) (_middlePoint.y * heightRatio);

			Log.i("Drawcall", "Real :" + realX + " : " + realY);
			int halfEyeDist = (int) (widthRatio * _foundFace.eyesDistance() / 2);

			if (_showTracking) {
				// Middle point
				_trackingRectangle.left = realX - RECTANGLE_SIZE;
				_trackingRectangle.top = realY - RECTANGLE_SIZE;
				_trackingRectangle.right = realX + RECTANGLE_SIZE;
				_trackingRectangle.bottom = realY + RECTANGLE_SIZE;
				canvas.drawRect(_trackingRectangle, _middlePointColor);
			}

			if (_showEyes) {
				// Left eye
				_trackingRectangle.left = realX - halfEyeDist - RECTANGLE_SIZE;
				_trackingRectangle.top = realY - RECTANGLE_SIZE;
				_trackingRectangle.right = realX - halfEyeDist + RECTANGLE_SIZE;
				_trackingRectangle.bottom = realY + RECTANGLE_SIZE;
				canvas.drawRect(_trackingRectangle, _eyeColor);

				// Right eye
				_trackingRectangle.left = realX + halfEyeDist - RECTANGLE_SIZE;
				_trackingRectangle.top = realY - RECTANGLE_SIZE;
				_trackingRectangle.right = realX + halfEyeDist + RECTANGLE_SIZE;
				_trackingRectangle.bottom = realY + RECTANGLE_SIZE;
				canvas.drawRect(_trackingRectangle, _eyeColor);
			}
		}
	}

	public void reset() {
		_distanceAtCalibrationPoint = -1;
		_currentAvgEyeDistance = -1;
		_calibrated = false;
		_calibrating = false;
		_calibrationsLeft = -1;
	}

	/**
	 * Sets this current EYE distance to be the distance of a peace of a4 paper
	 * e.g. 29,7cm
	 */
	public void calibrate() {
		if (!_calibrating || !_calibrated) {
			_points = new ArrayList<Point>();
			_calibrating = true;
			_calibrationsLeft = CALIBRATION_MEASUREMENTS;
			_threashold = CALIBRATION_MEASUREMENTS;
		}
	}

	private void doneCalibrating() {
		_calibrated = true;
		_calibrating = false;
		_currentFaceDetectionThread = null;
		// _measurementStartet = false;

		_threashold = AVERAGE_THREASHHOLD;

		_distanceAtCalibrationPoint = _currentAvgEyeDistance;
		MessageHUB.get().sendMessage(MessageHUB.DONE_CALIBRATION, null);
	}

	public boolean isCalibrated() {
		return _calibrated || _calibrating;
	}

	public void showMiddleEye(final boolean on) {
		_showTracking = on;
	}

	public void showEyePoints(final boolean on) {
		_showEyes = on;
	}

	private void updateMeasurement(final FaceDetector.Face currentFace) {
		if (currentFace == null) {
			// _facesFoundInMeasurement--;
			return;
		}

		_foundFace = _currentFaceDetectionThread.getCurrentFace();

		_points.add(new Point(_foundFace.eyesDistance(),
				CALIBRATION_DISTANCE_A4_MM
						* (_distanceAtCalibrationPoint / _foundFace
								.eyesDistance())));

		while (_points.size() > _threashold) {
			_points.remove(0);
		}

		float sum = 0;
		for (Point p : _points) {
			sum += p.getEyeDistance();
		}

		_currentAvgEyeDistance = sum / _points.size();

		_currentDistanceToFace = CALIBRATION_DISTANCE_A4_MM
				* (_distanceAtCalibrationPoint / _currentAvgEyeDistance);

		_currentDistanceToFace = Util.MM_TO_CM(_currentDistanceToFace);

		MeasurementStepMessage message = new MeasurementStepMessage();
		message.setConfidence(currentFace.confidence());
		message.setCurrentAvgEyeDistance(_currentAvgEyeDistance);
		message.setDistToFace(_currentDistanceToFace);
		message.setEyesDistance(currentFace.eyesDistance());
		message.setMeasurementsLeft(_calibrationsLeft);
		message.setProcessTimeForLastFrame(_processTimeForLastFrame);

		MessageHUB.get().sendMessage(MessageHUB.MEASUREMENT_STEP, message);
	}

	private long _lastFrameStart = System.currentTimeMillis();
	private float _processTimeForLastFrame = -1;

	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if (_calibrationsLeft == -1)
			return;

		if (_calibrationsLeft > 0) {
			// Doing calibration !

			if (_currentFaceDetectionThread != null
					&& _currentFaceDetectionThread.isAlive()) {
				// Drop Frame
				return;
			}

			// No face detection started or already finished
			_processTimeForLastFrame = System.currentTimeMillis()
					- _lastFrameStart;
			_lastFrameStart = System.currentTimeMillis();

			if (_currentFaceDetectionThread != null) {
				_calibrationsLeft--;
				updateMeasurement(_currentFaceDetectionThread.getCurrentFace());

				if (_calibrationsLeft == 0) {
					doneCalibrating();

					invalidate();
					return;
				}
			}

			_currentFaceDetectionThread = new FaceDetectionThread(data,
					_previewSize);
			_currentFaceDetectionThread.start();

			invalidate();
		} else {
			// Simple Measurement

			if (_currentFaceDetectionThread != null
					&& _currentFaceDetectionThread.isAlive()) {
				// Drop Frame
				return;
			}

			// No face detection started or already finished
			_processTimeForLastFrame = System.currentTimeMillis()
					- _lastFrameStart;
			_lastFrameStart = System.currentTimeMillis();

			if (_currentFaceDetectionThread != null)
				updateMeasurement(_currentFaceDetectionThread.getCurrentFace());

			_currentFaceDetectionThread = new FaceDetectionThread(data,
					_previewSize);
			_currentFaceDetectionThread.start();
			invalidate();
		}
	}

	/*
	 * SURFACE METHODS, TO CREATE AND RELEASE SURFACE THE CORRECT WAY.
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		synchronized (this) {
			// This allows us to make our own drawBitmap
			this.setWillNotDraw(false);
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		mCamera.release();
		mCamera = null;
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format,
			final int width, final int height) {

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		Parameters parameters = mCamera.getParameters();
		_previewSize = parameters.getPreviewSize();
		// mCamera.setDisplayOrientation(90);
		// mCamera.setParameters(parameters);

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			mCamera.setPreviewCallback(this);

		} catch (Exception e) {
			Log.d("This", "Error starting camera preview: " + e.getMessage());
		}
	}
}
