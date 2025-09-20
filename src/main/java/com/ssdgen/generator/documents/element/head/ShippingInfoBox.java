package com.ssdgen.generator.documents.element.head;

import com.ssdgen.generator.documents.data.model.InvoiceAnnotModel;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.data.model.Client;
import com.ssdgen.generator.documents.data.model.Address;
import com.ssdgen.generator.documents.data.model.ContactNumber;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.element.container.VerticalContainer;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Color;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;

public class ShippingInfoBox extends ElementBox {

    private final PDFont fontN;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSizeSmall;
    private final float fontSizeBig;
    private final float width;
    private final Color lineStrokeColor;

    private final InvoiceModel model;
    private final InvoiceAnnotModel annot;
    private final Map<String, Boolean> proba;

    private VerticalContainer vContainer;

    public void setBorderThickness(float thick) {
        vContainer.setBorderThickness(thick);
    }

    public void setBorderColor(Color color) {
        vContainer.setBorderColor(color);
    }

    public void setBackgroundColor(Color color) {
        vContainer.setBackgroundColor(color);
    }

    public ShippingInfoBox(PDFont fontN, PDFont fontB, PDFont fontI,
            float fontSizeSmall, float fontSizeBig,
            float width, Color lineStrokeColor,
            InvoiceModel model, InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.width = width;
        this.lineStrokeColor = lineStrokeColor;

        this.model = model;
        this.annot = annot;
        this.proba = proba;
        this.init();
    }

    private void init() throws Exception {
        vContainer = new VerticalContainer(0, 0, width);

        Client client = model.getClient();
        Address address = client.getShippingAddress();
        ContactNumber contact = client.getShippingContactNumber();
        String connec = (contact.getPhoneLabel().length() > 0) ? ": " : "";
        String clientName = client.getShippingName();
        String clientAddr = address.getLine1() + " " + address.getZip() + " " + address.getCity();
        String clientZip = address.getZip();

        vContainer.addElement(new SimpleTextBox(fontB, fontSizeBig, 0, 0, client.getShippingHead(), "SHH"));
        vContainer.addElement(new SimpleTextBox(fontN, fontSizeBig, 0, 0, clientName, "SHN"));
        vContainer.addElement(new SimpleTextBox(fontN, fontSizeBig, 0, 0, address.getLine1(), "SHA"));
        vContainer.addElement(
                new SimpleTextBox(fontN, fontSizeBig, 0, 0, address.getZip() + " " + address.getCity(), "SHA"));
        if (proba.get("ship_address_phone") && proba.get("bill_address_phone")) {
            vContainer.addElement(new SimpleTextBox(fontN, fontSizeSmall, 0, 0,
                    contact.getPhoneLabel() + connec + contact.getPhoneValue(), "SHC"));
        } else if (proba.get("ship_address_country")) {
            vContainer.addElement(new SimpleTextBox(fontN, fontSizeSmall, 0, 0, address.getCountry(), "SA"));
            clientAddr += " " + address.getCountry();
        }

        if (proba.get("ship_address_fax") && proba.get("bill_address_fax")) {
            vContainer.addElement(new SimpleTextBox(fontN, fontSizeSmall, 0, 0,
                    contact.getFaxLabel() + connec + contact.getFaxValue(), "SHF"));
        }

        if (proba.get("addresses_bordered") && client.getShippingHead().length() > 0) {
            vContainer.setBorderColor(lineStrokeColor);
            vContainer.setBorderThickness(0.5f);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            annot.setShipto(new InvoiceAnnotModel.Shipto());
            annot.getShipto().setShiptoName(clientName);
            if (contact.getPhoneLabel().length() > 0) {
                annot.getShipto().setShiptoAddr(clientAddr);
                annot.getShipto().setShiptoPOBox(clientZip);
            }
        }
    }

    @Override
    public BoundingBox getBBox() {
        return vContainer.getBBox();
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
        vContainer.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        vContainer.build(stream, writer);
    }
}
