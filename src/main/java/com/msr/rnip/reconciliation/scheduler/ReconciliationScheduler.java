package com.msr.rnip.reconciliation.scheduler;

import com.google.common.collect.Lists;
import com.msr.rnip.reconciliation.model.Charge;
import com.msr.rnip.reconciliation.model.Task;
import com.msr.rnip.reconciliation.model.TaskState;
import com.msr.rnip.reconciliation.service.AsyncReconciliationService;
import com.msr.rnip.reconciliation.service.ChargeService;
import com.msr.rnip.reconciliation.service.PackageService;
import com.msr.rnip.reconciliation.service.TaskService;
import com.msr.rnip.reconciliation.utils.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.persistence.NoResultException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//@author p4r53c
@Component
public class ReconciliationScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconciliationScheduler.class);

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private XMLHelper xmlHelper;

    @Autowired
    private PackageService packageService;

    @Autowired
    private AsyncReconciliationService asyncReconciliationService;

    @Autowired
    private TaskService taskService;

    private static final int THREADS = 4;
    private static final int CHARGES_FOR_RECONCILE = 400;
    private static final int PACKAGE_SIZE = 100;

    @Scheduled(fixedDelay = 100)
    public void reconciliationRunner() throws ParserConfigurationException, SAXException, IOException, NoResultException {
        try {
            Task currentTask = taskService.getGreatestByDateAndState(TaskState.QUEUED, TaskState.PROCESSING);

            String taskId = currentTask.getTaskId();
            LOGGER.debug("Begin work with Task [{}]", taskId);
            //Task currentTask = this.taskService.getTask(taskId).orElseThrow(TaskNotFoundException::new);

            List<Charge> charges = this.chargeService.getChargesForReconcile(CHARGES_FOR_RECONCILE);

            if (charges.size() != 0) {

                if (currentTask.getTaskState() == TaskState.QUEUED) {
                    currentTask.setTaskState(TaskState.PROCESSING);
                    long totalRowsToProcess = this.chargeService.getAllChargesToProcess();
                    currentTask.setTaskTotalRows(BigInteger.valueOf(totalRowsToProcess));
                }
                BigInteger processedRows = (currentTask.getTaskProcessedRows() == null) ? BigInteger.valueOf(0) : (currentTask.getTaskProcessedRows());

                processedRows = processedRows.add(BigInteger.valueOf(charges.size()));
                currentTask.setTaskProcessedRows(processedRows);
                this.taskService.save(currentTask);
                LOGGER.debug("Invoke reconciliation async service");
                if ((charges.size()) > PACKAGE_SIZE) {

                    List<List<Charge>> chunks = Lists.partition(charges, (charges.size()) / THREADS);
                    CompletableFuture<Boolean> threadResult1 = this.asyncReconciliationService.doReconcile(chunks.get(0), taskId);
                    CompletableFuture<Boolean> threadResult2 = this.asyncReconciliationService.doReconcile(chunks.get(1), taskId);
                    CompletableFuture<Boolean> threadResult3 = this.asyncReconciliationService.doReconcile(chunks.get(2), taskId);
                    CompletableFuture<Boolean> threadResult4 = this.asyncReconciliationService.doReconcile(chunks.get(3), taskId);

                    CompletableFuture.allOf(threadResult1, threadResult2, threadResult3, threadResult4).join();

                } else {
                    CompletableFuture<Boolean> result = this.asyncReconciliationService.doReconcile(charges, taskId);
                    result.join();
                }

            } else {
                LOGGER.debug("No available Charges for Task [{}]. Finishing", taskId);
                currentTask.setTaskState(TaskState.FINISHED);
                currentTask.setTaskFinishedDate(new Date());

                // FIXME: При некоторых обстоятельствах processedRows > totalRows.
                //  - В будущем исправить этот костыль.
                BigInteger totalRows = currentTask.getTaskTotalRows();
                BigInteger processedRows = currentTask.getTaskProcessedRows();
                if (processedRows.compareTo(totalRows) == 1) {
                    currentTask.setTaskProcessedRows(totalRows);
                }
                this.taskService.save(currentTask);
            }

        } catch (NoResultException | EmptyResultDataAccessException ignored) {
        }
    }
}

