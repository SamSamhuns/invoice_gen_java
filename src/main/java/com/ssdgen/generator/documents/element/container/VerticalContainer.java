package com.ssdgen.generator.documents.element.container;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.HAlign;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VerticalContainer extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());

    private final List<ElementBox> elements;
    private final BoundingBox box;
    private final float maxWidth;
    private float borderThickness;
    private Color borderColor;
    private Color backgroundColor;

    public VerticalContainer(float posX, float posY, float maxWidth) {
        this(posX, posY, maxWidth, 1, null, null);
    }

    public VerticalContainer(float posX, float posY, float maxWidth, float borderThickness, Color borderColor, Color backgroundColor) {
        this.elements = new ArrayList<>();
        this.maxWidth = maxWidth;
        this.box = new BoundingBox(posX, posY, 0, 0);
        this.borderThickness = borderThickness;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    public void addElement(ElementBox element) throws Exception {
        this.elements.add(element);
        if ( maxWidth > 0 && element.getBBox().getWidth() > maxWidth ) {
            element.setWidth(maxWidth);
        }
        if ( element.getBBox().getWidth() > this.getBBox().getWidth() ) {
            this.getBBox().setWidth(element.getBBox().getWidth());
        }
        element.getBBox().setPosX(0);
        element.getBBox().setPosY(0);
        element.translate(box.getPosX(), box.getPosY() - this.box.getHeight());
        this.box.setHeight(this.box.getHeight() + element.getBBox().getHeight());
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

    public void alignElements(HAlign align) {
        alignElements(align, maxWidth);
    }

    public void alignElements(HAlign align, float width) {
        // default alignment is LEFT
        for ( ElementBox element : elements ) {
            float posX = box.getPosX();
            switch ( align ) {
                case CENTER:
                    posX = (width - box.getPosX() - element.getBBox().getWidth())/2; break;
                case RIGHT:
                    posX = (width - box.getPosX()) - element.getBBox().getWidth(); break;
            }
            float transX = posX - element.getBBox().getPosX();
            element.translate(transX, 0);
        }
    }

    @Override
    public String toString() {
        return "VerticalContainer{" +
                "Num elements=" + elements.size() +
                ", (X, Y)=(" + box.getPosX() + ", " + box.getPosY()  + ")" +
                ", (W, H)=(" + box.getWidth() + ", " + box.getHeight()  + ")" +
                ", borderThickness=" + borderThickness +
                ", borderColor=" + borderColor +
                ", backgroundColor=" + backgroundColor +
                '}';
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void setWidth(float width) throws Exception {
        this.setHeight(0);
        this.box.setWidth(width);
        for ( ElementBox element : elements ) {
            element.setWidth(width);
            element.getBBox().setPosX(0);
            element.getBBox().setPosY(0);
            float offsetY = this.box.getHeight();
            element.translate(box.getPosX(), box.getPosY() - offsetY);
            this.box.setHeight(this.box.getHeight() + element.getBBox().getHeight());
        }
    }

    @Override
    public void setHeight(float height) throws Exception {
        this.box.setHeight(height);
        //throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        box.translate(offsetX, offsetY);
        for ( ElementBox element : elements ) {
            element.translate(offsetX, offsetY);
        }
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if ( borderColor != null ) {
            stream.setLineWidth(borderThickness);
            stream.setStrokingColor(borderColor);
            borderThickness += 1f;  // add an expansion factor
            stream.addRect(box.getPosX()-borderThickness, box.getPosY() - box.getHeight() - borderThickness, box.getWidth() + borderThickness*2, box.getHeight() + borderThickness*2);
            stream.stroke();
        }
        if ( backgroundColor != null ) {
            stream.setNonStrokingColor(backgroundColor);
            stream.addRect(box.getPosX(), box.getPosY() - box.getHeight(), box.getWidth(), box.getHeight());
            stream.fill();
        }

        for ( ElementBox element : elements ) {
            element.build(stream, writer);
        }
    }

}
