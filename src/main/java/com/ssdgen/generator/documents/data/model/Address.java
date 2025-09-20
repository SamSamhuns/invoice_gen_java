package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Address {

    private static final Logger LOGGER = Logger.getLogger(Address.class.getName());

    private String line1;
    private String line2;
    private String line3;
    private String zip;
    private String city;
    private String country;

    public Address() {
    }

    public Address(String line1, String line2, String line3, String zip, String city, String country) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", line3='" + line3 + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<Address> {

        private static final String[] addressFiles = new String[] {
                "common/address/france.csv", "common/address/luxembourg.csv", "common/address/belgium.csv",
                "common/address/germany.csv", "common/address/ae_dubai_en.csv", "common/address/us_west.csv" };
        private static final Map<Address, String> addresses = new HashMap<>();
        {
            for (String addressFile : addressFiles) {
                try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(addressFile)) {
                    Reader in = new InputStreamReader(inputStream);
                    Iterable<CSVRecord> records = CSVFormat.newFormat(',').withFirstRecordAsHeader().parse(in);
                    for (CSVRecord record : records) {
                        String number = record.get("NUMBER");
                        String street = record.get("STREET");
                        String city = record.get("CITY");
                        String postcode = record.get("POSTCODE");
                        String country = record.get("COUNTRY");
                        if (street != null && !street.isEmpty()) {
                            Address address = new Address(number + ", " + street, "", "", postcode, city, country);
                            switch (addressFile) {
                                case "common/address/france.csv":
                                    addresses.put(address, "FR");
                                    break;
                                case "common/address/luxembourg.csv":
                                    addresses.put(address, "LU");
                                    break;
                                case "common/address/belgium.csv":
                                    addresses.put(address, "BE");
                                    break;
                                case "common/address/germany.csv":
                                    addresses.put(address, "DE");
                                    break;
                                case "common/address/ae_dubai_en.csv":
                                    addresses.put(address, "AE_en");
                                    break;
                                case "common/address/us_west.csv":
                                    addresses.put(address, "US_west");
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "unable to parse csv source: " + addressFile, e);
                }
            }
        }

        @Override
        public Address generate(GenerationContext ctx) {
            List<Address> goodAddresses = addresses.entrySet().stream()
                    .filter(comp -> comp.getValue().matches(ctx.getCountry())).map(comp -> comp.getKey())
                    .collect(Collectors.toList());
            Address address = goodAddresses.get(ctx.getRandom().nextInt(goodAddresses.size()));
            // TODO include a random line 3 with app No 2, Stage 3...
            return address;
        }
    }
}
