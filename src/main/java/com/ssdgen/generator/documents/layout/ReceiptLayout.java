package com.ssdgen.generator.documents.layout;

import com.ssdgen.generator.documents.data.model.ReceiptModel;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.stream.XMLStreamWriter;

public interface ReceiptLayout {
    String name();

    void builtReceipt(ReceiptModel model, PDDocument document, XMLStreamWriter writer,XMLStreamWriter writerEval) throws Exception;

}
