package com.tec.fontsize.utils;


public class Util {

	public static float CM_TO_MM(final float cm) {
		return cm * 10;
	}

	public static float CM_TO_MM(final int cm) {
		return cm * 10.0f;
	}

	public static float MM_TO_CM(final float mm) {
		return mm / 10;
	}

	public static float MM_TO_CM(final int mm) {
		return mm / 10.0f;
	}
}
