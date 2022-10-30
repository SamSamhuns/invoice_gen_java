package com.fairandsmart.generator.documents.layout.macomp;

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

import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.helper.HelperImage;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ContactNumber;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.footer.FootBox;
import com.fairandsmart.generator.documents.element.footer.SignatureBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.product.ProductBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MACOMPLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "MACOMP";
    }

    @Override
    public void buildInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer, InvoiceAnnotModel annot) throws Exception {
        PDPage page = new PDPage(PDRectangle.A4);

        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        // init invoice annotation objects
        annot.setVendor(new InvoiceAnnotModel.Vendor());
        annot.setInvoice(new InvoiceAnnotModel.Invoice());
        annot.setBillto(new InvoiceAnnotModel.Billto());
        annot.setTotal(new InvoiceAnnotModel.Total());
        annot.setItems(new ArrayList<InvoiceAnnotModel.Item>());

        // set frequently accessed vars
        Random rnd = model.getRandom();
        Client client = model.getClient();
        Company company = model.getCompany();
        PaymentInfo payment = model.getPaymentInfo();
        ProductContainer pc = model.getProductContainer();
        String cur = pc.getCurrency();

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> proba = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // get barcode number
        String barcodeNum = model.getReference().getValueBarcode();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 25;
        float rightPageMargin = 25;
        float bottomPageMargin = 8;
        float fontSize = 8;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = new Color(239,239,239);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        float maxLogoWidth; float maxLogoHeight;
        float posLogoX; float posLogoY;
        float logoScale;
        // gen barcode img
        BufferedImage barcodeBufImg = HelperImage.generateEAN13BarcodeImage(barcodeNum);
        PDImageXObject barcodeImg = LosslessFactory.createFromImage(document, barcodeBufImg);

        ///////////////////////////////////      Build Page components now      ////////////////////////////////////

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Invoice number
        new SimpleTextBox(fontB, fontSize+2, 310, 748, model.getReference().getLabelInvoice()+" : "+ model.getReference().getValueInvoice()).build(contentStream, writer);
        annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());

        // Vendor/company address
        VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,8,9,260,lineStrokeColor,model,document,company,annot,proba);
        vendorInfoBox.translate(50, 785);
        vendorInfoBox.build(contentStream,writer);

        // check if billing and shipping addresses should be switched
        float topY = 630, botY = 510;
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = topY; topY=botY; botY=tmp;
        }
        float billX = 50; float billY = topY;
        float shipX = 50; float shipY = botY;

        // Billing Address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,8,9,260,lineStrokeColor,model,document,client,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(contentStream,writer);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,8,9,260,lineStrokeColor,model,document,client,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(contentStream,writer);

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 300, pAX = 310, pAY = 630;
            proba.put("vendor_tax_number_top", proba.get("vendor_address_tax_number"));

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,document,payment,company,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(contentStream,writer);
        }

        String sigText = company.getSignature().getLabel()+" "+(company.getName().length() < 25 ? company.getName() : "");
        SignatureBox sigB = new SignatureBox(
                fontN, 8, sigText, 50,400, 90,30,
                lineStrokeColor, model, document, company);
        sigB.translate(50, 150);
        sigB.build(contentStream,writer);

        VerticalContainer invoiceInfo = new VerticalContainer(310, 580, 400);

        float[] configRow = {150f, 200f};
        TableRowBox elementInfoContainer = new TableRowBox(configRow, 0, 0);
        SimpleTextBox Label = new SimpleTextBox(fontB, fontSize+1, 0,0, model.getDate().getLabelInvoice(),black, null, HAlign.LEFT);
        elementInfoContainer.addElement(Label, false);
        SimpleTextBox Value = new SimpleTextBox(fontN, fontSize, 0,0, model.getDate().getValueInvoice());
        elementInfoContainer.addElement(Value, false);
        invoiceInfo.addElement(elementInfoContainer);
        invoiceInfo.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));

        TableRowBox elementInfoContainer1 = new TableRowBox(configRow,0, 0);
        SimpleTextBox Label1 = new SimpleTextBox(fontB, fontSize+1, 0,0, model.getReference().getLabelClient(),black, null, HAlign.LEFT);
        elementInfoContainer1.addElement(Label1, false);
        SimpleTextBox Value1 = new SimpleTextBox(fontN, fontSize, 0,0, model.getReference().getValueClient());
        elementInfoContainer1.addElement(Value1, false);
        invoiceInfo.addElement(elementInfoContainer1);
        invoiceInfo.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));

        TableRowBox elementInfoContainer2 = new TableRowBox(configRow,0, 0);
        SimpleTextBox Label2 = new SimpleTextBox(fontB, fontSize+1, 0,0, model.getReference().getLabelOrder(),black, null, HAlign.LEFT);
        elementInfoContainer2.addElement(Label2, false);
        SimpleTextBox Value2 = new SimpleTextBox(fontN, fontSize, 0,0, model.getReference().getValueOrder(),black, null, HAlign.LEFT);
        elementInfoContainer2.addElement(Value2, false);
        invoiceInfo.addElement(elementInfoContainer2);
        invoiceInfo.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));

        TableRowBox elementInfoContainer3 = new TableRowBox(configRow,0, 0);
        SimpleTextBox Label3 = new SimpleTextBox(fontB, fontSize+1, 0,0, payment.getLabelPaymentType(),black, null, HAlign.LEFT);
        elementInfoContainer3.addElement(Label3, false);
        SimpleTextBox Value3 = new SimpleTextBox(fontN, fontSize, 0,0, payment.getValuePaymentType(),black, null, HAlign.LEFT);
        elementInfoContainer3.addElement(Value3, false);
        invoiceInfo.addElement(elementInfoContainer3);
        invoiceInfo.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));

        invoiceInfo.build(contentStream, writer);

        ProductBox products = new ProductBox(30, 400, pc,fontI, fontB, fontSize);
        products.build(contentStream, writer);

        VerticalContainer footer = new VerticalContainer(50, 100, 1000);
        footer.addElement(new HorizontalLineBox(0,0,530, 0));
        footer.addElement(new BorderBox(white,white, 0,0, 0, 0, 3));
        FootBox footBox = new FootBox(fontN, fontB, fontI, 11, model, document);

        footer.addElement(footBox);
        footer.build(contentStream, writer);

        contentStream.close();
        writer.writeEndElement();
    }
}
