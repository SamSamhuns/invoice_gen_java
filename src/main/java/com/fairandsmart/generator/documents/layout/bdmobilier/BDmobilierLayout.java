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
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;å
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.ProductTable;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
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
import java.util.Arrays;
import java.util.Random;


@ApplicationScoped
public class BDmobilierLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "BDMobilier";
    }

    @Override
    public void buildInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer, InvoiceAnnotModel modelAnnot) throws Exception {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        // set frequently accessed vars
        Random rnd = model.getRandom();
        Client client = model.getClient();
        Company company = model.getCompany();
        PaymentInfo payment = model.getPaymentInfo();
        ProductContainer pc = model.getProductContainer();
        String cur = pc.getCurrency();

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> genProb = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        IDNumbers idNumbers = model.getCompany().getIdNumbers();
        Address address = model.getCompany().getAddress();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDType1Fonts(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN : fontB;

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 10;
        float rightPageMargin = 35;

        Color lineStrokeColor = genProb.get("line_stroke_black") ? Color.BLACK: Color.BLUE;
        Color grayishFontColor = HelperCommon.getRandomColor(3);

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + model.getCompany().getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);

        /*//////////////////   Build Page components now   //////////////////*/

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        /// Draw top left logo
        float logoWidth = 100;
        float logoHeight = (logoImg.getHeight() * logoWidth) / logoImg.getWidth();
        float posLogoY = pageHeight - logoHeight - 20;
        contentStream.drawImage(logoImg, leftPageMargin, posLogoY, logoWidth, logoHeight);

        // Top right company header info
        VerticalContainer headerContainer = new VerticalContainer(0, pageHeight-9, 250);  // PosX is reset later for alignment
        headerContainer.addElement(new SimpleTextBox(fontNB,10,0,0,model.getCompany().getName(),"SN"));
        headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,model.getDate().getValueInvoice(),grayishFontColor,Color.WHITE,"IDATE"));

        // Purchase Order Number
        if (genProb.get("purchase_order_number_top")) {
            headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder(), "LO"));
        }
        HorizontalContainer numFact = new HorizontalContainer(0, 0);
        numFact.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelInvoice()+" ",grayishFontColor,Color.WHITE));
        numFact.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getValueInvoice(),grayishFontColor,Color.WHITE,"IN"));

        headerContainer.addElement(numFact);

        headerContainer.translate(pageWidth - headerContainer.getBoundingBox().getWidth() - rightPageMargin, 0);  // align top right header to fit properly
        headerContainer.build(contentStream, writer);

        // check if billing and shipping addresses should be switched
        float leftAddrX = 120 + rnd.nextInt(15);
        float rightAddrX = 335 + rnd.nextInt(15);
        if (genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight-110;
        float shipX = rightAddrX; float shipY = pageHeight-110;

        // Billing Address
        VerticalContainer billAddrContainer = new VerticalContainer(billX,billY,250);
        billAddrContainer.addElement(new SimpleTextBox(fontB,9, 0,0,model.getClient().getBillingHead(),grayishFontColor,Color.WHITE));
        billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getBillingName(),"BN"));
        billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getBillingAddress().getLine1(),"BA"));
        billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getBillingAddress().getZip()+" "+model.getClient().getBillingAddress().getCity(),"BA"));
        if (genProb.get("bill_address_phone_fax")) {
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingContactNumber().getPhoneLabel()+": "+model.getClient().getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingContactNumber().getFaxLabel()+": "+model.getClient().getBillingContactNumber().getFaxValue(), "BF"));
        } else {
            billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getBillingAddress().getCountry(),"BA"));
        }
        if (genProb.get("client_bill_address_tax_number")) {
            billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getIdNumbers().getVatLabel()+": "+model.getClient().getIdNumbers().getVatValue(),"BA"));
        }
        if (genProb.get("addresses_bordered") & model.getClient().getBillingHead().length() > 0) {
            billAddrContainer.setBorderColor(lineStrokeColor);
            billAddrContainer.setBorderThickness(0.5f);
        }

        billAddrContainer.build(contentStream,writer);

        // Shipping Address
        VerticalContainer shipAddrContainer = new VerticalContainer(shipX,shipY,250);
        shipAddrContainer.addElement(new SimpleTextBox(fontB,9,0,0,model.getClient().getShippingHead(),grayishFontColor,Color.WHITE));
        shipAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getShippingName(),"SHN"));
        shipAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getShippingAddress().getLine1(),"SHA"));
        shipAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getShippingAddress().getZip()+" "+model.getClient().getShippingAddress().getCity(),"SHA"));
        if (genProb.get("bill_address_phone_fax") & genProb.get("ship_address_phone_fax")) {
            String connec = (model.getClient().getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingContactNumber().getPhoneLabel()+connec+model.getClient().getShippingContactNumber().getPhoneValue(), "SHC"));
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingContactNumber().getFaxLabel()+connec+model.getClient().getShippingContactNumber().getFaxValue(), "SHF"));
        } else {
            shipAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getShippingAddress().getCountry(),"SHA"));
        }
        if (genProb.get("addresses_bordered") & model.getClient().getShippingHead().length() > 0) {
            shipAddrContainer.setBorderColor(lineStrokeColor);
            shipAddrContainer.setBorderThickness(0.5f);
        }

        shipAddrContainer.build(contentStream,writer);

        // Left side info
        VerticalContainer infoOrder = new VerticalContainer(leftPageMargin,pageHeight-211,76);
        // Purhase Order Number right if not at the top
        if (genProb.get("purchase_order_number_left") & !genProb.get("purchase_order_number_top")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,8,0,0,model.getReference().getLabelOrder()));
            infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder(),"PDATE"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        }
        infoOrder.addElement(new SimpleTextBox(fontNB,8, 0,0,model.getReference().getLabelOrder()));
        infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder(),"ONUM"));
        infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        // Payment Due
        if (genProb.get("payment_due_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,8,0,0,model.getDate().getLabelPaymentDue()));
            infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValuePaymentDue(),"PDATE"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        }
        infoOrder.addElement(new SimpleTextBox(fontNB,8,0,0,model.getPaymentInfo().getLabelPaymentType()));
        infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentType(),"PMODE"));
        infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getProductContainer().getFmtTotalWithTaxAndDiscount(),"TTX"));
        infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        // Payment Terms
        if (genProb.get("payment_terms_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,8,0,0,model.getPaymentInfo().getLabelPaymentTerm(), "PTL"));
            infoOrder.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentTerm(), "PTV"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        }
        infoOrder.build(contentStream,writer);

        // table top horizontal line, will be built after verticalTableItems
        float ttx1 = 110; float tty1 = billAddrContainer.getBoundingBox().getPosY() - billAddrContainer.getBoundingBox().getHeight() - 15;
        float ttx2 = pageWidth-rightPageMargin; float tty2 = tty1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(ttx1, tty1, ttx2, tty2, lineStrokeColor);

        ////////////////////////////////////      Building Table      ////////////////////////////////////
        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (genProb.get("currency_in_table_items")) {
              amtSuffix = " "+cur;
              modelAnnot.getTotal().setCurrency(cur);
        }
        // table text colors
        Color textColor = genProb.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // textColor black (predominantly) or white
        Color bgColor = (textColor == Color.WHITE) ? Color.BLACK: Arrays.asList(Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(rnd.nextInt(3)); // bgColor should be contrasting to textColor

        // Building Header Item labels, table values and footer labels list
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin;
        ProductTable pt = new ProductTable(pc, amtSuffix, tableWidth);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow2 = pt.getConfigRow();  // configRow values must add to tableWidth: 530 which is pageW - leftM - rightM
        Map<String, ProductTable.ColumnItem> itemMap = pt.getItemMap();

        // item list head
        float[] configRow = {209,56,45,70,71};
        TableRowBox row1 = new TableRowBox(configRow, 0, 0);

        row1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getNameHead(),textColor,bgColor), false);
        row1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getUPHead(),textColor,bgColor), false);
        row1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getDiscountHead(),textColor,bgColor), false);
        row1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getQtyHead(),textColor,bgColor), false);
        row1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getLineTotalHead(),textColor,bgColor), false);
        row1.setBackgroundColor(bgColor);

        TableRowBox row2 = new TableRowBox(configRow, 0, 0);
        row2.addElement(new SimpleTextBox(fontB, 9, 0, 0, "",textColor,bgColor), false);
        row2.addElement(new SimpleTextBox(fontB, 9, 0, 0, "",textColor,bgColor), false);
        row2.addElement(new SimpleTextBox(fontB, 9, 0, 0, "",textColor,bgColor), false);
        row2.addElement(new SimpleTextBox(fontB, 9, 0, 0, "",textColor,bgColor), false);
        row2.addElement(new SimpleTextBox(fontB, 9, 0, 0, "",textColor,bgColor), false);
        row2.setBackgroundColor(bgColor);

        VerticalContainer verticalInvoiceItems = new VerticalContainer(ttx1, tty1, 500);
        verticalInvoiceItems.addElement(row1);
        verticalInvoiceItems.addElement(row2);
        verticalInvoiceItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));

        String quantity;
        String discount = "";
        // item list body
        for(int w=0; w< model.getProductContainer().getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            textColor = Color.BLACK;
            bgColor = (randomProduct.getName().equalsIgnoreCase("shipping")) ? Color.LIGHT_GRAY: Color.WHITE;
            quantity = (randomProduct.getName().equalsIgnoreCase("shipping")) ? "": Float.toString(randomProduct.getQuantity());
            discount = (randomProduct.getDiscountRate() == 0.0) ? "--": randomProduct.getFmtTotalDiscount();

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(fontN, 9, 2, 0, randomProduct.getName(),textColor,bgColor,"PD"), false);
            productLine.addElement(new SimpleTextBox(fontN, 9, 2, 0, randomProduct.getFmtPrice(),textColor,bgColor,"UP"), false);
            productLine.addElement(new SimpleTextBox(fontN, 9, 2, 0, discount,textColor,bgColor,"DISC"), false);
            productLine.addElement(new SimpleTextBox(fontN, 9, 2, 0, randomProduct.getQuantity()+"",textColor,bgColor,"QTY"), false);
            productLine.addElement(new SimpleTextBox(fontN, 9, 2, 0, randomProduct.getFmtTotalPrice(),textColor,bgColor,"PTWTX"), false);

            verticalInvoiceItems.addElement(productLine);
        }
        verticalInvoiceItems.build(contentStream,writer);
        tableTopInfoLine.build(contentStream,writer);

        float posYTotal = verticalInvoiceItems.getBoundingBox().getPosY()-verticalInvoiceItems.getBoundingBox().getHeight()-13;
        new BorderBox(bgColor,bgColor,1,405,posYTotal-33,158,45).build(contentStream,writer);
        // new BorderBox(Color.BLACK,Color.BLACK,1,405,posYTotal-13,158,13).build(contentStream,writer);
        new HorizontalLineBox(ttx1, posYTotal+13, ttx2, posYTotal+13, lineStrokeColor).build(contentStream,writer);

        // Totals and Taxes calculations
        new SimpleTextBox(fontN, 9, 410, posYTotal+11, pc.getTotalHead(),textColor,bgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 508, posYTotal+11, pc.getFmtTotal(),textColor,bgColor,"TWTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 410, posYTotal-2, pc.getTaxTotalHead(),textColor,bgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 508, posYTotal-2, pc.getFmtTotalTax(),textColor,bgColor,"TTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 410, posYTotal-16, pc.getWithTaxTotalHead(),textColor,bgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 508, posYTotal-16, pc.getFmtTotalWithTax(),textColor,bgColor,"TA").build(contentStream,writer);

        // Footer company info
        int footerFontSize = 7 + rnd.nextInt(3);
        HorizontalContainer infoEntreprise = new HorizontalContainer(0,0);
        infoEntreprise.addElement(new SimpleTextBox(fontNB,footerFontSize,0,0, model.getCompany().getName(),"SN"));
        infoEntreprise.addElement(new SimpleTextBox(fontNB,footerFontSize,0,0, " - "));
        infoEntreprise.addElement(new SimpleTextBox(fontNB,footerFontSize,0,0, address.getCountry(),"SA"));

        HorizontalContainer infoEntreprise2 = new HorizontalContainer(0,0);
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, address.getLine1()+" ","SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " - "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, address.getZip() + " " +address.getCity(),"SA"));
        if (model.getLang() == "fr") {
            infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " "+idNumbers.getSiretLabel()+" "));
            infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, idNumbers.getSiretValue(),"SSIRET"));
        }
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " - "+ idNumbers.getVatLabel() +" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, idNumbers.getVatValue(),"SVAT"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " - "+model.getCompany().getContact().getFaxLabel()+" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, model.getCompany().getContact().getFaxValue(),"SFAX"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, model.getCompany().getContact().getPhoneLabel()+" : "));
        infoEntreprise3.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, model.getCompany().getContact().getPhoneValue(),"SCN"));

        infoEntreprise.translate(pageMiddleX-infoEntreprise.getBoundingBox().getWidth()/2,61);
        infoEntreprise2.translate(pageMiddleX-infoEntreprise2.getBoundingBox().getWidth()/2,53);
        infoEntreprise3.translate(pageMiddleX-infoEntreprise3.getBoundingBox().getWidth()/2,45);

        infoEntreprise.build(contentStream,writer);
        infoEntreprise2.build(contentStream,writer);
        infoEntreprise3.build(contentStream,writer);

        // Add Signature at bottom
        if (genProb.get("signature_bottom")) {
              String compSignatureName = model.getCompany().getName();
              compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
              SimpleTextBox singatureTextBox = new SimpleTextBox(
                      fontN, 8, 0, 130,
                      model.getCompany().getSignature().getLabel()+" "+compSignatureName, "Signature");

              float singatureTextxPos;
              if (genProb.get("signature_bottom_left")) {  // bottom left
                  singatureTextxPos = leftPageMargin + 25;
              } else {                                     // bottom right
                  singatureTextxPos = pageWidth - singatureTextBox.getBoundingBox().getWidth() - 50;
              }

              singatureTextBox.getBoundingBox().setPosX(singatureTextxPos);
              singatureTextBox.build(contentStream, writer);
              new HorizontalLineBox(
                      singatureTextxPos - 10, 135,
                      singatureTextxPos + singatureTextBox.getBoundingBox().getWidth() + 10, 135
                      ).build(contentStream, writer);
              String signaturePath = HelperCommon.getResourceFullPath(this, "common/signature/" + model.getCompany().getSignature().getFullPath());
              PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
              int signatureWidth = 120;
              int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
              // align signature to center of singatureTextBox bbox
              float signatureXPos = singatureTextBox.getBoundingBox().getPosX() + singatureTextBox.getBoundingBox().getWidth()/2 - signatureWidth/2;
              float signatureYPos = 140;
              contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add company stamp watermark, 40% prob
        if (genProb.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + model.getCompany().getStamp().getFullPath());
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f; float maxAStamp = 0.8f;
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp; float yPosStamp;
            // draw to lower right if signature if present
            if (genProb.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = ((genProb.get("signature_bottom_left")) ? leftPageMargin + 5 : 405) + rnd.nextInt(10);
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
                stampWidth += rnd.nextInt(20);
                stampHeight = (stampWidth * stampImg.getHeight()) / stampImg.getWidth();
            }
            else if (genProb.get("stamp_bottom_elongated")) {
                // elongate stamps if the stamp is a not a Rectangular one
                // and set rotation to 0
                rotAngle = 0;
                stampWidth = stampWidth + 50;
                stampHeight = stampHeight - 10;
            }
            HelperImage.addWatermarkImagePDF(document, page, stampImg, xPosStamp, yPosStamp,
                                               stampWidth, stampHeight, minAStamp, maxAStamp, rotAngle);
        }
        // if no signature and no stamp, then add a footer note
        else if (!genProb.get("signature_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or the Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignMsgBox = new SimpleTextBox(fontN, footerFontSize-1, 0, 80, noStampSignMsg, "Footnote");
            // align the text to the center
            noStampSignMsgBox.getBoundingBox().setPosX(pageMiddleX - noStampSignMsgBox.getBoundingBox().getWidth()/2);
            noStampSignMsgBox.build(contentStream, writer);
        }

        // Add bg logo watermark or confidential stamp, but not both at once
        if (genProb.get("confidential_watermark")) {
            // Add confidential watermark
            HelperImage.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "Confidential");
        }
        else if (genProb.get("logo_watermark")) {
            // Add watermarked background logo
            HelperImage.addWatermarkImagePDF(document, page, logoImg);
        }

        contentStream.close();
        writer.writeEndElement();
    }
}
