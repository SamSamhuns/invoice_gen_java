package com.ssdgen.generator.workspace;

import com.ssdgen.generator.job.JobManager;
import com.ssdgen.generator.security.AuthenticationService;
import com.ssdgen.generator.workspace.entity.FileItem;
import com.ssdgen.generator.workspace.entity.Workspace;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.apache.tika.Tika;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class WorkspaceManager {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceManager.class.getName());
    public static final String WORKSPACES_HOME = "workspaces";

    @ConfigProperty(name = "fs.generator.home")
    public String home;

    @ConfigProperty(name = "fs.generator.page.size")
    public long limit;

    @Inject
    AuthenticationService auth;

    @Inject
    JobManager jobs;

    private static Path root;

    @PostConstruct
    protected void init() {
        LOGGER.log(Level.INFO, "Initialising Workspace Service");
        if ( home.startsWith("~") ) {
            home = home.replaceFirst("\\~", Paths.get(System.getProperty("user.home")).toString());
        }
        root = Paths.get(home, WORKSPACES_HOME);
        LOGGER.log(Level.INFO, "Initializing service with root folder: " + root);
        try {
            Files.createDirectories(root);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "unable to initialize workspace service", e);
        }
    }

    @Transactional
    public Long findIdForConnectedUser() throws WorkspaceManagerException {
        Workspace workspace = Workspace.findByOwner(auth.getConnectedUser());
        if ( workspace == null ) {
            workspace = this.bootstrap(auth.getConnectedUser());
        }
        return workspace.id;
    }

    @Transactional
    public Workspace load(Long id) throws WorkspaceNotFoundException, WorkspaceManagerException, AccessDeniedException {
        LOGGER.log(Level.INFO, "Loading workspace for id: " + id);
        Workspace workspace = Workspace.findById(id);
        if ( workspace == null ) {
            throw new WorkspaceNotFoundException("Unable to find workspace for id: " + id);
        }
        if ( !auth.isSuperUserConnected() && !auth.getConnectedUser().equals(workspace.owner) ) {
            throw new AccessDeniedException("Access Denied for user [" + auth.getConnectedUser() + "] on workspace with id: " + id);
        }
        Path wsroot = Paths.get(root.toString(), workspace.owner);
        try {
            workspace.root = wsroot.toString();
            workspace.size = Files.size(wsroot);
            workspace.content = Files.list(wsroot).map(this::toFileItem).collect(Collectors.toList());
            workspace.activeJobs = jobs.countActiveForOwner(workspace.owner);
            workspace.jobs = jobs.findForOwner(workspace.owner);
            LOGGER.log(Level.INFO, "Workspace: " + workspace);
            return workspace;
        } catch ( IOException e ) {
            throw new WorkspaceManagerException("Error while accessing workspace folder", e);
        }
    }

    public void purge(Long id) throws WorkspaceNotFoundException, WorkspaceManagerException, AccessDeniedException {
        LOGGER.log(Level.INFO, "Purge workspace for id: " + id);
        Workspace workspace = Workspace.findById(id);
        if ( workspace == null ) {
            throw new WorkspaceNotFoundException("Unable to find workspace for id: " + id);
        }
        if ( !auth.isSuperUserConnected() && !auth.getConnectedUser().equals(workspace.owner) ) {
            throw new AccessDeniedException("Access Denied for user [" + auth.getConnectedUser() + "] on workspace with id: " + id);
        }
        Path wsroot = Paths.get(root.toString(), workspace.owner);
        try {
            Files.list(wsroot).forEach(this::deletePath);
        } catch ( IOException | RuntimeException e ) {
            LOGGER.log(Level.SEVERE, "Error while purging workspace", e);
            throw new WorkspaceManagerException("Error while purging workspace folder", e);
        }
    }

    private Workspace bootstrap(String owner) throws WorkspaceManagerException {
        LOGGER.log(Level.INFO, "Bootstraping workspace for owner: " + owner);
        try {
            Path wsroot = Paths.get(root.toString(), owner);
            try {
                Files.createDirectory(wsroot);
            } catch ( FileAlreadyExistsException e ) { //
            }
            Workspace workspace = new Workspace();
            workspace.owner = owner;
            workspace.persist();
            return workspace;
        } catch ( IOException e ) {
            throw new WorkspaceManagerException("Error while creating workspace folder", e);
        }
    }

    private void deletePath(Path p) throws RuntimeException {
        try {
            Files.delete(p);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    private FileItem toFileItem(Path path) {
        FileItem item = new FileItem();
        item.setName(path.getFileName().toString());
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            item.setSize(attributes.size() / 1024);
            item.setCreationDate(new Date(attributes.creationTime().toMillis()));
            item.setModificationDate(new Date(attributes.lastModifiedTime().toMillis()));
            item.setMimeType(new Tika().detect(new File(path.toString())));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return item;
    }

}
