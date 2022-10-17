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

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.github.javafaker.Faker;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private IDNumbers idNumbers;

    private String billingHead;
    private String billingName;
    private Address billingAddress;
    private ContactNumber billingContactNumber;

    private String shippingHead;
    private String shippingName;
    private Address shippingAddress;
    private ContactNumber shippingContactNumber;

    public Client(String billingHead, String billingName, Address billingAddress, ContactNumber billingContactNumber,
                  String shippingHead, String shippingName, Address shippingAddress, ContactNumber shippingContactNumber) {
        this.billingHead = billingHead;
        this.billingName = billingName;
        this.billingAddress = billingAddress;
        this.billingContactNumber = billingContactNumber;
        this.shippingHead = shippingHead;
        this.shippingName = shippingName;
        this.shippingAddress = shippingAddress;
        this.shippingContactNumber = shippingContactNumber;
        // Note idNumbers must be assigned later
    }

    public IDNumbers getIdNumbers() {
        return idNumbers;
    }

    public void setIdNumbers(IDNumbers idNumbers) {
        this.idNumbers = idNumbers;
    }

    public String getBillingHead() {
        return billingHead;
    }

    public void setBillingHead(String billingHead) {
        this.billingHead = billingHead;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public ContactNumber getBillingContactNumber() {
        return billingContactNumber;
    }

    public void setBillingContactNumber(ContactNumber billingContactNumber) {
        this.billingContactNumber = billingContactNumber;
    }

    public String getShippingHead() {
        return shippingHead;
    }

    public void setShippingHead(String shippingHead) {
        this.shippingHead = shippingHead;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ContactNumber getShippingContactNumber() {
        return shippingContactNumber;
    }

    public void setShippingContactNumber(ContactNumber shippingContactNumber) {
        this.shippingContactNumber = shippingContactNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idNumbers='" + idNumbers + '\'' +
                "billingHead='" + billingHead + '\'' +
                ", billingName=" + billingName +
                ", billingAddress=" + billingAddress +
                ", billingContactNumber=" + billingContactNumber +
                "shippingHead='" + shippingHead + '\'' +
                ", shippingName=" + shippingName +
                ", shippingAddress=" + shippingAddress +
                ", shippingContactNumber=" + shippingContactNumber +
                '}';
    }

    public static class Generator implements ModelGenerator<Client> {

        private static final Map<String, String> billingHeads = new LinkedHashMap<>();
        private static final Map<String, String> shippingHeads = new LinkedHashMap<>();

        {
            billingHeads.put("Destinataire", "fr");
            billingHeads.put("Adresse Facturation", "fr");
            billingHeads.put("Adresse de Facturation", "fr");

            billingHeads.put("Invoice To", "en");
            billingHeads.put("Invoiced To", "en");
            billingHeads.put("Invoice Address", "en");
            billingHeads.put("Bill To", "en");
            billingHeads.put("Billed To", "en");
            billingHeads.put("Billing Address", "en");
            billingHeads.put("Sold To", "en");
            billingHeads.put("Buyer", "en");
            billingHeads.put("Purchaser", "en");
        }
        {
            shippingHeads.put("Livraison à", "fr");
            shippingHeads.put("Adresse Livraison", "fr");
            shippingHeads.put("Adresse de Livraison", "fr");

            shippingHeads.put("Delivery To", "en");
            shippingHeads.put("Deliver To", "en");
            shippingHeads.put("Delivery Address", "en");
            shippingHeads.put("Ship To", "en");
            shippingHeads.put("Shipped To", "en");
            shippingHeads.put("Shipping Address", "en");
            shippingHeads.put("Send To", "en");
            shippingHeads.put("Shipping Receiver", "en");
            shippingHeads.put("Delivery Receiver", "en");
        }
        {
          // billingHeads and shippingHeads must have the same number of corresponding values
          assert billingHeads.size() == shippingHeads.size();
        }

        @Override
        public Client generate(GenerationContext ctx) {
            Faker faker = Faker.instance(Locale.forLanguageTag(ctx.getLocale()));
            String billingName = faker.name().fullName();
            String shippingName = billingName;
            Address billingAddress = new Address.Generator().generate(ctx);
            Address shippingAddress = billingAddress;
            ContactNumber billingContactNumber = new ContactNumber.Generator().generate(ctx);
            ContactNumber shippingContactNumber = billingContactNumber;

            // shippingAddress & shippingContactNumber is different
            if ( ctx.getRandom().nextInt(50) < 10 ) {
                shippingAddress = new Address.Generator().generate(ctx);
                shippingContactNumber = new ContactNumber.Generator().generate(ctx);
            }
            // shippingName is different
            if ( ctx.getRandom().nextInt(50) < 5 ) {
                shippingName = faker.name().fullName();
            }
            // shippingName is set to same as billing and Address is empty
            if ( ctx.getRandom().nextInt(50) < 4 ) {
                shippingName = "Same as Billing Address";
                shippingAddress = new Address("", "", "", "", "", "");
                shippingContactNumber = new ContactNumber("", "", "", "");
            }

            // For Address Heads
            List<String> localizedBillHeads = billingHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> localizedShipHeads = shippingHeads.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            assert localizedBillHeads.size() == localizedShipHeads.size();
            int idxA = ctx.getRandom().nextInt(localizedBillHeads.size()); // Note: Only one index for both shipping & billing, to retrieve similar format heads!
            Client genClient = new Client(
                    localizedBillHeads.get(idxA), billingName, billingAddress, billingContactNumber,
                    localizedShipHeads.get(idxA), shippingName, shippingAddress, shippingContactNumber);

            // shippingAddress is not present
            if ( ctx.getRandom().nextInt(50) < 2 ) {
                genClient.setShippingHead("");
                genClient.setShippingName("");
                genClient.setShippingAddress(new Address("", "", "", "", "", ""));
                genClient.setShippingContactNumber(new ContactNumber("", "", "", ""));
            }
            // assign idNumbers, ie. vatLabel and vatValue
            genClient.setIdNumbers(new IDNumbers.Generator().generate(ctx));

            return genClient;
        }
    }
}
