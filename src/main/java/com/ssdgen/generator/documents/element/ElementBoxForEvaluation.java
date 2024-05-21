package com.ssdgen.generator.documents.element;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class ElementBoxForEvaluation extends ElementBox{


    public static String writeXMLZone(XMLStreamWriter writer, String type, String optionalClass, int orderPos) throws XMLStreamException {

        String id = "" ;
        writer.writeStartElement("DL_ZONE");
        writer.writeAttribute("gedi_type", type);
        writer.writeAttribute("id", id);
        writer.writeAttribute("optionalclass", optionalClass);
        writer.writeAttribute("orderpos", ""+orderPos);
        writer.writeEndElement();
        writer.writeCharacters(System.getProperty("line.separator"));
        return id;

    }


}
