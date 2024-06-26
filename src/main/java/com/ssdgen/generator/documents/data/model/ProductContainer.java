package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.helper.HelperCommon;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.mifmif.common.regex.Generex;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ProductContainer {

    private List<Product> products = new ArrayList<Product>();
    private float total;
    private float totalWithTax;
    private float totalWithDiscount;
    private float totalWithTaxAndDiscount;
    private float totalShippingCost;

    private String currency;
    private Boolean discountAvailable;
    private Boolean taxAvailable;
    private Boolean shippingCostAvailable;
    // display field heads, must be set in constructor //
    private final String nameHead;
    private final String codeHead;
    private final String qtyHead;
    private final String qtySuffix;
    private final String unitPriceHead;
    private final String lineTotalHead;
    private final String snHead;
    // tax & discount rate & total heads
    private final String taxHead;
    private final String taxRateHead;
    private final String taxRateTotalHead;
    private final String taxTotalHead;
    private final String discountHead;
    private final String discountRateTotalHead;
    private final String discountRateHead;
    private final String discountTotalHead;
    // totals with tax, discount and both
    private final String totalHead;
    private final String withTaxTotalHead;
    private final String withDiscountTotalHead;
    private final String withTaxAndDiscountTotalHead;


    public ProductContainer(String currency, String nameHead, String codeHead, String qtyHead, String qtySuffix, String unitPriceHead, String lineTotalHead, String snHead,
                            String taxHead, String taxRateHead, String taxRateTotalHead, String taxTotalHead,
                            String discountHead, String discountRateHead, String discountRateTotalHead, String discountTotalHead,
                            String totalHead, String withTaxTotalHead, String withDiscountTotalHead, String withTaxAndDiscountTotalHead) {
        this.setCurrency(currency);
        this.nameHead = nameHead;
        this.codeHead = codeHead;
        this.qtyHead = qtyHead;
        this.qtySuffix = qtySuffix;
        this.unitPriceHead = unitPriceHead;
        this.lineTotalHead = lineTotalHead;
        this.snHead = snHead;

        this.taxHead = taxHead;
        this.taxRateHead = taxRateHead;
        this.taxRateTotalHead = taxRateTotalHead;
        this.taxTotalHead = taxTotalHead;

        this.discountHead = discountHead;
        this.discountRateHead = discountRateHead;
        this.discountRateTotalHead = discountRateTotalHead;
        this.discountTotalHead = discountTotalHead;

        this.totalHead = totalHead;
        this.withTaxTotalHead = withTaxTotalHead;
        this.withDiscountTotalHead = withDiscountTotalHead;
        this.withTaxAndDiscountTotalHead = withTaxAndDiscountTotalHead;
    }

    public void addProduct(Product product) {
        products.add(product);
        total = total + (product.getQuantity() * product.getPrice());
        totalWithTax = totalWithTax + (product.getQuantity() * product.getPriceWithTax());
        totalWithDiscount = totalWithDiscount + (product.getQuantity() * product.getPriceWithDiscount());
        totalWithTaxAndDiscount = totalWithTaxAndDiscount + (product.getQuantity() * product.getPriceWithTaxAndDiscount());
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // getter only field heads

    public String getNameHead() {
        return nameHead;
    }

    public String getCodeHead() {
        return codeHead;
    }

    public String getQtyHead() {
        return qtyHead;
    }

    public String getQtySuffix() {
        return qtySuffix;
    }

    public String getUPHead() {
      return unitPriceHead;
    }

    public String getLineTotalHead() {
        return lineTotalHead;
    }

    public String getsnHead() {
        return snHead;
    }

    // Total base

    public float getTotal() {
        return total;
    }

    public String getFmtTotal() {
        return String.format("%.2f", this.getTotal());
    }

    public void setTotal(float total) {
        this.total = total;
    }

    // Total with tax

    public float getTotalWithTax() {
        return totalWithTax;
    }

    public String getFmtTotalWithTax() {
        return String.format("%.2f", this.getTotalWithTax());
    }

    public void setTotalWithTax(float totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    // Total with discount

    public float getTotalWithDiscount() {
        return totalWithDiscount;
    }

    public String getFmtTotalWithDiscount() {
        return String.format("%.2f", this.getTotalWithDiscount());
    }

    public void setTotalWithDiscount(float totalWithTax) {
        this.totalWithDiscount = totalWithDiscount;
    }

    // Total with tax and discount

    public float getTotalWithTaxAndDiscount() {
        return totalWithTaxAndDiscount;
    }

    public String getFmtTotalWithTaxAndDiscount() {
        return String.format("%.2f", this.getTotalWithTaxAndDiscount());
    }

    public void setTotalWithTaxAndDiscount(float totalWithTaxAndTax) {
        this.totalWithTaxAndDiscount = totalWithTaxAndDiscount;
    }

    // shipping cost

    public float gettotalShippingCost() {
        return totalShippingCost;
    }

    public String getFmttotalShippingCost() {
        return String.format("%.2f", this.gettotalShippingCost());
    }

    public void settotalShippingCost(float totalShippingCost) {
        this.totalShippingCost = totalShippingCost;
    }

    // tax calc from existing values

    public float getTotalTax() {
        return totalWithTax - total;
    }

    public String getFmtTotalTax() {
        return String.format("%.2f", this.getTotalTax());
    }

    public float getTotalTaxRate() {
        return this.getTotalTax() / total;
    }

    public String getFmtTotalTaxRate() {
        return String.format("%.2f", this.getTotalTaxRate() * 100) + "%";
    }

    // discount calc from existing values

    public float getTotalDiscount() {
        return totalWithDiscount - total;
    }

    public String getFmtTotalDiscount() {
        return String.format("%.2f", this.getTotalDiscount());
    }

    public float getTotalDiscountRate() {
        return this.getTotalDiscount() / total;
    }

    public String getFmtTotalDiscountRate() {
        return String.format("%.2f", this.getTotalDiscountRate() * 100) + "%";
    }

    // tax getter heads //

    public String getTaxHead() {
      return taxHead;
    }

    public String getTaxRateHead() {
        return taxRateHead;
    }

    public String getTaxRateTotalHead() {
        return taxRateTotalHead;
    }

    public String getTaxTotalHead() {
        return taxTotalHead;
    }

    // discount getter heads //

    public String getDiscountHead() {
        return discountHead;
    }

    public String getDiscountRateHead() {
        return discountRateHead;
    }

    public String getDiscountRateTotalHead() {
        return discountRateTotalHead;
    }

    public String getDiscountTotalHead() {
        return discountTotalHead;
    }

    // total, total+tax, total+discount, total+tax+discount getter heads //

    public String getTotalHead() {
        return totalHead;
    }

    public String getWithTaxTotalHead() {
        return withTaxTotalHead;
    }

    public String getWithDiscountTotalHead() {
        return withDiscountTotalHead;
    }

    public String getWithTaxAndDiscountTotalHead() {
        return withTaxAndDiscountTotalHead;
    }

    // discount available boolean

    public Boolean getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(Boolean discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    // tax available boolean

    public Boolean getTaxAvailable() {
        return taxAvailable;
    }

    public void setTaxAvailable(Boolean taxAvailable) {
        this.taxAvailable = taxAvailable;
    }

    // shipping available boolean

    public Boolean getShippingCostAvailable() {
        return shippingCostAvailable;
    }

    public void setShippingCostAvailable(Boolean shippingCostAvailable) {
        this.shippingCostAvailable = shippingCostAvailable;
    }

    @Override
    public String toString() {
        return "ProductContainer{" +
                "products=" + products +
                ", total=" + total +
                ", totalWithTax=" + totalWithTax +
                ", totalWithDiscount=" + totalWithDiscount +
                ", totalWithTaxAndDiscount=" + totalWithTaxAndDiscount +
                '}';
    }

    public static class Generator implements ModelGenerator<ProductContainer> {

        private static final Map<String, String> nameHeads = new LinkedHashMap<>();
        private static final Map<String, String> codeHeads = new LinkedHashMap<>();
        private static final Map<String, String> qtyHeads = new LinkedHashMap<>();
        private static final Map<String, String> qtySuffixes = new LinkedHashMap<>();
        private static final Map<String, String> unitPriceHeads = new LinkedHashMap<>();
        private static final Map<String, String> lineTotalHeads = new LinkedHashMap<>();
        private static final Map<String, String> snHeads = new LinkedHashMap<>();

        private static final Map<String, String> taxHeads = new LinkedHashMap<>();
        private static final Map<String, String> taxRateHeads = new LinkedHashMap<>();
        private static final Map<String, String> taxRateTotalHeads = new LinkedHashMap<>();
        private static final Map<String, String> taxTotalHeads = new LinkedHashMap<>();

        private static final Map<String, String> discountHeads = new LinkedHashMap<>();
        private static final Map<String, String> discountRateHeads = new LinkedHashMap<>();
        private static final Map<String, String> discountRateTotalHeads = new LinkedHashMap<>();
        private static final Map<String, String> discountTotalHeads = new LinkedHashMap<>();

        private static final Map<String, String> totalHeads = new LinkedHashMap<>();
        private static final Map<String, String> withTaxTotalHeads = new LinkedHashMap<>();
        private static final Map<String, String> withDiscountTotalHeads = new LinkedHashMap<>();
        private static final Map<String, String> withTaxAndDiscountTotalHeads = new LinkedHashMap<>();

        // Primary heads //
        {
            nameHeads.put("Désignation", "fr");
            nameHeads.put("Description", "fr");
            nameHeads.put("Désignation du Produit", "fr");

            nameHeads.put("Item", "en");
            nameHeads.put("Name", "en");
            nameHeads.put("Goods", "en");
            nameHeads.put("Activity", "en");
            nameHeads.put("Services", "en");
            nameHeads.put("Service Type", "en");
            nameHeads.put("Description", "en");
            nameHeads.put("Particulars", "en");
            nameHeads.put("Product Desc", "en");
            nameHeads.put("Product Details", "en");
            nameHeads.put("Item Description", "en");
            nameHeads.put("Item & Description", "en");
            nameHeads.put("Product Description", "en");
            nameHeads.put("Description of Goods", "en");
        }
        {
            codeHeads.put("Code Produit", "fr");

            codeHeads.put("ID", "en");
            codeHeads.put("Item ID", "en");
            codeHeads.put("Item Ref", "en");
            codeHeads.put("Product\n Ref", "en");
            codeHeads.put("Product\n ID", "en");
            codeHeads.put("Product\n Code", "en");
        }
        {
            qtyHeads.put("Qté", "fr");
            qtyHeads.put("Quantité", "fr");

            qtyHeads.put("Qty", "en");
            qtyHeads.put("Quantity", "en");
        }
        {
            qtySuffixes.put("Pc", "fr");

            qtySuffixes.put("PC", "en");
            qtySuffixes.put("PCs", "en");
            qtySuffixes.put("Unit", "en");
            qtySuffixes.put("units", "en");
        }
        {
            unitPriceHeads.put("UP", "fr");
            unitPriceHeads.put("Prix Unitaire", "fr");
            unitPriceHeads.put("P.U. HT", "fr");
            unitPriceHeads.put("P.U.", "fr");

            unitPriceHeads.put("Rate", "en");
            unitPriceHeads.put("Unit Price", "en");
            unitPriceHeads.put("Price\n per unit", "en");
        }
        {
            lineTotalHeads.put("Montant H.T.", "fr");
            lineTotalHeads.put("Montant HT", "fr");

            lineTotalHeads.put("Amount", "en");
            lineTotalHeads.put("Total", "en");
            lineTotalHeads.put("Gross", "en");
            lineTotalHeads.put("Net Amount", "en");
        }
        {
            snHeads.put("Non.", "fr");
            snHeads.put("S.Non.", "fr");
            snHeads.put("Numéro de série", "fr");

            snHeads.put("#", "en");
            snHeads.put("No.", "en");
            snHeads.put("SN.", "en");
            snHeads.put("S.No.", "en");
            snHeads.put("SI No.", "en");
            snHeads.put("Serial\n No.", "en");
        }
        // tax heads //
        {
            taxHeads.put("TVA", "fr");
            taxHeads.put("Montant TVA", "fr");

            taxHeads.put("VAT Tax", "en");
            taxHeads.put("(Tax|TAX|Vat|VAT)", "en");
            taxHeads.put("(Tax|TAX|Vat|VAT) (Amt|Amount)", "en");
        }
        {
            taxRateHeads.put("TVA", "fr");
            taxRateHeads.put("Taux de TVA", "fr");

            taxRateHeads.put("(Tax|TAX|Vat|VAT)\\%", "en");
            taxRateHeads.put("\\@(Tax|TAX|Vat|VAT)", "en");
            taxRateHeads.put("(Tax|TAX|Vat|VAT) Rate", "en");
        }
        {
            taxRateTotalHeads.put("Taux d'imposition Total", "fr");

            taxRateTotalHeads.put("(Total|Final) \\@(Tax|TAX|Vat|VAT)", "en");
            taxRateTotalHeads.put("(Total|Final) (Tax|TAX|Vat|VAT)\\%", "en");
            taxRateTotalHeads.put("(Total|Final) (Tax|TAX|Vat|VAT) Rate", "en");
        }
        {
            taxTotalHeads.put("Montant TVA", "fr");
            taxTotalHeads.put("TVA", "fr");

            taxTotalHeads.put("(Tax|TAX|Vat|VAT) (Amt|Amount)", "en");
            taxTotalHeads.put("Total (Tax|TAX|Vat|VAT)", "en");
            taxTotalHeads.put("Sales (Tax|TAX)", "en");
        }
        // discount heads //
        {
            discountHeads.put("TOTAL REMISE IMMEDIATE", "fr");

            discountHeads.put("(Disc|Discount)", "en");
        }
        {
            discountRateHeads.put("Remise", "fr");

            discountRateHeads.put("\\@DISC", "en");
            discountRateHeads.put("Disc \\%", "en");
            discountRateHeads.put("(Disc\\.|Disc|Discount) Rate", "en");
        }
        {
            discountRateTotalHeads.put("Taux d'actualisation Total", "fr");

            discountRateTotalHeads.put("(Total|Final) Disc\\%", "en");
            discountRateTotalHeads.put("(Total|Final) \\@(Disc|Discount)", "en");
            discountRateTotalHeads.put("(Total|Final) (Disc\\.|Disc|Discount) Rate", "en");
        }
        {
            discountTotalHeads.put("Montant de la remise", "fr");

            discountTotalHeads.put("Discount (Amt|Amount)", "en");
            discountTotalHeads.put("(Total|Final|Gross) (Disc|Discount)", "en");
        }
        // total, total+tax, total+discount, total+tax+discount heads //
        {
            totalHeads.put("Montant H.T.", "fr");
            totalHeads.put("Montant HT", "fr");

            totalHeads.put("(Total|Amt|Amount|Gross)", "en");
            totalHeads.put("Total Net (Amt|Amount)", "en");
            totalHeads.put("Total (w\\.o\\.|w/o|without|Excl\\.) (Tax|TAX|Vat|VAT)", "en");
        }
        {
            withTaxTotalHeads.put("Montant TTC", "fr");

            withTaxTotalHeads.put("Total (Amt|Amount) (with|w/) (Tax|TAX|Vat|VAT)", "en");
            withTaxTotalHeads.put("(Total|Amt|Amount) (with|w/) (Tax|TAX|Vat|VAT)", "en");
        }
        {
            withDiscountTotalHeads.put("Montant avec remise", "fr");

            withDiscountTotalHeads.put("Net (Amt|Amount)", "en");
            withDiscountTotalHeads.put("(Total|Final) Amount w/ (Disc|Discount)", "en");
            withDiscountTotalHeads.put("(Total|Final) (w/|with) (Disc|Discount)", "en");
        }
        {
            withTaxAndDiscountTotalHeads.put("Montant TTC", "fr");
            withTaxAndDiscountTotalHeads.put("Total TTC", "fr");
            withTaxAndDiscountTotalHeads.put("Net à payer", "fr");

            withTaxAndDiscountTotalHeads.put("(Amt|Amount|Balance) to pay", "en");
            withTaxAndDiscountTotalHeads.put("(Total|Final) (Amt|Amount|Payable|Due)", "en");
            withTaxAndDiscountTotalHeads.put("Gross (Amt|Amount)", "en");
            withTaxAndDiscountTotalHeads.put("Balance Due", "en");
            withTaxAndDiscountTotalHeads.put("Total Net", "en");
            withTaxAndDiscountTotalHeads.put("Total", "en");
        }


        private final List<Product> products = new ArrayList<Product>();
        private static final List<List<String>> productsFileLangList = Arrays.asList(
                Arrays.asList("common/product/fr/householdandmedia_fr.json", "fr"),
                Arrays.asList("common/product/en/householdandmedia_en.json", "en"));
        {
            int currentListSize = 0;
            for (int i=0; i < productsFileLangList.size(); i++) {
                Reader jsonReader = new InputStreamReader(ProductContainer.class.getClassLoader().getResourceAsStream(productsFileLangList.get(i).get(0)));
                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<Product>>(){}.getType();
                products.addAll(gson.fromJson(jsonReader, collectionType));

                for (int j=currentListSize; j<products.size(); j++) {
                    products.get(j).setLanguage(productsFileLangList.get(i).get(1));
                }
                currentListSize += products.size();
            }
        }

        @Override
        public ProductContainer generate(GenerationContext ctx) {

            List<String> localNameHeads = nameHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localCodeHeads = codeHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localQtyHeads = qtyHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localQtySuffixes = qtySuffixes.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localUPHeads = unitPriceHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localLineTotalHeads = lineTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localSNHeads = snHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            List<String> localTaxHeads = taxHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localTaxRateHeads = taxRateHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localTaxRateTotalHeads = taxRateTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localTaxTotalHeads = taxTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            List<String> localDiscountHeads = discountHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localDiscountRateHeads = discountRateHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localDiscountRateTotalHeads = discountRateTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localDiscountTotalHeads = discountTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            List<String> localTotalHeads = totalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localWithTaxTotalHeads = withTaxTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localWithDiscountTotalHeads = withDiscountTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localWithTaxAndDiscountTotalHeads = withTaxAndDiscountTotalHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            List<Product> productsLangFiltered = new ArrayList<Product>();
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getLanguage() == ctx.getLanguage()) {
                    productsLangFiltered.add(products.get(i));
                }
            }

            ProductContainer productContainer = new ProductContainer(
                    ctx.getCurrency(),
                    localNameHeads.get(ctx.getRandom().nextInt(localNameHeads.size())),
                    localCodeHeads.get(ctx.getRandom().nextInt(localCodeHeads.size())),
                    localQtyHeads.get(ctx.getRandom().nextInt(localQtyHeads.size())),
                    localQtySuffixes.get(ctx.getRandom().nextInt(localQtySuffixes.size())),
                    localUPHeads.get(ctx.getRandom().nextInt(localUPHeads.size())),
                    localLineTotalHeads.get(ctx.getRandom().nextInt(localLineTotalHeads.size())),
                    localSNHeads.get(ctx.getRandom().nextInt(localSNHeads.size())),
                    new Generex(localTaxHeads.get(ctx.getRandom().nextInt(localTaxHeads.size()))).random(),
                    new Generex(localTaxRateHeads.get(ctx.getRandom().nextInt(localTaxRateHeads.size()))).random(),
                    new Generex(localTaxRateTotalHeads.get(ctx.getRandom().nextInt(localTaxRateTotalHeads.size()))).random(),
                    new Generex(localTaxTotalHeads.get(ctx.getRandom().nextInt(localTaxTotalHeads.size()))).random(),
                    new Generex(localDiscountHeads.get(ctx.getRandom().nextInt(localDiscountHeads.size()))).random(),
                    new Generex(localDiscountRateHeads.get(ctx.getRandom().nextInt(localDiscountRateHeads.size()))).random(),
                    new Generex(localDiscountRateTotalHeads.get(ctx.getRandom().nextInt(localDiscountRateTotalHeads.size()))).random(),
                    new Generex(localDiscountTotalHeads.get(ctx.getRandom().nextInt(localDiscountTotalHeads.size()))).random(),
                    new Generex(localTotalHeads.get(ctx.getRandom().nextInt(localTotalHeads.size()))).random(),
                    new Generex(localWithTaxTotalHeads.get(ctx.getRandom().nextInt(localWithTaxTotalHeads.size()))).random(),
                    new Generex(localWithDiscountTotalHeads.get(ctx.getRandom().nextInt(localWithDiscountTotalHeads.size()))).random(),
                    new Generex(localWithTaxAndDiscountTotalHeads.get(ctx.getRandom().nextInt(localWithTaxAndDiscountTotalHeads.size()))).random()
                    );

            Boolean discountAvailable = false; // ctx.getRandom().nextInt(100) < 10; TODO uncomment
            productContainer.setDiscountAvailable(discountAvailable);

            Boolean taxAvailable = true;  // ctx.getRandom().nextInt(100) < 90; TODO uncomment
            productContainer.setTaxAvailable(taxAvailable);

            final int maxProductNum = ctx.getMaxProductNum();
            final int MAXQTYPERPRODUCT = 5;
            float price = 0;
            float taxRate = 0;
            float discountRate = 0;
            float priceWithTax = 0;
            float priceWithDiscount = 0;
            float priceWithTaxAndDiscount = 0;
            Generex codeGenerex = new Generex("[A-Z0-9]"+"{"+new Generex("[4-9]").random()+"}");  // [A-Z0-9]

            for (int i = 0; i < ctx.getRandom().nextInt(maxProductNum - 1)+1; i++) {
                Product product = productsLangFiltered.get(ctx.getRandom().nextInt(productsLangFiltered.size()));
                int quantity = ctx.getRandom().nextInt(MAXQTYPERPRODUCT - 1) + 1;
                quantity = (product.getPrice() < 250) ? quantity * 8: quantity; // 8x qty if price < 250
                product.setQuantity(quantity);
                product.setCurrency(ctx.getCurrency());

                price = product.getPrice();

                priceWithTax = price;
                // add tax for each item if taxAvailable
                if (taxAvailable) {
                    taxRate = HelperCommon.rand_uniform(0.0f, 0.2f);  // taxRate from 0% to 20%
                    priceWithTax = HelperCommon.round(price * (1 + taxRate), 2);
                }

                priceWithDiscount = price;
                // add discounts for each item if discountAvailable
                if (discountAvailable) {
                    discountRate = HelperCommon.rand_uniform(0.05f, 0.15f);  // discountRate from 5% to 15%
                    priceWithDiscount = HelperCommon.round(price * (1 - discountRate), 2);
                }
                priceWithTaxAndDiscount = price * (1 + taxRate - discountRate);

                product.setTaxRate(taxRate);
                product.setDiscountRate(discountRate);
                product.setPriceWithTax(priceWithTax);
                product.setPriceWithDiscount(priceWithDiscount);
                product.setPriceWithTaxAndDiscount(priceWithTaxAndDiscount);
                product.setCode(codeGenerex.random());

                productContainer.addProduct(product);
            }

            Boolean shippingCostAvailable = ctx.getRandom().nextInt(100) < 15;
            productContainer.setShippingCostAvailable(shippingCostAvailable);
            // Shipping will always be last item if shippingCostAvailable
            if (shippingCostAvailable) {
                Product shippingProduct = new Product();
                price = 10 + ctx.getRandom().nextInt(200);

                shippingProduct.setName((ctx.getRandom().nextBoolean()) ? "Shipping": "SHIPPING");
                shippingProduct.setCurrency(ctx.getCurrency());
                shippingProduct.setQuantity(1);
                shippingProduct.setPrice(price);
                shippingProduct.setTaxRate(0);  // vat for shipping is always 0
                shippingProduct.setDiscountRate(0);  // discount for shipping is always 0
                shippingProduct.setPriceWithTax(price);
                shippingProduct.setPriceWithDiscount(price);
                shippingProduct.setPriceWithTaxAndDiscount(price);

                productContainer.addProduct(shippingProduct);
            }

            return productContainer;
        }

    }

}
