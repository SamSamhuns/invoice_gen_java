package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Company {

    private static final Logger LOGGER = Logger.getLogger(Company.class.getName());

    private Logo logo;
    private Stamp stamp;
    private IDNumbers idNumbers;
    private String name;
    private String industry;
    private Address address;
    private ContactNumber contact;
    private String email;
    private String website;
    private Signature signature;
    private String addressHeader;

    public Company() {
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    public IDNumbers getIdNumbers() {
        return idNumbers;
    }

    public void setIdNumbers(IDNumbers idNumbers) {
        this.idNumbers = idNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressHeader() {
        return addressHeader;
    }

    public void setAddressHeader(String addressHeader) {
        this.addressHeader = addressHeader;
    }

    public ContactNumber getContact() {
        return contact;
    }

    public void setContact(ContactNumber contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "Company{" +
                "logo=" + logo +
                ", idNumbers=" + idNumbers +
                ", name='" + name + '\'' +
                ", industry='" + industry + '\'' +
                ", address=" + address +
                ", contact=" + contact +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<Company> {

        private static final Map<String, String> addressHeaders = new LinkedHashMap<>();

        {
            addressHeaders.put("Le Fournisseur", "fr");

            addressHeaders.put("TAX INVOICE", "en");
            addressHeaders.put("Sold by", "en");
            addressHeaders.put("Supplier", "en");
            addressHeaders.put("Seller", "en");
            addressHeaders.put("Vendor", "en");
            addressHeaders.put("From", "en");
        }

        private static final List<String> companiesFileList = Arrays.asList(
                "common/company/companies_ae_en.csv",
                "common/company/companies_fr.csv");
        private static final List<String> companiesCountryList = Arrays.asList(
                "AE_en",
                "FR");
        private static final Map<Company, String> companies = new HashMap<>();
        {
            assert companiesFileList.size() == companiesCountryList.size();
            for (int i=0; i<companiesFileList.size(); i++) {
                String companiesFile = companiesFileList.get(i);
                String companiesCountry = companiesCountryList.get(i);
                try {
                    Reader in = new InputStreamReader(Logo.class.getClassLoader().getResourceAsStream(companiesFile));
                    Iterable<CSVRecord> records = CSVFormat.newFormat(';').withQuote('"').withFirstRecordAsHeader().parse(in);
                    for (CSVRecord record : records) {
                        String name = record.get("name");
                        String website = record.get("domain");
                        String industry = record.get("industry");
                        String addressL1 = record.get("address1");
                        String addressL2 = record.get("address2");
                        String postcode = record.get("postcode");
                        String city = record.get("city");
                        String country = record.get("country");
                        if ( name.length() > 3 ) {
                            Company comp = new Company();
                            comp.setName(name);
                            comp.setWebsite(website);
                            comp.setIndustry(industry);
                            Address companyAddress = new Address(addressL1, addressL2, "", postcode, city, country);
                            comp.setAddress(companyAddress);
                            companies.put(comp, companiesCountry);
                        }
                    }
                } catch ( Exception e ) {
                    LOGGER.log(Level.SEVERE, "unable to parse csv source: " + companiesFile, e);
                }
            }
        }

        @Override
        public Company generate(GenerationContext ctx) {
            List<String> filteredAddressHeaders = addressHeaders.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<Company> goodCompanies = companies.entrySet().stream().filter(comp -> comp.getValue().matches(ctx.getCountry())).map(comp -> comp.getKey()).collect(Collectors.toList());
            Company company = goodCompanies.get(ctx.getRandom().nextInt(goodCompanies.size()));
            company.setLogo(new Logo(ctx, company.getName()));
            company.setAddressHeader(filteredAddressHeaders.get(ctx.getRandom().nextInt(filteredAddressHeaders.size())));
            company.setStamp(new Stamp(ctx, company.getName()));
            company.setIdNumbers(new IDNumbers.Generator().generate(ctx));
            company.setContact(new ContactNumber.Generator().generate(ctx));
            company.setSignature(new Signature.Generator().generate(ctx));
            return company;
        }
    }

}
