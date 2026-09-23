package com.rtms.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "trainer_profiles")
public class TrainerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "certification_number", nullable = false)
    private String certificationNumber;

    @Column(name = "certification_issued_date")
    private LocalDate certificationIssuedDate;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }

    public LocalDate getCertificationIssuedDate() {
        return certificationIssuedDate;
    }

    public void setCertificationIssuedDate(LocalDate certificationIssuedDate) {
        this.certificationIssuedDate = certificationIssuedDate;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
