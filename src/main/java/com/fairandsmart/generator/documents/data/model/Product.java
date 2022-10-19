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

public class Product {


    private String name;
    private String description;
    private String ean;
    private String sku;
    private String brand;
    private String language;
    private String currency;

    private float price;
    // assigned during generation
    private int quantity;
    private String code;
    private float taxRate;
    private float discountRate;
    private float priceWithTax;
    private float priceWithDiscount;
    private float priceWithTaxAndDiscount;

    // Added later
    private String taxType;
    private int taxReference;
    private String deliveryType;

    public Product() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getPrice() {
        return price;
    }

    public String getFormatedPrice() {
        return String.format("%.2f", this.getPrice()) + " " + currency;
    }

    // Assigned during generation

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public int getTaxReference() {
        return taxReference;
    }

    public void setTaxReference(int taxReference) {
        this.taxReference = taxReference;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public float getTaxRate() {
        return taxRate;
    }

    public String getFormatedTaxRate() {
        return String.format("%.2f", this.getTaxRate() * 100) + "%";
    }

    public void setTaxRate(float taxRate) {
        this.taxRate = taxRate;
    }

    public float getDiscountRate() {
        return discountRate;
    }

    public String getFormatedDiscountRate() {
        return String.format("%.2f", this.getDiscountRate() * 100) + "%";
    }

    public void setDiscountRate(float discountRate) {
        this.discountRate = discountRate;
    }

    // Price with Tax

    public float getPriceWithTax() {
        return priceWithTax;
    }

    public String getFormatedPriceWithTax() {
        return String.format("%.2f", this.getPriceWithTax()) + " " + currency;
    }

    public void setPriceWithTax(float priceWithTax) {
        this.priceWithTax = priceWithTax;
    }

    // Price with Discount

    public float getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public String getFormatedPriceWithDiscount() {
        return String.format("%.2f", this.getPriceWithDiscount()) + " " + currency;
    }

    public void setPriceWithDiscount(float priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    // Price with Tax and Discount

    public float getPriceWithTaxAndDiscount() {
        return priceWithTaxAndDiscount;
    }

    public String getFormatedPriceWithTaxAndDiscount() {
        return String.format("%.2f", this.getPriceWithTaxAndDiscount()) + " " + currency;
    }

    public void setPriceWithTaxAndDiscount(float priceWithTaxAndDiscount) {
        this.priceWithTaxAndDiscount = priceWithTaxAndDiscount;
    }

    /* getter methods that calculate from existing params */

    // Total Price
    public float getTotalPrice() {
        return price * quantity;
    }

    public String getFormatedTotalPrice() {
        return String.format("%.2f", this.getTotalPrice()) + " " + currency;
    }

    // Total Price with Tax
    public float getTotalPriceWithTax() {
        return priceWithTax * quantity;
    }

    public String getFormatedTotalPriceWithTax() {
        return String.format("%.2f", this.getTotalPriceWithTax()) + " " + currency;
    }

    // Total Price with Discount
    public float getTotalPriceWithDiscount() {
        return priceWithDiscount * quantity;
    }

    public String getFormatedTotalPriceWithDiscount() {
        return String.format("%.2f", this.getTotalPriceWithDiscount()) + " " + currency;
    }

    // Total Price with Tax and Discount (FINAL price)
    public float getTotalPriceWithTaxAndDiscount() {
        return priceWithTaxAndDiscount * quantity;
    }

    public String getFormatedTotalPriceWithTaxAndDDiscount() {
        return String.format("%.2f", this.getTotalPriceWithTaxAndDiscount()) + " " + currency;
    }

    // Total Tax
    public float getTotalTax() {
        return this.getTotalPriceWithTax() - this.getTotalPrice();
    }

    public String getFormatedTotalTax() {
        return String.format("%.2f", this.getTotalTax()) + " " + currency;
    }

    // Total Discount
    public float getTotalDiscount() {
        return this.getTotalPrice() - this.getTotalPriceWithDiscount();
    }

    public String getFormatedTotalDiscount() {
        return String.format("%.2f", this.getTotalDiscount()) + " " + currency;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description ='" + description + '\'' +
                ", ean='" + ean + '\'' +
                ", sku='" + sku + '\'' +
                ", brand='" + brand + '\'' +
                ", currency='" + currency + '\'' +
                ", taxType='" + taxType + '\'' +
                ", taxReference=" + taxReference +
                ", deliveryType='" + deliveryType + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", taxRate=" + taxRate +
                ", discountRate=" + discountRate +
                ", priceWithTax=" + priceWithTax +
                ", priceWithDiscount=" + priceWithDiscount +
                ", priceWithTaxAndDiscount=" + priceWithTaxAndDiscount +
                '}';
    }

    /*
        "brand": "Samsung",
        "categories": [
            "Large household appliances",
            "Washing machine",
            "Washing machine with porthole"
        ],
        "ean": "8806088499048",
        "name": "Washing machine with porthole Samsung ADD WASH WW90K4437YW",
        "price": 399.167,
        "sku": "000000000001079579"
    */

}
