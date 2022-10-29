package com.fairandsmart.generator.documents.element.payment;

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
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.head.CompanyInfoBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Color;
import java.util.Random;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;


public class PaymentInfoBox extends ElementBox {

    private final PDFont fontN;
    private final PDFont fontB;
    private final PDFont fontI;
    private final float fontSizeSmall;
    private final float fontSizeBig;
    private final float width;
    private final Color lineStrokeColor;

    private VerticalContainer vContainer;
    private final InvoiceModel model;
    private final PDDocument document;
    private final PaymentInfo payment;
    private final Company company;
    private final InvoiceAnnotModel annot;
    private final Map<String, Boolean> proba;

    private final Random rnd = new Random();

    public PaymentInfoBox(PDFont fontN, PDFont fontB, PDFont fontI,
                          float fontSizeSmall, float fontSizeBig, float width, Color lineStrokeColor,
                          InvoiceModel model, PDDocument document,
                          PaymentInfo payment, Company company,
                          InvoiceAnnotModel annot, Map<String, Boolean> proba) throws Exception {
        this.fontN = fontN;
        this.fontB = fontB;
        this.fontI = fontI;
        this.fontSizeSmall = fontSizeSmall;
        this.fontSizeBig = fontSizeBig;
        this.width = width;
        this.lineStrokeColor = lineStrokeColor;

        this.document = document;
        this.model = model;

        this.payment = payment;
        this.company = company;

        this.annot = annot;
        this.proba = proba;
        this.init();
    }

    private void init() throws Exception {
        PDFont fontNB = rnd.nextBoolean() ? fontN: fontB;
        annot.setPaymentto(new InvoiceAnnotModel.Paymentto());

        vContainer = new VerticalContainer(0,0,width);

        vContainer.addElement(new SimpleTextBox(fontB,fontSizeBig,0,0, payment.getAddressHeader(), "PH"));

        HorizontalContainer bankName = new HorizontalContainer(0,0);
        bankName.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelBankName()+": ", "PBN"));
        bankName.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueBankName(), "PBN"));
        vContainer.addElement(bankName);
        annot.getPaymentto().setBankName(payment.getValueBankName());

        HorizontalContainer accountName = new HorizontalContainer(0,0);
        accountName.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelAccountName()+": ", "PAName"));
        accountName.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueAccountName(), "PAName"));
        vContainer.addElement(accountName);
        annot.getPaymentto().setAccountName(payment.getValueAccountName());

        if (proba.get("payment_account_number")) {
            HorizontalContainer accountNumber = new HorizontalContainer(0,0);
            accountNumber.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelAccountNumber()+": ", "PANum"));
            accountNumber.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueAccountNumber(), "PANum"));
            vContainer.addElement(accountNumber);
            annot.getPaymentto().setAccountNumber(payment.getValueAccountNumber());
        }
        if (proba.get("payment_branch_name")) {
            HorizontalContainer branchName = new HorizontalContainer(0,0);
            branchName.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelBranchName()+": ", "PBName"));
            branchName.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueBranchName(), "PBName"));
            vContainer.addElement(branchName);
            annot.getPaymentto().setBranchAddress(payment.getValueBranchName());
        }

        HorizontalContainer ibanNumber = new HorizontalContainer(0,0);
        ibanNumber.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelIBANNumber()+": ", "PBNum"));
        ibanNumber.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueIBANNumber(), "PBNum"));
        vContainer.addElement(ibanNumber);
        annot.getPaymentto().setIbanNumber(payment.getValueIBANNumber());

        if (proba.get("payment_routing_number")) {
            HorizontalContainer routingNumber = new HorizontalContainer(0,0);
            routingNumber.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelRoutingNumber()+": ", "PBNum"));
            routingNumber.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueRoutingNumber(), "PBNum"));
            vContainer.addElement(routingNumber);
            annot.getPaymentto().setRoutingNumber(payment.getValueRoutingNumber());
        }
        if (proba.get("payment_swift_number")) {
            HorizontalContainer swiftCode = new HorizontalContainer(0,0);
            swiftCode.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, payment.getLabelSwiftCode()+": ", "PSNum"));
            swiftCode.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, payment.getValueSwiftCode(), "PSNum"));
            vContainer.addElement(swiftCode);
            annot.getPaymentto().setSwiftCode(payment.getValueSwiftCode());
        }
        if (proba.get("payment_vendor_tax_number") && !proba.get("vendor_tax_number_top")) {
            HorizontalContainer vatNumber = new HorizontalContainer(0,0);
            vatNumber.addElement(new SimpleTextBox(fontNB,fontSizeSmall,0,0, company.getIdNumbers().getVatLabel()+": ", "SVAT"));
            vatNumber.addElement(new SimpleTextBox(fontN,fontSizeSmall,0,0, company.getIdNumbers().getVatValue(), "SVAT"));
            vContainer.addElement(vatNumber);
            annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }
        if (proba.get("addresses_bordered")) {
            vContainer.setBorderColor(lineStrokeColor);
            vContainer.setBorderThickness(0.5f);
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
