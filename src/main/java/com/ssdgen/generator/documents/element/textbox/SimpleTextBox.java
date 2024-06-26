package com.ssdgen.generator.documents.element.textbox;

import com.ssdgen.generator.documents.element.BoundingBox;
import com.ssdgen.generator.documents.element.Padding;
import com.ssdgen.generator.documents.common.VerifCharEncoding;
import com.ssdgen.generator.documents.element.ElementBox;
import com.ssdgen.generator.documents.element.HAlign;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.davidmoten.text.utils.WordWrap;

import javax.xml.stream.XMLStreamWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SimpleTextBox extends ElementBox {

    private static final Logger LOGGER = Logger.getLogger(SimpleTextBox.class.getName());

    private Color textColor;
    private Color backgroundColor;
    private final PDFont font;
    private final float fontSize;
    private final BoundingBox box;
    private Padding padding;
    private final float lineHeight;
    private final float underline;
    private final float overline;
    private final String text;
    private List<String> textLines;
    private String entityName;
    private HAlign halign;

    /*
      (posX, posY) represent the top left corner of image
    */
    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text) throws Exception {
        this(font, fontSize, posX, posY, text, Color.BLACK, null, HAlign.LEFT, "undefined", true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, String entityName) throws Exception {
        this(font, fontSize, posX, posY, text, Color.BLACK, null, HAlign.LEFT, entityName, true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, HAlign halign) throws Exception {
        this(font, fontSize, posX, posY, text, Color.BLACK, null, halign, "undefined", true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, String entityName, Boolean useANSIEncoding) throws Exception {
      this(font, fontSize, posX, posY, text, Color.BLACK, null, HAlign.LEFT, entityName, useANSIEncoding);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, HAlign halign, String entityName) throws Exception {
        this(font, fontSize, posX, posY, text, Color.BLACK, null, halign, entityName, true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, Color textColor, Color backgroundColor) throws Exception {
        this(font, fontSize, posX, posY, text, textColor, backgroundColor, HAlign.LEFT, "undefined", true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, Color textColor, Color backgroundColor, String entityName) throws Exception {
        this(font, fontSize, posX, posY, text, textColor, backgroundColor, HAlign.LEFT, entityName, true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, Color textColor, Color backgroundColor, HAlign halign) throws Exception {
        this(font, fontSize, posX, posY, text, textColor, backgroundColor, halign, "undefined", true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, Color textColor, Color backgroundColor, HAlign halign, String entityName) throws Exception {
        this(font, fontSize, posX, posY, text, textColor, backgroundColor, halign, entityName, true);
    }

    public SimpleTextBox(PDFont font, float fontSize, float posX, float posY, String text, Color textColor, Color backgroundColor, HAlign halign, String entityName, Boolean useANSIEncoding) throws Exception {
        this.padding = new Padding();
        this.font = font;
        this.fontSize = fontSize;
        if(text == null){
            text = "";
        }
        this.text = useANSIEncoding ? VerifCharEncoding.remove(text) : text;
        //this.text = text.replace("\n", "").replace("\r", "");
        this.entityName = entityName;
        this.textLines = new ArrayList<>();
        this.textLines.add(this.text);
        this.underline = font.getFontDescriptor().getFontBoundingBox().getLowerLeftY() / 1000 * fontSize;
        this.overline = font.getFontDescriptor().getFontBoundingBox().getUpperRightY() / 1000 * fontSize;
        this.lineHeight = overline - underline;
        this.box = new BoundingBox(posX, posY, fontSize * font.getStringWidth(this.text) / 1000, lineHeight);
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.halign = halign;
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
    public void setWidth(float width) throws IOException {
        //TODO we need to count the number of spaces between word to include the good posX in the offset
        float contentWidth = width - this.padding.getHorizontalPadding();
        /*if ( contentWidth <= 0 ) {
            throw new IOException("unable to fit content in the desired width");
        }*/
        String dummyString = "x".repeat(100);
        float dummyStringWidth = (this.fontSize * this.font.getStringWidth(dummyString) / 1000);
        // max length of chars that fit within contentWidth
        int maxContentLength = Math.max((int)(dummyString.length() / (dummyStringWidth/contentWidth)) - 3, 1); // -3 makes breaking more likely

        String wrappedText = WordWrap.from(text)
                                     .maxWidth(maxContentLength)
                                     .insertHyphens(true)
                                     .breakWords(true)
                                     .wrap();

        this.textLines = new ArrayList<>();
        Collections.addAll(this.textLines, wrappedText.split("\n"));
        this.box.setHeight((this.textLines.size() * lineHeight) + padding.getVerticalPadding());
        this.box.setWidth(width + padding.getHorizontalPadding());
    }

    @Override
    public void setHeight(float height) throws Exception {
        throw new Exception("Not allowed");
    }

    public void setPadding(float left, float top, float right, float bottom) throws IOException {
        this.padding = new Padding(left, top, right, bottom);
        this.setWidth(this.getBBox().getWidth() + padding.getHorizontalPadding());
        //this.box = new BoundingBox(box.getPosX(), box.getPosY(), box.getWidth() + padding.getHorizontalPadding(), box.getHeight() + padding.getVerticalPadding());
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void setHalign(HAlign halign) {
        this.halign = halign;
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

        stream.beginText();
        stream.setNonStrokingColor(textColor);
        stream.setFont(font, fontSize);
        float lineOffsetX = 0;
        stream.newLineAtOffset(box.getPosX(), box.getPosY() - underline - padding.getTop());
        for (int i=0; i<this.textLines.size(); i++) {
            float lineWidth = this.fontSize * this.font.getStringWidth(this.textLines.get(i)) / 1000;
            switch ( halign ) {
                case LEFT :
                    lineOffsetX = padding.getLeft(); break;
                case RIGHT:
                    lineOffsetX = box.getWidth() - lineWidth - padding.getRight(); break;
                case CENTER:
                    lineOffsetX = padding.getLeft() + ((box.getWidth() - lineWidth - padding.getHorizontalPadding()) / 2); break;
            }
            stream.newLineAtOffset(lineOffsetX, 0-lineHeight);
            stream.showText(this.textLines.get(i));
            stream.newLineAtOffset(0-lineOffsetX, 0);
        }
        stream.endText();

        lineOffsetX = 0;
        float offsetY = 0 - padding.getTop() - lineHeight;
        for (int i=0; i<this.textLines.size(); i++) {
            float lineWidth = this.fontSize * this.font.getStringWidth(this.textLines.get(i)) / 1000;
            switch ( halign ) {
                case LEFT :
                    lineOffsetX = padding.getLeft(); break;
                case RIGHT:
                    lineOffsetX = box.getWidth() - lineWidth - padding.getRight(); break;
                case CENTER:
                    lineOffsetX = padding.getLeft() + ((box.getWidth() - lineWidth - padding.getHorizontalPadding()) / 2); break;
            }

            float wordOffsetX = lineOffsetX;
            if ( textLines.get(i).length() > 0 ) {
                String[] words = textLines.get(i).split(" ");
                List<String> wordIds = new ArrayList<>();
                for (String word : words) {
                    //TODO we need to count the number of spaces between word to include the good posX in the offset
                    float wordWidth = fontSize * font.getStringWidth(word) / 1000;
                    BoundingBox wordBox = new BoundingBox(box.getPosX() + wordOffsetX, box.getPosY() + offsetY, wordWidth, lineHeight);
                    wordIds.add(writeXMLZone(writer, "ocrx_word", word, wordBox, entityName));
                    wordOffsetX = wordOffsetX + wordWidth + (fontSize * font.getSpaceWidth() / 1000);
                }
               // if ( entityName != null && entityName.length() > 0 ) {
               //     lineWidth = fontSize * font.getStringWidth(textLines.get(i)) / 1000;
               //     BoundingBox entityBox = new BoundingBox(box.getPosX() + lineOffsetX, box.getPosY() + offsetY, lineWidth, lineHeight);
               //     writeXMLZone(writer, entityName, this.textLines.get(i), entityBox, wordIds);
               // }
            }
            offsetY = offsetY - lineHeight;
        }
    }

}
