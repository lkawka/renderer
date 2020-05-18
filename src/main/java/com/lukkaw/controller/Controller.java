package com.lukkaw.controller;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.lukkaw.Config;
import com.lukkaw.drawable.Drawable;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.Color;
import com.lukkaw.image.Point;

import javafx.scene.input.MouseEvent;

public class Controller {

	private final Config config;
	private final List<ImageListener> imageListeners = new ArrayList<>();
	private final List<DrawableListener> drawableListeners = new ArrayList<>();
	private final List<Drawable> drawables = new ArrayList<>();
	private Drawable active;
	private Long highestPriority = 1L;
	private Boolean useAntiAliasing = false;

	private Color borderColor;
	private Color fillColor;
	private boolean fill = false;

	public Controller(Config config) {
		this.config = config;
	}

	public void imageClicked(MouseEvent e) {
		Point click = new Point((int) e.getX(), (int) e.getY());
		if (fill) {
			fill(click);
			fill = false;
		} else if (active != null) {
			active.edit(click);
			refresh();
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

	public void setFill(Color borderColor, Color fillColor) {
		fill = true;
		this.borderColor = borderColor;
		this.fillColor = fillColor;
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

	public Boolean getAntiAliasing() {
		return useAntiAliasing;
	}

	public void setAntiAliasing(boolean useAntiAliasing) {
		this.useAntiAliasing = useAntiAliasing;
		refresh();
	}

	private void redrawDrawables() {
		Canvas image = new Canvas(config, useAntiAliasing);
		redrawDrawables(image);
	}

	private void redrawDrawables(Canvas image) {
		drawables.stream()
				.sorted(comparing(d -> d.getShape().getPriority()))
				.forEach(drawable -> drawable.draw(image));
		draw(image);
	}

	private void draw(Canvas image) {
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

	private void fill(Point click) {
		Canvas image = new Canvas(config, useAntiAliasing);
		drawables.stream()
				.sorted(comparing(d -> d.getShape().getPriority()))
				.forEach(drawable -> drawable.draw(image));

		Queue<Point> points = new LinkedList<>();
		points.add(click);
		while (!points.isEmpty()) {
			Point point = points.poll();
			Color c = image.getPixel(point);
			if (!c.equals(borderColor) && !c.equals(fillColor)) {
				image.setPixel(point, fillColor);
				if (point.x > 0) {
					points.add(new Point(point.x - 1, point.y));
				}
				if (point.y > 0) {
					points.add(new Point(point.x, point.y - 1));
				}
				if (point.x < image.getWidth() - 1) {
					points.add(new Point(point.x + 1, point.y));
				}
				if (point.y < image.getHeight() - 1) {
					points.add(new Point(point.x, point.y + 1));
				}
			}
		}
		draw(image);
	}
}
