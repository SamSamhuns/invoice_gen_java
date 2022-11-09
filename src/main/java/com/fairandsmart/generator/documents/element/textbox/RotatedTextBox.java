package com.fairandsmart.generator.documents.element.textbox;

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
import com.fairandsmart.generator.documents.common.VerifCharEncoding;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.HAlign;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;

import org.davidmoten.text.utils.WordWrap;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class RotatedTextBox extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(RotatedTextBox.class.getName());

    private final PDFont font;
    private final float fontSize;
    private final BoundingBox box;
    private final float rotAngle;
    private final String text;
    private String entityName;
    private final float textWidth;
    private final float textHeight;
    private final float pageWidth;
    private Color textColor;
    private Color backgroundColor;

    /*
      (posX, posY) represent the top left corner of image
    */
    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float pageWidth, String text) throws Exception {
        this(font, fontSize, posX, posY, 90f, pageWidth, text, Color.BLACK, null, "undefined", true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float pageWidth, String text, String entityName) throws Exception {
        this(font, fontSize, posX, posY, 90f, pageWidth, text, Color.BLACK, null, entityName, true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text) throws Exception {
        this(font, fontSize, posX, posY, rotAngle, pageWidth, text, Color.BLACK, null, "undefined", true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text, String entityName) throws Exception {
        this(font, fontSize, posX, posY, rotAngle, pageWidth, text, Color.BLACK, null, entityName, true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text, String entityName, Boolean useANSIEncoding) throws Exception {
      this(font, fontSize, posX, posY, rotAngle, pageWidth, text, Color.BLACK, null, entityName, useANSIEncoding);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text, Color textColor, Color backgroundColor) throws Exception {
        this(font, fontSize, posX, posY, rotAngle, pageWidth, text, textColor, backgroundColor, "undefined", true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text, Color textColor, Color backgroundColor, String entityName) throws Exception {
        this(font, fontSize, posX, posY, rotAngle, pageWidth, text, textColor, backgroundColor, entityName, true);
    }

    public RotatedTextBox(PDFont font, float fontSize, float posX, float posY, float rotAngle, float pageWidth, String text, Color textColor, Color backgroundColor, String entityName, Boolean useANSIEncoding) throws Exception {
        this.font = font;
        this.fontSize = fontSize;
        this.rotAngle = rotAngle;
        this.pageWidth = pageWidth;
        if(text == null){
            text = "";
        }
        this.text = useANSIEncoding ? VerifCharEncoding.remove(text) : text;
        this.entityName = entityName;

        this.textWidth = font.getStringWidth(this.text) / 1000;
        this.textHeight = fontSize;
        this.box = new BoundingBox(posX, posY, fontSize * this.textWidth, this.textHeight);

        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void setWidth(float width) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        this.getBBox().translate(offsetX, offsetY);
    }

    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if ( backgroundColor != null ) {
            stream.setNonStrokingColor(backgroundColor);
            stream.addRect(box.getPosX(), box.getPosY()-box.getHeight(), box.getWidth(), box.getHeight());
            stream.fill();
        }

        float xPos = box.getPosX();
        float yPos = box.getPosY();

        stream.beginText();
        stream.setNonStrokingColor(textColor);
        stream.setFont(font, fontSize);

        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(rotAngle), 0, 0);
        matrix.translate(0, -pageWidth);
        stream.setTextMatrix(matrix);
        stream.newLineAtOffset(yPos - textWidth / 2 - fontSize, pageWidth - xPos - textWidth / 2 - fontSize);

        stream.showText(text);

        stream.endText();

        this.writeXMLZone(writer, "ocrx_word", text, box, entityName);
    }
}
