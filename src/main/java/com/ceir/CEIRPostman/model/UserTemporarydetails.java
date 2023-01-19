package com.ceir.CEIRPostman.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
@Entity
public class UserTemporarydetails {
	private static long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;     
	
	@OneToOne(fetch = FetchType.LAZY ,	optional = false)
	@JoinColumn(name = "user_id",nullable = false, insertable = true)
	private User userDetails;   
	private String emailId;   
	private String mobileNo;  
	private String emailIdOtp;
	private String mobileOtp;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@CreationTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@UpdateTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifiedOn;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmailIdOtp() {
		return emailIdOtp;
	}
	public void setEmailIdOtp(String emailIdOtp) {
		this.emailIdOtp = emailIdOtp;
	}
	public String getMobileOtp() {
		return mobileOtp;
	}
	public void setMobileOtp(String mobileOtp) {
		this.mobileOtp = mobileOtp;
	}
	
	public User getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(User userDetails) {
		this.userDetails = userDetails;
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
	public UserTemporarydetails() {
		super();
	}
	public UserTemporarydetails(User userDetails, String emailId, String mobileNo, String emailIdOtp,
			String mobileOtp) {
		super();
		this.userDetails = userDetails;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.emailIdOtp = emailIdOtp;
		this.mobileOtp = mobileOtp;
	}
	
	
	
}