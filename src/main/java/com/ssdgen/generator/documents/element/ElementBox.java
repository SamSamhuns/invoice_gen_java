package com.ssdgen.generator.documents.element;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class ElementBox {

    private static int cpt = 0;
    private static final float srcDpi = 72;
    private static final float destDpi = 300;
    private static final float maxX = 595;
    private static final float maxY = 842;


    public int nextElementId() {
        cpt++;
        return cpt;
    }

    protected BoundingBox convertBox(BoundingBox box) {
        float scale = destDpi / srcDpi;
        BoundingBox newbox = new BoundingBox(
                box.getPosX() * scale,
                (maxY - box.getPosY() - box.getHeight()) * scale,
                box.getWidth() * scale,
                box.getHeight() * scale);
        return newbox;
    }

    public String writeXMLZone(XMLStreamWriter writer, String type, String content, BoundingBox box, String cclass) throws XMLStreamException {

        BoundingBox tbox = convertBox(box);
        String id = "" + nextElementId();
        writer.writeStartElement("DL_ZONE");
        writer.writeAttribute("gedi_type", type);
        writer.writeAttribute("id", id);
        writer.writeAttribute("col", "" + (int) tbox.getPosX());
        writer.writeAttribute("row", "" + (int) tbox.getPosY());
        writer.writeAttribute("width", "" + (int) tbox.getWidth());
        writer.writeAttribute("height", "" + (int) tbox.getHeight());
        writer.writeAttribute("contents", content);
        writer.writeAttribute("correctclass", cclass);
        writer.writeEndElement();
        writer.writeCharacters(System.getProperty("line.separator"));
        return id;

    }

    public abstract BoundingBox getBBox();

    public abstract void setWidth(float width) throws Exception;

    public abstract void setHeight(float height) throws Exception;

    public abstract void translate(float offsetX, float offsetY);

    public abstract void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception;

}
