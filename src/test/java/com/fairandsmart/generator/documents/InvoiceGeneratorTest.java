package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.amazon.AmazonLayout;
import com.fairandsmart.generator.documents.layout.bdmobilier.BDmobilierLayout;
import com.fairandsmart.generator.documents.layout.cdiscount.CdiscountLayout;
import com.fairandsmart.generator.documents.layout.darty.DartyLayout;
import com.fairandsmart.generator.documents.layout.ldlc.LDLCLayout;
import com.fairandsmart.generator.documents.layout.macomp.MACOMPLayout;
import com.fairandsmart.generator.documents.layout.materielnet.MaterielnetLayout;
import com.fairandsmart.generator.documents.layout.naturedecouvertes.NatureDecouvertesLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class InvoiceGeneratorTest {

@Test
public void generateUsingRoundRobinOnAllLayout() throws Exception {

        List<InvoiceLayout> availablesLayout = new ArrayList<>();
        availablesLayout.add(new AmazonLayout());
        availablesLayout.add(new BDmobilierLayout());
        availablesLayout.add(new CdiscountLayout());
        availablesLayout.add(new DartyLayout());
        availablesLayout.add(new LDLCLayout());
        availablesLayout.add(new MACOMPLayout());
        availablesLayout.add(new MaterielnetLayout());
        availablesLayout.add(new NatureDecouvertesLayout());

        Path generated = Paths.get("target/generated");
        if ( !Files.exists(generated) ) {
                Files.createDirectories(generated);
        }

        // get ntests var from cli, with mvn test -Dntests=NUM, otherwise use 5
        String ntests_env = System.getProperty("ntests");
        int ntests = ((ntests_env == null) ? 5 : Integer.parseInt(ntests_env));

        for(int i=1; i<=ntests; i++) {
                Path pdf = Paths.get("target/generated/basic-"+ i + ".pdf");
                Path xml = Paths.get("target/generated/basic-"+ i + ".xml");
                Path img = Paths.get("target/generated/basic-"+ i + ".jpg");
                Path json = Paths.get("target/generated/basic-"+ i + ".json");

                GenerationContext ctx = GenerationContext.generate();
                InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                InvoiceGenerator.getInstance().generateInvoice(availablesLayout.get(i % availablesLayout.size()), model, pdf, xml, img, json);
        }

}

}
