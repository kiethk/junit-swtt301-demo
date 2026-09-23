package com.rtms.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "veterinarian_profiles")
public class VeterinarianProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "license_issued_date")
    private LocalDate licenseIssuedDate;

    @Column(name = "specialization")
    private String specialization;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getLicenseIssuedDate() {
        return licenseIssuedDate;
    }

    public void setLicenseIssuedDate(LocalDate licenseIssuedDate) {
        this.licenseIssuedDate = licenseIssuedDate;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
