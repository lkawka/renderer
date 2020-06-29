package com.lukkaw.controller;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CameraMovement {
	public final Double dAlpha;
	public final Double dBeta;
	public final Double dGamma;
	public final Double dz;

	public CameraMovement dAlpha(double v) {
		return new CameraMovement(this.dAlpha + v, dBeta, dGamma, dz);
	}

	public CameraMovement dBeta(double v) {
		return new CameraMovement(this.dAlpha, dBeta + v, dGamma, dz);
	}

	public CameraMovement dGamma(double v) {
		return new CameraMovement(this.dAlpha, dBeta, dGamma + v, dz);
	}

	public CameraMovement dz(double v) {
		return new CameraMovement(dAlpha, dBeta, dGamma, dz + v);
	}
}
