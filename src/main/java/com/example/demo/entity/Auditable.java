package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class Auditable {

//  @CreatedDate
  @Column(name = "created_date", updatable = false)
  private LocalDateTime createdDate;

//  @LastModifiedDate
  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;

  @PrePersist
  protected void onCreate() {
    createdDate = LocalDateTime.now();
    lastModifiedDate = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    lastModifiedDate = LocalDateTime.now();
  }
}
