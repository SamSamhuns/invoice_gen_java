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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoiceDate {

    private String labelInvoice;
    private String valueInvoice;
    private String labelOrder;
    private String valueOrder;
    private String labelShipping;
    private String valueShipping;
    private String labelPayment;
    private String valuePayment;


    public InvoiceDate(String labelInvoice, String valueInvoice, String labelOrder, String valueOrder, String labelShipping, String valueShipping, String labelPayment, String valuePayment
    ) {
        this.labelInvoice = labelInvoice;
        this.valueInvoice = valueInvoice;
        this.labelOrder = labelOrder;
        this.valueOrder = valueOrder;
        this.labelShipping = labelShipping;
        this.valueShipping = valueShipping;
        this.labelPayment = labelPayment;
        this.valuePayment = valuePayment;
    }

    public String getValueInvoice() {
        return valueInvoice;
    }

    public void setValueInvoice(String valueInvoice) {
        this.valueInvoice = valueInvoice;
    }

    public String getLabelInvoice() {
        return labelInvoice;
    }

    public void setLabelInvoice(String labelInvoice) {
        this.labelInvoice = labelInvoice;
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

    public String getLabelShipping() {
        return labelShipping;
    }

    public void setLabelShipping(String labelShipping) {
        this.labelShipping = labelShipping;
    }

    public String getValueShipping() {
        return valueShipping;
    }

    public void setValueShipping(String valueShipping) {
        this.valueShipping = valueShipping;
    }

    public String getLabelPayment() {
        return labelPayment;
    }

    public void setLabelPayment(String labelPayment) {
        this.labelPayment = labelPayment;
    }

    public String getValuePayment() {
        return valuePayment;
    }

    public void setValuePayment(String valuePayment) {
        this.valuePayment = valuePayment;
    }

    @Override
    public String toString() {
        return "InvoiceDate{" +
                "labelInvoice='" + labelInvoice + '\'' +
                ", valueInvoice='" + valueInvoice + '\'' +
                ", labelOrder='" + labelOrder + '\'' +
                ", valueOrder='" + valueOrder + '\'' +
                ", labelShipping='" + labelShipping + '\'' +
                ", valueShipping='" + valueShipping + '\'' +
                ", labelPayment='" + labelPayment + '\'' +
                ", valuePayment='" + valuePayment + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<InvoiceDate> {

        private static final long from = 252493200;
        private static final long to = System.currentTimeMillis() / 1000;
        private static final Map<SimpleDateFormat, String> formats = new LinkedHashMap<>();
        private static final Map<String, String> labelsInvoice = new LinkedHashMap<>();
        private static final Map<String, String> labelsOrder = new LinkedHashMap<>();
        private static final Map<String, String> labelsShipping = new LinkedHashMap<>();
        private static final Map<String, String> labelsPayment = new LinkedHashMap<>();
        {
          // formats.put(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"), "en");
            formats.put(new SimpleDateFormat("MMM d, YYYY"), "en");
            formats.put(new SimpleDateFormat("d MMM, YYYY"), "en");

            formats.put(new SimpleDateFormat("dd/MM/YY"), "fr");
            formats.put(new SimpleDateFormat("d MMM YYYY"), "fr");
        }
        {
            labelsInvoice.put("Du", "fr");
            labelsInvoice.put("Date de la facture", "fr");

            labelsInvoice.put("Invoice Date", "en");
            labelsInvoice.put("Date", "en");
            labelsInvoice.put("Dated", "en");
        }
        {
            labelsOrder.put("Commandé le", "fr");
            labelsOrder.put("Date de commande", "fr");

            labelsOrder.put("Order date", "en");
            labelsOrder.put("Date of Order", "en");
        }
        {
            labelsShipping.put("Expédié le", "fr");
            labelsShipping.put("Date d'expédition", "fr");

            labelsShipping.put("Shipping date", "en");
            labelsShipping.put("Shipment date", "en");
            labelsShipping.put("Date of Shipment", "en");
        }
        {
            labelsPayment.put("Payé le", "fr");
            labelsPayment.put("Date de paiement", "fr");

            labelsPayment.put("Purchase date", "en");
            labelsPayment.put("Date of Purchase", "en");
        }


        @Override
        public InvoiceDate generate(GenerationContext ctx) {
            long date = (ctx.getRandom().nextInt((int)(to-from)) + from) * 1000;
            ctx.setDate(date);
            List<SimpleDateFormat> localizedFormats = formats.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            List<String> localizedLabels = labelsInvoice.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsOrder = labelsOrder.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsShipping = labelsShipping.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsPayment = labelsPayment.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            Date invoiceDate = calendar.getTime();
            Date shippingDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_WEEK, -4);
            Date orderDate = calendar.getTime();
            Date paymentDate = calendar.getTime();
            return new InvoiceDate(
                    localizedLabels.get(ctx.getRandom().nextInt(localizedLabels.size())),
                    localizedFormats.get(ctx.getRandom().nextInt(localizedFormats.size())).format(invoiceDate),
                    localizedLabelsOrder.get(ctx.getRandom().nextInt(localizedLabelsOrder.size())),
                    localizedFormats.get(ctx.getRandom().nextInt(localizedFormats.size())).format(orderDate),
                    localizedLabelsShipping.get(ctx.getRandom().nextInt(localizedLabelsShipping.size())),
                    localizedFormats.get(ctx.getRandom().nextInt(localizedFormats.size())).format(shippingDate),
                    localizedLabelsPayment.get(ctx.getRandom().nextInt(localizedLabelsPayment.size())),
                    localizedFormats.get(ctx.getRandom().nextInt(localizedFormats.size())).format(paymentDate)
            );
        }

    }

}
