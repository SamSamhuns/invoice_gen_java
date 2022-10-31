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
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;

import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.line.VerticalLineBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
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
import java.util.Arrays;
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

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        /// Draw top left logo
        float maxLogoWidth = 200;
        float maxLogoHeight = 100;
        float logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        float logoWidth = logoImg.getWidth() * logoScale;
        float logoHeight = logoImg.getHeight() * logoScale;
        float posLogoX = leftPageMargin;
        float posLogoY = pageHeight - topPageMargin;
        new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(contentStream,writer);

        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        // Top center document title
        if (proba.get("doc_title_top_center")) {
            SimpleTextBox docTitleBox = new SimpleTextBox(fontB,16,0,0,docTitle,"SN");
            docTitleBox.translate(pageMiddleX-(docTitleBox.getBBox().getWidth()/2), pageHeight-80);
            docTitleBox.build(contentStream,writer);
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
        headerCont.build(contentStream,writer);

        // check if billing and shipping addresses should be switched
        float leftAddrX = 120 + rnd.nextInt(15);
        float rightAddrX = 335 + rnd.nextInt(15);
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight-110;
        float shipX = rightAddrX; float shipY = pageHeight-110;

        // Billing Address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,document,client,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(contentStream,writer);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,document,client,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(contentStream,writer);

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
        infoOrder.build(contentStream,writer);

        // table top horizontal line, will be built after verticalTableItems
        float ttx1 = 85; float tty1 = billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight() - 15;
        float ttx2 = pageWidth-rightPageMargin; float tty2 = tty1;
        HorizontalLineBox tableTopInfoLine = new HorizontalLineBox(ttx1, tty1, ttx2, tty2, lineStrokeColor);

        ////////////////////////////////////      Building Table      ////////////////////////////////////
        // check if cur should be included in table amt items
        String amtSuffix = "";
        if (proba.get("currency_in_table_items")) {
            amtSuffix = " "+cur;
            annot.getTotal().setCurrency(cur);
        }
        boolean upperCap = rnd.nextBoolean();  // table header items case
        HAlign tableHdrAlign = proba.get("table_center_align_items") ? HAlign.CENTER : HAlign.LEFT;

        // always set to false but individually change SimpleTextBox HAlign
        boolean centerAlignItems = false;
        // table text colors
        Color hdrTextColor = proba.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // hdrTextColor black (predominantly) or white
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
            if (proba.get("currency_in_table_headers") && !proba.get("currency_in_table_items") && pt.getNumericalHdrs().contains(tableHeader)) {
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
            annot.getItems().add(randomItem);

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
            new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor).build(contentStream,writer);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                new VerticalLineBox(xPos-2, yPos, xPos-2, yPos - tableItemsHeight, lineStrokeColor).build(contentStream,writer);
                xPos += configRow[i];
            }
            new VerticalLineBox(xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor).build(contentStream,writer);
        }

        float tableBottomY = verticalTableItems.getBBox().getPosY() - tableItemsHeight;
        new HorizontalLineBox(ttx1, tableBottomY, ttx2, tableBottomY, lineStrokeColor).build(contentStream,writer);
        new BorderBox(hdrBgColor,hdrBgColor,1,395,tableBottomY-50,166,45).build(contentStream,writer);

        // Table footer Totals calculations
        // totals labels
        new SimpleTextBox(fontNB, 9, 400, tableBottomY-6,  pc.getTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        new SimpleTextBox(fontNB, 9, 400, tableBottomY-19, pc.getTaxTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        new SimpleTextBox(fontNB, 9, 400, tableBottomY-33, pc.getWithTaxAndDiscountTotalHead(), hdrTextColor,hdrBgColor).build(contentStream,writer);
        // totals values
        new SimpleTextBox(fontN, 9, 495, tableBottomY-6,  pc.getFmtTotal()+amtSuffix, hdrTextColor,hdrBgColor,"TWTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 495, tableBottomY-19, pc.getFmtTotalTax()+amtSuffix, hdrTextColor,hdrBgColor,"TTX").build(contentStream,writer);
        new SimpleTextBox(fontN, 9, 495, tableBottomY-33, pc.getFmtTotalWithTaxAndDiscount()+amtSuffix, hdrTextColor,hdrBgColor,"TA").build(contentStream,writer);

        annot.getTotal().setSubtotalPrice(pc.getFmtTotal()+amtSuffix);
        annot.getTotal().setTaxPrice(pc.getFmtTotalTax()+amtSuffix);
        annot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount()+amtSuffix);
        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 300;
            float pAX = (proba.get("signature_bottom_left")) ? rightAddrX: ttx1;
            float pAY = tableBottomY-60;

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,document,payment,company,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(contentStream,writer);
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
            sigTextBox.build(contentStream,writer);

            new HorizontalLineBox(
                    sigTX - 10, sigTY + 5,
                    sigTX + sigTextBox.getBBox().getWidth() + 10, sigTY + 5
                    ).build(contentStream,writer);

            String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

            float maxSW = 110, maxSH = 65;
            float sigScale = Math.min(maxSW/sigImg.getWidth(), maxSH/sigImg.getHeight());
            float sigW = sigImg.getWidth() * sigScale;
            float sigH = sigImg.getHeight() * sigScale;
            // align signature to center of sigTextBox bbox
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(contentStream,writer);
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
            stampBox.build(contentStream,writer);
        }
        // if no signature and no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or the Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignMsgBox = new SimpleTextBox(fontN,8,0,80, noStampSignMsg, "footnote");
            // align the text to the center
            noStampSignMsgBox.getBBox().setPosX(pageMiddleX - noStampSignMsgBox.getBBox().getWidth()/2);
            noStampSignMsgBox.build(contentStream,writer);
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
            footCompanyBox.build(contentStream,writer);
        }

        contentStream.close();
        writer.writeEndElement();
    }
}
