package com.fairandsmart.generator.documents.element.head;

/*-
 * #%L
 * FacoGen / A tool for annotated GEDI based invoice generation.
 *
 * Authors:
 *
 * Xavier Lefevre <xavier.lefevre@fairandsmart.com> / FairAndSmart
 * Nicolas Rueff <nicolas.rueff@fairandsmart.com> / FairAndSmart
 * Alan Balbo <alan.balbo@fairandsmart.com> / FairAndSmart
 * Frederic Pierre <frederic.pierre@fairansmart.com> / FairAndSmart
 * Victor Guillaume <victor.guillaume@fairandsmart.com> / FairAndSmart
 * Jérôme Blanchard <jerome.blanchard@fairandsmart.com> / FairAndSmart
 * Aurore Hubert <aurore.hubert@fairandsmart.com> / FairAndSmart
 * Kevin Meszczynski <kevin.meszczynski@fairandsmart.com> / FairAndSmart
 * %%
 * Copyright (C) 2019 Fair And Smart
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.ContactNumber;

import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.border.BorderBox;

import org.apache.pdfbox.pdmodel.PDDocument;
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
    private final PDDocument document;
    private final Company company;
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
                         InvoiceModel model, PDDocument document, Company company,
                         InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.width = width;
        this.lineStrokeColor = lineStrokeColor;

        this.model = model;
        this.document = document;
        this.company = company;

        this.annot = annot;
        this.proba = proba;
        this.init();
    }

    private void init() throws Exception {
        PDFont fontNB = rnd.nextBoolean() ? fontN: fontB;

        vContainer = new VerticalContainer(0,0,width);

        Address address = company.getAddress();
        IDNumbers idNumber = company.getIdNumbers();
        ContactNumber contact = company.getContact();

        vContainer.addElement(new SimpleTextBox(fontNB,fontSizeBig,0,0, company.getName()+"","SN"));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getLine1(),"SA"));
        vContainer.addElement(new SimpleTextBox(fontN,fontSizeBig,0,0, address.getZip() +"  "+ address.getCity(),"SA"));
        vContainer.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,3));
        if (proba.get("vendor_address_phone")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getPhoneLabel()+": "+contact.getPhoneValue(), "SC"));
        }
        if (proba.get("vendor_address_fax")) {
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getFaxLabel()+": "+contact.getFaxValue(), "SF"));
        }
        if (proba.get("vendor_address_tax_number")) {
            String vatText = idNumber.getVatLabel()+": "+idNumber.getVatValue();
            vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, vatText, "SVAT"));
            annot.getVendor().setVendorTrn(idNumber.getVatValue());
        }
        // else if (proba.get("vendor_address_country")) {
        //     vContainer.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0,client.getBillingAddress().getCountry(),"BA"));
        //     clientAddr += " " + address.getCountry();
        // }

        if (proba.get("addresses_bordered")) {
            vContainer.setBorderColor(lineStrokeColor);
            vContainer.setBorderThickness(0.5f);
        }
        annot.getVendor().setVendorName(company.getName());
        annot.getVendor().setVendorAddr(address.getLine1()+" "+address.getZip()+" "+address.getCity());
        annot.getVendor().setVendorPOBox(address.getZip());
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
