package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.PayslipModel;
import com.fairandsmart.generator.documents.layout.payslip.GenericPayslipLayout;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestSSDPayslipLayout {
@Test
public void test() throws Exception {

        List<String> artefact_dirs = Arrays.asList(
                "target/SSDPayslip/pdf",
                "target/SSDPayslip/xml",
                "target/SSDPayslip/img",
                "target/SSDPayslip/xmlEval");

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
                Path pdf = Paths.get("target/SSDPayslip/pdf/payslip-"+ i + ".pdf");
                Path xml = Paths.get("target/SSDPayslip/xml/payslip-"+ i + ".xml");
                Path img = Paths.get("target/SSDPayslip/img/payslip-"+ i + ".jpg");
                Path xmlForEval = Paths.get("target/SSDPayslip/xmlEval/payslip-"+ i + ".xml");

                GenerationContext ctx = GenerationContext.generate();
                PayslipModel model = new PayslipModel.Generator().generate(ctx);
                GenericPayslipLayout layout = new GenericPayslipLayout();
                PayslipGenerator.getInstance().generatePayslip(layout, model, pdf, xml, img, xmlForEval);
        }
}
}
