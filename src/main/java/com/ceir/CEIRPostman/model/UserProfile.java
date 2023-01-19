package com.ceir.CEIRPostman.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
@Entity 
public class UserProfile {
	private static long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;     
	private String firstName;  
	private String middleName;
	private String lastName;
	private String companyName;
	private Integer type;
	private Integer vatStatus;  
	private String vatNo;
	private String propertyLocation;
	private String street;
	private String locality;
	private String province;   
	private String country;
	private String passportNo;
	private String email;
	private String phoneNo;

	@Transient
	private String asTypeName;


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@UpdateTimestamp
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime  modifiedOn;
	private String phoneOtp;  
	private String emailOtp;
	private String displayName;

	private String employeeId;
	private String natureOfEmployment;
	private String designation;
	private String authorityName;
	private String authorityEmail;
	private String authorityPhoneNo;
	private String operatorTypeName;
	private Integer operatorTypeId;

	private String nidFilename;
	private String photoFilename;
	private String idCardFilename;


	@Transient
	private String username;

	@Transient 
	private long[] roles;
	@Transient
	private long usertypeId;
	@Transient
	private String password;    


	@OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "userid", nullable = false)
	private User user;  



	public long[] getRoles() {
		return roles;
	}
	public void setRoles(long[] roles) {
		this.roles = roles;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getUsertypeId() {
		return usertypeId;
	}
	public void setUsertypeId(long usertypeId) {
		this.usertypeId = usertypeId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getVatStatus() {
		return vatStatus;
	}
	public void setVatStatus(Integer vatStatus) {
		this.vatStatus = vatStatus;
	}
	public String getVatNo() {
		return vatNo;
	}
	public void setVatNo(String vatNo) {
		this.vatNo = vatNo;
	}
	public String getPropertyLocation() {
		return propertyLocation;
	}
	public void setPropertyLocation(String propertyLocation) {
		this.propertyLocation = propertyLocation;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPassportNo() {
		return passportNo;
	}
	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
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
	public String getPhoneOtp() {
		return phoneOtp;
	}
	public void setPhoneOtp(String phoneOtp) {
		this.phoneOtp = phoneOtp;
	}
	public String getEmailOtp() {
		return emailOtp;
	}
	public void setEmailOtp(String emailOtp) {
		this.emailOtp = emailOtp;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}


	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getNatureOfEmployment() {
		return natureOfEmployment;
	}
	public void setNatureOfEmployment(String natureOfEmployment) {
		this.natureOfEmployment = natureOfEmployment;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getAuthorityName() {
		return authorityName;
	}
	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}
	public String getAuthorityEmail() {
		return authorityEmail;
	}
	public void setAuthorityEmail(String authorityEmail) {
		this.authorityEmail = authorityEmail;
	}
	public String getAuthorityPhoneNo() {
		return authorityPhoneNo;
	}
	public void setAuthorityPhoneNo(String authorityPhoneNo) {
		this.authorityPhoneNo = authorityPhoneNo;
	}



	public String getOperatorTypeName() {
		return operatorTypeName;
	}
	public void setOperatorTypeName(String operatorTypeName) {
		this.operatorTypeName = operatorTypeName;
	}
	public Integer getOperatorTypeId() {
		return operatorTypeId;
	}
	public void setOperatorTypeId(Integer operatorTypeId) {
		this.operatorTypeId = operatorTypeId;
	}
	public String getNidFilename() {
		return nidFilename;
	}
	public void setNidFilename(String nidFilename) {
		this.nidFilename = nidFilename;
	}
	public String getPhotoFilename() {
		return photoFilename;
	}
	public void setPhotoFilename(String photoFilename) {
		this.photoFilename = photoFilename;
	}
	public String getIdCardFilename() {
		return idCardFilename;
	}
	public void setIdCardFilename(String idCardFilename) {
		this.idCardFilename = idCardFilename;
	}

	public String getAsTypeName() {
		return asTypeName;
	}
	public void setAsTypeName(String asTypeName) {
		this.asTypeName = asTypeName;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", companyName=" + companyName + ", type=" + type + ", vatStatus=" + vatStatus + ", vatNo="
				+ vatNo + ", propertyLocation=" + propertyLocation + ", street=" + street + ", locality=" + locality
				+ ", province=" + province + ", country=" + country + ", passportNo=" + passportNo + ", email=" + email
				+ ", phoneNo=" + phoneNo + ", asTypeName=" + asTypeName + ", createdOn=" + createdOn + ", modifiedOn="
				+ modifiedOn + ", phoneOtp=" + phoneOtp + ", emailOtp=" + emailOtp + ", displayName=" + displayName
				+ ", employeeId=" + employeeId + ", natureOfEmployment=" + natureOfEmployment + ", designation="
				+ designation + ", authorityName=" + authorityName + ", authorityEmail=" + authorityEmail
				+ ", authorityPhoneNo=" + authorityPhoneNo + ", operatorTypeName=" + operatorTypeName
				+ ", operatorTypeId=" + operatorTypeId + ", nidFilename=" + nidFilename + ", photoFilename="
				+ photoFilename + ", idCardFilename=" + idCardFilename + ", username=" + username + ", questionList="
				+  ", roles=" + roles + ", usertypeId=" + usertypeId + ", password=" + password + "]";
	}

}
