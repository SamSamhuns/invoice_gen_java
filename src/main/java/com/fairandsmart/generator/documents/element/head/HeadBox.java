package com.fairandsmart.generator.documents.element.head;

import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;

public class HeadBox extends ElementBox {

    private final PDFont font;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSize;
    private final InvoiceModel model;
    private VerticalContainer container;
    private final PDDocument document;

    public HeadBox(PDFont font, PDFont fontB, PDFont fontI, float fontSize, InvoiceModel model, PDDocument document) throws Exception {
        this.font = font;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSize = fontSize;
        this.model = model;
        this.document = document;
        this.init();
    }

    private void init() throws Exception {
        container = new VerticalContainer(0,0, 0);
        HorizontalContainer top = new HorizontalContainer(0,0);
        CompanyInfoBox companyInfoBox = new CompanyInfoBox(font, fontB, 11, model, document);
        companyInfoBox.setWidth(250);
        CompanyInfoBox companyInfoBox2 = new CompanyInfoBox(font, fontB, 11, model, document);
        companyInfoBox2.setWidth(250);
        top.addElement(companyInfoBox);
        top.addElement(companyInfoBox2);
        container.addElement(top);
    }

    @Override
    public BoundingBox getBBox() {
        return container.getBBox();
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
        container.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        container.build(stream, writer);
    }
}
