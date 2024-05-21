package com.fairandsmart.generator.documents.data.model;

import com.fairandsmart.generator.documents.data.generator.ModelGenerator;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.mifmif.common.regex.Generex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PaymentInfo {

    private static final Logger LOGGER = Logger.getLogger(Company.class.getName());

    private String addressHeader;
    private String labelPaymentTerm;
    private String valuePaymentTerm;
    private String labelPaymentType;
    private String valuePaymentType;
    private String labelBankName;
    private String valueBankName;
    private String labelAccountName;
    private String valueAccountName;
    private String labelAccountNumber;
    private String valueAccountNumber;
    private String labelBranchName;
    private String valueBranchName;
    private String labelIBANNumber;
    private String valueIBANNumber;
    private String labelRoutingNumber;
    private String valueRoutingNumber;
    private String labelSwiftCode;
    private String valueSwiftCode;
    private String labelAccountCurrency;
    private String valueAccountCurrency;

    public PaymentInfo(String addressHeader,
                       String labelPaymentTerm,
                       String valuePaymentTerm,
                       String labelPaymentType,
                       String valuePaymentType,
                       String labelBankName,
                       String valueBankName,
                       String labelAccountName,
                       String valueAccountName,
                       String labelAccountNumber,
                       String valueAccountNumber,
                       String labelBranchName,
                       String valueBranchName,
                       String labelIBANNumber,
                       String valueIBANNumber,
                       String labelRoutingNumber,
                       String valueRoutingNumber,
                       String labelSwiftCode,
                       String valueSwiftCode,
                       String labelAccountCurrency,
                       String valueAccountCurrency) {
        this.addressHeader = addressHeader;
        this.labelPaymentTerm = labelPaymentTerm;
        this.valuePaymentTerm = valuePaymentTerm;
        this.labelPaymentType = labelPaymentType;
        this.valuePaymentType = valuePaymentType;
        this.labelBankName = labelBankName;
        this.valueBankName = valueBankName;
        this.labelAccountName = labelAccountName;
        this.valueAccountName = valueAccountName;
        this.labelAccountNumber = labelAccountNumber;
        this.valueAccountNumber = valueAccountNumber;
        this.labelBranchName = labelBranchName;
        this.valueBranchName = valueBranchName;
        this.labelIBANNumber = labelIBANNumber;
        this.valueIBANNumber = valueIBANNumber;
        this.labelRoutingNumber = labelRoutingNumber;
        this.valueRoutingNumber = valueRoutingNumber;
        this.labelSwiftCode = labelSwiftCode;
        this.valueSwiftCode = valueSwiftCode;
        this.labelAccountCurrency = labelAccountCurrency;
        this.valueAccountCurrency = valueAccountCurrency;
    }

    public String getAddressHeader() {
        return addressHeader;
    }
    public String getLabelPaymentTerm() {
        return labelPaymentTerm;
    }
    public String getValuePaymentTerm() {
        return valuePaymentTerm;
    }
    public String getLabelPaymentType() {
        return labelPaymentType;
    }
    public String getValuePaymentType() {
        return valuePaymentType;
    }
    public String getLabelBankName() {
        return labelBankName;
    }
    public String getValueBankName() {
        return valueBankName;
    }
    public String getLabelAccountName() {
      return labelAccountName;
    }
    public String getValueAccountName() {
      return valueAccountName;
    }
    public String getLabelAccountNumber() {
        return labelAccountNumber;
    }
    public String getValueAccountNumber() {
        return valueAccountNumber;
    }
    public String getLabelBranchName() {
        return labelBranchName;
    }
    public String getValueBranchName() {
        return valueBranchName;
    }
    public String getLabelIBANNumber() {
        return labelIBANNumber;
    }
    public String getValueIBANNumber() {
        return valueIBANNumber;
    }
    public String getLabelRoutingNumber() {
        return labelRoutingNumber;
    }
    public String getValueRoutingNumber() {
        return valueRoutingNumber;
    }
    public String getLabelSwiftCode() {
        return labelSwiftCode;
    }
    public String getValueSwiftCode() {
        return valueSwiftCode;
    }
    public String getLabelAccountCurrency() {
      return labelAccountCurrency;
    }
    public String getValueAccountCurrency() {
      return valueAccountCurrency;
    }

    public void setAddressHeader(String addressHeader) {
        this.addressHeader = addressHeader;
    }
    public void setLabelPaymentTerm(String labelPaymentTerm) {
        this.labelPaymentTerm = labelPaymentTerm;
    }
    public void setValuePaymentTerm(String valuePaymentTerm) {
        this.valuePaymentTerm = valuePaymentTerm;
    }
    public void setLabelPaymentType(String labelPaymentType) {
        this.labelPaymentType = labelPaymentType;
    }
    public void setValuePaymentType(String valuePaymentType) {
        this.valuePaymentType = valuePaymentType;
    }
    public void setLabelBankName(String labelBankName) {
        this.labelBankName = labelBankName;
    }
    public void setValueBankName(String valueBankName) {
        this.valueBankName = valueBankName;
    }
    public void setLabelAccountName(String labelAccountName) {
      this.labelAccountName = labelAccountName;
    }
    public void setValueAccountName(String valueAccountName) {
      this.valueAccountName = valueAccountName;
    }
    public void setLabelAccountNumber(String labelAccountNumber) {
        this.labelAccountNumber = labelAccountNumber;
    }
    public void setValueAccountNumber(String valueAccountNumber) {
        this.valueAccountNumber = valueAccountNumber;
    }
    public void setLabelBranchName(String labelBranchName) {
        this.labelBranchName = labelBranchName;
    }
    public void setValueBranchName(String valueBranchName) {
        this.valueBranchName = valueBranchName;
    }
    public void setLabelIBANNumber(String labelIBANNumber) {
        this.labelIBANNumber = labelIBANNumber;
    }
    public void setValueIBANNumber(String valueIBANNumber) {
        this.valueIBANNumber = valueIBANNumber;
    }
    public void setLabelRoutingNumber(String labelRoutingNumber) {
        this.labelRoutingNumber = labelRoutingNumber;
    }
    public void setValueRoutingNumber(String valueRoutingNumber) {
        this.valueRoutingNumber = valueRoutingNumber;
    }
    public void setLabelSwiftCode(String labelSwiftCode) {
        this.labelSwiftCode = labelSwiftCode;
    }
    public void setValueSwiftCode(String valueSwiftCode) {
        this.valueSwiftCode = valueSwiftCode;
    }
    public void setLabelAccountCurrency(String labelAccountCurrency) {
      this.labelAccountCurrency = labelAccountCurrency;
    }
    public void setValueAccountCurrency(String valueAccountCurrency) {
      this.valueAccountCurrency = valueAccountCurrency;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "addressHeader='" + addressHeader + '\'' +
                ", labelPaymentTerm='" + labelPaymentTerm + '\'' +
                ", valuePaymentTerm='" + valuePaymentTerm + '\'' +
                ", labelPaymentType='" + labelPaymentType + '\'' +
                ", valuePaymentType='" + valuePaymentType + '\'' +
                ", labelBankName='" + labelBankName + '\'' +
                ", valueBankName='" + valueBankName + '\'' +
                ", labelAccountName='" + labelAccountName + '\'' +
                ", valueAccountName='" + valueAccountName + '\'' +
                ", labelAccountNumber='" + labelAccountNumber + '\'' +
                ", valueAccountNumber='" + valueAccountNumber + '\'' +
                ", labelBranchName='" + labelBranchName + '\'' +
                ", valueBranchName='" + valueBranchName + '\'' +
                ", labelIBANNumber='" + labelIBANNumber + '\'' +
                ", valueIBANNumber='" + valueIBANNumber + '\'' +
                ", labelRoutingNumber='" + labelRoutingNumber + '\'' +
                ", valueRoutingNumber='" + valueRoutingNumber + '\'' +
                ", labelSwiftCode='" + labelSwiftCode + '\'' +
                ", valueSwiftCode='" + valueSwiftCode + '\'' +
                ", labelAccountCurrency='" + labelAccountCurrency + '\'' +
                ", valueAccountCurrency='" + valueAccountCurrency + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<PaymentInfo> {

        private static final Map<String, String> addressHeaders = new HashMap<>();
        private static final Map<String, String> labelsPaymentTerm = new HashMap<>();
        private static final Map<String, String> valuesPaymentTerm = new HashMap<>();
        private static final Map<String, String> labelsPaymentType = new HashMap<>();
        private static final Map<String, String> valuesPaymentType = new HashMap<>();
        private static final Map<String, String> labelsBankName = new HashMap<>();
        private static final Map<String, String> valuesBankName = new HashMap<>();
        private static final Map<String, String> labelsAccountName = new HashMap<>();
        private static final Map<String, String> valuesAccountName = new HashMap<>();
        private static final Map<String, String> labelsAccountNumber = new HashMap<>();
        private static final Map<String, String> valuesAccountNumber = new HashMap<>();
        private static final Map<String, String> labelsBranchName = new HashMap<>();
        private static final Map<String, String> valuesBranchName = new HashMap<>();
        private static final Map<String, String> labelsIBANNumber = new HashMap<>();
        private static final Map<String, String> valuesIBANNumber = new HashMap<>();
        private static final Map<String, String> labelsRoutingNumber = new HashMap<>();
        private static final Map<String, String> valuesRoutingNumber = new HashMap<>();
        private static final Map<String, String> labelsSwiftCode = new HashMap<>();
        private static final Map<String, String> valuesSwiftCode = new HashMap<>();
        private static final Map<String, String> labelsAccountCurrency = new HashMap<>();
        // Currency value is used from ctx.getCurrency()

        {
            addressHeaders.put("Adresse de Facturation", "fr");

            addressHeaders.put("Pay To", "en");
            addressHeaders.put("Wire To", "en");
            addressHeaders.put("ACH Payment Instruction", "en");
            addressHeaders.put("Wire Instruction", "en");
            addressHeaders.put("Payment", "en");
            addressHeaders.put("Payment Address", "en");
            addressHeaders.put("Payment Instruction", "en");
            addressHeaders.put("Please Wire Funds To", "en");
        }
        {
            labelsPaymentTerm.put("Modalités de Paiement", "fr");

            labelsPaymentTerm.put("Terms", "en");
            labelsPaymentTerm.put("Payment Terms", "en");
        }
        {
            valuesPaymentTerm.put("Fin du Mois", "fr");

            valuesPaymentTerm.put("[1-5] MD", "en");
            valuesPaymentTerm.put("Net (7|10|15|30|60|90)( days| )", "en");
            valuesPaymentTerm.put("([1-9]|[1-2][0-9]|30) (MFI|of Month Following Inv Date)", "en");
            valuesPaymentTerm.put("(EOM|End of month)", "en");
            valuesPaymentTerm.put("(Cash|Pay) (on delivery|next delivery|before shipment|in advance|with order)", "en");
        }
        {
            labelsPaymentType.put("Payment de paiement", "fr");
            labelsPaymentType.put("Moyen de paiement", "fr");
            labelsPaymentType.put("Mode de règlement", "fr");

            labelsPaymentType.put("Payment type", "en");
            labelsPaymentType.put("Payment means", "en");
            labelsPaymentType.put("Pay through", "en");
            labelsPaymentType.put("Pay with", "en");
            labelsPaymentType.put("Pay by", "en");
        }
        {
            valuesPaymentType.put("Paypal ", "fr");
            valuesPaymentType.put("CB", "fr");
            valuesPaymentType.put("Virement", "fr");
            valuesPaymentType.put("Chèque", "fr");

            valuesPaymentType.put("Paypal", "en");
            valuesPaymentType.put("Credit Card", "en");
            valuesPaymentType.put("ACH Transfer", "en");
            valuesPaymentType.put("Wire Transfer", "en");
            valuesPaymentType.put("Bank Transfer", "en");
            valuesPaymentType.put("Cheque", "en");
        }
        {
            labelsBankName.put("Nom de banque", "fr");

            labelsBankName.put("Bank", "en");
            labelsBankName.put("Bank Name", "en");
        }
        // valueBankName is loaded from bank csv dataset
        {
            labelsAccountName.put("Nom du compte", "fr");

            labelsAccountName.put("Account Name", "en");
            labelsAccountName.put("A/C Name", "en");
        }
        // valueAccountName is same as Vendor/Company Name
        {
            labelsAccountNumber.put("Numéro de compte", "fr");

            labelsAccountNumber.put("Account Num", "en");
            labelsAccountNumber.put("Account #", "en");
            labelsAccountNumber.put("A/C Num", "en");
            labelsAccountNumber.put("A/C No", "en");
        }
        {
            valuesAccountNumber.put("[0-9]{12} ", "FR");

            valuesAccountNumber.put("[0-9]{13} ", "US");

            valuesAccountNumber.put("[0-9]{13}", "AE_en");
            valuesAccountNumber.put("[0]{4}[0-9]{9}", "AE_en");
        }
        {
            labelsBranchName.put("Nom de la filiale", "fr");

            labelsBranchName.put("Branch", "en");
            labelsBranchName.put("Branch Name", "en");
            labelsBranchName.put("Branch Address", "en");
        }
        // valueBranchName is loaded from bank csv dataset
        {
            labelsIBANNumber.put("IBAN ", "fr");
            labelsIBANNumber.put("IBAN No ", "fr");
            labelsIBANNumber.put("IBAN Num ", "fr");

            labelsIBANNumber.put("IBAN", "en");
            labelsIBANNumber.put("IBAN #", "en");
            labelsIBANNumber.put("IBAN No", "en");
            labelsIBANNumber.put("IBAN Num", "en");
        }
        {
            valuesIBANNumber.put("FR[0-9]{8}", "FR");

            valuesIBANNumber.put("US[0-9]{8}", "US");

            valuesIBANNumber.put("AE[0-9]{8}", "AE_en");
            valuesIBANNumber.put("AE[0]{3}[0-9]{5}", "AE_en");
        }
        {
            labelsRoutingNumber.put("Numéro de routage", "fr");

            labelsRoutingNumber.put("Routing #", "en");
            labelsRoutingNumber.put("Routing No", "en");
            labelsRoutingNumber.put("Routing Num", "en");
            labelsRoutingNumber.put("Routing Code", "en");
        }
        {
            valuesRoutingNumber.put("[0-9]{9} ", "FR");

            valuesRoutingNumber.put("[0-9]{9}", "AE_en");
        }
        {
            labelsSwiftCode.put("Code Rapide", "fr");

            labelsSwiftCode.put("Swift", "en");
            labelsSwiftCode.put("Swift #", "en");
            labelsSwiftCode.put("Swift Code", "en");
            labelsSwiftCode.put("Swift Number", "en");
        }
        {
            valuesSwiftCode.put("[A-Z]{8,12}", "FR");

            valuesSwiftCode.put("[A-Z]{8,13}", "AE_en");
        }
        {
            labelsAccountCurrency.put("Devise", "fr");

            labelsAccountCurrency.put("Cur", "en");
            labelsAccountCurrency.put("CUR", "en");
            labelsAccountCurrency.put("Currency", "en");
        }

        // Notes: fmt is "csv file, country, language"
        List<List<String>> banksCsvCountryLangList = Arrays.asList(
                Arrays.asList("common/bank/ae_en.csv", "AE_en", "en"),
                Arrays.asList("common/bank/fr.csv", "FR", "fr"));
        private static final Map<Company, String> banksInfo = new HashMap<>();
        {
            for (int i=0; i<banksCsvCountryLangList.size(); i++) {
                String banksFile = banksCsvCountryLangList.get(i).get(0);
                String bankCountry = banksCsvCountryLangList.get(i).get(1);
                String bankLang = banksCsvCountryLangList.get(i).get(2);
                try {
                    Reader in = new InputStreamReader(Logo.class.getClassLoader().getResourceAsStream(banksFile));
                    Iterable<CSVRecord> records = CSVFormat.newFormat(';').withQuote('"').withFirstRecordAsHeader().parse(in);
                    for (CSVRecord record : records) {
                        String bankName = record.get("name");
                        String address = record.get("address");
                        String country = bankCountry;

                        Company bankCompany = new Company();
                        bankCompany.setName(bankName);
                        bankCompany.setIndustry("banking");
                        bankCompany.setAddress(new Address(address, "", "", new Generex("[0-9]{5}").random(), address, country));

                        banksInfo.put(bankCompany, bankLang);
                    }
                } catch ( Exception e ) {
                    LOGGER.log(Level.SEVERE, "unable to parse csv source: " + banksFile, e);
                }
            }
        }

        @Override
        public PaymentInfo generate(GenerationContext ctx) {
            List<String> filteredAddressHeaders = addressHeaders.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsPaymentTerm = labelsPaymentTerm.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesPaymentTerm = valuesPaymentTerm.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsPaymentType = labelsPaymentType.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesPaymentType = valuesPaymentType.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsBankName = labelsBankName.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsAccountName = labelsAccountName.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsAccountNumber = labelsAccountNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesAccountNumber = valuesAccountNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsBranchName = labelsBranchName.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsIBANNumber = labelsIBANNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesIBANNumber = valuesIBANNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsRoutingNumber = labelsRoutingNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesRoutingNumber = valuesRoutingNumber.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsSwiftCode = labelsSwiftCode.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredValuesSwiftCode = valuesSwiftCode.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getCountry())).map(Map.Entry::getKey).collect(Collectors.toList());
            List<String> filteredLabelsAccountCurrency = labelsAccountCurrency.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());

            // select a random bank
            List<Company> filteredBanksCompanies = banksInfo.entrySet().stream().filter(entry -> entry.getValue().equals(ctx.getLanguage())).map(Map.Entry::getKey).collect(Collectors.toList());
            Company filteredBankCompany = filteredBanksCompanies.get(ctx.getRandom().nextInt(filteredBanksCompanies.size()));

            // select or generate the fields
            String filteredAddressHeader = filteredAddressHeaders.get(ctx.getRandom().nextInt(filteredAddressHeaders.size()));

            String filteredLabelPaymentTerm = new Generex(filteredLabelsPaymentTerm.get(ctx.getRandom().nextInt(filteredLabelsPaymentTerm.size()))).random();
            String filteredValuePaymentTerm = new Generex(filteredValuesPaymentTerm.get(ctx.getRandom().nextInt(filteredValuesPaymentTerm.size()))).random();

            String filteredLabelPaymentType = filteredLabelsPaymentType.get(ctx.getRandom().nextInt(filteredLabelsPaymentType.size()));
            String filteredValuePaymentType = filteredValuesPaymentType.get(ctx.getRandom().nextInt(filteredValuesPaymentType.size()));

            String filteredLabelBankName = filteredLabelsBankName.get(ctx.getRandom().nextInt(filteredLabelsBankName.size()));
            String filteredValueBankName = filteredBankCompany.getName();

            String filteredLabelAccountName = filteredLabelsAccountName.get(ctx.getRandom().nextInt(filteredLabelsAccountName.size()));
            String filteredValueAccountName = "";  // valueAccountName is same as Vendor/Company Name and assigned later

            String filteredLabelAccountNumber = filteredLabelsAccountNumber.get(ctx.getRandom().nextInt(filteredLabelsAccountNumber.size()));
            String filteredValueAccountNumber = new Generex(filteredValuesAccountNumber.get(ctx.getRandom().nextInt(filteredValuesAccountNumber.size()))).random();

            String filteredLabelBranchName = filteredLabelsBranchName.get(ctx.getRandom().nextInt(filteredLabelsBranchName.size()));
            String filteredvalueBranchName = filteredBankCompany.getAddress().getLine1();

            String filteredLabelIBANNumber = filteredLabelsIBANNumber.get(ctx.getRandom().nextInt(filteredLabelsIBANNumber.size()));
            String filteredValueIBANNumber = new Generex(filteredValuesIBANNumber.get(ctx.getRandom().nextInt(filteredValuesIBANNumber.size()))).random() + filteredValueAccountNumber; // IBAN number uses the Account Number as a suffix

            String filteredLabelRoutingNumber = filteredLabelsRoutingNumber.get(ctx.getRandom().nextInt(filteredLabelsRoutingNumber.size()));
            String filteredValueRoutingNumber = new Generex(filteredValuesRoutingNumber.get(ctx.getRandom().nextInt(filteredValuesRoutingNumber.size()))).random();

            String filteredLabelSwiftCode = filteredLabelsSwiftCode.get(ctx.getRandom().nextInt(filteredLabelsSwiftCode.size()));
            String filteredValueSwiftCode = new Generex(filteredValuesSwiftCode.get(ctx.getRandom().nextInt(filteredValuesSwiftCode.size()))).random();

            String filteredLabelAccountCurrency = filteredLabelsAccountCurrency.get(ctx.getRandom().nextInt(filteredLabelsAccountCurrency.size()));
            String filteredValueAccountCurrency = ctx.getCurrency();

            return new PaymentInfo(filteredAddressHeader,
                                   filteredLabelPaymentTerm,
                                   filteredValuePaymentTerm,
                                   filteredLabelPaymentType,
                                   filteredValuePaymentType,
                                   filteredLabelBankName,
                                   filteredValueBankName,
                                   filteredLabelAccountName,
                                   filteredValueAccountName,
                                   filteredLabelAccountNumber,
                                   filteredValueAccountNumber,
                                   filteredLabelBranchName,
                                   filteredvalueBranchName,
                                   filteredLabelIBANNumber,
                                   filteredValueIBANNumber,
                                   filteredLabelRoutingNumber,
                                   filteredValueRoutingNumber,
                                   filteredLabelSwiftCode,
                                   filteredValueSwiftCode,
                                   filteredLabelAccountCurrency,
                                   filteredValueAccountCurrency);
        }

    }
}
