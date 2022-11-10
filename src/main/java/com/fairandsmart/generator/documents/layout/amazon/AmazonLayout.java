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

import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.helper.HelperImage;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.product.ProductTableBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class AmazonLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Amazon";
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
        float topPageMargin = rightPageMargin;
        float bottomPageMargin = 8;

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

        PDPageContentStream stream = new PDPageContentStream(document, page);
        new BorderBox(HelperCommon.getRandomColor(6), white, 4, 0, 0, pageWidth, pageHeight).build(stream,writer);

        // Barcode top
        if (proba.get("barcode_top")) {
            new ImageBox(barcodeImg, pageWidth / 2 + 10, pageHeight-topPageMargin, barcodeImg.getWidth(), (float)(barcodeImg.getHeight() / 1.5), "barcode:"+barcodeNum).build(stream,writer);
        }
        // Or Logo top
        else if (proba.get("logo_top")) {
            maxLogoWidth = 150;
            maxLogoHeight = 90;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = pageWidth-logoWidth-rightPageMargin;
            posLogoY = pageHeight-topPageMargin;
            new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(stream,writer);
        }

        // title and rext top
        VerticalContainer topTextCont = new VerticalContainer(leftPageMargin, 810, 500);
        topTextCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, "Page 1 of 1" + ((rnd.nextBoolean()) ? ", 1-1/1": ".")));
        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        String textTopInvString = ((rnd.nextBoolean()) ? docTitle+" for ": docTitle+" dated ")+model.getDate().getValueInvoice();
        if (proba.get("text_top_invoice_number") && !proba.get("invoice_number_top")) {  // inc invoice number if not mentioned below
            textTopInvString = model.getReference().getLabelInvoice()+" "+model.getReference().getValueInvoice()+" for "+model.getDate().getValueInvoice();
            annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        }
        topTextCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, textTopInvString, docTitle+" Number"));
        topTextCont.addElement(new SimpleTextBox(fontNB, 13, 0, 0, docTitle));
        if (proba.get("text_top_center") && !proba.get("barcode_top")) {  // center top text if barcode not present
            topTextCont.alignElements(HAlign.CENTER, topTextCont.getBBox().getWidth());
            topTextCont.translate(pageMiddleX - topTextCont.getBBox().getWidth()/2, 0);
        }
        annot.setTitle(docTitle);
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        topTextCont.build(stream,writer);

        // Vendor/Company Address
        proba.put("vendor_address_tax_number", false);
        VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,9,10,300,lineStrokeColor,model,annot,proba);
        vendorInfoBox.translate(leftPageMargin, 760);
        vendorInfoBox.build(stream,writer);

        float leftTopInfoY = 670; // Progressively inc by 10 pts after each left-side box
        // if no "vendor_address_phone_fax" then Purchase Order Number, Left side
        if (proba.get("purchase_order_number_top") && !proba.get("vendor_address_phone") && !proba.get("vendor_address_fax")) {
            String purchaseOrderText = model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, purchaseOrderText, "LO").build(stream,writer);
            leftTopInfoY += 10;
            annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // TAX number, Left side
        if (proba.get("vendor_tax_number_top")) {
            String vatText = company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, vatText, "SVAT").build(stream,writer);
            annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }

        float rightTopInfoY = 670; // Progressively inc by 10 pts after each right-side box, building from bottom
        // Currency Used, Right side
        if (proba.get("currency_top")) {
            String currencyText = payment.getLabelAccountCurrency()+": "+cur;
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, currencyText, "CUR").build(stream,writer);
            rightTopInfoY += 10;
            annot.getTotal().setCurrency(cur);
        }
        // Payment Terms, Right side
        if (proba.get("payment_terms_top")) {
            String paymentTermText = payment.getLabelPaymentTerm()+": "+payment.getValuePaymentTerm();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentTermText, "PT").build(stream,writer);
            rightTopInfoY += 10;
            annot.getInvoice().setPaymentTerm(payment.getValuePaymentTerm());
        }
        // Payment Due Date if Payment Terms is not mentioned
        else if (proba.get("payment_due_top")) {
            String paymentDueText = model.getDate().getLabelPaymentDue()+": "+model.getDate().getValuePaymentDue();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentDueText, "PT").build(stream,writer);
            rightTopInfoY += 10;
            annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        // invoice number, Right side
        if (proba.get("invoice_number_top")) {
            String invoiceText = model.getReference().getLabelInvoice() + ": " +model.getReference().getValueInvoice();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, invoiceText, "INV").build(stream,writer);
            annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        }

        new HorizontalLineBox(leftPageMargin, 650, pageWidth-rightPageMargin, 650, lineStrokeColor).build(stream,writer);

        // check if billing and shipping addresses should be switched
        float leftAddrX = leftPageMargin;
        float rightAddrX = pageWidth/2 + rnd.nextInt(5);
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = 645;
        float shipX = rightAddrX; float shipY = 645;

        // Billing Address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(stream,writer);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(stream,writer);

        // Product table
        float tableTopPosX = leftPageMargin;
        float tableTopPosY = billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight() - 15;
        float tableWidth = pageWidth-rightPageMargin-leftPageMargin;
        ProductTableBox productTableBox = new ProductTableBox(
                fontN, fontB, fontI, 8, 8, 600, lineStrokeColor, tableTopPosX, tableTopPosY, tableWidth, model, annot, proba);
        productTableBox.build(stream,writer);

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 350;
            float pAX = proba.get("signature_bottom_left") ? rightAddrX: leftPageMargin;
            float pAY = productTableBox.getBBox().getPosY() - productTableBox.getBBox().getHeight() - 10;

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(stream,writer);
        }

        // Add Signature at bottom
        if (proba.get("signature_bottom")) {
            String sigText = company.getSignature().getLabel()+" "+(company.getName().length() < 25 ? company.getName() : "");
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0,sigText, "Signature");

            float sigTX;
            float sigTY = 130;
            if (proba.get("signature_bottom_left")) {  // bottom left
                sigTX = leftPageMargin + 25;
            } else {                                     // bottom right
                sigTX = pageWidth - sigTextBox.getBBox().getWidth() - 50;
            }
            sigTextBox.translate(sigTX, sigTY);
            sigTextBox.build(stream,writer);

            new HorizontalLineBox(
                    sigTX - 10, sigTY + 5,
                    sigTX + sigTextBox.getBBox().getWidth() + 5, sigTY + 5,
                    lineStrokeColor).build(stream,writer);

            String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

            float maxSW = 110, maxSH = 65;
            float sigScale = Math.min(maxSW/sigImg.getWidth(), maxSH/sigImg.getHeight());
            float sigW = sigImg.getWidth() * sigScale;
            float sigH = sigImg.getHeight() * sigScale;
            // align signature to center of sigTextBox bbox
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(stream,writer);
        }

        float footerHLineY = 110;
        // Add footer line and info if footer_info to be used and number of items less than 5
        if (proba.get("footer_info") && pc.getProducts().size() < 5) {
            new HorizontalLineBox(leftPageMargin, footerHLineY, pageWidth-rightPageMargin, footerHLineY, lineStrokeColor).build(stream,writer);

            VerticalContainer verticalFooterCont = new VerticalContainer(leftPageMargin, footerHLineY-10, 450);
            String compEmail = ((company.getWebsite() == null) ? "company.domain.com" :  company.getWebsite());
            String footerLine1 = (rnd.nextBoolean()) ? String.format("To return an item, visit %s/returns", compEmail) : String.format("For feedback, visit %s/feedback", compEmail);
            String footerLine2 = (rnd.nextBoolean()) ? "For more information on orders, visit http://" : "For queries on orders, visit http://";
            String footerLine3 = (rnd.nextBoolean()) ? String.format("%s/account-name", compEmail) : String.format("%s/orders", compEmail);
            verticalFooterCont.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine1));
            verticalFooterCont.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine2));
            verticalFooterCont.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine3));
            verticalFooterCont.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 9, 0, 0, barcodeNum));

            if (proba.get("footer_info_center")) {
                verticalFooterCont.alignElements(HAlign.CENTER, verticalFooterCont.getBBox().getWidth());
                verticalFooterCont.translate(pageMiddleX - verticalFooterCont.getBBox().getWidth()/2, 0);
            }
            verticalFooterCont.build(stream,writer);
        }

        // Logo Bottom if logo is not at top or barcode at top
        if (!proba.get("logo_top") | proba.get("barcode_top")) {
            maxLogoWidth = 120;
            maxLogoHeight = 80;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = pageWidth-logoWidth-rightPageMargin;
            posLogoY = bottomPageMargin+logoHeight+footerHLineY/2-logoHeight/2;
            new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(stream,writer);
        }

        // Barcode bottom
        if (proba.get("barcode_bottom")) {
            float bW = barcodeImg.getWidth() - 15, bH = barcodeImg.getHeight() - 72;
            new ImageBox(barcodeImg, leftPageMargin, bottomPageMargin+bH, bW, bH, "barcode:"+barcodeNum).build(stream,writer);
        }

        // Add company stamp watermark
        if (proba.get("stamp_bottom")) {
            float alpha = HelperCommon.rand_uniform(0.6f, 0.8f);
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp, yPosStamp;
            // draw to lower right if signature in bottom or lower left if signature in bottom left
            if (proba.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = ((proba.get("signature_bottom_left")) ? leftPageMargin + 5 : 405) + rnd.nextInt(10);
                yPosStamp = 125 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = 125 + rnd.nextInt(5);
            }
            StampBox stampBox = new StampBox(resDim,resDim,alpha,model,document,company,proba);
            stampBox.translate(xPosStamp,yPosStamp);
            stampBox.build(stream,writer);
        }
        // if no signature anåd no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontN, 7, 20, 130, noStampMsg, "footnote").build(stream,writer);
        }

        // Add bg logo watermark or confidential stamp, but not both at once
        if (proba.get("confidential_watermark")) {
            // Add confidential watermark
            HelperImage.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "confidential");
        }
        else if (proba.get("logo_watermark")) {
            // Add watermarked background logo
            HelperImage.addWatermarkImagePDF(document, page, logoImg);
        }

        stream.close();
        writer.writeEndElement();
    }
}
