package com.fairandsmart.generator.documents.element.head;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.ContactNumber;

import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.border.BorderBox;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Color;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.Random;


public class VendorInfoBox extends ElementBox {

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
    private final Random rnd = new Random();

    public void setBorderThickness(float thick) {
        vContainer.setBorderThickness(thick);
    }

    public void setBorderColor(Color color) {
        vContainer.setBorderColor(color);
    }

    public void setBackgroundColor(Color color) {
        vContainer.setBackgroundColor(color);
    }

    public VendorInfoBox(PDFont fontN, PDFont fontB, PDFont fontI,
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
        PDFont fontNB = rnd.nextBoolean() ? fontN: fontB;

        vContainer = new VerticalContainer(0,0,width);

        Company company = model.getCompany();
        Address address = company.getAddress();
        IDNumbers idNumber = company.getIdNumbers();
        ContactNumber contact = company.getContact();
        String compName = company.getName();
        String compAddr = address.getLine1()+" "+address.getZip()+" "+address.getCity();
        String compZip = address.getZip();

        vContainer.addElement(new SimpleTextBox(fontB,fontSizeBig+1,0,0, compName+"","SN"));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getLine1(),"SA"));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getZip() +"  "+ address.getCity(),"SA"));
        vContainer.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,3));
        if (proba.get("vendor_address_country")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0,address.getCountry(),"BA"));
            compAddr += " " + address.getCountry();
        }
        else {
            if (proba.get("vendor_address_phone")) {
                vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getPhoneLabel()+": "+contact.getPhoneValue(), "SC"));
            }
            if (proba.get("vendor_address_fax")) {
                vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getFaxLabel()+": "+contact.getFaxValue(), "SF"));
            }
        }
        if (proba.get("vendor_address_tax_number")) {
            String vatText = idNumber.getVatLabel()+": "+idNumber.getVatValue();
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, vatText, "SVAT"));
            annot.getVendor().setVendorTrn(idNumber.getVatValue());
        }

        if (proba.get("addresses_bordered")) {
            vContainer.setBorderColor(lineStrokeColor);
            vContainer.setBorderThickness(0.5f);
        }
        annot.getVendor().setVendorName(compName);
        annot.getVendor().setVendorAddr(compAddr);
        annot.getVendor().setVendorPOBox(compZip);
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
