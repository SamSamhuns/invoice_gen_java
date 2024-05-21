package com.ssdgen.generator.documents.data.model;

public class Job {

    private String code;
    private String label;

    public Job(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public Job() {
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Job{" +
                "code='" + code + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
