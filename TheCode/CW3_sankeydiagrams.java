package com.example.sankeyplot;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class CW3_2251668_sankeydiagrams extends Application {
    private SankeyPlot sankeyPlot;
    private double recRightUp = 0.15;

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        // Set drag and drop event listener
        root.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        ArrayList<String> list = new ArrayList<>();
        // Handle drag and drop events
        root.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            list.clear();
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            list.add(line);
                        }
                        reader.close();
                        if (!checkFileValidity(list)) {
                            showAlert("Invalid file, Please drag and drop a standard text file.");
                            event.consume();
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Create and display the next scene
            }
            openNextScene(primaryStage, list);
            event.setDropCompleted(success);
            event.consume();
        });
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sankey Plot");
        Label label = new Label("Please drag and drop a standard text here.");
        root.getChildren().add(label);
        primaryStage.show();

    }

    private void openNextScene(Stage primaryStage, ArrayList<String> list) {
        VBox root = new VBox();
        // Set drag and drop event listener
        root.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        ArrayList<String> list1 = new ArrayList<>();
        // Handle drag and drop events
        root.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            list1.clear();
            if (db.hasFiles()) {
                success = true;

                for (File file : db.getFiles()) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            list1.add(line);
                        }
                        reader.close();
                        if (!checkFileValidity(list1)) {
                            showAlert("Invalid file, Please drag and drop a standard text file.");
                            event.consume();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Reset the recRightUp
                recRightUp = 0.15;
                // Create and display the next scene
                openNextScene(primaryStage, list1);
            }
            event.setDropCompleted(success);
            event.consume();
        });
        Rectangle rectangle1 = new Rectangle();
        Pane pane = new Pane(rectangle1);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pane, root);
        Scene scene = new Scene(stackPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        sankeyPlot = new SankeyPlot(scene, pane, rectangle1);
        sankeyPlot.ReadData(list);
        sankeyPlot.plotSankey();

    }


    public static void main(String[] args) {
        launch(args);
    }

    public boolean checkFileValidity(List<String> list) {
        boolean check = true;
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    // Check the first line
                    break;
                case 1:
                    // Check the second line
                    break;
                default:
                    // Check the rest of the lines
                    String[] split = list.get(i).split(" ");
                    Long test = 0L;
                    try {
                        // Check if the last element is a number
                        test = Long.parseLong(split[split.length - 1]);
                    } catch (Exception e) {
                        return false;
                    }
                    // Check if the number is negative
                    if (test <= 0L) {
                        return false;
                    }
                    // Check if the key is duplicated
                    if (split.length == 1) {
                        return false;

                    } else if (split.length == 2) {
                        try {// Check if the key is duplicated
                            var value = map.put(split[0], Long.parseLong(split[1]));
                            if (value != null) {
                                return false;

                            }
                            break;
                        } catch (Exception e) {
                            return false;

                        }
                    }
                    // Check if the key is duplicated
                    String key = "";
                    for (int i1 = 0; i1 < split.length - 1; i1++) {
                        key += " " + split[i1];
                    }
                    try {
                        map.put(key, Long.parseLong(split[split.length - 1]));
                        break;
                    } catch (Exception e) {
                        return false;

                    }
            }
            System.out.println(list.get(i));
        }
        return check;
    }

    public void showAlert(String headerText) {
        // Create the alert
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(headerText);
        alert.setContentText("Click OK to exit.");
        // Add a custom icon.
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);

        alert.setOnCloseRequest(event -> {
            if (alert.getResult() != null && alert.getResult().equals(okButton)) {
                alert.close();
            }
        });

        alert.show();
    }


}
