package com.ssdgen.generator.documents.element.textbox;

import com.ssdgen.generator.documents.element.ElementBoxForEvaluation;

import javax.xml.stream.XMLStreamWriter;
import java.util.logging.Logger;

public class SimpleTextBoxForEvaluation {

    private static final Logger LOGGER = Logger.getLogger(SimpleTextBoxForEvaluation.class.getName());

    private final String optionalClass;
    private final int orderPos;


    public  SimpleTextBoxForEvaluation( String optionalClass, int orderPos)  {

        this.optionalClass = optionalClass;
        this.orderPos = orderPos;

    }

    public void build( XMLStreamWriter writer) throws Exception {
        ElementBoxForEvaluation.writeXMLZone(writer, "ocrx_word", optionalClass,orderPos);
    }

}
