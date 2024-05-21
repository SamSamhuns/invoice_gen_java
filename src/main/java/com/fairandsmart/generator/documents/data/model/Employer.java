package com.fairandsmart.generator.documents.data.model;

import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Employer {

    private static final Logger LOGGER = Logger.getLogger(Employer.class.getName());

    private Logo logo;
    private IDNumbers idNumbers;
    private String name;
    private String industry;
    private Address address;
    private ContactNumber contact;
    private String email;
    private String website;

    public Employer() {
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public IDNumbers getIdNumbers() { return idNumbers; }

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

    @Override
    public String toString() {
        return "Employer{" +
                "logo=" + logo +
                ", idNumbers=" + idNumbers +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", contact=" + contact +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<Employer> {

        private static final List<String> companiesFileList = Arrays.asList(
                "common/company/companies_ae_en.csv",
                "common/company/companies_fr.csv");
        private static final List<String> companiesCountryList = Arrays.asList(
                "AE_en",
                "FR");
        private static final Map<Employer, String> employers = new HashMap<>();
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
                            Employer emp = new Employer();
                            emp.setName(name);
                            emp.setWebsite(website);
                            emp.setIndustry(industry);
                            Address companyAddress = new Address(addressL1, addressL2, "", postcode, city, country);
                            emp.setAddress(companyAddress);
                            employers.put(emp, companiesCountry);
                        }
                    }
                } catch ( Exception e ) {
                    LOGGER.log(Level.SEVERE, "unable to parse csv source: " + companiesFile, e);
                }
            }
        }

        @Override
        public Employer generate(GenerationContext ctx) {
            List<Employer> goodEmployers = employers.entrySet().stream().filter(comp -> comp.getValue().matches(ctx.getCountry())).map(comp -> comp.getKey()).collect(Collectors.toList());
            Employer employer = goodEmployers.get(ctx.getRandom().nextInt(goodEmployers.size()));
            employer.setLogo(new Logo(ctx, employer.getName()));
            employer.setIdNumbers(new IDNumbers.Generator().generate(ctx));
            employer.setContact(new ContactNumber.Generator().generate(ctx));
            return employer;
        }
    }

}
