package com.fairandsmart.generator.documents.layout.cdiscount;

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
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;

import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.line.VerticalLineBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class CdiscountLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Cdiscount";
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

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> proba = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 34;
        float rightPageMargin = 34;
        float topPageMargin = 34;
        float bottomPageMargin = 34;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = Color.LIGHT_GRAY;
        Color gray = new Color(239,239,239);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.9f), 0)).collect(Collectors.toList()); // darken colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);

        ///////////////////////////////////      Build Page components now      ////////////////////////////////////

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // draw top logo
        float maxLogoWidth = 200;
        float maxLogoHeight = 60;
        float logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        float logoWidth = logoImg.getWidth() * logoScale;
        float logoHeight = logoImg.getHeight() * logoScale;
        float posLogoX = leftPageMargin;
        float posLogoY = pageHeight-3;
        new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(contentStream,writer);

        // top left info
        // vendor address
        VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,8,9,250,lineStrokeColor,model,document,company,annot,proba);
        vendorInfoBox.translate(leftPageMargin+2, posLogoY-logoHeight-4);
        vendorInfoBox.build(contentStream,writer);

        // top right hdr info
        float hdrWidth = 252;
        float hdrHeight = 14;
        float posHdrX = pageWidth-rightPageMargin-hdrWidth;
        float posHdrY = pageHeight-topPageMargin-hdrHeight;

        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        SimpleTextBox docTitleBox = new SimpleTextBox(fontB,15,0,0, docTitle, docTitle);
        docTitleBox.translate(posHdrX+hdrWidth/2-docTitleBox.getBBox().getWidth()/2, posHdrY+32);
        docTitleBox.build(contentStream,writer);
        // tax invoice number
        HorizontalContainer containerInvNum = new HorizontalContainer(0,0);
        containerInvNum.addElement(new SimpleTextBox(fontNB,9,0,0, model.getReference().getLabelInvoice()+" "));
        containerInvNum.addElement(new SimpleTextBox(fontB,9,0,0, model.getReference().getValueInvoice(), "IN"));
        containerInvNum.translate(posHdrX + hdrWidth/2 - containerInvNum.getBBox().getWidth()/2, posHdrY+10);
        // tax invoice date
        HorizontalContainer containerDate = new HorizontalContainer(0,0);
        containerDate.addElement(new SimpleTextBox(fontNB,9,0,0, "From "));
        containerDate.addElement(new SimpleTextBox(fontB,9,0,0, model.getDate().getValueInvoice(), "IDATE"));
        containerDate.translate(posHdrX + hdrWidth/2 - containerDate.getBBox().getWidth()/2, posHdrY-4);

        new BorderBox(gray,gray, 1, posHdrX, posHdrY-3, hdrWidth, hdrHeight).build(contentStream,writer);
        new BorderBox(gray,gray, 1, posHdrX, posHdrY-hdrHeight-5, hdrWidth, hdrHeight).build(contentStream,writer);

        annot.setTitle(docTitle);
        annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        containerInvNum.build(contentStream,writer);
        containerDate.build(contentStream,writer);

        // border box around invoice body
        new BorderBox(themeColor,white,1,leftPageMargin,bottomPageMargin,pageWidth-rightPageMargin-leftPageMargin,655).build(contentStream,writer);

        // Payment Info and Address top right or Bottom left or Bottom right
        if (proba.get("payment_address_top") || (proba.get("payment_address_bottom") && pc.getProducts().size() < 6)) {
            float pAW = 400;
            float pAX = 0, pAY = 0;
            if (proba.get("payment_address_top")) {
                pAX = posHdrX;
                pAY = containerDate.getBBox().getPosY() - containerDate.getBBox().getHeight() - 5;
            }
            else if (proba.get("payment_address_bottom")) {
                pAX = proba.get("signature_bottom_left") ? posHdrX: 90;
                pAY = bottomPageMargin + 120 + rnd.nextInt(5);
            }
            proba.put("vendor_tax_number_top", proba.get("vendor_address_tax_number"));

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,7,9,pAW,lineStrokeColor,model,document,payment,company,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(contentStream,writer);
        }

        // check if billing and shipping addresses should be switched
        float addrShift = rnd.nextInt(10);
        float leftAddrX = leftPageMargin+30;
        float rightAddrX = leftPageMargin+290;
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX+addrShift; float billY = pageHeight-158;
        float shipX = rightAddrX+addrShift; float shipY = billY;

        // save orig bordered mode and reset later
        Boolean origBorderMode = proba.get("addresses_bordered");
        proba.put("addresses_bordered", false);
        // Billing address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,7,8,250,themeColor,model,document,client,annot,proba);
        billingInfoBox.translate(billX, billY);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,7,8,250,themeColor,model,document,client,annot,proba);
        shippingInfoBox.translate(shipX, shipY);

        // draw borders around bill+ship addresses & thrn build addresses
        float bAY = billY;
        float bAH = billingInfoBox.getBBox().getHeight();
        new BorderBox(themeColor,white,1, leftPageMargin+27,    bAY-bAH, 227,bAH+2).build(contentStream,writer);
        new BorderBox(themeColor,white,1, leftPageMargin+27+245,bAY-bAH, 227,bAH+2).build(contentStream,writer);
        billingInfoBox.build(contentStream,writer);
        shippingInfoBox.build(contentStream,writer);
        proba.put("addresses_bordered", origBorderMode);  // reset to origBorderMode

        // client mail & payment info under billing addr
        float box1PosX = billX;
        float box1PosY = billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight() - 6;
        float box1W = 85;
        float box1H = 11;
        Color box1Color = grayish;

        // labels info 1
        List<BorderBox> boxes1 = new ArrayList<BorderBox>();
        VerticalContainer labelInfo1 = new VerticalContainer(box1PosX, box1PosY, 250);
        labelInfo1.addElement(new SimpleTextBox(fontN,8,0,0,(rnd.nextBoolean()?"E":"E-")+"mail address"));
        boxes1.add(new BorderBox(box1Color,box1Color,1,labelInfo1.getBBox().getPosX(),labelInfo1.getBBox().getPosY()-labelInfo1.getBBox().getHeight()-1,box1W,box1H));
        labelInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getLabelOrder()));
        boxes1.add(new BorderBox(box1Color,box1Color,1,labelInfo1.getBBox().getPosX(),labelInfo1.getBBox().getPosY()-labelInfo1.getBBox().getHeight()-1,box1W,box1H));
        labelInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getLabelPaymentType()+""));
        boxes1.add(new BorderBox(box1Color,box1Color,1,labelInfo1.getBBox().getPosX(),labelInfo1.getBBox().getPosY()-labelInfo1.getBBox().getHeight()-1,box1W,box1H));
        labelInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getLabelPaymentTerm()+""));
        boxes1.add(new BorderBox(box1Color,box1Color,1,labelInfo1.getBBox().getPosX(),labelInfo1.getBBox().getPosY()-labelInfo1.getBBox().getHeight()-1,box1W,box1H));
        labelInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getLabelAccountCurrency()+""));
        boxes1.add(new BorderBox(box1Color,box1Color,1,labelInfo1.getBBox().getPosX(),labelInfo1.getBBox().getPosY()-labelInfo1.getBBox().getHeight()-1,box1W,box1H));

        for (BorderBox box : boxes1) box.build(contentStream,writer);
        labelInfo1.build(contentStream,writer);

        // values info 1
        VerticalContainer valueInfo1 = new VerticalContainer(box1PosX+box1W+3,box1PosY,250);
        valueInfo1.addElement(new SimpleTextBox(fontN,8,0,0,client.getBillingName().split("\\s+")[0].toLowerCase()+"@gmail.com"));
        valueInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder(),"ONUM"));
        valueInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentType(),"PMODE"));
        valueInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentTerm(),"PTERM"));
        valueInfo1.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo1.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValueAccountCurrency(),"PCUR"));

        annot.getTotal().setCurrency(cur);
        valueInfo1.build(contentStream,writer);

        // client ID & shipping info under shipping addr
        float box2PosX = shipX;
        float box2PosY = box1PosY;
        float box2W = 85;
        float box2H = 11;
        Color box2Color = grayish;

        // labels info 2
        List<BorderBox> boxes2 = new ArrayList<BorderBox>();
        VerticalContainer labelInfo2 = new VerticalContainer(box2PosX, box2PosY, 250);
        labelInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getLabelClient()));
        boxes2.add(new BorderBox(box2Color,box2Color,1,labelInfo2.getBBox().getPosX(),labelInfo2.getBBox().getPosY()-labelInfo2.getBBox().getHeight()-1,box2W,box2H));
        labelInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelOrder()));
        boxes2.add(new BorderBox(box2Color,box2Color,1,labelInfo2.getBBox().getPosX(),labelInfo2.getBBox().getPosY()-labelInfo2.getBBox().getHeight()-1,box2W,box2H));
        labelInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelShipping()));
        boxes2.add(new BorderBox(box2Color,box2Color,1,labelInfo2.getBBox().getPosX(),labelInfo2.getBBox().getPosY()-labelInfo2.getBBox().getHeight()-1,box2W,box2H));
        labelInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelInvoice()));
        boxes2.add(new BorderBox(box2Color,box2Color,1,labelInfo2.getBBox().getPosX(),labelInfo2.getBBox().getPosY()-labelInfo2.getBBox().getHeight()-1,box2W,box2H));
        labelInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueInvoice()));

        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        for (BorderBox box : boxes2) box.build(contentStream,writer);
        labelInfo2.build(contentStream,writer);

        // values info 2
        VerticalContainer valueInfo2 = new VerticalContainer(box2PosX+box2W+3,box2PosY,250);
        valueInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueClient(),"CNUM"));
        valueInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueOrder(),"IDATE"));
        valueInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueShipping(),"SDATE"));
        valueInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelPaymentDue()));
        new BorderBox(box2Color,box2Color,1,valueInfo2.getBBox().getPosX(),valueInfo2.getBBox().getPosY()-valueInfo2.getBBox().getHeight()-1,box2W,box2H).build(contentStream,writer);
        valueInfo2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfo2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValuePaymentDue()));

        annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        valueInfo2.build(contentStream,writer);

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
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin - 5;
        int maxHdrNum = 14;
        ProductTable pt = new ProductTable(pc, amtSuffix, model.getLang(), tableWidth, maxHdrNum);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow = pt.getConfigRow();
        Map<String, ProductTable.ColItem> itemMap = pt.getItemMap();

        // table text colors
        Color hdrTextColor = proba.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == Color.WHITE) ? Color.BLACK: Arrays.asList(Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        // table top info
        String tableTopText = pt.getTableTopInfo();
        tableTopText = tableTopText.equals("All prices are in")? tableTopText+" "+cur: tableTopText;
        float tableTopPosX = leftPageMargin+2;
        float tableTopPosY = labelInfo1.getBBox().getPosY() - labelInfo1.getBBox().getHeight() - 30;

        SimpleTextBox tableTopInfo = new SimpleTextBox(fontB,9,tableTopPosX,tableTopPosY,tableTopText,themeColor,white);
        tableTopInfo.build(contentStream,writer);

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

        VerticalContainer verticalTableItems = new VerticalContainer(leftPageMargin+2, tableTopInfo.getBBox().getPosY()-tableTopInfo.getBBox().getHeight()-5, 600);
        verticalTableItems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));
        verticalTableItems.addElement(new BorderBox(white,white, 0,0, 0, 0, 1));
        verticalTableItems.addElement(row1);
        verticalTableItems.addElement(new BorderBox(white,white, 0,0,0, 0, 1));
        verticalTableItems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));

        // table item list body
        String quantity; String snNum;
        Color cellTextColor; Color cellBgColor;
        for(int w=0; w<pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);
            cellTextColor = black;
            cellBgColor = (randomProduct.getName().equalsIgnoreCase("shipping")) ? Color.LIGHT_GRAY: white;
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
                cellBgColor = proba.get("alternate_table_items_bg_color") && w % 2 == 0 ? gray: cellBgColor;
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, 8, 0, 0, cellText, cellTextColor, cellBgColor, cellAlign, tableHeader+"Item");
                productLine.addElement(rowBox, false);
            }
            annot.getItems().add(randomItem);

            productLine.setBackgroundColor(cellBgColor);
            verticalTableItems.addElement(productLine);
            verticalTableItems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));
        }

        float tableItemsHeight = verticalTableItems.getBBox().getHeight();

        verticalTableItems.build(contentStream,writer);

        // tabel footer returns information
        float msgSize = 320;
        float posMsgX = leftPageMargin+2;
        float posMsgY = verticalTableItems.getBBox().getPosY()-verticalTableItems.getBBox().getHeight()-10;

        VerticalContainer tableFooterMsg = new VerticalContainer(posMsgX, posMsgY, 300);
        tableFooterMsg.addElement(new SimpleTextBox(fontN,10,0,0,"To return an item, go to the customer service section to obtain a return agreement."));
        tableFooterMsg.addElement(new SimpleTextBox(fontN,8,0,0,"* Order preparation costs include shipping costs"));
        tableFooterMsg.build(contentStream,writer);

        // Add vertical borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopPosY - tableTopInfo.getBBox().getHeight() - 4;
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor).build(contentStream,writer);
                xPos += configRow[i];
            }
        }

        // label totals container sub-table
        int labelTCPosX = 370;
        int labelTCWidth = 110;
        List<BorderBox> labelTCBorders = new ArrayList<BorderBox>();
        VerticalContainer labelTC = new VerticalContainer(labelTCPosX,posMsgY-2,250);

        labelTC.addElement(new SimpleTextBox(fontNB,8,0,0,pc.getWithTaxAndDiscountTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,5,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Credit/Gift Card"));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Delivery Cost"));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Preparation Fees* "));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getWithTaxTotalHead()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTaxTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBBox().getPosY()-labelTC.getBBox().getHeight(),labelTCWidth,1));

        new BorderBox(lgray,gray,1,labelTCPosX-1,posMsgY-labelTC.getBBox().getHeight()-2,labelTCWidth,labelTC.getBBox().getHeight()+1).build(contentStream,writer);
        for (BorderBox labelBox: labelTCBorders) labelBox.build(contentStream,writer);
        labelTC.build(contentStream,writer);

        // value totals container sub-table
        int valueTCPosX = 480;
        int valueTCWidth = 82;
        List<BorderBox> valueTCBorders = new ArrayList<BorderBox>();
        VerticalContainer valueTC = new VerticalContainer(valueTCPosX,posMsgY-2,250);

        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTaxAndDiscount()+amtSuffix,"TA"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,"0.00"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,"--"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0, 0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,"0.00"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTax()+amtSuffix,"TA"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalTax()+amtSuffix,"TTX"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotal()+amtSuffix,"TWTX"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBBox().getPosY()-valueTC.getBBox().getHeight(),valueTCWidth,1));

        new BorderBox(lgray,white,1,valueTCPosX-3,posMsgY-valueTC.getBBox().getHeight()-2,valueTCWidth,valueTC.getBBox().getHeight()+1).build(contentStream,writer);
        for (BorderBox valueBox: valueTCBorders) valueBox.build(contentStream,writer);
        valueTC.build(contentStream,writer);

        annot.getTotal().setSubtotalPrice(pc.getFmtTotal()+amtSuffix);
        annot.getTotal().setTaxPrice(pc.getFmtTotalTax()+amtSuffix);
        annot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount()+amtSuffix);
        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        float posTableFooterY = valueTC.getBBox().getPosY() - valueTC.getBBox().getHeight() - 3;
        SimpleTextBox discInfoBox = new SimpleTextBox(fontI,9,0,0,"No discount will be applied in case of early payment");
        discInfoBox.translate(pageMiddleX - discInfoBox.getBBox().getWidth()/2, posTableFooterY);
        discInfoBox.build(contentStream,writer);

        new BorderBox(lgray,white,1,124,posTableFooterY-34,351,14).build(contentStream,writer);
        new BorderBox(lgray,white,1,124,posTableFooterY-47,351,14).build(contentStream,writer);

        int sizeTab1;
        for(sizeTab1=0; sizeTab1<Math.max(pc.getProducts().size()-2, 1); sizeTab1++) {
            new BorderBox(lgray,white,1,124,posTableFooterY-47-13*sizeTab1,351,14).build(contentStream,writer);
        }

        new BorderBox(lgray,white,1,124,posTableFooterY-47-13*(sizeTab1),351,14).build(contentStream,writer);

        new SimpleTextBox(fontN,8,189,posTableFooterY-21,"Details of eco-participation and private copy remuneration").build(contentStream,writer);
        new SimpleTextBox(fontN,8,170,posTableFooterY-35,"Serial").build(contentStream,writer);
        new SimpleTextBox(fontN,8,250,posTableFooterY-35,"ECO-PARTICIPATION Tax").build(contentStream,writer);
        new SimpleTextBox(fontN,8,380,posTableFooterY-35,"Private Copy Tax").build(contentStream,writer);

        new BorderBox(lgray,lgray,1,240,posTableFooterY-60-14*(sizeTab1-1)+sizeTab1,1,13*(sizeTab1+1)).build(contentStream,writer);
        new BorderBox(lgray,lgray,1,360,posTableFooterY-60-14*(sizeTab1-1)+sizeTab1,1,13*(sizeTab1+1)).build(contentStream,writer);

        int sizeTab2;
        for(sizeTab2=0; sizeTab2<Math.max(pc.getProducts().size()-2, 1); sizeTab2++) {
            Product randomProduct = pc.getProducts().get(sizeTab2);
            new SimpleTextBox(fontN, 8, 155, posTableFooterY-47-13*sizeTab2, randomProduct.getEan(), "SNO").build(contentStream,writer);
            new SimpleTextBox(fontN, 8, 283, posTableFooterY-47-13*sizeTab2, "N/A").build(contentStream,writer);
        }

        // Add Signature at bottom
        if (proba.get("signature_bottom")) {
            String compSignatureName = company.getName();
            compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0, company.getSignature().getLabel()+" "+compSignatureName, "Signature");

            float sigTX, sigTY = bottomPageMargin + 43;
            if (proba.get("signature_bottom_left")) {  // bottom left
                sigTX = leftPageMargin + 55;
            } else {                                     // bottom right
                sigTX = pageWidth - sigTextBox.getBBox().getWidth() - 75;
            }
            sigTextBox.translate(sigTX,sigTY);
            sigTextBox.build(contentStream,writer);

            new HorizontalLineBox(
                    sigTX - 10, sigTY + 2,
                    sigTX + sigTextBox.getBBox().getWidth() + 5, sigTY + 2,
                    lineStrokeColor).build(contentStream,writer);

            String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

            float maxSW = 120, maxSH = 60;
            float sigScale = Math.min(maxSW/sigImg.getWidth(), maxSH/sigImg.getHeight());
            float sigW = sigImg.getWidth() * sigScale;
            float sigH = sigImg.getHeight() * sigScale;
            // align signature to center of sigTextBox bbox
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(contentStream,writer);
        }
        // no stamp or signature req info
        if (!proba.get("signature_bottom") && !proba.get("stamp_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignBox = new SimpleTextBox(fontN,7,0,0,noStampSignMsg,"Footnote");
            noStampSignBox.translate(pageMiddleX-noStampSignBox.getBBox().getWidth()/2, 60);
            noStampSignBox.build(contentStream,writer);
        }
        // Add company stamp watermark, 40% prob
        if (proba.get("stamp_bottom")) {
            float alpha = HelperCommon.rand_uniform(0.6f, 0.8f);
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp, yPosStamp;
            // draw to lower right if signature in bottom or lower left if signature in bottom left
            if (proba.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = ((proba.get("signature_bottom_left")) ? leftPageMargin+6 : pageMiddleX - 80) + rnd.nextInt(10);
                yPosStamp = bottomPageMargin + 50 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = bottomPageMargin + 50 + rnd.nextInt(5);
            }
            StampBox stampBox = new StampBox(resDim,resDim,alpha,model,document,company,proba);
            stampBox.translate(xPosStamp,yPosStamp);
            stampBox.build(contentStream,writer);
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

        // footer website & addr info
        SimpleTextBox footerEmail = new SimpleTextBox(fontB,8,0,0,company.getWebsite());
        footerEmail.translate(pageMiddleX-footerEmail.getBBox().getWidth()/2, bottomPageMargin-5);
        footerEmail.build(contentStream,writer);
        HorizontalContainer footercontainer = new HorizontalContainer(0,0);
        if (model.getLang().matches("fr")) {
            footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,"N°RCS : "));
            footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,company.getIdNumbers().getCidValue(),"SCID"));
        }
        footercontainer.addElement(new SimpleTextBox(fontN,8,0,0," "+company.getAddress().getCity()));
        footercontainer.translate(pageMiddleX - footercontainer.getBBox().getWidth()/2, bottomPageMargin-13);
        footercontainer.build(contentStream,writer);

        contentStream.close();
        writer.writeEndElement();
    }
}
