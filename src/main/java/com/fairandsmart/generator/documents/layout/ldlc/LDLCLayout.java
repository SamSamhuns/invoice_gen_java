package com.fairandsmart.generator.documents.layout.ldlc;

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

import com.fairandsmart.generator.documents.data.model.Product;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
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

@ApplicationScoped
public class LDLCLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "LDLC";
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

        Address address = model.getCompany().getAddress();
        IDNumbers idNumbers = model.getCompany().getIdNumbers();

        PDFont font = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontI = PDType1Font.HELVETICA_OBLIQUE;
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        String logo = this.getClass().getClassLoader().getResource("common/logo/fr/LDLC.png").getFile();
        PDImageXObject logoHeader = PDImageXObject.createFromFile(logo, document);
        float ratioLogo = (float)logoHeader.getWidth() / (float)logoHeader.getHeight();
        int tailleLogo = 140;
        float posLogoX = 20;
        float posLogoY = page.getMediaBox().getHeight()-tailleLogo/ratioLogo - 20;
        contentStream.drawImage(logoHeader, posLogoX, posLogoY, tailleLogo, tailleLogo/ratioLogo);


        float posHeaderX = 15;
        float posHeaderY = page.getMediaBox().getHeight()-138;

        VerticalContainer infoEntreprise = new VerticalContainer(20,page.getMediaBox().getHeight()-85,400);

        HorizontalContainer infoEntreprise1 = new HorizontalContainer(0,0);
        infoEntreprise1.addElement(new SimpleTextBox(font,6,0,0, address.getLine1()+" ","SA"));
        infoEntreprise1.addElement(new SimpleTextBox(font,6,0,0, ", "));
        infoEntreprise1.addElement(new SimpleTextBox(font,6,0,0, address.getZip() + " - " +address.getCity(),"SA"));

        HorizontalContainer infoEntreprise2 = new HorizontalContainer(0,0);
        infoEntreprise2.addElement(new SimpleTextBox(font,6,0,0, "Non-surcharged Customer Service number : "));
        infoEntreprise2.addElement(new SimpleTextBox(font,6,0,0, model.getCompany().getContact().getPhoneValue(),"SCN"));

        HorizontalContainer infoEntreprise3 = new HorizontalContainer(0,0);
        infoEntreprise3.addElement(new SimpleTextBox(font,6,0,0, "Find all of our contact details on our website. "));
        infoEntreprise3.addElement(new SimpleTextBox(font,6,0,0, model.getCompany().getWebsite()));
        infoEntreprise3.addElement(new SimpleTextBox(font,6,0,0, ",  section \"Need help\""));

        infoEntreprise.addElement(infoEntreprise1);
        infoEntreprise.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        infoEntreprise.addElement(infoEntreprise2);
        infoEntreprise.addElement(new SimpleTextBox(font, 6, 0, 0, "You will be asked for your order number for any call"));
        infoEntreprise.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        infoEntreprise.addElement(infoEntreprise3);

        infoEntreprise.build(contentStream,writer);


        //Barcode top
        String barcode = this.getClass().getClassLoader().getResource("invoices/parts/ldlc/barcode.jpg").getFile();
        PDImageXObject pdBarcode = PDImageXObject.createFromFile(barcode, document);
        float ratioBarcode = pdBarcode.getWidth() / pdBarcode.getHeight();
        int tailleBarcode = 150;
        float posBarcodeX = page.getMediaBox().getWidth()/2-tailleBarcode/2;
        float posBarcodeY = posHeaderY-(tailleBarcode/ratioBarcode)/2;
        new ImageBox(pdBarcode, posBarcodeX, posBarcodeY, tailleBarcode, tailleBarcode/ratioBarcode,"").build(contentStream,writer);

        //Billing Address
        int hBH = tailleBarcode-65 ;
        int wBH = (int) (posBarcodeX-20);
        float posBHX = 25;
        float posBHY = (posBarcodeY-hBH-10);
        new BorderBox(Color.BLACK, Color.WHITE, 1,posBHX, posBHY, posBarcodeX-20,hBH).build(contentStream,writer);
        VerticalContainer verticalAddressContainer = new VerticalContainer(28, posBarcodeY-12, 250 );
        verticalAddressContainer.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Client Delivery:"));
        verticalAddressContainer.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getBillingName().toUpperCase(), "BN" ));
        verticalAddressContainer.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getBillingAddress().getLine1().toUpperCase(), "BA" ));
        verticalAddressContainer.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getBillingAddress().getZip().toUpperCase() + " "+model.getClient().getBillingAddress().getCity().toUpperCase(), "BA" ));
        verticalAddressContainer.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getBillingAddress().getCountry().toUpperCase(), "BA" ));
        verticalAddressContainer.build(contentStream,writer);

        //Shipping Address
        int wSH = wBH;
        int hSH = hBH;
        float posSHX = posBarcodeX+tailleBarcode;
        float posSHY = posBarcodeY-hSH-10;
        new BorderBox(Color.BLACK, Color.WHITE, 1,posSHX, posSHY, wSH,hSH).build(contentStream,writer);
        VerticalContainer verticalAddressContainer2 = new VerticalContainer(posBarcodeX+tailleBarcode+2, posBarcodeY-12, 250 );
        verticalAddressContainer2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Client Invoice :"));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getShippingName().toUpperCase(),"SHN" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getShippingAddress().getLine1().toUpperCase(),"SHA" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getShippingAddress().getZip().toUpperCase() + " "+model.getClient().getShippingAddress().getCity().toUpperCase(),"SHA" ));
        verticalAddressContainer2.addElement(new SimpleTextBox(fontI, 9, 0, 0, model.getClient().getShippingAddress().getCountry().toUpperCase(), "SHA" ));
        verticalAddressContainer2.build(contentStream,writer);

        //REF
        float posRefX = page.getMediaBox().getWidth()-page.getMediaBox().getWidth()/3+20;
        float posRefY = page.getMediaBox().getHeight()-49;
        new BorderBox(Color.BLACK, Color.WHITE, 1,posRefX, posRefY, page.getMediaBox().getWidth()-page.getMediaBox().getWidth()/3,50).build(contentStream,writer);
        new SimpleTextBox(font, 9, posRefX+5, posRefY+48, "Reference to remember when paying").build(contentStream,writer);
        VerticalContainer verticalREF1 = new VerticalContainer(posRefX+5, posRefY+38, 250 );
        verticalREF1.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getLabelClient()));
        verticalREF1.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getLabelInvoice()));
        verticalREF1.addElement(new SimpleTextBox(font, 9, 0, 0, model.getDate().getLabelInvoice()));
        verticalREF1.build(contentStream,writer);

        VerticalContainer verticalREF2 = new VerticalContainer(posRefX+100, posRefY+38, 250 );
        verticalREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getReference().getValueClient(),"CNUM"));
        verticalREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getReference().getValueInvoice(),"IN"));
        verticalREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getDate().getValueInvoice(),"IDATE"));
        verticalREF2.build(contentStream,writer);

        //Numero et Ref
        float posNumRefX = posSHX+10;
        float posNumRefY = posSHY+hSH+5;
        int hNumRef = (int) (posRefY-posNumRefY-5);
        BorderBox bdNumRef = new BorderBox(Color.BLACK, Color.WHITE, 1,posNumRefX, posNumRefY, wSH-10,hNumRef);
        bdNumRef.build(contentStream,writer);
        VerticalContainer verticalNumREF = new VerticalContainer(posNumRefX+5, posRefY-10, 250 );
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getLabelInvoice()));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, model.getDate().getLabelInvoice()));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getLabelClient()));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getLabelOrder()));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, "Ref Client : "));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, "Representing :"));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, "N° Intra Comm :"));
        verticalNumREF.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF.addElement(new SimpleTextBox(font, 9, 0, 0, "N° Siret :"));
        verticalNumREF.build(contentStream,writer);

        VerticalContainer verticalNumREF2 = new VerticalContainer(verticalNumREF.getBBox().getPosX()+verticalNumREF.getBBox().getWidth()+15, posRefY-10, 250 );
        verticalNumREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getReference().getValueInvoice(),"IN"));
        verticalNumREF2.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getDate().getValueInvoice(),"IDATE"));
        verticalNumREF2.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF2.addElement(new SimpleTextBox(font, 9, 0, 0, model.getReference().getValueClient(),"CNUM"));
        verticalNumREF2.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getReference().getValueOrder(),"ONUM"));
        verticalNumREF2.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF2.addElement(new SimpleTextBox(font, 9, 0, 0, ""));
        verticalNumREF2.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalNumREF2.addElement(new SimpleTextBox(font, 9, 0, 0, "INTERNET"));
        verticalNumREF2.build(contentStream,writer);

        //num facture ss code barre
        new SimpleTextBox(font, 9, posBarcodeX+tailleBarcode/2-model.getReference().getValueInvoice().length()*2, posBarcodeY-tailleBarcode/ratioBarcode, model.getReference().getValueInvoice(),"IN").build(contentStream,writer);

        //ligne garantie + nb page
        int posGarantieY = (int) (posBHY-3);
        new SimpleTextBox(fontBold, 7, posBHX, posBHY-3, "WARRANTY: The labels stuck on new parts are necessary for the warranty. Packaging should be kept").build(contentStream,writer);
        new SimpleTextBox(fontBold, 9, page.getMediaBox().getWidth()-70, posBHY-2, "page 1/"+document.getNumberOfPages()).build(contentStream,writer);

        float[] configRow = {70f, 210f, 28f, 45f, 35f, 50f, 40f, 40f, 20f};
        TableRowBox firstLine = new TableRowBox(configRow, 0, 0);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Code", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Designation", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Qty", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Px Unit. €", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Rem %", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Mtt base. €", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Eco-P. HT", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "Uni. Vte", Color.BLACK, Color.WHITE), false);
        firstLine.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "T", Color.BLACK, Color.WHITE), false);

        VerticalContainer verticalInvoiceItems = new VerticalContainer(25, posGarantieY-15, 600 );
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(5), 0));
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(firstLine);
        verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceItems.addElement(new HorizontalLineBox(0,0, page.getMediaBox().getWidth()-(25), 0));

        /*new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX(), firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+70f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+280f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+308f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+353f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+388f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+438f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+478f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+518f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);
        new BorderBox(Color.BLACK,Color.BLACK, 0,firstLine.getBBox().getPosX()+530f, firstLine.getBBox().getPosY()-15, 1, 20).build(contentStream,writer);*/


        float reduc = 10;
        float VAT;
        String TVACode = "";
        String[][] tabTVA = new String[2][model.getProductContainer().getProducts().size()];

        for(int w=0; w< model.getProductContainer().getProducts().size(); w++) {

            Product randomProduct = model.getProductContainer().getProducts().get(w);

            TableRowBox productLine = new TableRowBox(configRow, 0, 0);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, randomProduct.getEan(), "SNO"), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, (randomProduct.getName()!=null)?randomProduct.getName().toUpperCase():"", "PD"), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, Float.toString(randomProduct.getQuantity()), "QTY"), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, Float.toString(randomProduct.getPrice()), "UP"), false);
            reduc = randomProduct.getTotalDiscount();
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, Float.toString(reduc)), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, Float.toString(randomProduct.getTotalPrice()), "PTWTX" ), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, Float.toString(randomProduct.getTotalPrice()*(reduc/100)) ), false);
            productLine.addElement(new SimpleTextBox(font, 8, 2, 0, "PIE"), false);
            // TODO fix hardcoded tax value checks
            // VAT = randomProduct.getTaxRate()*1000;
            VAT = 200;
            TVACode = "1";
            tabTVA[0][w] = TVACode;
            tabTVA[1][w] = Float.toString(randomProduct.getTotalPrice());
            productLine.addElement(new SimpleTextBox(font, 8, 0, 0, TVACode), false);

            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
            verticalInvoiceItems.addElement(productLine);
            verticalInvoiceItems.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        }

        verticalInvoiceItems.build(contentStream,writer);

        new HorizontalLineBox(25,255, page.getMediaBox().getWidth()-(25), 0).build(contentStream,writer);

        float[] configRowTVA = {30f, 42f, 90f, 90f};
        TableRowBox firstLineTVA = new TableRowBox(configRowTVA, 0, 0);
        firstLineTVA.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "T", Color.BLACK, Color.WHITE), true);
        firstLineTVA.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "% VAT", Color.BLACK, Color.WHITE), true);
        firstLineTVA.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "CALCULATION BASIS", Color.BLACK, Color.WHITE), true);
        firstLineTVA.addElement(new SimpleTextBox(fontBold, 8, 2, 0, "TOTAL", Color.BLACK, Color.WHITE), true);

        VerticalContainer verticalInvoiceTVA = new VerticalContainer(25, 250, 600 );
        verticalInvoiceTVA.addElement(new HorizontalLineBox(0,0, 250, 0));
        verticalInvoiceTVA.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceTVA.addElement(firstLineTVA);
        verticalInvoiceTVA.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceTVA.addElement(new HorizontalLineBox(0,0, 250, 0));

        new HorizontalLineBox(25,190, 250, 0).build(contentStream,writer);

        float totalTVA1 = 0 ;
        totalTVA1 +=  Float.parseFloat(tabTVA[1][0]);

        TableRowBox TVALine = new TableRowBox(configRowTVA, 0, 0);
        if(totalTVA1 != 0){
            TVALine.addElement(new SimpleTextBox(font, 8, 2, 0, "1"), true);
            TVALine.addElement(new SimpleTextBox(font, 8, 2, 0, "2,1", "TXR"), true);
            TVALine.addElement(new SimpleTextBox(font, 8, 2, 0, totalTVA1+"", "TTX"), true);
            TVALine.addElement(new SimpleTextBox(font, 8, 2, 0, (float)((int)(totalTVA1*0.021*1000))/1000 +""), true);
        }
        verticalInvoiceTVA.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));
        verticalInvoiceTVA.addElement(TVALine);
        verticalInvoiceTVA.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 5));

        verticalInvoiceTVA.build(contentStream,writer);

        //TOTAUX
        float posTotauxX = 340;
        float posTotauxY = 165;
        new BorderBox(Color.BLACK, Color.WHITE, 1,posTotauxX, posTotauxY, 225,85).build(contentStream,writer);
        VerticalContainer verticalTotaux = new VerticalContainer(posTotauxX+2, posTotauxY+85-2, 250 );
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Discount amount"));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Postage amount"));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getProductContainer().getTotalHead()));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Of which eco-participation"));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getProductContainer().getTaxRateHead()));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getProductContainer().getWithTaxTotalHead()));
        verticalTotaux.addElement(new SimpleTextBox(fontBold, 9, 0, 0,"Your payment"));
        verticalTotaux.build(contentStream,writer);

        VerticalContainer verticalTotaux2 = new VerticalContainer(posTotauxX+190, posTotauxY+85-2, 250 );
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, "0,00"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, "0,00"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, (float)((int)model.getProductContainer().getTotal()*100)/100+"","TWTX"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, "0,00"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, (float)((int)model.getProductContainer().getTotalTax()*100)/100+"", "TTX"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0, (float)((int)model.getProductContainer().getTotalWithTax()*100)/100+"","TA"));
        verticalTotaux2.addElement(new SimpleTextBox(font, 9, 0, 0,(float)((int)model.getProductContainer().getTotalWithTax()*100)/100+"", "TA"));
        verticalTotaux2.build(contentStream,writer);

        new BorderBox(Color.BLACK, Color.WHITE, 1,posTotauxX, posTotauxY-30, 225,28).build(contentStream,writer);
        VerticalContainer verticalNet = new VerticalContainer(posTotauxX+3, posTotauxY-5, 250 );
        verticalNet.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "NET TO PAY"));
        verticalNet.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "Payment deadline"));
        verticalNet.build(contentStream,writer);

        VerticalContainer verticalNet2 = new VerticalContainer(posTotauxX+170, posTotauxY-5, 250 );
        verticalNet2.addElement(new SimpleTextBox(fontBold, 9, 0, 0, "0,00 "));
        verticalNet2.addElement(new SimpleTextBox(font, 9, 0, 0, model.getDate().getValueInvoice(),"IDATE"));
        verticalNet2.build(contentStream,writer);

        new SimpleTextBox(fontBold, 9, 30, 182, "Our invoices are denominated in Euros.").build(contentStream,writer);
        new SimpleTextBox(fontBold, 9, 30, 162, "No discount for early payment will be granted.").build(contentStream,writer);

        new BorderBox(Color.BLACK, Color.WHITE, 1,25, 56, 160,80).build(contentStream,writer);
        VerticalContainer verticalPaiement = new VerticalContainer(25+3, 140-5, 250 );
        verticalPaiement.addElement(new SimpleTextBox(font, 9, 0, 0, "PROVIDED SETTLEMENT"));
        verticalPaiement.addElement(new SimpleTextBox(fontBold, 9, 0, 0, model.getPaymentInfo().getValuePaymentType()));
        new BorderBox(Color.BLACK, Color.BLACK, 1,25, 113, 160,1).build(contentStream,writer);
        verticalPaiement.build(contentStream,writer);

        String footer2 = this.getClass().getClassLoader().getResource("invoices/parts/ldlc/footer2.jpg").getFile();
        PDImageXObject logoFooter2 = PDImageXObject.createFromFile(footer2, document);
        float ratioFooter2 = (float)logoFooter2.getWidth() / (float)logoFooter2.getHeight();
        float tailleFooter2 = 155;
        float posFooter2X = 27;
        float posFooter2Y = 58;
        contentStream.drawImage(logoFooter2, posFooter2X, posFooter2Y, tailleFooter2, tailleFooter2/ratioFooter2);

        VerticalContainer infoFooter = new VerticalContainer(192,135,400);

        HorizontalContainer infoFooter1 = new HorizontalContainer(0,0);
        infoFooter1.addElement(new SimpleTextBox(font,6,0,0, model.getCompany().getName()+" ","SN"));
        infoFooter1.addElement(new SimpleTextBox(font,6,0,0, " - SA with management board and supervisory board with a capital of "+ (100000+(Math.random()*(9999999 - 100000))) +" Eur - "));
        infoFooter1.addElement(new SimpleTextBox(font,6,0,0, idNumbers.getCidValue(),"SCID"));


        HorizontalContainer infoFooter2 = new HorizontalContainer(0,0);
        infoFooter2.addElement(new SimpleTextBox(font,6,0,0, idNumbers.getToaLabel()+" "));
        infoFooter2.addElement(new SimpleTextBox(font,6,0,0, idNumbers.getToaValue(),"STOA"));
        infoFooter2.addElement(new SimpleTextBox(font,6,0,0, " - "+idNumbers.getVatLabel()+" "));
        infoFooter2.addElement(new SimpleTextBox(font,6,0,0, idNumbers.getVatValue(),"SVAT"));

        HorizontalContainer infoFooter3 = new HorizontalContainer(0,0);
        infoFooter3.addElement(new SimpleTextBox(font,6,0,0, idNumbers.getSiretLabel()+" : "));
        infoFooter3.addElement(new SimpleTextBox(font,6,0,0,idNumbers.getSiretValue()));

        infoFooter.addElement(new SimpleTextBox(font, 6, 0, 0, "The general conditions of sale (\"GTC\") applicable to your order accepted during its registration are"));
        infoFooter.addElement(new SimpleTextBox(font, 6, 0, 0, "also available on our site."));
        infoFooter.addElement(new BorderBox(Color.WHITE,Color.WHITE,0,0,0,0,9));
        infoFooter.addElement(infoFooter1);
        infoFooter.addElement(infoFooter2);
        infoFooter.addElement(infoFooter3);

        infoFooter.build(contentStream,writer);

        new SimpleTextBox(fontI, 6, 238, 56, "Do not throw on the public highway").build(contentStream,writer);

        contentStream.close();

    }
}
