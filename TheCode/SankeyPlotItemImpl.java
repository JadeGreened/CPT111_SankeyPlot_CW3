package com.example.sankeyplot.PlotItem.Impl;

import com.example.sankeyplot.PlotItem.SankeyPlotItem;
import com.example.sankeyplot.model.DataNode;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class SankeyPlotItemImpl implements SankeyPlotItem {
    private DataNode node;
    private Rectangle rectangle;
    private Scene scene;
    private Pane pane;
    private Rectangle rectangle1;
    private Path filledPath;

    public SankeyPlotItemImpl(DataNode node, Rectangle rectangle, Scene scene, Pane pane, Rectangle rectangle1, Path filledPath) {
        this.node = node;
        this.rectangle = rectangle;
        this.scene = scene;
        this.pane = pane;
        this.rectangle1 = rectangle1;
        this.filledPath = filledPath;
    }

    public void plotRectangles(Scene scene, ArrayList<Rectangle> rectangleList, double recRightUp, double rectangleX) {

        //The width of the rectangle
        double rectangleWidth = 1.0 / 30;
        //Set Binding
        rectangle.widthProperty().bind(Bindings.multiply(scene.widthProperty(), rectangleWidth));
        rectangle.heightProperty().bind(Bindings.multiply(rectangle1.heightProperty(), node.getSize()));
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setFill(Color.TRANSPARENT);
        //The x-axis of the rectangle
        rectangle.xProperty().bind(Bindings.multiply(scene.widthProperty(), rectangleX));
        rectangle.yProperty().bind(Bindings.multiply(scene.heightProperty(), recRightUp));
        //Set the color of the rectangle and set the animation
        Color startColor = Color.TRANSPARENT;
        Color endColor = node.getColor();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rectangle.fillProperty(), startColor)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(rectangle.fillProperty(), endColor))
        );
        timeline.setCycleCount(1);
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5)); // Delay 2 scoends

        // Disable the timeline when the delay finishes
        delay.setOnFinished(event -> timeline.play());

        //Activate the delay
        delay.play();


        //The move ratio of the rectangle

        rectangleList.add(rectangle);

    }
    public void plotCurve( double leftUpY1, double rightUpY2, double rightDownY2, double leftDonwY1, double rectangleX) {
        CubicCurveTo cubicCurveTo1 = new CubicCurveTo();
        double controlX1 = 0.45;
        cubicCurveTo1.controlX1Property().bind(Bindings.multiply(scene.widthProperty(), controlX1));
        cubicCurveTo1.controlY1Property().bind(Bindings.multiply(scene.heightProperty(), leftUpY1));
        double controlX2 = 0.55;
        cubicCurveTo1.controlX2Property().bind(Bindings.multiply(scene.widthProperty(), controlX2));
        cubicCurveTo1.controlY2Property().bind(Bindings.multiply(scene.heightProperty(), rightUpY2));
        cubicCurveTo1.xProperty().bind(Bindings.multiply(scene.widthProperty(), rectangleX));
        cubicCurveTo1.yProperty().bind(Bindings.multiply(scene.heightProperty(), rightUpY2));
        CubicCurveTo cubicCurveTo2 = new CubicCurveTo();
        cubicCurveTo2.controlX1Property().bind(Bindings.multiply(scene.widthProperty(), controlX2));
        cubicCurveTo2.controlY1Property().bind(Bindings.multiply(scene.heightProperty(), rightDownY2));
        cubicCurveTo2.controlX2Property().bind(Bindings.multiply(scene.widthProperty(), controlX1));
        cubicCurveTo2.controlY2Property().bind(Bindings.multiply(scene.heightProperty(), leftDonwY1));
        cubicCurveTo2.xProperty().bind(Bindings.add(rectangle1.xProperty(), rectangle1.widthProperty()));
        cubicCurveTo2.yProperty().bind(Bindings.multiply(scene.heightProperty(), leftDonwY1));
        MoveTo moveTo = new MoveTo();
        moveTo.xProperty().bind(Bindings.add(rectangle1.xProperty(), rectangle1.widthProperty()));
        moveTo.yProperty().bind(Bindings.multiply(scene.heightProperty(), leftUpY1));
        filledPath.getElements().add(moveTo);
        filledPath.getElements().add(cubicCurveTo1);
        LineTo lineTo = new LineTo();
        lineTo.xProperty().bind(rectangle.xProperty());
        lineTo.yProperty().bind(Bindings.multiply(scene.heightProperty(), rightDownY2));
        filledPath.getElements().add(lineTo);
        filledPath.getElements().add(cubicCurveTo2);
        filledPath.getElements().add(new ClosePath());
        // Set the stroke color to transparent
        filledPath.setStroke(Color.TRANSPARENT);
        // Get the hue value of the certain color and change the transparency
        Color hsbCyan = Color.CYAN.deriveColor(0, 1, 1, 0.5);
        Color hsbNodeColor = node.getColor().deriveColor(0, 1, 1, 0.5);

        //crate a linear gradient
        LinearGradient linearGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, hsbCyan),
                new Stop(1, hsbNodeColor)
        );
        filledPath.setFill(linearGradient);
        pane.getChildren().add(filledPath);
    }
    public void plotText(ArrayList<Text> textList){
        Text text = new Text(node.getName() + ": " + node.getValue());
        //The text is placed near the rectangle
        text.yProperty().bind(Bindings.add(rectangle.yProperty(), rectangle.heightProperty().multiply(0.5)));
        text.xProperty().bind(Bindings.subtract(rectangle.xProperty(), text.getLayoutBounds().getWidth()));
        textList.add(text);
    }

    public void plotAnimation() {

        // Create a Timeline animation to change the clipRect's width from 0 to scene's width

        //set clip with the original width 0
        Rectangle clipRect = new Rectangle(0, 0, 0, scene.getHeight());
        filledPath.setClip(clipRect);

        // Create a Timeline animation to change the clipRect's width from 0 to scene's width
        double animationDuration = 2.0;
        Timeline clipTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(clipRect.widthProperty(), 0)),
                new KeyFrame(Duration.seconds(animationDuration), new KeyValue(clipRect.widthProperty(), scene.getWidth()))
        );
        //Listener to change the clipRect's width and height when the window's width and height change
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            clipRect.setWidth(newValue.doubleValue());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            clipRect.setHeight(newValue.doubleValue());
        });

        clipTimeline.play();

    }

}
