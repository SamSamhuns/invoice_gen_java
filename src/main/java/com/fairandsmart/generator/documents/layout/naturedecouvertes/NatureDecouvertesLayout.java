package com.fairandsmart.generator.documents.layout.naturedecouvertes;

import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.helper.HelperImage;

import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.ProductContainer;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.Client;

import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.product.ProductTableBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.line.VerticalLineBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.table.TableRowBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;
import com.fairandsmart.generator.documents.element.footer.FootCompanyBox;

import com.mifmif.common.regex.Generex;

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
import java.util.ArrayList;
import java.util.stream.Collectors;


public class NatureDecouvertesLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Nature&Decouvertes";
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

        // get barcode number
        String barcodeNum = model.getReference().getValueBarcode();

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN: fontB;

        boolean upperCap = rnd.nextBoolean();

        // Page coords
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 30;
        float rightPageMargin = 30;
        float topPageMargin = 10;
        float bottomPageMargin = 10;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = new Color(239,239,239);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

        // always set to false but individually change SimpleTextBox HAlign
        float ratioPage = 0.24f; // pageWidth/2480;  pageHeight 841; pageWidth 595;

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
        if (proba.get("logo_top")) {
            maxLogoWidth = 150;
            maxLogoHeight = 90;
            logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
            logoWidth = logoImg.getWidth() * logoScale;
            logoHeight = logoImg.getHeight() * logoScale;
            posLogoX = leftPageMargin;
            posLogoY = pageHeight-topPageMargin;
            new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo").build(stream,writer);
        } else {  // Barcode top
            new ImageBox(barcodeImg, leftPageMargin, pageHeight-topPageMargin, barcodeImg.getWidth(), (float)(barcodeImg.getHeight()/2), barcodeNum).build(stream,writer);
        }

        float topAddrX = 307;
        float topAddrY = pageHeight - topPageMargin;
        // company/vendor info top (switch with billing address sometimes)
        if (proba.get("vendor_address_top")) {
            VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,9,11,250,lineStrokeColor,model,annot,proba);
            vendorInfoBox.translate(topAddrX, topAddrY);
            vendorInfoBox.build(stream,writer);
        }
        else { // add the Payment Info and address
            float pAW = 330;
            float pAX = topAddrX;
            float pAY = topAddrY;
            proba.put("vendor_tax_number_top", proba.get("vendor_address_tax_number"));

            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,8,9,pAW,lineStrokeColor,model,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(stream,writer);
        }

        // check if billing and shipping addresses should be switched
        float leftAddrX = leftPageMargin;
        float rightAddrX = topAddrX;
        if (proba.get("switch_bill_ship_addresses")) {
            float tmp = leftAddrX; leftAddrX=rightAddrX; rightAddrX=tmp;
        }
        float billX = leftAddrX; float billY = pageHeight - topPageMargin - 100;
        float shipX = rightAddrX; float shipY = billY;

        // billing address
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,11,250,lineStrokeColor,model,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(stream,writer);

        // shipping address
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,11,250,lineStrokeColor,model,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(stream,writer);

        // table top order num, order id, invoice id, inv date and due date info
        float tableTopY = billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight() - 25;

        if (proba.get("currency_top")) {
            new SimpleTextBox(fontB,12, leftPageMargin,tableTopY, payment.getLabelAccountCurrency()+": "+cur,"CUR").build(stream,writer);
            annot.getTotal().setCurrency(cur);
        }
        else if (proba.get("purchase_order_number_top")) {
            new SimpleTextBox(fontB,12, leftPageMargin,tableTopY, model.getReference().getLabelOrder()+": "+model.getReference().getValueOrder(),"ONUM").build(stream,writer);
            annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());

            new SimpleTextBox(fontNB,12, leftPageMargin,tableTopY-13, model.getDate().getLabelPaymentDue()+": "+model.getDate().getValuePaymentDue(),"DDATE").build(stream,writer);
            annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        }
        if (proba.get("invoice_number_top")) {
            new SimpleTextBox(fontB,12, topAddrX,tableTopY, model.getReference().getLabelInvoice()+": "+model.getReference().getValueInvoice(),"IN").build(stream,writer);
            annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());

            new SimpleTextBox(fontNB,12, topAddrX,tableTopY-13, model.getDate().getLabelInvoice()+": "+model.getDate().getValueInvoice(),"IN").build(stream,writer);
            annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        }
        ////////////////////////////////////      Building Table      ////////////////////////////////////

        float tableTopPosX = leftPageMargin;
        float tableTopPosY = tableTopY-35;
        float tableWidth = pageWidth - leftPageMargin - rightPageMargin;
        int maxHdrNum = 14;
        ProductTableBox productTableBox = new ProductTableBox(
                maxHdrNum, fontN, fontB, fontI, 8, 8, 600, lineStrokeColor, tableTopPosX, tableTopPosY, tableWidth, model, annot, proba);
        productTableBox.build(stream,writer);

        float xPos = leftPageMargin;
        float yPos = tableTopPosY-12;
        float tableHeight = 350;
        float[] configRow = productTableBox.getConfigRow();

        // table footer payment & total info
        VerticalContainer paymentInfoCont = new VerticalContainer(340, yPos-tableHeight-5, 250);
        paymentInfoCont.addElement(new SimpleTextBox(fontB,11,0,0,pc.getWithTaxAndDiscountTotalHead()));
        paymentInfoCont.addElement(new SimpleTextBox(fontN,8,0,0,payment.getLabelPaymentType()+": "+payment.getValuePaymentType(),"PT"));
        paymentInfoCont.build(stream,writer);

        VerticalContainer totalCont = new VerticalContainer(500, yPos-tableHeight-5, 250);
        totalCont.addElement(new SimpleTextBox(fontN,11,0,0,pc.getFmtTotalWithTaxAndDiscount(),"TA"));
        annot.getTotal().setTotalPrice(pc.getFmtTotalWithTaxAndDiscount());
        totalCont.build(stream,writer);

        float tableBottomY = yPos-tableHeight-paymentInfoCont.getBBox().getHeight()-10;
        // Add vertical borders across columns in table
        new VerticalLineBox(xPos, yPos, xPos, tableBottomY, lineStrokeColor).build(stream,writer);  // first line extends down more
        xPos += configRow[0];
        for (int i=1; i < configRow.length; i++) {
            if (i < configRow.length - 1) {
                new VerticalLineBox(xPos-2, yPos, xPos-2, yPos-tableHeight, lineStrokeColor).build(stream,writer);
            } else {
                new VerticalLineBox(xPos-2, yPos, xPos-2, tableBottomY, lineStrokeColor).build(stream,writer); // penultimate line extends down more
            }
            xPos += configRow[i];
        }
        new VerticalLineBox(xPos, yPos, xPos, tableBottomY, lineStrokeColor).build(stream,writer);  // final line extends down more
        // add table bottom horizontal lines
        new HorizontalLineBox(leftPageMargin, yPos - tableHeight, pageWidth-rightPageMargin, yPos - tableHeight, lineStrokeColor).build(stream,writer);
        new HorizontalLineBox(leftPageMargin, tableBottomY, pageWidth-rightPageMargin, tableBottomY, lineStrokeColor).build(stream,writer);

        ////////////////////////////////////      Finished Table      ////////////////////////////////////

        // text above barcode bottom left
        VerticalContainer backContainer = new VerticalContainer(82, bottomPageMargin+165, 150);
        backContainer.addElement(new SimpleTextBox(fontN,10,0,0,"Label to stick on your package in case of return. "));
        backContainer.addElement(new SimpleTextBox(fontN,8,0,0,"Order "+model.getReference().getValueOrder()));
        annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());
        backContainer.build(stream,writer);

        // barcode bottom left
        new ImageBox(barcodeImg, leftPageMargin+20, backContainer.getBBox().getPosY()-backContainer.getBBox().getHeight()-3, 160, 50, barcodeNum).build(stream,writer);

        // footer table with total amt + total taxFCont info
        float[] footerConfigRow = {55,55,45,60};
        float tFX = 345, tFY = tableBottomY - 10;

        VerticalContainer taxFCont = new VerticalContainer(tFX, tFY, 230);
        HAlign taxAlign = HAlign.CENTER;

        // tax+total label heads
        TableRowBox taxLabelRow = new TableRowBox(footerConfigRow,0,0);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, "VAT Code", black,null,taxAlign), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTotalHead(), black,null,taxAlign), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTaxRateHead(), black,null,taxAlign), false);
        taxLabelRow.addElement(new SimpleTextBox(fontB,8,0,0, pc.getTaxTotalHead(), black,null,taxAlign), false);
        taxLabelRow.setBackgroundColor(grayish);
        taxLabelRow.setBorderColor(black);
        taxFCont.addElement(taxLabelRow);

        taxFCont.addElement(new BorderBox(white,white,0,0,0,0,1));

        // tax+total value heads
        TableRowBox taxValueRow = new TableRowBox(footerConfigRow,0,0);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, new Generex("[0-9]{5}").random(), black,null,taxAlign), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotal(), black,null,taxAlign), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotalTaxRate(), black,null,taxAlign), false);
        taxValueRow.addElement(new SimpleTextBox(fontN,8,0,0, pc.getFmtTotalTax(), black,null,taxAlign), false);
        taxValueRow.setBorderColor(black);
        taxFCont.addElement(taxValueRow);

        annot.getTotal().setSubtotalPrice(pc.getFmtTotal());
        annot.getTotal().setTaxRate(pc.getFmtTotalTaxRate());
        annot.getTotal().setTaxPrice(pc.getFmtTotalTax());

        taxFCont.build(stream,writer);

        // Add Signature at bottom right
        if (proba.get("signature_bottom")) {
            String compSignatureName = company.getName();
            compSignatureName = compSignatureName.length() < 25? compSignatureName: "";
            float sigTX = tFX;
            float sigTY = taxFCont.getBBox().getPosY() - taxFCont.getBBox().getHeight() - 60;
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN, 8,sigTX,sigTY, company.getSignature().getLabel()+" "+compSignatureName, "Signature");
            sigTextBox.build(stream,writer);

            new HorizontalLineBox(
                    sigTX - 10, sigTY + 5,
                    sigTX + sigTextBox.getBBox().getWidth() + 5, sigTY + 5,
                    lineStrokeColor).build(stream,writer);

            String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
            PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

            float maxSW = 100, maxSH = 60;
            float sigScale = Math.min(maxSW/sigImg.getWidth(), maxSH/sigImg.getHeight());
            float sigW = sigImg.getWidth() * sigScale;
            float sigH = sigImg.getHeight() * sigScale;
            // align signature to center of sigTextBox bbox
            float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;
            float sigIY = sigTY + sigH + 10;

            new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "signature").build(stream,writer);
        }

        // Add company stamp watermark, 40% prob
        if (proba.get("stamp_bottom")) {
            float alpha = HelperCommon.rand_uniform(0.6f, 0.8f);
            float resDim = 105 + rnd.nextInt(20);
            float xPosStamp, yPosStamp;
            // draw to lower right if signature in bottom
            if (proba.get("signature_bottom") && rnd.nextInt(3) < 2) {
                xPosStamp = 400 + rnd.nextInt(15);
                yPosStamp = 80 + rnd.nextInt(20);
            }
            else {  // draw to lower center
                xPosStamp = pageWidth/2 - (resDim/2) + rnd.nextInt(10) - 10;
                yPosStamp = 80 + rnd.nextInt(20);
            }

            StampBox stampBox = new StampBox(resDim,resDim,alpha,model,document,company,proba);
            stampBox.translate(xPosStamp,yPosStamp);
            stampBox.build(stream,writer);
        }
        // if no signature and no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampText = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            VerticalContainer noStampCont = new VerticalContainer(leftPageMargin,tFY,240);
            noStampCont.addElement(new SimpleTextBox(fontN,7,0,0, noStampText, "Footnote"));
            noStampCont.build(stream,writer);
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

        // footer company name, info, address & contact information at bottom center
        if (proba.get("vendor_info_footer")) {
            FootCompanyBox footCompanyBox = new FootCompanyBox(fontN,fontB,fontI,7,8, themeColor, pageWidth-leftPageMargin-rightPageMargin,model,annot,proba);
            float fW = footCompanyBox.getBBox().getWidth();
            footCompanyBox.alignElements(HAlign.CENTER, fW);
            footCompanyBox.translate(pageMiddleX-fW/2,55);
            footCompanyBox.build(stream,writer);
        }

        stream.close();
        writer.writeEndElement();
      }
  }
