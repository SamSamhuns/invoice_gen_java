package com.ssdgen.generator.documents.element.line;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.Color;
import javax.xml.stream.XMLStreamWriter;

public class VerticalLineBox extends ElementBox {

    private final float targetX;
    private final float targetY;
    private final float lineWidth;
    private final BoundingBox box;
    private Color strokeColor;

    public VerticalLineBox(float posX, float posY, float targetX, float targetY) {
        this(posX, posY, targetX, targetY, 1f, Color.BLACK);
    }

    public VerticalLineBox(float posX, float posY, float targetX, float targetY, Color strokeColor) {
        this(posX, posY, targetX, targetY, 1f, strokeColor);
    }

    public VerticalLineBox(float posX, float posY, float targetX, float targetY, float lineWidth) {
        this(posX, posY, targetX, targetY, lineWidth, Color.BLACK);
    }

    public VerticalLineBox(float posX, float posY, float targetX, float targetY, float lineWidth, Color strokeColor) {
        this.lineWidth = lineWidth;
        this.targetX = targetX;
        this.targetY = targetY;
        this.strokeColor = strokeColor;
        this.box = new BoundingBox(posX, posY, 0, 0);
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        this.getBBox().translate(offsetX, offsetY);
    }

    @Override
    public void setWidth(float width) throws Exception {
        this.getBBox().setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        this.getBBox().setHeight(height);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        stream.setLineWidth(lineWidth);
        stream.moveTo(this.getBBox().getPosX(), this.getBBox().getPosY());
        stream.lineTo(this.getBBox().getPosX(), targetY);
        stream.closePath();
        stream.setStrokingColor(strokeColor);
        stream.stroke();
    }

}
