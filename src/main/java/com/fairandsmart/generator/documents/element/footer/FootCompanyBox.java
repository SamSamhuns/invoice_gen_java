package com.fairandsmart.generator.documents.element.footer;

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

import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.ContactNumber;

import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.Map;
import java.util.Random;


public class FootCompanyBox extends ElementBox {

    private final PDFont fontN;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSizeSmall;
    private final float fontSizeBig;
    private final Color fontColor;
    private final float width;

    private final InvoiceModel model;
    private final InvoiceAnnotModel annot;
    private final Map<String, Boolean> proba;

    private VerticalContainer vContainer;
    private final Random rnd = new Random();

    public FootCompanyBox(PDFont fontN, PDFont fontB, PDFont fontI,
                          float fontSizeSmall, float fontSizeBig, Color fontColor, float width,
                          InvoiceModel model, InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.fontColor = fontColor;
        this.width = width;

        this.model = model;
        this.annot = annot;
        this.proba = proba;
        this.init();
    }

    private void init() throws Exception {
        PDFont fontNB = rnd.nextBoolean() ? fontN: fontB;
        vContainer = new VerticalContainer(0,0,width);

        // footer company name, info, address & contact information
        Company company = model.getCompany();
        Address address = company.getAddress();
        IDNumbers idNumber = company.getIdNumbers();
        ContactNumber contact = company.getContact();

        HorizontalContainer line1 = new HorizontalContainer(0,0);
        line1.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, company.getName(),"SN"));
        line1.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, " - "));
        line1.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, address.getCountry(),"SA"));

        HorizontalContainer line2 = new HorizontalContainer(0,0);
        line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, address.getLine1()+" ","SA"));
        line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, " - "));
        line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, address.getZip() + " " +address.getCity(),"SA"));
        if (model.getLang().equals("fr")) {  // specific to FR invoices
            line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, " "+idNumber.getSiretLabel()+" "));
            line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, idNumber.getSiretValue(),"SSIRET"));
        }
        if (proba.get("vendor_tax_number_bottom")) {
            line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, " - "+idNumber.getVatLabel() +" : "));
            line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, idNumber.getVatValue(),"SVAT"));
            annot.getVendor().setVendorTrn(idNumber.getVatValue());
        }

        line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, " - "+contact.getFaxLabel()+" : "));
        line2.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getFaxValue(),"SFAX"));

        HorizontalContainer line3 = new HorizontalContainer(0,0);
        line3.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getPhoneLabel()+" : "));
        line3.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, contact.getPhoneValue(),"SCN"));

        annot.getVendor().setVendorName(company.getName());
        annot.getVendor().setVendorAddr(address.getLine1()+" "+address.getZip()+" "+address.getCity()+" "+address.getCountry());
        annot.getVendor().setVendorPOBox(address.getZip());

        vContainer.addElement(line1);
        vContainer.addElement(line2);
        vContainer.addElement(line3);
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

    public void alignElements(HAlign align) {
        vContainer.alignElements(align, this.width);
    }

    public void alignElements(HAlign align, float width) {
        vContainer.alignElements(align, width);
    }
}
