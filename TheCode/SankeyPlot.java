package com.example.sankeyplot;

import com.example.sankeyplot.PlotItem.Impl.SankeyPlotItemImpl;
import com.example.sankeyplot.model.DataNode;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.util.*;
import java.util.stream.Collectors;

public class SankeyPlot {
    private List<DataNode> nodeList = new ArrayList<>();
    private Scene scene;
    private Pane pane;
    private Rectangle rectangle1;
    private String chartName = "";
    private String chartTotalName = "";
    private Long total;

    private Map<String, String> map = new HashMap<>();

    public SankeyPlot(List<DataNode> nodeList, Scene scene, Pane pane, Rectangle rectangle1, String chartName, String chartTotalName, Long total, Map<String, String> map) {
        this.nodeList = nodeList;
        this.scene = scene;
        this.pane = pane;
        this.rectangle1 = rectangle1;
        this.chartName = chartName;
        this.chartTotalName = chartTotalName;
        this.total = total;
        this.map = map;
    }

    public SankeyPlot(Scene scene, Pane pane, Rectangle rectangle1) {
        this.scene = scene;
        this.pane = pane;
        this.rectangle1 = rectangle1;
    }

    public void plotSankey() {
        //plot the whole rectangle
        plotRectangle1();
        double rectangleX = 18.0 / 20;
        ArrayList<Rectangle> rectangleList = new ArrayList<>();
        double recRightUp = 0.15;
        double rightUpY2 = 0.15;
        double leftUpY1 = 0.25;
        ArrayList<Text> textList = new ArrayList<>();

        //plot the rectangles and curves
        for (DataNode node : nodeList) {

            SankeyPlotItemImpl sankeyPlotItem = createSankeyPlotItem(node);
            sankeyPlotItem.plotRectangles(scene, rectangleList, recRightUp, rectangleX);
            recRightUp += (0.4 / nodeList.size()) * 0.5 + node.getSize() * 0.5;
            double rightDownY2 = rightUpY2 + (0.5 * node.getSize());
            double leftDonwY1 = leftUpY1 + node.getSize() * 0.5;
            sankeyPlotItem.plotCurve(leftUpY1, rightUpY2, rightDownY2, leftDonwY1, rectangleX);
            leftUpY1 += (node.getSize()) * 0.5;
            rightUpY2 += (0.4 / nodeList.size()) * 0.5 + node.getSize() * 0.5;
            sankeyPlotItem.plotText(textList);
            sankeyPlotItem.plotAnimation();
        }

        //plot the text
        plotTheText(textList);
        pane.getChildren().addAll(textList);
        pane.getChildren().addAll(rectangleList);


    }

    private void plotTheText(ArrayList<Text> textList) {
        Text text = new Text(chartTotalName + ": " + total);
        text.xProperty().bind(Bindings.add(rectangle1.xProperty(), rectangle1.widthProperty()));
        text.yProperty().bind(Bindings.add(rectangle1.yProperty(), rectangle1.heightProperty().multiply(0.5)));

        //Set the name of the chart
        Text text1 = new Text(chartName);
        text1.xProperty().bind(Bindings.subtract(scene.widthProperty(), text1.getLayoutBounds().getWidth()).divide(2));
        text1.yProperty().bind(Bindings.multiply(scene.heightProperty(), 19.0 / 20));
        textList.add(text);
        textList.add(text1);
    }

    private void plotRectangle1() {

        double rectangle1Width = 1.0 / 30;
        rectangle1.widthProperty().bind(Bindings.multiply(scene.widthProperty(), rectangle1Width));
        double rectangle1Height = 1.0 / 2;
        rectangle1.heightProperty().bind(Bindings.multiply(scene.heightProperty(), rectangle1Height));
        // Set the position of the rectangle
        //The x-axis of the left rectangle is 1/20 of the width of the scene
        double rectangle1X = 1.0 / 20;
        rectangle1.xProperty().bind(Bindings.multiply(scene.widthProperty(), rectangle1X));
        rectangle1.yProperty().bind(Bindings.subtract(
                Bindings.divide(scene.heightProperty(), 2),
                rectangle1.heightProperty().divide(2)
        ));
        rectangle1.setFill(Color.TRANSPARENT);
        Duration duration = Duration.seconds(0.5);
        Color startColor = Color.TRANSPARENT;
        Color endColor = Color.CYAN;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rectangle1.fillProperty(), startColor)),
                new KeyFrame(duration, new KeyValue(rectangle1.fillProperty(), endColor))
        );
        timeline.setCycleCount(1); // Only play once
        timeline.play();
    }

    private SankeyPlotItemImpl createSankeyPlotItem(DataNode node) {
        Rectangle rectangle = new Rectangle();
        Path filledPath = new Path();
        return new SankeyPlotItemImpl(node, rectangle, scene, pane, rectangle1, filledPath);

    }

    public void ReadData(ArrayList<String> list) {
        Map<String, Long> map = new HashMap<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    chartName = list.get(i);
                    break;
                case 1:
                    chartTotalName = list.get(i);
                    break;
                default:
                    String[] split = list.get(i).split(" ");
                    if (split.length == 1) {
                        break;
                    } else if (split.length == 2) {
                        map.put(split[0], Long.parseLong(split[1]));
                        break;
                    }
                    String key = "";
                    for (int i1 = 0; i1 < split.length - 1; i1++) {
                        key += " " + split[i1];
                    }
                        map.put(key, Long.parseLong(split[split.length - 1]));
                        break;

            }
            System.out.println(list.get(i));
            count++;
        }

            total = map.values().stream().mapToLong(Long::longValue).sum();

        Map<String, Long> sortedMap = sortByValue(map);
        Set<String> keys = sortedMap.keySet();
        int i = 0;
        double startHue = 0.0;
        double endHue = 360;
        double hueStep = (endHue - startHue) / keys.size();

        for (String key : keys) {
            Long amount = map.get(key);
            double saturation = 1.0;
            double brightness = 1.0;
            nodeList.add(new DataNode(key, amount, ((double) amount / total), Color.hsb(startHue + i * hueStep, saturation, brightness)));
            i++;
        }
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }


}
