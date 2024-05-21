package com.ssdgen.generator.documents;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.layout.InvoiceLayout;
import com.ssdgen.generator.documents.layout.macomp.MACOMPLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;


public class TestMACOMPLayout {

@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/macomp/pdf",
                "target/macomp/xml",
                "target/macomp/img",
                "target/macomp/json");

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
                Path pdf = Paths.get("target/macomp/pdf/macomp-"+ i + ".pdf");
                Path xml = Paths.get("target/macomp/xml/macomp-"+ i + ".xml");
                Path img = Paths.get("target/macomp/img/macomp-"+ i + ".jpg");
                Path json = Paths.get("target/macomp/json/macomp-"+ i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                InvoiceLayout layout = new MACOMPLayout();
                InvoiceGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json);
        }
}

}
