package com.ssdgen.generator.job.handler;

import com.ssdgen.generator.documents.ReceiptGenerator;
import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.ReceiptModel;
import com.ssdgen.generator.documents.layout.receipt.GenericReceiptLayout;
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
public class ReceiptGenerationHandler implements JobHandler {

    private static final Logger LOGGER = Logger.getLogger(ReceiptGenerationHandler.class.getName());
    public static final String[] SUPPORTED_TYPES = { "receipt.generate" };
    public static final String PARAM_QTY = "qty";
    public static final String PARAM_START_IDX = "start-idx";
    public static final String PARAM_OUTPUT = "output";

    private String root;
    private Long jobId;
    private Map<String, String> params;

    @Inject
    @Any
    Instance<GenericReceiptLayout> layouts;

    @Inject
    JobManager manager;

    public ReceiptGenerationHandler() {
        LOGGER.log(Level.INFO, "ReceiptGenerationHandler instanciated");
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
                LOGGER.log(Level.INFO, "ReceiptGeneration started");
                if (!params.containsKey(PARAM_QTY)) {
                    report.append("Missing parameters: " + PARAM_QTY);
                    manager.fail(jobId, report.toString());
                    return;
                }
                manager.start(jobId);
                LOGGER.log(Level.INFO, "Starting generation of receipts");
                report.append("Starting generation of receipts \r\n");
                int qty = Integer.parseInt(params.get(PARAM_QTY));
                int start = Integer.parseInt(params.getOrDefault(PARAM_START_IDX, "1"));
                int stop = start + qty;
                // TODO Filter layouts according to param
                LOGGER.log(Level.INFO, "layouts");
                List<GenericReceiptLayout> availableLayouts = layouts.stream().collect(Collectors.toList());

                LOGGER.log(Level.INFO, "availableLayouts.size() = " + availableLayouts.size());

                if (availableLayouts.size() == 0) {
                    report.append("Unable to find available layouts for this job.");
                    LOGGER.log(Level.INFO, "Unable to find available layouts for this job");
                    manager.fail(jobId, report.toString());
                    return;
                }
                LOGGER.log(Level.INFO, "After generating layout");
                for (int i = start; i < stop; i++) {
                    Path pdf = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "receipt") + "-" + i + ".pdf");
                    Path xml = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "receipt") + "-" + i + ".xml");
                    // Path xmlEval = Paths.get(root, params.getOrDefault(PARAM_OUTPUT,
                    // "receiptEval") + "-" + i + ".xml");
                    Path xmlEval = null;
                    Path img = Paths.get(root, params.getOrDefault(PARAM_OUTPUT, "receipt") + "-" + i + ".jpg");
                    // TODO configure context according to config
                    GenerationContext ctx = GenerationContext.generate();
                    ReceiptModel model = new ReceiptModel.Generator().generate(ctx);
                    ReceiptGenerator.getInstance().generateReceipt(new GenericReceiptLayout(), model, pdf, xml, img,
                            xmlEval);
                    manager.progress(jobId, ((i - start) * 100L) / qty);
                }
                report.append("All receipts generated");
                LOGGER.log(Level.INFO, "All receipts generated");
                manager.complete(jobId, report.toString());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while executing job", e);
                report.append("Error occurred during job: " + e.getMessage());
                manager.fail(jobId, report.toString());
            }
        } catch (JobNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Unable to find a job for id: " + jobId, e);
        }
        LOGGER.log(Level.INFO, "Generation Handler thread finished");

    }

}
