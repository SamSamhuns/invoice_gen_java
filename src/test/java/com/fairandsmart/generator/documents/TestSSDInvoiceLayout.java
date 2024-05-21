package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceSSDGenerator;
import com.fairandsmart.generator.documents.layout.invoiceSSD.InvoiceSSDLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestSSDInvoiceLayout {

@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/SSDInvoice/pdf",
                "target/SSDInvoice/xml",
                "target/SSDInvoice/img",
                "target/SSDInvoice/json",
                "target/SSDInvoice/xmlEval");

        for (String dirs : artefact_dirs) {
                Path dir = Paths.get(dirs);
                if ( !Files.exists(dir) ) {
                        Files.createDirectories(dir);
                }
        }

        // get ntests var from cli, with mvn test -Dntests=NUM, otherwise use 5
        String ntests_env = System.getProperty("ntests");
        int ntests = ((ntests_env == null) ? 5 : Integer.parseInt(ntests_env));

        for (int i=1; i<=ntests; i++) {
                Path pdf = Paths.get("target/SSDInvoice/pdf/invoice-"+ i + ".pdf");
                Path xml = Paths.get("target/SSDInvoice/xml/invoice-"+ i + ".xml");
                Path img = Paths.get("target/SSDInvoice/img/invoice-"+ i + ".jpg");
                Path json = Paths.get("target/SSDInvoice/json/invoice-"+ i + ".json");
                Path xmlEval = Paths.get("target/SSDInvoice/xmlEval/invoice-"+ i + ".xml");
                try {
                        GenerationContext ctx = GenerationContext.generate();
                        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                        InvoiceSSDLayout layout = new InvoiceSSDLayout();
                        InvoiceSSDGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json, xmlEval);
                } catch (Exception e) {
                        System.out.println("exception occured" + e);
                }
        }
}

}
