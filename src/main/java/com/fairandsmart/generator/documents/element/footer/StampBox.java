package com.fairandsmart.generator.documents.element.footer;

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
import com.fairandsmart.generator.documents.data.helper.HelperImage;
import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.Company;
import com.fairandsmart.generator.documents.element.BoundingBox;
import com.fairandsmart.generator.documents.element.ElementBox;
import com.fairandsmart.generator.documents.element.image.ImageBox;
import com.fairandsmart.generator.documents.element.container.VerticalContainer;

import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.Random;


public class StampBox extends ElementBox {

    private float stampWidth;
    private float stampHeight;
    private float alpha;
    private double rotAngle;

    private final InvoiceModel model;
    private final PDDocument document;
    private final Company company;
    private final Map<String, Boolean> proba;

    private final Random rnd = new Random();

    private VerticalContainer vContainer;

    public StampBox(float stampWidth, float stampHeight, float alpha,
                    InvoiceModel model, PDDocument document,
                    Company company, Map<String, Boolean> proba) throws Exception {

        this.stampWidth = stampWidth;
        this.stampHeight = stampHeight;
        this.alpha = alpha;
        this.rotAngle = 10 + rnd.nextInt(130);

        this.model = model;
        this.document = document;
        this.company = company;
        this.proba = proba;

        this.init();
    }

    private void init() throws Exception {
        vContainer = new VerticalContainer(0,0,0);

        String stampPath = HelperCommon.getResourceFullPath(this, "common/stamp/" + company.getStamp().getFullPath());
        PDImageXObject stampImg = PDImageXObject.createFromFile(stampPath, document);

        if (company.getStamp().getName().matches("(.*)" + "_rect")) {
            // For Rectangular stamps, set rotation angle to 0 and
            // resize stamp maintaining aspect ratio
            rotAngle = 0;
            stampWidth += rnd.nextInt(20);
            stampHeight = (stampWidth * stampImg.getHeight()) / stampImg.getWidth();
        }
        else if (proba.get("stamp_bottom_elongated")) {
            // elongate stamps if the stamp is a not a Rectangular one
            // and set rotation to 0
            rotAngle = 0;
            stampWidth = stampWidth + 50;
            stampHeight = stampHeight - 10;
        }
        // rotate if rotAngle is nonzero
        if (rotAngle != 0.0) {
            BufferedImage imgBuf = HelperImage.getRotatedImage(stampImg.getImage(), rotAngle);
            stampImg = LosslessFactory.createFromImage(document, imgBuf);
        }
        ImageBox stampImgBox = new ImageBox(stampImg,0,0,stampWidth,stampHeight,alpha,"stamp");
        vContainer.addElement(stampImgBox);
    }

    @Override
    public BoundingBox getBBox() {
        return vContainer.getBBox();
    }

    @Override
    public void setWidth(float width) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    @Override
    public void translate(float offsetX, float offsetY) {
        vContainer.translate(offsetX, offsetY);
    }

    @Override
    public void build(PDPageContentStream stream, XMLStreamWriter writer) throws Exception {
        vContainer.build(stream, writer);
    }
}
