package com.fairandsmart.generator.workspace.entity;

import java.util.Comparator;
import java.util.Date;

public class FileItem {

    private String name;
    private String mimeType;
    private long size;
    private Date creationDate;
    private Date modificationDate;

    public FileItem() {
        this.creationDate = new Date();
        this.modificationDate = this.creationDate;
        size = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public String toString() {
        return "FileItem{" +
                "name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                '}';
    }

    public static class NameComparatorAsc implements Comparator<FileItem> {
        @Override
        public int compare(FileItem o1, FileItem o2) {
            /*
            if ( o1.isFolder() && !o2.isFolder() ) {
                return -1;
            }
            if ( !o1.isFolder() && o2.isFolder() ) {
                return 1;
            }
            */
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static class NameComparatorDesc implements Comparator<FileItem> {
        @Override
        public int compare(FileItem o1, FileItem o2) {
            /*
            if ( o1.isFolder() && !o2.isFolder() ) {
                return -1;
            }
            if ( !o1.isFolder() && o2.isFolder() ) {
                return 1;
            }
            */
            return o2.getName().compareTo(o1.getName());
        }
    }
}
