package com.fairandsmart.generator.documents.layout.materielnet;

import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.helper.HelperImage;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.data.model.PaymentInfo;
import com.fairandsmart.generator.documents.data.model.Client;
import com.fairandsmart.generator.documents.data.model.Address;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.data.model.IDNumbers;
import com.fairandsmart.generator.documents.data.model.ProductContainer;

import com.fairandsmart.generator.documents.element.product.ProductTableBox;
import com.fairandsmart.generator.documents.element.payment.PaymentInfoBox;
import com.fairandsmart.generator.documents.element.head.VendorInfoBox;
import com.fairandsmart.generator.documents.element.head.BillingInfoBox;
import com.fairandsmart.generator.documents.element.head.ShippingInfoBox;
import com.fairandsmart.generator.documents.element.HAlign;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.line.HorizontalLineBox;
import com.fairandsmart.generator.documents.element.footer.StampBox;
import com.fairandsmart.generator.documents.element.footer.FootCompanyBox;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class MaterielnetLayout implements InvoiceLayout {

    @Override
    public String name() {
        return "Materielnet";
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
        IDNumbers idNumbers = company.getIdNumbers();
        Address address = company.getAddress();
        String cur = pc.getCurrency();

        // get gen config probability map loading from config json file, int value out of 100, 60 -> 60% proba
        Map<String, Boolean> proba = HelperCommon.getMatchedConfigMap(model.getConfigMaps(), this.name());

        // Set fontFaces
        HelperCommon.PDCustomFonts fontSet = HelperCommon.getRandomPDFontFamily(document, this);
        PDFont fontN = fontSet.getFontNormal();
        PDFont fontB = fontSet.getFontBold();
        PDFont fontI = fontSet.getFontItalic();
        PDFont fontNB = (rnd.nextBoolean()) ? fontN : fontB;

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float pageMiddleX = pageWidth/2;
        float leftPageMargin = 25;
        float rightPageMargin = 25;
        float topPageMargin = 15;
        float bottomPageMargin = 15;

        // colors
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color lgray = new Color(220,220,220);
        Color grayish = HelperCommon.getRandomGrayishColor();
        List<Integer> themeRGB = company.getLogo().getThemeRGB();
        Color themeColor = new Color(themeRGB.get(0), themeRGB.get(1), themeRGB.get(2));
        themeRGB = themeRGB.stream().map(v -> Math.max((int)(v*0.7f), 0)).collect(Collectors.toList()); // darken colors
        Color lineStrokeColor = proba.get("line_stroke_black") ? black: themeColor;

        // load logo img
        String logoPath = HelperCommon.getResourceFullPath(this, "common/logo/" + company.getLogo().getFullPath());
        PDImageXObject logoImg = PDImageXObject.createFromFile(logoPath, document);
        float logoWidth; float logoHeight;
        float maxLogoWidth; float maxLogoHeight;
        float posLogoX; float posLogoY;
        float logoScale;

        /*//////////////////   Build Page components now   //////////////////*/

        PDPageContentStream stream = new PDPageContentStream(document, page);

        // logo top
        maxLogoWidth = 130;
        maxLogoHeight = 90;
        logoScale = Math.min(maxLogoWidth/logoImg.getWidth(), maxLogoHeight/logoImg.getHeight());
        logoWidth = logoImg.getWidth() * logoScale;
        logoHeight = logoImg.getHeight() * logoScale;
        posLogoX = leftPageMargin;
        posLogoY = pageHeight-topPageMargin;
        ImageBox logoImgBox = new ImageBox(logoImg, posLogoX, posLogoY, logoWidth, logoHeight, "logo");
        logoImgBox.build(stream,writer);

        // title top
        String docTitle = (rnd.nextBoolean() ? "Tax Invoice": "Invoice");
        SimpleTextBox docTitleBox = new SimpleTextBox(fontNB, 13, 0, 0, docTitle);
        docTitleBox.translate(pageMiddleX-docTitleBox.getBBox().getWidth()/2, pageHeight-topPageMargin);
        annot.setTitle(docTitle);
        docTitleBox.build(stream,writer);

        // top right invoice num+date info
        VerticalContainer invNumCont = new VerticalContainer(pageMiddleX-30, pageHeight-45, 250);
        HorizontalContainer numFact = new HorizontalContainer(0,0);
        numFact.addElement(new SimpleTextBox(fontN,9,0,0,model.getReference().getLabelInvoice()+" "));
        numFact.addElement(new SimpleTextBox(fontN,9,0,0, model.getReference().getValueInvoice(), "IN"));
        annot.getInvoice().setInvoiceId(model.getReference().getValueInvoice());
        numFact.addElement(new SimpleTextBox(fontN,9,0,0, " - "));
        numFact.addElement(new SimpleTextBox(fontN,9,0,0, model.getDate().getValueInvoice(), "IDATE"));
        annot.getInvoice().setInvoiceDate(model.getDate().getValueInvoice());
        invNumCont.addElement(numFact);
        invNumCont.build(stream,writer);

        float addresses_top = Math.min(logoImgBox.getBBox().getPosY()-10, invNumCont.getBBox().getPosY()-invNumCont.getBBox().getHeight()-10);

        // Vendor/Company Address
        VendorInfoBox vendorInfoBox = new VendorInfoBox(fontN,fontB,fontI,8,10,300,lineStrokeColor,model,annot,proba);
        vendorInfoBox.translate(leftPageMargin, addresses_top);
        vendorInfoBox.build(stream,writer);

        // top left invoice Purchase Order Number info
        VerticalContainer orderNumCont = new VerticalContainer(leftPageMargin, vendorInfoBox.getBBox().getPosY()-vendorInfoBox.getBBox().getHeight()-10, 250);

        HorizontalContainer orderHCont = new HorizontalContainer(0,0);
        orderHCont.addElement(new SimpleTextBox(fontN,9,0,0,model.getReference().getLabelOrder()+" "));
        orderHCont.addElement(new SimpleTextBox(fontN,9,0,0,model.getReference().getValueOrder()));
        annot.getInvoice().setInvoiceOrderId(model.getReference().getValueOrder());

        orderNumCont.addElement(orderHCont);
        orderNumCont.addElement(new SimpleTextBox(fontI,8,0,0,"Delivery by"+company.getName()));

        orderNumCont.build(stream,writer);

        // Billing Address
        float billX = vendorInfoBox.getBBox().getPosX()+vendorInfoBox.getBBox().getWidth()+30;
        float billY = addresses_top;
        BillingInfoBox billingInfoBox = new BillingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        billingInfoBox.translate(billX, billY);
        billingInfoBox.build(stream,writer);

        // Shipping Address
        float shipX = billX+billingInfoBox.getBBox().getWidth()+30;
        float shipY = addresses_top;
        ShippingInfoBox shippingInfoBox = new ShippingInfoBox(fontN,fontNB,fontI,9,9,250,lineStrokeColor,model,annot,proba);
        shippingInfoBox.translate(shipX, shipY);
        shippingInfoBox.build(stream,writer);

        // Product table
        float lowestY = Math.min(orderNumCont.getBBox().getPosY() - orderNumCont.getBBox().getHeight(),
                                 billingInfoBox.getBBox().getPosY() - billingInfoBox.getBBox().getHeight());
        lowestY = Math.min(lowestY, shippingInfoBox.getBBox().getPosY() - shippingInfoBox.getBBox().getHeight());

        float tableTopPosX = leftPageMargin;
        float tableTopPosY = lowestY - 10;
        float tableWidth = pageWidth-rightPageMargin-leftPageMargin;
        ProductTableBox productTableBox = new ProductTableBox(
                12, fontN, fontB, fontI, 8, 8, 600, lineStrokeColor, tableTopPosX, tableTopPosY, tableWidth, model, annot, proba);
        productTableBox.build(stream,writer);

        // table footer totals,taxes,discounts info
        float footerPosY = 270;
        VerticalContainer totalContainer = new VerticalContainer(388,footerPosY,250);

        HorizontalContainer baseHT = new HorizontalContainer(0,0);
        baseHT.addElement(new SimpleTextBox(fontB,9,0,0,model.getProductContainer().getTotalHead()+"  "));
        baseHT.addElement(new SimpleTextBox(fontN,9,0,0,model.getProductContainer().getFmtTotal(),"TWTX"));
        annot.getTotal().setSubtotalPrice(model.getProductContainer().getFmtTotal());

        HorizontalContainer taxeTVA = new HorizontalContainer(0,0);
        taxeTVA.addElement(new SimpleTextBox(fontB,9,0,0,model.getProductContainer().getTaxTotalHead()));
        taxeTVA.addElement(new SimpleTextBox(fontB,9,0,0," "+model.getProductContainer().getFmtTotalTaxRate()+"  "));
        taxeTVA.addElement(new SimpleTextBox(fontN,9,0,0,model.getProductContainer().getFmtTotalTax(),"TTX"));
        annot.getTotal().setTaxRate(model.getProductContainer().getFmtTotalTaxRate());
        annot.getTotal().setTaxPrice(model.getProductContainer().getFmtTotalTax());

        HorizontalContainer totalTTC = new HorizontalContainer(0,0);
        totalTTC.addElement(new SimpleTextBox(fontB,9,0,0,model.getProductContainer().getWithTaxAndDiscountTotalHead()+"  "));
        totalTTC.addElement(new SimpleTextBox(fontN,9,0,0,model.getProductContainer().getFmtTotalWithTaxAndDiscount(),"TA"));
        annot.getTotal().setTotalPrice(model.getProductContainer().getFmtTotalWithTaxAndDiscount());

        totalContainer.addElement(baseHT);
        totalContainer.addElement(new BorderBox(white,white,0,0,0,0,4));
        totalContainer.addElement(taxeTVA);
        totalContainer.addElement(new BorderBox(white,white,0,0,0,0,4));
        totalContainer.addElement(totalTTC);

        totalContainer.build(stream,writer);

        // payment info
        new SimpleTextBox(fontB,9,leftPageMargin,footerPosY+20,"Payment Terms and Date").build(stream,writer);

        VerticalContainer paymentLabels = new VerticalContainer(leftPageMargin,footerPosY,250);
        paymentLabels.addElement(new SimpleTextBox(fontN, 9,0,0,payment.getLabelPaymentType()+" : "));
        paymentLabels.addElement(new SimpleTextBox(fontN, 9,0,0,model.getDate().getLabelPaymentDue()+" : "));
        paymentLabels.build(stream,writer);

        VerticalContainer paymentValues = new VerticalContainer(leftPageMargin+paymentLabels.getBBox().getWidth()+20,footerPosY,250);
        paymentValues.addElement(new SimpleTextBox(fontI, 9,0,0,payment.getValuePaymentType(),"PMODE"));
        paymentValues.addElement(new SimpleTextBox(fontI, 9,0,0,model.getDate().getValuePaymentDue()));
        annot.getInvoice().setInvoiceDueDate(model.getDate().getValuePaymentDue());
        paymentValues.build(stream,writer);

        // Payment Info and Address
        if (proba.get("payment_address")) {
            float pAW = 350;
            float pAX = proba.get("signature_bottom_left") ? shipX: leftPageMargin;
            float pAY = productTableBox.getBBox().getPosY() - productTableBox.getBBox().getHeight() - 10;

            proba.put("vendor_tax_number_top", false);
            PaymentInfoBox paymentBox = new PaymentInfoBox(fontN,fontB,fontI,9,10,pAW,lineStrokeColor,model,annot,proba);
            paymentBox.translate(pAX, pAY);
            paymentBox.build(stream,writer);
        }

        // Add Signature at bottom
        if (proba.get("signature_bottom")) {
            String sigText = company.getSignature().getLabel()+" "+(company.getName().length() < 25 ? company.getName() : "");
            SimpleTextBox sigTextBox = new SimpleTextBox(fontN,8,0,0,sigText, "Signature");

            float sigTX;
            float sigTY = 120;
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

        // Add company stamp watermark
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
            stampBox.build(stream,writer);
        }
        // if no signature anåd no stamp, then add a footer note
        else if (!proba.get("signature_bottom")) {
            String noStampMsg = "*This document is computer generated and does not require a signature or \nthe Company's stamp in order to be considered valid";
            new SimpleTextBox(fontN, 7, 20, 130, noStampMsg, "footnote").build(stream,writer);
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
            FootCompanyBox footCompanyBox = new FootCompanyBox(fontN,fontB,fontI,fSize,fSize+1,themeColor, pageWidth-leftPageMargin-rightPageMargin,model,annot,proba);
            float fW = footCompanyBox.getBBox().getWidth();
            footCompanyBox.alignElements(HAlign.CENTER, fW);
            footCompanyBox.translate(pageMiddleX-fW/2,82);
            footCompanyBox.build(stream,writer);
        }

        stream.close();
        writer.writeEndElement();
    }
}
