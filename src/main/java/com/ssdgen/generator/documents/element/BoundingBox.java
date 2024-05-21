package com.ssdgen.generator.documents.element;

public class BoundingBox {

    private float posX;
    private float posY;
    private float width;
    private float height;

    public BoundingBox(float posX, float posY, float width, float height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void translate(float offsetX, float offsetY) {
        this.setPosX(posX + offsetX);
        this.setPosY(posY + offsetY);
    }

}
