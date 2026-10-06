package com.aurahealth.hms.wardbed.entity;

import com.aurahealth.hms.patientregistry.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/** One inpatient stay. {@code bed} is always the bed the patient is in right now. */
@Entity
@Table(name = "admissions")
public class Admission {

    public enum AdmissionStatus { ACTIVE, DISCHARGE_READY, DISCHARGED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdmissionStatus status;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(length = 160)
    private String attending;

    @Column(length = 500)
    private String notes;

    @Column(name = "admitted_at", nullable = false)
    private LocalDateTime admittedAt;

    @Column(name = "admitted_by", length = 160)
    private String admittedBy;

    @Column(name = "discharged_at")
    private LocalDateTime dischargedAt;

    @Column(name = "discharged_by", length = 160)
    private String dischargedBy;

    @Column(name = "discharge_notes", length = 500)
    private String dischargeNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (admittedAt == null) admittedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isCurrent() {
        return status != AdmissionStatus.DISCHARGED;
    }

    public UUID getId() { return id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Bed getBed() { return bed; }
    public void setBed(Bed bed) { this.bed = bed; }
    public AdmissionStatus getStatus() { return status; }
    public void setStatus(AdmissionStatus status) { this.status = status; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getAttending() { return attending; }
    public void setAttending(String attending) { this.attending = attending; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getAdmittedAt() { return admittedAt; }
    public void setAdmittedAt(LocalDateTime admittedAt) { this.admittedAt = admittedAt; }
    public String getAdmittedBy() { return admittedBy; }
    public void setAdmittedBy(String admittedBy) { this.admittedBy = admittedBy; }
    public LocalDateTime getDischargedAt() { return dischargedAt; }
    public void setDischargedAt(LocalDateTime dischargedAt) { this.dischargedAt = dischargedAt; }
    public String getDischargedBy() { return dischargedBy; }
    public void setDischargedBy(String dischargedBy) { this.dischargedBy = dischargedBy; }
    public String getDischargeNotes() { return dischargeNotes; }
    public void setDischargeNotes(String dischargeNotes) { this.dischargeNotes = dischargeNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
