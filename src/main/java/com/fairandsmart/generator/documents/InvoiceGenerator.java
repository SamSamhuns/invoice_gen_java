package com.fairandsmart.generator.documents;

/*-
 * #%L
 * FacoGen / A tool for annotated GEDI based invoice generation.
 *
 * Authors:
 *
 * Xavier Lefevre <xavier.lefevre@fairandsmart.com> / FairAndSmart
 * Nicolas Rueff <nicolas.rueff@fairandsmart.com> / FairAndSmart
 * Alan Balbo <alan.balbo@fairandsmart.com> / FairAndSmart
 * Frederic Pierre <frederic.pierre@fairansmart.com> / FairAndSmart
 * Victor Guillaume <victor.guillaume@fairandsmart.com> / FairAndSmart
 * Jérôme Blanchard <jerome.blanchard@fairandsmart.com> / FairAndSmart
 * Aurore Hubert <aurore.hubert@fairandsmart.com> / FairAndSmart
 * Kevin Meszczynski <kevin.meszczynski@fairandsmart.com> / FairAndSmart
 * Djedjiga Belhadj <djedjiga.belhadj@gmail.com> / Loria
 * %%
 * Copyright (C) 2019 Fair And Smart
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fairandsmart.generator.documents.layout.ldlc.LDLCLayout;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.amazon.AmazonLayout;
import com.fairandsmart.generator.documents.layout.bdmobilier.BDmobilierLayout;
import com.fairandsmart.generator.documents.layout.cdiscount.CdiscountLayout;
import com.fairandsmart.generator.documents.layout.darty.DartyLayout;
import com.fairandsmart.generator.documents.layout.macomp.MACOMPLayout;
import com.fairandsmart.generator.documents.layout.materielnet.MaterielnetLayout;
import com.fairandsmart.generator.documents.layout.ngeneric.NGenericLayout;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceGenerator {

    private static final String DATE_FORMAT = "mm/dd/yyyy hh:mm";

    private static class InvoiceGeneratorHolder {
        private final static InvoiceGenerator instance = new InvoiceGenerator();
    }

    public static InvoiceGenerator getInstance() {
        return InvoiceGeneratorHolder.instance;
    }

    private InvoiceGenerator() {
    }

    public void generateInvoice(InvoiceLayout layout, InvoiceModel model, Path pdf, Path xml, Path img, Path json) throws Exception {

        OutputStream xmlos = Files.newOutputStream(xml);
        XMLStreamWriter xmlOut = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlos, "utf-8"));
        xmlOut.writeStartDocument();
        xmlOut.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
        xmlOut.writeAttribute("GEDI_version", "2.4");
        xmlOut.writeAttribute("GEDI_date", "07/29/2013");
        xmlOut.writeStartElement("USER");
        xmlOut.writeAttribute("name", "FairAndSmartGenerator");
        xmlOut.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        xmlOut.writeAttribute("dateFormat", DATE_FORMAT);
        xmlOut.writeEndElement();
        xmlOut.writeStartElement("DL_DOCUMENT");
        xmlOut.writeAttribute("src", img.getFileName().toString());
        xmlOut.writeAttribute("NrOfPages", "1");
        xmlOut.writeAttribute("docTag", "xml");

        // Export as PDF
        PDDocument document = new PDDocument();
        // Build invoice layout and populate pdf
        layout.builtInvoice(model, document, xmlOut);
        document.save(pdf.toFile());

        // Export model annots as JSON
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        FileWriter json_file = new FileWriter(json.toString());
        gsonBuilder.toJson(model, json_file);
        json_file.flush();
        json_file.close();

        // Export as TIFF
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        ImageIOUtil.writeImage(bim, img.toString(), 300);

        document.close();

        xmlOut.writeEndElement();
        xmlOut.writeEndElement();
        xmlOut.writeEndDocument();
        xmlOut.close();

    }

    public static void main(String args[]) throws Exception {
        List<InvoiceLayout> availablesLayout = new ArrayList<>();
        availablesLayout.add(new AmazonLayout());
        availablesLayout.add(new BDmobilierLayout());
        availablesLayout.add(new CdiscountLayout());
        availablesLayout.add(new DartyLayout());
        //availablesLayout.add(new GenericLayout());
        availablesLayout.add(new LDLCLayout());
        availablesLayout.add(new MACOMPLayout());
        availablesLayout.add(new MaterielnetLayout());
        availablesLayout.add(new NGenericLayout());

        Path generated = Paths.get("target/generated/" + args[0]);
        if ( !Files.exists(generated) ) {
            Files.createDirectories(generated);
        }

        int start = Integer.parseInt(args[1]);
        int stop = Integer.parseInt(args[2]);
        for ( int i=start; i<stop; i++) {
            //String ts = "" + System.currentTimeMillis();
            Path pdf = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".pdf");
            Path xml = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".xml");
            Path img = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".tiff");
            Path json = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".json");
            // Path xmlEval = Paths.get("target/generated/" + args[0] + "/basicEval-"+ i + ".tiff");

            GenerationContext ctx = GenerationContext.generate();
            InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
            InvoiceGenerator.getInstance().generateInvoice(availablesLayout.get(i % availablesLayout.size()), model, pdf, xml, img, json);
            System.out.println("current: " + i);
        }
    }

}
