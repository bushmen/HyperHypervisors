package pl.edu.agh.student.hyperhypervisors.model;

import java.io.Serializable;

public class MemoryDescription implements Serializable {

    private long total;
    private long free;
    private long used;
    private long actualFree;
    private long actualUsed;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getActualFree() {
        return actualFree;
    }

    public void setActualFree(long actualFree) {
        this.actualFree = actualFree;
    }

    public long getActualUsed() {
        return actualUsed;
    }

    public void setActualUsed(long actualUsed) {
        this.actualUsed = actualUsed;
    }
}
