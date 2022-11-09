package com.fairandsmart.generator.documents.layout.darty;

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
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.ContactNumber;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.textbox.RotatedTextBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.line.VerticalLineBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;
import com.fairandsmart.generator.documents.element.footer.FootCompanyBox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

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
import java.util.stream.Collectors;

@ApplicationScoped
public class DartyLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Darty";
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

        IDNumbers idNumbers = company.getIdNumbers();
        Address address = company.getAddress();
        String cur = pc.getCurrency();

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> proba = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN : fontB;

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 20;
        float rightPageMargin = 20;
        float topPageMargin = 10;
        float bottomPageMargin = 10;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = new Color(220,220,220);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        float maxLogoWidth; float maxLogoHeight;
        float posLogoX; float posLogoY;
        float logoScale;

        /*//////////////////   Build Page components now   //////////////////*/

        PDPageContentStream stream = new PDPageContentStream(document, page);

        // logo top
        maxLogoWidth = 130;
        maxLogoHeight = 90;
        logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        logoWidth = logoImg.getWidth() * logoScale;
        logoHeight = logoImg.getHeight() * logoScale;
        posLogoX = leftPageMargin;
        posLogoY = pageHeight-topPageMargin;
        ImageBox logoImgBox = new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo");
        logoImgBox.build(stream,writer);

        // Vendor/Company Address
        VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,9,10,300,lineStrokeColor,model,document,company,annot,proba);
        vendorInfoBox.translate(posLogoX, posLogoY-logoImgBox.getBBox().getHeight()-5);
        vendorInfoBox.build(stream,writer);

        // Billing Shipping Address
        float heightBH = 200;
        float widthBH = 225;
        float posBHX = 350;
        float posBHY = pageHeight-topPageMargin-heightBH;
        new BorderBox(lineStrokeColor,white,1, posBHX, posBHY, widthBH, heightBH).build(stream,writer);  // wntire addr block
        new BorderBox(lineStrokeColor,lineStrokeColor,1, posBHX, posBHY+heightBH/2, widthBH, 1).build(stream,writer);  // mid-way addr line box

        float shipX = posBHX+5; float shipY = posBHY+heightBH-10;
        float billX = posBHX+5; float billY = posBHY+heightBH/2-10;

        // remove bordering temporarily
        Boolean addresses_bordered_actual = proba.get("addresses_bordered");
        proba.put("addresses_bordered", false);

        // Shipping Address
        // ship head outside
        new RotatedTextBox(fontNB, 9, posBHX-20, pageHeight-80,  90f, pageWidth, client.getShippingHead()).build(stream,writer);
        client.setShippingHead("");
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,document,client,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(stream,writer);

        // Billing Address
        // bill head outside
        new RotatedTextBox(fontNB, 9, posBHX-20, pageHeight-170, 90f, pageWidth, client.getBillingHead()).build(stream,writer);
        client.setBillingHead("");
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,document,client,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(stream,writer);
        // restore actual addresses_bordered
        proba.put("addresses_bordered", addresses_bordered_actual);

        // table top information
        float rectGrayY = vendorInfoBox.getBBox().getPosY()-vendorInfoBox.getBBox().getHeight()-5;
        float rectGrayW = 270;
        float rectGrayH = 42;

        // add doc title
        String docTitle = rnd.nextBoolean() ? "Invoice": "Tax Invoice";
        new BorderBox(lgray,lgray,1, leftPageMargin-2, rectGrayY-42, rectGrayW, rectGrayH).build(stream,writer);
        SimpleTextBox titleBox = new SimpleTextBox(fontB,11,0,0, docTitle);
        titleBox.translate(leftPageMargin + rectGrayW/2 - titleBox.getBBox().getWidth()/2, rectGrayY-3);  // move to rectGray center
        titleBox.build(stream,writer);
        annot.setTitle(docTitle);

        // invoice ref and date
        HorizontalContainer HDateCont = new HorizontalContainer(0,0);
        HDateCont.addElement(new SimpleTextBox(fontN,8,0,0, model.getReference().getLabelInvoice()+":  "));
        HDateCont.addElement(new SimpleTextBox(fontN,8,0,0, model.getReference().getValueInvoice()+"  ", "IN" ));
        annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        HDateCont.addElement(new SimpleTextBox(fontN,8,0,0, model.getDate().getLabelInvoice().toLowerCase()+" "));
        HDateCont.addElement(new SimpleTextBox(fontN,8,0,0, model.getDate().getValueInvoice(), "IDATE" ));
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        HDateCont.translate(leftPageMargin + rectGrayW/2 - HDateCont.getBBox().getWidth()/2, rectGrayY-25);
        HDateCont.build(stream,writer);

        // payment order ref and due date
        HorizontalContainer HPayDateCont = new HorizontalContainer(leftPageMargin, 595 );
        HPayDateCont.addElement(new SimpleTextBox(fontB,9,0,0, model.getReference().getLabelOrder()+":  "));
        HPayDateCont.addElement(new SimpleTextBox(fontNB,9,0,0, model.getReference().getValueOrder()+"  ","ONUM" ));
        annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        HPayDateCont.addElement(new SimpleTextBox(fontB,9,0,0, model.getDate().getLabelPaymentDue().toLowerCase()+" "));
        HPayDateCont.addElement(new SimpleTextBox(fontNB,9,0,0, model.getDate().getValuePaymentDue(), "PDATE" ));
        annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        HPayDateCont.build(stream,writer);

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
        float tableTopPosY = 580;

        SimpleTextBox tableTopBox = new SimpleTextBox(((rnd.nextInt(100) < 40) ? fontN : fontB), 9, tableTopPosX, tableTopPosY, tableTopText);
        tableTopBox.build(stream,writer);

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
                      row1.getBBox().getWidth(), row1.getBBox().getHeight()).build(stream,writer);

        // table item list body
        String quantity; String snNum;
        String qtySuffix = rnd.nextBoolean() ? " "+pc.getQtySuffix() : "" ;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            cellTextColor = black;
            cellBgColor = randomProduct.getName().equalsIgnoreCase("shipping") ? lgray: white;
            quantity = randomProduct.getName().equalsIgnoreCase("shipping") ? "": randomProduct.getQuantity()+qtySuffix;
            snNum = randomProduct.getName().equalsIgnoreCase("shipping") ? "": Integer.toString(w + 1);

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
                        cellText = randomProduct.getFmtTotalPriceWithTaxAndDiscount()+amtSuffix;
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

        if (proba.get("total_in_words")) {
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
        verticalTableItems.build(stream,writer);
        tableTopInfoLine.build(stream,writer); // must be built after verticalTableItems

        // Add vertical borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopPosY - tableTopBox.getBBox().getHeight() - 2;
            new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor).build(stream,writer);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                new VerticalLineBox(xPos-2, yPos, xPos-2, yPos - tableItemsHeight, lineStrokeColor).build(stream,writer);
                xPos += configRow[i];
            }
            new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor).build(stream,writer);
        }

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // payment and table bottom info
        float posMsgX = leftPageMargin, posMsgY = verticalTableItems.getBBox().getPosY()-verticalTableItems.getBBox().getHeight()-30;
        VerticalContainer tableFooterInfo = new VerticalContainer(posMsgX, posMsgY, 200);

        HorizontalContainer payCont = new HorizontalContainer(0,0);
        payCont.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getLabelPaymentType()+": "));
        payCont.addElement(new SimpleTextBox(fontNB,9,0,0, payment.getValuePaymentType()+", ","PMODE"));
        payCont.addElement(new SimpleTextBox(fontB,9,0,0, pc.getFmtTotalWithTaxAndDiscount(),"TA"));

        tableFooterInfo.addElement(payCont);
        tableFooterInfo.addElement(new SimpleTextBox(fontN,8,0,0, "The contract of the trust specifies the guarantees and services you benefit from"));
        tableFooterInfo.build(stream,writer);

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 350;
            float pAX = proba.get("signature_bottom_left") ? billX: leftPageMargin;
            float pAY = tableFooterInfo.getBBox().getPosY() - tableFooterInfo.getBBox().getHeight() - 15;

            proba.put("vendor_tax_number_top", proba.get("vendor_address_tax_number"));
            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,document,payment,company,annot,proba);
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
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(stream,writer);
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
            new SimpleTextBox(fontN, 7, tableFooterInfo.getBBox().getPosX(), tableFooterInfo.getBBox().getPosY()-40, noStampMsg, "footnote").build(stream,writer);
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
            FootCompanyBox footCompanyBox = new FootCompanyBox(fontN,fontB,fontI,fSize,fSize+1,themeColor, pageWidth-leftPageMargin-rightPageMargin,model,document,company,annot,proba);
            float fW = footCompanyBox.getBBox().getWidth();
            footCompanyBox.alignElements(HAlign.CENTER, fW);
            footCompanyBox.translate(pageMiddleX-fW/2,60);
            footCompanyBox.build(stream,writer);
        }

        HorizontalContainer footerExtraInfo = new HorizontalContainer(0,0);
        footerExtraInfo.addElement(new SimpleTextBox(fontN,7,0,0, "Disclaimer: The legal terms and conditions that apply to this Order shall be exclusively as per the company's Purchase Order Conditions"));
        footerExtraInfo.translate(pageMiddleX-footerExtraInfo.getBBox().getWidth()/2, bottomPageMargin+10);
        footerExtraInfo.build(stream,writer);

        stream.close();
        writer.writeEndElement();
    }
}
