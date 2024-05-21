package com.fairandsmart.generator.documents.element;

import com.fairandsmart.generator.documents.InvoiceGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.element.product.ProductBox;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


@RunWith(JUnit4.class)
public class TestProductBox implements InvoiceLayout {

    @Override
    public String name() {
        return "TestProductBox";
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
        PDFont font = PDType1Font.HELVETICA;
        PDFont fontB = PDType1Font.HELVETICA_BOLD;
        float fontSize = 10;

        ProductBox products = new ProductBox(30, 500, model.getProductContainer(), font, fontB, fontSize);
        products.build(stream,writer);

        stream.close();

        writer.writeEndElement();
    }

    @Test
    public void test() throws Exception {
        Path dir = Paths.get("target/productbox");
        if ( !Files.exists(dir) ) {
                Files.createDirectories(dir);
        }

        // get ntests var from cli, with mvn test -Dntests=NUM, otherwise use 5
        String ntests_env = System.getProperty("ntests");
        int ntests = ((ntests_env == null) ? 5 : Integer.parseInt(ntests_env));

        for (int i = 0; i < ntests; i++) {
            Path pdf = Paths.get("target/productbox/productbox-" + i + ".pdf");
            Path xml = Paths.get("target/productbox/productbox-" + i + ".xml");
            Path img = Paths.get("target/productbox/productbox-" + i + ".jpg");
            Path json = Paths.get("target/productbox/productbox-" + i + ".json");

            GenerationContext ctx = GenerationContext.generate();
            InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
            InvoiceGenerator.getInstance().generateInvoice(this, model, pdf, xml, img, json);
        }
    }
}
