package com.msr.rnip.reconciliation.service;

import com.msr.rnip.reconciliation.dao.ChargeDao;
import com.msr.rnip.reconciliation.dao.impl.CustomChargeDaoImpl;
import com.msr.rnip.reconciliation.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

//@author p4r53c
@Service
public class ChargeService {

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private CustomChargeDaoImpl customChargeDaoImpl;

    public List<Charge> getChargesForReconcile(int quantity) {
        return this.customChargeDaoImpl.getNotProcessedCharges(quantity);
    }

    public Optional<Charge> getCharge(String billId) {
        return this.chargeDao.findById(billId);
    }

    public long getAllChargesToProcess() {
        return this.customChargeDaoImpl.countNotProcessedCharges();
    }

    public void save(Charge charge) {
        this.chargeDao.saveAndFlush(charge);
    }

}
