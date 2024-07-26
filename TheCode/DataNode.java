package com.example.sankeyplot.model;

import javafx.scene.paint.Color;

public class DataNode {

        private String name;
        private Long value;

        private double size;

        private Color color;


        public DataNode() {
        }

        public DataNode(String name, Long value, double size, Color color) {
            this.name = name;
            this.value = value;
            this.size = size;
            this.color = color;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }


        public double getSize() {
            return size;
        }


        public void setSize(double size) {
            this.size = size;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String toString() {
            return "Node{name = " + name + ", value = " + value + ", size = " + size + ", color = " + color + "}";
        }
    }
