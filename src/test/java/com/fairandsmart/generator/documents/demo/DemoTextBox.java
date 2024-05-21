package com.ssdgen.generator.documents.demo;

import com.ssdgen.generator.documents.InvoiceGenerator;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.layout.InvoiceLayout;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;

import com.ssdgen.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@RunWith(JUnit4.class)
public class DemoTextBox implements InvoiceLayout {

    @Override
    public String name() {
        return "DemoTextBox";
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

        SimpleTextBox stb = new SimpleTextBox(
                PDType1Font.HELVETICA_BOLD,
                12,
                20,
                750,
                model.getReference().getLabelInvoice(),
                "HEAD");
        stb.build(stream,writer);
        SimpleTextBox stb2 = new SimpleTextBox(
                PDType1Font.HELVETICA,
                12,
                20 + stb.getBBox().getWidth() + 5,
                750,
                model.getReference().getValueInvoice(),
                "IN");
        stb2.build(stream,writer);

        stream.close();
        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {
        Path dir = Paths.get("target/demotextbox");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/demotextbox/demotextbox.pdf");
        Path xml = Paths.get("target/demotextbox/demotextbox.xml");
        Path img = Paths.get("target/demotextbox/demotextbox.jpg");
        Path json = Paths.get("target/demotextbox/demotextbox.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }
}
