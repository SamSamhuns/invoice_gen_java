package com.ssdgen.generator.workspace.entity;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceContent {

    private long total;
    private long offset;
    private long limit;
    private List<FileItem> items;

    public WorkspaceContent() {
        items = new ArrayList<>();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public List<FileItem> getItems() {
        return items;
    }

    public void setItems(List<FileItem> content) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "WorkspaceContent{" +
                "total=" + total +
                ", offset=" + offset +
                ", limit=" + limit +
                ", items=" + items +
                '}';
    }
}
