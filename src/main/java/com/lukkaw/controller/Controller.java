package com.lukkaw.controller;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;

import com.lukkaw.Config;
import com.lukkaw.drawable.Drawable;
import com.lukkaw.image.FastImage;
import com.lukkaw.image.Point;

import javafx.scene.input.MouseEvent;

public class Controller {

	private Config config;
	private List<ImageListener> imageListeners = new ArrayList<>();
	private List<DrawableListener> drawableListeners = new ArrayList<>();
	private List<Drawable> drawables = new ArrayList<>();
	private Drawable active;
	private Long highestPriority = 1L;

	public Controller(Config config) {
		this.config = config;
	}

	public void imageClicked(MouseEvent e) {
		if (active != null) {
			active.edit(new Point((int) e.getX(), (int) e.getY()));
			redrawDrawables();
			broadcastActiveSet(active);
		}
	}

	public void addListener(ImageListener listener) {
		this.imageListeners.add(listener);
	}

	public void addListener(DrawableListener listener) {
		this.drawableListeners.add(listener);
	}

	public void addDrawable(Drawable drawable) {
		drawables.add(drawable);
		broadcastDrawableAdded(drawable);

		setActive(drawable);
	}

	public void removeDrawable(Drawable drawable) {
		drawables.remove(drawable);

		resetActive();
		broadcastDrawableRemoved(drawable);
	}

	public void setActive(Drawable drawable) {
		if (active != null) {
			active.setIsActive(false);
		}

		active = drawable;
		drawable.setIsActive(true);
		drawable.getShape().setPriority(incrementAndGetHighestPriority());

		refresh();
	}

	public void resetActive() {
		if (active != null) {
			active.setIsActive(false);
			active = null;
		}

		refresh();
	}

	public void clear() {
		drawables.forEach(this::broadcastDrawableRemoved);
		drawables.clear();

		highestPriority = 1L;

		resetActive();
	}

	public void load(List<Drawable> drawables) {
		clear();

		if (drawables != null) {
			this.drawables.addAll(drawables);
			highestPriority += drawables.size();

			drawables.forEach(this::broadcastDrawableAdded);

			refresh();
		}
	}

	public List<Drawable> getDrawables() {
		return new ArrayList<>(drawables);
	}

	public void refresh() {
		broadcastActiveSet(active);
		redrawDrawables();
	}

	private void redrawDrawables() {
		FastImage image = new FastImage(config);
		drawables.stream()
				.sorted(comparing(d -> d.getShape().getPriority()))
				.forEach(drawable -> drawable.draw(image));
		draw(image);
	}

	private void draw(FastImage image) {
		imageListeners.forEach(listener -> listener.draw(image));
	}

	private void broadcastDrawableAdded(Drawable drawable) {
		drawableListeners.forEach(listener -> listener.drawableAdded(drawable));
	}

	private void broadcastDrawableRemoved(Drawable drawable) {
		drawableListeners.forEach(listener -> listener.drawableRemoved(drawable));
	}

	private void broadcastActiveSet(Drawable drawable) {
		drawableListeners.forEach(listener -> listener.activeSet(drawable));
	}

	private Long incrementAndGetHighestPriority() {
		highestPriority += 1;
		return highestPriority;
	}
}
