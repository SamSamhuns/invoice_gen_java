package com.ssdgen.generator.documents.data.model;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.generator.ModelGenerator;
import com.github.javafaker.Faker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

public class EmployeeInformation {

    private static final Logger LOGGER = Logger.getLogger(EmployeeInformation.class.getName());
    private static final DecimalFormat dfd = new DecimalFormat("0.00");
    private final Random random= new Random();
    private String employeCode;
    private String registrationNumber; // Matricule
    private String socialSecurityNumber;

    private String assignment ; //affectation
    private String employment;
    private String classification;
    private String echelon;
    private String contratType;
    private String arrivalDate;
    private String socialSecurityCeiling; //plafonSecuriteSociale
    private String timetable; //horaire
    private String hourlyRate; //tauxHoraire
    private String mincoef;
    private String coef;
    private String monthlyPay; //salaireMensuel
    private String monthlyPayRef; //salaireMensuelRef
    private String categoryLabel; //libCategorie
    private String dateSeniority; // dateEnciennte
    private String localisation;
    private String releaseDate; //dateSortie
    private String meansOfPayment; //moyenPaiement
    private String PaymentDate;
    private Date periode;
    private String conv;

    public EmployeeInformation(String employeCode, String registrationNumber, String socialSecurityNumber, String assignment,
                               String employment, String classification, String echelon, String contratType,
                               String arrivalDate, String socialSecurityCeiling, String timetable, String hourlyRate,
                               String mincoef, String coef, String monthlyPay, String monthlyPayRef,String categoryLabel,
                               String dateSeniority, String localisation, String releaseDate, String meansOfPayment, String PaymentDate,Date periode, String conv)
    {
        this.employeCode= employeCode;
        this.registrationNumber = registrationNumber;
        this.socialSecurityNumber = socialSecurityNumber;
        this.assignment = assignment;
        this.employment = employment;
        this.classification = classification;
        this.echelon = echelon;
        this.contratType = contratType;
        this.arrivalDate = arrivalDate;
        this.socialSecurityCeiling = socialSecurityCeiling;
        this.timetable = timetable;
        this.hourlyRate = hourlyRate;
        this.mincoef = mincoef;
        this.coef =coef;
        this.monthlyPay = monthlyPay;
        this.monthlyPayRef =  monthlyPayRef;
        this.categoryLabel = categoryLabel;
        this.dateSeniority = dateSeniority;
        this.localisation = localisation;
        this.releaseDate = releaseDate;
        this.meansOfPayment = meansOfPayment;
        this.PaymentDate = PaymentDate;
        this.periode = periode;
        this.conv =conv;

    }

    public EmployeeInformation() {
    }

    public String getEmployeCode() {
        return employeCode;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getAssignment() {
        return assignment;
    }

    public String getClassification() {
        return classification;
    }

    public String getEchelon() {
        return echelon;
    }

    public String getSocialSecurityCeiling() {
        return socialSecurityCeiling;
    }

    public String getTimetable() {
        return timetable;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public String getMincoef() {
        return mincoef;
    }

    public String getMonthlyPay() {
        return monthlyPay;
    }

    public String getMonthlyPayRef() {
        return monthlyPayRef;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public String getDateSeniority() {
        return dateSeniority;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMeansOfPayment() {
        return meansOfPayment;
    }

    public String getPaymentDate() {
        return PaymentDate;
    }

    public String getEmployment() {
        return employment;
    }

    public String getContratType() {
        return contratType;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getCoef() {
        return coef;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public void setContratType(String contratType) {
        this.contratType = contratType;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setCoef(String coef) {
        this.coef = coef;
    }

    public void setEmployeCode(String employeCode) {
        this.employeCode = employeCode;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setEchelon(String echelon) {
        this.echelon = echelon;
    }

    public void setSocialSecurityCeiling(String socialSecurityCeiling) {
        this.socialSecurityCeiling = socialSecurityCeiling;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setMincoef(String mincoef) {
        this.mincoef = mincoef;
    }

    public void setMonthlyPay(String monthlyPay) {
        this.monthlyPay = monthlyPay;
    }

    public void setMonthlyPayRef(String monthlyPayRef) {
        this.monthlyPayRef = monthlyPayRef;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public void setDateSeniority(String dateSeniority) {
        this.dateSeniority = dateSeniority;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setMeansOfPayment(String meansOfPayment) {
        this.meansOfPayment = meansOfPayment;
    }

    public void setPaymentDate(String paymentDate) {
        PaymentDate = paymentDate;
    }

    public Date getPeriode() {
        return periode;
    }

    public void setPeriode(Date periode) {
        this.periode = periode;
    }

    public String getConv() {
        return conv;
    }

    public void setConv(String conv) {
        this.conv = conv;
    }

    public String getEmployeCodeLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Code","Employee Code"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getRegistartionNumberLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Mat:","Matricule"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getSecurityNumberLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Social Security Number","N SS"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getDateStartLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Entry on : ","Entry Date"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getEmploymentLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Job :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getQualifLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Qualification :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getReleaseDateLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Released on :","Released Date"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getClassificationLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Classification :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getCoeffLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Coefficient :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getSocialSecurityCeilingLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Security Ceiling :","Social Security Ceiling"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getHourlyRateLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Hourly Rate :", "Hourly Minimum Wage :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getDateSeniorityLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Seniority :", "Seniority Date :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getMonthlyRefPayLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Monthly Salary Ref :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getMonthlyPayLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Monthly Salary :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getTimeTableLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Schedule :","Index or number of hours"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getEchelonLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("echelon :","Echelon","Ech"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getContratLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Contract :"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getAssignementLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Assignment :", "Assn"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getMinCoeffLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Coef.min :", "min"));
        return labels.get(this.random.nextInt(labels.size()));
    }

    public String getPaymentDateLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Payment on:", "Payment Date"));
        return labels.get(this.random.nextInt(labels.size()));
    }
    public String getPaymentPeriodLabel() {
        List<String> labels = new ArrayList<String>(Collections.singletonList("Period :"));
        return labels.get(this.random.nextInt(labels.size()));
    }
    public String getPaymentPeriodDatesLabel() {
        List<String> labels = new ArrayList<String>(Arrays.asList("Payment Period :",""));
        return labels.get(this.random.nextInt(labels.size()));
    }

    @Override
    public String toString() {
        return "EmployeeInformation{" +
                "employeCode='" + employeCode + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", socialSecurityNumber='" + socialSecurityNumber + '\'' +
                ", assignment='" + assignment + '\'' +
                ", employment='" + employment + '\'' +
                ", classification='" + classification + '\'' +
                ", echelon='" + echelon + '\'' +
                ", contratType='" + contratType + '\'' +
                ", arrivalDate='" + arrivalDate + '\'' +
                ", socialSecurityCeiling='" + socialSecurityCeiling + '\'' +
                ", timetable='" + timetable + '\'' +
                ", hourlyRate='" + hourlyRate + '\'' +
                ", mincoef='" + mincoef + '\'' +
                ", monthlyPay='" + monthlyPay + '\'' +
                ", monthlyPayRef='" + monthlyPayRef + '\'' +
                ", categoryLabel='" + categoryLabel + '\'' +
                ", dateSeniority='" + dateSeniority + '\'' +
                ", localisation='" + localisation + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", meansOfPayment='" + meansOfPayment + '\'' +
                ", PaymentDate='" + PaymentDate + '\'' +
                '}';
    }

    public static class Generator implements ModelGenerator<EmployeeInformation> {
//TODO HEADS AND CONTENT LISTS

        private final List<DepartCommune> DepartsComm;
        private static final String DepartCommFile = "payslips/employee/commonCodeInsee.json";
        {
            Reader jsonReader = new InputStreamReader(EmployeeInformation.class.getClassLoader().getResourceAsStream(DepartCommFile));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<DepartCommune>>(){}.getType();
            DepartsComm = gson.fromJson(jsonReader, collectionType);
        }

        private final List<CategoryEmployee> CategoriesEmp;
        private static final String CategoriesEmpFile = "payslips/employee/employeeCode.json";
        {
            Reader jsonReader = new InputStreamReader(EmployeeInformation.class.getClassLoader().getResourceAsStream(CategoriesEmpFile));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<CategoryEmployee>>(){}.getType();
            CategoriesEmp = gson.fromJson(jsonReader, collectionType);
        }

        private final List<CategorieCoef> CategorieCoef;
        private static final String CategoriesCoefFile = "payslips/employee/categorie_coeff.json";
        {
            Reader jsonReader = new InputStreamReader(EmployeeInformation.class.getClassLoader().getResourceAsStream(CategoriesCoefFile));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<CategorieCoef>>(){}.getType();
            CategorieCoef = gson.fromJson(jsonReader, collectionType);
        }

        private final List<ConventionCollec> ConvColl;
        private static final String ConvCollFile = "payslips/employee/conventions_list.json";
        {
            Reader jsonReader = new InputStreamReader(EmployeeInformation.class.getClassLoader().getResourceAsStream(ConvCollFile));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<ConventionCollec>>(){}.getType();
            ConvColl = gson.fromJson(jsonReader, collectionType);
        }

        private final List<CoeffSalary> CoefSalary;
        private static final String CoefSalaryFile = "payslips/employee/coeff_salary_min.json";
        {
            Reader jsonReader = new InputStreamReader(EmployeeInformation.class.getClassLoader().getResourceAsStream(CoefSalaryFile));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<CoeffSalary>>(){}.getType();
            CoefSalary = gson.fromJson(jsonReader, collectionType);
        }

        @Override
        public EmployeeInformation generate(GenerationContext ctx) {
            Faker faker = Faker.instance(Locale.forLanguageTag(ctx.getLanguagePayslip()));
            Random rand =new Random();
            EmployeeInformation employeeInformation = new EmployeeInformation();

            String registrationNumber = String.format("%03d", rand.nextInt(100)+1);

            String gender = ""+(rand.nextInt(2)+1);
            String yearBirth = String.format("%02d", rand.nextInt(99)+1);

            String monthBirth = String.format("%02d", rand.nextInt(12)+1);

            DepartCommune departComm = DepartsComm.get(ctx.getRandom().nextInt(DepartsComm.size()));
            String departmentCode = departComm.getCode_dept();
            String commonBirth = departComm.getCode_comm();

            String orderBirth = String.format("%03d", rand.nextInt(999)+1);
            String keySS = String.format("%02d", rand.nextInt(99)+1);

            String socialSecurityNumber = gender + yearBirth + monthBirth+ departmentCode +commonBirth+orderBirth + keySS;

            String assignment = String.format("%03d", rand.nextInt(999)+1);

            CategoryEmployee categorieEmp = CategoriesEmp.get(ctx.getRandom().nextInt(CategoriesEmp.size()));
            List<Job> listEmp = categorieEmp.getList_job();
            Job emp = listEmp.get(ctx.getRandom().nextInt(listEmp.size()));
            String categoryLabel = categorieEmp.getCategorie();
            String employeCode = emp.getCode();
            String employment = emp.getLabel();
            /*  String arr[] = employment.split(" ", 4);
            if (arr.length>=3) employment = arr[0]+ " "+ arr[1] +" "+arr[2];
            else if (arr.length>=2 ) employment = arr[0]+ " "+ arr[1] ;
                    else employment = arr[0];*/
            String classification ;
            if(categoryLabel.toLowerCase().contains("cadre")) classification ="CADRE";
            else classification ="NON CADRE";


            String echelon = String.format("%01d", rand.nextInt(9)+1);

            int cntrType= rand.nextInt(2);
            String contratType= "";
            switch (cntrType){
                case 0:
                    contratType = "CDI";
                    break;
                case 1:
                    contratType = "CDD";
                    break;
            }

            /////////////////
            long from = 946684800;
            long to = 1483228800 ;
            long date = (ctx.getRandom().nextInt((int)(to-from)) + from) * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            Date d1 = calendar.getTime();
            ////////////////
            //Date d1 = new Date();
            String pattern = "";//""d MMM YYYY"; // "D/MM/yyyy";
            int rnd= ctx.getRandom().nextInt(2);
            switch (rnd){
                case 0:
                    pattern = "d MMM YYYY";
                    break;
                case 1:
                    pattern = "d/MM/yyyy";
                    break;
            }
            DateFormat df = new SimpleDateFormat(pattern);
            String arrivalDate = df.format(d1);
            String mincoef = "120";
            String coef = " ";
            int coefInt=0;
            boolean stop = false;
            int i=0;
            while (!stop){
                if (categoryLabel.toLowerCase().contains(CategorieCoef.get(i).getCategorie())){
                    int low = CategorieCoef.get(i).getCoeffMin();
                    int high =CategorieCoef.get(i).getCoeffMax();
                    coefInt = rand.nextInt(high-low) + low;
                    stop = true;
                }else {
                    i++;
                }
                if(i==(CategorieCoef.size()-1) && !stop){
                    stop=true;
                    coefInt=200;

                }
            }
            coef =Integer.toString(coefInt);
            String socialSecurityCeiling = "2435";
            String dateSeniority = arrivalDate;
            String releaseDate = " ";

            ///////////////
            long from1 = 1483228800;
            long to1 = System.currentTimeMillis() / 1000;
            long date1 = (ctx.getRandom().nextInt((int)(to1-from1)) + from1) * 1000;
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(date1);
            Date periode = calendar1.getTime();
            //////////////
            Calendar cal = Calendar.getInstance();
            cal.setTime(periode);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            String PaymentDate = df.format(cal.getTime());

            String meansOfPayment = null;
            int randPay= rand.nextInt(2);
            switch (randPay){
                case 0:
                    meansOfPayment = "Par chèque";
                    break;
                case 1:
                    meansOfPayment = "Par virement";
                    break;
            }

            String timetable = null;
            String hourlyRate = null;

            int diff = Math.abs(CoefSalary.get(0).getCoefficient()-coefInt);
            int indexCloser=0;
            for(int j=1;j<CoefSalary.size();j++){
                if( diff > Math.abs(CoefSalary.get(j).getCoefficient()-coefInt)){
                    diff = Math.abs(CoefSalary.get(j).getCoefficient()-coefInt);
                    indexCloser = j;
                }
            }


            String monthlyPay = dfd.format(CoefSalary.get(indexCloser).getBaseMin()+(rand.nextDouble()*200));
            String monthlyPayRef = " ";

            String localisation = " ";
            ConventionCollec conv = ConvColl.get(rand.nextInt(ConvColl.size()));
            String convColl=conv.getName();

            return new EmployeeInformation( employeCode,  registrationNumber,  socialSecurityNumber,  assignment,
                     employment,  classification,  echelon,  contratType, arrivalDate,  socialSecurityCeiling,
                     timetable,  hourlyRate, mincoef, coef, monthlyPay,  monthlyPayRef, categoryLabel,
                     dateSeniority, localisation, releaseDate, meansOfPayment, PaymentDate, periode,convColl);
        }
    }
}
