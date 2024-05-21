package com.ssdgen.generator.documents.layout;

import com.ssdgen.generator.documents.data.model.PayslipModel;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.stream.XMLStreamWriter;

public interface PayslipLayout {
    String name();

    void builtPayslip(PayslipModel model, PDDocument document, XMLStreamWriter writer,XMLStreamWriter writerEval) throws Exception;

}
