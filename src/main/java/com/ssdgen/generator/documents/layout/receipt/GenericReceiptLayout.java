package com.ssdgen.generator.documents.layout.receipt;

import com.ssdgen.generator.documents.data.model.Model;
import com.ssdgen.generator.documents.data.model.ReceiptModel;
import com.ssdgen.generator.documents.element.HAlign;
import com.ssdgen.generator.documents.element.border.BorderBox;
import com.ssdgen.generator.documents.element.container.HorizontalContainer;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import com.ssdgen.generator.documents.element.head.CompanyInfoBoxPayslip;
import com.ssdgen.generator.documents.element.image.ImageBox;
import com.ssdgen.generator.documents.element.product.ReceiptGSTSammury;
import com.ssdgen.generator.documents.element.product.ReceiptProductBox;
import com.ssdgen.generator.documents.element.table.TableRowBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBoxForEvaluation;
import com.ssdgen.generator.documents.layout.SSDLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class GenericReceiptLayout implements SSDLayout {
    private final float fontSize = 9.5f;
    private PDFont[] fonts;
    ReceiptModel model;
    private boolean timeAvailable;
    private int receiptNumberPos;
    private boolean logo_available;
    private boolean referenceWithName;
    private boolean faxAvailable;
    private int datePos;
    private int titlePos;
    private boolean timeTwice;
    private boolean receiptNumberAvailable;
    private boolean printedDateAvailable;
    private boolean NameAvailable;
    private boolean orderNumAvailable;
    private boolean cashierAvailable;
    private boolean billToAddrAvailable;
    private boolean refAvailable;
    private boolean receiptInfoOneColumn;
    private boolean sumUpAvailable;
    private boolean companyInfoAvailable;
    private int pos_element;


    @Override
    public String name() {
        return "Generic Receipt";
    }
    private static final List<PDFont[]> FONTS = new ArrayList<>();
    {
        FONTS.add(new PDFont[] {PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA_OBLIQUE} );
        FONTS.add(new PDFont[] {PDType1Font.COURIER, PDType1Font.COURIER_BOLD, PDType1Font.COURIER_OBLIQUE} );
        FONTS.add(new PDFont[] {PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD, PDType1Font.TIMES_ITALIC} );
    }

    @Override
    public void builtSSD(Model model, PDDocument document, XMLStreamWriter writer, XMLStreamWriter writerEval) throws Exception {
        this.model = (ReceiptModel)model;
        this.timeAvailable = model.getRandom().nextBoolean();
        this.receiptNumberPos = model.getRandom().nextInt(4);
        this.datePos = model.getRandom().nextInt(4);
        this.titlePos = model.getRandom().nextInt(4);
        this.timeTwice = model.getRandom().nextBoolean();
        this.logo_available = model.getRandom().nextBoolean();
        this.referenceWithName = model.getRandom().nextBoolean();
        this.faxAvailable = model.getRandom().nextBoolean();
        this.receiptNumberAvailable = model.getRandom().nextBoolean();
        this. printedDateAvailable = model.getRandom().nextBoolean();
        this.NameAvailable = model.getRandom().nextBoolean();
        this.orderNumAvailable = model.getRandom().nextBoolean();
        this.cashierAvailable = model.getRandom().nextBoolean();
        this.billToAddrAvailable = model.getRandom().nextBoolean();
        this.refAvailable = model.getRandom().nextBoolean();
        this.receiptInfoOneColumn = model.getRandom().nextBoolean();
        this.sumUpAvailable = model.getRandom().nextBoolean();
        this.companyInfoAvailable = model.getRandom().nextBoolean();
        this.pos_element =0;
        Boolean modeEval = true;
        if(writerEval== null) {
            modeEval = false;
        }

        // sets of table row possible sizes
        float[] configRow1v1 = {300f};//500f};
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        writer.writeStartElement("DL_PAGE");
        writer.writeAttribute("gedi_type", "DL_PAGE");
        writer.writeAttribute("pageID", "1");
        //
        if(modeEval) {
            writerEval.writeStartElement("DL_PAGE");
            writerEval.writeAttribute("gedi_type", "DL_PAGE");
            writerEval.writeAttribute("pageID", "1");
            writerEval.writeCharacters(System.getProperty("line.separator"));
        }
        writer.writeAttribute("width", "2480");
        writer.writeAttribute("height", "3508"); // 3508
        writer.writeCharacters(System.getProperty("line.separator"));
        PDPageContentStream stream = new PDPageContentStream(document, page);
        PDFont font = PDType1Font.HELVETICA_BOLD;
        this.fonts = FONTS.get(model.getRandom().nextInt(FONTS.size()));
        VerticalContainer receiptPage = new VerticalContainer(0,0,0);
        //payslip parts
        TableRowBox firstPart = null, secondPart = null,  sumup = null, thirdPart = null, fourthPart= null, fifthPart= null, sixthPart= null;
        //companyInfo
        CompanyInfoBoxPayslip companyInfoBox = new CompanyInfoBoxPayslip(fonts[2], fonts[1], fontSize, model, document);
        ImageBox companyLogo =  companyInfoBox.getLogoBox(14, Color.WHITE); // 42
        // Date and Time horizontal
        HorizontalContainer b03 = new HorizontalContainer(0,0);
        b03.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getValue(),Color.BLACK, null, HAlign.CENTER, "IDATE" ));
        if(this.timeAvailable){
            b03.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            b03.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getTime(),Color.BLACK, null, HAlign.CENTER ));
        }
        // Title and date
        if(model.getRandom().nextBoolean()) font = fonts[1];
        else font = fonts[0];
        SimpleTextBox title = new SimpleTextBox(font, fontSize, 0, 0, this.model.getHeadTitle());//,Color.BLACK, null, HAlign.CENTER);
        if(model.getRandom().nextBoolean()) font = fonts[1];
        else font = fonts[0];
        SimpleTextBox companyName = new SimpleTextBox(font, fontSize, 0, 0, model.getCompany().getName().toUpperCase(), Color.BLACK, null, HAlign.CENTER, "SN");
        SimpleTextBox emptyBox= new SimpleTextBox(fonts[0], fontSize, 0, 0, "", Color.BLACK, null, HAlign.CENTER);
        SimpleTextBox companyAddressline1 = new SimpleTextBox(fonts[0], fontSize, 0, 0, model.getCompany().getAddress().getLine1(), Color.BLACK, null, HAlign.CENTER, "SA");
        SimpleTextBox companyAddressline2 = new SimpleTextBox(fonts[0], fontSize, 0, 0, model.getCompany().getAddress().getZip() + " " +model.getCompany().getAddress().getCity(), Color.BLACK, null, HAlign.CENTER, "SA");
        SimpleTextBox companyAddressline3 = new SimpleTextBox(fonts[0], fontSize, 0, 0, model.getCompany().getAddress().getCountry(), Color.BLACK, null, HAlign.CENTER, "SA");
        SimpleTextBox phone = new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getContact().getPhoneLabel()+" "+model.getCompany().getContact().getPhoneValue(), Color.BLACK, null, HAlign.CENTER);//, "SCN");
        SimpleTextBox fax = new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getContact().getFaxLabel()+"  "+model.getCompany().getContact().getFaxValue());//,"SFAX");
        SimpleTextBox reference = new SimpleTextBox(fonts[0], 9, 0, 0, "(" +this.model.getReference().getValueOrder() +")",Color.BLACK, null, HAlign.CENTER);//,"ONUM");
        firstPart = new TableRowBox(configRow1v1, 0, 0);
        HorizontalContainer b0 = new HorizontalContainer(0,0);
        if(logo_available){
            b0.addElement(companyLogo);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("logo", pos_element).build(writerEval);
            }
            b0.addElement(companyName);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation(companyName.getEntityName(), pos_element).build(writerEval);
            }
        }else {
            b0.addElement(companyName);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation(companyName.getEntityName(), pos_element).build(writerEval);
            }
            if(referenceWithName){
                b0.addElement(reference);
                if(modeEval) {
                    pos_element++;
                    new SimpleTextBoxForEvaluation("orderNumber", pos_element).build(writerEval);
                }
            }
        }
        VerticalContainer a = new VerticalContainer(0,0,300f);
        a.addElement(b0);
        if(!referenceWithName){
            a.addElement(reference);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("orderNumber", pos_element).build(writerEval);
            }
        }
        a.alignElements(HAlign.CENTER,300f);
        HorizontalContainer b = new HorizontalContainer(0,0);
        b.addElement(a);
        firstPart.addElement(a,true);
        receiptPage.addElement(firstPart);
        secondPart = new TableRowBox(configRow1v1, 0, 0);
        VerticalContainer a1 = new VerticalContainer(0,0,300f);
        if (datePos==0) {
            a1.addElement(b03);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("date", pos_element).build(writerEval);
                if (this.timeAvailable) {
                    pos_element++;
                    new SimpleTextBoxForEvaluation("date", pos_element).build(writerEval);
                }
            }
        }
        a1.addElement(companyAddressline1);
        a1.addElement(companyAddressline2);
        a1.addElement(companyAddressline3);
        a1.alignElements(HAlign.CENTER,300f);
        HorizontalContainer b1 = new HorizontalContainer(0,0);
        b1.addElement(a1);
        secondPart.addElement(b1,true);
        if(modeEval) {
            pos_element++;
            new SimpleTextBoxForEvaluation("address", pos_element).build(writerEval);
        }
        receiptPage.addElement(secondPart);
        thirdPart = new TableRowBox(configRow1v1, 0, 0);
        VerticalContainer a2 = new VerticalContainer(0,0,150f);
        a2.addElement(phone);
        HorizontalContainer b2 = new HorizontalContainer(0,0);
        b2.addElement(a2);
        if(modeEval) {
            pos_element++;
            new SimpleTextBoxForEvaluation("phone", pos_element).build(writerEval);
        }
        if (faxAvailable) {
            VerticalContainer a3 = new VerticalContainer(0, 0, 150f);
            a3.addElement(fax);
            b2.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            b2.addElement(a3);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("fax", pos_element).build(writerEval);
            }
        }
        VerticalContainer a4 = new VerticalContainer(0,0,300f);
        a4.addElement(b2);
        a4.alignElements(HAlign.CENTER,300f);
        thirdPart.addElement(a4, true);
        receiptPage.addElement(thirdPart);
        fourthPart = new TableRowBox(configRow1v1, 0, 0);
        VerticalContainer titleVC= new VerticalContainer(0,0,0);
        title.setHalign(HAlign.CENTER);
        titleVC.addElement(title);
        titleVC.setHeight(150);
        fourthPart.addElement(titleVC,true);
        if (titlePos==0) {
            receiptPage.addElement(fourthPart);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("title", pos_element).build(writerEval);
            }
        }
        fifthPart = new TableRowBox(configRow1v1, 0, 0);
        if (datePos==1){
            ArrayList<HorizontalContainer> infolist = new ArrayList<HorizontalContainer>();
            ArrayList<String> infolistForEval = new ArrayList<>();
            if(receiptInfoOneColumn){
                HorizontalContainer dateH= new HorizontalContainer(0,0);
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getLabel() + " : ",Color.BLACK, null, HAlign.CENTER ));
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getValue(),Color.BLACK, null, HAlign.CENTER, "IDATE" ));
                if(this.timeAvailable){
                    dateH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
                    dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getTime(),Color.BLACK, null, HAlign.CENTER));
                }
                infolist.add(dateH);
                infolistForEval.add("date");
            }else {
                HorizontalContainer dateH= new HorizontalContainer(0,0);
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getLabel() + " : ",Color.BLACK, null, HAlign.CENTER ));
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getValue(),Color.BLACK, null, HAlign.CENTER, "IDATE" ));
                infolistForEval.add("date");
                infolist.add(dateH);
                if(this.timeAvailable){
                    HorizontalContainer timeH= new HorizontalContainer(0,0);
                    timeH.addElement(new SimpleTextBox(font, 9, 0, 0, this.model.getDate().getTimeLabel() + " : ",Color.BLACK, null, HAlign.CENTER));
                    timeH.addElement(new SimpleTextBox(font, 9, 0, 0, this.model.getDate().getTime(),Color.BLACK, null, HAlign.CENTER ));
                    infolist.add(timeH);
                    infolistForEval.add("time");
                }
            }
            if (receiptNumberAvailable){
                HorizontalContainer recNumH= new HorizontalContainer(0,0);
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getLabelInvoice() +" : ",Color.BLACK, null, HAlign.CENTER ));
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getValueInvoice(),Color.BLACK, null, HAlign.CENTER ));
                infolist.add(recNumH);
                infolistForEval.add("reference");
            }
            if(orderNumAvailable){
                HorizontalContainer recNumH= new HorizontalContainer(0,0);
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getLabelOrder() +" : ",Color.BLACK, null, HAlign.CENTER ));
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getValueOrder(),Color.BLACK, null, HAlign.CENTER ));
                infolist.add(recNumH);
                infolistForEval.add("orderNumber");
            }
            if(refAvailable){
                HorizontalContainer recNumH= new HorizontalContainer(0,0);
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getLabelClient() +" : ",Color.BLACK, null, HAlign.CENTER ));
                recNumH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getValueClient(),Color.BLACK, null, HAlign.CENTER ));
                infolist.add(recNumH);
                infolistForEval.add("clientRef");
            }
            if(printedDateAvailable){
                HorizontalContainer dateH= new HorizontalContainer(0,0);
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getprintedDateLabel()+" : ",Color.BLACK, null, HAlign.CENTER));
                dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getValue(),Color.BLACK, null, HAlign.CENTER, "IDATE" ));
                infolist.add(dateH);
                infolistForEval.add("printedDate");
            }
            if(cashierAvailable){
                HorizontalContainer cashH= new HorizontalContainer(0,0);
                cashH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getCashierLabel() +" : ",Color.BLACK, null, HAlign.CENTER ));
                cashH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getCashierLabel()+model.getRandom().nextInt(6),Color.BLACK, null, HAlign.CENTER ));
                infolist.add(cashH);
                infolistForEval.add("cashier");
            }
            long seed = System.nanoTime();
            Collections.shuffle(infolist, new Random(seed));
            Collections.shuffle(infolistForEval, new Random(seed));
            /// find date if if
            if(receiptInfoOneColumn && this.timeAvailable){
                int index = infolistForEval.indexOf("date");
                infolistForEval.add(index+1,"time");
            }
            VerticalContainer infoBlock = new VerticalContainer(0,0,300f);
            HorizontalContainer infoBlockHF = new HorizontalContainer(0,0);
            if(receiptInfoOneColumn){
                for (int i=0;i<infolist.size();i++){
                    infoBlock.addElement(infolist.get(i));
                }
                infoBlock.alignElements(HAlign.LEFT,300f);
                infoBlockHF.addElement(infoBlock);
            }else {
                VerticalContainer infoBlockR = new VerticalContainer(0,0,150f);
                VerticalContainer infoBlockL = new VerticalContainer(0,0,150f);
                HorizontalContainer infoBlockH = new HorizontalContainer(0,0);
                for (int i=0;i<(infolist.size()/2);i++){
                    infoBlockL.addElement(infolist.get(i));
                }
                for (int i=(infolist.size()/2)+1;i<infolist.size();i++){
                    infoBlockR.addElement(infolist.get(i));
                }
                infoBlockH.addElement(infoBlockL);
                infoBlockH.addElement(infoBlockR);
                infoBlock.addElement(infoBlockH);
                infoBlock.alignElements(HAlign.CENTER,300f);
                infoBlockHF.addElement(infoBlock);
            }
            if(modeEval) {
                for (int i = 0; i < infolistForEval.size(); i++) {
                    pos_element++;
                    new SimpleTextBoxForEvaluation(infolistForEval.get(i), pos_element).build(writerEval);
                }
            }
            fifthPart.addElement(infoBlockHF,true);
            receiptPage.addElement(fifthPart);
        }
        if (titlePos==1) {
            receiptPage.addElement(fourthPart);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("title", pos_element).build(writerEval);
            }
        }
        ReceiptProductBox receiptProductTableBox = new ReceiptProductBox(0, 0, this.model.getProductReceiptContainer(),fonts[2], fonts[1], fontSize);
        receiptPage.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 0, 10));
        receiptPage.addElement(receiptProductTableBox);
        if(modeEval) {
            for (int i = 0; i < receiptProductTableBox.getChosenFormatHeaders().length; i++) {
                pos_element++;
                new SimpleTextBoxForEvaluation(receiptProductTableBox.getChosenFormatHeaders()[i], pos_element).build(writerEval);
            }
        }
        sumup = new TableRowBox(configRow1v1, 0, 0);
        VerticalContainer a6 = new VerticalContainer(0,0,300f);
        if(this.model.getProductReceiptContainer().getTotaltaxAvailable()) {
            HorizontalContainer totalHT = new HorizontalContainer(0, 0);
            totalHT.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getTotalHead(), Color.BLACK, null, HAlign.LEFT));
            totalHT.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            totalHT.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getFmtTotal(), Color.BLACK, null, HAlign.CENTER));//, "TWTX"));
            a6.addElement(totalHT);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("totalHT", pos_element).build(writerEval);
            }
            HorizontalContainer totalTax = new HorizontalContainer(0, 0);
            totalTax.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getTaxTotalHead(), Color.BLACK, null, HAlign.LEFT));
            totalTax.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            totalTax.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getFmtTotalTax(), Color.BLACK, null, HAlign.CENTER));//, "TTX"));
            //totalTax.setHeight(5);
            a6.addElement(totalTax);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("totalTax", pos_element).build(writerEval);
            }
        }
        HorizontalContainer totalTTC = new HorizontalContainer(0,0);
        totalTTC.addElement(new SimpleTextBox(fonts[0], fontSize+1, 0, 0, this.model.getProductReceiptContainer().getWithTaxTotalHead(), Color.BLACK, null, HAlign.LEFT));
        totalTTC.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
        if(this.model.getProductReceiptContainer().getRoundAvailable()) {
            totalTTC.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getFmtTotalWithTax(), Color.BLACK, null, HAlign.CENTER));
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("rounded", pos_element).build(writerEval);
            }
        }
        else {
            totalTTC.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getFmtTotalWithTax(), Color.BLACK, null, HAlign.CENTER, "TA"));
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("TA", pos_element).build(writerEval);
            }
        }
        a6.addElement(totalTTC);
        if(this.model.getProductReceiptContainer().getDiscountAvailable()) {
            HorizontalContainer totalDiscount = new HorizontalContainer(0, 0);
            totalDiscount.addElement(new SimpleTextBox(fonts[0], fontSize + 1, 0, 0, this.model.getProductReceiptContainer().getDiscountHead(), Color.BLACK, null, HAlign.LEFT));
            totalDiscount.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            totalDiscount.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getTotalDiscount(), Color.BLACK, null, HAlign.CENTER));
            a6.addElement(totalDiscount);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("discount", pos_element).build(writerEval);
            }
        }
        if(this.model.getProductReceiptContainer().getRoundAvailable()) {
            HorizontalContainer totalRounding = new HorizontalContainer(0, 0);
            totalRounding.addElement(new SimpleTextBox(fonts[0], fontSize + 1, 0, 0, this.model.getProductReceiptContainer().getRoundingHead(), Color.BLACK, null, HAlign.LEFT));
            totalRounding.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            totalRounding.addElement(new SimpleTextBox(fonts[0], fontSize, 0, 0, this.model.getProductReceiptContainer().getTotalRounding(), Color.BLACK, null, HAlign.CENTER));
            a6.addElement(totalRounding);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("rounding", pos_element).build(writerEval);
            }
            if(model.getRandom().nextBoolean()) font = fonts[1];
            else font = fonts[0];
            HorizontalContainer totalRounded = new HorizontalContainer(0, 0);
            totalRounded.addElement(new SimpleTextBox(font, fontSize + 1, 0, 0, this.model.getProductReceiptContainer().getRoundedHead(), Color.BLACK, null, HAlign.LEFT));
            totalRounded.addElement(new BorderBox(Color.WHITE, Color.WHITE, 0, 0, 0, 5, 0));
            totalRounded.addElement(new SimpleTextBox(font, fontSize, 0, 0, this.model.getProductReceiptContainer().getTotalRounded(), Color.BLACK, null, HAlign.CENTER, "TA"));
            a6.addElement(totalRounded);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("TA", pos_element).build(writerEval);
            }
        }
        if(model.getRandom().nextBoolean()) font = fonts[1];
        else font = fonts[0];
        HorizontalContainer cash = new HorizontalContainer(0,0);
        cash.addElement(new SimpleTextBox(font, fontSize+1, 0, 0, this.model.getProductReceiptContainer().getCashHead(), Color.BLACK, null, HAlign.LEFT));
        cash.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
        cash.addElement(new SimpleTextBox(font, fontSize, 0, 0, this.model.getProductReceiptContainer().getCash(), Color.BLACK, null, HAlign.CENTER ));
        a6.addElement(cash);
        if(modeEval) {
            pos_element++;
            new SimpleTextBoxForEvaluation("cash", pos_element).build(writerEval);
        }
        if(model.getRandom().nextBoolean()) font = fonts[1];
        else font = fonts[0];
        HorizontalContainer change = new HorizontalContainer(0,0);
        change.addElement(new SimpleTextBox(font, fontSize+1, 0, 0, this.model.getProductReceiptContainer().getChangeHead(), Color.BLACK, null, HAlign.LEFT));
        change.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
        change.addElement(new SimpleTextBox(font, fontSize, 0, 0, this.model.getProductReceiptContainer().getChange(), Color.BLACK, null, HAlign.CENTER));
        a6.addElement(change);
        if(modeEval) {
            pos_element++;
            new SimpleTextBoxForEvaluation("change", pos_element).build(writerEval);
        }
        HorizontalContainer hElmt = new HorizontalContainer(0,0);
        a6.alignElements(HAlign.RIGHT,300f);
        hElmt.addElement(a6);
        hElmt.setHeight(hElmt.getBBox().getHeight()+30);
        sumup.addElement(hElmt,true);
        receiptPage.addElement(sumup);
        if(sumUpAvailable) {
            VerticalContainer GST = new VerticalContainer(0, 0, 0);
            ReceiptGSTSammury GSTTable = new ReceiptGSTSammury(0, 0, this.model.getProductReceiptContainer(), fonts[2], fonts[1], fontSize);
            GST.addElement(GSTTable);
            receiptPage.addElement(GSTTable);
            if(modeEval) {
                for (int i = 0; i < GSTTable.getChosenFormatHeaderss().length; i++) {
                    pos_element++;
                    new SimpleTextBoxForEvaluation(GSTTable.getChosenFormatHeaderss()[i], pos_element).build(writerEval);
                }
            }
        }
        if(datePos==2 && timeTwice){
            VerticalContainer dateTime = new VerticalContainer(0, 0, 0);
            HorizontalContainer dateH= new HorizontalContainer(0,0);
            dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getValue(),Color.BLACK, null, HAlign.CENTER, "IDATE" ));
            dateH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getDate().getTime(),Color.BLACK, null, HAlign.CENTER ));
            dateH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            dateH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, this.model.getReference().getValueOrder(),Color.BLACK, null, HAlign.CENTER ));
            dateTime.addElement(dateH);
            dateTime.alignElements(HAlign.LEFT,300);
            receiptPage.addElement(dateTime);
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("date", pos_element).build(writerEval);
                pos_element++;
                new SimpleTextBoxForEvaluation("time", pos_element).build(writerEval);
                pos_element++;
                new SimpleTextBoxForEvaluation("orderNumber", pos_element).build(writerEval);
            }
        }
        if(companyInfoAvailable){
            TableRowBox companyInfoPart = new TableRowBox(configRow1v1, 0, 0);
            VerticalContainer companyInfoV = new VerticalContainer(0, 0, 0);
            HorizontalContainer companyInfoH= new HorizontalContainer(0,0);
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getSiretLabel(),Color.BLACK, null, HAlign.CENTER ));
            companyInfoH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getSiretValue(),Color.BLACK, null, HAlign.CENTER));
            companyInfoH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getVatLabel(),Color.BLACK, null, HAlign.CENTER ));
            companyInfoH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getVatValue(),Color.BLACK, null, HAlign.CENTER ));
            companyInfoH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getToaLabel(),Color.BLACK, null, HAlign.CENTER ));
            companyInfoH.addElement(new BorderBox(Color.WHITE,Color.WHITE, 0,0, 0, 5, 0));
            companyInfoH.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, model.getCompany().getIdNumbers().getToaValue(),Color.BLACK, null, HAlign.CENTER ));
            if(modeEval) {
                pos_element++;
                new SimpleTextBoxForEvaluation("siret", pos_element).build(writerEval);
                pos_element++;
                new SimpleTextBoxForEvaluation("vat", pos_element).build(writerEval);
                pos_element++;
                new SimpleTextBoxForEvaluation("toa", pos_element).build(writerEval);
            }
            companyInfoV.addElement(companyInfoH);
            companyInfoV.alignElements(HAlign.CENTER,300);
            companyInfoPart.addElement(companyInfoV,true);
            receiptPage.addElement(companyInfoPart);
        }
        sixthPart = new TableRowBox(configRow1v1, 0, 0);
        List<String> footnotes = this.model.getFootnotes();
        VerticalContainer foot = new VerticalContainer(0,0,0);
        for(int i=0;i<footnotes.size();i++){
            HorizontalContainer hCon = new HorizontalContainer(0,0);
            hCon.addElement(new SimpleTextBox(fonts[0], 9, 0, 0, footnotes.get(i), Color.BLACK, null, HAlign.CENTER));
            foot.addElement(hCon);
        }
        foot.alignElements(HAlign.CENTER,300f);
        if(modeEval) {
            pos_element++;
            new SimpleTextBoxForEvaluation("footnote", pos_element).build(writerEval);
        }
        sixthPart.addElement(foot,true);
        receiptPage.addElement(sixthPart);
        receiptPage.translate(20,785); //830
        receiptPage.build(stream,writer);
        stream.close();
        writer.writeEndElement();
        if(modeEval) {
            writerEval.writeEndElement();
        }
    }
}
