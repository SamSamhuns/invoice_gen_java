package com.fairandsmart.generator.documents.layout.bdmobilier;

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
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;

import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.product.ProductTableBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;
import com.fairandsmart.generator.documents.element.footer.FootCompanyBox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class BDmobilierLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "BDMobilier";
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

        IDNumbers idNumbers = company.getIdNumbers();
        Address address = company.getAddress();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN : fontB;

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 10;
        float rightPageMargin = 35;
        float topPageMargin = 10;

        // colors
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color lineStrokeColor = proba.get("line_stroke_black") ? Color.BLACK: themeColor;
        Color grayish = HelperCommon.getRandomGrayishColor();

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);

        /*//////////////////   Build Page components now   //////////////////*/

        PDPageContentStream stream = new PDPageContentStream(document, page);

        /// Draw top left logo
        float maxLogoWidth = 200;
        float maxLogoHeight = 100;
        float logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        float logoWidth = logoImg.getWidth() * logoScale;
        float logoHeight = logoImg.getHeight() * logoScale;
        float posLogoX = leftPageMargin;
        float posLogoY = pageHeight - topPageMargin;
        new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(stream,writer);

        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        // Top center document title
        if (proba.get("doc_title_top_center")) {
            SimpleTextBox docTitleBox = new SimpleTextBox(fontB,16,0,0,docTitle,"SN");
            docTitleBox.translate(pageMiddleX-(docTitleBox.getBBox().getWidth()/2), pageHeight-80);
            docTitleBox.build(stream,writer);
            annot.setTitle(docTitle);
        }
        // Top right company header info
        // top right document title
        VerticalContainer headerCont = new VerticalContainer(0, pageHeight-topPageMargin, 250);  // PosX is reset later for alignment
        if (proba.get("doc_title_top_right") && !proba.get("doc_title_top_right")) {
            headerCont.addElement(new SimpleTextBox(fontB,13,0,0,docTitle,"SN"));
            headerCont.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            annot.setTitle(docTitle);
        }
        // Vendor name
        headerCont.addElement(new SimpleTextBox(fontNB,10,0,0,company.getName(),"SN"));
        // Invoice date
        headerCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getDate().getValueInvoice(),grayish,Color.WHITE,"IDATE"));
        annot.getVendor().setVendorName(company.getName());
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());

        // Purchase Order Number
        if (proba.get("purchase_order_number_top_right")) {
            headerCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder(),grayish,Color.WHITE,"LO"));
            annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // vendor tax number
        else if (proba.get("vendor_tax_number_top")) {
            headerCont.addElement(new SimpleTextBox(fontN,10,0,0,company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue(),grayish,Color.WHITE,"SVAT"));
            annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }
        // invoice id number
        HorizontalContainer invNumCont = new HorizontalContainer(0, 0);
        invNumCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelInvoice()+" ",grayish,Color.WHITE));
        invNumCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getValueInvoice(),grayish,Color.WHITE,"IN"));
        annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());

        headerCont.addElement(invNumCont);

        headerCont.translate(pageWidth - headerCont.getBBox().getWidth() - rightPageMargin, 0);  // align top right header to fit properly
        headerCont.build(stream,writer);

        // check if billing and shipping addresses should be switched
        float leftAddrX = 120 + rnd.nextInt(15);
        float rightAddrX = 335 + rnd.nextInt(15);
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight-110;
        float shipX = rightAddrX; float shipY = pageHeight-110;

        // Billing Address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(stream,writer);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(stream,writer);

        // Left side info
        int leftFSize = 7;
        VerticalContainer infoOrder = new VerticalContainer(leftPageMargin,pageHeight-211,76);
        // Purhase Order Number right if not at the top
        if (proba.get("purchase_order_number_left") && !proba.get("purchase_order_number_top_right")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,model.getReference().getLabelOrder()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,model.getReference().getValueOrder(),"ONUM"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // Payment Due
        if (proba.get("payment_due_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,model.getDate().getLabelPaymentDue()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,model.getDate().getValuePaymentDue(),"PDATE"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        // Currency Used
        if (proba.get("currency_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelAccountCurrency()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,cur, "CUR"));
            annot.getTotal().setCurrency(cur);
        }
        infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelPaymentType()));
        infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,payment.getValuePaymentType(),"PMODE"));
        infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,pc.getFmtTotalWithTaxAndDiscount()+" "+cur,"TTX"));
        annot.getTotal().setCurrency(cur);
        annot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount());

        infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        // Payment Terms
        if (proba.get("payment_terms_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelPaymentTerm(), "PTL"));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,payment.getValuePaymentTerm(), "PTV"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            annot.getInvoice().setPaymentTerm(payment.getValuePaymentTerm());
        }
        infoOrder.build(stream,writer);

        // table top horizontal line, will be built after verticalTableItems
        float ttx1 = 85; float tty1 = billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight() - 15;
        float ttx2 = pageWidth-rightPageMargin; float tty2 = tty1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(ttx1, tty1, ttx2, tty2, lineStrokeColor);

        // Product table
        float tableWidth = pageWidth-rightPageMargin-ttx1;
        int maxHdrNum = 8;
        ProductTableBox productTableBox = new ProductTableBox(
                maxHdrNum, fontN, fontB, fontI, 8, 8, tableWidth+15, lineStrokeColor, ttx1, tty1, tableWidth, model, annot, proba);
        productTableBox.build(stream,writer);

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 330;
            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,annot,proba);

            float pAX = (proba.get("signature_bottom_left")) ? pageWidth-paymentBox.getBBox().getWidth()-rightPageMargin: ttx1;
            float pAY = productTableBox.getBBox().getPosY()-productTableBox.getBBox().getHeight()-5;

            paymentBox.translate(pAX, pAY);
            paymentBox.build(stream,writer);
        }

        // Add Signature at bottom
        if (proba.get("signature_bottom")) {
            String compSignatureName = company.getName();
            compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0, company.getSignature().getLabel()+" "+compSignatureName, "Signature");

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
                    sigTX + sigTextBox.getBBox().getWidth() + 10, sigTY + 5
                    ).build(stream,writer);

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

        // Add company stamp watermark, 40% prob
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
        // if no signature and no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or the Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignMsgBox = new SimpleTextBox(fontN,8,0,80, noStampSignMsg, "footnote");
            // align the text to the center
            noStampSignMsgBox.getBBox().setPosX(pageMiddleX - noStampSignMsgBox.getBBox().getWidth()/2);
            noStampSignMsgBox.build(stream,writer);
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

        // Footer company info
        if (proba.get("vendor_info_footer")) {
            int fSize = 7 + rnd.nextInt(3);
            FootCompanyBox footCompanyBox = new FootCompanyBox(fontN,fontB,fontI,fSize,fSize+1,themeColor, pageWidth-leftPageMargin-rightPageMargin,model,annot,proba);
            float fW = footCompanyBox.getBBox().getWidth();
            footCompanyBox.alignElements(HAlign.CENTER, fW);
            footCompanyBox.translate(pageMiddleX-fW/2,60);
            footCompanyBox.build(stream,writer);
        }

        stream.close();
        writer.writeEndElement();
    }
}
