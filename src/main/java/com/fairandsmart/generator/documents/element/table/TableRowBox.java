package com.fairandsmart.generator.documents.element.table;

import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.VAlign;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TableRowBox extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());

    private final List<ElementBox> elements;
    private final float[] config;
    private final BoundingBox box;
    private final VAlign valign;
    private float borderThickness;
    private Color borderColor;
    private Color backgroundColor;

    public TableRowBox(float[] config, float posX, float posY) {
        this(config, posX, posY, VAlign.TOP);
    }

    public TableRowBox(float[] config, float posX, float posY, VAlign valign) {
        this.config = config;
        this.elements = new ArrayList<>();
        float width = 0;
        for (float row : config) {
            width += row;
        }
        box = new BoundingBox(posX, posY, width, 0);
        this.valign = valign;
    }

    public void addElement(ElementBox element) throws Exception {
            addElement(element, false);
        }

    public void addElement(ElementBox element, Boolean center_align) throws Exception {
        if ( elements.size() == config.length ) {
            throw new Exception("Row is full, no more element allowed");
        }
        elements.add(element);
        element.setWidth(config[elements.size() - 1]);

        //TODO : Add Center Right & Left Horizontal alignment

        if( !( element.getBBox().getPosY()==0 && element.getBBox().getPosX()!=0 ))
        {   // Translate only if x is not 0 and y is 0 which is in case of image center alignment
            element.getBBox().setPosX(0);
            element.getBBox().setPosY(0);
        }

        element.translate(box.getPosX() + this.getColumnOffsetX(elements.size()-1), box.getPosY() );

        if ( center_align && elements.size()>1 ) {
            element.translate(element.getBBox().getWidth()/3 , box.getPosY() );
        }
        if ( element.getBBox().getHeight() > box.getHeight() ) {
            this.getBBox().setHeight(element.getBBox().getHeight());
        }

        this.realignElements();
    }

    private void realignElements() {
        int cpt = 0;
        for ( ElementBox element : elements ) {
            float posY = box.getPosY();
            switch ( valign ) {
                case BOTTOM:
                    posY = box.getPosY() - box.getHeight() + element.getBBox().getHeight(); break;
                case CENTER:
                    posY = box.getPosY() - box.getHeight() + (element.getBBox().getHeight()/2); break;
            }
            float transY = posY - element.getBBox().getPosY();
            element.translate(0, transY);
            cpt++;
        }
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
        throw new Exception("Not allowed");
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        for (ElementBox element : elements) {
            element.translate(offsetX, offsetY);
        }
        this.getBBox().translate(offsetX, offsetY);

    }

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

        for(ElementBox element : this.elements) {
            //if(element.getBBox().getHeight() < this.getBBox().getHeight()) {
            //    element.translate(0,  this.getBBox().getHeight() - element.getBBox().getHeight());
            //}
            element.build(stream, writer);
        }
    }

    private float getColumnOffsetX (int numCol) {
        //TODO maybe the offset X could be store in a float[] like config...
        float posX = 0;
        for (int i=0; i<numCol; i++) {
            posX += config[i];
        }
        return posX;
    }

}
