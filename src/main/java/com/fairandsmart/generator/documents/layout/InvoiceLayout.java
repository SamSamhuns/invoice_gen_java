package com.fairandsmart.generator.documents.layout;

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

import com.fairandsmart.generator.documents.data.model.InvoiceModel;

import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.Matrix;

import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.awt.Color;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public interface InvoiceLayout {

  String name();
  Random rnd = new Random();

  public class pdType1FontPair {
      private PDType1Font normalFont;
      private PDType1Font boldFont;

      public pdType1FontPair (PDType1Font normalFont, PDType1Font boldFont) {
          this.normalFont = normalFont;
          this.boldFont = boldFont;
      }

      public PDType1Font getNormalFont() {
          return this.normalFont;
      }

      public void setNormalFont(PDType1Font font) {
          this.normalFont = font;
      }

      public PDType1Font getBoldFont() {
          return this.boldFont;
      }

      public void setBoldFont(PDType1Font font) {
          this.boldFont = font;
      }
  }

  void builtInvoice(InvoiceModel model, PDDocument document, XMLStreamWriter writer) throws Exception;

  public static Random getRandom() {
      return rnd;
  }

  public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
          // generates a barcode based on the String barcodeText
          EAN13Bean barcodeGenerator = new EAN13Bean();
          BitmapCanvasProvider canvas = new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
          barcodeGenerator.generateBarcode(canvas, barcodeText);
          return canvas.getBufferedImage();
  }

  public static pdType1FontPair getRandomPDType1FontPair() throws Exception {
          final List<PDType1Font> pdType1NormalFontList = Arrays.asList(
                  PDType1Font.HELVETICA,
                  PDType1Font.COURIER,
                  PDType1Font.TIMES_ROMAN);
                  // PDType1Font.COURIER_OBLIQUE,
                  // PDType1Font.HELVETICA_OBLIQUE,
                  // PDType1Font.TIMES_ITALIC);
          final List<PDType1Font> pdType1BoldFontList = Arrays.asList(
                  PDType1Font.HELVETICA_BOLD,
                  PDType1Font.COURIER_BOLD,
                  PDType1Font.TIMES_BOLD);
                  // PDType1Font.COURIER_BOLD_OBLIQUE,
                  // PDType1Font.HELVETICA_BOLD_OBLIQUE,
                  // PDType1Font.TIMES_BOLD_ITALIC);
          assert pdType1NormalFontList.size() == pdType1BoldFontList.size();
          int fontIdx = rnd.nextInt(pdType1NormalFontList.size());

          return new pdType1FontPair(
                  pdType1NormalFontList.get(fontIdx),
                  pdType1BoldFontList.get(fontIdx)
                  );
      }

  public static Color getRandomColor(int cSize) throws Exception {
        final List<Color> colorsList = Arrays.asList(
              Color.GRAY,
              Color.LIGHT_GRAY,
              Color.DARK_GRAY,
              Color.WHITE,
              Color.ORANGE,
              Color.YELLOW,
              Color.BLACK
              // Color.RED,
              // Color.GREEN,
              // Color.BLUE,
              // Color.MAGENTA,
              // Color.CYAN,
              );
        return colorsList.get(rnd.nextInt(Math.min(cSize, colorsList.size())));
  }


  public static void addWatermarkImagePDF(final PDDocument doc, final PDPage page, final PDImageXObject imgPDF) throws IOException {

        float oImgW = imgPDF.getWidth();
        float oImgH = imgPDF.getHeight();
        float pageW = page.getMediaBox().getWidth();
        float pageH = page.getMediaBox().getHeight();
        // rescale img dims to be 1/2.5 of page dim
        float nImgW = (1f/2.5f) * pageW;
        float nimgH = (nImgW * oImgH) / oImgW;
        float xoff = (pageW - nImgW) / 2;
        float yoff = (pageH - nimgH) / 2;

        float minImgAlpha = 0.08f;
        float maxImgAlpha = 0.18f;
        addWatermarkImagePDF(doc, page, imgPDF, xoff, yoff, nImgW, nimgH, minImgAlpha, maxImgAlpha);
    }

    public static void addWatermarkImagePDF(
            final PDDocument doc, final PDPage page, final PDImageXObject imgPDF,
            final float xPos, final float yPos, final float imgW, final float imgH,
            final float minA, final float maxA) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true,
                true)) {
            contentStream.saveGraphicsState();
            PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
            pdExtGfxState.setBlendMode(BlendMode.MULTIPLY);

            // get uniform dist from minA to maxA in 0.01 diffs
            float alpha = (float)(rnd.nextInt((int)((maxA-minA)*100+1))+minA*100) / 100.0f;
            pdExtGfxState.setNonStrokingAlphaConstant(alpha);
            contentStream.setGraphicsStateParameters(pdExtGfxState);
            // draw on document
            contentStream.drawImage(imgPDF, xPos, yPos, imgW, imgH);
            contentStream.restoreGraphicsState();
            contentStream.close();
        }
    }

  public static void addWatermarkTextPDF(final PDDocument doc, final PDPage page, final PDFont font, final String text) throws IOException {
        try (PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true,
                true)) {
            final float fontHeight = 90 + rnd.nextInt(20); // arbitrary for short text
            final float width = page.getMediaBox().getWidth();
            final float height = page.getMediaBox().getHeight();
            final float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;
            final float diagonalLength = (float) Math.sqrt(width * width + height * height);
            final float angle = (float) Math.atan2(height, width) + (float) rnd.nextInt(5)/100;
            final float x = (diagonalLength - stringWidth) / 2; // "horizontal" position in rotated world
            final float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
            cs.transform(Matrix.getRotateInstance(angle, 0, 0));
            cs.setFont(font, fontHeight);
            if (rnd.nextInt(10) < 3) {
                cs.setRenderingMode(RenderingMode.STROKE); // for "hollow" effect
            }

            final PDExtendedGraphicsState gs = new PDExtendedGraphicsState();

            float minA = 0.10f; float maxA = 0.25f;
            // get uniform dist from minA to maxA in 0.01 diffs
            float alpha = (float)(rnd.nextInt((int)((maxA-minA)*100+1))+minA*100) / 100.0f;
            gs.setNonStrokingAlphaConstant(alpha);
            gs.setStrokingAlphaConstant(alpha);
            gs.setBlendMode(BlendMode.MULTIPLY);
            gs.setLineWidth(3f);
            cs.setGraphicsStateParameters(gs);

            // Set color
            cs.setNonStrokingColor(Color.red);
            cs.setStrokingColor(Color.red);

            cs.beginText();
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
        }
    }

}
