package com.msr.rnip.reconciliation.dao;

import com.msr.rnip.reconciliation.model.Charge;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CustomChargeDao {
    List<Charge> getNotProcessedCharges (int quantity);
    long countNotProcessedCharges();
}
