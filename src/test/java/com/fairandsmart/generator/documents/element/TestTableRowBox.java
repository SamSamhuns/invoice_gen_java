package com.ssdgen.generator.documents.element;

import com.ssdgen.generator.documents.InvoiceGenerator;
import com.ssdgen.generator.documents.layout.InvoiceLayout;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.element.container.VerticalContainer;
import com.ssdgen.generator.documents.element.line.HorizontalLineBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.element.table.TableRowBox;
import com.ssdgen.generator.documents.element.HAlign;

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
public class TestTableRowBox implements InvoiceLayout {

    @Override
    public String name() {
        return "TestTableRowBox";
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

        int startPosX = 25;
        int endPosX = 400;
        int startPosY = 750;
        PDFont font = PDType1Font.HELVETICA_BOLD;

        new HorizontalLineBox(startPosX, startPosY, endPosX, startPosY, Color.RED).build(stream,writer);

        VerticalContainer container = new VerticalContainer(startPosX, startPosY, 0);

        float[] config = new float[] {50, 50, 150, 50};


        TableRowBox row1 = new TableRowBox(config, 0, 0);
        row1.addElement(new SimpleTextBox(font, 12, 0, 0, "COL11"), false);
        row1.addElement(new SimpleTextBox(font, 12, 0, 0, "COL12"), false);
        row1.addElement(new SimpleTextBox(font, 12, 0, 0, "COL13"), false);
        row1.addElement(new SimpleTextBox(font, 12, 0, 0, "COL14"), false);
        row1.setBackgroundColor(Color.GRAY);
        container.addElement(row1);

        TableRowBox row11 = new TableRowBox(new float[] {50, 25, 25, 150, 50}, 0, 0);
        row11.addElement(new SimpleTextBox(font, 12, 0, 0, "COL111"), false);
        row11.addElement(new SimpleTextBox(font, 12, 0, 0, "SC112"), false);
        row11.addElement(new SimpleTextBox(font, 12, 0, 0, "SC122"), false);
        row11.addElement(new SimpleTextBox(font, 12, 0, 0, "COL113"), false);
        row11.addElement(new SimpleTextBox(font, 12, 0, 0, "COL114"), false);
        row11.setBackgroundColor(Color.GRAY);
        container.addElement(row11);

        TableRowBox row2 = new TableRowBox(config, 0,0);
        row2.addElement(new SimpleTextBox(font, 12, 0, 0, "COL21"), false);
        row2.addElement(new SimpleTextBox(font, 12, 0, 0, "COL22"), false);
        row2.addElement(new SimpleTextBox(font, 12, 0, 0, "Center Aligned COL23", Color.BLACK, Color.WHITE, HAlign.CENTER), false);
        row2.addElement(new SimpleTextBox(font, 12, 0, 0, "COL24"), false);
        container.addElement(row2);

        TableRowBox row3 = new TableRowBox(config, 0,0, VAlign.BOTTOM);
        row3.addElement(new SimpleTextBox(font, 12, 0, 0, "This BottomAligned col is going to be more than one line"), true);
        row3.addElement(new SimpleTextBox(font, 12, 0, 0, "This is a filler column text that is going to be more than one line"), false);
        row3.addElement(new SimpleTextBox(font, 12, 0, 0, "COL33"), false);
        row3.addElement(new SimpleTextBox(font, 12, 0, 0, "COL34"), false);
        row3.setBackgroundColor(Color.LIGHT_GRAY);
        container.addElement(row3);

        TableRowBox row4 = new TableRowBox(config, 0,0);
        row4.addElement(new SimpleTextBox(font, 12, 0, 0, "COL41", Color.WHITE, Color.DARK_GRAY), false);
        row4.addElement(new SimpleTextBox(font, 12, 0, 0, "COL42", Color.WHITE, Color.DARK_GRAY), false);
        row4.addElement(new SimpleTextBox(font, 12, 0, 0, "This topAligned col is going to be more than one line", Color.WHITE, Color.DARK_GRAY), false);
        row4.addElement(new SimpleTextBox(font, 12, 0, 0, "COL44", Color.WHITE, Color.DARK_GRAY), false);
        row4.setBackgroundColor(Color.DARK_GRAY);
        container.addElement(row4);

        container.build(stream,writer);
        stream.close();

        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {

        Path dir = Paths.get("target/tablerow");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/tablerow/tablerow.pdf");
        Path xml = Paths.get("target/tablerow/tablerow.xml");
        Path img = Paths.get("target/tablerow/tablerow.jpg");
        Path json = Paths.get("target/tablerow/tablerow.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }
}
