package com.ssdgen.generator.documents.data.model;

public class SalaryLine {

    //News
    private int codeElement;
    private String heading;
    private float base;
    private float salaryRate; // taux salarial
    private float employeeContributions; // cotisations salariales
    private float employerRate; // taux patronal
    private float employerContributions; // cotisations patronales

    public int getCodeElement() {
        return codeElement;
    }

    public String getHeading() {
        return heading;
    }

    public float getBase() {
        return base;
    }

    public float getSalaryRate() {
        return salaryRate;
    }

    public float getEmployeeContributions() {
        return employeeContributions;
    }

    public float getEmployerRate() {
        return employerRate;
    }

    public float getEmployerContributions() {
        return employerContributions;
    }

    public void setCodeElement(int codeElement) {
        this.codeElement = codeElement;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public void setSalaryRate(float salaryRate) {
        this.salaryRate = salaryRate;
    }

    public void setEmployeeContributions(float employeeContributions) {
        this.employeeContributions = employeeContributions;
    }

    public void setEmployerRate(float employerRate) {
        this.employerRate = employerRate;
    }

    public void setEmployerContributions(float employerContributions) {
        this.employerContributions = employerContributions;
    }

    public SalaryLine(int codeElement, String heading, float base, float salaryRate, float employeeContributions, float employerRate, float employerContributions) {
        this.codeElement = codeElement;
        this.heading = heading;
        this.base = base;
        this.salaryRate = salaryRate;
        this.employeeContributions = employeeContributions;
        this.employerRate = employerRate;
        this.employerContributions = employerContributions;
    }

    public SalaryLine() {
    }

    @Override
    public String toString() {
        return "SalaryLine{" +
                "codeElement=" + codeElement +
                ", heading='" + heading + '\'' +
                ", base=" + base +
                ", salaryRate=" + salaryRate +
                ", employeeContributions=" + employeeContributions +
                ", employerRate=" + employerRate +
                ", employerContributions=" + employerContributions +
                '}';
    }


/*
    public String getFmtPrice() {
        return String.format("%.2f", this.getPrice()) + " " + currency;
    }*/



    /*
      "price": "479.0",
              "ean": "8806088499048",
              "name": "Lave linge hublot Samsung ADD WASH WW90K4437YW",
              "brand": "Samsung",
              "sku": "000000000001079579",
              "categories": [
              "Gros électroménager",
              "Lave-linge",
              "Lave-linge hublot"
              ]

              */

}
