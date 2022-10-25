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
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductTable;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class CdiscountLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Cdiscount";
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

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> genProb = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 34;
        float rightPageMargin = 34;
        float topPageMargin = 34;
        float bottomPageMargin = 22;

        // colors
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> (int)(v*0.9f)).collect(Collectors.toList()); // darken colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = genProb.get("line_stroke_black") ? Color.BLACK: themeColor;
        Color grayish = HelperCommon.getRandomColor(3);
        Color gray = new Color(239,239,239);
        Color lgray = Color.LIGHT_GRAY;
        Color white = Color.WHITE;

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
        float posLogoY = pageHeight-logoHeight-3;
        contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);

        // top left info
        // vendor address
        Address cAddr = company.getAddress();
        VerticalContainer vendorAddrCont = new VerticalContainer(leftPageMargin, posLogoY-2, 250);
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getName()+"","SN"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, cAddr.getLine1(),"SA"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, cAddr.getZip() +"  "+ cAddr.getCity(),"SA"));
        vendorAddrCont.addElement(new BorderBox(white,white,0,0,0,0,3));
        if (genProb.get("vendor_address_phone_fax")) {
            vendorAddrCont.addElement(new SimpleTextBox(fontN, 8, 0, 0, company.getContact().getPhoneLabel()+": "+company.getContact().getPhoneValue(), "SC"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN, 8, 0, 0, company.getContact().getFaxLabel()+": "+company.getContact().getFaxValue(), "SF"));
        }
        // specific to french invoices
        else if (model.getLang().matches("fr")) {
            HorizontalContainer rcs = new HorizontalContainer(0,0);
            rcs.addElement(new SimpleTextBox(fontN,8,0,0,"N°RCS "));
            rcs.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getCidValue(), "SCID"));
            rcs.addElement(new SimpleTextBox(fontN,8,0,0, " " +cAddr.getCity(), "SA"));
            vendorAddrCont.addElement(rcs);
            HorizontalContainer siret = new HorizontalContainer(0,0);
            siret.addElement(new SimpleTextBox(fontN,8,0,0,"Siret "));
            siret.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getSiretValue(), "SSIRET"));
            vendorAddrCont.addElement(siret);
            HorizontalContainer naf = new HorizontalContainer(0,0);
            naf.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getToaLabel()+" : "));
            naf.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getToaValue(), "STOA"));
            vendorAddrCont.addElement(naf);
        }
        // vendor tax id number
        if (genProb.get("vendor_address_tax_number")) {
            HorizontalContainer vatText = new HorizontalContainer(0,0);
            vatText.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getVatLabel()+" : "));
            vatText.addElement(new SimpleTextBox(fontN,8,0,0, company.getIdNumbers().getVatValue(), "SVAT"));
            vendorAddrCont.addElement(vatText);
            modelAnnot.getVendor().setVendorTrn(company.getIdNumbers().getVatValue());
        }
        modelAnnot.getVendor().setVendorName(company.getName());
        modelAnnot.getVendor().setVendorAddr(cAddr.getLine1()+" "+cAddr.getZip()+" "+cAddr.getCity());
        modelAnnot.getVendor().setVendorPOBox(cAddr.getZip());
        vendorAddrCont.build(contentStream,writer);

        // top right info
        float hdrWidth = 252;
        float hdrHeight = 14;
        float posHdrX = pageWidth-rightPageMargin-hdrWidth;
        float posHdrY = pageHeight-topPageMargin-hdrHeight;
        // tax invoice number
        HorizontalContainer containerInvNum = new HorizontalContainer(0,0);
        containerInvNum.addElement(new SimpleTextBox(fontNB,9,0,0, model.getReference().getLabelInvoice()+" "));
        containerInvNum.addElement(new SimpleTextBox(fontB,9,0,0, model.getReference().getValueInvoice(), "IN"));
        containerInvNum.translate(posHdrX + hdrWidth/2 - containerInvNum.getBoundingBox().getWidth()/2, posHdrY+10);
        // tax invoice date
        HorizontalContainer containerDate = new HorizontalContainer(0,0);
        containerDate.addElement(new SimpleTextBox(fontNB,9,0,0, "From "));
        containerDate.addElement(new SimpleTextBox(fontB,9,0,0, model.getDate().getValueInvoice(), "IDATE"));
        containerDate.translate(posHdrX + hdrWidth/2 - containerDate.getBoundingBox().getWidth()/2, posHdrY-4);

        new BorderBox(gray,gray, 1, posHdrX, posHdrY-4, hdrWidth, hdrHeight).build(contentStream,writer);
        new BorderBox(gray,gray, 1, posHdrX, posHdrY-hdrHeight-6, hdrWidth, hdrHeight).build(contentStream,writer);

        modelAnnot.setTitle(model.getReference().getLabelInvoice());
        modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        containerInvNum.build(contentStream,writer);
        containerDate.build(contentStream,writer);

        new BorderBox(Color.BLACK,white,1,34,42,530,652).build(contentStream,writer);

        // Billing address
        Address bAddr = client.getBillingAddress();
        new SimpleTextBox(fontB,9,87,pageHeight-158,client.getBillingHead(),themeColor,white).build(contentStream,writer);
        new BorderBox(themeColor,white,1,87,pageHeight-170-51,229,51).build(contentStream,writer);

        VerticalContainer billAddrCont = new VerticalContainer(90, pageHeight-172, 250 );
        billAddrCont.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getBillingName(),"BN"));
        billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bAddr.getLine1(),"BA"));
        billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, bAddr.getZip()+" "+ bAddr.getCity().toUpperCase() ,"BA"));
        if (genProb.get("bill_address_tax_number")) {
            billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0,client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BT"));
            modelAnnot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        else if (genProb.get("bill_address_phone_fax")) {
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getPhoneLabel()+": "+client.getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getBillingContactNumber().getFaxLabel()+": "+client.getBillingContactNumber().getFaxValue(), "BF"));
        }
        modelAnnot.getBillto().setCustomerName(client.getBillingName());
        modelAnnot.getBillto().setCustomerAddr(bAddr.getLine1()+" "+bAddr.getZip()+" "+bAddr.getCity());
        modelAnnot.getBillto().setCustomerPOBox(bAddr.getZip());
        billAddrCont.build(contentStream,writer);

        // Shipping Address
        Address sAddr = client.getShippingAddress();
        new SimpleTextBox(fontB,9,87+229,pageHeight-158,client.getShippingHead(),themeColor,white).build(contentStream,writer);
        new BorderBox(themeColor,white,1,87+230,pageHeight-170-51,189,51).build(contentStream,writer);

        VerticalContainer shipAddrCont = new VerticalContainer(320, pageHeight-172, 250 );
        shipAddrCont.addElement(new SimpleTextBox(fontNB, 9, 0, 0, client.getBillingName(),"SHN"));
        shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sAddr.getLine1(),"SHA"));
        shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, sAddr.getZip()+" "+ bAddr.getCity().toUpperCase() ,"SHA"));
        if (genProb.get("bill_address_phone_fax") & genProb.get("ship_address_phone_fax")) {
            String connec = (client.getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getPhoneLabel()+connec+client.getShippingContactNumber().getPhoneValue(), "BC"));
            shipAddrCont.addElement(new SimpleTextBox(fontN, 9, 0, 0, client.getShippingContactNumber().getFaxLabel()+connec+client.getShippingContactNumber().getFaxValue(), "BF"));
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            modelAnnot.getShipto().setShiptoName(client.getShippingName());
            if (client.getShippingContactNumber().getPhoneLabel().length() > 0) {
                modelAnnot.getShipto().setShiptoPOBox(sAddr.getZip());
                modelAnnot.getShipto().setShiptoAddr(sAddr.getLine1()+" "+sAddr.getZip()+" "+sAddr.getCity());
            }
        }
        shipAddrCont.build(contentStream,writer);

        VerticalContainer boxInfoClient = new VerticalContainer(87,pageHeight-223-14,250);
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));

        boxInfoClient.build(contentStream,writer);

        VerticalContainer labelInfoClient = new VerticalContainer(88,pageHeight-227,250);
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,"e-mail address"));
        labelInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getLabelOrder()));
        labelInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getLabelPaymentType()+""));
        labelInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelPayment()));

        labelInfoClient.build(contentStream,writer);

        VerticalContainer valueInfoClient = new VerticalContainer(175,pageHeight-227,250);
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,"companie@gmail.com"));
        valueInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder(),"ONUM"));
        valueInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentType(),"PMODE"));
        valueInfoClient.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValuePayment(),"IDATE"));

        valueInfoClient.build(contentStream,writer);

        VerticalContainer boxInfoClient2 = new VerticalContainer(320,pageHeight-223-14,250);
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));

        boxInfoClient2.build(contentStream,writer);

        VerticalContainer labelInfoClient2 = new VerticalContainer(321,pageHeight-227,250);
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"ID Client"));
        labelInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelOrder()));
        labelInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"Date of Validation"));
        labelInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"Date of Sending"));
        labelInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueInvoice()));

        labelInfoClient2.build(contentStream,writer);

        new BorderBox(gray,gray,1,396,pageHeight-272,110,11).build(contentStream,writer);

        VerticalContainer valeurInfoClient2 = new VerticalContainer(397,pageHeight-227,250);
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueClient(),"CNUM"));
        valeurInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueOrder(),"IDATE"));
        valeurInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueOrder()));
        valeurInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"No of Parcels"));
        valeurInfoClient2.addElement(new BorderBox(white,white,0,0,0,0,2.5f));
        int codeColis = 100000000 + new Random().nextInt(900000000);
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0, "DT"+codeColis+"FR"));

        valeurInfoClient2.build(contentStream,writer);

        new SimpleTextBox(fontB,9,36,521,"Your Order",themeColor,white).build(contentStream,writer);

        ////////////////////////////////////      Building Table      ////////////////////////////////////

        float[] configRow = {62f, 189f, 48f, 62f, 62f, 62f, 39f};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Ref.", Color.BLACK, gray), true);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Labels", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Qty", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PUHT", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PUTTC", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PVTTC",Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "VAT", Color.BLACK, gray), false);

        VerticalContainer verticalTabletems = new VerticalContainer(36, 510, 600);
        verticalTabletems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));
        verticalTabletems.addElement(new BorderBox(white,white, 0,0, 0, 0, 1));
        verticalTabletems.addElement(firstLine);
        verticalTabletems.addElement(new BorderBox(white,white, 0,0,0, 0, 1));
        verticalTabletems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));

        for(int w=0; w< pc.getProducts().size(); w++) {
            Product randomProduct = pc.getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getCode(), "SNO"), true);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getName(), "PD"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtPrice(), "UP"), false);
            float puttcR = (float)(int)((randomProduct.getPrice() + randomProduct.getPrice() * randomProduct.getTaxRate())*100)/100;
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, puttcR + "", "undefined"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtTotalPriceWithTax(), "undefined"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getTaxRate() * 100 + "%", "TXR"), false);

            verticalTabletems.addElement(productLine);
            verticalTabletems.addElement(new BorderBox(lgray,lgray,0,0,0,pageWidth-(72),0.3f));
        }
        float ratioMsg = 8.65f;
        float msgSize = 320;
        float posMsgX = 36;
        float posMsgY = verticalTabletems.getBoundingBox().getPosY()-verticalTabletems.getBoundingBox().getHeight()-10;

        VerticalContainer tableFooterMsg = new VerticalContainer(posMsgX, posMsgY, 300);
        tableFooterMsg.addElement(new SimpleTextBox(fontN,10,0,0,"To return an item, go to the customer service section to obtain a return agreement."));
        tableFooterMsg.addElement(new SimpleTextBox(fontN,8,0,0,"* Order preparation costs include shipping costs"));
        tableFooterMsg.build(contentStream,writer);

        verticalTabletems.build(contentStream,writer);

        // label totals container sub-table
        int labelTCPosX = 370;
        int labelTCWidth = 110;
        List<BorderBox> labelTCBorders = new ArrayList<BorderBox>();
        VerticalContainer labelTC = new VerticalContainer(labelTCPosX,posMsgY-2,250);

        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getWithTaxTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,5,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Credit/Gift Card"));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Delivery"));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"Preparation Fees* "));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,"TOTAL NET TTC"));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTaxTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));
        labelTC.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTotalHead().toUpperCase()));
        labelTCBorders.add(new BorderBox(lgray,lgray,1,labelTCPosX-1,labelTC.getBoundingBox().getPosY()-labelTC.getBoundingBox().getHeight(),labelTCWidth,1));

        new BorderBox(lgray,gray,1,labelTCPosX-1,posMsgY-labelTC.getBoundingBox().getHeight()-2,labelTCWidth,labelTC.getBoundingBox().getHeight()+1).build(contentStream,writer);
        for (BorderBox labelBox: labelTCBorders) labelBox.build(contentStream,writer);
        labelTC.build(contentStream,writer);

        // value totals container sub-table
        int valueTCPosX = 480;
        int valueTCWidth = 82;
        List<BorderBox> valueTCBorders = new ArrayList<BorderBox>();
        VerticalContainer valueTC = new VerticalContainer(valueTCPosX,posMsgY-2,250);

        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTax(),"TA"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,"0,00"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,""));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0, 0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,"0,00"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTax(),"TA"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalTax(),"TTX"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));
        valueTC.addElement(new BorderBox(white,white,0,0,0,0,4));
        valueTC.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotal(),"TWTX"));
        valueTCBorders.add(new BorderBox(lgray,lgray,1,valueTCPosX-3,valueTC.getBoundingBox().getPosY()-valueTC.getBoundingBox().getHeight(),valueTCWidth,1));

        new BorderBox(lgray,white,1,valueTCPosX-3,posMsgY-valueTC.getBoundingBox().getHeight()-2,valueTCWidth,valueTC.getBoundingBox().getHeight()+1).build(contentStream,writer);
        for (BorderBox valueBox: valueTCBorders) valueBox.build(contentStream,writer);
        valueTC.build(contentStream,writer);

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        float posTableFooterY = valueTC.getBoundingBox().getPosY() - valueTC.getBoundingBox().getHeight() - 3;
        new SimpleTextBox(fontI,9,170,posTableFooterY,"No discount will be applied in case of early payment").build(contentStream,writer);

        new BorderBox(lgray,white,1,124,posTableFooterY-34,351,14).build(contentStream,writer);
        new BorderBox(lgray,white,1,124,posTableFooterY-47,351,14).build(contentStream,writer);

        int sizeTab;
        for(sizeTab=0; sizeTab< pc.getProducts().size(); sizeTab++) {
            new BorderBox(lgray,white,1,124,posTableFooterY-47-13*sizeTab,351,14).build(contentStream,writer);
        }

        new BorderBox(lgray,white,1,124,posTableFooterY-47-13*(sizeTab),351,14).build(contentStream,writer);

        new SimpleTextBox(fontN,8,189,posTableFooterY-21,"Details of eco-participation and private copy remuneration").build(contentStream,writer);
        new SimpleTextBox(fontN,8,170,posTableFooterY-35,"Serial").build(contentStream,writer);
        new SimpleTextBox(fontN,8,250,posTableFooterY-35,"ECO-PARTICIPATION TTC").build(contentStream,writer);
        new SimpleTextBox(fontN,8,380,posTableFooterY-35,"Private Copy TTC").build(contentStream,writer);

        new BorderBox(lgray,lgray,1,240,posTableFooterY-60-14*(sizeTab-1)+sizeTab,1,13*(sizeTab+1)).build(contentStream,writer);
        new BorderBox(lgray,lgray,1,360,posTableFooterY-60-14*(sizeTab-1)+sizeTab,1,13*(sizeTab+1)).build(contentStream,writer);

        int sizeTab2;
        for(sizeTab2=0; sizeTab2< pc.getProducts().size(); sizeTab2++) {
            Product randomProduct = pc.getProducts().get(sizeTab2);
            new SimpleTextBox(fontN, 8, 155, posTableFooterY-47-13*sizeTab2, randomProduct.getEan(), "SNO").build(contentStream,writer);
            new SimpleTextBox(fontN, 8, 283, posTableFooterY-47-13*sizeTab2, "N/A").build(contentStream,writer);
        }

        // no stamp or signature req info
        if (!genProb.get("signature_bottom") && !genProb.get("stamp_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignBox = new SimpleTextBox(fontN,8,0,0,noStampSignMsg,"Footnote");
            noStampSignBox.translate(pageMiddleX-noStampSignBox.getBoundingBox().getWidth()/2, 60);
            noStampSignBox.build(contentStream, writer);
        }
        // footer website & addr info
        SimpleTextBox footerEmail = new SimpleTextBox(fontB,8,0,0,company.getWebsite());
        footerEmail.translate(pageMiddleX-footerEmail.getBoundingBox().getWidth()/2, bottomPageMargin+10);
        footerEmail.build(contentStream,writer);
        HorizontalContainer footercontainer = new HorizontalContainer(0,0);
        if (model.getLang().matches("fr")) {
            footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,"N°RCS : "));
            footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,company.getIdNumbers().getCidValue(),"SCID"));
        }
        footercontainer.addElement(new SimpleTextBox(fontN,8,0,0," "+company.getAddress().getCity()));
        footercontainer.translate(pageMiddleX - footercontainer.getBoundingBox().getWidth()/2, bottomPageMargin);
        footercontainer.build(contentStream,writer);

        contentStream.close();
    }
}
