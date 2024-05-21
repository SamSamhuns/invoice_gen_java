package com.ssdgen.generator.documents.element.container;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

public class LayeredContainer extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(LayeredContainer.class.getName());

    private final Map<Integer, ElementBox> elements;
    private final BoundingBox box;

    //TODO manage aligment and padding
    public LayeredContainer(float posX, float posY, float width, float height) {
        this.elements = new TreeMap<>();
        this.box = new BoundingBox(posX, posY, width, height);
    }

    public void addElement(int layer, ElementBox element) {
        this.elements.put(layer, element);
        element.getBBox().setPosX(box.getPosX());
        element.getBBox().setPosY(box.getPosY());
        if ( element.getBBox().getWidth() > box.getWidth() ) {
            box.setWidth(element.getBBox().getWidth());
            //TODO maybe resize all existing elements
        }
        if ( element.getBBox().getHeight() > box.getHeight() ) {
            box.setHeight(element.getBBox().getHeight());
            //TODO maybe resize all existing elements
        }
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void setWidth(float width) throws Exception {
        for ( ElementBox element : elements.values() ) {
            element.setWidth(width);
        }
        this.box.setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        for ( ElementBox element : elements.values() ) {
            element.setHeight(height);
        }
        this.box.setHeight(height);
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        box.translate(offsetX, offsetY);
        for ( ElementBox element : elements.values() ) {
            element.translate(offsetX, offsetY);
        }
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        for ( ElementBox element : elements.values() ) {
            element.build(stream, writer);
        }
    }

}
