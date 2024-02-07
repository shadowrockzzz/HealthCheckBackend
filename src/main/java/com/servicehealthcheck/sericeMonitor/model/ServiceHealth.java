package com.servicehealthcheck.sericeMonitor.model;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


@Entity
@Table(name = "service_health")
public class ServiceHealth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String status;

  public Long getId() {
      return id;
  }

  public String getName() {
      return name;
  }

  public String getStatus() {
      return status;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public void setName(String name) {
      this.name = name;
  }

  public void setStatus(String status) {
      this.status = status;
  }
}