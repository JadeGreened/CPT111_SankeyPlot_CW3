package com.example.sankeyplot.PlotItem;

import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public interface SankeyPlotItem {
    void plotRectangles(Scene scene, ArrayList<Rectangle> rectangleList, double recRightUp, double rectangleX);

    void plotCurve(double leftUpY1, double rightUpY2, double rightDownY2, double leftDonwY1, double rectangleX);

    void plotText(ArrayList<Text> textList);

    void plotAnimation();


}
