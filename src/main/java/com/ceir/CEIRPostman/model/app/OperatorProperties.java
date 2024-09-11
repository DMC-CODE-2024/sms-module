package com.ceir.CEIRPostman.model.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class OperatorProperties implements Serializable {
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
//    private Long operatorId;
    private String key;
    private String value;
    @ManyToOne
    @JoinColumn(name = "operator_id", referencedColumnName = "id")
    private Operator operator;

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

//    public Long getOperatorId() {
//        return operatorId;
//    }
//
//    public void setOperatorId(Long operatorId) {
//        this.operatorId = operatorId;
//    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "OperatorProperties{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
//                ", operatorId=" + operatorId +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", operator=" + operator +
                '}';
    }
}
