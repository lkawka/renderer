package com.lukkaw.controller;

import com.lukkaw.drawable.Drawable;

public interface DrawableListener {
	void drawableAdded(Drawable drawable);

	void drawableRemoved(Drawable drawable);

	void activeSet(Drawable drawable);
}
