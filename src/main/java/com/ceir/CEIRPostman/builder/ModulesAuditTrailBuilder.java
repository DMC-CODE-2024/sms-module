package com.ceir.CEIRPostman.builder;


import com.ceir.CEIRPostman.model.audit.ModulesAuditTrail;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class ModulesAuditTrailBuilder {

    public static ModulesAuditTrail forInsert(Integer statusCode, String status,
                                              String errorMessage, String moduleName,
                                              String action, String info) {
        ModulesAuditTrail modulesAuditTrail = new ModulesAuditTrail();

        modulesAuditTrail.setCreatedOn(LocalDateTime.now());
        modulesAuditTrail.setModifiedOn(LocalDateTime.now());
        modulesAuditTrail.setExecutionTime(Math.toIntExact(System.currentTimeMillis() / 1000));
        modulesAuditTrail.setStatusCode(statusCode);
        modulesAuditTrail.setStatus(status);
        modulesAuditTrail.setErrorMessage(errorMessage);
        modulesAuditTrail.setFeatureName("Send SMS");
        modulesAuditTrail.setModuleName(moduleName);
        modulesAuditTrail.setAction(action);
        modulesAuditTrail.setCount(0);
        modulesAuditTrail.setCount(0);
        modulesAuditTrail.setInfo(info);
        modulesAuditTrail.setServerName(getHostname());

        return modulesAuditTrail;
    }

    public static ModulesAuditTrail forUpdate(int id, Integer statusCode, String status,
                                              String errorMessage, String moduleName,
                                              String action, String info, int count, int failureCount, int executionTime, LocalDateTime startTime) {
        ModulesAuditTrail modulesAuditTrail = new ModulesAuditTrail();

        modulesAuditTrail.setId(id);
        modulesAuditTrail.setCreatedOn(startTime);
        modulesAuditTrail.setModifiedOn(LocalDateTime.now());
        modulesAuditTrail.setExecutionTime(Math.toIntExact(System.currentTimeMillis() / 1000) - executionTime);
        modulesAuditTrail.setStatusCode(statusCode);
        modulesAuditTrail.setStatus(status);
        modulesAuditTrail.setErrorMessage(errorMessage);
        modulesAuditTrail.setFeatureName("Send SMS");
        modulesAuditTrail.setModuleName(moduleName);
        modulesAuditTrail.setAction(action);
        modulesAuditTrail.setInfo(info);
        modulesAuditTrail.setCount(count);
        modulesAuditTrail.setFailureCount(failureCount);
        modulesAuditTrail.setServerName(getHostname());

        return modulesAuditTrail;
    }
    public static String getHostname() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (Exception ex) {
            return "NA";
        }
    }

}
