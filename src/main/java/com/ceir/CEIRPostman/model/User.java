package com.ceir.CEIRPostman.model;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "users")
public class User {  
	private static long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String username;
	@JsonIgnore
	private String password;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@CreationTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@UpdateTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifiedOn;

	private Integer currentStatus;  
	private Integer previousStatus; 
	private String remark;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usertype_id", nullable = false) 
	private Usertype usertype;


	@JsonIgnore
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	UserProfile userProfile;

	@JsonIgnore
	@OneToOne(mappedBy = "userDetails", cascade = {CascadeType.PERSIST, CascadeType.REMOVE},fetch = FetchType.LAZY)
	UserTemporarydetails userTemporarydetails; 
	
	

	public UserTemporarydetails getUserTemporarydetails() {
		return userTemporarydetails;
	}
	public void setUserTemporarydetails(UserTemporarydetails userTemporarydetails) {
		this.userTemporarydetails = userTemporarydetails;
	}
	public long getId() {      
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {

		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn =modifiedOn;
	}
	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Usertype getUsertype() { return usertype; } public void
	setUsertype(Usertype usertype) { this.usertype = usertype; }
	public Integer getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(Integer currentStatus) {
		this.currentStatus = currentStatus;
	}
	public Integer getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(Integer previousStatus) {
		this.previousStatus = previousStatus;
	}  
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + ", currentStatus=" + currentStatus + ", previousStatus="
				+ previousStatus + ", remark=" + remark + "]";
	}

}
