package com.ceir.CEIRPostman.dto;

import com.ceir.CEIRPostman.dto.AlertDTO;

public class AlertDTOBuilder {
    private String alertId;

    private String alertMessage;

    private String alertProcess;

    private String description;

    private String featureName;

    private String ip;

    private String priority;

    private String remarks;

    private String serverName;

    private Integer status;

    private String txnId;

    private Integer userId;

    public AlertDTOBuilder alertId(String alertId) {
        this.alertId = alertId;
        return this;
    }

    public AlertDTOBuilder alertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
        return this;
    }

    public AlertDTOBuilder alertProcess(String alertProcess) {
        this.alertProcess = alertProcess;
        return this;
    }

    public AlertDTOBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AlertDTOBuilder featureName(String featureName) {
        this.featureName = featureName;
        return this;
    }

    public AlertDTOBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    public AlertDTOBuilder priority(String priority) {
        this.priority = priority;
        return this;
    }

    public AlertDTOBuilder remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public AlertDTOBuilder serverName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public AlertDTOBuilder status(Integer status) {
        this.status = status;
        return this;
    }

    public AlertDTOBuilder txnId(String txnId) {
        this.txnId = txnId;
        return this;
    }

    public AlertDTOBuilder userId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public AlertDTO build() {
        return new AlertDTO(this.alertId, this.alertMessage, this.alertProcess, this.description, this.featureName, this.ip, this.priority, this.remarks, this.serverName, this.status, this.txnId, this.userId);
    }

    public String toString() {
        return "AlertDTO.AlertDTOBuilder(alertId=" + this.alertId + ", alertMessage=" + this.alertMessage + ", alertProcess=" + this.alertProcess + ", description=" + this.description + ", featureName=" + this.featureName + ", ip=" + this.ip + ", priority=" + this.priority + ", remarks=" + this.remarks + ", serverName=" + this.serverName + ", status=" + this.status + ", txnId=" + this.txnId + ", userId=" + this.userId + ")";
    }
}

