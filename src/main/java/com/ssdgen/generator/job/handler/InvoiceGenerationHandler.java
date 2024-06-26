package com.ssdgen.generator.job.handler;

import com.ssdgen.generator.documents.InvoiceGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.InvoiceModel;
import com.ssdgen.generator.documents.layout.InvoiceLayout;
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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Dependent
public class InvoiceGenerationHandler implements JobHandler {

    private static final Logger LOGGER = Logger.getLogger(InvoiceGenerationHandler.class.getName());

    public static final String[] SUPPORTED_TYPES = {"invoice.generate"};
    public static final String PARAM_QTY = "qty";
    public static final String PARAM_START_IDX = "start-idx";
    public static final String PARAM_OUTPUT = "output";
    private static final Collection<String> SUPPORTED_LAYOUTS = Arrays.asList("Amazon", "BDMobilier", "Cdiscount", "Nature&Decouvertes");

    private Long jobId;
    private String root;
    private Map<String, String> params;

    @Inject
    @Any
    Instance<InvoiceLayout> layouts;

    @Inject
    JobManager manager;

    public InvoiceGenerationHandler() {
        LOGGER.log(Level.INFO, "InvoiceGenerationHandler instanciated");
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
                LOGGER.log(Level.INFO, "Invoice Generation started");
                if ( !params.containsKey(PARAM_QTY) ) {
                    report.append("Missing parameters: " + PARAM_QTY);
                    manager.fail(jobId, report.toString());
                    return;
                }
                manager.start(jobId);
                report.append("Starting generation of invoices \r\n");
                int qty = Integer.parseInt(params.get(PARAM_QTY));
                int start = Integer.parseInt(params.getOrDefault(PARAM_START_IDX, "1"));
                int stop = start + qty;
                //TODO Filter layouts according to param
                List<InvoiceLayout> availableLayouts = layouts.stream().collect(Collectors.toList());
                // currently filtering acc to SUPPORTED_LAYOUTS variable
                availableLayouts = layouts.stream().filter(l -> SUPPORTED_LAYOUTS.contains(l.name())).collect(Collectors.toList());
                LOGGER.log(Level.INFO, "availableLayouts.size() = "+availableLayouts.size());

                if ( availableLayouts.size() == 0 ) {
                    report.append("Unable to find available layouts for this job.");
                    manager.fail(jobId, report.toString());
                    return;
                }
                for ( int i=start; i<stop; i++) {
                    String layoutName = availableLayouts.get(i % availableLayouts.size()).name();
                    Path pdf = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "invoice") + "_" + layoutName + "_" + i + ".pdf");
                    Path xml = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "invoice") + "_" + layoutName + "_" + i + ".xml");
                    Path img = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "invoice") + "_" + layoutName + "_" + i + ".jpg");
                    Path json = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "invoice") + "_" + layoutName + "_" + i + ".json");
                    //TODO configure context according to config
                    GenerationContext ctx = GenerationContext.generate();
                    InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
                    InvoiceGenerator.getInstance().generateInvoice(availableLayouts.get(i % availableLayouts.size()), model, pdf, xml, img, json);
                    manager.progress(jobId, ((i-start)* 100L) /qty);
                }
                LOGGER.log(Level.INFO, "All invoices generated");
                report.append("All invoices generated");
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
