package com.ssdgen.generator.documents.element.footer;

import com.ssdgen.generator.documents.data.helper.HelperImage;
import com.ssdgen.generator.documents.data.helper.HelperCommon;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.data.model.Company;
import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.image.ImageBox;
import com.ssdgen.generator.documents.element.container.VerticalContainer;

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
    private final float alpha;
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
