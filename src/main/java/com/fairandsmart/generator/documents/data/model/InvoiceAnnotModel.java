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

/*
  This class should be exporte dto JSON with gson to get an json annotation file
    A fully populated sample annot along with field descriptions
    is provided at src/main/resources/invoices/sample_invoice_annotation.json
*/
public class InvoiceAnnotModel{

    public static class Invoice {
        private String invoice_date;
        private String invoice_id;
        private String invoice_order_id;
        private String invoice_due_date;
        private String payment_term;

        public String getInvoice_date() {
            return invoice_date;
        }

        public void setInvoice_date(String invoice_date) {
            this.invoice_date = invoice_date;
        }

        public String getInvoice_id() {
            return invoice_id;
        }

        public void setInvoice_id(String invoice_id) {
            this.invoice_id = invoice_id;
        }

        public String getInvoice_order_id() {
            return invoice_order_id;
        }

        public void setInvoice_order_id(String invoice_order_id) {
            this.invoice_order_id = invoice_order_id;
        }

        public String getInvoice_due_date() {
            return invoice_due_date;
        }

        public void setInvoice_due_date(String invoice_due_date) {
            this.invoice_due_date = invoice_due_date;
        }

        public String getPayment_term() {
            return payment_term;
        }

        public void setPayment_term(String payment_term) {
            this.payment_term = payment_term;
        }

        public Invoice() {

        }

        public Invoice(String invoice_date, String invoice_id, String invoice_order_id, String invoice_due_date, String payment_term) {
            this.invoice_date = invoice_date;
            this.invoice_id = invoice_id;
            this.invoice_order_id = invoice_order_id;
            this.invoice_due_date = invoice_due_date;
            this.payment_term = payment_term;
        }
    }

    public static class Vendor {
        private String vendor_name;
        private String vendor_addr;
        private String vendor_po_box;
        private String vendor_trn;

        public String getVendor_name() {
            return vendor_name;
        }

        public void setVendor_name(String vendor_name) {
            this.vendor_name = vendor_name;
        }

        public String getVendor_addr() {
            return vendor_addr;
        }

        public void setVendor_addr(String vendor_addr) {
            this.vendor_addr = vendor_addr;
        }

        public String getVendor_po_box() {
            return vendor_po_box;
        }

        public void setVendor_po_box(String vendor_po_box) {
            this.vendor_po_box = vendor_po_box;
        }

        public String getVendor_trn() {
            return vendor_trn;
        }

        public void setVendor_trn(String vendor_trn) {
            this.vendor_trn = vendor_trn;
        }

        public Vendor() {
        }

        public Vendor(String vendor_name, String vendor_addr, String vendor_po_box, String vendor_trn) {
            this.vendor_name = vendor_name;
            this.vendor_addr = vendor_addr;
            this.vendor_po_box = vendor_po_box;
            this.vendor_trn = vendor_trn;
        }
    }

    public static class Billto {
        private String customer_name;
        private String customer_addr;
        private String customer_po_box;
        private String customer_trn;

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getCustomer_addr() {
            return customer_addr;
        }

        public void setCustomer_addr(String customer_addr) {
            this.customer_addr = customer_addr;
        }

        public String getCustomer_po_box() {
            return customer_po_box;
        }

        public void setCustomer_po_box(String customer_po_box) {
            this.customer_po_box = customer_po_box;
        }

        public String getCustomer_trn() {
            return customer_trn;
        }

        public void setCustomer_trn(String customer_trn) {
            this.customer_trn = customer_trn;
        }

        public Billto() {
        }

        public Billto(String customer_name, String customer_addr, String customer_po_box, String customer_trn) {
            this.customer_name = customer_name;
            this.customer_addr = customer_addr;
            this.customer_po_box = customer_po_box;
            this.customer_trn = customer_trn;
        }
    }

    public static class Shipto {
        private String shipto_name;
        private String shipto_po_box;
        private String shipto_addr;

        public String getShipto_name() {
            return shipto_name;
        }

        public void setShipto_name(String shipto_name) {
            this.shipto_name = shipto_name;
        }

        public String getShipto_po_box() {
            return shipto_po_box;
        }

        public void setShipto_po_box(String shipto_po_box) {
            this.shipto_po_box = shipto_po_box;
        }

        public String getShipto_addr() {
            return shipto_addr;
        }

        public void setShipto_addr(String shipto_addr) {
            this.shipto_addr = shipto_addr;
        }

        public Shipto() {
        }

        public Shipto(String shipto_name, String shipto_po_box, String shipto_addr) {
            this.shipto_name = shipto_name;
            this.shipto_po_box = shipto_po_box;
            this.shipto_addr = shipto_addr;
        }
    }

    public static class Paymentto {
        private String bank_name;
        private String account_name;
        private String account_number;
        private String branch_address;
        private String iban_number;
        private String swift_code;
        private String routing_number;
        private String customer_trn;

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getBranch_address() {
            return branch_address;
        }

        public void setBranch_address(String branch_address) {
            this.branch_address = branch_address;
        }

        public String getIban_number() {
            return iban_number;
        }

        public void setIban_number(String iban_number) {
            this.iban_number = iban_number;
        }

        public String getSwift_code() {
            return swift_code;
        }

        public void setSwift_code(String swift_code) {
            this.swift_code = swift_code;
        }

        public String getRouting_number() {
            return routing_number;
        }

        public void setRouting_number(String routing_number) {
            this.routing_number = routing_number;
        }

        public String getCustomer_trn() {
            return customer_trn;
        }

        public void setCustomer_trn(String customer_trn) {
            this.customer_trn = customer_trn;
        }

        public Paymentto() {
        }

        public Paymentto(String bank_name, String account_name, String account_number, String branch_address, String iban_number, String swift_code, String routing_number, String customer_trn) {
            this.bank_name = bank_name;
            this.account_name = account_name;
            this.account_number = account_number;
            this.branch_address = branch_address;
            this.iban_number = iban_number;
            this.swift_code = swift_code;
            this.routing_number = routing_number;
            this.customer_trn = customer_trn;
        }
    }

    public static class Item {
        private String serial_number;
        private String description;
        private String item_code;
        private String quantity;
        private String unit;
        private String unit_price;
        private String discount;
        private String discount_rate;
        private String tax;
        private String tax_rate;
        private String sub_total;
        private String total;

        public String getSerial_number() {
            return serial_number;
        }

        public void setSerial_number(String serial_number) {
            this.serial_number = serial_number;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getItem_code() {
            return item_code;
        }

        public void setItem_code(String item_code) {
            this.item_code = item_code;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnit_price() {
            return unit_price;
        }

        public void setUnit_price(String unit_price) {
            this.unit_price = unit_price;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getDiscount_rate() {
            return discount_rate;
        }

        public void setDiscount_rate(String discount_rate) {
            this.discount_rate = discount_rate;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public void setTax_rate(String tax_rate) {
            this.tax_rate = tax_rate;
        }

        public String getSub_total() {
            return sub_total;
        }

        public void setSub_total(String sub_total) {
            this.sub_total = sub_total;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public Item() {
        }

        public Item(String serial_number, String description, String item_code, String quantity, String unit, String unit_price, String discount, String discount_rate, String tax, String tax_rate, String sub_total, String total) {
            this.serial_number = serial_number;
            this.description = description;
            this.item_code = item_code;
            this.quantity = quantity;
            this.unit = unit;
            this.unit_price = unit_price;
            this.discount = discount;
            this.discount_rate = discount_rate;
            this.tax = tax;
            this.tax_rate = tax_rate;
            this.sub_total = sub_total;
            this.total = total;
        }
    }

    public static class Total {
        private String tax_price;
        private String tax_rate;
        private String discount_price;
        private String discount_rate;
        private String subtotal_price;
        private String total_price;
        private String currency;

        public String getTax_price() {
            return tax_price;
        }

        public void setTax_price(String tax_price) {
            this.tax_price = tax_price;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public void setTax_rate(String tax_rate) {
            this.tax_rate = tax_rate;
        }

        public String getDiscount_price() {
            return discount_price;
        }

        public void setDiscount_price(String discount_price) {
            this.discount_price = discount_price;
        }

        public String getDiscount_rate() {
            return discount_rate;
        }

        public void setDiscount_rate(String discount_rate) {
            this.discount_rate = discount_rate;
        }

        public String getSubtotal_price() {
            return subtotal_price;
        }

        public void setSubtotal_price(String subtotal_price) {
            this.subtotal_price = subtotal_price;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Total() {
        }

        public Total(String tax_price, String tax_rate, String discount_price, String discount_rate, String subtotal_price, String total_price, String currency) {
            this.tax_price = tax_price;
            this.tax_rate = tax_rate;
            this.discount_price = discount_price;
            this.discount_rate = discount_rate;
            this.subtotal_price = subtotal_price;
            this.total_price = total_price;
            this.currency = currency;
        }
    }

    private Invoice invoice;
    private Vendor vendor;
    private Billto billto;
    private Shipto shipto;
    private Paymentto paymentto;
    private Item item;
    private Total total;

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Billto getBillto() {
        return billto;
    }

    public void setBillto(Billto billto) {
        this.billto = billto;
    }

    public Shipto getShipto() {
        return shipto;
    }

    public void setShipto(Shipto shipto) {
        this.shipto = shipto;
    }

    public Paymentto getPaymentto() {
        return paymentto;
    }

    public void setPaymentto(Paymentto paymentto) {
        this.paymentto = paymentto;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public InvoiceAnnotModel() {
    }
}
