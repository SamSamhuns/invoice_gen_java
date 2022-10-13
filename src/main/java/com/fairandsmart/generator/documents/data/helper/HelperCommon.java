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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.Matrix;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HelperCommon extends Helper {

    public static class pdType1Fonts {
        private PDFont fontNormal;
        private PDFont fontBold;
        private PDFont fontItalic;

        public pdType1Fonts (PDFont fontNormal, PDFont fontBold, PDFont fontItalic) {
            this.fontNormal = fontNormal;
            this.fontBold = fontBold;
            this.fontItalic = fontItalic;
        }

        public PDFont getFontNormal() {
            return this.fontNormal;
        }

        public void setfontNormal(PDFont font) {
            this.fontNormal = font;
        }

        public PDFont getFontBold() {
            return this.fontBold;
        }

        public void setfontBold(PDFont font) {
            this.fontBold = font;
        }

        public PDFont getFontItalic() {
            return this.fontItalic;
        }

        public void setfontItalic(PDFont font) {
            this.fontItalic = font;
        }
    }


    public HelperCommon() {
    }

    public static float round(float num, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float rand_uniform(float minA, float maxA) {
        // get uniform dist from minA to maxA in default steps of 0.01
        return HelperCommon.rand_uniform(minA, maxA, 0.01f);
    }

    public static float rand_uniform(float minA, float maxA, float diff) {
        // get uniform dist from minA to maxA in steps differences
        float steps = 1 / diff;
        return (float)(rnd.nextInt((int)((maxA - minA) * steps + 1)) + minA * steps) / steps;
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

    /**
    * Returns an HashMap object
    * that states whether the component (key) should be used in the layout generation
    * @param  configMaps  an absolute URL giving the base location of the image
    * @param  layout       the location of the image, relative to the url argument
    * @return      HashMap linking keys to booleans
    */
    public static Map<String, Boolean> getMatchedConfigMap(List<Map<String, Object>> configMaps, String layout) throws Exception {
      List<Map<String, Object>> matchedConfigs = configMaps.stream()
                                                            .filter(cfg -> cfg.get("layout").equals(layout))
                                                            .collect(Collectors.toList());
      if (matchedConfigs.size() == 0) {
          throw new Exception("No matching layout was found for " + layout + " in existing layout config list.");
      }
      // convert probability Object to String -> Float map
      Object cfg = matchedConfigs.get(0).get("probability");
      Type type = new TypeToken<Map<String, Integer>>(){}.getType();
      Map<String, Integer> probMap = new Gson().fromJson(cfg.toString(), type);
      // convert probability map into a boolean use or not use map
      // int value out of 100, 60 -> 60% proba
      Map<String, Boolean> genMap = new HashMap<String, Boolean>();
      for (Map.Entry<String, Integer> entry : probMap.entrySet())
         genMap.put(entry.getKey(), rnd.nextInt(100) < entry.getValue());

      return genMap;
    }

    public static pdType1Fonts getRandomPDType1Fonts() throws Exception {
            final List<PDFont> pd1fontNormalList = Arrays.asList(
                    PDType1Font.HELVETICA,
                    PDType1Font.COURIER,
                    PDType1Font.TIMES_ROMAN);
            final List<PDFont> pd1fontBoldList = Arrays.asList(
                    PDType1Font.HELVETICA_BOLD,
                    PDType1Font.COURIER_BOLD,
                    PDType1Font.TIMES_BOLD);
            final List<PDFont> pd1fontItalicList = Arrays.asList(
                    PDType1Font.HELVETICA_OBLIQUE,
                    PDType1Font.COURIER_OBLIQUE,
                    PDType1Font.TIMES_ITALIC);
                    // PDType1Font.COURIER_BOLD_OBLIQUE,
                    // PDType1Font.HELVETICA_BOLD_OBLIQUE,
                    // PDType1Font.TIMES_BOLD_ITALIC);
            assert pd1fontNormalList.size() == pd1fontBoldList.size();
            assert pd1fontNormalList.size() == pd1fontItalicList.size();
            int fontIdx = rnd.nextInt(pd1fontNormalList.size());

            return new pdType1Fonts(pd1fontNormalList.get(fontIdx),
                                    pd1fontBoldList.get(fontIdx),
                                    pd1fontItalicList.get(fontIdx));
    }
}
