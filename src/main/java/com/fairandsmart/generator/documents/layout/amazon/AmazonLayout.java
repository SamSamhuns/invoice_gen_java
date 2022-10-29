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
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ContactNumber;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.footer.SignatureBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
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
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        new BorderBox(HelperCommon.getRandomColor(6), white, 4, 0, 0, pageWidth, pageHeight).build(contentStream, writer);

        // Barcode top
        if (proba.get("barcode_top")) {
            new ImageBox(barcodeImg, pageWidth / 2, 810, barcodeImg.getWidth(), (float)(barcodeImg.getHeight() / 1.5), barcodeNum).build(contentStream, writer);
        }
        // Or Logo top
        else if (proba.get("logo_top")) {
            maxLogoWidth = 150;
            maxLogoHeight = 90;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = pageWidth-logoWidth-rightPageMargin;
            posLogoY = pageHeight-logoHeight-rightPageMargin;
            contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);
        }

        // Text top
        VerticalContainer topTextCont = new VerticalContainer(leftPageMargin, 810, 500);
        topTextCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, "Page 1 of 1" + ((rnd.nextBoolean()) ? ", 1-1/1": ".")));
        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        String textTopInvString = ((rnd.nextBoolean()) ? docTitle+" for ": docTitle+" dated ")+model.getDate().getValueInvoice();
        if (proba.get("text_top_invoice_number") && !proba.get("invoice_number_top")) {  // inc invoice number if not mentioned below
            textTopInvString = model.getReference().getLabelInvoice()+" "+model.getReference().getValueInvoice()+" for "+model.getDate().getValueInvoice();
            annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        }
        topTextCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, textTopInvString, docTitle+" Number"));
        topTextCont.addElement(new SimpleTextBox(fontNB, 10, 0, 0, ((rnd.nextBoolean()) ? "Retail / "+docTitle+" / Cash Memorandum": "Retail / "+docTitle) ));
        if (proba.get("text_top_center") && !proba.get("barcode_top")) {  // center top text if barcode not present
            topTextCont.alignElements("CENTER", topTextCont.getBBox().getWidth());
            topTextCont.translate(pageMiddleX - topTextCont.getBBox().getWidth()/2, 0);
        }
        annot.setTitle(docTitle);
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        topTextCont.build(contentStream, writer);

        // Vendor/Company Address
        VerticalContainer vendorAddrCont = new VerticalContainer(leftPageMargin, 761, 300);
        vendorAddrCont.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 10, 0, 0, company.getAddressHeader(), "SH"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getName(), "SN"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getLine1(), "SA" ));
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getZip()+" "+company.getAddress().getCity(), "SA"));
        if (proba.get("vendor_address_phone_fax")) {
            vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getContact().getPhoneLabel()+": "+company.getContact().getPhoneValue(), "SC"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getContact().getFaxLabel()+": "+company.getContact().getFaxValue(), "SF"));
        }
        if (proba.get("addresses_bordered")) {
            vendorAddrCont.setBorderColor(lineStrokeColor);
            vendorAddrCont.setBorderThickness(0.5f);
        }
        annot.getVendor().setVendorName(company.getName());
        annot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity());
        annot.getVendor().setVendorPOBox(company.getAddress().getZip());
        vendorAddrCont.build(contentStream, writer);

        float leftTopInfoY = 670; // Progressively inc by 10 pts after each left-side box
        // if no "vendor_address_phone_fax" then Purchase Order Number, Left side
        if (proba.get("purchase_order_number_top") && !proba.get("vendor_address_phone_fax")) {
            String purchaseOrderText = model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, purchaseOrderText, "LO").build(contentStream, writer);
            leftTopInfoY += 10;
            annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // TAX number, Left side
        if (proba.get("vendor_tax_number_top")) {
            String vatText = company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, vatText, "SVAT").build(contentStream, writer);
            annot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }

        float rightTopInfoY = 670; // Progressively inc by 10 pts after each right-side box, building from bottom
        // Currency Used, Right side
        if (proba.get("currency_top")) {
            String currencyText = payment.getLabelAccountCurrency()+": "+cur;
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, currencyText, "CUR").build(contentStream, writer);
            rightTopInfoY += 10;
            annot.getTotal().setCurrency(cur);
        }
        // Payment Terms, Right side
        if (proba.get("payment_terms_top")) {
            String paymentTermText = payment.getLabelPaymentTerm()+": "+payment.getValuePaymentTerm();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentTermText, "PT").build(contentStream, writer);
            rightTopInfoY += 10;
            annot.getInvoice().setPaymentTerm(payment.getValuePaymentTerm());
        }
        // Payment Due Date if Payment Terms is not mentioned
        else if (proba.get("payment_due_top")) {
            String paymentDueText = model.getDate().getLabelPaymentDue()+": "+model.getDate().getValuePaymentDue();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentDueText, "PT").build(contentStream, writer);
            rightTopInfoY += 10;
            annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        // invoice number, Right side
        if (proba.get("invoice_number_top")) {
            String invoiceText = model.getReference().getLabelInvoice() + ": " +model.getReference().getValueInvoice();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, invoiceText, "INV").build(contentStream, writer);
            annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        }

        HelperImage.drawLine(contentStream, leftPageMargin, 650, pageWidth-rightPageMargin, 650, lineStrokeColor);

        // check if billing and shipping addresses should be switched
        float leftAddrX = leftPageMargin;
        float rightAddrX = pageWidth/2 + rnd.nextInt(5);
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = 645;
        float shipX = rightAddrX; float shipY = 645;

        // Billing Address
        Address bAddr = client.getBillingAddress();
        ContactNumber bCN = client.getBillingContactNumber();;
        VerticalContainer billAddrCont = new VerticalContainer(billX, billY, 250);
        billAddrCont.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getBillingHead(), "BH" ));
        billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingName(), "BN" ));
        billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bAddr.getLine1(), "BA" ));
        billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bAddr.getZip()+" "+bAddr.getCity(), "BA" ));
        if (proba.get("bill_address_phone_fax")) {
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bCN.getPhoneLabel()+": "+bCN.getPhoneValue(), "BC"));
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bCN.getFaxLabel()+": "+bCN.getFaxValue(), "BF"));
        }
        else if (proba.get("bill_address_tax_number")) {
            billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BT"));
            annot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        if (proba.get("addresses_bordered") && client.getBillingHead().length() > 0) {
            billAddrCont.setBorderColor(lineStrokeColor);
            billAddrCont.setBorderThickness(0.5f);
        }
        annot.getBillto().setCustomerName(client.getBillingName());
        annot.getBillto().setCustomerAddr(bAddr.getLine1()+" "+bAddr.getZip()+" "+bAddr.getCity());
        annot.getBillto().setCustomerPOBox(bAddr.getZip());
        billAddrCont.build(contentStream, writer);

        // Shipping Address
        Address sAddr = client.getShippingAddress();
        ContactNumber sCN = client.getShippingContactNumber();
        VerticalContainer shipAddrCont = new VerticalContainer(shipX, shipY, 250);
        shipAddrCont.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getShippingHead(), "SHH" ));
        shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingName(), "SHN" ));
        shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sAddr.getLine1(), "SHA" ));
        shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sAddr.getZip()+" "+sAddr.getCity(), "SHA" ));
        if (proba.get("bill_address_phone_fax") && proba.get("ship_address_phone_fax")) {
            String connec = (sCN.getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sCN.getPhoneLabel()+connec+sCN.getPhoneValue(), "BC"));
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sCN.getFaxLabel()+connec+sCN.getFaxValue(), "BF"));
        }
        if (proba.get("addresses_bordered") && client.getShippingHead().length() > 0) {
            shipAddrCont.setBorderColor(lineStrokeColor);
            shipAddrCont.setBorderThickness(0.5f);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            annot.setShipto(new InvoiceAnnotModel.Shipto());
            annot.getShipto().setShiptoName(client.getShippingName());
            if (sCN.getPhoneLabel().length() > 0) {
                annot.getShipto().setShiptoPOBox(sAddr.getZip());
                annot.getShipto().setShiptoAddr(sAddr.getLine1()+" "+sAddr.getZip()+" "+sAddr.getCity());
            }
        }
        shipAddrCont.build(contentStream, writer);

        ////////////////////////////////////      Building Table      ////////////////////////////////////

        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (proba.get("currency_in_table_items")) {
            amtSuffix = " "+cur;
            annot.getTotal().setCurrency(cur);
        }
        boolean upperCap = rnd.nextBoolean();  // table header items case
        HAlign tableHdrAlign = proba.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;

        // Building Header Item labels, table values and footer labels list
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin;
        ProductTable pt = new ProductTable(pc, amtSuffix, model.getLang(), tableWidth);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow = pt.getConfigRow();
        Map<String, ProductTable.ColItem> itemMap = pt.getItemMap();

        // table header text colors
        Color hdrTextColor = proba.get("table_hdr_black_text") ? black: white; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == white) ? black: Arrays.asList(Color.GRAY, lgray, white).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        // table top info
        String tableTopText = pt.getTableTopInfo();
        tableTopText = tableTopText.equals("All prices are in")? tableTopText+" "+cur: tableTopText;
        tableTopText = (hdrBgColor == white) ? "" : tableTopText;
        float tableTopPosX = leftPageMargin;
        float tableTopPosY = billAddrCont.getBBox().getPosY() - billAddrCont.getBBox().getHeight() - 15;

        SimpleTextBox tableTopBox = new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 9, tableTopPosX, tableTopPosY, tableTopText);
        tableTopBox.build(contentStream, writer);

        // table top horizontal line, will be built after verticalTableItems
        float x1 = leftPageMargin; float y1 = tableTopBox.getBBox().getPosY() - tableTopBox.getBBox().getHeight() - 2;
        float x2 = pageWidth-rightPageMargin; float y2 = y1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(x1, y1, x2, y2, lineStrokeColor);

        // table item list head
        TableRowBox row1 = new TableRowBox(configRow, 0, 0);
        for (String tableHeader: tableHeaders) {
            String hdrLabel = itemMap.get(tableHeader).getLabelHeader();
            String tableHdrLabel = upperCap ? hdrLabel.toUpperCase() : hdrLabel;
            // if numerical header used, check if cur needs to appended at the end
            if (proba.get("currency_in_table_headers") && !proba.get("currency_in_table_items") && pt.getNumericalHdrs().contains(tableHeader)) {
                tableHdrLabel += " ("+cur+")";
            }
            row1.addElement(new SimpleTextBox(fontNB, 8, 0, 0, tableHdrLabel, hdrTextColor, hdrBgColor, tableHdrAlign, hdrLabel+"HeaderLabel"), false);
        }
        row1.setBackgroundColor(hdrBgColor);

        VerticalContainer verticalTableItems = new VerticalContainer(leftPageMargin, tableTopPosY - tableTopBox.getBBox().getHeight() - 2, 600);
        verticalTableItems.addElement(row1);
        verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));

        new BorderBox(hdrBgColor, hdrBgColor, 0,
                      leftPageMargin, tableTopPosY - tableTopBox.getBBox().getHeight() - 2 - row1.getBBox().getHeight(),
                      row1.getBBox().getWidth(), row1.getBBox().getHeight()).build(contentStream, writer);

        // table item list body
        String quantity; String snNum;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            cellTextColor = black;
            cellBgColor = (randomProduct.getName().equalsIgnoreCase("shipping")) ? lgray: white;
            quantity = (randomProduct.getName().equalsIgnoreCase("shipping")) ? "": Float.toString(randomProduct.getQuantity());
            snNum = (randomProduct.getName().equalsIgnoreCase("shipping")) ? "": Integer.toString(w + 1);

            InvoiceAnnotModel.Item randomItem = new InvoiceAnnotModel.Item();
            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String cellText = "";
                PDFont cellFont = fontN;
                HAlign cellAlign = tableHdrAlign;
                switch (tableHeader) {
                    case "SN":
                        cellText = snNum;
                        randomItem.setSerialNumber(cellText); break;
                    case "Qty":
                        cellText = quantity;
                        randomItem.setQuantity(cellText); break;
                    case "ItemCode":
                        cellText = randomProduct.getCode();
                        randomItem.setItemCode(cellText); break;
                    case "Item":
                        cellFont = fontNB;
                        cellText = randomProduct.getName();
                        randomItem.setDescription(cellText); break;
                    case "ItemRate":
                        cellText = randomProduct.getFmtPrice()+amtSuffix;
                        randomItem.setUnitPrice(cellText); break;
                    case "Disc":
                        cellText = randomProduct.getFmtTotalDiscount()+amtSuffix;
                        randomItem.setDiscount(cellText); break;
                    case "DiscRate":
                        cellText = randomProduct.getFmtDiscountRate();
                        randomItem.setDiscountRate(cellText); break;
                    case "Tax":
                        cellText = randomProduct.getFmtTotalTax()+amtSuffix;
                        randomItem.setTax(cellText); break;
                    case "TaxRate":
                        cellText = randomProduct.getFmtTaxRate();
                        randomItem.setTaxRate(cellText); break;
                    case "SubTotal":
                        cellText = randomProduct.getFmtTotalPriceWithDiscount()+amtSuffix;
                        randomItem.setSubTotal(cellText); break;
                    case "Total":
                        cellText = randomProduct.getFmtTotalPriceWithTaxAndDDiscount()+amtSuffix;
                        randomItem.setTotal(cellText); break;
                }
                cellBgColor = proba.get("alternate_table_items_bg_color") && w % 2 == 0 ? lgray: cellBgColor;
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, 8, 0, 0, cellText, cellTextColor, cellBgColor, cellAlign, tableHeader+"Item");
                productLine.addElement(rowBox, false);
            }
            annot.getItems().add(randomItem);

            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            productLine.setBackgroundColor(cellBgColor);
            verticalTableItems.addElement(productLine);
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
        }

        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
        verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        float tableItemsHeight = verticalTableItems.getBBox().getHeight();

        verticalTableItems.addElement(new HorizontalLineBox(0,0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));

        if (proba.get("table_footer_multi_row")) {
            float[] configFooterRow = {450f, 80f}; // Adds up to 530 which is pageW - leftM - rightM
            for (int i=0; i<tableHeaders.size(); i++ ) {
                TableRowBox footerInvoice = new TableRowBox(configFooterRow, 0, 25);
                String tableHeader = tableHeaders.get(i);
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                footerInvoice.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), HAlign.RIGHT, tableHeader+"FooterLabel"), false);
                footerInvoice.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), HAlign.RIGHT, tableHeader+"FooterValue"), false);
                verticalTableItems.addElement(footerInvoice);

                switch (tableHeader) {
                    case "Tax": annot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": annot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": annot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": annot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": annot.getTotal().setTotalPrice(hdrValue); break;
                }
            }
        }
        else {
            // Table Footer Single Row
            // Footer Labels for final total amount, tax and discount
            TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
                titleTotalInvoice.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), tableHdrAlign, tableHeader+"FooterLabel"), false);
            }
            verticalTableItems.addElement(titleTotalInvoice);

            verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0,0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            // Footer Numerical formatted values for final total amount, tax and discount
            TableRowBox totalInvoice1 = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                totalInvoice1.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), tableHdrAlign, tableHeader+"FooterValue"), false);
                switch (tableHeader) {
                    case "Tax": annot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": annot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": annot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": annot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": annot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": annot.getTotal().setTotalPrice(hdrValue); break;
                }
            }
            verticalTableItems.addElement(totalInvoice1);

            verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
        }

        // Add registered address information
        if (proba.get("registered_address_info")) {
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            String addressFooterText = String.format("Registered Address for %s, %s, %s, %s, %s, %s",
                                                     company.getName(),
                                                     company.getAddress().getLine1(),
                                                     company.getAddress().getLine2(),
                                                     company.getAddress().getZip(),
                                                     company.getAddress().getCity(),
                                                     company.getAddress().getCountry());
            SimpleTextBox addressFooter = new SimpleTextBox(fontN, 10, 0, 0, addressFooterText);
            addressFooter.setWidth(500);
            verticalTableItems.addElement(addressFooter);
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));

            annot.getVendor().setVendorName(company.getName());
            annot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity()+" "+company.getAddress().getCountry());
            annot.getVendor().setVendorPOBox(company.getAddress().getZip());
        }
        else if (proba.get("total_in_words") && !proba.get("registered_address_info")) {
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(white,white, 0, 0, 0, 0, 5));

            String totalInWordsText = HelperCommon.spellout_number(
                    pc.getTotalWithTax(),
                    new Locale(model.getLocale()));
            totalInWordsText = "Total in Words: " + totalInWordsText+" "+cur;
            totalInWordsText = (rnd.nextInt(100) < 50) ? totalInWordsText.toUpperCase() : totalInWordsText;

            SimpleTextBox totalInWordsFooter = new SimpleTextBox(fontN, 10, 0, 0, totalInWordsText);
            totalInWordsFooter.setWidth(500);
            verticalTableItems.addElement(totalInWordsFooter);
            verticalTableItems.addElement(new BorderBox(white, white, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            annot.getTotal().setCurrency(cur);
        }
        verticalTableItems.build(contentStream, writer);
        tableTopInfoLine.build(contentStream, writer); // must be built after verticalTableItems

        // Add horizontal borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopPosY - tableTopBox.getBBox().getHeight() - 2;
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                HelperImage.drawLine(contentStream, xPos-2, yPos, xPos-2, yPos - tableItemsHeight, lineStrokeColor);
                xPos += configRow[i];
            }
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
        }

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 300;
            float pAX = proba.get("signature_bottom_left") ? rightAddrX: leftPageMargin;
            float pAY = verticalTableItems.getBBox().getPosY() - verticalTableItems.getBBox().getHeight() - 10;

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,document,payment,company,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(contentStream,writer);
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
            sigTextBox.build(contentStream, writer);

            new HorizontalLineBox(
                    sigTX - 10, sigTY + 5,
                    sigTX + sigTextBox.getBBox().getWidth() + 5, sigTY + 5,
                    lineStrokeColor).build(contentStream, writer);

            String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

            float maxSW = 110, maxSH = 65;
            float sigScale = Math.min(maxSW/sigImg.getWidth(), maxSH/sigImg.getHeight());
            float sigW = sigImg.getWidth() * sigScale;
            float sigH = sigImg.getHeight() * sigScale;
            // align signature to center of sigTextBox bbox
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;;
            float sigIY = sigTY + 10;

            contentStream.drawImage(sigImg, sigIX, sigIY, sigW, sigH);
        }

        float footerHLineY = 110;
        // Add footer line and info if footer_info to be used and number of items less than 5
        if (proba.get("footer_info") && pc.getProducts().size() < 5) {
            new HorizontalLineBox(leftPageMargin, footerHLineY, pageWidth-rightPageMargin, footerHLineY, lineStrokeColor).build(contentStream, writer);

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
                verticalFooterCont.alignElements("CENTER", verticalFooterCont.getBBox().getWidth());
                verticalFooterCont.translate(pageMiddleX - verticalFooterCont.getBBox().getWidth()/2, 0);
            }
            verticalFooterCont.build(contentStream, writer);
        }

        // Logo Bottom if logo is not at top or barcode at top
        if (!proba.get("logo_top") | proba.get("barcode_top")) {
            maxLogoWidth = 120;
            maxLogoHeight = 80;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = pageWidth-logoWidth-rightPageMargin;
            posLogoY = bottomPageMargin+footerHLineY/2-logoHeight/2;
            contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);
        }

        // Barcode bottom
        if (proba.get("barcode_bottom")) {
            contentStream.drawImage(barcodeImg, leftPageMargin, bottomPageMargin, barcodeImg.getWidth() - 15, barcodeImg.getHeight() - 72);
        }

        // Add company stamp watermark, 40% prob
        if (proba.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + company.getStamp().getFullPath());
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f, maxAStamp = 0.8f;
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
            double rotAngle = 10 + rnd.nextInt(80);
            float stampWidth = resDim;
            float stampHeight = resDim;
            if (company.getStamp().getName().matches("(.*)" + "_rect")) {
                // For Rectangular stamps, set rotation angle to 0 and
                // resize stamp maintaining aspect ratio
                rotAngle = 0;
                stampWidth += rnd.nextInt(20);
                stampHeight = (stampWidth * stampImg.getHeight()) / stampImg.getWidth();
            }
            else if (proba.get("stamp_bottom_elongated")) {
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
        else if (!proba.get("signature_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontN, 7, 20, 130, noStampSignMsg, "Footnote").build(contentStream, writer);
        }

        // Add bg logo watermark or confidential stamp, but not both at once
        if (proba.get("confidential_watermark")) {
            // Add confidential watermark
            HelperImage.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "Confidential");
        }
        else if (proba.get("logo_watermark")) {
            // Add watermarked background logo
            HelperImage.addWatermarkImagePDF(document, page, logoImg);
        }

        contentStream.close();
        writer.writeEndElement();
    }
}
