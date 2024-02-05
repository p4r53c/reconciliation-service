package com.msr.rnip.reconciliation.service;

import com.msr.rnip.reconciliation.dao.PackageDao;
import com.msr.rnip.reconciliation.model.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@author p4r53c
@Service
public class PackageService {

    @Autowired
    private PackageDao packageDao;

    public List<Package> getAllPackages() {
        return this.packageDao.findAll();
    }

    public Optional<Package> getPackage(String packageId) {
        return this.packageDao.findById(packageId);
    }

    public Package createPackage(Package p) {
        return this.packageDao.saveAndFlush(p);
    }

    public void save(Package p) {
        this.packageDao.saveAndFlush(p);
    }
}
