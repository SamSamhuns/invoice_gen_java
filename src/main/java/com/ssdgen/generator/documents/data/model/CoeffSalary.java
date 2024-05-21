package com.ssdgen.generator.documents.data.model;

public class CoeffSalary {

    private int coefficient;
    private double baseMin;

    public CoeffSalary() {
    }

    public CoeffSalary(int coefficient, double baseMin) {
        this.coefficient = coefficient;
        this.baseMin = baseMin;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public double getBaseMin() {
        return baseMin;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public void setBaseMin(double baseMin) {
        this.baseMin = baseMin;
    }
}
