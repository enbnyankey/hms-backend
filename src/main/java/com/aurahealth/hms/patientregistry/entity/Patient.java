package com.aurahealth.hms.patientregistry.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String mrn;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Sex sex;

    @Column(length = 32)
    private String phone;

    @Column(length = 255)
    private String address;

    private LocalDate dob;

    @Column(name = "next_of_kin_name", length = 160)
    private String nextOfKinName;

    @Column(name = "next_of_kin_phone", length = 32)
    private String nextOfKinPhone;

    @Column(name = "nhis_number", length = 32)
    private String nhisNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "nhis_status", nullable = false, length = 24)
    private NhisStatus nhisStatus;

    @Column(name = "blood_group", length = 8)
    private String bloodGroup;

    @Column(length = 500)
    private String allergies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PatientCategory category;

    @Column(name = "last_visit")
    private LocalDate lastVisit;

    @Column(name = "registered_by", length = 160)
    private String registeredBy;

    @Column(name = "registered_on")
    private LocalDate registeredOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PatientRecordStatus status;

    @Column(name = "duplicate_flag", nullable = false)
    private boolean duplicateFlag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Sex { Male, Female }

    public enum NhisStatus { Verified, Pending, Expired, Not_Enrolled }

    public enum PatientCategory { Walk_in, Inpatient, Outpatient, Referral }

    public enum PatientRecordStatus { Active, Inactive }

    // --- getters and setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMrn() { return mrn; }
    public void setMrn(String mrn) { this.mrn = mrn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getNextOfKinName() { return nextOfKinName; }
    public void setNextOfKinName(String nextOfKinName) { this.nextOfKinName = nextOfKinName; }

    public String getNextOfKinPhone() { return nextOfKinPhone; }
    public void setNextOfKinPhone(String nextOfKinPhone) { this.nextOfKinPhone = nextOfKinPhone; }

    public String getNhisNumber() { return nhisNumber; }
    public void setNhisNumber(String nhisNumber) { this.nhisNumber = nhisNumber; }

    public NhisStatus getNhisStatus() { return nhisStatus; }
    public void setNhisStatus(NhisStatus nhisStatus) { this.nhisStatus = nhisStatus; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public PatientCategory getCategory() { return category; }
    public void setCategory(PatientCategory category) { this.category = category; }

    public LocalDate getLastVisit() { return lastVisit; }
    public void setLastVisit(LocalDate lastVisit) { this.lastVisit = lastVisit; }

    public String getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(String registeredBy) { this.registeredBy = registeredBy; }

    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }

    public PatientRecordStatus getStatus() { return status; }
    public void setStatus(PatientRecordStatus status) { this.status = status; }

    public boolean isDuplicateFlag() { return duplicateFlag; }
    public void setDuplicateFlag(boolean duplicateFlag) { this.duplicateFlag = duplicateFlag; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
