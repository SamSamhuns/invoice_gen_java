package com.fairandsmart.generator.documents.element;

import com.fairandsmart.generator.documents.InvoiceGenerator;
import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.util.Arrays;
import java.util.List;


@RunWith(JUnit4.class)
public class TestSimpleTextBoxFontRender implements InvoiceLayout {

    @Override
    public String name() {
        return "TestSimpleTextBoxFontRender";
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

        PDPageContentStream stream = new PDPageContentStream(document, page);

        int currentPosY = 750;
        stream.moveTo( 20, 750);
        stream.lineTo( 400, 750);
        stream.stroke();

        final List<List<String>> ttfFontNormalBoldItalicList = Arrays.asList(
                Arrays.asList("Arial.ttf", "Arial Bold.ttf", "Arial Italic.ttf"),
                Arrays.asList("Baskerville.ttf", "Baskerville Bold.ttf", "Baskerville Italic.ttf"),
                Arrays.asList("Century Gothic.ttf", "Century Gothic Bold.ttf", "Century Gothic Italic.ttf"),
                Arrays.asList("Futura.ttf", "Futura Bold.ttf", "Futura Italic.ttf"),
                Arrays.asList("Georgia.ttf", "Georgia Bold.ttf", "Georgia Italic.ttf"),
                Arrays.asList("Optima.ttf", "Optima Bold.ttf", "Optima Italic.ttf"),
                Arrays.asList("Palatino.ttf", "Palatino Bold.ttf", "Palatino Italic.ttf"),
                Arrays.asList("Univers.ttf", "UniversBlack.ttf", "Univers Italic.ttf"),
                Arrays.asList("Verdana.ttf", "Verdana Bold.ttf", "Verdana Italic.ttf"),
                Arrays.asList("bookman old style.ttf", "bookman old style Bold.ttf", "bookman old style Italic.ttf")
        );

        SimpleTextBox stb;
        for (List<String> fontNBI : ttfFontNormalBoldItalicList) {
            PDFont NFont = PDType0Font.load(document, new File(HelperCommon.getResourceFullPath(this, "common/font/" + fontNBI.get(0))));
            PDFont BFont = PDType0Font.load(document, new File(HelperCommon.getResourceFullPath(this, "common/font/" + fontNBI.get(1))));
            PDFont IFont = PDType0Font.load(document, new File(HelperCommon.getResourceFullPath(this, "common/font/" + fontNBI.get(2))));

            stb = new SimpleTextBox(NFont, 12, 20, currentPosY, "Normal: "+fontNBI.get(0));
            stb.build(stream,writer);
            currentPosY -= stb.getBBox().getHeight();
            stb = new SimpleTextBox(BFont, 12, 20, currentPosY, "Bold: "+fontNBI.get(1));
            stb.build(stream,writer);
            currentPosY -= stb.getBBox().getHeight();
            stb = new SimpleTextBox(IFont, 12, 20, currentPosY, "Italic: "+fontNBI.get(2));
            stb.build(stream,writer);
            currentPosY -= stb.getBBox().getHeight();
        }

        String all_currencies = "€, $, ¥, د.إ";
        String ar_currency = "إ.د";

        String fontNonANSI = "A_Nefel_Sereke.ttf";
        HelperCommon.PDCustomFonts nonAFont = HelperCommon.getNonANSIRandomPDFontFamily(document, this);
        PDFont NFont = nonAFont.getFontNormal();
        PDFont BFont = nonAFont.getFontBold();
        PDFont IFont = nonAFont.getFontItalic();

        stb = new SimpleTextBox(NFont, 12, 20, currentPosY, "N Arabic: "+fontNonANSI+" "+ar_currency+all_currencies, "undefined", false);
        stb.build(stream,writer);
        currentPosY -= stb.getBBox().getHeight();

        stb = new SimpleTextBox(BFont, 12, 20, currentPosY, "B Arabic: "+fontNonANSI+" "+ar_currency+all_currencies, "undefined", false);
        stb.build(stream,writer);
        currentPosY -= stb.getBBox().getHeight();

        stb = new SimpleTextBox(IFont, 12, 20, currentPosY, "I Arabic: "+fontNonANSI+" "+ar_currency+all_currencies, "undefined", false);
        stb.build(stream,writer);

        stream.close();
        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {
        Path dir = Paths.get("target/textbox-font");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/textbox-font/textbox.pdf");
        Path xml = Paths.get("target/textbox-font/textbox.xml");
        Path img = Paths.get("target/textbox-font/textbox.jpg");
        Path json = Paths.get("target/textbox-font/textbox.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }
}
