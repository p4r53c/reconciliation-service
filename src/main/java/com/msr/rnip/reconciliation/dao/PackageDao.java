package com.msr.rnip.reconciliation.dao;

import com.msr.rnip.reconciliation.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageDao extends JpaRepository<Package, String> {
}
