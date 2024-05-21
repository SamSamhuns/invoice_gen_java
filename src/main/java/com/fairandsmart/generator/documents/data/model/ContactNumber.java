package com.fairandsmart.generator.documents.data.model;

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

    public String getPhoneLabel() {
        return phoneLabel;
    }

    public void setPhoneLabel(String phoneLabel) {
        this.phoneLabel = phoneLabel;
    }

    public String getPhoneValue() {
        return phoneValue;
    }

    public void setPhoneValue(String phoneValue) {
        this.phoneValue = phoneValue;
    }

    public String getFaxLabel() {
        return faxLabel;
    }

    public void setFaxLabel(String faxLabel) {
        this.faxLabel = faxLabel;
    }

    public String getFaxValue() {
        return faxValue;
    }

    public void setFaxValue(String faxValue) {
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
            phoneLabels.put("Tel", "fr");
            phoneLabels.put("Téléphone", "fr");
            phoneLabels.put("Numéro de Tel", "fr");

            phoneLabels.put("Tel", "en");
            phoneLabels.put("Phone", "en");
            phoneLabels.put("Telephone", "en");
            phoneLabels.put("Phone No", "en");
            phoneLabels.put("Phone Number", "en");
        }
        {
            faxLabels.put("Fax", "fr");
            faxLabels.put("Télécopie", "fr");
            faxLabels.put("Numéro de Fax", "fr");

            faxLabels.put("Fax", "en");
            faxLabels.put("Fax No", "en");
            faxLabels.put("Fax Number", "en");
        }

        @Override
        public ContactNumber generate(GenerationContext ctx) {
            List<String> phoneFormat = phoneFormats.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> faxFormat = faxFormats.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idx1 = ctx.getRandom().nextInt(phoneFormat.size());
            int idx2 = ctx.getRandom().nextInt(faxFormat.size());
            String phoneNumber = new Generex(phoneFormat.get(idx1)).random();
            String faxNumber = new Generex(faxFormat.get(idx2)).random();

            List<String> locPLabels = phoneLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> locFLabels = faxLabels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxL = ctx.getRandom().nextInt(locPLabels.size());
            int idxF = ctx.getRandom().nextInt(locFLabels.size());
            return new ContactNumber(locPLabels.get(idxL), phoneNumber, locFLabels.get(idxF), faxNumber);
        }
    }

}
