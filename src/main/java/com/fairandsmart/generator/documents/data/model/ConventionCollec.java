package com.fairandsmart.generator.documents.data.model;

public class ConventionCollec {

    private String idcc;
    private String name;

    public ConventionCollec(String idcc, String name) {
        this.idcc = idcc;
        this.name = name;
    }

    public ConventionCollec() {
    }

    public String getIdcc() {
        return idcc;
    }

    public String getName() {
        return name;
    }

    public void setIdcc(String idcc) {
        this.idcc = idcc;
    }

    public void setName(String name) {
        this.name = name;
    }
}
