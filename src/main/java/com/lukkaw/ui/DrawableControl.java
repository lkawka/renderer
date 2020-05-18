package com.lukkaw.ui;

import java.io.File;
import java.io.IOException;

import com.lukkaw.controller.Controller;
import com.lukkaw.controller.DrawableListener;
import com.lukkaw.drawable.CircleDrawable;
import com.lukkaw.drawable.Drawable;
import com.lukkaw.drawable.DrawableState;
import com.lukkaw.drawable.LineDrawable;
import com.lukkaw.drawable.PartCircleDrawable;
import com.lukkaw.drawable.PolygonDrawable;
import com.lukkaw.drawable.RectangleDrawable;
import com.lukkaw.drawable.ShapeType;
import com.lukkaw.image.Color;
import com.lukkaw.shape.Polygon;
import com.lukkaw.shape.Rectangle;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DrawableControl implements DrawableListener {
	private static final File INITIAL_DIR = new File("src/main/resources");

	boolean cancelMovingMode = false;
	private final Controller controller;
	private final Stage stage;
	private Integer drawablesCount = 0;
	private final ObservableList<Drawable> allDrawables = FXCollections.observableArrayList();
	private ListView<Drawable> allShapesListView;
	private Label selectedLabel;
	private Button moveShapeButton;
	private ColorPicker selectedColorPicker;
	private VBox selectedBox;
	private TextField thicknessField;
	private Node fillControl;
	private Node clipControl;
	private RadioButton noFillButton;
	private RadioButton colorFillButton;
	private RadioButton imageFillButton;
	private ToggleGroup fillToggleGroup;
	private CheckBox clipCheckBox;
	private ListView<Drawable> clipListView;

	public DrawableControl(Controller controller, Stage stage) {
		this.controller = controller;
		this.stage = stage;

		controller.addListener(this);
	}

	public Node createUI() {
		VBox vBox = createVBox();
		vBox.setPrefWidth(300);
		vBox.setPadding(new Insets(4));
		vBox.getChildren().addAll(createNewDrawableMenu(), createAntiAliasingControl(), createBorderFillControl(),
				createDrawableList(),
				createSingleDrawableControl());
		return vBox;
	}

	@Override
	public void drawableAdded(Drawable drawable) {
		setDrawableName(drawable);
		allDrawables.addAll(drawable);
	}

	@Override
	public void drawableRemoved(Drawable drawable) {
		if (!allDrawables.remove(drawable)) {
			throw new RuntimeException("Cell for removal not found: " + drawable);
		}
	}

	@Override
	public void activeSet(Drawable drawable) {
		selectedBox.setDisable(drawable == null);
		if (drawable != null) {
			allShapesListView.getSelectionModel().select(drawable);

			selectedLabel.setText(drawable.toString());

			if (drawable.getType() == ShapeType.PART_CIRCLE) {
				moveShapeButton.setText("Move");
				moveShapeButton.setDisable(true);
			} else if (drawable.getState() == DrawableState.DONE) {
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

			if ((drawable.getType() != ShapeType.POLYGON) || (drawable.getState() == DrawableState.DRAWING)) {
				fillControl.setDisable(true);
				clipControl.setDisable(true);
			} else {
				fillControl.setDisable(false);
				clipControl.setDisable(false);

				Polygon polygon = (Polygon) drawable.getShape();

				if (polygon.getClippedRectangle() != null) {
					var rectangleDrawable = (RectangleDrawable) clipListView.getItems().filtered(
							drawable1 -> drawable1.getShape().equals(polygon.getClippedRectangle()))
							.get(0);
					clipListView.getSelectionModel().select(rectangleDrawable);
				}

				switch (polygon.getFillType()) {
				case NONE:
					fillToggleGroup.selectToggle(noFillButton);
					break;
				case COLOR:
					fillToggleGroup.selectToggle(colorFillButton);
					break;
				case IMAGE:
					fillToggleGroup.selectToggle(imageFillButton);

				}
			}

			selectedColorPicker.setValue(drawable.getShape().getColor().cast());

			thicknessField.setText(drawable.getShape().getBrush().toString());
		} else {
			allShapesListView.getSelectionModel().clearSelection();

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

		HBox buttonRow1 = createHBox();
		buttonRow1.getChildren()
				.addAll(addLineButton, addCircleButton, addPolygonButton, addPartCircleButton, addRectangleButton);

		VBox vBox = createVBox();
		vBox.getChildren().addAll(label, buttonRow1);

		return vBox;
	}

	private Node createAntiAliasingControl() {
		CheckBox antiAliasingCheckBox = new CheckBox("Enable anti aliasing");
		antiAliasingCheckBox.setSelected(controller.getAntiAliasing());
		antiAliasingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != newValue) {
				controller.setAntiAliasing(newValue);
			}
		});
		return antiAliasingCheckBox;
	}

	private Node createBorderFillControl() {
		Label borderColorLabel = new Label("Border color");
		ColorPicker borderColorPicker = new ColorPicker();
		Label fillColorLabel = new Label("Fill color");
		ColorPicker fillColorPicker = new ColorPicker();

		Button fillButton = new Button("Fill");
		fillButton.setOnAction(e -> controller
				.setFill(new Color(borderColorPicker.getValue()), new Color(fillColorPicker.getValue())));

		HBox row1 = createHBox();
		row1.getChildren().addAll(borderColorLabel, borderColorPicker);

		HBox row2 = createHBox();
		row2.getChildren().addAll(fillColorLabel, fillColorPicker);

		HBox row3 = createHBox();
		row3.getChildren().addAll(fillButton);

		VBox vBox = createVBox();
		vBox.getChildren().addAll(row1, row2, row3);
		vBox.setPrefHeight(100);

		return vBox;
	}

	private Node createDrawableList() {
		Label label = new Label("Shapes");

		allShapesListView = new ListView<>();
		allShapesListView.setPrefSize(30, 100);
		allDrawables.addAll(controller.getDrawables());
		allShapesListView.setItems(allDrawables);
		allShapesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		allShapesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue != oldValue) {
				controller.setActive(observable.getValue());
			}
		});

		VBox vBox = createVBox();
		vBox.getChildren().addAll(label, allShapesListView);
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

		fillControl = createFillControl();
		clipControl = createClipControl();

		HBox row1 = createHBox();
		row1.getChildren().addAll(moveShapeButton, removeShapeButton, selectedColorPicker);

		HBox row2 = createHBox();
		row2.setAlignment(Pos.CENTER_LEFT);
		row2.getChildren().addAll(thicknessLabel, thicknessField);

		HBox row3 = createHBox();
		row3.setSpacing(16);
		row3.getChildren().addAll(fillControl, clipControl);

		selectedBox = createVBox();
		selectedBox.setDisable(true);
		selectedBox.getChildren().addAll(selectedLabel, row1, row2, row3);
		return selectedBox;
	}

	private Node createFillControl() {
		Label fillLabel = new Label("Fill");

		noFillButton = new RadioButton("None");
		colorFillButton = new RadioButton("Color");
		imageFillButton = new RadioButton("Image");

		fillToggleGroup = new ToggleGroup();
		noFillButton.setToggleGroup(fillToggleGroup);
		colorFillButton.setToggleGroup(fillToggleGroup);
		imageFillButton.setToggleGroup(fillToggleGroup);
		fillToggleGroup.selectToggle(noFillButton);

		Label selectedImageLabel = new Label("No image selected");
		Button selectFillImageButton = new Button("Select image");

		VBox fillImageSelectorRow = createVBox();
		fillImageSelectorRow.setDisable(true);
		fillImageSelectorRow.setMaxWidth(150);
		fillImageSelectorRow.getChildren().addAll(selectedImageLabel, selectFillImageButton);

		noFillButton.setOnAction(e -> {
			fillImageSelectorRow.setDisable(true);
			((Polygon) getSelectedDrawable().getShape()).setFill(Polygon.FillType.NONE);
			controller.refresh();
		});

		colorFillButton.setOnAction(e -> {
			fillImageSelectorRow.setDisable(true);
			((Polygon) getSelectedDrawable().getShape()).setFill(Polygon.FillType.COLOR);
			controller.refresh();
		});

		imageFillButton.setOnAction(e -> {
			fillImageSelectorRow.setDisable(false);
			((Polygon) getSelectedDrawable().getShape()).setFill(Polygon.FillType.IMAGE);
			controller.refresh();
		});

		selectFillImageButton.setOnAction(e -> {
			File imageFile = loadImage();
			if (imageFile != null) {
				try {
					selectedImageLabel.setText(imageFile.getName());
					((Polygon) getSelectedDrawable().getShape()).setFill(imageFile.getCanonicalPath());
				} catch (IOException ioException) {
					selectedImageLabel.setText("No image selected");
					((Polygon) getSelectedDrawable().getShape()).setFill(Polygon.FillType.NONE);
				}
			} else {
				selectedImageLabel.setText("No image selected");
				((Polygon) getSelectedDrawable().getShape()).setFill(Polygon.FillType.NONE);
			}
			controller.refresh();
		});

		VBox box = createVBox();
		box.getChildren().addAll(fillLabel, noFillButton, colorFillButton, imageFillButton, fillImageSelectorRow);
		return box;
	}

	private Node createClipControl() {
		clipCheckBox = new CheckBox("Clip");

		clipListView = new ListView<>(
				allDrawables.filtered(drawable -> drawable.getType() == ShapeType.RECTANGLE));
		clipListView.setDisable(!clipCheckBox.isSelected());
		clipListView.setPrefSize(150, 150);

		clipCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != newValue) {
				clipListView.setDisable(!newValue);
				Polygon selectedPolygon = (Polygon) getSelectedDrawable().getShape();
				if (newValue) {
					if (clipListView.getSelectionModel().getSelectedItem() != null) {
						Rectangle clippingRectangle = (Rectangle) clipListView.getSelectionModel().getSelectedItem()
								.getShape();
						selectedPolygon.clip(clippingRectangle);
					} else {
						selectedPolygon.clip(null);
					}
				} else {
					selectedPolygon.clip(null);
				}
				controller.refresh();
			}
		});

		clipListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		clipListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue != oldValue) {
				Polygon selectedPolygon = (Polygon) getSelectedDrawable().getShape();
				Rectangle clippingRectangle = (Rectangle) clipListView.getSelectionModel().getSelectedItem().getShape();
				selectedPolygon.clip(clippingRectangle);
				controller.refresh();
			}
		});

		VBox box = createVBox();
		box.getChildren().addAll(clipCheckBox, clipListView);
		return box;
	}

	private File loadImage() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg",
				"*.png");
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setTitle("Open shapes");
		fileChooser.setInitialDirectory(INITIAL_DIR);

		return fileChooser.showOpenDialog(stage);
	}

	private Drawable getSelectedDrawable() {
		if (isAnyDrawableSelected()) {
			return allShapesListView.getSelectionModel().getSelectedItem();
		}
		throw new RuntimeException("No drawable selected");
	}

	private boolean isAnyDrawableSelected() {
		return allShapesListView != null && allShapesListView.getSelectionModel().getSelectedItem() != null;
	}

	private void setDrawableName(Drawable drawable) {
		drawablesCount += 1;
		drawable.setName(drawable.getType().toString() + " #" + drawablesCount);
	}

	private VBox createVBox() {
		VBox box = new VBox();
		box.setSpacing(8);
		box.setAlignment(Pos.TOP_LEFT);
		return box;
	}

	private HBox createHBox() {
		HBox box = new HBox();
		box.setSpacing(2);
		box.setAlignment(Pos.TOP_LEFT);
		return box;
	}
}
