package com.fairandsmart.generator.documents.element.line;

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

import java.awt.Color;
import javax.xml.stream.XMLStreamWriter;

public class HorizontalLineBox extends ElementBox {

    private final float targetX;
    private final float targetY;
    private final float lineWidth;
    private final BoundingBox box;
    private Color strokeColor;

    public HorizontalLineBox(float posX, float posY, float targetX, float targetY) {
        this(posX, posY, targetX, targetY,        1f, Color.BLACK);
    }

    public HorizontalLineBox(float posX, float posY, float targetX, float targetY, Color strokeColor) {
        this(posX, posY, targetX, targetY,        1f, strokeColor);
    }

    public HorizontalLineBox(float posX, float posY, float targetX, float targetY, float lineWidth) {
        this(posX, posY, targetX, targetY, lineWidth, Color.BLACK);
    }

    public HorizontalLineBox(float posX, float posY, float targetX, float targetY, float lineWidth, Color strokeColor) {
        this.lineWidth = lineWidth;
        this.targetX = targetX;
        this.targetY = targetY;
        this.strokeColor = strokeColor;
        this.box = new BoundingBox(posX, posY, 0, 0);
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Override
    public BoundingBox getBBox() {
        return box;
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        this.getBBox().translate(offsetX, offsetY);
    }

    @Override
    public void setWidth(float width) throws Exception {
        this.getBBox().setWidth(width);
    }

    @Override
    public void setHeight(float height) throws Exception {
        this.getBBox().setHeight(height);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        stream.setLineWidth(lineWidth);
        stream.moveTo(this.getBBox().getPosX(), this.getBBox().getPosY());
        stream.lineTo(targetX, this.getBBox().getPosY());
        stream.closePath();
        stream.setStrokingColor(strokeColor);
        stream.stroke();
    }

}
