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
import com.mifmif.common.regex.Generex;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContactNumber {

    private String phoneLabel;
    private String phoneValue;
    private String faxLabel;
    private String faxValue;

    public ContactNumber(String phoneLabel, String phoneValue, String faxLabel, String faxValue) {
        this.phoneLabel = phoneLabel;
        this.phoneValue = phoneValue;
        this.faxLabel = faxLabel;
        this.faxValue = faxValue;
    }

    public String getphoneLabel() {
        return phoneLabel;
    }

    public void setphoneLabel(String phoneLabel) {
        this.phoneLabel = phoneLabel;
    }

    public String getphoneValue() {
        return phoneValue;
    }

    public void setphoneValue(String phoneValue) {
        this.phoneValue = phoneValue;
    }

    public String getfaxLabel() {
        return faxLabel;
    }

    public void setfaxLabel(String faxLabel) {
        this.faxLabel = faxLabel;
    }

    public String getfaxValue() {
        return faxValue;
    }

    public void setfaxValue(String faxValue) {
        this.faxValue = faxValue;
    }

    @Override
    public String toString() {
        return "ContactNumber{" +
                "phoneLabel='" + phoneLabel + '\'' +
                ", phoneValue='" + phoneValue + '\'' +
                "faxLabel='" + faxLabel + '\'' +
                ", faxValue='" + faxValue + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<ContactNumber> {

        private static final Map<String, String> phoneFormats = new LinkedHashMap<>();
        private static final Map<String, String> faxFormats = new LinkedHashMap<>();
        private static final Map<String, String> phoneLabels = new LinkedHashMap<>();
        private static final Map<String, String> faxLabels = new LinkedHashMap<>();

        {
            phoneFormats.put("0[0-9]{1}[.][0-9]{2}[.][0-9]{2}[.]", "FR");
            phoneFormats.put("0[0-9]{1}[-][0-9]{2}[-][0-9]{2}[-]", "FR");
            phoneFormats.put("+33 (0) [0-9]{3} [0-9]{3} ", "FR");
            phoneFormats.put("+33 [(]0[)] [0-9]{1} [0-9]{2} [0-9]{2} ", "FR");

            phoneFormats.put("+1 [(]0[)] [0-9]{1} [0-9]{2} [0-9]{2} ", "US_west");

            phoneFormats.put("+971 [(]0[)] [0-9]{2} [0-9]{3} [0-9]{4} ", "AE_en");
            phoneFormats.put("+971 0[0-9]{2} [0-9]{3} [0-9]{4} ", "AE_en");
        }
        {
            faxFormats.put("[0-9]{2}[.][0-9]{2}", "FR");
            faxFormats.put("[0-9]{2}[-][0-9]{2}", "FR");
            faxFormats.put("[0-9]{3}", "FR");
            faxFormats.put("[0-9]{2} [0-9]{2}", "FR");

            faxFormats.put("[0-9]{2} [0-9]{2}", "US_west");

            faxFormats.put("[0-9]{5} [0-9]{4}", "AE_en");
        }
        {
            phoneLabels.put("Tel:", "fr");
            phoneLabels.put("Téléphone", "fr");
            phoneLabels.put("Numéro de Tel", "fr");

            phoneLabels.put("Phone", "en");
            phoneLabels.put("Tel", "en");
            phoneLabels.put("Telephone", "en");
        }
        {
            faxLabels.put("Fax:", "fr");
            faxLabels.put("Télécopie", "fr");
            faxLabels.put("Numéro de Fax", "fr");

            faxLabels.put("Fax", "en");
            faxLabels.put("Fax Number", "en");
        }

        @Override
        public ContactNumber generate(GenerationContext ctx) {
            List<String> phoneFormat = phoneFormats.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> faxFormat = faxFormats.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idx1 = ctx.getRandom().nextInt(phoneFormat.size());
            int idx2 = ctx.getRandom().nextInt(faxFormat.size());
            Generex generex1 = new Generex(phoneFormat.get(idx1));
            Generex generex2 = new Generex(faxFormat.get(idx2));
            String phoneNumber = generex1.random();
            String faxNumber = generex2.random();

            List<String> locPLabels = phoneLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> locFLabels = faxLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxL = ctx.getRandom().nextInt(locPLabels.size());
            int idxF = ctx.getRandom().nextInt(locFLabels.size());
            return new ContactNumber(locPLabels.get(idxL), phoneNumber, locFLabels.get(idxF), faxNumber);
        }
    }

}
