package com.fairandsmart.generator.documents.layout.naturedecouvertes;

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
 * Copyright (C) 2019 - 2020 Fair And Smart
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
import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.Color;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;


public class NatureDecouvertesLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Nature&Decouvertes";
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

        // get barcode number
        String barcodeNum = model.getReference().getValueBarcode();

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
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.min((int)(v*1.9f), 255)).collect(Collectors.toList()); // lighten colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = genProb.get("line_stroke_black") ? black: themeColor;

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
        BufferedImage barcodeBufImg = HelperImage.generateEAN13BarcodeImage(barcodeNum);
        PDImageXObject barcodeImg = LosslessFactory.createFromImage(document, barcodeBufImg);

        ///////////////////////////////////      Build Page components now      ////////////////////////////////////

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        float ratioPage = pageWidth/2480;
        float ratioLogo = (float)logoImg.getWidth() / (float)logoImg.getHeight();
        float widthLogo = 596*ratioPage;
        posLogoX = 147*ratioPage;
        float heightLogo = widthLogo/ratioLogo;
        // the origin is at the bottom left of the page
        posLogoY = pageHeight-(heightLogo+125*ratioPage);
        contentStream.drawImage(logoImg, posLogoX, posLogoY, widthLogo,heightLogo);

        VerticalContainer headerContainer = new VerticalContainer(1282*ratioPage,pageHeight-132*ratioPage,250);
        headerContainer.addElement(new SimpleTextBox(fontB,12,0,0,company.getName(),"SN"));
        headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getLine1(),"SA"));
        headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getZip()+" "+company.getAddress().getCity(),"SA"));
        headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,company.getAddress().getCountry(),"SA"));
        headerContainer.addElement(new SimpleTextBox(fontN,10,0,0,"Telephone : "+company.getContact().getPhoneValue(),"SA"));
        headerContainer.build(contentStream,writer);

        VerticalContainer shippingContainer = new VerticalContainer(147*ratioPage,pageHeight-499*ratioPage,250);
        shippingContainer.addElement(new SimpleTextBox(fontN,10,0,0,"Delivery address"));
        shippingContainer.addElement(new SimpleTextBox(fontB,11,0,0,client.getShippingName().toUpperCase(),"SHN"));
        shippingContainer.addElement(new SimpleTextBox(fontN,10,0,0,client.getShippingAddress().getLine1().toUpperCase(),"SHA"));
        shippingContainer.addElement(new SimpleTextBox(fontN,10,0,0,client.getShippingAddress().getZip()+" "+client.getShippingAddress().getCity().toUpperCase(),"SHA"));
        shippingContainer.addElement(new SimpleTextBox(fontN,10,0,0, "Tel : 06"+(int)(1000000+(Math.random()*(99999999 - 1000000)))));

        shippingContainer.build(contentStream,writer);

        VerticalContainer billingContainer = new VerticalContainer(1282*ratioPage,pageHeight-499*ratioPage,250);
        billingContainer.addElement(new SimpleTextBox(fontN,10, 0,0,"Billing address"));
        billingContainer.addElement(new SimpleTextBox(fontB,11,0,0,client.getBillingName().toUpperCase(),"BN"));
        billingContainer.addElement(new SimpleTextBox(fontN,10,0,0,client.getBillingAddress().getLine1().toUpperCase(),"BA"));
        billingContainer.addElement(new SimpleTextBox(fontN,10,0,0,client.getBillingAddress().getZip()+" "+client.getBillingAddress().getCity().toUpperCase(),"BA"));

        billingContainer.build(contentStream,writer);

        HorizontalContainer infoCommande = new HorizontalContainer(147*ratioPage, pageHeight-866*ratioPage);
        infoCommande.addElement(new SimpleTextBox(PDType1Font.TIMES_BOLD,15, 0,0,"Order number "+model.getReference().getValueOrder(),"ONUM"));
        infoCommande.build(contentStream,writer);

        HorizontalContainer infoInvoice = new HorizontalContainer(1357*ratioPage, pageHeight-875*ratioPage);
        infoInvoice.addElement(new SimpleTextBox(PDType1Font.TIMES_BOLD,12, 0,300,"Invoice No "+model.getReference().getValueInvoice(),"IN"));
        infoInvoice.build(contentStream,writer);

        HorizontalContainer dateInvoice = new HorizontalContainer(2000*ratioPage, pageHeight-875*ratioPage);
        dateInvoice.addElement(new SimpleTextBox(PDType1Font.TIMES_BOLD,12, 0,300,model.getDate().getValueOrder(),"IDATE"));
        dateInvoice.build(contentStream,writer);

        HorizontalContainer infoEntreprise = new HorizontalContainer(0,0);
        infoEntreprise.addElement(new SimpleTextBox(fontN,7,0,0, company.getName(),"SN"));
        infoEntreprise.addElement(new SimpleTextBox(fontN,7,0,0, " - "));
        infoEntreprise.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getCountry(),"SA"));

        HorizontalContainer infoEntreprise2 = new HorizontalContainer(0,0);
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getLine1()+" ","SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getAddress().getZip() + " " +company.getAddress().getCity(),"SA"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " "+company.getIdNumbers().getSiretLabel()+" "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getIdNumbers().getSiretValue(),"SSIRET"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "+ company.getIdNumbers().getVatLabel() +" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getIdNumbers().getVatValue(),"SVAT"));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, " - "+company.getContact().getFaxLabel()+" : "));
        infoEntreprise2.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getFaxValue(),"SFAX"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getPhoneLabel()+" : "));
        infoEntreprise3.addElement(new SimpleTextBox(fontN,7,0,0, company.getContact().getPhoneValue(),"SCN"));

        infoEntreprise.translate(pageMiddleX-infoEntreprise.getBBox().getWidth()/2,58);
        infoEntreprise2.translate(pageMiddleX-infoEntreprise2.getBBox().getWidth()/2,51);
        infoEntreprise3.translate(pageMiddleX-infoEntreprise3.getBBox().getWidth()/2,45);

        infoEntreprise.build(contentStream,writer);
        infoEntreprise2.build(contentStream,writer);
        infoEntreprise3.build(contentStream,writer);

        float[] configRow = {236*ratioPage,936*ratioPage,115*ratioPage,112*ratioPage,230*ratioPage,212*ratioPage,290*ratioPage};

        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Product reference", black,null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Designation", black, null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Qty Order", black, null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "VAT", black, null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Unit Price ", black, null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Delivered quantity", black, null), true);
        firstLine.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Total Price", black,null), true);


        VerticalContainer verticalInvoiceItems = new VerticalContainer(170*ratioPage, pageHeight-1046*ratioPage, 2136*ratioPage );
        new BorderBox(black,Color.WHITE, 1,165*ratioPage, pageHeight-2462*ratioPage, 2138*ratioPage,1418*ratioPage ).build(contentStream,writer);
        //new BorderBox(black,new Color (200,200,200, 1.0f), 1,165*ratioPage, pageHeight-2596*ratioPage, 1844*ratioPage,1552*ratioPage ).build(contentStream,writer);
        new BorderBox(black,Color.lightGray, 1,165*ratioPage, pageHeight-1158*ratioPage, 2138*ratioPage,118*ratioPage ).build(contentStream,writer);
        HelperImage.drawPolygon(
                contentStream,
                new float[] {165*ratioPage,2303*ratioPage,
                             2303*ratioPage, 165*ratioPage},
                new float[] {pageHeight-2596*ratioPage,pageHeight-2596*ratioPage,
                             pageHeight-1044*ratioPage,pageHeight-1044*ratioPage});
        HelperImage.drawPolygon(
                contentStream,
                new float[] {165*ratioPage,2009*ratioPage,
                             2009*ratioPage, 165*ratioPage},
                new float[] {pageHeight-2596*ratioPage,pageHeight-2596*ratioPage,
                             pageHeight-1044*ratioPage,pageHeight-1044*ratioPage});
        HelperImage.drawLine(contentStream, 406*ratioPage,pageHeight-1042*ratioPage, 406*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1342*ratioPage,pageHeight-1042*ratioPage, 1342*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1576*ratioPage,pageHeight-1042*ratioPage, 1576*ratioPage,pageHeight-2462*ratioPage);
        HelperImage.drawLine(contentStream, 1467*ratioPage,pageHeight-1042*ratioPage, 1467*ratioPage,pageHeight-2596*ratioPage);
        HelperImage.drawLine(contentStream, 1804*ratioPage,pageHeight-1042*ratioPage, 1804*ratioPage,pageHeight-2462*ratioPage);
        verticalInvoiceItems.addElement(firstLine);

        for(int w=0; w< pc.getProducts().size(); w++) {
              Product randomProduct = pc.getProducts().get(w);

              TableRowBox productLine = new TableRowBox(configRow, 0, -10);
              productLine.addElement(new SimpleTextBox(fontN, 7, 2, 0, randomProduct.getEan(), "SNO"), true);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getName(), "PD"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getTaxRate() * 100 + "%", "TXR"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtPrice(), "PTWTX"), false);
              float puttc = (float)(int)((randomProduct.getPrice() + randomProduct.getPrice() * randomProduct.getTaxRate())*100)/100;
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, puttc + "", "UP"), false);
              productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtTotalPriceWithTax(), "PTTX"), false);

              verticalInvoiceItems.addElement(productLine);
        }

        verticalInvoiceItems.build(contentStream, writer);

        VerticalContainer paymentTypeContainer = new VerticalContainer(1476*ratioPage,pageHeight-2479*ratioPage,250);
        paymentTypeContainer.addElement(new SimpleTextBox(fontB,11,0,0,"TOTAL T.T.C"));
        paymentTypeContainer.addElement(new SimpleTextBox(fontN,8,0,0,"Regulated by : "+payment.getLabelPaymentType(),"PT"));
        paymentTypeContainer.build(contentStream,writer);

        VerticalContainer totalContainer = new VerticalContainer(2019*ratioPage,pageHeight-2474*ratioPage,250);
        totalContainer.addElement(new SimpleTextBox(fontN,11,0,0,pc.getFmtTotalWithTax(),"TA"));
        totalContainer.build(contentStream,writer);

        VerticalContainer backContainer = new VerticalContainer(343*ratioPage, pageHeight-2840*ratioPage, 636*ratioPage);
        backContainer.addElement(new SimpleTextBox(fontN,10,0,0,"Label to stick on your package in case of return. "));
        backContainer.build(contentStream,writer);
        String barcode = this.getClass().getClassLoader().getResource("invoices/parts/ldlc/barcode.jpg").getFile();
        PDImageXObject pdBarcode = PDImageXObject.createFromFile(barcode, document);
        new ImageBox(pdBarcode, 343*ratioPage,pageHeight-2940*ratioPage, pdBarcode.getWidth()*ratioPage, pdBarcode.getHeight()*ratioPage, model.getReference().getValueOrder()).build(contentStream,writer);
        backContainer = new VerticalContainer(370*ratioPage, pageHeight-3050*ratioPage, 636*ratioPage);
        backContainer.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder()));
        backContainer.build(contentStream,writer);

        float[] row = {190*ratioPage,254*ratioPage,144*ratioPage,268*ratioPage};

        new BorderBox(black,Color.WHITE, 1,1440*ratioPage, pageHeight-3000*ratioPage, 900*ratioPage,228*ratioPage ).build(contentStream,writer);
        new BorderBox(black,Color.lightGray, 1,1440*ratioPage, pageHeight-2890*ratioPage, 900*ratioPage,118*ratioPage ).build(contentStream,writer);

        TableRowBox firstLineTax = new TableRowBox(row, 0, 0);
        firstLineTax.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Code VAT", black,null), true);
        firstLineTax.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Base H.T", black, null), true);
        firstLineTax.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Rate %", black, null), true);
        firstLineTax.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "Amount VAT", black, null), true);
        VerticalContainer tax = new VerticalContainer(1446*ratioPage, pageHeight-2798*ratioPage, 856*ratioPage );
        tax.addElement(firstLineTax);
        contentStream.drawLine(1648*ratioPage,pageHeight-2772*ratioPage, 1648*ratioPage,pageHeight-3000*ratioPage);
        contentStream.drawLine(1900*ratioPage,pageHeight-2772*ratioPage, 1900*ratioPage,pageHeight-3000*ratioPage);
        contentStream.drawLine(2064*ratioPage,pageHeight-2772*ratioPage, 2064*ratioPage,pageHeight-3000*ratioPage);

        TableRowBox taxContent = new TableRowBox(row, 0, -20);
        taxContent.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, "1", black,null), true);
        taxContent.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, pc.getFmtTotal(), black, null), true);
        taxContent.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, ""+pc.getProducts().get(0).getTaxRate(), black, null), true);
        taxContent.addElement(new SimpleTextBox(PDType1Font.HELVETICA_BOLD, 8, 0, 0, pc.getFmtTotalTax(), black, null), true);
        tax.addElement(taxContent);

        tax.build(contentStream, writer);

        contentStream.close();
      }
  }
