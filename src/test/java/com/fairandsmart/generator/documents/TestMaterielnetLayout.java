package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.materielnet.MaterielnetLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestMaterielnetLayout {

@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/materielnet/pdf",
                "target/materielnet/xml",
                "target/materielnet/img",
                "target/materielnet/json");

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
                Path pdf = Paths.get("target/materielnet/pdf/materielnet-" + i + ".pdf");
                Path xml = Paths.get("target/materielnet/xml/materielnet" + i + ".xml");
                Path img = Paths.get("target/materielnet/img/materielnet" +i + ".jpg");
                Path json = Paths.get("target/materielnet/json/materielnet" +i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                ctx.setCountry("AE_en");
                ctx.setLanguage("en");
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                InvoiceLayout layout = new MaterielnetLayout();
                InvoiceGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json);
        }
}

}
