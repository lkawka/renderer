package com.lukkaw.ui;

import com.lukkaw.controller.Controller;
import com.lukkaw.controller.DrawableListener;
import com.lukkaw.drawable.CircleDrawable;
import com.lukkaw.drawable.Drawable;
import com.lukkaw.drawable.DrawableState;
import com.lukkaw.drawable.LineDrawable;
import com.lukkaw.drawable.PartCircleDrawable;
import com.lukkaw.drawable.PolygonDrawable;
import com.lukkaw.drawable.RectangleDrawable;
import com.lukkaw.image.Color;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DrawableControl implements DrawableListener {

	boolean cancelMovingMode = false;
	private Controller controller;
	private Integer drawablesCount = 0;
	private ListView<Drawable> listView;
	private ObservableList<Drawable> drawableListCells = FXCollections.observableArrayList();
	private Label selectedLabel;
	private Button moveShapeButton;
	private ColorPicker selectedColorPicker;
	private VBox selectedBox;
	private TextField thicknessField;

	public DrawableControl(Controller controller) {
		this.controller = controller;

		controller.addListener(this);
	}

	public Node createUI() {
		VBox vBox = createVBox();
		vBox.setPrefWidth(320);
		vBox.getChildren().addAll(createNewDrawableMenu(), createAntiAliasingControl(), createDrawableList(),
				createSingleDrawableControl());
		return vBox;
	}

	@Override
	public void drawableAdded(Drawable drawable) {
		setDrawableName(drawable);
		drawableListCells.addAll(drawable);
	}

	@Override
	public void drawableRemoved(Drawable drawable) {
		if (!drawableListCells.remove(drawable)) {
			throw new RuntimeException("Cell for removal not found: " + drawable);
		}
	}

	@Override
	public void activeSet(Drawable drawable) {
		selectedBox.setDisable(drawable == null);
		if (drawable != null) {
			listView.getSelectionModel().select(drawable);

			selectedLabel.setText(drawable.toString());

			if (drawable.getState() == DrawableState.DONE) {
				moveShapeButton.setText("Move");
				moveShapeButton.setDisable(false);
				cancelMovingMode = false;
			} else if (drawable.getState() == DrawableState.MOVING) {
				moveShapeButton.setText("Cancel moving");
				moveShapeButton.setDisable(false);
				cancelMovingMode = true;
			} else {
				moveShapeButton.setText("Move");
				moveShapeButton.setDisable(true);
				cancelMovingMode = true;
			}

			selectedColorPicker.setValue(drawable.getShape().getColor().cast());

			thicknessField.setText(drawable.getShape().getBrush().toString());
		} else {
			listView.getSelectionModel().clearSelection();

			selectedLabel.setText("");
		}
	}

	private Node createNewDrawableMenu() {
		Label label = new Label("Add new shape");

		Button addLineButton = new Button("Line");
		Button addCircleButton = new Button("Circle");
		Button addPolygonButton = new Button("Polygon");
		Button addPartCircleButton = new Button("Part Circle");
		Button addRectangleButton = new Button("Rectangle");

		addLineButton.setOnAction(e -> controller.addDrawable(new LineDrawable()));
		addCircleButton.setOnAction(e -> controller.addDrawable(new CircleDrawable()));
		addPolygonButton.setOnAction(e -> controller.addDrawable(new PolygonDrawable()));
		addPartCircleButton.setOnAction(e -> controller.addDrawable(new PartCircleDrawable()));
		addRectangleButton.setOnAction(e -> controller.addDrawable(new RectangleDrawable()));

		HBox buttonRow1 = new HBox();
		buttonRow1.setSpacing(4);
		buttonRow1.setAlignment(Pos.CENTER);
		buttonRow1.getChildren().addAll(addLineButton, addCircleButton, addPolygonButton);

		HBox buttonRow2 = new HBox();
		buttonRow2.setSpacing(4);
		buttonRow2.setAlignment(Pos.CENTER);
		buttonRow2.getChildren().addAll(addPartCircleButton, addRectangleButton);

		VBox vBox = createVBox();
		vBox.getChildren().addAll(label, buttonRow1, buttonRow2);

		return vBox;
	}

	private Node createAntiAliasingControl() {
		CheckBox antiAliasingCheckBox = new CheckBox("Anti aliasing");
		antiAliasingCheckBox.setSelected(controller.getAntiAliasing());
		antiAliasingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null && oldValue != newValue) {
				controller.setAntiAliasing(newValue);
			}
		});
		return antiAliasingCheckBox;
	}

	private Node createDrawableList() {
		Label label = new Label("Shapes");

		listView = new ListView<>();
		listView.setPrefSize(40, 100);
		drawableListCells.addAll(controller.getDrawables());
		listView.setItems(drawableListCells);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue != oldValue) {
				controller.setActive(observable.getValue());
			}
		});

		VBox vBox = createVBox();
		vBox.getChildren().addAll(label, listView);
		return vBox;
	}

	private Node createSingleDrawableControl() {
		selectedLabel = new Label("");

		moveShapeButton = new Button("Move");
		moveShapeButton.setOnAction(e -> {
			if (isAnyDrawableSelected()) {
				getSelectedDrawable().setState(cancelMovingMode ? DrawableState.DONE : DrawableState.MOVING);
				controller.refresh();
			}
		});

		Button removeShapeButton = new Button("Remove");
		removeShapeButton.setOnAction(e -> {
			if (isAnyDrawableSelected()) {
				controller.removeDrawable(getSelectedDrawable());
			}
		});

		selectedColorPicker = new ColorPicker();
		selectedColorPicker.setOnAction(e -> {
			if (isAnyDrawableSelected()) {
				getSelectedDrawable().getShape().setColor(new Color(selectedColorPicker.getValue()));
				controller.refresh();
			}
		});

		Label thicknessLabel = new Label("Thickness");
		thicknessField = new TextField();
		thicknessField.setPrefWidth(32);
		thicknessField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String formattedValue = newValue;
				if (!newValue.matches("\\d*")) {
					formattedValue = newValue.replaceAll("[^\\d]", "");
					thicknessField.setText(formattedValue);
				}

				if (!formattedValue.equals("") && !formattedValue.equals(oldValue) && isAnyDrawableSelected()) {
					getSelectedDrawable().getShape().setBrush(Integer.parseInt(formattedValue));
					controller.refresh();
				}
			}
		});

		HBox buttonsBox = new HBox();
		buttonsBox.setSpacing(4);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.getChildren().addAll(moveShapeButton, removeShapeButton);

		HBox thicknessBox = new HBox();
		thicknessBox.setSpacing(8);
		thicknessBox.setAlignment(Pos.CENTER);
		thicknessBox.getChildren().addAll(thicknessLabel, thicknessField);

		selectedBox = createVBox();
		selectedBox.setDisable(true);
		selectedBox.getChildren().addAll(selectedLabel, buttonsBox, selectedColorPicker, thicknessBox);
		return selectedBox;
	}

	private Drawable getSelectedDrawable() {
		if (isAnyDrawableSelected()) {
			return listView.getSelectionModel().getSelectedItem();
		}
		throw new RuntimeException("No drawable selected");
	}

	private boolean isAnyDrawableSelected() {
		return listView != null && listView.getSelectionModel().getSelectedItem() != null;
	}

	private void setDrawableName(Drawable drawable) {
		drawablesCount += 1;
		drawable.setName(drawable.getType().toString() + " #" + drawablesCount);
	}

	private VBox createVBox() {
		VBox vBox = new VBox();
		vBox.setSpacing(8);
		vBox.setPadding(new Insets(8));
		vBox.setAlignment(Pos.CENTER);
		return vBox;
	}
}
