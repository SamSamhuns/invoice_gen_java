package com.ssdgen.generator.documents;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.layout.InvoiceLayout;
import com.ssdgen.generator.documents.layout.naturedecouvertes.NatureDecouvertesLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestNatureDecouvertesLayout {

@Test
public void test() throws Exception {
        List<String> artefact_dirs = Arrays.asList(
                "target/natureDecouvertes/pdf",
                "target/natureDecouvertes/xml",
                "target/natureDecouvertes/img",
                "target/natureDecouvertes/json");

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
                Path pdf = Paths.get("target/natureDecouvertes/pdf/natureDecouvertes-"+ i + ".pdf");
                Path xml = Paths.get("target/natureDecouvertes/xml/natureDecouvertes-"+ i + ".xml");
                Path img = Paths.get("target/natureDecouvertes/img/natureDecouvertes-"+ i + ".jpg");
                Path json = Paths.get("target/natureDecouvertes/json/natureDecouvertes-"+ i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                ctx.setMaxProductNum(10);  // increase max number of products to 10
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                InvoiceLayout layout = new NatureDecouvertesLayout();
                InvoiceGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json);
        }
}

}
