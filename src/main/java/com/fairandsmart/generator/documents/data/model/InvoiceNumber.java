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

    public InvoiceNumber(String labelInvoice, String valueInvoice, String labelOrder, String valueOrder, String labelClient, String valueClient) {
        this.labelInvoice = labelInvoice;
        this.valueInvoice = valueInvoice;
        this.labelOrder = labelOrder;
        this.valueOrder = valueOrder;
        this.labelClient = labelClient;
        this.valueClient = valueClient;
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

    @Override
    public String toString() {
        return "InvoiceNumber{" +
                "labelInvoice='" + labelInvoice + '\'' +
                ", valueInvoice='" + valueInvoice + '\'' +
                ", labelOrder='" + labelOrder + '\'' +
                ", valueOrder='" + valueOrder + '\'' +
                ", labelClient='" + labelClient + '\'' +
                ", valueClient='" + valueClient + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<InvoiceNumber> {

        private static final List<String> formatsInvoice = new ArrayList<>();
        private static final List<String> formatsOrder = new ArrayList<>();
        private static final List<String> formatsClient = new ArrayList<>();
        private static final Map<String, String> labelsInvoice = new LinkedHashMap<>();
        private static final Map<String, String> labelsOrder = new LinkedHashMap<>();
        private static final Map<String, String> labelsClient = new LinkedHashMap<>();
        {
            formatsInvoice.add("[A-D][H-N]-[A-Z]{2}-[0-9]{6}-[0-9]{2}");
            formatsInvoice.add("[0-9]{3}-[0-9]{5}-1[0-9]{4}");
            formatsInvoice.add("1[0-9]{6}");
            formatsInvoice.add("[3-7][0-9]{7}");
            formatsInvoice.add("[4-9][0-9]{9}");
            formatsInvoice.add("FV201[0-7]00[0-9]{4}");
            formatsInvoice.add("00[0-9]{5}");
            formatsInvoice.add("FC500[0-9]{3}");
            formatsInvoice.add("INV-[0-9]{4}");
            formatsInvoice.add("[0-9]{4}-7[0-9]{5}");
        }
        {
            formatsOrder.add("[A-D][H-N]-[A-Z]{2}-[0-9]{2}-[0-9]{2}");
            formatsOrder.add("[0-9]{3}-[0-9]{4}");
            formatsOrder.add("2[0-9]{6}");
            formatsOrder.add("[1-2][0-9]{6}");
            formatsOrder.add("[4-9][0-9]{3}");
            formatsOrder.add("CD201[0-7]00[0-9]{5}");
            formatsOrder.add("99[0-9]{5}");
            formatsOrder.add("CM5[0-9]{3}");
            formatsOrder.add("COM-[0-9]{5}");
            formatsOrder.add("[0-9]{3}-9[0-9]{2}");
        }
        {
            formatsClient.add("[A-D][H-N]-[A-Z]{2}-[0-9]{3}-[0-9]{2}");
            formatsClient.add("[0-9]{3}-[0-9]{3}-1[0-9]{2}");
            formatsClient.add("7[0-9]{4}");
            formatsClient.add("[1-2][0-9]{4}");
            formatsClient.add("[4-9][0-9]{4}");
            formatsClient.add("CL10[0-7]00[0-9]{2}");
            formatsClient.add("00[0-9]{4}");
            formatsClient.add("CL55[0-9]{3}");
            formatsClient.add("CL-[0-9]{4}");
            formatsClient.add("[0-9]{3}-0[0-9]{3}");
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
            labelsClient.put("Reference", "en");
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

            Generex generex;
            generex = new Generex(formatsInvoice.get(ctx.getRandom().nextInt(formatsInvoice.size())));
            String generatedInvoiceFmt = generex.random();

            generex = new Generex(formatsOrder.get(ctx.getRandom().nextInt(formatsOrder.size())));
            String generatedOrderFmt = generex.random();

            generex = new Generex(formatsClient.get(ctx.getRandom().nextInt(formatsClient.size())));
            String generatedClientFmt = generex.random();

            List<String> localizedLabelsInvoice = labelsInvoice.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsOrder = labelsOrder.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsClient = labelsClient.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            return new InvoiceNumber(localizedLabelsInvoice.get(ctx.getRandom().nextInt(localizedLabelsInvoice.size())),
                                     generatedInvoiceFmt,
                                     localizedLabelsOrder.get(ctx.getRandom().nextInt(localizedLabelsOrder.size())),
                                     generatedOrderFmt,
                                     localizedLabelsClient.get(ctx.getRandom().nextInt(localizedLabelsClient.size())),
                                     generatedClientFmt);
        }
    }
}
