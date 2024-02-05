package com.msr.rnip.reconciliation.service;

import com.msr.rnip.reconciliation.adapter.jinn.JINNDocument;
import com.msr.rnip.reconciliation.controller.excepions.TaskNotFoundException;
import com.msr.rnip.reconciliation.dao.TaskDao;
import com.msr.rnip.reconciliation.dao.impl.CustomTaskDaoImpl;
import com.msr.rnip.reconciliation.model.Task;
import com.msr.rnip.reconciliation.model.TaskState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@author p4r53c
@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private CustomTaskDaoImpl customTaskDaoImpl;

    public List<Task> getAllTasks() {
        return this.taskDao.findAll();
    }

    public Optional<Task> getTask(String taskId) throws TaskNotFoundException {
        Optional<Task> task = taskDao.findById(taskId);

        if(!task.isPresent())
            throw new TaskNotFoundException();

        return this.taskDao.findById(taskId);
    }

    public Task getGreatestByDateAndState(TaskState taskStateQ, TaskState taskStateP) {
        return this.customTaskDaoImpl.getGreatestByDateAndState(taskStateQ, taskStateP);
    }

    public Task createTask(Task task) {
        //LOGGER.debug("Task [{}] created", task.getTaskId());
        return this.taskDao.save(task);
    }

    public void save(Task task) {
        this.taskDao.saveAndFlush(task);
    }
}
