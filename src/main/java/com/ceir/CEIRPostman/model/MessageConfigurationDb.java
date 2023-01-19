package com.ceir.CEIRPostman.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class MessageConfigurationDb implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable =false)
	@CreationTimestamp
	private Date createdOn;
	@Column(nullable =false)
	@UpdateTimestamp
	private Date modifiedOn;
	@NotNull
	@NotBlank
	private String tag;
	
	@NotNull
	@NotBlank
	private String value;
	
	@NotNull
	@NotBlank
	private String description;
	
	@NotNull
	private Integer type; // have two values USER/SYSTEM.
	@Transient
	private String typeInterp;
	
	private String remark;
	
	@NotNull
	private Integer active;
	
	private String featureName;
	private String userType;
	private String Subject;
	private Integer channel;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTypeInterp() {
		return typeInterp;
	}
	public void setTypeInterp(String typeInterp) {
		this.typeInterp = typeInterp;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public String getSubject() {
		return Subject;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageConfigurationDb [id=");
		builder.append(id);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", modifiedOn=");
		builder.append(modifiedOn);
		builder.append(", tag=");
		builder.append(tag);
		builder.append(", value=");
		builder.append(value);
		builder.append(", description=");
		builder.append(description);
		builder.append(", type=");
		builder.append(type);
		builder.append(", typeInterp=");
		builder.append(typeInterp);
		builder.append(", remark=");
		builder.append(remark);
		builder.append(", active=");
		builder.append(active);
		builder.append(", featureName=");
		builder.append(featureName);
		builder.append(", userType=");
		builder.append(userType);
		builder.append("]");
		return builder.toString();
	}






}
