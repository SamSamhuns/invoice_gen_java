package com.fairandsmart.generator.documents.layout;

import com.fairandsmart.generator.documents.data.model.InvoiceModel;

import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import javax.xml.stream.XMLStreamWriter;


public interface InvoiceLayout {

  String name();

  void buildInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer, InvoiceAnnotModel annot) throws Exception;

}
