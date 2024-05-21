package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.amazon.AmazonLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestAmazonLayout {

@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/amazon/pdf",
                "target/amazon/xml",
                "target/amazon/img",
                "target/amazon/json");

        for (String dirs : artefact_dirs) {
                Path dir = Paths.get(dirs);
                if ( !Files.exists(dir) ) {
                        Files.createDirectories(dir);
                }
        }

        // get ntests var from cli, with mvn test -Dntests=NUM, otherwise use 5
        String ntests_env = System.getProperty("ntests");
        int ntests = ((ntests_env == null) ? 5 : Integer.parseInt(ntests_env));

        for(int i=1; i<=ntests; i++) {
                Path pdf = Paths.get("target/amazon/pdf/amazon-"+ i + ".pdf");
                Path xml = Paths.get("target/amazon/xml/amazon-"+ i + ".xml");
                Path img = Paths.get("target/amazon/img/amazon-"+ i + ".jpg");
                Path json = Paths.get("target/amazon/json/amazon-"+ i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                InvoiceLayout layout = new AmazonLayout();
                InvoiceGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json);
        }
}

}
