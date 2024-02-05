package com.msr.rnip.reconciliation.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Task")
@Table(name = "TASKS")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "TASK_ID", unique = true, length = 36)
    private String taskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TASK_STATE", nullable = false, length = 10)
    private TaskState taskState;

    @Column(name = "CREATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date taskCreationDate;

    @Column(name = "FINISHED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date taskFinishedDate;

    @Column(name = "PROCESSED_ROWS", nullable = true)
    private BigInteger taskProcessedRows;

    @Column(name = "TOTAL_ROWS", nullable = true)
    private BigInteger taskTotalRows;

    public Task() {
    };

    @PrePersist
    public void onCreate() {
        this.taskCreationDate = new Date();
        this.taskState = TaskState.QUEUED;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public Date getTaskCreationDate() {
        return taskCreationDate;
    }

    public void setTaskCreationDate(Date taskCreationDate) {
        this.taskCreationDate = taskCreationDate;
    }

    public Date getTaskFinishedDate() {
        return taskFinishedDate;
    }

    public void setTaskFinishedDate(Date taskFinishedDate) {
        this.taskFinishedDate = taskFinishedDate;
    }

    public BigInteger getTaskProcessedRows() {
        return taskProcessedRows;
    }

    public void setTaskProcessedRows(BigInteger taskProcessedRows) {
        this.taskProcessedRows = taskProcessedRows;
    }

    public BigInteger getTaskTotalRows() {
        return taskTotalRows;
    }

    public void setTaskTotalRows(BigInteger taskTotalRows) {
        this.taskTotalRows = taskTotalRows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getTaskId().equals(task.getTaskId()) &&
                getTaskState() == task.getTaskState() &&
                getTaskCreationDate().equals(task.getTaskCreationDate()) &&
                Objects.equals(getTaskFinishedDate(), task.getTaskFinishedDate()) &&
                Objects.equals(getTaskProcessedRows(), task.getTaskProcessedRows()) &&
                Objects.equals(getTaskTotalRows(), task.getTaskTotalRows());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskId(), getTaskState(), getTaskCreationDate(), getTaskFinishedDate(), getTaskProcessedRows(), getTaskTotalRows());
    }
}
