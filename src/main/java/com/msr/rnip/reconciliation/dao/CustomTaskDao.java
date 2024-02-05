package com.msr.rnip.reconciliation.dao;

import com.msr.rnip.reconciliation.model.Task;
import com.msr.rnip.reconciliation.model.TaskState;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomTaskDao {
    Task getGreatestByDateAndState(TaskState taskStateQ, TaskState taskStateP);

}
