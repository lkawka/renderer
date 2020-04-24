package com.lukkaw.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukkaw.controller.Controller;
import com.lukkaw.drawable.Drawable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class AppToolbar {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Controller controller;
    private Stage stage;

    public ToolBar createUI() {
        ToolBar toolBar = new ToolBar();

        Button openButton = new Button("Open");
        Button saveButton = new Button("Save");
        Button clearButton = new Button("Clear");

        openButton.setOnAction(e -> controller.load(loadDrawables()));
        saveButton.setOnAction(e -> saveDrawables());
        clearButton.setOnAction(e -> controller.clear());

        toolBar.getItems().addAll(openButton, saveButton, clearButton);

        return toolBar;
    }

    private List<Drawable> loadDrawables() {
        FileChooser fileChooser = new FileChooser();
        ExtensionFilter extensionFilter = new ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Open shapes");

        File file = fileChooser.showOpenDialog(stage);

        try {
            return Arrays.asList(MAPPER.readValue(file, Drawable[].class));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file: " + file, e);
        }
    }

    private void saveDrawables() {
        List<Drawable> drawables = controller.getDrawables();
        String jsonString;
        try {
            jsonString = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(drawables);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Cannot parse content", ex);
        }

        FileChooser fileChooser = new FileChooser();
        ExtensionFilter extensionFilter = new ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Save shapes");

        File file = fileChooser.showSaveDialog(stage);

        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(jsonString);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to write to file: " + file, ex);
        }
    }
}
