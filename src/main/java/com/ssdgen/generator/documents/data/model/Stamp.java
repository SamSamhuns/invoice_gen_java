package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
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
                Type collectionType = new TypeToken<List<Stamp>>() {
                }.getType();
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
            // filter by brandname, default is .* so use all stamps and then filter by
            // country
            List<Stamp> electibleStamps = stamps.stream().filter(stamp -> stamp.name.matches(ctx.getBrandName()) &&
                    stamp.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)")).collect(Collectors.toList());
            if (electibleStamps.size() > 0) {
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

        // filter by brandname (default: .* so use all stamps) and by country and the
        // company name
        List<Stamp> electibleStamps = stamps.stream().filter(stamp -> stamp.name.matches(ctx.getBrandName()) &&
                stamp.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)") &&
                stamp.name.matches(companyNameMod + "(.*)")).collect(Collectors.toList());

        if (electibleStamps.size() > 0) {
            electibleStamp = electibleStamps.get(ctx.getRandom().nextInt(electibleStamps.size()));
        } else {
            // if no mataches then use a random stamp
            electibleStamp = stamps.get(ctx.getRandom().nextInt(stamps.size()));
        }
        this.fullPath = electibleStamp.fullPath;
        this.name = electibleStamp.name;
    }

}
