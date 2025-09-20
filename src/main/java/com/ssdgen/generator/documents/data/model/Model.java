package com.ssdgen.generator.documents.data.model;

import java.util.Map;
import java.util.List;
import java.util.Random;

public class Model {
    private String lang;
    private String locale;
    private PaymentInfo paymentInfo;
    private Company company;
    private Client client;
    private ProductContainer productContainer;
    private static final Random rnd = new Random();
    private static List<Map<String, Object>> configMaps;

    public Model() {
    }

    public Random getRandom() {
        return rnd;
    }

    public Object callviaName(Object c, String methodName) throws Exception {
        // Calls a method with its name as a string
        return c.getClass().getMethod(methodName).invoke(c);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ProductContainer getProductContainer() {
        return productContainer;
    }

    public void setProductContainer(ProductContainer productContainer) {
        this.productContainer = productContainer;
    }

    public List<Map<String, Object>> getConfigMaps() {
        return configMaps;
    }

    public void setConfigMaps(List<Map<String, Object>> configMaps) {
        Model.configMaps = configMaps;
    }

    @Override
    public String toString() {
        return "Model{" +
                ", lang=" + lang +
                ", locale=" + locale +
                ", paymentInfo=" + paymentInfo +
                ", company=" + company +
                ", client=" + client +
                ", productContainer=" + productContainer +
                ", configMaps=" + configMaps +
                '}';
    }
}
