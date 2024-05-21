package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;

public class InvoiceModel extends Model{

    private InvoiceNumber reference;
    private InvoiceDate date;

    public InvoiceModel() {
    }

    public InvoiceDate getDate() {
        return date;
    }

    public void setDate(InvoiceDate date) {
        this.date = date;
    }

    public InvoiceNumber getReference() {
        return reference;
    }

    public void setReference(InvoiceNumber reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "InvoiceModel{" +
                "reference=" + reference +
                ", date=" + getDate() +
                ", lang=" + getLang() +
                ", locale=" + getLocale() +
                ", paymentInfo=" + getPaymentInfo() +
                ", company=" + getCompany() +
                ", client=" + getClient() +
                ", productContainer=" + getProductContainer() +
                ", configRow=" + getConfigMaps() +
                '}';
    }

    public static class Generator implements ModelGenerator<InvoiceModel> {

        @Override
        public InvoiceModel generate(GenerationContext ctx) {
            InvoiceModel model = new InvoiceModel();
            model.setReference(new InvoiceNumber.Generator().generate(ctx));
            model.setDate(new InvoiceDate.Generator().generate(ctx));
            model.setLang(ctx.getLanguage());
            model.setLocale(ctx.getLocale());
            model.setCompany(new Company.Generator().generate(ctx));
            model.setPaymentInfo(new PaymentInfo.Generator().generate(ctx));
            model.getPaymentInfo().setValueAccountName(model.getCompany().getName());
            model.setClient(new Client.Generator().generate(ctx));
            model.setProductContainer(new ProductContainer.Generator().generate(ctx));
            model.setConfigMaps(ctx.getConfigMaps());
            return model;
        }
    }
}
