package com.msr.rnip.reconciliation.dao.impl;

import com.msr.rnip.reconciliation.dao.CustomChargeDao;
import com.msr.rnip.reconciliation.model.Charge;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

//@author p4r53c
@Repository
@Qualifier("customChargeDaoImpl")
public class CustomChargeDaoImpl implements CustomChargeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Charge> getNotProcessedCharges(int quantity) {
        TypedQuery<Charge> query = entityManager.createQuery("SELECT c FROM Charge c " +
                                                                     "WHERE c.chargeIsProcessed IS NULL", Charge.class);

        return query.setMaxResults(quantity).getResultList();
    }

    @Override
    public long countNotProcessedCharges() {
        Query query = entityManager.createQuery("SELECT count(c.billId) FROM Charge c " +
                                                        "WHERE c.chargeIsProcessed IS NULL", Long.class);
        return (long) query.getSingleResult();
    }
}
