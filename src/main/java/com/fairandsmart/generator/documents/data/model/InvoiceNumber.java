package com.fairandsmart.generator.documents.data.model;

import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.mifmif.common.regex.Generex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class InvoiceNumber {

    private String labelInvoice;
    private String valueInvoice;
    private String labelOrder;
    private String valueOrder;
    private String labelClient;
    private String valueClient;
    private String valueBarcode;

    public InvoiceNumber(String labelInvoice, String valueInvoice, String labelOrder, String valueOrder, String labelClient, String valueClient, String valueBarcode) {
        this.labelInvoice = labelInvoice;
        this.valueInvoice = valueInvoice;
        this.labelOrder = labelOrder;
        this.valueOrder = valueOrder;
        this.labelClient = labelClient;
        this.valueClient = valueClient;
        this.valueBarcode = valueBarcode;
    }

    public String getLabelInvoice() {
        return labelInvoice;
    }

    public void setLabelInvoice(String labelInvoice) {
        this.labelInvoice = labelInvoice;
    }

    public String getValueInvoice() {
        return valueInvoice;
    }

    public void setValueInvoice(String valueInvoice) {
        this.valueInvoice = valueInvoice;
    }

    public String getLabelOrder() {
        return labelOrder;
    }

    public void setLabelOrder(String labelOrder) {
        this.labelOrder = labelOrder;
    }

    public String getValueOrder() {
        return valueOrder;
    }

    public void setValueOrder(String valueOrder) {
        this.valueOrder = valueOrder;
    }

    public String getLabelClient() {
        return labelClient;
    }

    public void setLabelClient(String labelClient) {
        this.labelClient = labelClient;
    }

    public String getValueClient() {
        return valueClient;
    }

    public void setValueClient(String valueClient) {
        this.valueClient = valueClient;
    }

    public String getValueBarcode() {
        return valueBarcode;
    }

    public void setValueBarcode(String valueBarcode) {
        this.valueBarcode = valueBarcode;
    }

    @Override
    public String toString() {
        return "InvoiceNumber{" +
                "labelInvoice='" + labelInvoice + '\'' +
                ", valueInvoice='" + valueInvoice + '\'' +
                ", labelOrder='" + labelOrder + '\'' +
                ", valueOrder='" + valueOrder + '\'' +
                ", labelClient='" + labelClient + '\'' +
                ", valueClient='" + valueClient + '\'' +
                ", valueBarcode='" + valueBarcode + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<InvoiceNumber> {

        private static final List<String> valuesInvoice = new ArrayList<>();
        private static final List<String> valuesOrder = new ArrayList<>();
        private static final List<String> valuesClient = new ArrayList<>();
        private static final List<String> valuesBarcode = new ArrayList<>();
        private static final Map<String, String> labelsInvoice = new LinkedHashMap<>();
        private static final Map<String, String> labelsOrder = new LinkedHashMap<>();
        private static final Map<String, String> labelsClient = new LinkedHashMap<>();
        {
            valuesInvoice.add("[A-D][H-N]-[A-Z]{2}-[0-9]{6}-[0-9]{2}");
            valuesInvoice.add("[0-9]{3}-[0-9]{5}-1[0-9]{4}");
            valuesInvoice.add("1[0-9]{6}");
            valuesInvoice.add("[3-7][0-9]{7}");
            valuesInvoice.add("[4-9][0-9]{9}");
            valuesInvoice.add("FV201[0-7]00[0-9]{4}");
            valuesInvoice.add("00[0-9]{5}");
            valuesInvoice.add("FC500[0-9]{3}");
            valuesInvoice.add("INV-[0-9]{4}");
            valuesInvoice.add("[0-9]{4}-7[0-9]{5}");
        }
        {
            valuesOrder.add("[A-D][H-N]-[A-Z]{2}-[0-9]{2}-[0-9]{2}");
            valuesOrder.add("[0-9]{3}-[0-9]{4}");
            valuesOrder.add("2[0-9]{6}");
            valuesOrder.add("[1-2][0-9]{6}");
            valuesOrder.add("[4-9][0-9]{3}");
            valuesOrder.add("CD201[0-7]00[0-9]{5}");
            valuesOrder.add("99[0-9]{5}");
            valuesOrder.add("CM5[0-9]{3}");
            valuesOrder.add("COM-[0-9]{5}");
            valuesOrder.add("[0-9]{3}-9[0-9]{2}");
        }
        {
            valuesClient.add("[A-D][H-N]-[A-Z]{2}-[0-9]{3}-[0-9]{2}");
            valuesClient.add("[0-9]{3}-[0-9]{3}-1[0-9]{2}");
            valuesClient.add("7[0-9]{4}");
            valuesClient.add("[1-2][0-9]{4}");
            valuesClient.add("[4-9][0-9]{4}");
            valuesClient.add("CL10[0-7]00[0-9]{2}");
            valuesClient.add("00[0-9]{4}");
            valuesClient.add("CL55[0-9]{3}");
            valuesClient.add("CL-[0-9]{4}");
            valuesClient.add("[0-9]{3}-0[0-9]{3}");
        }
        {
            valuesBarcode.add("[0-9]{12}");
        }
        {
            labelsInvoice.put("Invoice Number", "en");
            labelsInvoice.put("Invoice No.", "en");
            labelsInvoice.put("Invoice ID", "en");
            labelsInvoice.put("Invoice Reference", "en");
            labelsInvoice.put("Tax Invoice Number", "en");
            labelsInvoice.put("Tax Invoice No.", "en");
            labelsInvoice.put("Tax Invoice ID", "en");
            labelsInvoice.put("Tax Invoice Reference", "en");
            labelsInvoice.put("Tax Inv. No.", "en");
            labelsInvoice.put("Tax Inv. Reference", "en");

            labelsInvoice.put("Numéro de facture", "fr");
            labelsInvoice.put("N° facture", "fr");
            labelsInvoice.put("N° de facture", "fr");
            labelsInvoice.put("FACTURE N°", "fr");
            labelsInvoice.put("Facture n°", "fr");
            labelsInvoice.put("Facture-n°", "fr");
            labelsInvoice.put("FACTURE No", "fr");
            labelsInvoice.put("Référence de la facture", "fr");
        }
        {
            labelsOrder.put("Order Number", "en");
            labelsOrder.put("Order No.", "en");
            labelsOrder.put("Order ID", "en");
            labelsOrder.put("Order Reference", "en");
            labelsOrder.put("Purchase Order No", "en");
            labelsOrder.put("PO No", "en");
            labelsOrder.put("LPO No", "en");
            labelsOrder.put("L.P.O.", "en");

            labelsOrder.put("Numéro de commande", "fr");
            labelsOrder.put("N° commande", "fr");
            labelsOrder.put("N° de commande", "fr");
            labelsOrder.put("COMMANDE N°", "fr");
            labelsOrder.put("Commande n°", "fr");
            labelsOrder.put("Commande-n°", "fr");
            labelsOrder.put("COMMANDE No", "fr");
            labelsOrder.put("Référence de la commande", "fr");
        }
        {
            labelsClient.put("Client Number", "en");
            labelsClient.put("Client No.", "en");
            labelsClient.put("Client ID", "en");
            labelsClient.put("Client Reference", "en");
            labelsClient.put("Client Ref", "en");
            labelsClient.put("Ref", "en");

            labelsClient.put("Numéro de client", "fr");
            labelsClient.put("N° client", "fr");
            labelsClient.put("N° de client", "fr");
            labelsClient.put("CLIENT N°", "fr");
            labelsClient.put("Client n°", "fr");
            labelsClient.put("Client-n°", "fr");
            labelsClient.put("CLIENT No", "fr");
            labelsClient.put("Référence de client", "fr");
        }

        @Override
        public InvoiceNumber generate(GenerationContext ctx) {

            String generatedInvoiceFmt = new Generex(valuesInvoice.get(ctx.getRandom().nextInt(valuesInvoice.size()))).random();
            String generatedOrderFmt = new Generex(valuesOrder.get(ctx.getRandom().nextInt(valuesOrder.size()))).random();
            String generatedClientFmt = new Generex(valuesClient.get(ctx.getRandom().nextInt(valuesClient.size()))).random();
            String generatedBarcodeFmt = new Generex(valuesBarcode.get(ctx.getRandom().nextInt(valuesBarcode.size()))).random();

            List<String> filteredLabelsInvoice = labelsInvoice.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsOrder = labelsOrder.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsClient = labelsClient.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            return new InvoiceNumber(filteredLabelsInvoice.get(ctx.getRandom().nextInt(filteredLabelsInvoice.size())),
                                     generatedInvoiceFmt,
                                     filteredLabelsOrder.get(ctx.getRandom().nextInt(filteredLabelsOrder.size())),
                                     generatedOrderFmt,
                                     filteredLabelsClient.get(ctx.getRandom().nextInt(filteredLabelsClient.size())),
                                     generatedClientFmt,
                                     generatedBarcodeFmt);
        }
    }
}
