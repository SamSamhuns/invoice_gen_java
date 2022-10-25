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
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.ProductTable;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
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
import java.util.LinkedHashMap;


@ApplicationScoped
public class AmazonLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Amazon";
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

        // Generate barCodeNum
        Generex barCodeNumGen = new Generex("[0-9]{12}");
        String barCodeNum = barCodeNumGen.random();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 25;
        float rightPageMargin = 25;
        float bottomPageMargin = 8;

        // colors
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = genProb.get("line_stroke_black") ? Color.BLACK: themeColor;
        Color grayish = HelperCommon.getRandomColor(3);

        // always set to false but individually change SimpleTextBox HAlign
        boolean centerAlignItems = false;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        float maxLogoWidth; float maxLogoHeight;
        float posLogoX; float posLogoY;
        float logoScale;
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
        VerticalContainer topTextContainer = new VerticalContainer(leftPageMargin, 810, 500);
        topTextContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, "Page 1 of 1" + ((rnd.nextBoolean()) ? ", 1-1/1": ".")));
        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        String textTopInvString = ((rnd.nextBoolean()) ? docTitle+" for ": docTitle+" dated ")+model.getDate().getValueInvoice();
        if (genProb.get("text_top_invoice_number") && !genProb.get("invoice_number_top")) {  // inc invoice number if not mentioned below
            textTopInvString = model.getReference().getLabelInvoice()+" "+model.getReference().getValueInvoice()+" for "+model.getDate().getValueInvoice();
            modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        }
        topTextContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, textTopInvString, docTitle+" Number"));
        topTextContainer.addElement(new SimpleTextBox(fontNB, 10, 0, 0, ((rnd.nextBoolean()) ? "Retail / "+docTitle+" / Cash Memorandum": "Retail / "+docTitle) ));
        if (genProb.get("text_top_center") && !genProb.get("barcode_top")) {  // center top text if barcode not present
            topTextContainer.alignElements("CENTER", topTextContainer.getBoundingBox().getWidth());
            topTextContainer.translate(pageMiddleX - topTextContainer.getBoundingBox().getWidth()/2, 0);
        }
        modelAnnot.setTitle(docTitle);
        modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        topTextContainer.build(contentStream, writer);

        // Vendor/Company Address
        VerticalContainer vendorAddrContainer = new VerticalContainer(leftPageMargin, 761, 300);
        vendorAddrContainer.addElement(new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 10, 0, 0, company.getAddressHeader(), "SH"));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getName(), "SN"));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getLine1(), "SA" ));
        vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getZip()+" "+company.getAddress().getCity(), "SA"));
        if (genProb.get("vendor_address_phone_fax")) {
            vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getContact().getPhoneLabel()+": "+company.getContact().getPhoneValue(), "SC"));
            vendorAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getContact().getFaxLabel()+": "+company.getContact().getFaxValue(), "SF"));
        }
        if (genProb.get("addresses_bordered")) {
            vendorAddrContainer.setBorderColor(lineStrokeColor);
            vendorAddrContainer.setBorderThickness(0.5f);
        }
        modelAnnot.getVendor().setVendorName(company.getName());
        modelAnnot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity());
        modelAnnot.getVendor().setVendorPOBox(company.getAddress().getZip());
        vendorAddrContainer.build(contentStream, writer);

        float leftTopInfoY = 670; // Progressively inc by 10 pts after each left-side box
        // if no "vendor_address_phone_fax" then Purchase Order Number, Left side
        if (genProb.get("purchase_order_number_top") && !genProb.get("vendor_address_phone_fax")) {
            String purchaseOrderText = model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, purchaseOrderText, "LO").build(contentStream, writer);
            leftTopInfoY += 10;
            modelAnnot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // TAX number, Left side
        if (genProb.get("vendor_tax_number_top")) {
            String vatText = company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue();
            new SimpleTextBox(fontN, 9, leftPageMargin, leftTopInfoY, vatText, "SVAT").build(contentStream, writer);
            modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }

        float rightTopInfoY = 670; // Progressively inc by 10 pts after each right-side box, building from bottom
        // Currency Used, Right side
        if (genProb.get("currency_top")) {
            String currencyText = payment.getLabelAccountCurrency()+": "+cur;
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, currencyText, "CUR").build(contentStream, writer);
            rightTopInfoY += 10;
            modelAnnot.getTotal().setCurrency(cur);
        }
        // Payment Terms, Right side
        if (genProb.get("payment_terms_top")) {
            String paymentTermText = payment.getLabelPaymentTerm()+": "+payment.getValuePaymentTerm();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentTermText, "PT").build(contentStream, writer);
            rightTopInfoY += 10;
            modelAnnot.getInvoice().setPaymentTerm(payment.getValuePaymentTerm());
        }
        // Payment Due Date if Payment Terms is not mentioned
        else if (genProb.get("payment_due_top")) {
            String paymentDueText = model.getDate().getLabelPaymentDue()+": "+model.getDate().getValuePaymentDue();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, paymentDueText, "PT").build(contentStream, writer);
            rightTopInfoY += 10;
            modelAnnot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        // invoice number, Right side
        if (genProb.get("invoice_number_top")) {
            String invoiceText = model.getReference().getLabelInvoice() + ": " +model.getReference().getValueInvoice();
            new SimpleTextBox(fontN, 9, pageWidth/2, rightTopInfoY, invoiceText, "INV").build(contentStream, writer);
            modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
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
        billAddrContainer.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getBillingHead(), "BH" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingName(), "BN" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingAddress().getLine1(), "BA" ));
        billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingAddress().getZip()+" "+client.getBillingAddress().getCity(), "BA" ));
        if (genProb.get("client_bill_address_tax_number")) {
            billAddrContainer.addElement(new SimpleTextBox(fontN,9,0,0,client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BT"));
            modelAnnot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        else if (genProb.get("bill_address_phone_fax")) {
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getPhoneLabel()+": "+client.getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getFaxLabel()+": "+client.getBillingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") & client.getBillingHead().length() > 0) {
            billAddrContainer.setBorderColor(lineStrokeColor);
            billAddrContainer.setBorderThickness(0.5f);
        }
        modelAnnot.getBillto().setCustomerName(client.getBillingName());
        modelAnnot.getBillto().setCustomerAddr(client.getBillingAddress().getLine1()+" "+client.getBillingAddress().getZip()+" "+client.getBillingAddress().getCity());
        modelAnnot.getBillto().setCustomerPOBox(client.getBillingAddress().getZip());
        billAddrContainer.build(contentStream, writer);

        // Shipping Address
        VerticalContainer shipAddrContainer = new VerticalContainer(shipX, shipY, 250);
        shipAddrContainer.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getShippingHead(), "SHH" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingName(), "SHN" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingAddress().getLine1(), "SHA" ));
        shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity(), "SHA" ));
        if (genProb.get("bill_address_phone_fax") & genProb.get("ship_address_phone_fax")) {
            String connec = (client.getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getPhoneLabel()+connec+client.getShippingContactNumber().getPhoneValue(), "BC"));
            shipAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getFaxLabel()+connec+client.getShippingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") & client.getShippingHead().length() > 0) {
            shipAddrContainer.setBorderColor(lineStrokeColor);
            shipAddrContainer.setBorderThickness(0.5f);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            modelAnnot.getShipto().setShiptoName(client.getShippingName());
            if (client.getShippingContactNumber().getPhoneLabel().length() > 0) {
                modelAnnot.getShipto().setShiptoPOBox(client.getShippingAddress().getZip());
                modelAnnot.getShipto().setShiptoAddr(client.getShippingAddress().getLine1()+" "+client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity());
            }
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
        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (genProb.get("currency_in_table_items")) {
            amtSuffix = " "+cur;
            modelAnnot.getTotal().setCurrency(cur);
        }
        boolean upperCap = rnd.nextBoolean();  // table header items case
        HAlign tableHdrAlign = genProb.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;

        // Building Header Item labels, table values and footer labels list
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin;
        ProductTable pt = new ProductTable(pc, amtSuffix, tableWidth);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow = pt.getConfigRow();
        Map<String, ProductTable.ColItem> itemMap = pt.getItemMap();

        // table item list head
        TableRowBox row1 = new TableRowBox(configRow, 0, 0);
        for (String tableHeader: tableHeaders) {
            String hdrLabel = itemMap.get(tableHeader).getLabelHeader();
            String tableHdrLabel = upperCap ? hdrLabel.toUpperCase() : hdrLabel;
            // if numerical header used, check if cur needs to appended at the end
            if (genProb.get("currency_in_table_headers") && !genProb.get("currency_in_table_items") && pt.getNumericalHdrs().contains(tableHeader)) {
                tableHdrLabel += " ("+cur+")";
            }
            row1.addElement(new SimpleTextBox(fontNB, 8, 0, 0, tableHdrLabel, hdrTextColor, hdrBgColor, tableHdrAlign, hdrLabel+"HeaderLabel"), centerAlignItems);
        }
        row1.setBackgroundColor(hdrBgColor);

        VerticalContainer verticalTableItems = new VerticalContainer(leftPageMargin, tableTopInfoPosY - tableTopInfoBox.getBoundingBox().getHeight() - 2, 600);
        verticalTableItems.addElement(row1);
        verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));

        new BorderBox(hdrBgColor, hdrBgColor, 0,
                      leftPageMargin, tableTopInfoPosY - tableTopInfoBox.getBoundingBox().getHeight() - 2 - row1.getBoundingBox().getHeight(),
                      row1.getBoundingBox().getWidth(), row1.getBoundingBox().getHeight()).build(contentStream, writer);

        // table item list body
        String quantity; String snNum;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            cellTextColor = Color.BLACK;
            cellBgColor = (randomProduct.getName().equalsIgnoreCase("shipping")) ? Color.LIGHT_GRAY: Color.WHITE;
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
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, 8, 0, 0, cellText, cellTextColor, cellBgColor, cellAlign, tableHeader+"Item");
                productLine.addElement(rowBox, centerAlignItems);
            }
            modelAnnot.getItems().add(randomItem);

            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(productLine);
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
        }

        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        float tableItemsHeight = verticalTableItems.getBoundingBox().getHeight();

        verticalTableItems.addElement(new HorizontalLineBox(0,0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
        verticalTableItems.addElement(new SimpleTextBox(fontN, 9, 0, 0, ""));

        if (genProb.get("table_footer_multi_row")) {
            float[] configFooterRow = {450f, 80f}; // Adds up to 530 which is pageW - leftM - rightM
            for (int i=0; i<tableHeaders.size(); i++ ) {
                TableRowBox footerInvoice = new TableRowBox(configFooterRow, 0, 25);
                String tableHeader = tableHeaders.get(i);
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                footerInvoice.addElement(new SimpleTextBox(fontNB, 8, 0, 0, (upperCap ? hdrLabel.toUpperCase() : hdrLabel), HAlign.RIGHT, tableHeader+"FooterLabel"), centerAlignItems);
                footerInvoice.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), HAlign.RIGHT, tableHeader+"FooterValue"), centerAlignItems);
                verticalTableItems.addElement(footerInvoice);

                switch (tableHeader) {
                    case "Tax": modelAnnot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": modelAnnot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": modelAnnot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": modelAnnot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": modelAnnot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": modelAnnot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": modelAnnot.getTotal().setTotalPrice(hdrValue); break;
                }
            }
        }
        else {
            // Table Footer Single Row
            // Footer Labels for final total amount, tax and discount
            TableRowBox titleTotalInvoice = new TableRowBox(configRow, 0, 0);
            for (String tableHeader: tableHeaders) {
                String hdrLabel = itemMap.get(tableHeader).getLabelFooter();
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
                String hdrValue = itemMap.get(tableHeader).getValueFooter();
                totalInvoice1.addElement(new SimpleTextBox(fontN, 8, 0, 0, (upperCap ? hdrValue.toUpperCase() : hdrValue), tableHdrAlign, tableHeader+"FooterValue"), centerAlignItems);
                switch (tableHeader) {
                    case "Tax": modelAnnot.getTotal().setTaxPrice(hdrValue); break;
                    case "TaxRate": modelAnnot.getTotal().setTaxRate(hdrValue); break;
                    case "Disc": modelAnnot.getTotal().setDiscountPrice(hdrValue); break;
                    case "DiscRate": modelAnnot.getTotal().setDiscountRate(hdrValue); break;
                    case "ItemRate": modelAnnot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "SubTotal": modelAnnot.getTotal().setSubtotalPrice(hdrValue); break;
                    case "Total": modelAnnot.getTotal().setTotalPrice(hdrValue); break;
                }
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
                                                     company.getName(),
                                                     company.getAddress().getLine1(),
                                                     company.getAddress().getLine2(),
                                                     company.getAddress().getZip(),
                                                     company.getAddress().getCity(),
                                                     company.getAddress().getCountry());
            SimpleTextBox addressFooter = new SimpleTextBox(fontN, 10, 0, 0, addressFooterText);
            addressFooter.setWidth(500);
            verticalTableItems.addElement(addressFooter);
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));

            modelAnnot.getVendor().setVendorName(company.getName());
            modelAnnot.getVendor().setVendorAddr(company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity()+" "+company.getAddress().getCountry());
            modelAnnot.getVendor().setVendorPOBox(company.getAddress().getZip());
        }
        else if (genProb.get("total_in_words") & !genProb.get("registered_address_info")) {
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            verticalTableItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0, 0, 0, 0, 5));

            String totalInWordsText = HelperCommon.spellout_number(
                    pc.getTotalWithTax(),
                    new Locale(model.getLocale()));
            totalInWordsText = "Total in Words: " + totalInWordsText+" "+cur;
            totalInWordsText = (rnd.nextInt(100) < 50) ? totalInWordsText.toUpperCase() : totalInWordsText;

            SimpleTextBox totalInWordsFooter = new SimpleTextBox(fontN, 10, 0, 0, totalInWordsText);
            totalInWordsFooter.setWidth(500);
            verticalTableItems.addElement(totalInWordsFooter);
            verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
            modelAnnot.getTotal().setCurrency(cur);
        }
        verticalTableItems.build(contentStream, writer);
        tableTopInfoLine.build(contentStream, writer); // must be built after verticalTableItems

        // Add borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopInfoPosY - tableTopInfoBox.getBoundingBox().getHeight() - 2;
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                HelperImage.drawLine(contentStream, xPos-2, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
                xPos += configRow[i];
            }
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
        }

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // Payment Address
        if (genProb.get("payment_address")) {
            // Set paymentAddrContainer opposite to the signature location
            float paymentAddrXPos = (genProb.get("signature_bottom_left")) ? rightAddrX: leftPageMargin;
            float paymentAddrYPos = verticalTableItems.getBoundingBox().getPosY() - verticalTableItems.getBoundingBox().getHeight() - 10;

            VerticalContainer paymentAddrContainer = new VerticalContainer(paymentAddrXPos, paymentAddrYPos, 300);
            paymentAddrContainer.addElement(new SimpleTextBox(fontNB, 10, 0, 0, payment.getAddressHeader(), "PH"));
            modelAnnot.getPaymentto().setBankName(payment.getValueBankName());
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelBankName()+": "+payment.getValueBankName(), "PBN"));
            modelAnnot.getPaymentto().setAccountName(payment.getValueAccountName());
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelAccountName()+": "+payment.getValueAccountName(), "PAName"));
            modelAnnot.getPaymentto().setIbanNumber(payment.getValueIBANNumber());
            if (genProb.get("payment_account_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelAccountNumber()+": "+payment.getValueAccountNumber(), "PANum"));
                modelAnnot.getPaymentto().setAccountNumber(payment.getValueAccountNumber());
            }
            if (genProb.get("payment_branch_name")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelBranchName()+": "+payment.getValueBranchName(), "PBName"));
                modelAnnot.getPaymentto().setBranchAddress(payment.getValueBranchName());
            }
            paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelIBANNumber()+": "+payment.getValueIBANNumber(), "PBNum"));
            if (genProb.get("payment_routing_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelRoutingNumber()+": "+payment.getValueRoutingNumber(), "PRNum"));
                modelAnnot.getPaymentto().setRoutingNumber(payment.getValueRoutingNumber());
            }
            if (genProb.get("payment_swift_number")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, payment.getLabelSwiftCode()+": "+payment.getValueSwiftCode(), "PSNum"));
                modelAnnot.getPaymentto().setSwiftCode(payment.getValueSwiftCode());
            }
            // Vendor TAX number bottom added randomly if vendor_tax_number_top is NOT present
            if (genProb.get("vendor_payment_tax_number") && !genProb.get("vendor_tax_number_top")) {
                paymentAddrContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue(),"SVAT"));
                modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
            }
            if (genProb.get("addresses_bordered")) {
                paymentAddrContainer.setBorderColor(lineStrokeColor);
                paymentAddrContainer.setBorderThickness(0.5f);
            }
            paymentAddrContainer.build(contentStream, writer);
        }

        // Add Signature at bottom
        if (genProb.get("signature_bottom")) {
            String compSignatureName = company.getName();
            compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
            SimpleTextBox singatureTextBox = new SimpleTextBox(
                    fontN, 8, 0, 130,
                    company.getSignature().getLabel()+" "+compSignatureName, "Signature");

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

            String signaturePath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
            int signatureWidth = 120;
            int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
            // align signature to center of singatureTextBox bbox
            float signatureXPos = singatureTextBox.getBoundingBox().getPosX() + singatureTextBox.getBoundingBox().getWidth()/2 - signatureWidth/2;
            float signatureYPos = 140;
            contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        float footerHLineY = 110;
        // Add footer line and info if footer_info to be used and number of items less than 5
        if (genProb.get("footer_info") & pc.getProducts().size() < 5) {
            new HorizontalLineBox(leftPageMargin, footerHLineY, pageWidth-rightPageMargin, footerHLineY, lineStrokeColor).build(contentStream, writer);

            VerticalContainer verticalFooterContainer = new VerticalContainer(leftPageMargin, footerHLineY-10, 450);
            String compEmail = ((company.getWebsite() == null) ? "company.domain.com" :  company.getWebsite());
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
        if (genProb.get("barcode_bottom")) {
            contentStream.drawImage(barcodeImg, leftPageMargin, bottomPageMargin, barcodeImg.getWidth() - 15, barcodeImg.getHeight() - 72);
        }

        // Add company stamp watermark, 40% prob
        if (genProb.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + company.getStamp().getFullPath());
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
            if (company.getStamp().getName().matches("(.*)" + "_rect")) {
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
