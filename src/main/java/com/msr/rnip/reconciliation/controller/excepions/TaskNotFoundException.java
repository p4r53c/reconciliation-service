package com.msr.rnip.reconciliation.controller.excepions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@author p4r53c
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public TaskNotFoundException() {

    }

    public TaskNotFoundException(String message, final Throwable cause) {
        super(message, cause);
    }
}

