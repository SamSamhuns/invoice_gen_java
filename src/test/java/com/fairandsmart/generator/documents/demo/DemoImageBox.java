package com.fairandsmart.generator.documents.demo;

import com.fairandsmart.generator.documents.InvoiceGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


@RunWith(JUnit4.class)
public class DemoImageBox implements InvoiceLayout {

    @Override
    public String name() {
        return "DemoImageBox";
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

        String barcode = this.getClass().getClassLoader().getResource("invoices/parts/amazon/barcode1.jpg").getFile();
        PDImageXObject pdBarcode = PDImageXObject.createFromFile(barcode, document);
        new ImageBox(pdBarcode, 25, 750, pdBarcode.getWidth() / 2, pdBarcode.getHeight() / 2, "DMmZXznqN /-1 of 1 -// std-in-remote").build(stream,writer);

        stream.close();
        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {
        Path dir = Paths.get("target/demoimgbox");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        Path pdf = Paths.get("target/demoimgbox/demoimgbox.pdf");
        Path xml = Paths.get("target/demoimgbox/demoimgbox.xml");
        Path img = Paths.get("target/demoimgbox/demoimgbox.jpg");
        Path json = Paths.get("target/demoimgbox/demoimgbox.json");

        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
        InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
    }
}
