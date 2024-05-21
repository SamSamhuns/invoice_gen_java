package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.darty.DartyLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestDartyLayout {

@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/darty/pdf",
                "target/darty/xml",
                "target/darty/img",
                "target/darty/json");

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

                Path pdf = Paths.get("target/darty/pdf/darty" + i + ".pdf");
                Path xml = Paths.get("target/darty/xml/darty" + i + ".xml");
                Path img = Paths.get("target/darty/img/darty" + i + ".jpg");
                Path json = Paths.get("target/darty/json/darty" + i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                ctx.setCountry("FR");
                ctx.setLanguage("fr");
                InvoiceLayout layout = new DartyLayout();
                InvoiceGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json);
        }
}
}
