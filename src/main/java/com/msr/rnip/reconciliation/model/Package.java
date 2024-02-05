package com.msr.rnip.reconciliation.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Package")
@Table(name = "PACKAGES")
public class Package implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PACKAGE_ID", unique = true, length = 40)
    private String packageId;

    //@ManyToOne
    //@JoinColumn(name = "TASK_ID", updatable = false)
    @Column(name = "TASK_ID", length = 36, nullable = true)
    private String taskId;

    @Column(name = "IS_PROCESSED")
    private Boolean packageIsProcessed;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "RESULT_MESSAGE")
    private String packageResultMessage;

    public Package() {

    }

    public Package(String packageId, String taskId) {
        this.packageId = packageId;
        this.taskId = taskId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean isPackageIsProcessed() {
        return packageIsProcessed;
    }

    public void setPackageIsProcessed(boolean packageIsProcessed) {
        this.packageIsProcessed = packageIsProcessed;
    }

    public String getPackageResultMessage() {
        return packageResultMessage;
    }

    public void setPackageResultMessage(String packageResultMessage) {
        this.packageResultMessage = packageResultMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return isPackageIsProcessed() == aPackage.isPackageIsProcessed() &&
                getPackageId().equals(aPackage.getPackageId()) &&
                Objects.equals(getTaskId(), aPackage.getTaskId()) &&
                Objects.equals(getPackageResultMessage(), aPackage.getPackageResultMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPackageId(), getTaskId(), isPackageIsProcessed(), getPackageResultMessage());
    }
}
