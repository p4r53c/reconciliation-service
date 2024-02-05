package com.msr.rnip.reconciliation.dao;

import com.msr.rnip.reconciliation.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDao extends JpaRepository<Task, String>, CustomTaskDao {

}
