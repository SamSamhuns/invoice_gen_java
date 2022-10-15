package com.fairandsmart.generator.documents.element.image;

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

import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;

public class ImageBox extends ElementBox {

    private final PDImageXObject image;
    private Color backgroundColor;
    private final String text;
    private final BoundingBox box;

    public ImageBox(PDImageXObject image, float posX, float posY, String text) {
        this(image, posX, posY, image.getWidth(), image.getHeight(), text);
    }

    public ImageBox(PDImageXObject image, float posX, float posY, float width, float height, String text) {
        this.image = image;
        this.text = text;
        this.box = new BoundingBox(posX, posY - height, width, height);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return box;
    }

    @Override
    public void setWidth(float width) {
        if(box.getWidth()>width){
            box.setWidth(width);
        }
        else{
            translate((width- box.getWidth())/2, 0);// Center align
        }
        //float scale = width / box.getWidth();
      //  box.setHeight(box.getHeight() * scale);
       // box.setWidth(box.getWidth() * scale);
    }

    @Override
    public void setHeight(float height) {
        float scale = height / box.getHeight();
        box.setHeight(box.getHeight() * scale);
        box.setWidth(box.getWidth() * scale);
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        box.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if ( backgroundColor != null ) {
            stream.setNonStrokingColor(backgroundColor);
            stream.addRect(box.getPosX(), box.getPosY(), box.getWidth(), box.getHeight());
            stream.fill();
        }

        stream.drawImage(image, box.getPosX(), box.getPosY(), box.getWidth(), box.getHeight());
        //box.setPosY(box.getPosY() + box.getHeight());
        this.writeXMLZone(writer, "ocr_carea", text, box, "img");
    }

}
