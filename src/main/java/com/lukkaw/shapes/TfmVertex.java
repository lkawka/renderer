package com.lukkaw.shapes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lukkaw.Config.S_X;
import static com.lukkaw.Config.S_Y;

import com.lukkaw.arithmetics.Vector;
import com.lukkaw.image.Point;

import lombok.ToString;

@ToString
public class TfmVertex {
	public final Vector pG;
	public final Vector nG;
	public final Vector pPrim;

	public TfmVertex(Vector pG, Vector nG, Vector pPrim) {
		checkArgument(pG.raw.length == nG.raw.length);
		checkArgument(nG.raw.length == pPrim.raw.length);

		this.pG = pG.copy();
		this.nG = nG.copy();
		this.pPrim = pPrim.copy();
	}

	public Point toPoint() {
		return new Point((int) pPrim.raw[0] * (S_X / 2) + S_X / 2, (int) pPrim.raw[1] * (S_Y / 2) + S_Y / 2);
	}
}
