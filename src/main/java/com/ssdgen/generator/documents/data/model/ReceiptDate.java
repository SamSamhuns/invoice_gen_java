package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReceiptDate {

    private String label;
    private String value;
    private String time;
    private final String timeLabel;
    private final String printedDateLabel;

    public ReceiptDate(String label,String timeLabel, String printedDateLabel, String value, String time ) {
        this.label = label;
        this.value = value;
        this.time = time;
        this.timeLabel = timeLabel;
        this.printedDateLabel = printedDateLabel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public String getprintedDateLabel() {
        return printedDateLabel;
    }

    @Override
    public String toString() {
        return "ReceiptDate{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<ReceiptDate> {

        private static final long from = 252493200;
        private static final long to = System.currentTimeMillis() / 1000;
        private static final Map<SimpleDateFormat, String> formatsDate = new LinkedHashMap<>();
        private static final Map<SimpleDateFormat, String> formatsTime = new LinkedHashMap<>();
        private static final Map<String, String> labels = new LinkedHashMap<>();
        private static final Map<String, String> labelsOrder = new LinkedHashMap<>();
        private static final Map<String, String> labelsShipping = new LinkedHashMap<>();
        private static final Map<String, String> labelsPayment = new LinkedHashMap<>();
        private static final Map<String, String> labelsTime = new LinkedHashMap<>();
        private static final Map<String, String> labelsPrintedDate = new LinkedHashMap<>();
        {
            formatsDate.put(new SimpleDateFormat("dd/MM/YY"), "fr");
            formatsDate.put(new SimpleDateFormat("d MMM YYYY "), "fr");

            formatsDate.put(new SimpleDateFormat("MMM d, YYYY "), "en");
            formatsDate.put(new SimpleDateFormat("YYYY-MM-dd"), "en");
            formatsDate.put(new SimpleDateFormat("d MMM, YYYY "), "en");
        }
        {
            formatsTime.put(new SimpleDateFormat("HH:mm:ss"), "fr");
            formatsTime.put(new SimpleDateFormat("HH:mm"), "fr");

            formatsTime.put(new SimpleDateFormat("hh:mm:ss aa"), "en");
            formatsTime.put(new SimpleDateFormat("HH:mm:ss "), "en");
        }
        {
            labels.put("Date de la facture", "fr");
            labels.put("Du", "fr");

            labels.put("Receipt Date", "en");
            labels.put("Date", "en");
            labels.put("Dated", "en");
        }
        {
            labelsOrder.put("Commandé le", "fr");
            labelsOrder.put("Date de commande", "fr");

            labelsOrder.put("Order date", "en");
        }
        {
            labelsShipping.put("Expédié le", "fr");
            labelsShipping.put("Date d'expédition", "fr");

            labelsShipping.put("Expedition date", "en");
        }
        {
            labelsPayment.put("Payé le", "fr");
            labelsPayment.put("Date de paiement", "fr");

            labelsPayment.put("Purchase date", "en");
        }
        {
            labelsTime.put("Heure", "fr");

            labelsTime.put("Time", "en");
        }
        {
            labelsPrintedDate.put("Imprimé le", "fr");

            labelsPrintedDate.put("Printed Date", "en");
            labelsPrintedDate.put("PRN ON", "en");
            labelsPrintedDate.put("Printed on", "en");
        }

        @Override
        public ReceiptDate generate(GenerationContext ctx) {
            long date = (ctx.getRandom().nextInt((int)(to-from)) + from) * 1000;
            ctx.setDate(date);
            List<SimpleDateFormat> localizedFormats = formatsDate.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxF = ctx.getRandom().nextInt(localizedFormats.size());

            List<SimpleDateFormat> localizedFormatsTime = formatsTime.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            int idxT = ctx.getRandom().nextInt(localizedFormatsTime.size());


            List<String> localizedLabels = labels.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsOrder = labelsOrder.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsShipping = labelsShipping.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsPayment = labelsPayment.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelsTime = labelsTime.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedLabelPrintedDate = labelsPrintedDate.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            int idxL = ctx.getRandom().nextInt(localizedLabels.size());
            int idxLC = ctx.getRandom().nextInt(localizedLabelsOrder.size());
            int idxLE = ctx.getRandom().nextInt(localizedLabelsShipping.size());
            int idxLP = ctx.getRandom().nextInt(localizedLabelsPayment.size());
            int idxLT = ctx.getRandom().nextInt(localizedLabelsTime.size());
            int idxLPR = ctx.getRandom().nextInt(localizedLabelPrintedDate.size());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            Date invoiceDate = calendar.getTime();

            calendar.add(Calendar.DAY_OF_WEEK, -4);
            return new ReceiptDate(localizedLabels.get(idxL),localizedLabelsTime.get(idxLT),localizedLabelPrintedDate.get(idxLPR), localizedFormats.get(idxF).format(invoiceDate),localizedFormatsTime.get(idxT).format(invoiceDate)
            );
        }

    }

}
