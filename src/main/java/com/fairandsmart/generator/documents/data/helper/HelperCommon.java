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

import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.NumberFormat;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.awt.Color;
import java.net.URI;
import java.io.File;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class HelperCommon extends Helper {

    public static class PDCustomFonts {
        private PDFont fontNormal;
        private PDFont fontBold;
        private PDFont fontItalic;

        public PDCustomFonts (PDFont fontNormal, PDFont fontBold, PDFont fontItalic) {
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
          bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
          return bd.floatValue();
    }

    public static float rand_uniform(float minA, float maxA) {
          // get uniform dist from minA to maxA in default steps of 0.01
          return HelperCommon.rand_uniform(minA, maxA, 0.01f);
    }

    public static float rand_uniform(float minA, float maxA, float diff) {
          // get uniform dist from minA to maxA in steps differences
          float steps = 1 / diff;
          return (rnd.nextInt((int)((maxA - minA) * steps + 1)) + minA * steps) / steps;
    }

    public static String spellout_number(float num, Locale locale) {
          // conv 128.34 -> one hundred twenty eight point three four, etc.
          BigDecimal bd = new BigDecimal(Float.toString(num));
          NumberFormat fmtr = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
          return WordUtils.capitalizeFully(fmtr.format(bd));
    }

    public static String ordinalize_number(int num, Locale locale) {
          // conv 1 -> 1st, 2 -> 2nd, etc.
          BigDecimal bd = new BigDecimal(Integer.toString(num));
          NumberFormat fmtr = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.ORDINAL);
          return fmtr.format(bd);
    }

    public static String getResourceFullPath(Object classObj, String resourcePath) throws Exception {
          // note getResource returns URL with %20 for spaces etc,
          // so it must be converted to URI that gives a working path with %20 convereted to ' '
          URI resourceUri = new URI(classObj.getClass().getClassLoader().getResource(resourcePath).getFile());
          return resourceUri.getPath();
    }

    /* ////////////////////////////  Colors  ////////////////////////////*/

    public static Color getRandomGrayishColor() throws Exception {
        final List<Color> colorsList = Arrays.asList(
            new Color(220,220,220), // gainsboro
            new Color(211,211,211), // lightgray
            new Color(192,192,192), // silver
            new Color(169,169,169)  // darkgray
        );
        return colorsList.get(rnd.nextInt(colorsList.size()));
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

    /* ////////////////////////////   Font   //////////////////////////// */

    /* When rendering non ANSI text, the useANSIEncoding must be set to false when using SimpleTextBox */
    public static PDCustomFonts getNonANSIRandomPDFontFamily(PDDocument doc, Object classObj) throws Exception {
        final List<List<String>> ttfFontNBIList = Arrays.asList(
                Arrays.asList("A_Nefel_Sereke.ttf", "A_Nefel_Sereke_Bold.ttf", "A_Nefel_Sereke.ttf")
        );

        List<PDFont> pdfontNormalList = new ArrayList<PDFont>();
        List<PDFont> pdfontBoldList = new ArrayList<PDFont>();
        List<PDFont> pdfontItalicList = new ArrayList<PDFont>();
        for (List<String> fontNBI : ttfFontNBIList) {
            pdfontNormalList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(0)))));
            pdfontBoldList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(1)))));
            pdfontItalicList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(2)))));
        }
        assert pdfontNormalList.size() == pdfontBoldList.size() && pdfontNormalList.size() == pdfontItalicList.size();

        int fontIdx = rnd.nextInt(pdfontNormalList.size());
        return new PDCustomFonts(pdfontNormalList.get(fontIdx),
                                 pdfontBoldList.get(fontIdx),
                                 pdfontItalicList.get(fontIdx));
    }

    public static PDCustomFonts getRandomPDFontFamily(PDDocument doc, Object classObj) throws Exception {
          // IMPORTANT: fonts must be arranged in normal, bold and italic parts

          final List<List<String>> ttfFontNBIList = Arrays.asList(
                  Arrays.asList("Arial.ttf", "Arial Bold.ttf", "Arial Italic.ttf"),
                  Arrays.asList("Baskerville.ttf", "Baskerville Bold.ttf", "Baskerville Italic.ttf"),
                  Arrays.asList("Century Gothic.ttf", "Century Gothic Bold.ttf", "Century Gothic Italic.ttf"),
                  Arrays.asList("Futura.ttf", "Futura Bold.ttf", "Futura Italic.ttf"),
                  Arrays.asList("Georgia.ttf", "Georgia Bold.ttf", "Georgia Italic.ttf"),
                  Arrays.asList("Optima.ttf", "Optima Bold.ttf", "Optima Italic.ttf"),
                  Arrays.asList("Palatino.ttf", "Palatino Bold.ttf", "Palatino Italic.ttf"),
                  Arrays.asList("Univers.ttf", "UniversBlack.ttf", "Univers Italic.ttf"),
                  Arrays.asList("Verdana.ttf", "Verdana Bold.ttf", "Verdana Italic.ttf"),
                  Arrays.asList("bookman old style.ttf", "bookman old style Bold.ttf", "bookman old style Italic.ttf")
          );

          List<PDFont> pdfontNormalList = new ArrayList<PDFont>();
          List<PDFont> pdfontBoldList = new ArrayList<PDFont>();
          List<PDFont> pdfontItalicList = new ArrayList<PDFont>();
          for (List<String> fontNBI : ttfFontNBIList) {
              pdfontNormalList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(0)))));
              pdfontBoldList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(1)))));
              pdfontItalicList.add(PDType0Font.load(doc, new File(HelperCommon.getResourceFullPath(classObj, "common/font/" + fontNBI.get(2)))));
          }

          // add PDType1Font built-in fonts
          pdfontNormalList.add(PDType1Font.HELVETICA);
          pdfontBoldList.add(PDType1Font.HELVETICA_BOLD);
          pdfontItalicList.add(PDType1Font.HELVETICA_OBLIQUE);
          pdfontNormalList.add(PDType1Font.COURIER);
          pdfontBoldList.add(PDType1Font.COURIER_BOLD);
          pdfontItalicList.add(PDType1Font.COURIER_OBLIQUE);
          pdfontNormalList.add(PDType1Font.TIMES_ROMAN);
          pdfontBoldList.add(PDType1Font.TIMES_BOLD);
          pdfontItalicList.add(PDType1Font.TIMES_ITALIC);

          assert pdfontNormalList.size() == pdfontBoldList.size() && pdfontNormalList.size() == pdfontItalicList.size();

          int fontIdx = rnd.nextInt(pdfontNormalList.size());
          return new PDCustomFonts(pdfontNormalList.get(fontIdx),
                                   pdfontBoldList.get(fontIdx),
                                   pdfontItalicList.get(fontIdx));
    }
}
