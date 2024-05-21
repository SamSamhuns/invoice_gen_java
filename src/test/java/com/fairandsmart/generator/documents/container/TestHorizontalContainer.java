package com.fairandsmart.generator.documents.container;

import com.fairandsmart.generator.documents.InvoiceGenerator;
import com.fairandsmart.generator.documents.element.border.BorderBox;
import com.fairandsmart.generator.documents.element.container.HorizontalContainer;
import com.fairandsmart.generator.documents.element.textbox.SimpleTextBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class TestHorizontalContainer implements InvoiceLayout {

    @Override
    public String name() {
        return "TestHorizontal";
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


        new BorderBox(Color.RED, Color.WHITE, 15, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight()).build(stream,writer);

        HorizontalContainer container = new HorizontalContainer(50,700);
        container.setBackgroundColor(Color.GRAY);

        PDFont font = PDType1Font.HELVETICA_BOLD;
        SimpleTextBox stb = new SimpleTextBox(font, 12, 0, 0, "Simple title of text");
        stb.setBackgroundColor(Color.ORANGE);
        container.addElement(stb);
        font = PDType1Font.HELVETICA;
        SimpleTextBox stb2 = new SimpleTextBox(font, 9, 0, 0, "pretty subtitle");
        stb2.setBackgroundColor(Color.RED);
        container.addElement(stb2);
        SimpleTextBox stb3 = new SimpleTextBox(font, 9, 0, 0, "line3");
        stb3.setBackgroundColor(Color.YELLOW);
        container.addElement(stb3);
        container.build(stream,writer);


        stream.close();

        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {

        Path dir = Paths.get("target/texthorizontal");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/texthorizontal/texthorizontal.pdf");
        Path xml = Paths.get("target/texthorizontal/texthorizontal.xml");
        Path img = Paths.get("target/texthorizontal/texthorizontal.jpg");
        Path json = Paths.get("target/texthorizontal/texthorizontal.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }

}
