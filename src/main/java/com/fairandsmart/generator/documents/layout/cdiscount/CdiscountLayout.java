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
import java.util.Map;


@ApplicationScoped
public class CdiscountLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "CDiscount";
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

        Color lineStrokeColor = genProb.get("line_stroke_black") ? Color.BLACK: Color.BLUE;
        Color grayish = HelperCommon.getRandomColor(3);
        Color gray = new Color(239,239,239);

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        ///////////////////////////////////      Build Page components now      ////////////////////////////////////

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // draw top logo
        logoHeight = 60;
        logoWidth = (logoHeight * logoImg.getWidth()) / logoImg.getHeight();
        float posLogoX = leftPageMargin;
        float posLogoY = pageHeight-logoHeight-3;
        contentStream.drawImage(logoImg, posLogoX, posLogoY, logoWidth, logoHeight);

        // top left vendor address
        VerticalContainer verticalHeaderContainer = new VerticalContainer(leftPageMargin, posLogoY-2, 250);
        verticalHeaderContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getName()+"","SN"));
        verticalHeaderContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,3));
        verticalHeaderContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getLine1(),"SA"));
        verticalHeaderContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,3));
        verticalHeaderContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, company.getAddress().getZip() +"  "+ company.getAddress().getCity(),"SA"));
        verticalHeaderContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,3));

        // top left info
        // specific to french invoices
        if (model.getLang().matches("fr")) {
            HorizontalContainer rcs = new HorizontalContainer(0,0);
            rcs.addElement(new SimpleTextBox(fontN,9,0,0,"N°RCS "));
            rcs.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getCidValue(), "SCID"));
            rcs.addElement(new SimpleTextBox(fontN,9,0,0, " " +company.getAddress().getCity(), "SA"));

            HorizontalContainer siret = new HorizontalContainer(0,0);
            siret.addElement(new SimpleTextBox(fontN,9,0,0,"Siret "));
            siret.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getSiretValue(), "SSIRET"));

            HorizontalContainer naf = new HorizontalContainer(0,0);
            naf.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getToaLabel()+" : "));
            naf.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getToaValue(), "STOA"));

            verticalHeaderContainer.addElement(rcs);
            verticalHeaderContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,3));
            verticalHeaderContainer.addElement(naf);
            verticalHeaderContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,3));
        }
        // vendor tax id number
        HorizontalContainer vatText = new HorizontalContainer(0,0);
        vatText.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getVatLabel()+" : "));
        vatText.addElement(new SimpleTextBox(fontN,9,0,0, company.getIdNumbers().getVatValue(), "SVAT"));

        verticalHeaderContainer.addElement(vatText);
        verticalHeaderContainer.build(contentStream,writer);

        // top right info
        float hdrWidth = 252;
        float hdrHeight = 14;
        float posHdrX = pageWidth-rightPageMargin-hdrWidth;
        float posHdrY = pageHeight-topPageMargin-hdrHeight;
        // tax invoice number
        HorizontalContainer containerInvNum = new HorizontalContainer(0,0);
        containerInvNum.addElement(new SimpleTextBox(fontB,9,0,0, model.getReference().getLabelInvoice()+" "));
        containerInvNum.addElement(new SimpleTextBox(fontB,9,0,0, model.getReference().getValueInvoice(), "IN"));
        containerInvNum.translate(posHdrX + hdrWidth/2 - containerInvNum.getBoundingBox().getWidth()/2, posHdrY+10);
        // tax invoice date
        HorizontalContainer containerDate = new HorizontalContainer(0,0);
        containerDate.addElement(new SimpleTextBox(fontB,9,0,0, "From "));
        containerDate.addElement(new SimpleTextBox(fontB,9,0,0, model.getDate().getValueInvoice(), "IDATE"));
        containerDate.translate(posHdrX + hdrWidth/2 - containerDate.getBoundingBox().getWidth()/2, posHdrY-4);

        new BorderBox(gray,gray, 1, posHdrX, posHdrY-4, hdrWidth, hdrHeight).build(contentStream,writer);
        new BorderBox(gray,gray, 1, posHdrX, posHdrY-hdrHeight-6, hdrWidth, hdrHeight).build(contentStream,writer);

        containerInvNum.build(contentStream,writer);
        containerDate.build(contentStream,writer);

        new BorderBox(Color.BLACK,Color.WHITE,1,34,42,530,652).build(contentStream,writer);

        new SimpleTextBox(fontB,9,87,page.getMediaBox().getHeight()-158,"Billing",Color.RED,Color.WHITE).build(contentStream,writer);
        new SimpleTextBox(fontB,9,87+229,page.getMediaBox().getHeight()-158,"Delivery",Color.RED,Color.WHITE).build(contentStream,writer);

        new BorderBox(Color.RED,Color.WHITE,1,87,page.getMediaBox().getHeight()-170-51,229,51).build(contentStream,writer);
        new BorderBox(Color.RED,Color.WHITE,1,87+230,page.getMediaBox().getHeight()-170-51,189,51).build(contentStream,writer);

        Address bA = client.getBillingAddress();
        VerticalContainer facturationContainer = new VerticalContainer(90, page.getMediaBox().getHeight()-172, 250 );
        facturationContainer.addElement(new SimpleTextBox(fontB, 9, 0, 0, client.getBillingName(),"BN"));
        facturationContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,5));
        facturationContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, bA.getLine1(),"BA"));
        facturationContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,10));
        facturationContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, bA.getZip()+" "+ bA.getCity().toUpperCase() ,"BA"));

        facturationContainer.build(contentStream,writer);

        Address sA = client.getShippingAddress();
        VerticalContainer livraisonContainer = new VerticalContainer(320, page.getMediaBox().getHeight()-172, 250 );
        livraisonContainer.addElement(new SimpleTextBox(fontB, 9, 0, 0, client.getBillingName(),"SHN"));
        livraisonContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,5));
        livraisonContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, sA.getLine1(),"SHA"));
        livraisonContainer.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,10));
        livraisonContainer.addElement(new SimpleTextBox(fontN, 9, 0, 0, sA.getZip()+" "+ bA.getCity().toUpperCase() ,"SHA"));

        livraisonContainer.build(contentStream,writer);

        VerticalContainer boxInfoClient = new VerticalContainer(87,page.getMediaBox().getHeight()-223-14,250);
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));
        boxInfoClient.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient.addElement(new BorderBox(gray,gray,1,0,0,85,11));

        boxInfoClient.build(contentStream,writer);

        VerticalContainer labelInfoClient = new VerticalContainer(88,page.getMediaBox().getHeight()-227,250);
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,"e-mail address"));
        labelInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getLabelOrder()));
        labelInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getLabelPaymentType()+""));
        labelInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelPayment()));

        labelInfoClient.build(contentStream,writer);

        VerticalContainer valueInfoClient = new VerticalContainer(175,page.getMediaBox().getHeight()-227,250);
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,"companie@gmail.com"));
        valueInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueOrder(),"ONUM"));
        valueInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getPaymentInfo().getValuePaymentType(),"PMODE"));
        valueInfoClient.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valueInfoClient.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValuePayment(),"IDATE"));

        valueInfoClient.build(contentStream,writer);

        VerticalContainer boxInfoClient2 = new VerticalContainer(320,page.getMediaBox().getHeight()-223-14,250);
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));
        boxInfoClient2.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,1));
        boxInfoClient2.addElement(new BorderBox(gray,gray,1,0,0,73,11));

        boxInfoClient2.build(contentStream,writer);

        VerticalContainer labelInfoClient2 = new VerticalContainer(321,page.getMediaBox().getHeight()-227,250);
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"ID Client"));
        labelInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getLabelOrder()));
        labelInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"Date of validation"));
        labelInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"Date of Sending"));
        labelInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        labelInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueInvoice()));

        labelInfoClient2.build(contentStream,writer);

        new BorderBox(gray,gray,1,396,page.getMediaBox().getHeight()-272,110,11).build(contentStream,writer);

        VerticalContainer valeurInfoClient2 = new VerticalContainer(397,page.getMediaBox().getHeight()-227,250);
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getReference().getValueClient(),"CNUM"));
        valeurInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueOrder(),"IDATE"));
        valeurInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,model.getDate().getValueOrder()));
        valeurInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0,"No of Parcels"));
        valeurInfoClient2.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,2.5f));
        int codeColis = 100000000 + new Random().nextInt(900000000);
        valeurInfoClient2.addElement(new SimpleTextBox(fontN,8,0,0, "DT"+codeColis+"FR"));

        valeurInfoClient2.build(contentStream,writer);

        new SimpleTextBox(fontB,9,36,521,"Your Order",Color.RED,Color.WHITE).build(contentStream,writer);

        float[] configRow = {82f, 189f, 28f, 62f, 62f, 62f,39f};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Ref.", Color.BLACK, gray), true);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Labels", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "Qty", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PUHT", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PUTTC", Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "PVTTC",Color.BLACK, gray), false);
        firstLine.addElement(new SimpleTextBox(fontB, 8, 2, 0, "VAT", Color.BLACK, gray), false);

        VerticalContainer verticalInvoiceItems = new VerticalContainer(36, 510, 600 );
        verticalInvoiceItems.addElement(new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,0,0,0,page.getMediaBox().getWidth()-(72),0.3f));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 1));
        verticalInvoiceItems.addElement(firstLine);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0,0, 0, 1));
        verticalInvoiceItems.addElement(new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,0,0,0,page.getMediaBox().getWidth()-(72),0.3f));


        for(int w=0; w< pc.getProducts().size(); w++) {

            Product randomProduct = pc.getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getEan(), "SNO"), true);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getName(), "PD"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtPrice(), "UP"), false);
            float puttcR = (float)(int)((randomProduct.getPrice() + randomProduct.getPrice() * randomProduct.getTaxRate())*100)/100;
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, puttcR + "", "undefined"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getFmtTotalPriceWithTax(), "undefined"), false);
            productLine.addElement(new SimpleTextBox(fontN, 8, 2, 0, randomProduct.getTaxRate() * 100 + "%", "TXR"), false);

            verticalInvoiceItems.addElement(productLine);
            verticalInvoiceItems.addElement(new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,0,0,0,page.getMediaBox().getWidth()-(72),0.3f));
        }

        String msg = this.getClass().getClassLoader().getResource("invoices/parts/cdiscount/msg.png").getFile();
        PDImageXObject logoMsg = PDImageXObject.createFromFile(msg, document);
        float ratioMsg= (float)logoMsg.getWidth() / (float)logoMsg.getHeight();
        float tailleMsg = 320;
        float posMsgX = 36;
        float posMsgY = verticalInvoiceItems.getBoundingBox().getPosY()-verticalInvoiceItems.getBoundingBox().getHeight()-tailleMsg/ratioMsg-10;
        contentStream.drawImage(logoMsg, posMsgX, posMsgY, tailleMsg, tailleMsg/ratioMsg);

        verticalInvoiceItems.build(contentStream,writer);

        float tailleCase = 13.28f;
        float posTab = posMsgY-tailleMsg/ratioMsg-15+93;
        new BorderBox(Color.LIGHT_GRAY,gray,1,379,posTab-93,100,93).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase,100,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase*2,100,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase*3,100,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase*4,100,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase*5,100,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,379,posTab-tailleCase*6,100,1).build(contentStream,writer);

        new BorderBox(Color.LIGHT_GRAY,Color.WHITE,1,478,posTab-93,82,93).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase,82,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase*2,82,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase*3,82,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase*4,82,1).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase*5,82,tailleCase).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,478,posTab-tailleCase*6,82,1).build(contentStream,writer);



        VerticalContainer labelTotal = new VerticalContainer(380,posTab-2,250);
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getWithTaxTotalHead().toUpperCase()));
        labelTotal.addElement(new BorderBox(gray,gray,0,5,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,"Credit/Gift Card"));
        labelTotal.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,"Delivery"));
        labelTotal.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,"Preparation Fees* "));
        labelTotal.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,"TOTAL NET TTC"));
        labelTotal.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTaxTotalHead().toUpperCase()));
        labelTotal.addElement(new BorderBox(gray,gray,0,0,0,0,4));
        labelTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getTotalHead().toUpperCase()));
        labelTotal.build(contentStream,writer);

        VerticalContainer valeurTotal = new VerticalContainer(480,posTab-2,250);
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTax(),"TA"));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,"0,00"));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,""));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0, 0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,"0,00"));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalWithTax(),"TA"));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotalTax(),"TTX"));
        valeurTotal.addElement(new BorderBox(Color.white,Color.WHITE,0,0,0,0,4));
        valeurTotal.addElement(new SimpleTextBox(fontN,8,0,0,pc.getFmtTotal(),"TWTX"));
        valeurTotal.build(contentStream,writer);

        new SimpleTextBox(fontI,9,170,posMsgY-76,"No discount will be applied in case of early payment").build(contentStream,writer);

        new BorderBox(Color.LIGHT_GRAY,Color.WHITE,1,124,posMsgY-110,351,14).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.WHITE,1,124,posMsgY-123,351,14).build(contentStream,writer);

        int tailleTab;
        for(tailleTab=0; tailleTab< pc.getProducts().size(); tailleTab++) {

            new BorderBox(Color.LIGHT_GRAY,Color.WHITE,1,124,posMsgY-123-13*tailleTab,351,14).build(contentStream,writer);
        }

        new BorderBox(Color.LIGHT_GRAY,Color.WHITE,1,124,posMsgY-123-13*(tailleTab),351,14).build(contentStream,writer);

        new SimpleTextBox(fontN,8,189,posMsgY-97,"Details of eco-participation and private copy remuneration").build(contentStream,writer);
        new SimpleTextBox(fontN,8,170,posMsgY-111,"Wording").build(contentStream,writer);
        new SimpleTextBox(fontN,8,250,posMsgY-111,"ECO-PARTICIPATION TTC").build(contentStream,writer);
        new SimpleTextBox(fontN,8,380,posMsgY-111,"Private Copy TTC").build(contentStream,writer);

        //new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,240,posMsgY-136,1,27).build(contentStream,writer);
        //new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,360,posMsgY-136,1,27).build(contentStream,writer);

        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,240,posMsgY-136-14*(tailleTab-1)+tailleTab,1,13*(tailleTab+1)).build(contentStream,writer);
        new BorderBox(Color.LIGHT_GRAY,Color.LIGHT_GRAY,1,360,posMsgY-136-14*(tailleTab-1)+tailleTab,1,13*(tailleTab+1)).build(contentStream,writer);

        String footer = this.getClass().getClassLoader().getResource("invoices/parts/cdiscount/footer.png").getFile();
        PDImageXObject logofooter = PDImageXObject.createFromFile(footer, document);
        float ratioFooter= (float)logofooter.getWidth() / (float)logofooter.getHeight();
        float tailleFooter = 520;
        float posFooterX = 36;
        float posFooterY = 48;
        contentStream.drawImage(logofooter, posFooterX, posFooterY, tailleFooter, tailleFooter/ratioFooter);

        new SimpleTextBox(fontB,8,263,36,company.getWebsite()).build(contentStream,writer);
        HorizontalContainer footercontainer = new HorizontalContainer(249,22);
        footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,"N°RCS : "));
        footercontainer.addElement(new SimpleTextBox(fontN,8,0,0,company.getIdNumbers().getCidValue(),"SCID"));
        footercontainer.addElement(new SimpleTextBox(fontN,8,0,0," "+company.getAddress().getCity()));
        footercontainer.build(contentStream,writer);

        int tailleTab2;
        for(tailleTab2=0; tailleTab2< pc.getProducts().size(); tailleTab2++) {

            Product randomProduct = pc.getProducts().get(tailleTab2);

            new SimpleTextBox(fontN, 8, 155, posMsgY-123-13*tailleTab2, randomProduct.getEan(), "SNO").build(contentStream,writer);
            new SimpleTextBox(fontN, 8, 283, posMsgY-123-13*tailleTab2, "EcoTac TODO remove later").build(contentStream,writer);
        }

        contentStream.close();
    }
}
