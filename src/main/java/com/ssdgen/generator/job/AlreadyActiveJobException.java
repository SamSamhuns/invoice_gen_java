package com.ssdgen.generator.job;

public class AlreadyActiveJobException extends Exception {

    public AlreadyActiveJobException(String owner) {
        super("An active job already exists for owner: " + owner);
    }

}
