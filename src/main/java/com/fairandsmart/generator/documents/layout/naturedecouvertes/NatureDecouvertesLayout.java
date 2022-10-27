package com.fairandsmart.generator.documents.layout.naturedecouvertes;

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
 * Copyright (C) 2019 - 2020 Fair And Smart
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
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.ProductTable;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.ContactNumber;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.VAlign;

import com.mifmif.common.regex.Generex;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.Color;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


public class NatureDecouvertesLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Nature&Decouvertes";
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

        // get barcode number
        String barcodeNum = model.getReference().getValueBarcode();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        boolean upperCap = rnd.nextBoolean();

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 30;
        float rightPageMargin = 30;
        float topPageMargin = 10;
        float bottomPageMargin = 10;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.min((int)(v*1.9f), 255)).collect(Collectors.toList()); // lighten colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = genProb.get("line_stroke_black") ? black: themeColor;

        // always set to false but individually change SimpleTextBox HAlign
        boolean centerAlignItems = false;
        float ratioPage = 0.24f; // pageWidth/2480;
        // pageHeight 841.8898, pageWidth 595.27563

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

        // Logo top
        if (genProb.get("logo_top")) {
            maxLogoWidth = 150;
            maxLogoHeight = 90;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = leftPageMargin;
            posLogoY = pageHeight-logoHeight-topPageMargin;
            contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);
        } else {  // Barcode top
            new ImageBox(barcodeImg, leftPageMargin, pageHeight-topPageMargin, barcodeImg.getWidth(), (float)(barcodeImg.getHeight() / 1.5), barcodeNum).build(contentStream, writer);
        }

        float topAddrX = 307;
        float topAddrY = pageHeight - topPageMargin;
        // company/vendor info top (switch with billing address sometimes)
        if (genProb.get("vendor_address_top")) {
            VerticalContainer vendorAddrCont = new VerticalContainer(topAddrX,topAddrY,250);
            vendorAddrCont.addElement(new SimpleTextBox(fontB,12,0,0,company.getName(),"SN"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getLine1(),"SA"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getZip()+" "+company.getAddress().getCity(),"SA"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getCountry(),"SA"));
            if (genProb.get("vendor_address_phone_fax")) {
                vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, company.getContact().getPhoneLabel()+": "+company.getContact().getPhoneValue(), "SC"));
                vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, company.getContact().getFaxLabel()+": "+company.getContact().getFaxValue(), "SF"));
            }
            else if (genProb.get("vendor_address_tax_number")) {
                String vatText = company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue();
                vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,vatText, "SVAT"));
                modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
            }
            if (genProb.get("addresses_bordered")) {
                vendorAddrCont.setBorderColor(lineStrokeColor);
                vendorAddrCont.setBorderThickness(0.5f);
            }
            modelAnnot.getVendor().setVendorName(company.getName());
            modelAnnot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity());
            modelAnnot.getVendor().setVendorPOBox(company.getAddress().getZip());
            vendorAddrCont.build(contentStream,writer);
        }
        else { // add the payment info address
            float paymentAddrXPos = topAddrX;
            float paymentAddrYPos = topAddrY;

            VerticalContainer paymentAddrCont = new VerticalContainer(paymentAddrXPos, paymentAddrYPos, 300);

            paymentAddrCont.addElement(new SimpleTextBox(fontB, 10, 0, 0, payment.getAddressHeader()+":", "PH"));

            HorizontalContainer bankName = new HorizontalContainer(0,0);
            bankName.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelBankName()+": ", "PBN"));
            bankName.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueBankName(), "PBN"));
            paymentAddrCont.addElement(bankName);
            modelAnnot.getPaymentto().setBankName(payment.getValueBankName());

            HorizontalContainer accountName = new HorizontalContainer(0,0);
            accountName.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelAccountName()+": ", "PAName"));
            accountName.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueAccountName(), "PAName"));
            paymentAddrCont.addElement(accountName);
            modelAnnot.getPaymentto().setAccountName(payment.getValueAccountName());

            if (genProb.get("payment_account_number")) {
                HorizontalContainer accountNumber = new HorizontalContainer(0,0);
                accountNumber.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelAccountNumber()+": ", "PANum"));
                accountNumber.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueAccountNumber(), "PANum"));
                paymentAddrCont.addElement(accountNumber);
                modelAnnot.getPaymentto().setAccountNumber(payment.getValueAccountNumber());
            }
            if (genProb.get("payment_branch_name")) {
                HorizontalContainer branchName = new HorizontalContainer(0,0);
                branchName.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelBranchName()+": ", "PBName"));
                branchName.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueBranchName(), "PBName"));
                paymentAddrCont.addElement(branchName);
                modelAnnot.getPaymentto().setBranchAddress(payment.getValueBranchName());
            }

            HorizontalContainer ibanNumber = new HorizontalContainer(0,0);
            ibanNumber.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelIBANNumber()+": ", "PBNum"));
            ibanNumber.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueIBANNumber(), "PBNum"));
            paymentAddrCont.addElement(ibanNumber);
            modelAnnot.getPaymentto().setIbanNumber(payment.getValueIBANNumber());

            if (genProb.get("payment_routing_number")) {
                HorizontalContainer routingNumber = new HorizontalContainer(0,0);
                routingNumber.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelRoutingNumber()+": ", "PBNum"));
                routingNumber.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueRoutingNumber(), "PBNum"));
                paymentAddrCont.addElement(routingNumber);
                modelAnnot.getPaymentto().setRoutingNumber(payment.getValueRoutingNumber());
            }
            if (genProb.get("payment_swift_number")) {
                HorizontalContainer swiftCode = new HorizontalContainer(0,0);
                swiftCode.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelSwiftCode()+": ", "PSNum"));
                swiftCode.addElement(new SimpleTextBox(fontN,9,0,0, payment.getValueSwiftCode(), "PSNum"));
                paymentAddrCont.addElement(swiftCode);
                modelAnnot.getPaymentto().setSwiftCode(payment.getValueSwiftCode());
            }
            if (genProb.get("payment_vendor_tax_number")) {
                HorizontalContainer vatNumber = new HorizontalContainer(0,0);
                vatNumber.addElement(new SimpleTextBox(fontNB,9,0,0, company.getIdNumbers().getVatLabel()+": ", "SVAT"));
                vatNumber.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getVatValue(), "SVAT"));
                paymentAddrCont.addElement(vatNumber);
                modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
            }
            if (genProb.get("addresses_bordered")) {
                paymentAddrCont.setBorderColor(lineStrokeColor);
                paymentAddrCont.setBorderThickness(0.5f);
            }
            paymentAddrCont.build(contentStream, writer);
        }

        // check if billing and shipping addresses should be switched
        float leftAddrX = leftPageMargin;
        float rightAddrX = topAddrX;
        if (genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight - topPageMargin - 100;
        float shipX = rightAddrX; float shipY = billY;

        // billing address
        Address bAddr = client.getBillingAddress();
        ContactNumber bCN = client.getBillingContactNumber();
        VerticalContainer billAddrCont = new VerticalContainer(billX,billY,250);
        billAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,client.getBillingHead(),"BH"));
        billAddrCont.addElement(new SimpleTextBox(fontB,11,0,0,client.getBillingName().toUpperCase(),"BN"));
        billAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,bAddr.getLine1().toUpperCase(),"BA"));
        billAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,bAddr.getZip()+" "+bAddr.getCity().toUpperCase(),"BA"));
        if (genProb.get("bill_address_phone_fax")) {
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bCN.getPhoneLabel()+": "+bCN.getPhoneValue(), "BC"));
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bCN.getFaxLabel()+": "+bCN.getFaxValue(), "BF"));
        }
        else if (genProb.get("bill_address_tax_number")) {
            billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BT"));
            modelAnnot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        if (genProb.get("addresses_bordered") && client.getBillingHead().length() > 0) {
            billAddrCont.setBorderColor(lineStrokeColor);
            billAddrCont.setBorderThickness(0.5f);
        }
        modelAnnot.getBillto().setCustomerName(client.getBillingName().toUpperCase());
        modelAnnot.getBillto().setCustomerAddr(bAddr.getLine1().toUpperCase()+" "+bAddr.getZip()+" "+bAddr.getCity().toUpperCase());
        modelAnnot.getBillto().setCustomerPOBox(bAddr.getZip());
        billAddrCont.build(contentStream,writer);

        // shipping address
        Address sAddr = client.getShippingAddress();
        ContactNumber sCN = client.getShippingContactNumber();
        VerticalContainer shipAddrCont = new VerticalContainer(shipX,shipY,250);
        shipAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,client.getShippingHead(),"SHH"));
        shipAddrCont.addElement(new SimpleTextBox(fontB,11,0,0,client.getShippingName().toUpperCase(),"SHN"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,sAddr.getLine1().toUpperCase(),"SHA"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,10,0,0,sAddr.getZip()+" "+sAddr.getCity().toUpperCase(),"SHA"));
        if (genProb.get("bill_address_phone_fax") && genProb.get("ship_address_phone_fax")) {
            String connec = (sCN.getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sCN.getPhoneLabel()+connec+sCN.getPhoneValue(), "BC"));
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sCN.getFaxLabel()+connec+sCN.getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") && client.getShippingHead().length() > 0) {
            shipAddrCont.setBorderColor(lineStrokeColor);
            shipAddrCont.setBorderThickness(0.5f);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            modelAnnot.getShipto().setShiptoName(client.getShippingName().toUpperCase());
            if (sCN.getPhoneLabel().length() > 0) {
                modelAnnot.getShipto().setShiptoPOBox(sAddr.getZip());
                modelAnnot.getShipto().setShiptoAddr(sAddr.getLine1()+" "+sAddr.getZip()+" "+sAddr.getCity());
            }
        }
        shipAddrCont.build(contentStream,writer);

        // table top order num, order id, invoice id, inv date and due date info
        float tableTopY = billAddrCont.getBBox().getPosY() - billAddrCont.getBBox().getHeight() - 30;

        if (genProb.get("currency_top")) {
            new SimpleTextBox(fontB,12, leftPageMargin,tableTopY, payment.getLabelAccountCurrency()+": "+cur,"CUR").build(contentStream,writer);
            modelAnnot.getTotal().setCurrency(cur);
        }
        else if (genProb.get("purchase_order_number_top")) {
            new SimpleTextBox(fontB,12, leftPageMargin,tableTopY, model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder(),"ONUM").build(contentStream,writer);
            modelAnnot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());

            new SimpleTextBox(fontB,12, leftPageMargin,tableTopY-18, model.getDate().getLabelPaymentDue()+": "+model.getDate().getValuePaymentDue(),"DDATE").build(contentStream,writer);
            modelAnnot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        if (genProb.get("invoice_number_top")) {
            new SimpleTextBox(fontB,12, topAddrX,tableTopY, model.getReference().getLabelInvoice()+": "+model.getReference().getValueInvoice(),"IN").build(contentStream,writer);
            modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());

            new SimpleTextBox(fontB,12, topAddrX,tableTopY-18, model.getDate().getLabelInvoice()+": "+model.getDate().getValueInvoice(),"IN").build(contentStream,writer);
            modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        }
        ////////////////////////////////////      Building Table      ////////////////////////////////////

        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (genProb.get("currency_in_table_items")) {
            amtSuffix = " "+cur;
            modelAnnot.getTotal().setCurrency(cur);
        }
        HAlign tableHdrAlign = genProb.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;

        // Building Header Item labels, table values and footer labels list
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin;
        int maxHdrNum = 14;
        ProductTable pt = new ProductTable(pc, amtSuffix, model.getLang(), tableWidth, maxHdrNum);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow2 = pt.getConfigRow();
        Map<String, ProductTable.ColItem> itemMap = pt.getItemMap();

        // table header text colors
        Color hdrTextColor = genProb.get("table_hdr_black_text") ? black: white; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == white) ? black: Arrays.asList(Color.GRAY, Color.LIGHT_GRAY, white).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        float[] configRow = {236*ratioPage,936*ratioPage,115*ratioPage,112*ratioPage,230*ratioPage,212*ratioPage,290*ratioPage};

        // table item list head
        TableRowBox row1 = new TableRowBox(configRow, 0, 0);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Product reference", black,null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Designation", black, null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Qty Order", black, null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "VAT", black, null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Unit Price ", black, null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Delivered quantity", black, null), true);
        row1.addElement(new SimpleTextBox(fontB, 8, 0, 0, "Total Price", black,null), true);

        VerticalContainer verticalTableItems = new VerticalContainer(leftPageMargin, pageHeight-1046*ratioPage, tableWidth);
        new BorderBox(black,white, 1,leftPageMargin, pageHeight-2462*ratioPage, tableWidth,1418*ratioPage ).build(contentStream,writer);
        new BorderBox(black,grayish, 1,leftPageMargin, pageHeight-1158*ratioPage, tableWidth,118*ratioPage ).build(contentStream,writer);
        HelperImage.drawPolygon(
                contentStream,
                new float[] {leftPageMargin,2303*ratioPage,
                             2303*ratioPage, leftPageMargin},
                new float[] {pageHeight-2596*ratioPage,pageHeight-2596*ratioPage,
                             pageHeight-1044*ratioPage,pageHeight-1044*ratioPage});
        HelperImage.drawPolygon(
                contentStream,
                new float[] {leftPageMargin,2009*ratioPage,
                             2009*ratioPage, leftPageMargin},
                new float[] {pageHeight-2596*ratioPage,pageHeight-2596*ratioPage,
                             pageHeight-1044*ratioPage,pageHeight-1044*ratioPage});
        HelperImage.drawLine(contentStream, 406*ratioPage,pageHeight-1042*ratioPage, 406*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1342*ratioPage,pageHeight-1042*ratioPage, 1342*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1576*ratioPage,pageHeight-1042*ratioPage, 1576*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1467*ratioPage,pageHeight-1042*ratioPage, 1467*ratioPage,pageHeight-2596*ratioPage);
        HelperImage.drawLine(contentStream, 1804*ratioPage,pageHeight-1042*ratioPage, 1804*ratioPage,pageHeight-2462*ratioPage);
        verticalTableItems.addElement(row1);

        for(int w=0; w< pc.getProducts().size(); w++) {
              Product randomProduct = pc.getProducts().get(w);

              TableRowBox productLine = new TableRowBox(configRow, 0, -10);
              productLine.addElement(new SimpleTextBox(fontN, 7, 2, 0, randomProduct.getEan(), "SNO"), true);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getName(), "PD"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getTaxRate() * 100 + "%", "TXR"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtPrice(), "PTWTX"), false);
              float puttc = (float)(int)((randomProduct.getPrice() + randomProduct.getPrice() * randomProduct.getTaxRate())*100)/100;
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, puttc + "", "UP"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtTotalPriceWithTax(), "PTTX"), false);

              verticalTableItems.addElement(productLine);
        }

        verticalTableItems.build(contentStream, writer);

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        VerticalContainer paymentTypeContainer = new VerticalContainer(1476*ratioPage,pageHeight-2479*ratioPage,250);
        paymentTypeContainer.addElement(new SimpleTextBox(fontB,11,0,0,"TOTAL T.T.C"));
        paymentTypeContainer.addElement(new SimpleTextBox(fontN,8,0,0,"Regulated by : "+payment.getLabelPaymentType(),"PT"));
        paymentTypeContainer.build(contentStream,writer);

        VerticalContainer totalContainer = new VerticalContainer(2019*ratioPage,pageHeight-2474*ratioPage,250);
        totalContainer.addElement(new SimpleTextBox(fontN,11,0,0,pc.getFmtTotalWithTax(),"TA"));
        totalContainer.build(contentStream,writer);

        // text above barcode bottom left
        VerticalContainer backContainer = new VerticalContainer(82, bottomPageMargin+165, 150);
        backContainer.addElement(new SimpleTextBox(fontN,10,0,0,"Label to stick on your package in case of return. "));
        backContainer.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder()));
        backContainer.build(contentStream,writer);

        // barcode bottom left
        new ImageBox(barcodeImg, 85, backContainer.getBBox().getPosY()-backContainer.getBBox().getHeight()-3, 100, 40, barcodeNum).build(contentStream, writer);

        // footer table with total amt + total taxFCont info
        float[] footerConfigRow = {46,55,45,60};
        float tFX = 347, tFY = bottomPageMargin + 160;
        // new BorderBox(black,white, 1,1440*ratioPage, pageHeight-3000*ratioPage, 900*ratioPage,228*ratioPage).build(contentStream,writer);
        // new BorderBox(black,grayish, 1,1440*ratioPage, pageHeight-2890*ratioPage, 900*ratioPage,118*ratioPage).build(contentStream,writer);

        VerticalContainer taxFCont = new VerticalContainer(tFX, tFY, 220);

        // tax+total label heads
        TableRowBox taxLabelRow = new TableRowBox(footerConfigRow,0,0,VAlign.CENTER);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, "VAT Code", black,null), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTotalHead(), black,null), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTaxRateHead(), black,null), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTaxTotalHead(), black,null), false);
        taxFCont.addElement(taxLabelRow);

        float taxFW = taxFCont.getBBox().getWidth();
        float taxFH = taxFCont.getBBox().getHeight();
        new BorderBox(black,grayish,1,tFX,taxFCont.getBBox().getPosY()-taxFH-10,taxFW,taxFH).build(contentStream,writer);

        taxFCont.addElement(new BorderBox(white,white,0,0,0,0,10));
        // contentStream.drawLine(1648*ratioPage,pageHeight-2772*ratioPage, 1648*ratioPage,pageHeight-3000*ratioPage);
        // contentStream.drawLine(1900*ratioPage,pageHeight-2772*ratioPage, 1900*ratioPage,pageHeight-3000*ratioPage);
        // contentStream.drawLine(2064*ratioPage,pageHeight-2772*ratioPage, 2064*ratioPage,pageHeight-3000*ratioPage);

        // tax+total value heads
        TableRowBox taxValueRow = new TableRowBox(footerConfigRow,0,0,VAlign.CENTER);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, new Generex("[0-9]{5}").random(), black,null), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotal(), black,null), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotalTaxRate(), black,null), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotalTax(), black,null), false);
        taxFCont.addElement(taxValueRow);

        taxFCont.build(contentStream, writer);

        // footer company name, info, address & contact information
        HorizontalContainer infoEntreprise1 = new HorizontalContainer(0,0);
        infoEntreprise1.addElement(new SimpleTextBox(fontN,7,0,0, company.getName(),"SN"));
        infoEntreprise1.addElement(new SimpleTextBox(fontN,7,0,0, " - "));
        infoEntreprise1.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getCountry(),"SA"));

        HorizontalContainer infoEntreprise2 = new HorizontalContainer(0,0);
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getLine1()+" ","SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getZip() + " " +company.getAddress().getCity(),"SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " "+company.getIdNumbers().getSiretLabel()+" "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getIdNumbers().getSiretValue(),"SSIRET"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "+ company.getIdNumbers().getVatLabel() +" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getIdNumbers().getVatValue(),"SVAT"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "+company.getContact().getFaxLabel()+" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getFaxValue(),"SFAX"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getPhoneLabel()+" : "));
        infoEntreprise3.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getPhoneValue(),"SCN"));

        infoEntreprise1.translate(pageMiddleX-infoEntreprise1.getBBox().getWidth()/2,58);
        infoEntreprise2.translate(pageMiddleX-infoEntreprise2.getBBox().getWidth()/2,51);
        infoEntreprise3.translate(pageMiddleX-infoEntreprise3.getBBox().getWidth()/2,45);

        infoEntreprise1.build(contentStream,writer);
        infoEntreprise2.build(contentStream,writer);
        infoEntreprise3.build(contentStream,writer);

        contentStream.close();
        writer.writeEndElement();
      }
  }
