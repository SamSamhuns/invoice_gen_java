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
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.ProductTable;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

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
        Color lineStrokeColor = genProb.get("line_stroke_black") ? Color.BLACK: themeColor;
        Color grayish = HelperCommon.getRandomGrayishColor();

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);

        /*//////////////////   Build Page components now   //////////////////*/

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        /// Draw top left logo
        float maxLogoWidth = 200;
        float maxLogoHeight = 100;
        float logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        float logoWidth = logoImg.getWidth() * logoScale;
        float logoHeight = logoImg.getHeight() * logoScale;
        float posLogoX = leftPageMargin;
        float posLogoY = pageHeight - logoHeight - topPageMargin;
        contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);

        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        // Top center document title
        if (genProb.get("doc_title_top_center")) {
            SimpleTextBox docTitleBox = new SimpleTextBox(fontB,16,0,0,docTitle,"SN");
            docTitleBox.translate(pageMiddleX-(docTitleBox.getBBox().getWidth()/2), pageHeight-80);
            docTitleBox.build(contentStream, writer);
            modelAnnot.setTitle(docTitle);
        }
        // Top right company header info
        // top right document title
        VerticalContainer headerCont = new VerticalContainer(0, pageHeight-topPageMargin, 250);  // PosX is reset later for alignment
        if (genProb.get("doc_title_top_right") && !genProb.get("doc_title_top_right")) {
            headerCont.addElement(new SimpleTextBox(fontB,13,0,0,docTitle,"SN"));
            headerCont.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));
            modelAnnot.setTitle(docTitle);
        }
        // Vendor name
        headerCont.addElement(new SimpleTextBox(fontNB,10,0,0,company.getName(),"SN"));
        // Invoice date
        headerCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getDate().getValueInvoice(),grayish,Color.WHITE,"IDATE"));
        modelAnnot.getVendor().setVendorName(company.getName());
        modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());

        // Purchase Order Number
        if (genProb.get("purchase_order_number_top_right")) {
            headerCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder(),grayish,Color.WHITE,"LO"));
            modelAnnot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // vendor tax number
        else if (genProb.get("vendor_tax_number_top_right")) {
            headerCont.addElement(new SimpleTextBox(fontN,10,0,0,company.getIdNumbers().getVatLabel() + ": " + company.getIdNumbers().getVatValue(),grayish,Color.WHITE,"SVAT"));
            modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }
        // invoice id number
        HorizontalContainer invNumCont = new HorizontalContainer(0, 0);
        invNumCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getLabelInvoice()+" ",grayish,Color.WHITE));
        invNumCont.addElement(new SimpleTextBox(fontN,10,0,0,model.getReference().getValueInvoice(),grayish,Color.WHITE,"IN"));
        modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());

        headerCont.addElement(invNumCont);

        headerCont.translate(pageWidth - headerCont.getBBox().getWidth() - rightPageMargin, 0);  // align top right header to fit properly
        headerCont.build(contentStream, writer);

        // check if billing and shipping addresses should be switched
        float leftAddrX = 120 + rnd.nextInt(15);
        float rightAddrX = 335 + rnd.nextInt(15);
        if (genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight-110;
        float shipX = rightAddrX; float shipY = pageHeight-110;

        // Billing Address
        String clientBillAddr = client.getBillingAddress().getLine1()+" "+client.getBillingAddress().getZip()+" "+client.getBillingAddress().getCity();
        VerticalContainer billAddrCont = new VerticalContainer(billX,billY,250);
        billAddrCont.addElement(new SimpleTextBox(fontB,9, 0,0,client.getBillingHead(),grayish,Color.WHITE));
        billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getBillingName(),"BN"));
        billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getBillingAddress().getLine1(),"BA"));
        billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getBillingAddress().getZip()+" "+client.getBillingAddress().getCity(),"BA"));
        if (genProb.get("bill_address_phone_fax")) {
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getPhoneLabel()+": "+client.getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getFaxLabel()+": "+client.getBillingContactNumber().getFaxValue(), "BF"));
        } else {
            billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getBillingAddress().getCountry(),"BA"));
            clientBillAddr += " " + client.getBillingAddress().getCountry();
        }
        if (genProb.get("bill_address_tax_number")) {
            billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BA"));
            modelAnnot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        if (genProb.get("addresses_bordered") && client.getBillingHead().length() > 0) {
            billAddrCont.setBorderColor(lineStrokeColor);
            billAddrCont.setBorderThickness(0.5f);
        }
        modelAnnot.getBillto().setCustomerName(client.getBillingName());
        modelAnnot.getBillto().setCustomerAddr(clientBillAddr);
        modelAnnot.getBillto().setCustomerPOBox(client.getBillingAddress().getZip());
        billAddrCont.build(contentStream,writer);

        // Shipping Address
        String clientShipAddr = client.getShippingAddress().getLine1()+" "+client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity();
        VerticalContainer shipAddrCont = new VerticalContainer(shipX,shipY,250);
        shipAddrCont.addElement(new SimpleTextBox(fontB,9,0,0,client.getShippingHead(),grayish,Color.WHITE));
        shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getShippingName(),"SHN"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getShippingAddress().getLine1(),"SHA"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity(),"SHA"));
        if (genProb.get("bill_address_phone_fax") && genProb.get("ship_address_phone_fax")) {
            String connec = (client.getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getPhoneLabel()+connec+client.getShippingContactNumber().getPhoneValue(), "SHC"));
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getFaxLabel()+connec+client.getShippingContactNumber().getFaxValue(), "SHF"));
        } else {
            shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getShippingAddress().getCountry(),"SHA"));
            clientShipAddr += " " + client.getShippingAddress().getCountry();
        }
        if (genProb.get("addresses_bordered") && client.getShippingHead().length() > 0) {
            shipAddrCont.setBorderColor(lineStrokeColor);
            shipAddrCont.setBorderThickness(0.5f);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            modelAnnot.getShipto().setShiptoName(client.getShippingName());
            if (client.getShippingContactNumber().getPhoneLabel().length() > 0) {
                modelAnnot.getShipto().setShiptoPOBox(client.getShippingAddress().getZip());
                modelAnnot.getShipto().setShiptoAddr(client.getShippingAddress().getLine1()+" "+client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity());
            }
        }
        shipAddrCont.build(contentStream,writer);

        // Left side info
        int leftFSize = 7;
        VerticalContainer infoOrder = new VerticalContainer(leftPageMargin,pageHeight-211,76);
        // Purhase Order Number right if not at the top
        if (genProb.get("purchase_order_number_left") && !genProb.get("purchase_order_number_top_right")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,model.getReference().getLabelOrder()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,model.getReference().getValueOrder(),"ONUM"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            modelAnnot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        }
        // Payment Due
        if (genProb.get("payment_due_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,model.getDate().getLabelPaymentDue()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,model.getDate().getValuePaymentDue(),"PDATE"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            modelAnnot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        // Currency Used
        if (genProb.get("currency_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelAccountCurrency()));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,cur, "CUR"));
            modelAnnot.getTotal().setCurrency(cur);
        }
        infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelPaymentType()));
        infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,payment.getValuePaymentType(),"PMODE"));
        infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,pc.getFmtTotalWithTaxAndDiscount()+" "+cur,"TTX"));
        modelAnnot.getTotal().setCurrency(cur);
        modelAnnot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount());

        infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        // Payment Terms
        if (genProb.get("payment_terms_left")) {
            infoOrder.addElement(new SimpleTextBox(fontNB,leftFSize,0,0,payment.getLabelPaymentTerm(), "PTL"));
            infoOrder.addElement(new SimpleTextBox(fontN,leftFSize,0,0,payment.getValuePaymentTerm(), "PTV"));
            infoOrder.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
            modelAnnot.getInvoice().setPaymentTerm(payment.getValuePaymentTerm());
        }
        infoOrder.build(contentStream,writer);

        // table top horizontal line, will be built after verticalTableItems
        float ttx1 = 85; float tty1 = billAddrCont.getBBox().getPosY() - billAddrCont.getBBox().getHeight() - 15;
        float ttx2 = pageWidth-rightPageMargin; float tty2 = tty1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(ttx1, tty1, ttx2, tty2, lineStrokeColor);

        ////////////////////////////////////      Building Table      ////////////////////////////////////
        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (genProb.get("currency_in_table_items")) {
              amtSuffix = " "+cur;
              modelAnnot.getTotal().setCurrency(cur);
        }
        boolean upperCap = rnd.nextBoolean();  // table header items case
        HAlign tableHdrAlign = genProb.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;

        // always set to false but individually change SimpleTextBox HAlign
        boolean centerAlignItems = false;
        // table text colors
        Color hdrTextColor = genProb.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // hdrTextColor black (predominantly) or white
        Color hdrBgColor = (hdrTextColor == Color.WHITE) ? Color.BLACK: Arrays.asList(Color.GRAY, Color.LIGHT_GRAY, Color.WHITE).get(rnd.nextInt(3)); // hdrBgColor should be contrasting to hdrTextColor

        // Building Header Item labels, table values and footer labels list
        float tableWidth = pageWidth - rightPageMargin - ttx1;
        int maxHdrNum = 8;
        ProductTable pt = new ProductTable(pc, amtSuffix, model.getLang(), tableWidth, maxHdrNum);
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

        VerticalContainer verticalTableItems = new VerticalContainer(ttx1, tty1, 600);
        verticalTableItems.addElement(row1);
        verticalTableItems.addElement(new HorizontalLineBox(0, 0, pageWidth-rightPageMargin, 0, lineStrokeColor));
        verticalTableItems.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 0, 5));

        // table item list body
        String quantity; String snNum;
        Color cellTextColor = Color.BLACK;
        Color cellBgColor = Color.WHITE;
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

            verticalTableItems.addElement(productLine);
        }
        verticalTableItems.addElement(new BorderBox(Color.WHITE, cellBgColor, 0, 0, 0, 0, 5));
        verticalTableItems.build(contentStream,writer);
        tableTopInfoLine.build(contentStream,writer);

        float tableItemsHeight = verticalTableItems.getBBox().getHeight();
        // Add borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = ttx1;
            float yPos = tty1;
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                HelperImage.drawLine(contentStream, xPos-2, yPos, xPos-2, yPos - tableItemsHeight, lineStrokeColor);
                xPos += configRow[i];
            }
            HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
        }

        float tableBottomY = verticalTableItems.getBBox().getPosY() - tableItemsHeight;
        new HorizontalLineBox(ttx1, tableBottomY, ttx2, tableBottomY, lineStrokeColor).build(contentStream,writer);
        new BorderBox(hdrBgColor,hdrBgColor,1,395,tableBottomY-50,166,45).build(contentStream,writer);

        // Totals and Taxes calculations
        new SimpleTextBox(fontN, 9, 400, tableBottomY-6, pc.getTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 495, tableBottomY-6, pc.getFmtTotal()+amtSuffix, hdrTextColor,hdrBgColor,"TWTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 400, tableBottomY-19, pc.getTaxTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 495, tableBottomY-19, pc.getFmtTotalTax()+amtSuffix, hdrTextColor,hdrBgColor,"TTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 400, tableBottomY-33, pc.getWithTaxAndDiscountTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 495, tableBottomY-33, pc.getFmtTotalWithTaxAndDiscount()+amtSuffix, hdrTextColor,hdrBgColor,"TA").build(contentStream,writer);

        modelAnnot.getTotal().setSubtotalPrice(pc.getFmtTotal()+amtSuffix);
        modelAnnot.getTotal().setTaxPrice(pc.getFmtTotalTax()+amtSuffix);
        modelAnnot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount()+amtSuffix);
        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // Payment Address
        if (genProb.get("payment_address")) {
            // Set paymentAddrCont opposite to the signature location
            float paymentAddrXPos = (genProb.get("signature_bottom_left")) ? rightAddrX: ttx1;
            float paymentAddrYPos = tableBottomY-60;

            VerticalContainer paymentAddrCont = new VerticalContainer(paymentAddrXPos, paymentAddrYPos, 300);

            paymentAddrCont.addElement(new SimpleTextBox(fontB,10,0,0, payment.getAddressHeader(), "PH"));

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
            if (genProb.get("payment_vendor_tax_number") && !genProb.get("vendor_tax_number_top_right")) {
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

        // Footer company info
        int footerFontSize = 7 + rnd.nextInt(3);
        HorizontalContainer infoEntreprise = new HorizontalContainer(0,0);
        infoEntreprise.addElement(new SimpleTextBox(fontNB,footerFontSize,0,0, company.getName(),"SN"));
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
        // vendor tax number
        if (genProb.get("vendor_tax_number_bottom") && !genProb.get("vendor_tax_number_top_right")) {
            infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " - "+ idNumbers.getVatLabel() +" : "));
            infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, idNumbers.getVatValue(),"SVAT"));
            modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, " - "+company.getContact().getFaxLabel()+" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, company.getContact().getFaxValue(),"SFAX"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, company.getContact().getPhoneLabel()+" : "));
        infoEntreprise3.addElement(new SimpleTextBox(fontN,footerFontSize,0,0, company.getContact().getPhoneValue(),"SCN"));
        infoEntreprise.translate(pageMiddleX-infoEntreprise.getBBox().getWidth()/2,61);
        infoEntreprise2.translate(pageMiddleX-infoEntreprise2.getBBox().getWidth()/2,53);
        infoEntreprise3.translate(pageMiddleX-infoEntreprise3.getBBox().getWidth()/2,45);

        String vendorAddr = company.getName()+" "+company.getAddress().getLine1()+" "+company.getAddress().getZip()+" "+company.getAddress().getCity();
        modelAnnot.getVendor().setVendorName(company.getName());
        modelAnnot.getVendor().setVendorAddr(vendorAddr);

        infoEntreprise.build(contentStream,writer);
        infoEntreprise2.build(contentStream,writer);
        infoEntreprise3.build(contentStream,writer);

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
                  singatureTextxPos = pageWidth - singatureTextBox.getBBox().getWidth() - 50;
              }

              singatureTextBox.getBBox().setPosX(singatureTextxPos);
              singatureTextBox.build(contentStream, writer);
              new HorizontalLineBox(
                      singatureTextxPos - 10, 135,
                      singatureTextxPos + singatureTextBox.getBBox().getWidth() + 10, 135
                      ).build(contentStream, writer);
              String signaturePath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
              PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
              int signatureWidth = 120;
              int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
              // align signature to center of singatureTextBox bbox
              float signatureXPos = singatureTextBox.getBBox().getPosX() + singatureTextBox.getBBox().getWidth()/2 - signatureWidth/2;
              float signatureYPos = 140;
              contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add company stamp watermark, 40% prob
        if (genProb.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + company.getStamp().getFullPath());
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
            String noStampSignMsg = "*This document is computer generated and does not require a signature or the Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignMsgBox = new SimpleTextBox(fontN, footerFontSize-1, 0, 80, noStampSignMsg, "Footnote");
            // align the text to the center
            noStampSignMsgBox.getBBox().setPosX(pageMiddleX - noStampSignMsgBox.getBBox().getWidth()/2);
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
