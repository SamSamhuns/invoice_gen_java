package com.fairandsmart.generator.documents.element.container;

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

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VerticalContainer extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(VerticalContainer.class.getName());

    private final List<ElementBox> elements;
    private final BoundingBox box;
    private final float maxWidth;
    private float borderThickness;
    private Color borderColor;
    private Color backgroundColor;

    public VerticalContainer(float posX, float posY, float maxWidth) {
        this(posX, posY, maxWidth, 1, null, null);
    }

    public VerticalContainer(float posX, float posY, float maxWidth, float borderThickness, Color borderColor, Color backgroundColor) {
        this.elements = new ArrayList<>();
        this.maxWidth = maxWidth;
        this.box = new BoundingBox(posX, posY, 0, 0);
        this.borderThickness = borderThickness;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    public void addElement(ElementBox element) throws Exception {
        this.elements.add(element);
        if ( maxWidth > 0 && element.getBoundingBox().getWidth() > maxWidth ) {
            element.setWidth(maxWidth);
        }
        if ( element.getBoundingBox().getWidth() > this.getBoundingBox().getWidth() ) {
            this.getBoundingBox().setWidth(element.getBoundingBox().getWidth());
        }
        element.getBoundingBox().setPosX(0);
        element.getBoundingBox().setPosY(0);
        element.translate(box.getPosX(), box.getPosY() - this.box.getHeight());
        this.box.setHeight(this.box.getHeight() + element.getBoundingBox().getHeight());
    }

    public void setBorderThickness(float thick) {
        this.borderThickness = thick;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void alignElements(String align, float width) {
        for ( ElementBox element : elements ) {
            float posX = box.getPosX();
            //System.out.println(width);
            switch ( align ) {
                case "CENTER":
                    posX = (width - box.getPosX() - element.getBoundingBox().getWidth())/2; break;
                case "RIGHT":
                    posX = (width - box.getPosX()) - element.getBoundingBox().getWidth(); break;
            }
            float transX = posX - element.getBoundingBox().getPosX();
            element.translate(transX, 0);
        }
    }

    @Override
    public String toString() {
        return "VerticalContainer{" +
                "Num elements=" + elements.size() +
                ", (X, Y)=(" + box.getPosX() + ", " + box.getPosY()  + ")" +
                ", (W, H)=(" + box.getWidth() + ", " + box.getHeight()  + ")" +
                ", borderThickness=" + borderThickness +
                ", borderColor=" + borderColor +
                ", backgroundColor=" + backgroundColor +
                '}';
    }

    @Override
    public BoundingBox getBoundingBox() {
        return box;
    }

    @Override
    public void setWidth(float width) throws Exception {
        this.setHeight(0);
        this.box.setWidth(width);
        for ( ElementBox element : elements ) {
            element.setWidth(width);
            element.getBoundingBox().setPosX(0);
            element.getBoundingBox().setPosY(0);
            float offsetY = this.box.getHeight();
            element.translate(box.getPosX(), box.getPosY() - offsetY);
            this.box.setHeight(this.box.getHeight() + element.getBoundingBox().getHeight());
        }
    }

    @Override
    public void setHeight(float height) throws Exception {
        this.box.setHeight(height);
        //throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        box.translate(offsetX, offsetY);
        for ( ElementBox element : elements ) {
            element.translate(offsetX, offsetY);
        }
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        if ( borderColor != null ) {
            stream.setLineWidth(borderThickness);
            stream.setStrokingColor(borderColor);
            borderThickness += 0.5f;  // add an expansion factor
            stream.addRect(box.getPosX()-borderThickness, box.getPosY() - box.getHeight() - borderThickness, box.getWidth() + borderThickness*2, box.getHeight() + borderThickness*2);
            stream.stroke();
        }
        if ( backgroundColor != null ) {
            stream.setNonStrokingColor(backgroundColor);
            stream.addRect(box.getPosX(), box.getPosY() - box.getHeight(), box.getWidth(), box.getHeight());
            stream.fill();
        }

        for ( ElementBox element : elements ) {
            element.build(stream, writer);
        }
    }

}
