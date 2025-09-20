package com.ssdgen.generator.documents.element.head;

import com.ssdgen.generator.documents.data.model.PayslipModel;
import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.container.HorizontalContainer;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;

public class EmployeeInfoBox extends ElementBox {
    private PDFont font;
    private PDFont fontB;
    private float fontSize;
    private PayslipModel model;
    private VerticalContainer container;
    private HorizontalContainer hcontainer;
    private PDDocument document;

    public EmployeeInfoBox(HorizontalContainer hcontainer) {
        this.hcontainer = hcontainer;
    }

    public EmployeeInfoBox(VerticalContainer container) {
        this.container = container;
    }

    public EmployeeInfoBox(PDFont font, PDFont fontB, float fontSize, PayslipModel model, PDDocument document)
            throws Exception {
        this.font = font;
        this.fontB = fontB;
        this.fontSize = fontSize;
        this.model = model;
        this.document = document;
        this.container = new VerticalContainer(0, 0, 0);
        this.hcontainer = new HorizontalContainer(0, 0);

        this.init();
    }

    private void init() throws Exception {
        container.addElement(concatContainersVertically(new ElementBox[] {
                getEmployeeAddressBlock() }));
    }

    public VerticalContainer getEmployeeAddressBlock() throws Exception {
        VerticalContainer addContainer = new VerticalContainer(0, 0, 0);
        SimpleTextBox name = new SimpleTextBox(fontB, fontSize, 0, 0, model.getEmployee().getName(), "EN");
        // name.setEntityName("SN");
        addContainer.addElement(name);
        SimpleTextBox adresse1 = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployee().getAddress().getLine1(),
                "EA");
        // adresse1.setEntityName("SA");
        addContainer.addElement(adresse1);

        if (model.getEmployee().getAddress().getLine2() != null
                && model.getEmployee().getAddress().getLine2().length() > 0) {
            SimpleTextBox adresse2 = new SimpleTextBox(font, fontSize, 0, 0,
                    model.getEmployee().getAddress().getLine2(), "EA");
            // adresse2.setEntityName("SA");
            addContainer.addElement(adresse2);
        }

        if (model.getEmployee().getAddress().getLine3() != null
                && model.getEmployee().getAddress().getLine3().length() > 0) {
            SimpleTextBox adresse3 = new SimpleTextBox(font, fontSize, 0, 0,
                    model.getEmployee().getAddress().getLine3(), "EA");
            // adresse3.setEntityName("SA");
            addContainer.addElement(adresse3);
        }

        HorizontalContainer cityContainer = new HorizontalContainer(0, 0);
        SimpleTextBox zip = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployee().getAddress().getZip(), "EA");
        zip.setPadding(0, 0, 5, 0);
        // zip.setEntityName("SA");
        cityContainer.addElement(zip);
        SimpleTextBox city = new SimpleTextBox(font, fontSize, 0, 0, model.getEmployee().getAddress().getCity(), "EA");
        // city.setEntityName("SA");
        cityContainer.addElement(city);
        addContainer.addElement(cityContainer);

        if (model.getEmployee().getAddress().getCountry() != null
                && model.getEmployee().getAddress().getCountry().length() > 0) {
            SimpleTextBox country = new SimpleTextBox(font, fontSize, 0, 0,
                    model.getEmployee().getAddress().getCountry(), "EA");
            // country.setEntityName("SA");
            addContainer.addElement(country);
        }
        return addContainer;
    }

    public VerticalContainer concatContainersVertically(ElementBox[] parts) throws Exception {
        int x = 1;
        VerticalContainer result = new VerticalContainer(0, 0, 0);
        for (ElementBox part : parts) {
            result.addElement(part);
        }
        return result;
    }

    @Override
    public BoundingBox getBBox() {
        if (container != null)
            return container.getBBox();
        return hcontainer.getBBox();
    }

    @Override
    public void setWidth(float width) throws Exception {
        if (container == null)
            hcontainer.getBBox().setWidth(width);
        else
            container.getBBox().setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        if (container == null)
            hcontainer.translate(offsetX, offsetY);
        else
            container.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if (container == null)
            hcontainer.build(stream, writer);
        else
            container.build(stream, writer);
    }
}
