package com.ssdgen.generator.documents.data.model;

import java.util.List;

public class CategoryEmployee {

    private String category_code;
    private String category;
    List<Job> list_job;

    public CategoryEmployee() {
    }

    public CategoryEmployee(String category_code, String category, List<Job> list_job) {
        this.category_code = category_code;
        this.category = category;
        this.list_job = list_job;
    }

    public String getCode_categorie() {
        return category_code;
    }

    public String getCategorie() {
        return category;
    }

    public List<Job> getList_job() {
        return list_job;
    }

    public void setCode_categorie(String category_code) {
        this.category_code = category_code;
    }

    public void setCategorie(String category) {
        this.category = category;
    }

    public void setList_job(List<Job> list_job) {
        this.list_job = list_job;
    }
}
