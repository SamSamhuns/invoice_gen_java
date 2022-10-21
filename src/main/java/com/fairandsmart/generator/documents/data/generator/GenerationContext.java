package com.fairandsmart.generator.documents.data.generator;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GenerationContext {

    private static final Logger LOGGER = Logger.getLogger(GenerationContext.class.getName());

    private static final List<String> countries = new ArrayList<>();
    private static final List<String> languages = new ArrayList<>();
    private static final List<String> locales = new ArrayList<>();
    private static final List<String> currencies = new ArrayList<>();
    private static final Random rnd = new Random();
    private static List<Map<String, Object>> configMaps;

    {
        // countries.add("FR");
        // countries.add("LU");
        // countries.add("BE");
        // countries.add("UK");
        // countries.add("US_west");
        countries.add("AE_en");
    }
    {
        // languages.add("fr");
        languages.add("en");
    }
    {
        // locales.add("fr");
        // locales.add("en");
        locales.add("en-MS");
    }
    {
        currencies.add("EUR");
        // currencies.add("€");
        currencies.add("USD");
        currencies.add("$");
        // currencies.add("Rs.");
        // currencies.add("₹");
        currencies.add("Yuan");
        currencies.add("¥");
        currencies.add("Dhs");
        currencies.add("AED");
        currencies.add("DH");
        currencies.add("Dh");
        // currencies.add("د.إ");
    }

    private String country;
    private String language;
    private String locale;
    private String languagePayslip;
    private String brandName;
    private String currency;
    private long date;

    public GenerationContext() {
        brandName = ".*";
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Random getRandom() {
        return rnd;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLanguagePayslip() {
        return languagePayslip;
    }

    public void setLanguagePayslip(String languagePayslip) {
        this.languagePayslip = languagePayslip;
    }

    public List<Map<String, Object>> getConfigMaps() {
        return configMaps;
    }

    public void setConfigMaps(List<Map<String, Object>> configMaps) {
        GenerationContext.configMaps = configMaps;
    }

    @Override
    public String toString() {
        return "GenerationContext{" +
                "country='" + country + '\'' +
                ", language='" + language + '\'' +
                ", locale='" + locale + '\'' +
                ", brandName='" + brandName + '\'' +
                ", currency='" + currency + '\'' +
                ", date=" + date +
                '}';
    }

    // load configuration files //
    {
      final String genProbConfigFile = "config/generation_probabilities.json";
      {
          configMaps = new ArrayList<Map<String, Object>>();
          try {
              Reader jsonReader = new InputStreamReader(GenerationContext.class.getClassLoader().getResourceAsStream(genProbConfigFile));
              Gson gson = new Gson();
              Type collectionType = new TypeToken<List<Map<String, Object>>>(){}.getType();
              configMaps.addAll(gson.fromJson(jsonReader, collectionType));
          } catch ( Exception e ) {
              LOGGER.log(Level.SEVERE, "unable to parse json source: " + genProbConfigFile, e);
          }
      }
    }

    public static GenerationContext generate() {
        GenerationContext ctx = new GenerationContext();
        String lang = languages.get(rnd.nextInt(languages.size()));
        String locale = locales.get(rnd.nextInt(locales.size()));
        String country = countries.get(rnd.nextInt(countries.size()));
        String currency = currencies.get(rnd.nextInt(currencies.size()));

        ctx.setCountry(country);
        ctx.setLanguage(lang);
        ctx.setLocale(locale);
        ctx.setLanguagePayslip(lang);
        ctx.setCurrency(currency);
        ctx.setConfigMaps(configMaps);
        return ctx;
    }
}
