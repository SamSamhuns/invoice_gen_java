package com.fairandsmart.generator.documents.layout;

import com.fairandsmart.generator.documents.data.model.Model;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.stream.XMLStreamWriter;

public interface SSDLayout {

    String name();

    void builtSSD(Model model, PDDocument document, XMLStreamWriter writer,XMLStreamWriter writerEval) throws Exception;

}
