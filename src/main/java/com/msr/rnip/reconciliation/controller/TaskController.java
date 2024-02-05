package com.msr.rnip.reconciliation.controller;

import com.msr.rnip.reconciliation.controller.excepions.TaskNotFoundException;
import com.msr.rnip.reconciliation.model.Task;
import com.msr.rnip.reconciliation.model.TaskState;
import com.msr.rnip.reconciliation.service.AsyncReconciliationService;
import com.msr.rnip.reconciliation.service.ChargeService;
import com.msr.rnip.reconciliation.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

//@author p4r53c
@RestController
@RequestMapping("/reconciliationService/v2/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private AsyncReconciliationService asyncReconciliationService;

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    public Optional<Task> getTask(@PathVariable("id") String id) {
        try {
            return this.taskService.getTask(id);
        }
        catch (TaskNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found!", ex);
        }
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody()
    public Task createTask() {
        try {
            // Запред на создание новой задачи, если уже есть рабочая.
            // FIXME: Зделать правильную проверку со своим проверяемым исключением.
            taskService.getGreatestByDateAndState(TaskState.QUEUED, TaskState.PROCESSING);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There already exits processing Task");
        } catch (NoResultException | EmptyResultDataAccessException ex) {
            Task task = new Task();
            return this.taskService.createTask(task);
        }
    }
}
