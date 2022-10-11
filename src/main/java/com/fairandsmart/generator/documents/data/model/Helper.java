package com.fairandsmart.generator.documents.data.model;

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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.Map;


public class Helper {

    private static final Random rnd = new Random();

    public static Random getRandom() {
        return rnd;
    }

    public Helper() {
    }

    public static float round(float num, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float rand_uniform(float minA, float maxA) {
        // get uniform dist from minA to maxA in default steps of 0.01
        return Helper.rand_uniform(minA, maxA, 0.01f);
    }

    public static float rand_uniform(float minA, float maxA, float diff) {
        // get uniform dist from minA to maxA in steps differences
        float steps = 1 / diff;
        return (float)(rnd.nextInt((int)((maxA - minA) * steps + 1)) + minA * steps) / steps;
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
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
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
}
