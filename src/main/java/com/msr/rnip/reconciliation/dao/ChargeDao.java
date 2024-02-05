package com.msr.rnip.reconciliation.dao;

import com.msr.rnip.reconciliation.model.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeDao extends JpaRepository<Charge, String>, CustomChargeDao {

}
