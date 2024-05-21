package com.ssdgen.generator.documents.element.footer;

import com.ssdgen.generator.documents.data.helper.HelperCommon;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.data.model.Company;
import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.image.ImageBox;
import com.ssdgen.generator.documents.element.line.HorizontalLineBox;
import com.ssdgen.generator.documents.element.textbox.SimpleTextBox;
import com.ssdgen.generator.documents.element.container.VerticalContainer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import javax.xml.stream.XMLStreamWriter;


public class SignatureBox extends ElementBox {

    private final PDFont font;
    private final float fontSize;
    private final String sigText;

    private final float sigTextX;
    private final float sigTextY;
    private final float sigImgWidth;
    private final float sigImgHeight;
    private final Color lineStrokeColor;

    private final InvoiceModel model;
    private final PDDocument document;
    private final Company company;

    private VerticalContainer vContainer;

    public SignatureBox(PDFont font, float fontSize, String sigText,
                        float sigTextX, float sigTextY,
                        float sigImgWidth, float sigImgHeight,
                        Color lineStrokeColor,
                        InvoiceModel model, PDDocument document,
                        Company company) throws Exception {
        this.font = font;
        this.fontSize = fontSize;
        this.sigText = sigText;

        this.sigTextX = sigTextX;
        this.sigTextY = sigTextY;
        this.sigImgWidth = sigImgWidth;
        this.sigImgHeight = sigImgHeight;
        this.lineStrokeColor = lineStrokeColor;

        this.model = model;
        this.document = document;
        this.company = company;

        this.init();
    }

    private void init() throws Exception {
        vContainer = new VerticalContainer(0,0,sigImgWidth);

        SimpleTextBox sigTextBox = new SimpleTextBox(font,fontSize,sigTextX,sigTextY,sigText,"Signature");

        HorizontalLineBox sigLine = new HorizontalLineBox(
                sigTextX - 10, sigTextY + 5,
                sigTextX + sigTextBox.getBBox().getWidth() + 10, sigTextY + 5,
                lineStrokeColor);

        String sigPath = HelperCommon.getResourceFullPath(this, "common/signature/" + company.getSignature().getFullPath());
        PDImageXObject sigImg = PDImageXObject.createFromFile(sigPath, document);

        // resize maintaining aspect to (sigImgWidth, sigImgHeight)
        float sigScale = Math.min(sigImgWidth/sigImg.getWidth(), sigImgHeight/sigImg.getHeight());
        float sigW = sigImg.getWidth() * sigScale;
        float sigH = sigImg.getHeight() * sigScale;
        // align signature to center of sigTextBox bbox
        float sigIX = sigTextBox.getBBox().getPosX() + sigTextBox.getBBox().getWidth()/2 - sigW/2;
        float sigIY = sigTextY + 10;

        ImageBox sigImgBox = new ImageBox(sigImg, sigIX, sigIY, sigW, sigH, "Signature Image");

        vContainer.addElement(sigImgBox);
        vContainer.addElement(sigLine);
        vContainer.addElement(sigTextBox);
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
