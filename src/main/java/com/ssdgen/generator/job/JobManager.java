package com.ssdgen.generator.job;

import com.ssdgen.generator.job.entity.Job;
import com.ssdgen.generator.job.handler.JobHandler;
import com.ssdgen.generator.workspace.entity.Workspace;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class JobManager {

    private static final Logger LOGGER = Logger.getLogger(JobManager.class.getName());

    @Inject
    ManagedExecutor executor;

    @Inject
    @Any
    Instance<JobHandler> handlers;

    public JobManager() {
        LOGGER.log(Level.INFO, "JobManager instanciated");
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Job> findForOwner(String owner) {
        LOGGER.log(Level.INFO, "Listing jobs for owner : " + owner);
        return Job.findForOwner(owner);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Job> findActiveForOwner(String owner) {
        LOGGER.log(Level.INFO, "Listing active jobs for owner : " + owner);
        return Job.findActiveForOwner(owner);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public long countActiveForOwner(String owner) {
        LOGGER.log(Level.INFO, "Counting active jobs for owner : " + owner);
        return Job.countActiveForOwner(owner);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Job submit(Workspace workspace, String type, Map<String, String> params) throws UnsupportedJobException, AlreadyActiveJobException {
        LOGGER.log(Level.INFO, "Submitting new job of type : " + type);
        if ( Job.countActiveForOwner(workspace.owner) > 0 ) {
            throw new AlreadyActiveJobException(workspace.owner);
        }
        Job job = new Job();
        job.type = type;
        job.owner = workspace.owner;
        job.creationDate = System.currentTimeMillis();
        job.dueDate = job.creationDate;
        job.status = Job.Status.PENDING;
        job.params.putAll(params);
        job.persistAndFlush();
        JobHandler handler = handlers.stream().filter(h -> h.canHandle(job)).findFirst().orElseThrow(() -> new UnsupportedJobException(job));
        handler.setJobId(job.id);
        handler.setJobRoot(workspace.root);
        handler.setJobParams(params);
        executor.submit(handler);
        return job;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Job start(Long id) throws JobNotFoundException {
        LOGGER.log(Level.INFO, "Starting job with id : " + id);
        Job job = load(id);
        job.startDate = System.currentTimeMillis();
        job.status = Job.Status.RUNNING;
        job.persistAndFlush();
        return job;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Job progress(Long id, Long progression) throws JobNotFoundException {
        LOGGER.log(Level.INFO, "Setting progression of job with id : " + id);
        Job job = load(id);
        job.progression = progression;
        job.persistAndFlush();
        return job;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Job complete(Long id, String message) throws JobNotFoundException {
        LOGGER.log(Level.INFO, "Completing job with id : " + id);
        Job job = load(id);
        job.stopDate = System.currentTimeMillis();
        job.status = Job.Status.DONE;
        job.message = message;
        job.persistAndFlush();
        return job;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Job fail(Long id, String message) throws JobNotFoundException {
        LOGGER.log(Level.INFO, "Failing job with id : " + id);
        Job job = load(id);
        job.stopDate = System.currentTimeMillis();
        job.status = Job.Status.FAILED;
        job.message = message;
        job.persistAndFlush();
        return job;
    }

    private Job load(Long id) throws JobNotFoundException {
        Job job = Job.findById(id);
        if ( job == null ) {
            throw new JobNotFoundException("Unable to find a job with id: " + id + " in storage");
        }
        return job;
    }


}
