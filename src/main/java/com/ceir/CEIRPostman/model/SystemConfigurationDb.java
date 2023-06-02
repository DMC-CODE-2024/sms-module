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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class SystemConfigurationDb implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "created_on")
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date createdOn;

	@Column(name = "modified_on")
	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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

	private Integer type;

	@Transient
	private String typeInterp;

	private String remark;

	private Integer active;

	@Column(name = "feature_name")
	private String featureName;

	@Column(name = "user_type")
	private String userType;

	public SystemConfigurationDb() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public String getTag() {
		return tag;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public Integer getType() {
		return type;
	}

	public String getTypeInterp() {
		return typeInterp;
	}

	public String getRemark() {
		return remark;
	}

	public Integer getActive() {
		return active;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String getUserType() {
		return userType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setTypeInterp(String typeInterp) {
		this.typeInterp = typeInterp;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SystemConfigurationDb{");
		sb.append("id=").append(id);
		sb.append(", createdOn=").append(createdOn);
		sb.append(", modifiedOn=").append(modifiedOn);
		sb.append(", tag='").append(tag).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", type=").append(type);
		sb.append(", typeInterp='").append(typeInterp).append('\'');
		sb.append(", remark='").append(remark).append('\'');
		sb.append(", active=").append(active);
		sb.append(", featureName='").append(featureName).append('\'');
		sb.append(", userType='").append(userType).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
