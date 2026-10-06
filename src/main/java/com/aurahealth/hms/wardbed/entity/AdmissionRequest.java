package com.aurahealth.hms.wardbed.entity;

import com.aurahealth.hms.patientregistry.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/** A patient waiting for a bed (the admissions queue on the ward board). */
@Entity
@Table(name = "admission_requests")
public class AdmissionRequest {

    /** Frontend mapping: critical -> CRITICAL, warning -> URGENT, neutral -> ROUTINE. */
    public enum Urgency { CRITICAL, URGENT, ROUTINE }

    public enum RequestStatus { PENDING, ADMITTED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 120)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Urgency urgency;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_ward_id")
    private Ward preferredWard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_bed_id")
    private Bed suggestedBed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private RequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "requested_by", length = 160)
    private String requestedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id")
    private Admission admission;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (requestedAt == null) requestedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Urgency getUrgency() { return urgency; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Ward getPreferredWard() { return preferredWard; }
    public void setPreferredWard(Ward preferredWard) { this.preferredWard = preferredWard; }
    public Bed getSuggestedBed() { return suggestedBed; }
    public void setSuggestedBed(Bed suggestedBed) { this.suggestedBed = suggestedBed; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public Admission getAdmission() { return admission; }
    public void setAdmission(Admission admission) { this.admission = admission; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
