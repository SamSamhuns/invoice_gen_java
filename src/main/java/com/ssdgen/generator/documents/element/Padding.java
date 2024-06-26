package com.ssdgen.generator.documents.element;

public class Padding {

    private float left;
    private float top;
    private float right;
    private float bottom;

    public Padding() {
    }

    public Padding(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getHorizontalPadding() {
        return left + right;
    }

    public float getVerticalPadding() {
        return top + bottom;
    }
}
