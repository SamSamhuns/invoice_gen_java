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
 * Djedjiga Belhadj <djedjiga.belhadj@gmail.com> / Loria
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


import com.fairandsmart.generator.documents.data.helper.HelperCommon;
import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private Boolean taxRateAvailable;
    private Boolean shippingCostAvailable;
    // display field heads, must be set in constructor //
    private final String descHead;
    private final String qtyHead;
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


    public ProductContainer(String currency, String descHead, String qtyHead, String unitPriceHead, String lineTotalHead, String snHead,
                            String taxHead, String taxRateHead, String taxRateTotalHead, String taxTotalHead,
                            String discountHead, String discountRateHead, String discountRateTotalHead, String discountTotalHead,
                            String totalHead, String withTaxTotalHead, String withDiscountTotalHead, String withTaxAndDiscountTotalHead) {
        this.setCurrency(currency);
        this.descHead = descHead;
        this.qtyHead = qtyHead;
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

    // get only field heads

    public String getDescHead() {
        return descHead;
    }

    public String getQtyHead() {
        return qtyHead;
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

    public String getFormatedTotal() {
        return String.format("%.2f", this.getTotal()) + " " + currency;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    // Total with tax

    public float getTotalWithTax() {
        return totalWithTax;
    }

    public String getFormatedTotalWithTax() {
        return String.format("%.2f", this.getTotalWithTax()) + " " + currency;
    }

    public void setTotalWithTax(float totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    // Total with discount

    public float getTotalWithDiscount() {
        return totalWithDiscount;
    }

    public String getFormatedTotalWithDiscount() {
        return String.format("%.2f", this.getTotalWithDiscount()) + " " + currency;
    }

    public void setTotalWithDiscount(float totalWithTax) {
        this.totalWithDiscount = totalWithDiscount;
    }

    // Total with tax and discount

    public float getTotalWithTaxAndDiscount() {
        return totalWithTaxAndDiscount;
    }

    public String getFormatedTotalWithTaxAndDiscount() {
        return String.format("%.2f", this.getTotalWithTaxAndDiscount()) + " " + currency;
    }

    public void setTotalWithTaxAndDiscount(float totalWithTaxAndTax) {
        this.totalWithTaxAndDiscount = totalWithTaxAndDiscount;
    }

    // shipping cost

    public float gettotalShippingCost() {
        return totalShippingCost;
    }

    public String getFormatedtotalShippingCost() {
        return String.format("%.2f", this.gettotalShippingCost()) + " " + currency;
    }

    public void settotalShippingCost(float totalShippingCost) {
        this.totalShippingCost = totalShippingCost;
    }

    // tax calc from existing values

    public float getTotalTax() {
        return totalWithTax - total;
    }

    public String getFormatedTotalTax() {
        return String.format("%.2f", this.getTotalTax()) + " " + currency;
    }

    public float getTotalTaxRate() {
        return this.getTotalTax() / total;
    }

    public String getFormatedTotalTaxRate() {
        return String.format("%.2f", this.getTotalTaxRate() * 100) + "%";
    }

    // discount calc from existing values

    public float getTotalDiscount() {
        return totalWithDiscount - total;
    }

    public String getFormatedTotalDiscount() {
        return String.format("%.2f", this.getTotalDiscount()) + " " + currency;
    }

    public float getTotalDiscountRate() {
        return this.getTotalDiscount() / total;
    }

    public String getFormatedTotalDiscountRate() {
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

    public Boolean getTaxRateAvailable() {
        return taxRateAvailable;
    }

    public void setTaxRateAvailable(Boolean taxRateAvailable) {
        this.taxRateAvailable = taxRateAvailable;
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

        private static final Map<String, String> descHeads = new LinkedHashMap<>();
        private static final Map<String, String> qtyHeads = new LinkedHashMap<>();
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
            descHeads.put("Désignation", "fr");
            descHeads.put("Description", "fr");
            descHeads.put("Désignation du Produit", "fr");

            descHeads.put("Item", "en");
            descHeads.put("Goods", "en");
            descHeads.put("Services", "en");
            descHeads.put("Description", "en");
            descHeads.put("Product / Ref", "en");
            descHeads.put("Product Desc", "en");
            descHeads.put("Product Description", "en");
            descHeads.put("Description of Goods", "en");
        }
        {
            qtyHeads.put("Qté", "fr");
            qtyHeads.put("Quantité", "fr");

            qtyHeads.put("Qty", "en");
            qtyHeads.put("Quantity", "en");
        }
        {
            unitPriceHeads.put("UP", "fr");
            unitPriceHeads.put("Prix Unitaire", "fr");
            unitPriceHeads.put("P.U. HT", "fr");
            unitPriceHeads.put("P.U.", "fr");

            unitPriceHeads.put("Rate", "en");
            unitPriceHeads.put("Unit Price", "en");
            unitPriceHeads.put("Price per unit", "en");
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
            snHeads.put("Serial No.", "en");
        }
        // tax heads //
        {
            taxHeads.put("TVA", "fr");
            taxHeads.put("Montant TVA", "fr");

            taxHeads.put("Tax", "en");
            taxHeads.put("Vat", "en");
            taxHeads.put("VAT Tax", "en");
            taxHeads.put("VAT Amount", "en");
        }
        {
            taxRateHeads.put("TVA", "fr");
            taxRateHeads.put("Taux de TVA", "fr");

            taxRateHeads.put("Tax %", "en");
            taxRateHeads.put("Vat %", "en");
            taxRateHeads.put("@Vat", "en");
            taxRateHeads.put("Vat Rate", "en");
        }
        {
            taxRateTotalHeads.put("Taux d'imposition Total", "fr");

            taxRateTotalHeads.put("Total @Tax", "en");
            taxRateTotalHeads.put("Total @VAT", "en");
            taxRateTotalHeads.put("Total Tax%", "en");
            taxRateTotalHeads.put("Final VAT%", "en");
            taxRateTotalHeads.put("Final VAT Rate", "en");
            taxRateTotalHeads.put("Total VAT Rate", "en");
            taxRateTotalHeads.put("Total Tax Rate", "en");
        }
        {
            taxTotalHeads.put("Montant TVA", "fr");
            taxTotalHeads.put("TVA", "fr");

            taxTotalHeads.put("VAT Amount", "en");
            taxTotalHeads.put("Tax Amount", "en");
            taxTotalHeads.put("Total Tax", "en");
            taxTotalHeads.put("Sales Tax", "en");
        }
        // discount heads //
        {
            discountHeads.put("TOTAL REMISE IMMEDIATE", "fr");

            discountHeads.put("Discount", "en");
            discountHeads.put("Disc", "en");
        }
        {
            discountRateHeads.put("Remise", "fr");

            discountRateHeads.put("@DISC", "en");
            discountRateHeads.put("Disc %", "en");
            discountRateHeads.put("Disc. Rate", "en");
            discountRateHeads.put("Discount Rate", "en");
        }
        {
            discountRateTotalHeads.put("Taux d'actualisation Total", "fr");

            discountRateTotalHeads.put("Total @Discount", "en");
            discountRateTotalHeads.put("Final Disc%", "en");
            discountRateTotalHeads.put("Final Disc. Rate", "en");
            discountRateTotalHeads.put("Total Discount Rate", "en");
        }
        {
            discountTotalHeads.put("Montant de la remise", "fr");

            discountTotalHeads.put("Discount Amount", "en");
            discountTotalHeads.put("Total Discount", "en");
            discountTotalHeads.put("Gross Discount", "en");
            discountTotalHeads.put("Final Discount", "en");
        }
        // total, total+tax, total+discount, total+tax+discount heads //
        {
            totalHeads.put("Montant H.T.", "fr");
            totalHeads.put("Montant HT", "fr");

            totalHeads.put("Amount", "en");
            totalHeads.put("Total", "en");
            totalHeads.put("Gross", "en");
            totalHeads.put("Total Gross Amt", "en");
            totalHeads.put("Total w/o Tax", "en");
            totalHeads.put("Total w.o. Tax", "en");
            totalHeads.put("Total without Tax", "en");
            totalHeads.put("Total (Excl.Tax)", "en");
            totalHeads.put("Total (Excl.VAT)", "en");
        }
        {
            withTaxTotalHeads.put("Montant TTC", "fr");

            withTaxTotalHeads.put("Total Amount with Tax", "en");
            withTaxTotalHeads.put("Total Amount w/ Tax", "en");
            withTaxTotalHeads.put("Amount with Tax", "en");
            withTaxTotalHeads.put("Total with Tax", "en");
            withTaxTotalHeads.put("Total w/ Tax", "en");
            withTaxTotalHeads.put("Amount w/ Tax", "en");
        }
        {
            withDiscountTotalHeads.put("Montant avec remise", "fr");

            withDiscountTotalHeads.put("Total Amount w/ Discount", "en");
            withDiscountTotalHeads.put("Amount with Discount", "en");
            withDiscountTotalHeads.put("Total with Discount", "en");
        }
        {
            withTaxTotalHeads.put("Montant TTC", "fr");
            withTaxTotalHeads.put("Total TTC", "fr");
            withTaxTotalHeads.put("Net à payer", "fr");

            withTaxAndDiscountTotalHeads.put("Amount to pay", "en");
            withTaxAndDiscountTotalHeads.put("Total Amount", "en");
            withTaxAndDiscountTotalHeads.put("Balance Due", "en");
            withTaxAndDiscountTotalHeads.put("Final Amount", "en");
            withTaxAndDiscountTotalHeads.put("Total Net", "en");
            withTaxAndDiscountTotalHeads.put("Total", "en");
        }


        private final List<Product> products = new ArrayList<Product>();
        private static final List<String> productsFileList = Arrays.asList(
                "common/product/fr/householdandmedia_fr.json",
                "common/product/en/householdandmedia_en.json");
        private static final List<String> productsLangList = Arrays.asList(
                "fr",
                "en");
        {
            assert productsFileList.size() == productsLangList.size();
            int currentListSize = 0;
            for (int i=0; i < productsFileList.size(); i++) {
                Reader jsonReader = new InputStreamReader(ProductContainer.class.getClassLoader().getResourceAsStream(productsFileList.get(i)));
                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<Product>>(){}.getType();
                products.addAll(gson.fromJson(jsonReader, collectionType));

                for (int j=currentListSize; j<products.size(); j++) {
                    products.get(j).setLanguage(productsLangList.get(i));
                }
                currentListSize += products.size();
            }
        }

        @Override
        public ProductContainer generate(GenerationContext ctx) {

            List<String> localDescHeads = descHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localQtyHeads = qtyHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
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
            final int MAXPRODUCT = 6;
            final int MAXQTYPERPRODUCT = 5;

            ProductContainer productContainer = new ProductContainer(
                    ctx.getCurrency(),
                    localDescHeads.get(ctx.getRandom().nextInt(localDescHeads.size())),
                    localQtyHeads.get(ctx.getRandom().nextInt(localQtyHeads.size())),
                    localUPHeads.get(ctx.getRandom().nextInt(localUPHeads.size())),
                    localLineTotalHeads.get(ctx.getRandom().nextInt(localLineTotalHeads.size())),
                    localSNHeads.get(ctx.getRandom().nextInt(localSNHeads.size())),
                    localTaxHeads.get(ctx.getRandom().nextInt(localTaxHeads.size())),
                    localTaxRateHeads.get(ctx.getRandom().nextInt(localTaxRateHeads.size())),
                    localTaxRateTotalHeads.get(ctx.getRandom().nextInt(localTaxRateTotalHeads.size())),
                    localTaxTotalHeads.get(ctx.getRandom().nextInt(localTaxTotalHeads.size())),
                    localDiscountHeads.get(ctx.getRandom().nextInt(localDiscountHeads.size())),
                    localDiscountRateHeads.get(ctx.getRandom().nextInt(localDiscountRateHeads.size())),
                    localDiscountRateTotalHeads.get(ctx.getRandom().nextInt(localDiscountRateTotalHeads.size())),
                    localDiscountTotalHeads.get(ctx.getRandom().nextInt(localDiscountTotalHeads.size())),
                    localTotalHeads.get(ctx.getRandom().nextInt(localTotalHeads.size())),
                    localWithTaxTotalHeads.get(ctx.getRandom().nextInt(localWithTaxTotalHeads.size())),
                    localWithDiscountTotalHeads.get(ctx.getRandom().nextInt(localWithDiscountTotalHeads.size())),
                    localWithTaxAndDiscountTotalHeads.get(ctx.getRandom().nextInt(localWithTaxAndDiscountTotalHeads.size()))
                    );

            Boolean discountAvailable = false; // ctx.getRandom().nextInt(100) < 10; TODO fix this
            productContainer.setDiscountAvailable(discountAvailable);

            float price = 0;
            float taxRate = 0;
            float discountRate = 0;
            float priceWithTax = 0;
            float priceWithDiscount = 0;
            float priceWithTaxAndDiscount = 0;

            for (int i = 0; i < ctx.getRandom().nextInt(MAXPRODUCT - 1)+1; i++) {
                Product product = productsLangFiltered.get(ctx.getRandom().nextInt(productsLangFiltered.size()));
                product.setQuantity(ctx.getRandom().nextInt(MAXQTYPERPRODUCT - 1) + 1);
                product.setCurrency(ctx.getCurrency());

                price = product.getPrice();
                taxRate = HelperCommon.rand_uniform(0.0f, 0.2f);  // taxRate from 0% to 20%
                priceWithTax = HelperCommon.round(price * (1 + taxRate), 2);

                // add discounts for each item if discountAvailable
                priceWithDiscount = price;
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

                productContainer.addProduct(product);
            }

            Boolean shippingCostAvailable = ctx.getRandom().nextInt(100) < 15;
            productContainer.setShippingCostAvailable(shippingCostAvailable);
            // Shipping will always be last item if shippingCostAvailable
            if (shippingCostAvailable) {
                Product shippingProduct = new Product();
                price = 10 + ctx.getRandom().nextInt(200);
                taxRate = HelperCommon.rand_uniform(0.0f, 0.2f);
                priceWithTax = HelperCommon.round(price * (1 + taxRate), 2);

                shippingProduct.setName((ctx.getRandom().nextBoolean()) ? "Shipping": "SHIPPING");
                shippingProduct.setCurrency(ctx.getCurrency());
                shippingProduct.setQuantity(1);
                shippingProduct.setPrice(price);
                shippingProduct.setTaxRate(taxRate);
                shippingProduct.setDiscountRate(0);
                shippingProduct.setPriceWithTax(priceWithTax);
                shippingProduct.setPriceWithDiscount(price);
                shippingProduct.setPriceWithTaxAndDiscount(priceWithTax);

                productContainer.addProduct(shippingProduct);
            }

            Boolean taxRateAvailable = true;
            productContainer.setTaxRateAvailable(taxRateAvailable);

            return productContainer;
        }

    }

}
