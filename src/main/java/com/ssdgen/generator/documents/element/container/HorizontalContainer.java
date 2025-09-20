package com.ssdgen.generator.documents.element.container;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HorizontalContainer extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());

    private final List<ElementBox> elements;
    private final BoundingBox box;
    private float borderThickness;
    private Color borderColor;
    private Color backgroundColor;

    public HorizontalContainer(float posX, float posY) {
        this(posX, posY, 1, null, null);
    }

    public HorizontalContainer(float posX, float posY, float borderThickness, Color borderColor,
            Color backgroundColor) {
        this.elements = new ArrayList<>();
        this.box = new BoundingBox(posX, posY, 0, 0);
        this.borderThickness = borderThickness;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    public void addElement(ElementBox element) throws Exception {
        this.elements.add(element);
        element.getBBox().setPosX(0);
        element.getBBox().setPosY(0);
        element.translate(box.getPosX() + this.box.getWidth(), box.getPosY());
        if (element.getBBox().getHeight() > box.getHeight()) {
            this.box.setHeight(element.getBBox().getHeight());
        }
        this.box.setWidth(this.box.getWidth() + element.getBBox().getWidth());
    }

    public void setBorderThickness(float thick) {
        this.borderThickness = thick;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void setWidth(float width) throws Exception {
        this.box.setWidth(width);
        // throw new Exception("Not allowed");
    }

    @Override
    public void setHeight(float height) throws Exception {
        this.box.setHeight(height);
        // throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        box.translate(offsetX, offsetY);
        for (ElementBox element : elements) {
            element.translate(offsetX, offsetY);
        }
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if (borderColor != null) {
            stream.setLineWidth(borderThickness);
            stream.setStrokingColor(borderColor);
            stream.addRect(box.getPosX() - borderThickness, box.getPosY() - box.getHeight() - borderThickness,
                    box.getWidth() + borderThickness * 2, box.getHeight() + borderThickness * 2);
            stream.stroke();
        }
        if (backgroundColor != null) {
            stream.setNonStrokingColor(backgroundColor);
            stream.addRect(box.getPosX(), box.getPosY() - box.getHeight(), box.getWidth(), box.getHeight());
            stream.fill();
        }

        for (ElementBox element : elements) {
            element.build(stream, writer);
        }
    }
}
