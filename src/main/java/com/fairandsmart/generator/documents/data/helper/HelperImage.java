package com.fairandsmart.generator.documents.data.helper;

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

import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.Matrix;

import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.IOException;


public class HelperImage extends Helper {

    public HelperImage() {
    }

    public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
        // generates a barcode based on the String barcodeText
        EAN13Bean barcodeGenerator = new EAN13Bean();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        barcodeGenerator.generateBarcode(canvas, barcodeText);
        return canvas.getBufferedImage();
    }

    public static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws Exception {
        drawLine(cs, x1, y1, x2, y2, Color.BLACK);
    }

    public static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2, Color strokeColor) throws Exception {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.closePath();
        cs.setStrokingColor(strokeColor);
        cs.stroke();
    }

    public static void drawPolygon(PDPageContentStream cs, float[] x, float[] y) throws IOException {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Error: some points are missing coordinate");
        }
        for (int i = 0; i < x.length; i++) {
            if (i == 0) {
                cs.moveTo(x[i], y[i]);
            }
            else {
                cs.lineTo(x[i], y[i]);
            }
        }
        cs.closePath();
        cs.stroke();
    }

    public static BufferedImage getRotatedImage(BufferedImage buffImage, double angle) {
        // Stackoverflow https://stackoverflow.com/a/66189875
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));

        int width = buffImage.getWidth();
        int height = buffImage.getHeight();

        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

        BufferedImage rotatedImage = new BufferedImage(
                nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = rotatedImage.createGraphics();

        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, width / 2, height / 2);
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
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
        double rotAngle = 0;
        addWatermarkImagePDF(doc, page, imgPDF, xoff, yoff, nImgW, nimgH, minImgAlpha, maxImgAlpha, rotAngle);
    }

    public static void addWatermarkImagePDF(
            final PDDocument doc, final PDPage page, final PDImageXObject imgPDF,
            final float xPos, final float yPos, final float imgW, final float imgH,
            final float minAlpha, final float maxAlpha, final double rotAngle) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true,
                true)) {
            contentStream.saveGraphicsState();
            final PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
            pdExtGfxState.setBlendMode(BlendMode.MULTIPLY);
            float alpha = HelperCommon.rand_uniform(minAlpha, maxAlpha);
            pdExtGfxState.setNonStrokingAlphaConstant(alpha);
            contentStream.setGraphicsStateParameters(pdExtGfxState);

            // draw on document,check if rotation is required or not
            if (rotAngle != 0.0) {
                BufferedImage imgBuf = HelperImage.getRotatedImage(imgPDF.getImage(), rotAngle);
                PDImageXObject imgPDFRot = LosslessFactory.createFromImage(doc, imgBuf);
                contentStream.drawImage(imgPDFRot, xPos, yPos, imgW, imgH);
            }
            else {
                contentStream.drawImage(imgPDF, xPos, yPos, imgW, imgH);
            }
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
              final float stringWidth = font.getStringWidth(text) / 1000 * fontHeight;  /// mult by fontSize to get width as well
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

              float alpha = HelperCommon.rand_uniform(0.10f, 0.25f);
              gs.setNonStrokingAlphaConstant(alpha);
              gs.setStrokingAlphaConstant(alpha);
              gs.setBlendMode(BlendMode.MULTIPLY);
              gs.setLineWidth(3f);
              cs.setGraphicsStateParameters(gs);

              cs.setNonStrokingColor(Color.red);
              cs.setStrokingColor(Color.red);

              cs.beginText();
              cs.newLineAtOffset(x, y);
              cs.showText(text);
              cs.endText();
          }
    }
}
