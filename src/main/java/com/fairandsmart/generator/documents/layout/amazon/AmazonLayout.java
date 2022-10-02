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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.awt.*;


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

        //Generate barCodeNum
        Generex barCodeNumGen = new Generex("[0-9]{12}");
        String barCodeNum = barCodeNumGen.random();

        //Set fontFaces
        InvoiceLayout.pdType1FontPair fontPair = InvoiceLayout.getRandomPDType1FontPair();
        PDType1Font normalFont = fontPair.getNormalFont();
        PDType1Font boldFont = fontPair.getBoldFont();

        //Center or left alignment for items in table
        boolean centerAlignItems = (InvoiceLayout.getRandom().nextInt(2) == 0) ? true: false;

        /* Build Page components now */
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        new BorderBox(InvoiceLayout.getRandomColor(6), Color.WHITE, 4, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight()).build(contentStream, writer);

        //Barcode top
        BufferedImage barcodeTopImage = InvoiceLayout.generateEAN13BarcodeImage(barCodeNum);
        PDImageXObject pdBarcode = LosslessFactory.createFromImage(document, barcodeTopImage);
        new ImageBox(pdBarcode, page.getMediaBox().getWidth() / 2, 810, pdBarcode.getWidth(), (float)(pdBarcode.getHeight() / 1.5), barCodeNum).build(contentStream, writer);

        //Text top
        VerticalContainer infos = new VerticalContainer(25, 810, 500);
        infos.addElement(new SimpleTextBox(normalFont, 9, 0, 0, "Page 1 of 1, 1-1/1"));
        infos.addElement(new SimpleTextBox(normalFont, 9, 0, 0, "Invoice for "+model.getReference().getValue()+" "+model.getDate().getValue()));
        infos.addElement(new SimpleTextBox(boldFont, 10, 0, 0, "Retail / Tax Invoice / Cash Memorandum"));
        infos.build(contentStream, writer);

        // invoice / TRN number
        new SimpleTextBox(boldFont, 10, 25, 761, "Sold By").build(contentStream, writer);
        new SimpleTextBox(normalFont, 9, 25, 750, model.getCompany().getLogo().getName(), "SN").build(contentStream, writer);
        new SimpleTextBox(normalFont, 9, 25, 740, model.getCompany().getAddress().getLine1(), "SA" ).build(contentStream, writer);
        new SimpleTextBox(normalFont, 9, 25, 730, model.getCompany().getAddress().getZip()+" "+model.getCompany().getAddress().getCity(), "SA").build(contentStream, writer);
        String vatSentence = model.getCompany().getIdNumbers().getVatLabel()+" "+model.getCompany().getIdNumbers().getVatValue();
        new SimpleTextBox(normalFont, 9, 25, 690, vatSentence, "SVAT").build(contentStream, writer);
        new SimpleTextBox(normalFont, 9, 25, 680, "CST Number: "+model.getCompany().getIdNumbers().getVatValue(), "SVAT").build(contentStream, writer);
        String invoiceNumPrefix = (InvoiceLayout.getRandom().nextInt(10) < 5) ? "Invoice No. ": "";
        new SimpleTextBox(normalFont, 9, page.getMediaBox().getWidth()/2, 680, invoiceNumPrefix + model.getReference().getValue()).build(contentStream, writer);

        contentStream.moveTo(20, 650);
        contentStream.lineTo( page.getMediaBox().getWidth()-(20*2), 650);
        contentStream.stroke();

        // BIlling Address
        VerticalContainer verticalAddressContainer = new VerticalContainer(25, 630, 250);
        verticalAddressContainer.addElement(new SimpleTextBox(boldFont, 9, 0, 0, model.getClient().getBillingHead()));
        verticalAddressContainer.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalAddressContainer.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getBillingName(), "BN" ));
        verticalAddressContainer.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getBillingAddress().getLine1(), "BA" ));
        verticalAddressContainer.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getBillingAddress().getZip() + " "+model.getClient().getBillingAddress().getCity(), "BA" ));

        verticalAddressContainer.build(contentStream, writer);

        // Shipping Address
        VerticalContainer verticalAddressContainer2 = new VerticalContainer(page.getMediaBox().getWidth()/2 + InvoiceLayout.getRandom().nextInt(5), 630, 250 );
        verticalAddressContainer2.addElement(new SimpleTextBox(boldFont, 9, 0, 0, model.getClient().getShippingHead()));
        verticalAddressContainer2.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalAddressContainer2.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getShippingName(), "SHN" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getShippingAddress().getLine1(), "SHA" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getClient().getShippingAddress().getZip() + " " + model.getClient().getShippingAddress().getCity(), "SHA" ));

        verticalAddressContainer2.build(contentStream, writer);

        SimpleTextBox box1 = new SimpleTextBox(boldFont, 9, 25, 560, (InvoiceLayout.getRandom().nextInt(2) == 0) ? "Nature of Transaction: Sale": "Transaction: Sale");
        box1.build(contentStream, writer);

        float[] configRow = {20f, 130f, 60f, 60f, 60f, 60f, 60f, 60f};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        Color tableHdrBgColor = InvoiceLayout.getRandomColor(1);
        firstLine.setBackgroundColor(tableHdrBgColor);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "QTY", Color.BLACK, tableHdrBgColor), false);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "DESCRIPTION", Color.BLACK, tableHdrBgColor), false);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "UNIT PRICE", Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "DISCOUNT", Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "TOTAL WITHOUT TAX", Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "TAX TYPE", Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "TAX RATE", Color.BLACK, tableHdrBgColor), centerAlignItems);
        firstLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, "TAX AMOUNT", Color.BLACK, tableHdrBgColor), centerAlignItems);

        VerticalContainer verticalInvoiceItems = new VerticalContainer(25, 550, 600);
        verticalInvoiceItems.addElement(firstLine);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0, 0, page.getMediaBox().getWidth()-(20*2), 0));

        for(int w=0; w<model.getProductContainer().getProducts().size(); w++) {

            Product randomProduct = model.getProductContainer().getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
            productLine.addElement(new SimpleTextBox(boldFont, 8, 0, 0, randomProduct.getName(), "PD"), false);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, randomProduct.getFormatedPriceWithoutTax(), "UP"), centerAlignItems);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, ""), centerAlignItems);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, randomProduct.getFormatedTotalPriceWithoutTax(), "PTWTX" ), centerAlignItems);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, ""), centerAlignItems);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, Float.toString(randomProduct.getTaxRate() * 100)+"%", "TXR"), centerAlignItems);
            productLine.addElement(new SimpleTextBox(normalFont, 8, 0, 0, randomProduct.getFormatedTotalTax() ), centerAlignItems);

            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalInvoiceItems.addElement(productLine);
            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
        }

        TableRowBox shipping = new TableRowBox(configRow, 0, 0);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, ""), false);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "Shipping"), false);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "0.00"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, ""), centerAlignItems);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "0.00"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "Tax"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "0"), centerAlignItems);
        shipping.addElement(new SimpleTextBox(normalFont, 8, 0, 0, "0.00"), centerAlignItems);

        verticalInvoiceItems.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""));
        verticalInvoiceItems.addElement(shipping);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""));

        TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, ""), false);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, ""), false);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "TOTAL GROSS AMOUNT"), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "TOTAL DISCOUNT"), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "FINAL NET AMOUNT"), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "TAX TYPE"), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "TAX RATE"), centerAlignItems);
        titleTotalInvoice.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "TAX AMOUNT"), centerAlignItems);
        verticalInvoiceItems.addElement(titleTotalInvoice);

        verticalInvoiceItems.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        TableRowBox totalInvoice1 = new TableRowBox(configRow, 0, 0);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""), false);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""), false);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getProductContainer().getFormatedTotalWithoutTax(), "TWTX" ), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, ""), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getProductContainer().getFormatedTotalWithTax(), "TA" ), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, "VAT@"), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, Float.toString(model.getProductContainer().getProducts().get(0).getTaxRate() * 100)+"%", "TXR"), centerAlignItems);
        totalInvoice1.addElement(new SimpleTextBox(normalFont, 9, 0, 0, model.getProductContainer().getFormatedTotalTax(), "TTX" ), centerAlignItems);
        verticalInvoiceItems.addElement(totalInvoice1);

        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(20*2), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        // Add registered address information, 50% prob
        if (InvoiceLayout.getRandom().nextInt(100) < 50) {
              verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(20*2), 0));
              verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

              String addressFooterText = String.format("Registered Address for %s, %s, %s, %s, %s, %s",
                                                       model.getCompany().getName(),
                                                       model.getCompany().getAddress().getLine1(),
                                                       model.getCompany().getAddress().getLine2(),
                                                       model.getCompany().getAddress().getZip(),
                                                       model.getCompany().getAddress().getCity(),
                                                       model.getCompany().getAddress().getCountry());
              SimpleTextBox addressFooter = new SimpleTextBox(normalFont, 10, 0, 0, addressFooterText);
              addressFooter.setWidth(500);
              verticalInvoiceItems.addElement(addressFooter);
              verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
              verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(20*2), 0));
        }
        verticalInvoiceItems.build(contentStream, writer);

        // Add Signature if it is not null
        if (model.getCompany().getSignature().getName() != null) {
              String compSignatureName = model.getCompany().getName();
              compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
              SimpleTextBox singatureText = new SimpleTextBox(
                      normalFont, 8, 0, 130,
                      model.getCompany().getSignature().getLabel()+" "+compSignatureName, "Signature");
              float singatureTextxPos = page.getMediaBox().getWidth() - singatureText.getBoundingBox().getWidth() - 50;
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
              int signatureHeight = 60;
              // align signature to center of singatureText bbox
              float signatureXPos = singatureText.getBoundingBox().getPosX() + singatureText.getBoundingBox().getWidth()/2 - signatureWidth/2;
              float signatureYPos = 140;
              contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add footer line and info
        new HorizontalLineBox(20,110, page.getMediaBox().getWidth()-(20*2), 0).build(contentStream, writer);

        VerticalContainer verticalFooterContainer = new VerticalContainer(25, 100, 450);
        String compEmail = ((model.getCompany().getWebsite() == null) ? "company.domain.com" :  model.getCompany().getWebsite());
        verticalFooterContainer.addElement(new SimpleTextBox(boldFont, 9, 0, 0, String.format("To return an item, visit %s/returns", compEmail)));
        verticalFooterContainer.addElement(new SimpleTextBox(boldFont, 9, 0, 0, "For more information on your orders, visit http://"));
        verticalFooterContainer.addElement(new SimpleTextBox(boldFont, 9, 0, 0, String.format("%s/your-account", compEmail)));
        verticalFooterContainer.addElement(new SimpleTextBox(normalFont, 9, 0, 0, barCodeNum));
        verticalFooterContainer.build(contentStream, writer);

        // Logo Bottom
        // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
        URI logoUri = new URI(this.getClass().getClassLoader().getResource("common/logo/" + model.getCompany().getLogo().getFullPath()).getFile());
        String logoPath = logoUri.getPath();
        PDImageXObject logoFooter = PDImageXObject.createFromFile(logoPath, document);
        float ratio = logoFooter.getWidth() / logoFooter.getHeight();
        contentStream.drawImage(logoFooter, 480, 10, 85, 85 / ((ratio == 0) ? 1 : ratio) - 5);

        // Barcode bottom
        BufferedImage barcodeFooterImage = InvoiceLayout.generateEAN13BarcodeImage(barCodeNum);
        PDImageXObject barCodeFooter = LosslessFactory.createFromImage(document, barcodeFooterImage);
        contentStream.drawImage(barCodeFooter, 25, 10, barCodeFooter.getWidth() - 10, barCodeFooter.getHeight() - 70);
        contentStream.close();

        // Add confidential watermark, 9% prob
        if (InvoiceLayout.getRandom().nextInt(100) < 9) {
            InvoiceLayout.addWatermarkText(document, page, PDType1Font.HELVETICA, "Confidential");
        }

        writer.writeEndElement();
    }


}
