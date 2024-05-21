package com.ssdgen.generator.job;

import com.ssdgen.generator.job.entity.Job;

public class UnsupportedJobException extends Exception {

    public UnsupportedJobException(Job job) {
        super("Unable to find a handler for job: " + job);
    }

}
