package com.ssdgen.generator.documents.element.head;

import com.ssdgen.generator.documents.data.model.InvoiceAnnotModel;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.data.model.Client;
import com.ssdgen.generator.documents.data.model.Address;
import com.ssdgen.generator.documents.data.model.IDNumbers;
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


public class BillingInfoBox extends ElementBox {

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

    public BillingInfoBox(PDFont fontN, PDFont fontB, PDFont fontI,
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
        vContainer = new VerticalContainer(0,0,width);

        Client client = model.getClient();
        Address address = client.getBillingAddress();
        IDNumbers idNumber = client.getIdNumbers();
        ContactNumber contact = client.getBillingContactNumber();
        String clientName = client.getBillingName();
        String clientAddr = address.getLine1()+" "+address.getZip()+" "+address.getCity();
        String clientZip = address.getZip();

        vContainer.addElement(new SimpleTextBox(fontB,fontSizeBig,0,0, client.getBillingHead(), "BH" ));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, clientName, "BN" ));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getLine1(), "BA" ));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getZip()+" "+address.getCity(), "BA" ));
        if (proba.get("bill_address_phone")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getPhoneLabel()+": "+contact.getPhoneValue(), "BC"));
        }
        else if (proba.get("bill_address_country")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, address.getCountry(),"BA"));
            clientAddr += " " + address.getCountry();
        }

        if (proba.get("bill_address_tax_number")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, idNumber.getVatLabel()+": "+idNumber.getVatValue(),"BT"));
            annot.getBillto().setCustomerTrn(idNumber.getVatValue());
        }
        else if (proba.get("bill_address_fax")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getFaxLabel()+": "+contact.getFaxValue(), "BF"));
        }

        if (proba.get("addresses_bordered") && client.getBillingHead().length() > 0) {
            vContainer.setBorderColor(lineStrokeColor);
            vContainer.setBorderThickness(0.5f);
        }
        annot.getBillto().setCustomerName(clientName);
        annot.getBillto().setCustomerAddr(clientAddr);
        annot.getBillto().setCustomerPOBox(clientZip);
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
