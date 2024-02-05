package com.msr.rnip.reconciliation.dao.impl;

import com.msr.rnip.reconciliation.dao.CustomTaskDao;
import com.msr.rnip.reconciliation.model.Task;
import com.msr.rnip.reconciliation.model.TaskState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//@author p4r53c
@Repository
@Qualifier("customTaskDaoImpl")
public class CustomTaskDaoImpl implements CustomTaskDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Task getGreatestByDateAndState(TaskState taskStateQ, TaskState taskStateP) {
            Query query = entityManager.createQuery("SELECT t FROM Task t " +
                    "WHERE t.taskState = :taskStateQ OR t.taskState = :taskStateP " +
                    "ORDER BY t.taskCreationDate DESC");
            query.setParameter("taskStateQ", taskStateQ);
            query.setParameter("taskStateP", taskStateP);
            Object result = query.setMaxResults(1).getSingleResult();
            return (Task) result;
    }
}
