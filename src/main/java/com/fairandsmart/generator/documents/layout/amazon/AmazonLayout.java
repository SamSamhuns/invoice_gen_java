package com.fairandsmart.generator.documents.layout.amazon;

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

import com.fairandsmart.generator.documents.data.model.Helper;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.mifmif.common.regex.Generex;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;


@ApplicationScoped
public class AmazonLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Amazon";
    }

    @Override
    public void builtInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer) throws Exception {
        PDPage page = new PDPage(PDRectangle.A4);

        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        Random rnd = Helper.getRandom();

        // Set probability map, int value out of 100, 60 -> 60% proba
        Map<String, Integer> genProb = new HashMap<>();
        genProb.put("barcode_top", 60);
        genProb.put("switch_bill_ship_addresses", 10);
        genProb.put("registered_address_info", 50);
        genProb.put("barcode_bottom", 60);
        genProb.put("stamp_bottom", 45);
        genProb.put("logo_watermark", 15);
        genProb.put("confidential_watermark", 4);

        // Generate barCodeNum
        Generex barCodeNumGen = new Generex("[0-9]{12}");
        String barCodeNum = barCodeNumGen.random();

        // Set fontFaces
        InvoiceLayout.pdType1Fonts fontPair = InvoiceLayout.getRandomPDType1Fonts();
        PDFont fontNormal1 = fontPair.getFontNormal();
        PDFont fontBold1 = fontPair.getFontBold();

        // Center or left alignment for items in table
        boolean centerAlignItems = (rnd.nextInt(2) == 0) ? true: false;

        float pageWidth = page.getMediaBox().getWidth();

        /* Build Page components now */
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        new BorderBox(InvoiceLayout.getRandomColor(6), Color.WHITE, 4, 0, 0, pageWidth, page.getMediaBox().getHeight()).build(contentStream, writer);

        // Barcode top
        if (rnd.nextInt(100) < genProb.get("barcode_top")) {
            BufferedImage barcodeTopImage = InvoiceLayout.generateEAN13BarcodeImage(barCodeNum);
            PDImageXObject pdBarcode = LosslessFactory.createFromImage(document, barcodeTopImage);
            new ImageBox(pdBarcode, pageWidth / 2, 810, pdBarcode.getWidth(), (float)(pdBarcode.getHeight() / 1.5), barCodeNum).build(contentStream, writer);
        }

        // Text top
        VerticalContainer infos = new VerticalContainer(25, 810, 500);
        infos.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, "Page 1 of 1" + ((rnd.nextInt(2) == 1) ? ", 1-1/1": ".")));
        infos.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, "Invoice for "+model.getReference().getValue()+" "+model.getDate().getValue()));
        infos.addElement(new SimpleTextBox(fontBold1, 10, 0, 0, ((rnd.nextInt(2) == 1) ? "Retail" : "Institution") + " / Tax Invoice / Cash Memorandum"));
        infos.build(contentStream, writer);

        // invoice / TRN number
        new SimpleTextBox(fontBold1, 10, 25, 761, "Sold By").build(contentStream, writer);
        new SimpleTextBox(fontNormal1, 9, 25, 750, model.getCompany().getLogo().getName(), "SN").build(contentStream, writer);
        new SimpleTextBox(fontNormal1, 9, 25, 740, model.getCompany().getAddress().getLine1(), "SA" ).build(contentStream, writer);
        new SimpleTextBox(fontNormal1, 9, 25, 730, model.getCompany().getAddress().getZip()+" "+model.getCompany().getAddress().getCity(), "SA").build(contentStream, writer);
        String vatSentence = model.getCompany().getIdNumbers().getVatLabel()+" "+model.getCompany().getIdNumbers().getVatValue();
        if (rnd.nextInt(2) == 1) {
            new SimpleTextBox(fontNormal1, 9, 25, 690, "CST Number: "+model.getCompany().getIdNumbers().getVatValue(), "SVAT").build(contentStream, writer);
        }
        new SimpleTextBox(fontNormal1, 9, 25, 680, vatSentence, "SVAT").build(contentStream, writer);
        new SimpleTextBox(fontNormal1, 9, pageWidth/2, 680, ((rnd.nextInt(10) < 5) ? "Invoice No. ": "") + model.getReference().getValue()).build(contentStream, writer);

        contentStream.moveTo(20, 650);
        contentStream.lineTo( pageWidth-(20*2), 650);
        contentStream.stroke();

        // check if billing and shipping addresses should be switched
        float leftAddrX = 25;
        float rightAddrX = pageWidth/2 + rnd.nextInt(5);
        if (rnd.nextInt(100) < genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = 630;
        float shipX = rightAddrX; float shipY = 630;

        // Billing Address
        VerticalContainer verticalAddressContainer = new VerticalContainer(billX, billY, 250);
        verticalAddressContainer.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, model.getClient().getBillingHead()));
        verticalAddressContainer.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalAddressContainer.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getBillingName(), "BN" ));
        verticalAddressContainer.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getBillingAddress().getLine1(), "BA" ));
        verticalAddressContainer.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getBillingAddress().getZip() + " "+model.getClient().getBillingAddress().getCity(), "BA" ));

        verticalAddressContainer.build(contentStream, writer);

        // Shipping Address
        VerticalContainer verticalAddressContainer2 = new VerticalContainer(shipX, shipY, 250);
        verticalAddressContainer2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, model.getClient().getShippingHead()));
        verticalAddressContainer2.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getShippingName(), "SHN" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getShippingAddress().getLine1(), "SHA" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getClient().getShippingAddress().getZip() + " " + model.getClient().getShippingAddress().getCity(), "SHA" ));

        verticalAddressContainer2.build(contentStream, writer);

        SimpleTextBox box1 = new SimpleTextBox(fontBold1, 9, 25, 560, (rnd.nextInt(2) == 0) ? "Nature of Transaction: Sale": "Transaction: Sale");
        box1.build(contentStream, writer);

        boolean upperCap = rnd.nextInt(2) == 1;
        float[] configRow = {20f, 130f, 60f, 60f, 60f, 60f, 60f, 60f};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        Color tableHdrBgColor = InvoiceLayout.getRandomColor(1);
        firstLine.setBackgroundColor(tableHdrBgColor);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "QTY": "Qty"), Color.BLACK, tableHdrBgColor), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "DESCRIPTION" : "Description" ), Color.BLACK, tableHdrBgColor), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "UNIT PRICE" : "Unit Price"), Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "DISCOUNT" : "Discount"), Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "TOTAL WITHOUT TAX": "Total without Tax"), Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "TAX TYPE": "Tax Type"), Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "TAX RATE": "Tax Rate"), Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, (upperCap ? "TAX AMOUNT": "Tax Amount"), Color.BLACK, tableHdrBgColor), centerAlignItems);

        VerticalContainer verticalInvoiceItems = new VerticalContainer(25, 550, 600);
        verticalInvoiceItems.addElement(firstLine);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0, 0, pageWidth-(20*2), 0));
        // item list
        for(int w=0; w<model.getProductContainer().getProducts().size(); w++) {

            Product randomProduct = model.getProductContainer().getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
            productLine.addElement(new SimpleTextBox(fontBold1, 8, 0, 0, randomProduct.getName(), "PD"), false);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, randomProduct.getFormatedPrice(), "UP"), centerAlignItems);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, ""), centerAlignItems);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, randomProduct.getFormatedTotalPrice(), "PTWTX" ), centerAlignItems);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, ""), centerAlignItems);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, Float.toString(Helper.round(randomProduct.getTaxRate() * 100, 2))+"%", "TXR"), centerAlignItems);
            productLine.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, randomProduct.getFormatedTotalTax() ), centerAlignItems);

            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalInvoiceItems.addElement(productLine);
            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
        }

        TableRowBox shipping = new TableRowBox(configRow, 0, 0);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, ""), false);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, (upperCap ? "SHIPPING" : "Shipping")), false);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, "0.00"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, ""), centerAlignItems);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, "0.00"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, (upperCap ? "TAX" : "Tax")), centerAlignItems);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, "0"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(fontNormal1, 8, 0, 0, "0.00"), centerAlignItems);

        verticalInvoiceItems.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""));
        verticalInvoiceItems.addElement(shipping);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, pageWidth-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""));

        TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, ""), false);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, ""), false);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "TOTAL GROSS AMOUNT": "Total Gross Amount")), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "TOTAL DISCOUNT": "Total")), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "FINAL NET AMOU)NT": "Final Net Amount")), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "TAX TYPE": "Tax Type")), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "TAX RATE": "Tax Rate")), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, (upperCap ? "TAX AMOUNT": "Tax Amount")), centerAlignItems);
        verticalInvoiceItems.addElement(titleTotalInvoice);

        verticalInvoiceItems.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, pageWidth-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        TableRowBox totalInvoice1 = new TableRowBox(configRow, 0, 0);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""), false);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""), false);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getProductContainer().getFormatedTotal(), "TWTX" ), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, ""), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getProductContainer().getFormatedTotalWithTax(), "TA" ), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, (upperCap ? "VAT@": "vat@")), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, Float.toString(model.getProductContainer().getProducts().get(0).getTaxRate() * 100)+"%", "TXR"), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, model.getProductContainer().getFormatedTotalTax(), "TTX" ), centerAlignItems);
        verticalInvoiceItems.addElement(totalInvoice1);

        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, pageWidth-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        // Add registered address information
        if (rnd.nextInt(100) < genProb.get("registered_address_info")) {
              verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, pageWidth-(20*2), 0));
              verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

              String addressFooterText = String.format("Registered Address for %s, %s, %s, %s, %s, %s",
                                                       model.getCompany().getName(),
                                                       model.getCompany().getAddress().getLine1(),
                                                       model.getCompany().getAddress().getLine2(),
                                                       model.getCompany().getAddress().getZip(),
                                                       model.getCompany().getAddress().getCity(),
                                                       model.getCompany().getAddress().getCountry());
              SimpleTextBox addressFooter = new SimpleTextBox(fontNormal1, 10, 0, 0, addressFooterText);
              addressFooter.setWidth(500);
              verticalInvoiceItems.addElement(addressFooter);
              verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
              verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, pageWidth-(20*2), 0));
        }
        verticalInvoiceItems.build(contentStream, writer);

        // Add Signature if it is not null
        if (model.getCompany().getSignature().getName() != null) {
              String compSignatureName = model.getCompany().getName();
              compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
              SimpleTextBox singatureText = new SimpleTextBox(
                      fontNormal1, 8, 0, 130,
                      model.getCompany().getSignature().getLabel()+" "+compSignatureName, "Signature");
              float singatureTextxPos = pageWidth - singatureText.getBoundingBox().getWidth() - 50;
              singatureText.getBoundingBox().setPosX(singatureTextxPos);
              singatureText.build(contentStream, writer);
              new HorizontalLineBox(
                      singatureTextxPos - 10, 135,
                      singatureTextxPos + singatureText.getBoundingBox().getWidth() + 10, 135
                      ).build(contentStream, writer);

              // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
              URI signatureUri = new URI(this.getClass().getClassLoader().getResource("common/signature/" + model.getCompany().getSignature().getFullPath()).getFile());
              String signaturePath = signatureUri.getPath();
              PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
              int signatureWidth = 120;
              int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
              // align signature to center of singatureText bbox
              float signatureXPos = singatureText.getBoundingBox().getPosX() + singatureText.getBoundingBox().getWidth()/2 - signatureWidth/2;
              float signatureYPos = 140;
              contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add footer line and info
        new HorizontalLineBox(20, 110, pageWidth-(20*2), 0).build(contentStream, writer);

        VerticalContainer verticalFooterContainer = new VerticalContainer(25, 100, 450);
        String compEmail = ((model.getCompany().getWebsite() == null) ? "company.domain.com" :  model.getCompany().getWebsite());
        verticalFooterContainer.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, String.format("To return an item, visit %s/returns", compEmail)));
        String infoText = (rnd.nextInt(2) == 1) ? "For more information on your orders, visit http://": "For queries on orders, visit http://";
        verticalFooterContainer.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, infoText));
        verticalFooterContainer.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, String.format("%s/your-account", compEmail)));
        verticalFooterContainer.addElement(new SimpleTextBox(fontNormal1, 9, 0, 0, barCodeNum));
        verticalFooterContainer.build(contentStream, writer);

        // Logo Bottom
        // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
        URI logoUri = new URI(this.getClass().getClassLoader().getResource("common/logo/" + model.getCompany().getLogo().getFullPath()).getFile());
        String logoPath = logoUri.getPath();
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float ratio = logoImg.getWidth() / logoImg.getHeight();
        contentStream.drawImage(logoImg, 480, 10, 85, 85 / ((ratio == 0) ? 1 : ratio) - 5);

        // Barcode bottom
        if (rnd.nextInt(100) < genProb.get("barcode_bottom")) {
            BufferedImage barcodeFooterImage = InvoiceLayout.generateEAN13BarcodeImage(barCodeNum);
            PDImageXObject barCodeFooter = LosslessFactory.createFromImage(document, barcodeFooterImage);
            contentStream.drawImage(barCodeFooter, 25, 10, barCodeFooter.getWidth() - 10, barCodeFooter.getHeight() - 70);
        }

        // Add company stamp watermark, 40% prob
        if (rnd.nextInt(100) < genProb.get("stamp_bottom")) {
            // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
            URI stampUri = new URI(this.getClass().getClassLoader().getResource("common/stamp/" + model.getCompany().getStamp().getFullPath()).getFile());
            String stampPath = stampUri.getPath();
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f; float maxAStamp = 0.8f;
            float resDim = 90 + rnd.nextInt(20);
            float xPosStamp; float yPosStamp;
            // draw to lower right if signature if present
            if (rnd.nextInt(2) == 1 && model.getCompany().getSignature().getName() != null) {
                xPosStamp = 400 + rnd.nextInt(10);
                yPosStamp = 125 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = 125 + rnd.nextInt(5);
            }
            double rotAngle = 10 + rnd.nextInt(80);
            float stampWidth = resDim;
            float stampHeight = resDim;
            if (model.getCompany().getStamp().getName().matches("(.*)" + "_rect")) {
                // For Rectangular stamps, set rotation angle to 0 and
                // resize stamp maintaining aspect ratio
                rotAngle = 0;
                stampHeight = (stampWidth * stampImg.getHeight()) / stampImg.getWidth();
            }
            InvoiceLayout.addWatermarkImagePDF(document, page, stampImg, xPosStamp, yPosStamp,
                                               stampWidth, stampHeight, minAStamp, maxAStamp, rotAngle);
        }
        // if no signature and no logo, add a footer note
        else if (model.getCompany().getSignature().getName() == null) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontNormal1, 7, 20, 130, noStampSignMsg, "Footnote").build(contentStream, writer);
        }

        // Add bg logo watermark or confidential stamp, but not both at once
        if (rnd.nextInt(100) < genProb.get("confidential_watermark")) {
            // Add confidential watermark
            InvoiceLayout.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "Confidential");
        }
        else if (rnd.nextInt(100) < genProb.get("logo_watermark")) {
            // Add watermarked background logo
            InvoiceLayout.addWatermarkImagePDF(document, page, logoImg);
        }

        contentStream.close();
        writer.writeEndElement();
    }


}
