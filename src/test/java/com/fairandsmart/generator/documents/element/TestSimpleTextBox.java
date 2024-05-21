package com.ssdgen.generator.documents.element;

import com.ssdgen.generator.documents.InvoiceGenerator;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.layout.InvoiceLayout;

import com.ssdgen.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


@RunWith(JUnit4.class)
public class TestSimpleTextBox implements InvoiceLayout {

    @Override
    public String name() {
        return "TestSimpleTextBox";
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

        PDFont font = PDType1Font.HELVETICA_BOLD;

        stream.moveTo( 20, 750);
        stream.lineTo( 400, 750);
        stream.stroke();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb = new SimpleTextBox(font, 12, 20, currentPosY, "Simple title of text");
        stb.setBackgroundColor(Color.ORANGE);
        stb.build(stream,writer);
        currentPosY -= stb.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb2 = new SimpleTextBox(font, 12, 20, currentPosY, "Simple title of text which is a little bit longer without max width");
        stb2.setBackgroundColor(Color.CYAN);
        stb2.build(stream,writer);
        currentPosY -= stb2.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb3 = new SimpleTextBox(font, 12, 60, currentPosY, "Simple title of text which is a little bit longer but WITH max width");
        stb3.setWidth(100);
        stb3.setBackgroundColor(Color.PINK);
        stb3.build(stream,writer);
        currentPosY -= stb3.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb4 = new SimpleTextBox(font, 12, 20, currentPosY, "Simple text with padding");
        stb4.setBackgroundColor(Color.YELLOW);
        stb4.setPadding(20, 10, 75, 50);
        stb4.build(stream,writer);
        currentPosY -= stb4.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb5 = new SimpleTextBox(font, 12, 20, currentPosY, "Align Right");
        stb5.setBackgroundColor(Color.LIGHT_GRAY);
        stb5.setWidth(150);
        stb5.setHalign(HAlign.RIGHT);
        stb5.build(stream,writer);
        currentPosY -= stb5.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb6 = new SimpleTextBox(font, 12, 20, currentPosY, "Align right with padding");
        stb6.setBackgroundColor(Color.ORANGE);
        stb6.setPadding(10, 0, 10, 0);
        stb6.setWidth(200);
        stb6.setHalign(HAlign.RIGHT);
        stb6.build(stream,writer);
        currentPosY -= stb6.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb7 = new SimpleTextBox(font, 12, 20, currentPosY, "Align center");
        stb7.setBackgroundColor(Color.LIGHT_GRAY);
        stb7.setWidth(150);
        stb7.setHalign(HAlign.CENTER);
        stb7.build(stream,writer);
        currentPosY -= stb7.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb8 = new SimpleTextBox(font, 12, 20, currentPosY, "Align center padding asym");
        stb8.setBackgroundColor(Color.ORANGE);
        stb8.setPadding(10, 0, 50, 0);
        stb8.setWidth(250);
        stb8.setHalign(HAlign.CENTER);
        stb8.build(stream,writer);
        currentPosY -= stb8.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb9 = new SimpleTextBox(font, 12, 20, currentPosY, "Align right multi line of text should be placed");
        stb9.setBackgroundColor(Color.LIGHT_GRAY);
        stb9.setWidth(100);
        stb9.setHalign(HAlign.RIGHT);
        stb9.build(stream,writer);
        currentPosY -= stb9.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb10 = new SimpleTextBox(font, 12, 20, currentPosY, "Align center multi line of text should be placed");
        stb10.setBackgroundColor(Color.YELLOW);
        stb10.setWidth(100);
        stb10.setHalign(HAlign.CENTER);
        stb10.build(stream,writer);
        currentPosY -= stb10.getBBox().getHeight();

        // System.out.println("posY: " + currentPosY);
        SimpleTextBox stb11 = new SimpleTextBox(font, 12, 20, currentPosY, "Align center multi line of text with padding");
        stb11.setBackgroundColor(Color.LIGHT_GRAY);
        stb11.setPadding(10, 20, 20, 20);
        stb11.setWidth(150);
        stb11.setHalign(HAlign.CENTER);
        stb11.build(stream,writer);

        stream.close();

        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {
        Path dir = Paths.get("target/textbox");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/textbox/textbox.pdf");
        Path xml = Paths.get("target/textbox/textbox.xml");
        Path img = Paths.get("target/textbox/textbox.jpg");
        Path json = Paths.get("target/textbox/textbox.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }
}
