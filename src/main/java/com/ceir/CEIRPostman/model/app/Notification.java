package com.ceir.CEIRPostman.model.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Notification implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreationTimestamp
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime modifiedOn;

	private String channelType;

	@Column(length = 1000)
	private String message;

	private Long userId;

	private Long featureId;

	private String featureTxnId;

	private String featureName;

	private String subFeature;

	private Integer status;

	private String subject;

	private Integer retryCount;

	@Column(length = 10)
	private String referTable;

	@Column(length = 20)
	private String roleType;

	@Column(length = 50)
	private String receiverUserType;

//	private Integer authorityStatus;

	@Column(nullable = true)
	private String email;

	@Column(nullable = false)
	private String msisdn;

	@Column(nullable = false, unique = true)
	private String operatorName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime notificationSentTime;

	@Column(nullable = false, unique = true)
	private String corelationId;

	@Column(nullable = false)
	private String msgLang;

	@Column
	private int deliveryStatus;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime deliveryTime;

	@Column
	private String sendSmsInterface;

	@Column
	private LocalDateTime smsScheduledTime;

	public Notification() {

	}

	public Notification(Long id, LocalDateTime createdOn, LocalDateTime modifiedOn, String channelType, String message, Long userId, Long featureId, String featureTxnId, String featureName, String subFeature, Integer status, String subject, Integer retryCount, String referTable, String roleType, String receiverUserType, String email, String msisdn, String operatorName, LocalDateTime notificationSentTime, String corelationId, String msgLang, int deliveryStatus, LocalDateTime deliveryTime, String sendSmsInterface, LocalDateTime smsScheduledTime) {
		this.id = id;
		this.createdOn = createdOn;
		this.modifiedOn = modifiedOn;
		this.channelType = channelType;
		this.message = message;
		this.userId = userId;
		this.featureId = featureId;
		this.featureTxnId = featureTxnId;
		this.featureName = featureName;
		this.subFeature = subFeature;
		this.status = status;
		this.subject = subject;
		this.retryCount = retryCount;
		this.referTable = referTable;
		this.roleType = roleType;
		this.receiverUserType = receiverUserType;
		this.email = email;
		this.msisdn = msisdn;
		this.operatorName = operatorName;
		this.notificationSentTime = notificationSentTime;
		this.corelationId = corelationId;
		this.msgLang = msgLang;
		this.deliveryStatus = deliveryStatus;
		this.deliveryTime = deliveryTime;
		this.sendSmsInterface = sendSmsInterface;
		this.smsScheduledTime = smsScheduledTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	public String getFeatureTxnId() {
		return featureTxnId;
	}

	public void setFeatureTxnId(String featureTxnId) {
		this.featureTxnId = featureTxnId;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getSubFeature() {
		return subFeature;
	}

	public void setSubFeature(String subFeature) {
		this.subFeature = subFeature;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public String getReferTable() {
		return referTable;
	}

	public void setReferTable(String referTable) {
		this.referTable = referTable;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getReceiverUserType() {
		return receiverUserType;
	}

	public void setReceiverUserType(String receiverUserType) {
		this.receiverUserType = receiverUserType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public LocalDateTime getNotificationSentTime() {
		return notificationSentTime;
	}

	public void setNotificationSentTime(LocalDateTime notificationSentTime) {
		this.notificationSentTime = notificationSentTime;
	}

	public String getCorelationId() {
		return corelationId;
	}

	public void setCorelationId(String corelationId) {
		this.corelationId = corelationId;
	}

	public String getMsgLang() {
		return msgLang;
	}

	public void setMsgLang(String msgLang) {
		this.msgLang = msgLang;
	}

	public int getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(int deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public LocalDateTime getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(LocalDateTime deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getSendSmsInterface() {
		return sendSmsInterface;
	}

	public void setSendSmsInterface(String sendSmsInterface) {
		this.sendSmsInterface = sendSmsInterface;
	}

	public LocalDateTime getSmsScheduledTime() {
		return smsScheduledTime;
	}

	public void setSmsScheduledTime(LocalDateTime smsScheduledTime) {
		this.smsScheduledTime = smsScheduledTime;
	}

	@Override
	public String toString() {
		return "Notification{" +
				"id=" + id +
				", createdOn=" + createdOn +
				", modifiedOn=" + modifiedOn +
				", channelType='" + channelType + '\'' +
				", message='" + message + '\'' +
				", userId=" + userId +
				", featureId=" + featureId +
				", featureTxnId='" + featureTxnId + '\'' +
				", featureName='" + featureName + '\'' +
				", subFeature='" + subFeature + '\'' +
				", status=" + status +
				", subject='" + subject + '\'' +
				", retryCount=" + retryCount +
				", referTable='" + referTable + '\'' +
				", roleType='" + roleType + '\'' +
				", receiverUserType='" + receiverUserType + '\'' +
				", email='" + email + '\'' +
				", msisdn='" + msisdn + '\'' +
				", operatorName='" + operatorName + '\'' +
				", notificationSentTime=" + notificationSentTime +
				", corelationId='" + corelationId + '\'' +
				", msgLang='" + msgLang + '\'' +
				", deliveryStatus=" + deliveryStatus +
				", deliveryTime=" + deliveryTime +
				", sendSmsInterface='" + sendSmsInterface + '\'' +
				", smsScheduledTime=" + smsScheduledTime +
				'}';
	}
}
