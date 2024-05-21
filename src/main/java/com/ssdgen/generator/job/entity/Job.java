package com.ssdgen.generator.job.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.annotation.Nonnull;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
public class Job extends PanacheEntity implements Delayed {

    private static final Logger LOGGER = Logger.getLogger(Job.class.getName());

    @Version
    public long version;
    public String owner;
    public String type;
    public long creationDate;
    public long dueDate;
    public long startDate;
    public long stopDate;
    @Enumerated(EnumType.STRING)
    public Status status;
    public long progression;
    @Lob
    public String message;
    @ElementCollection
    public Map<String, String> params;

    public Job() {
        this.params = new HashMap<>();
        progression = 0;
    }

    @Override
    @Transient
    public long getDelay(TimeUnit unit) {
        LOGGER.log(Level.FINE, "Getting delay for job: " + this.id);
        long diff = dueDate - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@Nonnull Delayed obj) {
        LOGGER.log(Level.FINE, "Comparing to another object: " + obj);
        if (!(obj instanceof Job)) {
            throw new IllegalArgumentException("Illegal comparison to non-Job");
        }
        Job other = (Job) obj;
        return (int) (other.dueDate - this.dueDate);
    }

    public static List<Job> findForOwner(String owner) {
        return find("owner", owner).list();
    }


    /*public static long countActiveForOwner(String owner) {
        return find("owner = ?1 and ( status = ?2)", owner, Status.RUNNING).count(); // status = ?2 or Status.PENDING,
    }*/
    public static List<Job> findActiveForOwner(String owner) {
        return find("owner = ?1 and (status = ?2 or status = ?3)", owner, Status.PENDING, Status.RUNNING).list();
    }

    public static long countActiveForOwner(String owner) {
        return find("owner = ?1 and (status = ?2 or status = ?3)", owner, Status.PENDING, Status.RUNNING).count();
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", owner='" + owner + '\'' +
                ", type='" + type + '\'' +
                ", creationDate=" + creationDate +
                ", dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", stopDate=" + stopDate +
                ", status=" + status +
                ", progression=" + progression +
                ", message='" + message + '\'' +
                ", params=" + params +
                '}';
    }

    public enum Status {
        PENDING,
        RUNNING,
        DONE,
        FAILED
    }

}
