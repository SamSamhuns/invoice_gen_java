package com.ssdgen.generator.documents.element.border;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;

public class BorderBox extends ElementBox {

    private final Color borderColor;
    private final Color color;
    private final int thick;
    private final BoundingBox box;

    /*
     * (posX, poxY) represent bottom left coord of box
     */
    public BorderBox(Color borderColor, Color color, int thick, float posX, float posY, float width, float height) {
        this.borderColor = borderColor;
        this.color = color;
        this.thick = thick;
        this.box = new BoundingBox(posX, posY, width, height);
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
        stream.setNonStrokingColor(borderColor);
        stream.addRect(box.getPosX(), box.getPosY(), box.getWidth(), box.getHeight());
        stream.fill();
        stream.setNonStrokingColor(color);
        stream.addRect(box.getPosX() + thick, box.getPosY() + thick, box.getWidth() - (thick * 2),
                box.getHeight() - (thick * 2));
        stream.fill();
    }

}
