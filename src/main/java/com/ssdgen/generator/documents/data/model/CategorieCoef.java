package com.ssdgen.generator.documents.data.model;

public class CategorieCoef {

    private String category;
    private int coeffMin;
    private int coeffMax;

    public CategorieCoef() {
    }

    public CategorieCoef(String category, int coeffMin, int coeffMax) {
        this.category = category;
        this.coeffMin = coeffMin;
        this.coeffMax = coeffMax;
    }

    public String getCategorie() {
        return category;
    }

    public int getCoeffMin() {
        return coeffMin;
    }

    public int getCoeffMax() {
        return coeffMax;
    }

    public void setCategorie(String category) {
        this.category = category;
    }

    public void setCoeffMin(int coeffMin) {
        this.coeffMin = coeffMin;
    }

    public void setCoeffMax(int coeffMax) {
        this.coeffMax = coeffMax;
    }
}
