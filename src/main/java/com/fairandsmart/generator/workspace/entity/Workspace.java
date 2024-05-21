package com.fairandsmart.generator.workspace.entity;

import com.fairandsmart.generator.job.entity.Job;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.List;

@Entity
public class Workspace extends PanacheEntity {

    @Version
    public long version;
    public String owner;
    @Transient
    public String root;
    @Transient
    public long size;
    @Transient
    public List<FileItem> content;
    @Transient
    public long activeJobs;
    @Transient
    public List<Job> jobs;

    public static Workspace findByOwner(String owner){
        return find("owner", owner).firstResult();
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + id +
                ", version=" + version +
                ", owner='" + owner + '\'' +
                ", root='" + root + '\'' +
                ", size=" + size +
                ", content=" + content.size() + " items" +
                ", jobs=" + jobs.size() + " jobs" +
                '}';
    }
}
