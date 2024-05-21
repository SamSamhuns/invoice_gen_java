package com.fairandsmart.generator.documents.layout.ldlc;

import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.helper.HelperImage;

import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.InvoiceNumber;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.product.ProductTableBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        InvoiceNumber ref = model.getReference();
        PaymentInfo payment = model.getPaymentInfo();
        ProductContainer pc = model.getProductContainer();
        String cur = pc.getCurrency();
        Address address = company.getAddress();
        IDNumbers idNumbers = company.getIdNumbers();

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> proba = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // get barcode number
        String barcodeNum = ref.getValueBarcode();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 20;
        float rightPageMargin = 20;
        float bottomPageMargin = 5;
        float topPageMargin = 5;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = new Color(239,239,239);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

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

        PDPageContentStream stream = new PDPageContentStream(document, page);

        // Logo top
        maxLogoWidth = 150;
        maxLogoHeight = 80;
        logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        logoWidth = logoImg.getWidth() * logoScale;
        logoHeight = logoImg.getHeight() * logoScale;
        posLogoX = leftPageMargin;
        posLogoY = pageHeight-topPageMargin;
        new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(stream,writer);

        // Vendor simplified address
        VerticalContainer infoCompanyCont = new VerticalContainer(leftPageMargin,posLogoY-logoHeight-20,400);

        HorizontalContainer infoCompany1 = new HorizontalContainer(0,0);
        String info1Text  = company.getName();
        annot.getVendor().setVendorName(company.getName());
        if (proba.get("vendor_tax_number_top")) {
            info1Text += " , "+idNumbers.getVatLabel()+": "+idNumbers.getVatValue();
            annot.getVendor().setVendorTrn(idNumbers.getVatValue());
        }
        infoCompany1.addElement(new SimpleTextBox(fontN,6,0,0, info1Text,"SA"));

        HorizontalContainer infoCompany2 = new HorizontalContainer(0,0);
        infoCompany2.addElement(new SimpleTextBox(fontN,6,0,0, address.getLine1()+" , ","SA"));
        infoCompany2.addElement(new SimpleTextBox(fontN,6,0,0, address.getZip() + " - " +address.getCity(),"SA"));
        annot.getVendor().setVendorAddr(address.getLine1()+" , "+address.getZip()+" - "+address.getCity());
        annot.getVendor().setVendorPOBox(address.getZip());

        HorizontalContainer infoCompany3 = new HorizontalContainer(0,0);
        infoCompany3.addElement(new SimpleTextBox(fontN,6,0,0, "Non-surcharged Customer Service number : "));
        infoCompany3.addElement(new SimpleTextBox(fontN,6,0,0, company.getContact().getPhoneValue(),"SCN"));

        HorizontalContainer infoCompany4 = new HorizontalContainer(0,0);
        infoCompany4.addElement(new SimpleTextBox(fontN,6,0,0, "Find all of our contact details on our website. "));
        infoCompany4.addElement(new SimpleTextBox(fontN,6,0,0, company.getWebsite()));
        infoCompany4.addElement(new SimpleTextBox(fontN,6,0,0, ",  section \"Need help\""));

        infoCompanyCont.addElement(infoCompany1);
        infoCompanyCont.addElement(infoCompany2);
        infoCompanyCont.addElement(new BorderBox(white,white,0,0,0,0,9));
        infoCompanyCont.addElement(infoCompany3);
        infoCompanyCont.addElement(new BorderBox(white,white,0,0,0,0,9));
        infoCompanyCont.addElement(infoCompany4);

        infoCompanyCont.build(stream,writer);

        // title top
        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        SimpleTextBox docTitleBox = new SimpleTextBox(fontNB, 15, 0, 0, docTitle);
        docTitleBox.translate(pageMiddleX-docTitleBox.getBBox().getWidth()/2, pageHeight-topPageMargin-30);
        annot.setTitle(docTitle);
        docTitleBox.build(stream,writer);

        // Barcode top
        float sizeBarcode = barcodeImg.getWidth() - 30;
        float ratioBarcode = barcodeImg.getWidth() / barcodeImg.getHeight();
        float posBarcodeX = pageWidth/2-sizeBarcode/2;
        float posBarcodeY = pageHeight-145-(sizeBarcode/ratioBarcode)/2;
        if (proba.get("barcode_top")) {
            new ImageBox(barcodeImg, posBarcodeX, posBarcodeY, sizeBarcode, (float)(barcodeImg.getHeight() / 1.5), "barcode:"+barcodeNum).build(stream,writer);
        }

        // top right invoice num+date reference info
        VerticalContainer vertLabelCont = new VerticalContainer(0,0,250);
        vertLabelCont.addElement(new SimpleTextBox(fontN,9,0, 0, ref.getLabelInvoice()));
        vertLabelCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertLabelCont.addElement(new SimpleTextBox(fontN,9,0,0, model.getDate().getLabelInvoice()));
        vertLabelCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertLabelCont.addElement(new SimpleTextBox(fontN,9,0,0, ref.getLabelClient()));
        vertLabelCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertLabelCont.addElement(new SimpleTextBox(fontN,9,0,0, ref.getLabelOrder()));
        vertLabelCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertLabelCont.addElement(new SimpleTextBox(fontN,9,0,0, model.getDate().getLabelOrder()));

        VerticalContainer vertValueCont = new VerticalContainer(0,0,250);
        vertValueCont.addElement(new SimpleTextBox(fontNB,9,0,0, ref.getValueInvoice(),"IN"));
        vertValueCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertValueCont.addElement(new SimpleTextBox(fontNB,9,0,0, model.getDate().getValueInvoice(),"IDATE"));
        vertValueCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertValueCont.addElement(new SimpleTextBox(fontNB,9,0,0, ref.getValueClient(),"CNUM"));
        vertValueCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertValueCont.addElement(new SimpleTextBox(fontNB,9,0,0, ref.getValueOrder(),"ONUM"));
        vertValueCont.addElement(new BorderBox(white,white,0, 0,0, 0,5));
        vertValueCont.addElement(new SimpleTextBox(fontNB,9,0,0, model.getDate().getValueOrder(),"ODATE"));

        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        annot.getInvoice().setInvoiceId(ref.getValueInvoice());

        float refBoxW = vertLabelCont.getBBox().getWidth() + vertValueCont.getBBox().getWidth();
        float refBoxH = Math.max(vertLabelCont.getBBox().getHeight(), vertValueCont.getBBox().getHeight()) + 10;
        float refPosX = docTitleBox.getBBox().getPosX() + docTitleBox.getBBox().getWidth() + 15;
        float refPosY = pageHeight - topPageMargin -5;

        new BorderBox(lineStrokeColor,white,1, refPosX, refPosY-refBoxH, refBoxW+55, refBoxH).build(stream,writer);
        vertLabelCont.translate(refPosX+5, refPosY-5);
        vertValueCont.translate(refPosX+110, refPosY-5);
        vertLabelCont.build(stream,writer);
        vertValueCont.build(stream,writer);

        // check if billing and shipping addresses should be switched
        float leftX = leftPageMargin, rightX = posBarcodeX+sizeBarcode+5;
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftX; leftX=rightX; rightX=tmp;
        }
        float billX = leftX; float billY = infoCompanyCont.getBBox().getPosY() - infoCompanyCont.getBBox().getHeight() - 10;
        float shipX = rightX; float shipY = infoCompanyCont.getBBox().getPosY() - infoCompanyCont.getBBox().getHeight() - 10;

        // Billing Address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        billingInfoBox.translate(billX+5, billY-10);

        // Shipping Address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        shippingInfoBox.translate(shipX+5, shipY-10);

        float billH = billingInfoBox.getBBox().getHeight();
        float shipH = shippingInfoBox.getBBox().getHeight();
        float maxAddrH = Math.max(billH, shipH)+30;

        new BorderBox(lineStrokeColor, white, 1, shipX,shipY-maxAddrH+2, 170,maxAddrH).build(stream,writer);
        new BorderBox(lineStrokeColor, white, 1, billX,billY-maxAddrH+2, 170,maxAddrH).build(stream,writer);
        billingInfoBox.build(stream,writer);
        shippingInfoBox.build(stream,writer);

        //guaranteed line + number of pages
        float posGuaY = Math.min(posBarcodeY-(float)(barcodeImg.getHeight() / 1.5), billY-maxAddrH+2) - 10;
        String guaText = rnd.nextBoolean() ? "WARRANTY: The packaging and labels stuck on new parts should not be discarded.": "WARRANTY: Void after 1 year.";
        SimpleTextBox guaTextBox1 = new SimpleTextBox(fontB,7,0,0, guaText);
        SimpleTextBox guaTextBox2 = new SimpleTextBox(fontB,9,0,0, "page 1/"+document.getNumberOfPages());
        guaTextBox1.translate(leftPageMargin, posGuaY);
        guaTextBox2.translate(pageWidth-rightPageMargin-guaTextBox2.getBBox().getWidth(), posGuaY);
        guaTextBox1.build(stream,writer);
        guaTextBox2.build(stream,writer);

        // Product table
        float tableTopPosX = leftPageMargin;
        float tableTopPosY = posGuaY - 10;
        float tableWidth = pageWidth-rightPageMargin-leftPageMargin;
        ProductTableBox productTableBox = new ProductTableBox(
                12, fontN, fontB, fontI, 8, 8, 600, lineStrokeColor, tableTopPosX, tableTopPosY, tableWidth, model, annot, proba);
        productTableBox.build(stream,writer);

        new HorizontalLineBox(leftPageMargin,260, pageWidth-rightPageMargin, 0, lineStrokeColor).build(stream,writer);

        // table footer left side tax info
        float[] configRowVAT = {60f, 90f};
        TableRowBox headerLineVAT = new TableRowBox(configRowVAT, 0, 0);
        headerLineVAT.addElement(new SimpleTextBox(fontB, 8, 2, 0, pc.getTaxRateTotalHead(), black, white), true);
        headerLineVAT.addElement(new SimpleTextBox(fontB, 8, 2, 0, pc.getTaxTotalHead(), black, white), true);
        TableRowBox valueLineVAT = new TableRowBox(configRowVAT, 0, 0);
        valueLineVAT.addElement(new SimpleTextBox(fontN, 8, 2, 0, pc.getFmtTotalTaxRate(), "TXR"), true);
        valueLineVAT.addElement(new SimpleTextBox(fontN, 8, 2, 0, pc.getFmtTotalTax(), "TTX"), true);

        annot.getTotal().setTaxRate(pc.getFmtTotalTaxRate());
        annot.getTotal().setTaxPrice(pc.getFmtTotalTax());

        VerticalContainer vertInvoiceVATCont = new VerticalContainer(leftPageMargin, 250, 600);

        vertInvoiceVATCont.addElement(new HorizontalLineBox(0,0, 180, 0, lineStrokeColor));
        vertInvoiceVATCont.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));
        vertInvoiceVATCont.addElement(headerLineVAT);
        vertInvoiceVATCont.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));
        vertInvoiceVATCont.addElement(new HorizontalLineBox(0,0, 180, 0, lineStrokeColor));
        vertInvoiceVATCont.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));
        vertInvoiceVATCont.addElement(valueLineVAT);
        vertInvoiceVATCont.addElement(new BorderBox(white,white, 0,0, 0, 0, 5));
        vertInvoiceVATCont.addElement(new HorizontalLineBox(0,0, 180, 0, lineStrokeColor));

        vertInvoiceVATCont.build(stream,writer);

        new SimpleTextBox(fontB,9,leftPageMargin, 182, (rnd.nextBoolean() ? "Our invoices are denominated in " : "Currency used: ")+cur+".").build(stream,writer);
        new SimpleTextBox(fontB,9,leftPageMargin, 162, (rnd.nextBoolean() ? "No discount for early payment." : "Discounts not applicable")).build(stream,writer);

        // table footer right side totals
        float posTotalX = 340;
        float posTotalY = 175;
        new BorderBox(lineStrokeColor,white,1, posTotalX,posTotalY, 235,75).build(stream,writer);
        VerticalContainer vertTotal1 = new VerticalContainer(posTotalX+2, posTotalY+75-2, 250 );
        vertTotal1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getDiscountHead()));
        vertTotal1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getTotalHead()));
        vertTotal1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getTaxRateTotalHead()));
        vertTotal1.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getWithTaxTotalHead()));
        vertTotal1.addElement(new SimpleTextBox(fontB, 9, 0, 0, rnd.nextBoolean() ? "Your payment": "Payment"));
        vertTotal1.build(stream,writer);

        VerticalContainer vertTotal2 = new VerticalContainer(posTotalX+150, posTotalY+75-2, 250 );
        vertTotal2.addElement(new SimpleTextBox(fontN, 9, 0, 0, (float)((int)pc.getTotalDiscount()*100)/100+"","TD"));
        vertTotal2.addElement(new SimpleTextBox(fontN, 9, 0, 0, (float)((int)pc.getTotal()*100)/100+"","TWTX"));
        vertTotal2.addElement(new SimpleTextBox(fontN, 9, 0, 0, pc.getFmtTotalTaxRate(), "TTX"));
        vertTotal2.addElement(new SimpleTextBox(fontN, 9, 0, 0, (float)((int)pc.getTotalWithTax()*100)/100+"","TA"));
        vertTotal2.addElement(new SimpleTextBox(fontN, 9, 0, 0,(float)((int)pc.getTotalWithTax()*100)/100+"", "TA"));

        annot.getTotal().setSubtotalPrice((float)((int)pc.getTotal()*100)/100+"");
        annot.getTotal().setDiscountPrice((float)((int)pc.getTotalDiscount()*100)/100+"");
        vertTotal2.build(stream,writer);

        // bottom right payment info
        new BorderBox(lineStrokeColor,white,1, posTotalX,posTotalY-35, 235,32).build(stream,writer);
        VerticalContainer verticalNet = new VerticalContainer(posTotalX+3, posTotalY-5, 250 );
        verticalNet.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getWithTaxAndDiscountTotalHead()));
        verticalNet.addElement(new SimpleTextBox(fontB, 9, 0, 0, model.getDate().getLabelPaymentDue()));
        verticalNet.build(stream,writer);

        VerticalContainer verticalNet2 = new VerticalContainer(posTotalX+150, posTotalY-5, 250 );
        verticalNet2.addElement(new SimpleTextBox(fontB, 9, 0, 0, pc.getFmtTotalWithTaxAndDiscount()+""));
        verticalNet2.addElement(new SimpleTextBox(fontN, 9, 0, 0, model.getDate().getValuePaymentDue(),"IDATE"));

        annot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount());
        annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        verticalNet2.build(stream,writer);

        // bottom left payment address
        float pAW = 300, pAX = leftPageMargin, pAY = 140;
        proba.put("addresses_bordered", true);
        PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,8,9,pAW,lineStrokeColor,model,annot,proba);
        paymentBox.translate(pAX, pAY);
        paymentBox.build(stream,writer);

        // Add Signature at bottom
        if (proba.get("signature_bottom")) {
            String sigText = company.getSignature().getLabel()+" "+(company.getName().length() < 25 ? company.getName() : "");
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0,sigText, "Signature");

            float sigTX;
            float sigTY = 50;
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
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(stream,writer);
        }

        // Barcode bottom
        if (proba.get("barcode_bottom") && !proba.get("barcode_top") && !proba.get("signature_bottom") && !proba.get("stamp_bottom")) {
            float bW = barcodeImg.getWidth() - 50, bH = barcodeImg.getHeight() - 80;
            ImageBox botBarcode = new ImageBox(barcodeImg,0,0, bW,bH, "barcode:"+barcodeNum);
            botBarcode.translate(pageWidth-bW-rightPageMargin, bH+bottomPageMargin);
            botBarcode.build(stream,writer);
        }

        // Add company stamp watermark
        if (proba.get("stamp_bottom")) {
            float alpha = HelperCommon.rand_uniform(0.6f, 0.8f);
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp, yPosStamp;
            // draw to lower right if signature in bottom or lower left if signature in bottom left
            if (proba.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = ((proba.get("signature_bottom_left")) ? leftPageMargin + 5 : 405) + rnd.nextInt(10);
                yPosStamp = 50 + rnd.nextInt(5);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(5) - 5;
                yPosStamp = 50 + rnd.nextInt(5);
            }
            StampBox stampBox = new StampBox(resDim,resDim,alpha,model,document,company,proba);
            stampBox.translate(xPosStamp,yPosStamp);
            stampBox.build(stream,writer);
        }
        // if no signature anåd no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontN, 7, leftPageMargin, 45, noStampMsg, "footnote").build(stream,writer);
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

        stream.close();
        writer.writeEndElement();
    }
}
