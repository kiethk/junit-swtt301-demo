package com.rtms.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_horse_profiles")
public class CandidateHorseProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admission_id", nullable = false, unique = true)
    private Long admissionId;

    @Column(nullable = false)
    private String name;

    private String breed;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "registry_name")
    private String registryName;

    @Column(name = "sire_name")
    private String sireName;

    @Column(name = "sire_registration_number", length = 100)
    private String sireRegistrationNumber;

    @Column(name = "dam_name")
    private String damName;

    @Column(name = "dam_registration_number", length = 100)
    private String damRegistrationNumber;

    @Column(name = "pedigree_notes", length = 2000)
    private String pedigreeNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(Long admissionId) {
        this.admissionId = admissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getSireName() {
        return sireName;
    }

    public void setSireName(String sireName) {
        this.sireName = sireName;
    }

    public String getSireRegistrationNumber() {
        return sireRegistrationNumber;
    }

    public void setSireRegistrationNumber(String sireRegistrationNumber) {
        this.sireRegistrationNumber = sireRegistrationNumber;
    }

    public String getDamName() {
        return damName;
    }

    public void setDamName(String damName) {
        this.damName = damName;
    }

    public String getDamRegistrationNumber() {
        return damRegistrationNumber;
    }

    public void setDamRegistrationNumber(String damRegistrationNumber) {
        this.damRegistrationNumber = damRegistrationNumber;
    }

    public String getPedigreeNotes() {
        return pedigreeNotes;
    }

    public void setPedigreeNotes(String pedigreeNotes) {
        this.pedigreeNotes = pedigreeNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}