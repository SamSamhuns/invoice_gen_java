package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.ReceiptModel;
import com.fairandsmart.generator.documents.layout.receipt.GenericReceiptLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestSSDReceiptLayout {
@Test
public void test() throws Exception {
        List<String> artefact_dirs = Arrays.asList(
                "target/SSDReceipt/pdf",
                "target/SSDReceipt/xml",
                "target/SSDReceipt/img",
                "target/SSDReceipt/xmlEval");

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
                Path pdf = Paths.get("target/SSDReceipt/pdf/receipt-"+ i + ".pdf");
                Path xml = Paths.get("target/SSDReceipt/xml/receipt-"+ i + ".xml");
                Path img = Paths.get("target/SSDReceipt/img/receipt-"+ i + ".jpg");
                Path xmlForEval = Paths.get("target/SSDReceipt/xmlEval/receipt-"+ i + ".xml");

                GenerationContext ctx = GenerationContext.generate();
                ReceiptModel model = new ReceiptModel.Generator().generate(ctx);
                GenericReceiptLayout layout = new GenericReceiptLayout();
                ReceiptGenerator.getInstance().generateReceipt(layout, model, pdf, xml, img, xmlForEval);
        }
}
}
