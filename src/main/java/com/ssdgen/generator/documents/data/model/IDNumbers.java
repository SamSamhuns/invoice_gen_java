package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.mifmif.common.regex.Generex;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IDNumbers {

    // specific to french invoices
    private final String cidLabel; // Company ID (like Siren Number)
    private final String cidValue;
    private final String siretLabel; // Particular to french invoice
    private final String siretValue; // 14 digits unique company center id Siren + Nic(5 digits)
    private final String toaLabel; // Type of activity eg: APE code / NAF Code for an enterprise
    private final String toaValue;
    // for all invoices
    private String vatLabel;
    private String vatValue;

    public IDNumbers(String vatValue, String vatLabel,
            String cidValue, String cidLabel,
            String siretValue, String siretLabel,
            String toaValue, String toaLabel) {
        this.vatValue = vatValue;
        this.vatLabel = vatLabel;
        this.cidLabel = cidLabel;
        this.cidValue = cidValue;
        this.siretLabel = siretLabel;
        this.siretValue = siretValue;
        this.toaLabel = toaLabel;
        this.toaValue = toaValue;
    }

    public String getVatLabel() {
        return vatLabel;
    }

    public void setLabel(String vatLabel) {
        this.vatLabel = vatLabel;
    }

    public String getVatValue() {
        return vatValue;
    }

    public void setVatValue(String vatValue) {
        this.vatValue = vatValue;
    }

    public String getCidValue() {
        return cidValue;
    }

    public String getCidLabel() {
        return cidLabel;
    }

    public String getSiretValue() {
        return siretValue;
    }

    public String getSiretLabel() {
        return siretLabel;
    }

    public String getToaValue() {
        return toaValue;
    }

    public String getToaLabel() {
        return toaLabel;
    }

    @Override
    public String toString() {
        return "IDNumbers{" +
                "vatValue='" + vatValue + '\'' +
                ", vatLabel='" + vatLabel + '\'' +
                ", cidValue='" + cidValue + '\'' +
                ", cidLabel='" + cidLabel + '\'' +
                ", siretValue='" + siretValue + '\'' +
                ", siretLabel='" + siretLabel + '\'' +
                ", toaValue='" + toaValue + '\'' +
                ", toaLabel='" + toaLabel + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<IDNumbers> {

        private static final Map<String, String> vatValues = new HashMap<>();
        private static final Map<String, String> vatLabels = new HashMap<>();
        private static final Map<String, String> cidLabels = new HashMap<>();
        private static final Map<String, String> siretLabels = new HashMap<>();
        private static final Map<String, String> toaLabels = new HashMap<>();

        {
            // specific to french invoices
            siretLabels.put("Siret", "fr");
            siretLabels.put("N° Siret", "fr");

            siretLabels.put("Siret", "en");
        }
        {
            // specific to french invoices
            toaLabels.put("Code APE", "fr");
            toaLabels.put("NAF", "fr");
            toaLabels.put("N.A.F.", "fr");

            toaLabels.put("APE", "en");
            toaLabels.put("NAF", "en");
        }
        {
            // specific to french invoices
            cidLabels.put("Siren", "fr");
            cidLabels.put("N° Siren", "fr");

            cidLabels.put("Siren", "en");
        }
        {
            vatLabels.put("Numéro de TVA", "fr");
            vatLabels.put("N° TVA Intracommunautaire", "fr");
            vatLabels.put("TVA intracommunautaire", "fr");
            vatLabels.put("N° intracommunautaire", "fr");
            vatLabels.put("TVA Intracomm.", "fr");
            vatLabels.put("No identif. Intracomm.", "fr");
            vatLabels.put("N° Identification TVA", "fr");
            vatLabels.put("TVA numéro ", "fr");
            vatLabels.put("N° TVA Intracom", "fr");
            vatLabels.put("N° TVA", "fr");

            vatLabels.put("(Tax|TAX|Vat|VAT|TRN|TIN) (No\\.|No|Number)", "en");
        }
        {
            vatValues.put("[0-9]{15}", "AE_en");
            vatValues.put("[0-9]{10}", "AE_en");
            vatValues.put("100[0-9]{13}", "AE_en");
            vatValues.put("UAE[0-9]{14}", "AE_en");

            vatValues.put("[0-9]{10}", "US");

            vatValues.put("FR[0-9]{8}", "FR");

            vatValues.put("LU[0-9]{8}", "LU");

            vatValues.put("GB[0-9]{9}", "UK");
            vatValues.put("GB[0-9]{9} [0-9]{3}", "UK");
            vatValues.put("GB[0-9]{3} [0-9]{4} [0-9]{2}", "UK");

            vatValues.put("BE[0-9]{10}", "BE");
        }

        @Override
        public IDNumbers generate(GenerationContext ctx) {

            String cidValue = "", siretValue = "", vatValue = "", toaValue = "";
            if ("FR" != ctx.getCountry().intern()) {
                List<String> filteredVatValue = vatValues.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                vatValue = new Generex(filteredVatValue.get(ctx.getRandom().nextInt(filteredVatValue.size()))).random();
            } else {
                // French Specific System
                String space = "";
                Random random = new Random();
                int random_space = random.nextInt(2);
                if (random_space == 1) {
                    space = " ";
                }

                Generex gensiren = new Generex("[0-9]{9}"); // For Siren Number
                cidValue = gensiren.random();
                int siren = Integer.parseInt(cidValue);
                String[] cidVal = cidValue.split("(?<=\\G...)"); // For breaking string at every 3rd position
                cidValue = cidVal[0] + space + cidVal[1] + space + cidVal[2];

                Generex gennic = new Generex("[0-9]{5}");
                siretValue = cidValue + space + gennic.random();

                int key = (12 + 3 * (siren % 97)) % 97; // To calculate key for VAT from SIREN
                vatValue = "FR" + space + key + space + cidValue; // VAT number

                try {
                    toaValue = getToacode();
                } catch (IOException e) {
                    System.out.println("File Reading Exception");
                }
            }

            List<String> filteredvatLabel = vatLabels.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            String vatLabel = new Generex(filteredvatLabel.get(ctx.getRandom().nextInt(filteredvatLabel.size())))
                    .random();

            List<String> filteredcidLabel = cidLabels.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            String cidLabel = new Generex(filteredcidLabel.get(ctx.getRandom().nextInt(filteredcidLabel.size())))
                    .random();

            List<String> filteredsiretLabel = siretLabels.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            String siretLabel = new Generex(filteredsiretLabel.get(ctx.getRandom().nextInt(filteredsiretLabel.size())))
                    .random();

            List<String> filteredtoaLabel = toaLabels.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            String toaLabel = new Generex(filteredtoaLabel.get(ctx.getRandom().nextInt(filteredtoaLabel.size())))
                    .random();

            return new IDNumbers(vatValue, vatLabel, cidValue, cidLabel, siretValue, siretLabel, toaValue, toaLabel);
        }

        private String getToacode() throws IOException {
            FileReader fileReader = null;
            BufferedReader reader = null;
            String lineIn = null;
            Random random = new Random();
            int random_line = random.nextInt(1267 - 1 + 1) + 1; // 1267 lines in file, each containg an ape code
            try {
                String codePath = this.getClass().getClassLoader().getResource("common/apecodes.txt").getFile();
                fileReader = new FileReader(codePath);
                reader = new BufferedReader(fileReader);
                int i = 1;
                while (((lineIn = reader.readLine()) != null) && i != random_line) {
                    i++;
                }

            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Cannot read from apecodes.txt file");
            } finally {
                if (lineIn != null)
                    reader.close();
            }
            return lineIn;
        }
    }
}
