package com.msr.rnip.reconciliation.model;

public enum TaskState {
    QUEUED("QUEUED"),
    PREPARING("PREPARING"),
    REJECTED("REJECTED"),
    PROCESSING("PROCESSING"),
    FINISHED("FINISHED"),
    STOPPED("STOPPED");

    TaskState(String taskState) {
    }
}
