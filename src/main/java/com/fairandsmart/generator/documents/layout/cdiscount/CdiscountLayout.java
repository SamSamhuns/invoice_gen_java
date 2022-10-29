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
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.element.product.ProductTable;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.HAlign;

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
    public void buildInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer, InvoiceAnnotModel modelAnnot) throws Exception {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        // init invoice annotation objects
        modelAnnot.setVendor(new InvoiceAnnotModel.Vendor());
        modelAnnot.setInvoice(new InvoiceAnnotModel.Invoice());
        modelAnnot.setBillto(new InvoiceAnnotModel.Billto());
        modelAnnot.setTotal(new InvoiceAnnotModel.Total());
        modelAnnot.setItems(new ArrayList<InvoiceAnnotModel.Item>());

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
        Color lineStrokeColor = genProb.get("line_stroke_black") ? black: themeColor;

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
        VerticalContainer vendorAddrCont = new VerticalContainer(leftPageMargin, posLogoY-4, 250);
        vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, company.getName()+"","SN"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, cAddr.getLine1(),"SA"));
        vendorAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, cAddr.getZip() +"  "+ cAddr.getCity(),"SA"));
        vendorAddrCont.addElement(new BorderBox(white,white,0,0,0,0,3));
        if (genProb.get("vendor_address_phone_fax")) {
            vendorAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, company.getContact().getPhoneLabel()+": "+company.getContact().getPhoneValue(), "SC"));
            vendorAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, company.getContact().getFaxLabel()+": "+company.getContact().getFaxValue(), "SF"));
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
        if (genProb.get("addresses_bordered")) {
            vendorAddrCont.setBorderColor(themeColor);
            vendorAddrCont.setBorderThickness(0.5f);
        }
        modelAnnot.getVendor().setVendorName(company.getName());
        modelAnnot.getVendor().setVendorAddr(cAddr.getLine1()+" "+cAddr.getZip()+" "+cAddr.getCity());
        modelAnnot.getVendor().setVendorPOBox(cAddr.getZip());
        vendorAddrCont.build(contentStream,writer);

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

        modelAnnot.setTitle(docTitle);
        modelAnnot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        containerInvNum.build(contentStream,writer);
        containerDate.build(contentStream,writer);

        // border box around invoice body
        new BorderBox(black,white,1,leftPageMargin,bottomPageMargin,pageWidth-rightPageMargin-leftPageMargin,655).build(contentStream,writer);

        // Payment Address top right or Bottom left or Bottom right
        if (genProb.get("payment_address_top") || (genProb.get("payment_address_bottom") && pc.getProducts().size() < 6)) {
            modelAnnot.setPaymentto(new InvoiceAnnotModel.Paymentto());
            float fSize = 8;
            float paymentAddrXPos = 0, paymentAddrYPos = 0;
            if (genProb.get("payment_address_top")) {
                paymentAddrXPos = posHdrX;
                paymentAddrYPos = containerDate.getBBox().getPosY() - containerDate.getBBox().getHeight() - 5;
            }
            else if (genProb.get("payment_address_bottom")) {
                paymentAddrXPos = genProb.get("signature_bottom_left") ? posHdrX: 90;
                paymentAddrYPos = bottomPageMargin + 120 + rnd.nextInt(5);
            }

            VerticalContainer paymentAddrCont = new VerticalContainer(paymentAddrXPos, paymentAddrYPos, 400);

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
            if (genProb.get("payment_vendor_tax_number") && !genProb.get("vendor_address_tax_number")) {
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

        // Billing address
        Address bAddr = client.getBillingAddress();
        float bAddrPosX = 90, bAddrPosY, bAddrHeight;
        VerticalContainer billAddrCont = new VerticalContainer(bAddrPosX, pageHeight-158, 250);

        billAddrCont.addElement(new SimpleTextBox(fontB,9,0,0, client.getBillingHead(),themeColor,null));
        billAddrCont.addElement(new SimpleTextBox(fontNB,9,0,0, client.getBillingName(),"BN"));
        billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, bAddr.getLine1(),"BA"));
        billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, bAddr.getZip()+" "+bAddr.getCity().toUpperCase() ,"BA"));
        if (genProb.get("bill_address_tax_number")) {
          billAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, client.getIdNumbers().getVatLabel()+": "+client.getIdNumbers().getVatValue(),"BT"));
          modelAnnot.getBillto().setCustomerTrn(client.getIdNumbers().getVatValue());
        }
        else if (genProb.get("bill_address_phone_fax")) {
            billAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, client.getBillingContactNumber().getPhoneLabel()+": "+client.getBillingContactNumber().getPhoneValue(), "BC"));
            billAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, client.getBillingContactNumber().getFaxLabel()+": "+client.getBillingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered")) {
            bAddrPosY = billAddrCont.getBBox().getPosY();
            bAddrHeight = billAddrCont.getBBox().getHeight();
            new BorderBox(themeColor,white,1,87,bAddrPosY-bAddrHeight,229,bAddrHeight).build(contentStream,writer);
        }
        modelAnnot.getBillto().setCustomerName(client.getBillingName());
        modelAnnot.getBillto().setCustomerAddr(bAddr.getLine1()+" "+bAddr.getZip()+" "+bAddr.getCity());
        modelAnnot.getBillto().setCustomerPOBox(bAddr.getZip());
        billAddrCont.build(contentStream,writer);

        // Shipping Address
        Address sAddr = client.getShippingAddress();
        VerticalContainer shipAddrCont = new VerticalContainer(320, pageHeight-158, 250);

        shipAddrCont.addElement(new SimpleTextBox(fontB,9,0,0, client.getShippingHead(),themeColor,null));
        shipAddrCont.addElement(new SimpleTextBox(fontNB,9,0,0, client.getShippingName(),"SHN"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, sAddr.getLine1(),"SHA"));
        shipAddrCont.addElement(new SimpleTextBox(fontN,9,0,0, sAddr.getZip()+" "+sAddr.getCity().toUpperCase() ,"SHA"));
        if (genProb.get("bill_address_phone_fax") && genProb.get("ship_address_phone_fax")) {
            String connec = (client.getShippingContactNumber().getPhoneLabel().length() > 0) ? ": ": "";
            shipAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, client.getShippingContactNumber().getPhoneLabel()+connec+client.getShippingContactNumber().getPhoneValue(), "BC"));
            shipAddrCont.addElement(new SimpleTextBox(fontN,8,0,0, client.getShippingContactNumber().getFaxLabel()+connec+client.getShippingContactNumber().getFaxValue(), "BF"));
        }
        if (genProb.get("addresses_bordered") && client.getShippingHead().length() > 0) {
            // match address box of billing address
            bAddrPosY = billAddrCont.getBBox().getPosY();
            bAddrHeight = billAddrCont.getBBox().getHeight();
            new BorderBox(themeColor,white,1,87+230,bAddrPosY-bAddrHeight,192,bAddrHeight).build(contentStream,writer);
        }
        // add annotations for shipping address if these fields are not empty
        if (client.getShippingName().length() > 0) {
            modelAnnot.setShipto(new InvoiceAnnotModel.Shipto());
            modelAnnot.getShipto().setShiptoName(client.getShippingName());
            if (client.getShippingContactNumber().getPhoneLabel().length() > 0) {
                modelAnnot.getShipto().setShiptoPOBox(sAddr.getZip());
                modelAnnot.getShipto().setShiptoAddr(sAddr.getLine1()+" "+sAddr.getZip()+" "+sAddr.getCity());
            }
        }
        shipAddrCont.build(contentStream,writer);

        // client mail & payment info under billing addr
        float box1PosX = 87;
        float box1PosY = billAddrCont.getBBox().getPosY() - billAddrCont.getBBox().getHeight() - 4;
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

        modelAnnot.getTotal().setCurrency(cur);
        valueInfo1.build(contentStream,writer);

        // client ID & shipping info under shipping addr
        float box2PosX = 317;
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

        modelAnnot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
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

        modelAnnot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        valueInfo2.build(contentStream,writer);

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
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin - 5;
        int maxHdrNum = 14;
        ProductTable pt = new ProductTable(pc, amtSuffix, model.getLang(), tableWidth, maxHdrNum);
        List<String> tableHeaders = pt.getTableHeaders();
        float[] configRow = pt.getConfigRow();
        Map<String, ProductTable.ColItem> itemMap = pt.getItemMap();

        // table text colors
        Color hdrTextColor = genProb.get("table_hdr_black_text") ? Color.BLACK: Color.WHITE; // hdrTextColor black (predominantly) or white
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
            if (genProb.get("currency_in_table_headers") && !genProb.get("currency_in_table_items") && pt.getNumericalHdrs().contains(tableHeader)) {
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
                cellBgColor = genProb.get("alternate_table_items_bg_color") && w % 2 == 0 ? gray: cellBgColor;
                SimpleTextBox rowBox = new SimpleTextBox(cellFont, 8, 0, 0, cellText, cellTextColor, cellBgColor, cellAlign, tableHeader+"Item");
                productLine.addElement(rowBox, false);
            }
            modelAnnot.getItems().add(randomItem);

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

        // Add horizontal borders to table cell items if table cell is CENTER aligned horizontally
        if ( tableHdrAlign == HAlign.CENTER ) {
            float xPos = leftPageMargin;
            float yPos = tableTopPosY - tableTopInfo.getBBox().getHeight() - 4;
            // HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
            xPos += configRow[0];
            for (int i=1; i < configRow.length; i++) {
                HelperImage.drawLine(contentStream, xPos, yPos, xPos, yPos - tableItemsHeight, lineStrokeColor);
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

        modelAnnot.getTotal().setSubtotalPrice(pc.getFmtTotal()+amtSuffix);
        modelAnnot.getTotal().setTaxPrice(pc.getFmtTotalTax()+amtSuffix);
        modelAnnot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount()+amtSuffix);
        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        float posTableFooterY = valueTC.getBBox().getPosY() - valueTC.getBBox().getHeight() - 3;
        SimpleTextBox discInfoBox = new SimpleTextBox(fontI,9,0,0,"No discount will be applied in case of early payment");
        discInfoBox.translate(pageMiddleX - discInfoBox.getBBox().getWidth()/2, posTableFooterY);
        discInfoBox.build(contentStream,writer);

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

        // Add Signature at bottom
        if (genProb.get("signature_bottom")) {
            String compSignatureName = company.getName();
            compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0, company.getSignature().getLabel()+" "+compSignatureName, "Signature");

            float sigTX;
            if (genProb.get("signature_bottom_left")) {  // bottom left
                sigTX = leftPageMargin + 55;
            } else {                                     // bottom right
                sigTX = pageWidth - sigTextBox.getBBox().getWidth() - 75;
            }
            sigTextBox.translate(sigTX,bottomPageMargin + 43);
            sigTextBox.build(contentStream, writer);

            new HorizontalLineBox(
                    sigTX - 10, bottomPageMargin + 44,
                    sigTX + sigTextBox.getBBox().getWidth() + 5, bottomPageMargin + 44,
                    lineStrokeColor).build(contentStream, writer);

            String signaturePath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
            int signatureWidth = 120;
            int signatureHeight = (signatureWidth * signatureImg.getHeight()) / signatureImg.getWidth();
            // align signature to center of sigTextBox bbox
            float signatureXPos = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - signatureWidth/2;
            float signatureYPos = bottomPageMargin + 45;
            contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }
        // no stamp or signature req info
        if (!genProb.get("signature_bottom") && !genProb.get("stamp_bottom")) {
            String noStampSignMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            SimpleTextBox noStampSignBox = new SimpleTextBox(fontN,7,0,0,noStampSignMsg,"Footnote");
            noStampSignBox.translate(pageMiddleX-noStampSignBox.getBBox().getWidth()/2, 60);
            noStampSignBox.build(contentStream, writer);
        }
        // Add company stamp watermark, 40% prob
        if (genProb.get("stamp_bottom")) {
            String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + company.getStamp().getFullPath());
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f, maxAStamp = 0.8f;
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp, yPosStamp;
            // draw to lower right if signature in bottom or lower left if signature in bottom left
            if (genProb.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = ((genProb.get("signature_bottom_left")) ? leftPageMargin+6 : pageMiddleX - 80) + rnd.nextInt(10);
                yPosStamp = bottomPageMargin + 50 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = bottomPageMargin + 50 + rnd.nextInt(5);
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
        // Add bg logo watermark or confidential stamp, but not both at once
        if (genProb.get("confidential_watermark")) {
            // Add confidential watermark
            HelperImage.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "Confidential");
        }
        else if (genProb.get("logo_watermark")) {
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
