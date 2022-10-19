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
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.TableColumnItem;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;

import com.mifmif.common.regex.Generex;

import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;


@ApplicationScoped
public class AmazonLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Amazon";
    }

    @Override
    public void buildInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer) throws Exception {
        PDPage page = new PDPage(PDRectangle.A4);

        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        Random rnd = model.getRandom();
        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> genProb = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // Generate barCodeNum
        Generex barCodeNumGen = new Generex("[0-9]{12}");
        String barCodeNum = barCodeNumGen.random();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDType1Fonts(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 25;
        float rightPageMargin = 40;

        Color lineStrokeColor = (rnd.nextInt(100) < 95) ? Color.BLACK: Color.BLUE;
        Color grayishFontColor = HelperCommon.getRandomColor(3);

        // always set to false but individually change SimpleTextBox HAlign
        boolean centerAlignItems = false;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + model.getCompany().getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        // gen barcode img
        BufferedImage barcodeBufImg = HelperImage.generateEAN13BarcodeImage(barCodeNum);
        PDImageXObject barcodeImg = LosslessFactory.createFromImage(document, barcodeBufImg);

        ///////////////////////////////////      Build Page components now      ////////////////////////////////////
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        new BorderBox(HelperCommon.getRandomColor(6), Color.WHITE, 4, 0, 0, pageWidth, pageHeight).build(contentStream, writer);

        // Barcode top
        if (genProb.get("barcode_top")) {
            new ImageBox(barcodeImg, pageWidth / 2, 810, barcodeImg.getWidth(), (float)(barcodeImg.getHeight() / 1.5), barCodeNum).build(contentStream, writer);
        }
        // Or Logo top
        else if (genProb.get("logo_top")) {
            logoWidth = 100;
            logoHeight = (logoWidth * logoImg.getHeight()) / logoImg.getWidth();
            contentStream.drawImage(logoImg, pageWidth-logoWidth-rightPageMargin, pageHeight-logoHeight-rightPageMargin, logoWidth, logoHeight);
        }

        // Text top
        VerticalContainer infos = new VerticalContainer(leftPageMargin, 810, 500);
        infos.addElement(new SimpleTextBox(fontN, 9, 0, 0, "Page 1 of 1" + ((rnd.nextBoolean()) ? ", 1-1/1": ".")));
        infos.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getReference().getLabelInvoice()+" for "+model.getReference().getValueInvoice()+" "+model.getDate().getValueInvoice(), "Invoice Number"));
        infos.addElement(new SimpleTextBox(fontB, 10, 0, 0, ((rnd.nextBoolean()) ? "Retail" : "Institution") + ((rnd.nextBoolean()) ? " / Tax Invoice / Cash Memorandum": " / Invoice") ));
        infos.build(contentStream, writer);

        // Vendor/Company Address
        VerticalContainer vendorAddrContainer = new VerticalContainer(leftPageMargin, 761, 300);
        vendorAddrContainer.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 10, 0, 0, model.getCompany().getAddressHeader(), "SH"));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getCompany().getLogo().getName(), "SN"));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getCompany().getAddress().getLine1(), "SA" ));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getCompany().getAddress().getZip()+" "+model.getCompany().getAddress().getCity(), "SA"));
        if (genProb.get("vendor_address_phone_fax")) {
            vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getCompany().getContact().getPhoneLabel()+": "+model.getCompany().getContact().getPhoneValue(), "SC"));
            vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getCompany().getContact().getFaxLabel()+": "+model.getCompany().getContact().getFaxValue(), "SF"));
        }
        if (genProb.get("addresses_bordered")) {
            vendorAddrContainer.setBorderColor(lineStrokeColor);
            vendorAddrContainer.setBorderThickness(0.5f);
        }
        vendorAddrContainer.build(contentStream, writer);

        float leftTopInfoY = 670; // Progressively inc by 10 pts after each left-side box
        // if no "vendor_address_phone_fax" then Purchase Order Number, Left side
        if (!genProb.get("vendor_address_phone_fax") && genProb.get("purchase_order_number_top")) {
          String purchaseOrderText = model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder();
          new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, purchaseOrderText, "LO").build(contentStream, writer);
          leftTopInfoY += 10;
        }
        // TAX number, Left side
        if (genProb.get("tax_number_top")) {
            String vatText = model.getCompany().getIdNumbers().getVatLabel() + ": " + model.getCompany().getIdNumbers().getVatValue();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, vatText, "SVAT").build(contentStream, writer);
        }

        float rightTopInfoY = 670; // Progressively inc by 10 pts after each right-side box
        // Currency Used, Right side
        if (genProb.get("currency_top")) {
            String currencyText = model.getPaymentInfo().getLabelAccountCurrency()+": "+model.getProductContainer().getCurrency();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, currencyText, "CUR").build(contentStream, writer);
            rightTopInfoY += 10;
        }
        // Currency Exchange Used, Right side
        if (genProb.get("currency_exchange_top")) {
            String currencyText = model.getPaymentInfo().getLabelAccountCurrency()+" Exchange: "+model.getProductContainer().getCurrency();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, currencyText, "CEX").build(contentStream, writer);
            rightTopInfoY += 10;
        }
        // Payment Terms, Right side
        if (genProb.get("payment_terms_top")) {
            String paymentTermText = model.getPaymentInfo().getLabelPaymentTerm()+": "+model.getPaymentInfo().getValuePaymentTerm();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentTermText, "PT").build(contentStream, writer);
            rightTopInfoY += 10;
        }
        // invoice number, Right side
        if (genProb.get("invoice_number_top")) {
            String invoiceText = model.getReference().getLabelInvoice() + ": " +model.getReference().getValueInvoice();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, invoiceText, "INV").build(contentStream, writer);
        }

        HelperImage.drawLine(contentStream, leftPageMargin, 650, pageWidth-rightPageMargin, 650, lineStrokeColor);

        // check if billing and shipping addresses should be switched
        float leftAddrX = leftPageMargin;
        float rightAddrX = pageWidth/2 + rnd.nextInt(5);
        if (genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = 645;
        float shipX = rightAddrX; float shipY = 645;

        // Billing Address
        VerticalContainer billAddrContainer = new VerticalContainer(billX, billY, 250);
        billAddrContainer.addElement(new SimpleTextBox(fontNB, 9, 0, 0, model.getClient().getBillingHead(), "BH" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingName(), "BN" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingAddress().getLine1(), "BA" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingAddress().getZip() + " "+model.getClient().getBillingAddress().getCity(), "BA" ));
        if (genProb.get("client_bill_address_tax_number")) {
          billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getIdNumbers().getVatLabel()+": "+model.getClient().getIdNumbers().getVatValue(),"BT"));
        }
        else if (genProb.get("bill_address_phone_fax")) {
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingContactNumber().getPhoneLabel()+": "+model.getClient().getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getBillingContactNumber().getFaxLabel()+": "+model.getClient().getBillingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") & model.getClient().getBillingHead().length() > 0) {
            billAddrContainer.setBorderColor(lineStrokeColor);
            billAddrContainer.setBorderThickness(0.5f);
        }
        billAddrContainer.build(contentStream, writer);

        // Shipping Address
        VerticalContainer shipAddrContainer = new VerticalContainer(shipX, shipY, 250);
        shipAddrContainer.addElement(new SimpleTextBox(fontNB, 9, 0, 0, model.getClient().getShippingHead(), "SHH" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingName(), "SHN" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingAddress().getLine1(), "SHA" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingAddress().getZip() + " " + model.getClient().getShippingAddress().getCity(), "SHA" ));
        if (genProb.get("bill_address_phone_fax") & genProb.get("ship_address_phone_fax")) {
            String connec = (model.getClient().getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingContactNumber().getPhoneLabel()+connec+model.getClient().getShippingContactNumber().getPhoneValue(), "BC"));
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getClient().getShippingContactNumber().getFaxLabel()+connec+model.getClient().getShippingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") & model.getClient().getShippingHead().length() > 0) {
            shipAddrContainer.setBorderColor(lineStrokeColor);
            shipAddrContainer.setBorderThickness(0.5f);
        }
        shipAddrContainer.build(contentStream, writer);

        // table header text colors
        Color hdrTextColor = genProb.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == Color.WHITE) ? Color.BLACK: Arrays.asList(Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        // table top info
        String tableTopInfoText = (hdrBgColor == Color.WHITE) ? "" : new Generex("(Transaction|Nature of Transaction|Transaction Type): (Purchase|Sale)").random();
        float tableTopInfoPosX = leftPageMargin;
        float tableTopInfoPosY = billAddrContainer.getBoundingBox().getPosY() - billAddrContainer.getBoundingBox().getHeight() - 15;

        SimpleTextBox tableTopInfoBox = new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 9, tableTopInfoPosX, tableTopInfoPosY, tableTopInfoText);
        tableTopInfoBox.build(contentStream, writer);

        // table top horizontal line, will be built after verticalTableItems
        float x1 = leftPageMargin; float y1 = tableTopInfoBox.getBoundingBox().getPosY() - tableTopInfoBox.getBoundingBox().getHeight() - 2;
        float x2 = pageWidth-rightPageMargin; float y2 = y1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(x1, y1, x2, y2, lineStrokeColor);

        ////////////////////////////////////      Building Table      ////////////////////////////////////

        ProductContainer pc = model.getProductContainer();
        boolean upperCap = rnd.nextBoolean();
        HAlign tableHdrAlign = (genProb.get("table_center_align_items")) ? HAlign.CENTER: HAlign.LEFT;

        // Header Item list head labels
        String snHead = pc.getsnHead();
        String qtyHead = pc.getQtyHead();
        String nameHead = pc.getNameHead();
        String unitPriceHead = pc.getUPHead();
        String discountHead = pc.getDiscountHead();
        String totalWithoutTaxHead = pc.getTotalHead();
        String taxRateHead = pc.getTaxRateHead();
        String taxHead = pc.getTaxHead();

        // Footer labels for final total amount, tax and discount
        String totalHead = pc.getTotalHead();
        String discountTotalHead = pc.getDiscountTotalHead();
        String taxAndDiscountTotalHead = pc.getWithTaxAndDiscountTotalHead();
        String taxRateTotalHead = pc.getTaxRateTotalHead();
        String taxTotalHead = pc.getTaxTotalHead();

        // building item table column width list
        float[] configRow = {40f, 40f, 150f, 60f, 60f, 60f, 60f, 60f};  // Adds up to 530 which is pageW - leftM - rightM
        // Use maps to assign order of items, headers and footers to columns
        // SN, Qty, Item, ItemRate, Disc, Total, TaxRate, Tax
        Map<String, TableColumnItem> itemMap = new LinkedHashMap<>();
        // TableColumnItem constructor(float colWidth, String colLabelHeader, String colLabelFooter, String colValueFooter)
        itemMap.put("SN", new TableColumnItem(40f, snHead, "", ""));
        itemMap.put("Qty", new TableColumnItem(40f, qtyHead, "", ""));
        itemMap.put("Item", new TableColumnItem(150f, nameHead, "", ""));
        itemMap.put("ItemRate", new TableColumnItem(60f, unitPriceHead, totalHead, pc.getFormatedTotal()));
        itemMap.put("Disc", new TableColumnItem(60f, discountHead, discountTotalHead, pc.getFormatedTotalDiscount()));
        itemMap.put("Total", new TableColumnItem(60f, totalWithoutTaxHead, taxAndDiscountTotalHead, pc.getFormatedTotalWithTax()));
        itemMap.put("TaxRate", new TableColumnItem(60f, taxRateHead, taxRateTotalHead, pc.getFormatedTotalTaxRate()));
        itemMap.put("Tax", new TableColumnItem(60f, taxHead, taxTotalHead, pc.getFormatedTotalTax()));
        List<String> tableHeaders = Arrays.asList("SN", "Qty", "Item", "ItemRate", "Disc", "Total", "TaxRate", "Tax");

        TableRowBox row1 = new TableRowBox(configRow, 0, 0);
        for (String tableHeader: tableHeaders) {
            String hdrLabel = itemMap.get(tableHeader).getColLabelHeader();
            row1.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), hdrTextColor, hdrBgColor, tableHdrAlign, hdrLabel+"HeaderLabel"), centerAlignItems);
        }
        row1.setBackgroundColor(hdrBgColor);

        VerticalContainer verticalTableItems = new VerticalContainer(leftPageMargin, tableTopInfoPosY - tableTopInfoBox.getBoundingBox().getHeight() - 2, 600);
        verticalTableItems.addElement(row1);
        verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));

        // item list
        String quantity; String snNum;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<model.getProductContainer().getProducts().size(); w++) {
            Product randomProduct = model.getProductContainer().getProducts().get(w);
            cellTextColor = Color.BLACK;
            cellBgColor = (randomProduct.getName().toLowerCase().equals("shipping")) ? Color.LIGHT_GRAY: Color.WHITE;
            quantity = (randomProduct.getName().toLowerCase().equals("shipping")) ? "": Float.toString(randomProduct.getQuantity());
            snNum = (randomProduct.getName().toLowerCase().equals("shipping")) ? "": Integer.toString(w + 1);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String cellText = "";
                PDFont cellFont = fontN;
                HAlign cellAlign = tableHdrAlign;
                switch (tableHeader) {
                    case "SN":
                        cellText = snNum; break;
                    case "Qty":
                        cellText = quantity; break;
                    case "Item":
                        cellText = randomProduct.getName();
                        cellFont = fontNB; break;
                    case "ItemRate":
                        cellText = randomProduct.getFormatedPrice(); break;
                    case "Disc":
                        cellText = randomProduct.getFormatedTotalDiscount(); break;
                    case "Total":
                        cellText = randomProduct.getFormatedTotalPrice(); break;
                    case "TaxRate":
                        cellText = randomProduct.getFormatedTaxRate(); break;
                    case "Tax":
                        cellText = randomProduct.getFormatedTotalTax(); break;
                }
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, 8, 0, 0, cellText, cellAlign, tableHeader+"Item");
                x1 = rowBox.getBoundingBox().getPosX();
                y1 = rowBox.getBoundingBox().getPosY() - rowBox.getBoundingBox().getHeight();
                HelperImage.drawLine(contentStream, x1, y1, x1 + rowBox.getBoundingBox().getWidth(), y1, Color.RED);
                productLine.addElement(rowBox, centerAlignItems);
            }

            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(productLine);
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
        }

        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        float tableItemsBottomY = verticalTableItems.getBoundingBox().getHeight();

        verticalTableItems.addElement(new HorizontalLineBox(0,0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));

        if (genProb.get("table_footer_multi_row")) {
            float[] configFooterRow = {450f, 80f}; // Adds up to 530 which is pageW - leftM - rightM
            for (int i=0; i<tableHeaders.size(); i++ ) {
              TableRowBox footerInvoice = new TableRowBox(configFooterRow, 0, 25);
              String tableHeader = tableHeaders.get(i);
              String hdrLabel = itemMap.get(tableHeader).getColLabelFooter();
              String hdrValue = itemMap.get(tableHeader).getColValueFooter();
              footerInvoice.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), HAlign.RIGHT, tableHeader+"FooterLabel"), centerAlignItems);
              footerInvoice.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), HAlign.RIGHT, tableHeader+"FooterValue"), centerAlignItems);
              verticalTableItems.addElement(footerInvoice);
            }
        }
        else {
            // Table Footer Single Row
            // Footer Labels for final total amount, tax and discount
            TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrLabel = itemMap.get(tableHeader).getColLabelFooter();
                titleTotalInvoice.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), tableHdrAlign, tableHeader+"FooterLabel"), centerAlignItems);
            }
            verticalTableItems.addElement(titleTotalInvoice);

            verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0,0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

            // Footer Numerical formatted values for final total amount, tax and discount
            TableRowBox totalInvoice1 = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrValue = itemMap.get(tableHeader).getColValueFooter();
                totalInvoice1.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), tableHdrAlign, tableHeader+"FooterValue"), centerAlignItems);
            }
            verticalTableItems.addElement(totalInvoice1);

            verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        }

        // Add registered address information
        if (genProb.get("registered_address_info")) {
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

            String addressFooterText = String.format("Registered Address for %s, %s, %s, %s, %s, %s",
                                                     model.getCompany().getName(),
                                                     model.getCompany().getAddress().getLine1(),
                                                     model.getCompany().getAddress().getLine2(),
                                                     model.getCompany().getAddress().getZip(),
                                                     model.getCompany().getAddress().getCity(),
                                                     model.getCompany().getAddress().getCountry());
            SimpleTextBox addressFooter = new SimpleTextBox(fontN, 10, 0, 0, addressFooterText);
            addressFooter.setWidth(500);
            verticalTableItems.addElement(addressFooter);
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        }
        else if (genProb.get("total_in_words") & !genProb.get("registered_address_info")) {
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

            String totalInWordsText = HelperCommon.spellout_number(
                    model.getProductContainer().getTotalWithTax(),
                    new Locale(model.getLocale()));
            totalInWordsText = "Total in Words: " + totalInWordsText+" "+model.getProductContainer().getCurrency();
            totalInWordsText = (rnd.nextInt(100) < 50) ? totalInWordsText.toUpperCase() : totalInWordsText;

            SimpleTextBox totalInWordsFooter = new SimpleTextBox(fontN, 10, 0, 0, totalInWordsText);
            totalInWordsFooter.setWidth(500);
            verticalTableItems.addElement(totalInWordsFooter);
            verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        }
        verticalTableItems.build(contentStream, writer);
        tableTopInfoLine.build(contentStream, writer); // must be built after verticalTableItems

        // Add borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopInfoPosY - tableTopInfoBox.getBoundingBox().getHeight() - 2;
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsBottomY, lineStrokeColor);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
              HelperImage.drawLine(contentStream, xPos-2, yPos, xPos, yPos - tableItemsBottomY, lineStrokeColor);
              xPos += configRow[i];
            }
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsBottomY, lineStrokeColor);
        }

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // Payment Address
        if (genProb.get("payment_address")) {
            // Set paymentAddrContainer opposite to the signature location
            float paymentAddrXPos = (genProb.get("signature_bottom_left")) ? rightAddrX: leftPageMargin;
            float paymentAddrYPos = verticalTableItems.getBoundingBox().getPosY() - verticalTableItems.getBoundingBox().getHeight() - 10;
            PaymentInfo mp = model.getPaymentInfo();

            VerticalContainer paymentAddrContainer = new VerticalContainer(paymentAddrXPos, paymentAddrYPos, 300);
            paymentAddrContainer.addElement(new SimpleTextBox(fontNB, 10, 0, 0, mp.getAddressHeader(), "PH"));
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelBankName()+": "+model.getPaymentInfo().getValueBankName(), "PBN"));
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelAccountName()+": "+model.getPaymentInfo().getValueAccountName(), "PAName"));
            if (genProb.get("payment_account_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelAccountNumber()+": "+mp.getValueAccountNumber(), "PANum"));
            }
            if (genProb.get("payment_branch_name")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelBranchName()+": "+mp.getValueBranchName(), "PBName"));
            }
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getPaymentInfo().getLabelIBANNumber()+": "+mp.getValueIBANNumber(), "PBNum"));
            if (genProb.get("payment_routing_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelRoutingNumber()+": "+mp.getValueRoutingNumber(), "PRNum"));
            }
            if (genProb.get("payment_swift_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, mp.getLabelSwiftCode()+": "+mp.getValueSwiftCode(), "PSNum"));
            }
            // Client TAX number bottom added randomly if client_bill_address_tax_number is NOT present
            if (!genProb.get("client_bill_address_tax_number") && genProb.get("client_payment_tax_number")) {
                billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,model.getClient().getIdNumbers().getVatLabel()+": "+model.getClient().getIdNumbers().getVatValue(),"PTax"));
            }
            if (genProb.get("addresses_bordered")) {
                paymentAddrContainer.setBorderColor(lineStrokeColor);
                paymentAddrContainer.setBorderThickness(0.5f);
            }
            paymentAddrContainer.build(contentStream, writer);
        }

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
                    singatureTextxPos + singatureTextBox.getBoundingBox().getWidth() + 5, 135,
                    lineStrokeColor).build(contentStream, writer);

            String signaturePath = HelperCommon.getResourceFullPath(this, "common/signature/" + model.getCompany().getSignature().getFullPath());
            PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
            int signatureWidth = 120;
            int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
            // align signature to center of singatureTextBox bbox
            float signatureXPos = singatureTextBox.getBoundingBox().getPosX() + singatureTextBox.getBoundingBox().getWidth()/2 - signatureWidth/2;
            float signatureYPos = 140;
            contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add footer line and info if footer_info to be used and number of items less than 5
        if (genProb.get("footer_info") & model.getProductContainer().getProducts().size() < 5) {
            new HorizontalLineBox(20, 110, pageWidth-rightPageMargin, 110, lineStrokeColor).build(contentStream, writer);

            VerticalContainer verticalFooterContainer = new VerticalContainer(leftPageMargin, 100, 450);
            String compEmail = ((model.getCompany().getWebsite() == null) ? "company.domain.com" :  model.getCompany().getWebsite());
            String footerLine1 = (rnd.nextBoolean()) ? String.format("To return an item, visit %s/returns", compEmail) : String.format("For feedback, visit %s/feedback", compEmail);
            String footerLine2 = (rnd.nextBoolean()) ? "For more information on orders, visit http://" : "For queries on orders, visit http://";
            String footerLine3 = (rnd.nextBoolean()) ? String.format("%s/account-name", compEmail) : String.format("%s/orders", compEmail);
            verticalFooterContainer.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine1));
            verticalFooterContainer.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine2));
            verticalFooterContainer.addElement(new SimpleTextBox(fontB, 9, 0, 0, footerLine3));
            verticalFooterContainer.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 9, 0, 0, barCodeNum));

            if (genProb.get("footer_info_center")) {
                verticalFooterContainer.alignElements("CENTER", verticalFooterContainer.getBoundingBox().getWidth());
                verticalFooterContainer.translate(pageMiddleX - verticalFooterContainer.getBoundingBox().getWidth()/2, 0);
            }
            verticalFooterContainer.build(contentStream, writer);
        }

        // Logo Bottom if logo is not at top or barcode at top
        if (!genProb.get("logo_top") | genProb.get("barcode_top")) {
            logoWidth = 85;
            logoHeight = (logoWidth * logoImg.getHeight()) / logoImg.getWidth();
            contentStream.drawImage(logoImg, pageWidth-logoWidth-rightPageMargin, 8, logoWidth, logoHeight);
        }

        // Barcode bottom
        if (genProb.get("barcode_bottom")) {
            contentStream.drawImage(barcodeImg, leftPageMargin, 8, barcodeImg.getWidth() - 15, barcodeImg.getHeight() - 72);
        }

        // Add company stamp watermark, 40% prob
        if (genProb.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + model.getCompany().getStamp().getFullPath());
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f; float maxAStamp = 0.8f;
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp; float yPosStamp;
            // draw to lower right if signature in bottom or lower left if signature in bottom left
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
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontN, 7, 20, 130, noStampSignMsg, "Footnote").build(contentStream, writer);
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
