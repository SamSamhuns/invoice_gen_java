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
import java.math.BigDecimal;
import java.util.Random;


public class Helper {

    private static Random rnd = new Random();

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
        float steps = (float)(1 / diff);
        return (float)(rnd.nextInt((int)((maxA - minA) * steps + 1)) + minA * steps) / steps;
    }
}
