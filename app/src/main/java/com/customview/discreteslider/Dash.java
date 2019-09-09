package com.customview.discreteslider;


import android.support.annotation.FloatRange;

public class Dash {

	public float length;

	public Dash(@FloatRange(from = 0) float length) {
		super();
		this.length = Math.max(length, 0);
	}
}
