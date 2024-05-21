package com.fairandsmart.generator.job;

import com.fairandsmart.generator.job.entity.Job;

public class UnsupportedJobException extends Exception {

    public UnsupportedJobException(Job job) {
        super("Unable to find a handler for job: " + job);
    }

}
