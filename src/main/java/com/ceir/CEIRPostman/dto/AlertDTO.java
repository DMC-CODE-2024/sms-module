package com.ceir.CEIRPostman.dto;

import java.util.Objects;

public class AlertDTO {
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

    AlertDTO(String alertId, String alertMessage, String alertProcess, String description, String featureName, String ip, String priority, String remarks, String serverName, Integer status, String txnId, Integer userId) {
        this.alertId = alertId;
        this.alertMessage = alertMessage;
        this.alertProcess = alertProcess;
        this.description = description;
        this.featureName = featureName;
        this.ip = ip;
        this.priority = priority;
        this.remarks = remarks;
        this.serverName = serverName;
        this.status = status;
        this.txnId = txnId;
        this.userId = userId;
    }

    public static AlertDTOBuilder builder() {
        return new AlertDTOBuilder();
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public void setAlertProcess(String alertProcess) {
        this.alertProcess = alertProcess;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof com.ceir.CEIRPostman.dto.AlertDTO))
            return false;
        com.ceir.CEIRPostman.dto.AlertDTO other = (com.ceir.CEIRPostman.dto.AlertDTO)o;
        if (!other.canEqual(this))
            return false;
        Object this$status = getStatus(), other$status = other.getStatus();
        if ((this$status == null) ? (other$status != null) : !this$status.equals(other$status))
            return false;
        Object this$userId = getUserId(), other$userId = other.getUserId();
        if ((this$userId == null) ? (other$userId != null) : !this$userId.equals(other$userId))
            return false;
        Object this$alertId = getAlertId(), other$alertId = other.getAlertId();
        if ((this$alertId == null) ? (other$alertId != null) : !this$alertId.equals(other$alertId))
            return false;
        Object this$alertMessage = getAlertMessage(), other$alertMessage = other.getAlertMessage();
        if ((this$alertMessage == null) ? (other$alertMessage != null) : !this$alertMessage.equals(other$alertMessage))
            return false;
        Object this$alertProcess = getAlertProcess(), other$alertProcess = other.getAlertProcess();
        if ((this$alertProcess == null) ? (other$alertProcess != null) : !this$alertProcess.equals(other$alertProcess))
            return false;
        Object this$description = getDescription(), other$description = other.getDescription();
        if ((this$description == null) ? (other$description != null) : !this$description.equals(other$description))
            return false;
        Object this$featureName = getFeatureName(), other$featureName = other.getFeatureName();
        if ((this$featureName == null) ? (other$featureName != null) : !this$featureName.equals(other$featureName))
            return false;
        Object this$ip = getIp(), other$ip = other.getIp();
        if ((this$ip == null) ? (other$ip != null) : !this$ip.equals(other$ip))
            return false;
        Object this$priority = getPriority(), other$priority = other.getPriority();
        if ((this$priority == null) ? (other$priority != null) : !this$priority.equals(other$priority))
            return false;
        Object this$remarks = getRemarks(), other$remarks = other.getRemarks();
        if ((this$remarks == null) ? (other$remarks != null) : !this$remarks.equals(other$remarks))
            return false;
        Object this$serverName = getServerName(), other$serverName = other.getServerName();
        if ((this$serverName == null) ? (other$serverName != null) : !this$serverName.equals(other$serverName))
            return false;
        Object this$txnId = getTxnId(), other$txnId = other.getTxnId();
        return !((this$txnId == null) ? (other$txnId != null) : !this$txnId.equals(other$txnId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof com.ceir.CEIRPostman.dto.AlertDTO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId, alertMessage, alertProcess, description, featureName, ip, priority, remarks, serverName, status, txnId, userId);
    }
/*    public int hashCode() {
        int PRIME = 59;
        result = 1;
        Object $status = getStatus();
        result = result * 59 + (($status == null) ? 43 : $status.hashCode());
        Object $userId = getUserId();
        result = result * 59 + (($userId == null) ? 43 : $userId.hashCode());
        Object $alertId = getAlertId();
        result = result * 59 + (($alertId == null) ? 43 : $alertId.hashCode());
        Object $alertMessage = getAlertMessage();
        result = result * 59 + (($alertMessage == null) ? 43 : $alertMessage.hashCode());
        Object $alertProcess = getAlertProcess();
        result = result * 59 + (($alertProcess == null) ? 43 : $alertProcess.hashCode());
        Object $description = getDescription();
        result = result * 59 + (($description == null) ? 43 : $description.hashCode());
        Object $featureName = getFeatureName();
        result = result * 59 + (($featureName == null) ? 43 : $featureName.hashCode());
        Object $ip = getIp();
        result = result * 59 + (($ip == null) ? 43 : $ip.hashCode());
        Object $priority = getPriority();
        result = result * 59 + (($priority == null) ? 43 : $priority.hashCode());
        Object $remarks = getRemarks();
        result = result * 59 + (($remarks == null) ? 43 : $remarks.hashCode());
        Object $serverName = getServerName();
        result = result * 59 + (($serverName == null) ? 43 : $serverName.hashCode());
        Object $txnId = getTxnId();
        return result * 59 + (($txnId == null) ? 43 : $txnId.hashCode());
    }*/

    public String toString() {
        return "AlertDTO(alertId=" + getAlertId() + ", alertMessage=" + getAlertMessage() + ", alertProcess=" + getAlertProcess() + ", description=" + getDescription() + ", featureName=" + getFeatureName() + ", ip=" + getIp() + ", priority=" + getPriority() + ", remarks=" + getRemarks() + ", serverName=" + getServerName() + ", status=" + getStatus() + ", txnId=" + getTxnId() + ", userId=" + getUserId() + ")";
    }

    public String getAlertId() {
        return this.alertId;
    }

    public String getAlertMessage() {
        return this.alertMessage;
    }

    public String getAlertProcess() {
        return this.alertProcess;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public String getIp() {
        return this.ip;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getServerName() {
        return this.serverName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getTxnId() {
        return this.txnId;
    }

    public Integer getUserId() {
        return this.userId;
    }
}
