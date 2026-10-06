package com.aurahealth.hms.wardbed.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "beds")
public class Bed {

    /**
     * AVAILABLE   clean and ready to admit
     * OCCUPIED    has a current admission (set only by admit/transfer, never by hand)
     * RESERVED    held for an incoming patient (theatre, PACU, transfer)
     * CLEANING    vacated, awaiting housekeeping / terminal clean
     * MAINTENANCE out of service
     */
    public enum BedStatus { AVAILABLE, OCCUPIED, RESERVED, CLEANING, MAINTENANCE }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(length = 160)
    private String room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BedStatus status;

    @Column(nullable = false)
    private boolean isolation;

    @Column(name = "status_note", length = 255)
    private String statusNote;

    @Column(name = "status_changed_at", nullable = false)
    private LocalDateTime statusChangedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (statusChangedAt == null) statusChangedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Changes the status and stamps when it happened. */
    public void changeStatus(BedStatus newStatus, String note) {
        this.status = newStatus;
        this.statusNote = note;
        this.statusChangedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public BedStatus getStatus() { return status; }
    public boolean isIsolation() { return isolation; }
    public void setIsolation(boolean isolation) { this.isolation = isolation; }
    public String getStatusNote() { return statusNote; }
    public LocalDateTime getStatusChangedAt() { return statusChangedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
