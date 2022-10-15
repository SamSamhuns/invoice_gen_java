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

import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Stamp {

    private String fullPath;
    private String name;

    public Stamp(String fullPath, String name) {
        this.fullPath = fullPath;
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Stamp{" +
                "fullPath='" + fullPath + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<Stamp> getStampList() {
        final List<String> stampsFileList = Collections.singletonList(
                "common/stamp/ae_en/metadata.json");
        List<Stamp> stamps = new ArrayList<Stamp>();
        {
          for (String brandFile : stampsFileList) {
              Reader jsonReader = new InputStreamReader(Stamp.class.getClassLoader().getResourceAsStream(brandFile));
              Gson gson = new Gson();
              Type collectionType = new TypeToken<List<Stamp>>(){}.getType();
              stamps.addAll(gson.fromJson(jsonReader, collectionType));
          }
        }
        return stamps;
    }

    public static class Generator implements ModelGenerator<Stamp> {

        private static final List<Stamp> stamps = getStampList();

        @Override
        public Stamp generate(GenerationContext ctx) {
            Stamp electibleStamp;
            // filter by brandname, default is .* so use all stamps and then filter by country
            List<Stamp> electibleStamps = stamps.stream().filter(stamp ->
                    stamp.name.matches(ctx.getBrandName()) &&
                    stamp.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)")
                    ).collect(Collectors.toList());
            if ( electibleStamps.size() > 0 ) {
                electibleStamp = electibleStamps.get(ctx.getRandom().nextInt(electibleStamps.size()));
            } else {
                electibleStamp = stamps.get(ctx.getRandom().nextInt(stamps.size()));
            }
            return electibleStamp;
        }
    }

    public Stamp(GenerationContext ctx, String companyName) {
        List<Stamp> stamps = getStampList();
        Stamp electibleStamp;
        final String companyNameMod = companyName.replace(' ', '_').replace(".", "").replace(",", "");

        // filter by brandname (default: .* so use all stamps) and by country and the company name
        List<Stamp> electibleStamps = stamps.stream().filter(stamp ->
                stamp.name.matches(ctx.getBrandName()) &&
                stamp.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)") &&
                stamp.name.matches(companyNameMod + "(.*)")
                ).collect(Collectors.toList());

        if ( electibleStamps.size() > 0 ) {
            electibleStamp = electibleStamps.get(ctx.getRandom().nextInt(electibleStamps.size()));
        } else {
            // if no mataches then use a random stamp
            electibleStamp = stamps.get(ctx.getRandom().nextInt(stamps.size()));
        }
        this.fullPath = electibleStamp.fullPath;
        this.name = electibleStamp.name;
    }

}
