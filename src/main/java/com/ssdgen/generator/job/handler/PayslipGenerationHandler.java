package com.ssdgen.generator.job.handler;

import com.ssdgen.generator.documents.PayslipGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.PayslipModel;

import com.ssdgen.generator.documents.layout.payslip.GenericPayslipLayout;
import com.ssdgen.generator.job.JobManager;
import com.ssdgen.generator.job.JobNotFoundException;
import com.ssdgen.generator.job.entity.Job;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Dependent
public class PayslipGenerationHandler implements JobHandler {

    private static final Logger LOGGER = Logger.getLogger(PayslipGenerationHandler.class.getName());
    public static final String[] SUPPORTED_TYPES = {"payslip.generate"};
    public static final String PARAM_QTY = "qty";
    public static final String PARAM_START_IDX = "start-idx";
    public static final String PARAM_OUTPUT = "output";

    private String root;
    private Long jobId;
    private Map<String, String> params;

    @Inject
    @Any
    Instance<GenericPayslipLayout> layouts;


    @Inject
    JobManager manager;

    public PayslipGenerationHandler() {
        LOGGER.log(Level.INFO, "PayslipGenerationHandler instanciated");
    }

    @Override
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Override
    public void setJobRoot(String root) {
        this.root = root;
    }

    @Override
    public void setJobParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public boolean canHandle(Job job) {
        return Arrays.stream(SUPPORTED_TYPES).anyMatch(type -> type.equals(job.type));
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO, "Starting Generation Handler thread");
        StringBuffer report = new StringBuffer();

        try {
            try {
                LOGGER.log(Level.INFO, "PayslipGeneration started");
                if ( !params.containsKey(PARAM_QTY) ) {
                    report.append("Missing parameters: " + PARAM_QTY);
                    manager.fail(jobId, report.toString());
                    return;
                }
                manager.start(jobId);
                LOGGER.log(Level.INFO, "Starting generation of payslips");
                report.append("Starting generation of payslips \r\n");
                int qty = Integer.parseInt(params.get(PARAM_QTY));
                int start = Integer.parseInt(params.getOrDefault(PARAM_START_IDX, "1"));
                int stop = start + qty;
                //TODO Filter layouts according to param
                LOGGER.log(Level.INFO, "layouts");
                List<GenericPayslipLayout> availableLayouts = layouts.stream().collect(Collectors.toList());

                LOGGER.log(Level.INFO, "availableLayouts.size() = "+availableLayouts.size());

                if ( availableLayouts.size() == 0 ) {
                    report.append("Unable to find available layouts for this job.");
                    LOGGER.log(Level.INFO, "Unable to find available layouts for this job");
                    manager.fail(jobId, report.toString());
                    return;
                }
                LOGGER.log(Level.INFO, "After generating layout");
                for ( int i=start; i<stop; i++) {                    //TODO configure context according to config

                    Path pdf = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "payslip") + "-" + i + ".pdf");
                    Path xml = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "payslip") + "-" + i + ".xml");
                    Path xmlEval = null;
                    //Path xmlEval = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "payslipEval") + "-" + i + ".xml");
                    Path img = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "payslip") + "-" + i + ".jpg");
                    //TODO configure context according to config
                    GenerationContext ctx = GenerationContext.generate();
                    PayslipModel model = new PayslipModel.Generator().generate(ctx);
                    PayslipGenerator.getInstance().generatePayslip(new com.ssdgen.generator.documents.layout.payslip.GenericPayslipLayout(), model, pdf, xml, img,xmlEval);
                    manager.progress(jobId, ((i-start)* 100L) /qty);
                }
                report.append("All payslips generated");
                LOGGER.log(Level.INFO, "All payslips generated");
                manager.complete(jobId, report.toString());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while executing job",  e);
                report.append("Error occurred during job: " + e.getMessage());
                manager.fail(jobId, report.toString());
            }
        } catch (JobNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Unable to find a job for id: " + jobId, e);
        }
        LOGGER.log(Level.INFO, "Generation Handler thread finished");

    }

}
