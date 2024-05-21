package com.ssdgen.generator.job.handler;

import com.ssdgen.generator.job.entity.Job;

import java.util.Map;

public interface JobHandler extends Runnable {

    String PARAM_ROOT = "root";

    boolean canHandle(Job job);

    void setJobId(Long id);

    void setJobRoot(String root);

    void setJobParams(Map<String, String> params);

}
