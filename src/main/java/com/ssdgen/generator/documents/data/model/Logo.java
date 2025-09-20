package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Logo {

    private String fullPath;
    private String name;
    private List<Integer> themeRGB;

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

    public List<Integer> getThemeRGB() {
        return themeRGB;
    }

    public void setThemeRGB(List<Integer> themeRGB) {
        this.themeRGB = themeRGB;
    }

    @Override
    public String toString() {
        return "Logo{" +
                "fullPath='" + fullPath + '\'' +
                ", name='" + name + '\'' +
                ", themeRGB='" + themeRGB + '\'' +
                '}';
    }

    public static List<Logo> getLogoList() {
        final List<String> logosFileList = Arrays.asList(
                "common/logo/ae_en/metadata.json",
                "common/logo/fr/metadata.json");
        List<Logo> logos = new ArrayList<Logo>();
        {
            for (String brandFile : logosFileList) {
                Reader jsonReader = new InputStreamReader(Logo.class.getClassLoader().getResourceAsStream(brandFile));
                Gson gson = new Gson();
                Type collectionType = new TypeToken<List<Logo>>() {
                }.getType();
                logos.addAll(gson.fromJson(jsonReader, collectionType));
            }
        }
        return logos;
    }

    public static class Generator implements ModelGenerator<Logo> {

        private static final List<Logo> logos = getLogoList();

        @Override
        public Logo generate(GenerationContext ctx) {
            Logo electibleLogo;
            // filter by brandname, default is .* so use all logos and then filter by
            // country
            List<Logo> electibleLogos = logos.stream().filter(logo -> logo.name.matches(ctx.getBrandName()) &&
                    logo.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)")).collect(Collectors.toList());
            if (electibleLogos.size() > 0) {
                electibleLogo = electibleLogos.get(ctx.getRandom().nextInt(electibleLogos.size()));
            } else {
                electibleLogo = logos.get(ctx.getRandom().nextInt(logos.size()));
            }
            return electibleLogo;
        }
    }

    public Logo(String fullPath, String name, List<Integer> themeRGB) {
        this.fullPath = fullPath;
        this.name = name;
        this.themeRGB = themeRGB;
    }

    public Logo(GenerationContext ctx, String companyName) {
        List<Logo> logos = getLogoList();
        Logo electibleLogo;

        // filter by brandname (default: .* so use all logos) and by country and the
        // company name
        List<Logo> electibleLogos = logos.stream().filter(logo -> logo.name.matches(ctx.getBrandName()) &&
                logo.fullPath.matches(ctx.getCountry().toLowerCase() + "(.*)") &&
                logo.name.matches(companyName + "(.*)")).collect(Collectors.toList());

        if (electibleLogos.size() > 0) {
            electibleLogo = electibleLogos.get(ctx.getRandom().nextInt(electibleLogos.size()));
        } else {
            // if no mataches then use a random logo
            electibleLogo = logos.get(ctx.getRandom().nextInt(logos.size()));
        }
        this.fullPath = electibleLogo.fullPath;
        this.name = electibleLogo.name;
        this.themeRGB = electibleLogo.themeRGB;
    }

}
