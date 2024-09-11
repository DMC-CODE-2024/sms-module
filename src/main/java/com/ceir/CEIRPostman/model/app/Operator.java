package com.ceir.CEIRPostman.model.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Operator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime createdOn;
    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedOn;
    private String operatorName;
    private String channelType;
    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
    private List<OperatorProperties> properties;

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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public List<OperatorProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<OperatorProperties> properties) {
        this.properties = properties;
    }

    public Operator(String channelType) {
        this.channelType = channelType;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", operatorName='" + operatorName + '\'' +
                ", channelType='" + channelType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
