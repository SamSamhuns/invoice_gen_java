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

import com.fairandsmart.generator.documents.data.model.*;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.net.URI;
import java.awt.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;


@ApplicationScoped
public class BDmobilierLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "BDMobilier";
    }

    @Override
    public void builtInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer) throws Exception {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508");

        Random rnd = Helper.getRandom();

        // Set probability map, int value out of 100, 60 -> 60% proba
        Map<String, Integer> genProb = new HashMap<>();
        genProb.put("switch_bill_ship_addresses", 10);
        genProb.put("stamp_bottom", 45);
        genProb.put("logo_watermark", 15);
        genProb.put("confidential_watermark", 6);

        IDNumbers idNumbers = model.getCompany().getIdNumbers();
        Address address = model.getCompany().getAddress();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set fontFaces
        InvoiceLayout.pdType1Fonts fontPair = InvoiceLayout.getRandomPDType1Fonts();
        PDFont fontNormal1 = fontPair.getFontNormal();
        PDFont fontBold1 = fontPair.getFontBold();
        PDFont fontItalic1 = fontPair.getFontItalic();

        float leftMarginX = 10;
        PDFont companyNameFontFace = (rnd.nextInt(2) == 1) ? fontNormal1 : fontBold1;
        Color grayishFontColor = InvoiceLayout.getRandomColor(3);

        /* Build Page components now */

        // Top left logo
        URI logoUri = new URI(this.getClass().getClassLoader().getResource("common/logo/" + model.getCompany().getLogo().getFullPath()).getFile());
        String logoPath = logoUri.getPath();
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);

        int sizeLogo = 100;
        float ratioLogo = (float)logoImg.getWidth() / (float)logoImg.getHeight();
        float posLogoY = page.getMediaBox().getHeight()-sizeLogo/ratioLogo-20;
        contentStream.drawImage(logoImg, leftMarginX, posLogoY, sizeLogo, sizeLogo/ratioLogo);

        // check if billing and shipping addresses should be switched
        float leftAddrX = 120 + rnd.nextInt(15);
        float rightAddrX = 335 + rnd.nextInt(15);
        if (rnd.nextInt(100) < genProb.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX;
        float billY = page.getMediaBox().getHeight()-121;
        float shipX = rightAddrX;
        float shipY = page.getMediaBox().getHeight()-121;

        // Billing Address
        VerticalContainer billingContainer = new VerticalContainer(billX,billY,250);
        billingContainer.addElement(new SimpleTextBox(fontBold1,9, 0,0,model.getClient().getBillingHead(),grayishFontColor,Color.WHITE));
        billingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getBillingName(),"BN"));
        billingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getBillingAddress().getLine1(),"BA"));
        billingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getBillingAddress().getZip()+" "+model.getClient().getBillingAddress().getCity(),"BA"));
        billingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getBillingAddress().getCountry(),"BA"));
        billingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getBillingContactNumber().getPhoneValue()));

        billingContainer.build(contentStream,writer);

        // Shipping Address
        VerticalContainer shippingContainer = new VerticalContainer(shipX,shipY,250);
        shippingContainer.addElement(new SimpleTextBox(fontBold1,9,0,0,model.getClient().getShippingHead(),grayishFontColor,Color.WHITE));
        shippingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getShippingName(),"SHN"));
        shippingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getShippingAddress().getLine1(),"SHA"));
        shippingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getShippingAddress().getZip()+" "+model.getClient().getShippingAddress().getCity(),"SHA"));
        shippingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getShippingAddress().getCountry(),"SHA"));
        shippingContainer.addElement(new SimpleTextBox(fontNormal1,9,0,0,model.getClient().getShippingContactNumber().getPhoneValue()));

        shippingContainer.build(contentStream,writer);

        // Top right company info
        VerticalContainer headerContainer = new VerticalContainer(420, page.getMediaBox().getHeight()-9,250);
        headerContainer.addElement(new SimpleTextBox(companyNameFontFace,10,0,0,model.getCompany().getName(),"SN"));
        headerContainer.addElement(new SimpleTextBox(fontNormal1,10,0,0,model.getDate().getValue(),grayishFontColor,Color.WHITE,"IDATE"));

        HorizontalContainer numFact = new HorizontalContainer(0,0);
        numFact.addElement(new SimpleTextBox(fontNormal1,10,0,0,model.getReference().getLabel()+" ",grayishFontColor,Color.WHITE));
        numFact.addElement(new SimpleTextBox(fontNormal1,10,0,0,model.getReference().getValue(),grayishFontColor,Color.WHITE,"IN"));

        headerContainer.addElement(numFact);

        headerContainer.build(contentStream,writer);

        // Left top info
        VerticalContainer infoCommande = new VerticalContainer(leftMarginX,page.getMediaBox().getHeight()-211,76);
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8, 0,0,model.getReference().getLabelCommand()));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getReference().getValueCommand(),"ONUM"));
        infoCommande.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getDate().getLabelCommand()));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getDate().getValueCommand(),"IDATE"));
        infoCommande.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getPaymentInfo().getLabelType()));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getPaymentInfo().getValueType(),"PMODE"));
        infoCommande.addElement(new SimpleTextBox(fontNormal1,8,0,0,model.getProductContainer().getFormatedTotalWithTax(),"TTX"));
        infoCommande.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));

        infoCommande.build(contentStream,writer);

        // item list head
        float[] configRow = {209,56,45,90,51};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        firstLine.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "Product / Reference",Color.WHITE,Color.BLACK), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "Unit Price",Color.WHITE,Color.BLACK), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "Discount",Color.WHITE,Color.BLACK), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "Quantity",Color.WHITE,Color.BLACK), false);
        firstLine.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "Total",Color.WHITE,Color.BLACK), false);

        TableRowBox line2 = new TableRowBox(configRow, 0, 0);
        line2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "",Color.WHITE,Color.BLACK), false);
        line2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "",Color.WHITE,Color.BLACK), false);
        line2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "",Color.WHITE,Color.BLACK), false);
        line2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "",Color.WHITE,Color.BLACK), false);
        line2.addElement(new SimpleTextBox(fontBold1, 9, 0, 0, "",Color.WHITE,Color.BLACK), false);


        VerticalContainer verticalInvoiceItems = new VerticalContainer(110, page.getMediaBox().getHeight()-209, 500);
        verticalInvoiceItems.addElement(firstLine);
        verticalInvoiceItems.addElement(line2);

        String discount = "";
        // item list body
        for(int w=0; w< model.getProductContainer().getProducts().size(); w++) {

            Product randomProduct = model.getProductContainer().getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, randomProduct.getName(), "PD"), false);
            productLine.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, randomProduct.getFormatedPriceWithoutTax(), "UP"), false);
            discount = (randomProduct.getDiscount() == 0.0) ? "--": randomProduct.getFormatedDiscount();
            productLine.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, discount, "undefined"), false);
            productLine.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, (int)(randomProduct.getQuantity())+"", "QTY"), false);
            productLine.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, randomProduct.getFormatedTotalPriceWithoutTax(), "PTWTX"), false);

            verticalInvoiceItems.addElement(productLine);
        }
        // Shipping / Delivery info
        TableRowBox lineDelivery = new TableRowBox(configRow, 0, 0);
        String currency = model.getProductContainer().getCurrency();
        lineDelivery.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, "Shipping Cost",Color.BLACK,Color.LIGHT_GRAY), false);
        lineDelivery.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, "0,00 "+currency,Color.BLACK,Color.LIGHT_GRAY), false);
        lineDelivery.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, "--",Color.BLACK,Color.LIGHT_GRAY), false);
        lineDelivery.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, "1",Color.BLACK,Color.LIGHT_GRAY), false);
        lineDelivery.addElement(new SimpleTextBox(fontNormal1, 9, 2, 0, "0,00",Color.BLACK,Color.LIGHT_GRAY), false);

        verticalInvoiceItems.addElement(lineDelivery);

        verticalInvoiceItems.build(contentStream,writer);

        ProductContainer pc= model.getProductContainer();

        float posYTotal = verticalInvoiceItems.getBoundingBox().getPosY()-verticalInvoiceItems.getBoundingBox().getHeight()-13;
        new BorderBox(Color.BLACK,Color.BLACK,1,405,posYTotal,158,13).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK,1,405,posYTotal-13,158,13).build(contentStream,writer);

        // Totals and Taxes calculations
        new SimpleTextBox(fontNormal1, 9, 435, posYTotal+11, pc.getTotalWithoutTaxHead(),Color.WHITE,Color.BLACK).build(contentStream,writer);
        new SimpleTextBox(fontNormal1, 9, 508, posYTotal+11, pc.getFormatedTotalWithoutTax(),Color.WHITE,Color.BLACK,"TWTX").build(contentStream,writer);
        new SimpleTextBox(fontNormal1, 9, 435, posYTotal-2, pc.getTotalTaxHead(),Color.WHITE,Color.BLACK).build(contentStream,writer);
        new SimpleTextBox(fontNormal1, 9, 508, posYTotal-2, pc.getFormatedTotalTax(),Color.WHITE,Color.BLACK,"TTX").build(contentStream,writer);
        new SimpleTextBox(fontNormal1, 9, 435, posYTotal-16, pc.getTotalAmountHead(),Color.WHITE,Color.BLACK).build(contentStream,writer);
        new SimpleTextBox(fontNormal1, 9, 508, posYTotal-16, pc.getFormatedTotalWithTax(),Color.WHITE,Color.BLACK,"TA").build(contentStream,writer);

        // Footer company info
        int footerFontSize = 7 + rnd.nextInt(3);
        HorizontalContainer infoEntreprise = new HorizontalContainer(0,0);
        infoEntreprise.addElement(new SimpleTextBox(companyNameFontFace,footerFontSize,0,0, model.getCompany().getName(),"SN"));
        infoEntreprise.addElement(new SimpleTextBox(companyNameFontFace,footerFontSize,0,0, " - "));
        infoEntreprise.addElement(new SimpleTextBox(companyNameFontFace,footerFontSize,0,0, address.getCountry(),"SA"));

        HorizontalContainer infoEntreprise2 = new HorizontalContainer(0,0);
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, address.getLine1()+" ","SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, " - "));
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, address.getZip() + " " +address.getCity(),"SA"));
        if (model.getLang() == "fr") {
            infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, " "+idNumbers.getSiretLabel()+" "));
            infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, idNumbers.getSiretValue(),"SSIRET"));
        }
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, " - "+ idNumbers.getVatLabel() +" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, idNumbers.getVatValue(),"SVAT"));
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, " - "+model.getCompany().getContact().getFaxLabel()+" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, model.getCompany().getContact().getFaxValue(),"SFAX"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, model.getCompany().getContact().getPhoneLabel()+" : "));
        infoEntreprise3.addElement(new SimpleTextBox(fontNormal1,footerFontSize,0,0, model.getCompany().getContact().getPhoneValue(),"SCN"));

        float middlePageX = page.getMediaBox().getWidth()/2;
        infoEntreprise.translate(middlePageX-infoEntreprise.getBoundingBox().getWidth()/2,61);
        infoEntreprise2.translate(middlePageX-infoEntreprise2.getBoundingBox().getWidth()/2,53);
        infoEntreprise3.translate(middlePageX-infoEntreprise3.getBoundingBox().getWidth()/2,45);

        infoEntreprise.build(contentStream,writer);
        infoEntreprise2.build(contentStream,writer);
        infoEntreprise3.build(contentStream,writer);

        // Add Signature if it is not null
        if (model.getCompany().getSignature().getName() != null) {
              String compSignatureName = model.getCompany().getName();
              compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
              SimpleTextBox singatureText = new SimpleTextBox(
                      fontNormal1, 8, 0, 130,
                      model.getCompany().getSignature().getLabel()+" "+compSignatureName, "Signature");
              float singatureTextxPos = page.getMediaBox().getWidth() - singatureText.getBoundingBox().getWidth() - 50;
              singatureText.getBoundingBox().setPosX(singatureTextxPos);
              singatureText.build(contentStream, writer);
              new HorizontalLineBox(
                      singatureTextxPos - 10, 135,
                      singatureTextxPos + singatureText.getBoundingBox().getWidth() + 10, 135
                      ).build(contentStream, writer);

              // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
              URI signatureUri = new URI(this.getClass().getClassLoader().getResource("common/signature/" + model.getCompany().getSignature().getFullPath()).getFile());
              String signaturePath = signatureUri.getPath();
              PDImageXObject signatureImg = PDImageXObject.createFromFile(signaturePath, document);
              int signatureWidth = 120;
              int signatureHeight = 60;
              // align signature to center of singatureText bbox
              float signatureXPos = singatureText.getBoundingBox().getPosX() + singatureText.getBoundingBox().getWidth()/2 - signatureWidth/2;
              float signatureYPos = 140;
              contentStream.drawImage(signatureImg, signatureXPos, signatureYPos, signatureWidth, signatureHeight);
        }

        // Add company stamp watermark, 40% prob
        if (rnd.nextInt(100) < genProb.get("stamp_bottom")) {
            // note getResource returns URL with %20 for spaces etc, so it must be converted to URI that gives a working path with %20 convereted to ' '
            URI stampUri = new URI(this.getClass().getClassLoader().getResource("common/stamp/" + model.getCompany().getStamp().getFullPath()).getFile());
            String stampPath = stampUri.getPath();
            PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

            float minAStamp = 0.6f; float maxAStamp = 0.8f;
            float resDim = 100 + rnd.nextInt(20);
            float xPosStamp; float yPosStamp;
            // draw to lower right if signature if present
            if (rnd.nextInt(2) == 1 && model.getCompany().getSignature().getName() != null) {
                xPosStamp = 390 + rnd.nextInt(10);
                yPosStamp = 125 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = page.getMediaBox().getWidth()/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = 125 + rnd.nextInt(5);
            }
            InvoiceLayout.addWatermarkImagePDF(document, page, stampImg, xPosStamp, yPosStamp, resDim, resDim, minAStamp, maxAStamp);
        }

        // Add bg logo watermark or confidential stamp, but not both at once
        if (rnd.nextInt(100) < genProb.get("logo_watermark")) {
            // Add confidential watermark, 9% prob
            InvoiceLayout.addWatermarkTextPDF(document, page, PDType1Font.HELVETICA, "Confidential");
        }
        else if (rnd.nextInt(100) < genProb.get("confidential_watermark")) {
            // Add watermarked background logo
            InvoiceLayout.addWatermarkImagePDF(document, page, logoImg);
        }

        contentStream.close();
    }
}
