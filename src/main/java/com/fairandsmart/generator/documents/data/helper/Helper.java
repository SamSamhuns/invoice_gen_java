package com.fairandsmart.generator.documents.data.helper;

/*-
 * #%L
 * FacoGen / A tool for annotated GEDI based invoice generation.
 *
 *
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

import java.util.Random;


public class Helper {

    protected static final Random rnd = new Random();

    public static Random getRandom() {
        return rnd;
    }

    public Helper() {
    }
}
