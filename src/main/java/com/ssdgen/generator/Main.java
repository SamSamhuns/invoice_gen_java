package com.ssdgen.generator;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;

public class Main {

    public static void main(String[] args) throws Exception {
        GenerationContext ctx = GenerationContext.generate();
        InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
    }
}
