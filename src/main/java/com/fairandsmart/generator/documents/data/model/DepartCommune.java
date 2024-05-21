package com.fairandsmart.generator.documents.data.model;

public class DepartCommune {

    private String code_comm;
    private String nom_dept;
    private String code_dept;
    private String nom_comm;

    public DepartCommune(String code_comm, String nom_dept, String code_dept, String nom_comm) {
        this.code_comm = code_comm;
        this.nom_dept = nom_dept;
        this.code_dept = code_dept;
        this.nom_comm = nom_comm;
    }

    public DepartCommune() {
    }

    public String getCode_comm() {
        return code_comm;
    }

    public String getNom_dept() {
        return nom_dept;
    }

    public String getCode_dept() {
        return code_dept;
    }

    public String getNom_comm() {
        return nom_comm;
    }

    public void setCode_comm(String code_comm) {
        this.code_comm = code_comm;
    }

    public void setNom_dept(String nom_dept) {
        this.nom_dept = nom_dept;
    }

    public void setCode_dept(String code_dept) {
        this.code_dept = code_dept;
    }

    public void setNom_comm(String nom_comm) {
        this.nom_comm = nom_comm;
    }

    @Override
    public String toString() {
        return "DepartCommune{" +
                "code_comm='" + code_comm + '\'' +
                ", nom_dept='" + nom_dept + '\'' +
                ", code_dept='" + code_dept + '\'' +
                ", nom_comm='" + nom_comm + '\'' +
                '}';
    }
}
