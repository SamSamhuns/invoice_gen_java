package com.ssdgen.generator.documents.data.model;

import java.util.List;
import java.util.ArrayList;


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

        public String getInvoiceDate() {
            return invoice_date;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoice_date = invoiceDate;
        }

        public String getInvoiceId() {
            return invoice_id;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoice_id = invoiceId;
        }

        public String getInvoiceOrderId() {
            return invoice_order_id;
        }

        public void setInvoiceOrderId(String invoiceOrderId) {
            this.invoice_order_id = invoiceOrderId;
        }

        public String getInvoiceDueDate() {
            return invoice_due_date;
        }

        public void setInvoiceDueDate(String invoiceDueDate) {
            this.invoice_due_date = invoiceDueDate;
        }

        public String getPaymentTerm() {
            return payment_term;
        }

        public void setPaymentTerm(String paymentTerm) {
            this.payment_term = paymentTerm;
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

        public String getVendorName() {
            return vendor_name;
        }

        public void setVendorName(String vendorName) {
            this.vendor_name = vendorName;
        }

        public String getVendorAddr() {
            return vendor_addr;
        }

        public void setVendorAddr(String vendorAddr) {
            this.vendor_addr = vendorAddr;
        }

        public String getVendorPOBox() {
            return vendor_po_box;
        }

        public void setVendorPOBox(String vendorPOBox) {
            this.vendor_po_box = vendorPOBox;
        }

        public String getVendorTrn() {
            return vendor_trn;
        }

        public void setVendorTrn(String vendorTrn) {
            this.vendor_trn = vendorTrn;
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

        public String getCustomerName() {
            return customer_name;
        }

        public void setCustomerName(String customerName) {
            this.customer_name = customerName;
        }

        public String getCustomerAddr() {
            return customer_addr;
        }

        public void setCustomerAddr(String customerAddr) {
            this.customer_addr = customerAddr;
        }

        public String getCustomerPOBox() {
            return customer_po_box;
        }

        public void setCustomerPOBox(String customerPOBox) {
            this.customer_po_box = customerPOBox;
        }

        public String getCustomerTrn() {
            return customer_trn;
        }

        public void setCustomerTrn(String customerTrn) {
            this.customer_trn = customerTrn;
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

        public String getShiptoName() {
            return shipto_name;
        }

        public void setShiptoName(String shiptoName) {
            this.shipto_name = shiptoName;
        }

        public String getShiptoPOBox() {
            return shipto_po_box;
        }

        public void setShiptoPOBox(String shiptoPOBox) {
            this.shipto_po_box = shiptoPOBox;
        }

        public String getShiptoAddr() {
            return shipto_addr;
        }

        public void setShiptoAddr(String shiptoAddr) {
            this.shipto_addr = shiptoAddr;
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

        public String getBankName() {
            return bank_name;
        }

        public void setBankName(String bankName) {
            this.bank_name = bankName;
        }

        public String getAccountName() {
            return account_name;
        }

        public void setAccountName(String accountName) {
            this.account_name = accountName;
        }

        public String getAccountNumber() {
            return account_number;
        }

        public void setAccountNumber(String accountNumber) {
            this.account_number = accountNumber;
        }

        public String getBranchAddress() {
            return branch_address;
        }

        public void setBranchAddress(String branchAddress) {
            this.branch_address = branchAddress;
        }

        public String getIbanNumber() {
            return iban_number;
        }

        public void setIbanNumber(String ibanNumber) {
            this.iban_number = ibanNumber;
        }

        public String getSwiftCode() {
            return swift_code;
        }

        public void setSwiftCode(String swiftCode) {
            this.swift_code = swiftCode;
        }

        public String getRoutingNumber() {
            return routing_number;
        }

        public void setRoutingNumber(String routingNumber) {
            this.routing_number = routingNumber;
        }

        public String getCustomerTrn() {
            return customer_trn;
        }

        public void setCustomerTrn(String customerTrn) {
            this.customer_trn = customerTrn;
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

        public String getSerialNumber() {
            return serial_number;
        }

        public void setSerialNumber(String serialNumber) {
            this.serial_number = serialNumber;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getItemCode() {
            return item_code;
        }

        public void setItemCode(String itemCode) {
            this.item_code = itemCode;
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

        public String getUnitPrice() {
            return unit_price;
        }

        public void setUnitPrice(String unitPrice) {
            this.unit_price = unitPrice;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getDiscountRate() {
            return discount_rate;
        }

        public void setDiscountRate(String discountRate) {
            this.discount_rate = discountRate;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getTaxRate() {
            return tax_rate;
        }

        public void setTaxRate(String taxRate) {
            this.tax_rate = taxRate;
        }

        public String getSubTotal() {
            return sub_total;
        }

        public void setSubTotal(String subTotal) {
            this.sub_total = subTotal;
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

        public String getTaxPrice() {
            return tax_price;
        }

        public void setTaxPrice(String taxPrice) {
            this.tax_price = taxPrice;
        }

        public String getTaxRate() {
            return tax_rate;
        }

        public void setTaxRate(String taxRate) {
            this.tax_rate = taxRate;
        }

        public String getDiscountPrice() {
            return discount_price;
        }

        public void setDiscountPrice(String discountPrice) {
            this.discount_price = discountPrice;
        }

        public String getDiscountRate() {
            return discount_rate;
        }

        public void setDiscountRate(String discountRate) {
            this.discount_rate = discountRate;
        }

        public String getSubtotalPrice() {
            return subtotal_price;
        }

        public void setSubtotalPrice(String subtotalPrice) {
            this.subtotal_price = subtotalPrice;
        }

        public String getTotalPrice() {
            return total_price;
        }

        public void setTotalPrice(String totalPrice) {
            this.total_price = totalPrice;
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

    private String title;
    private Invoice invoice;
    private Vendor vendor;
    private Billto billto;
    private Shipto shipto;
    private Paymentto paymentto;
    private List<Item> items;
    private Total total;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public InvoiceAnnotModel(Boolean areInitFieldsNull) {
        if (!areInitFieldsNull) {
            this.title = "";
            this.invoice = new Invoice();
            this.vendor = new Vendor();
            this.billto = new Billto();
            this.shipto = new Shipto();
            this.paymentto = new Paymentto();
            this.items = new ArrayList<Item>();
            this.total = new Total();
        }
    }
}
