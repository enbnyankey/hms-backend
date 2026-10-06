package com.aurahealth.hms.wardbed.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bed_transfers")
public class BedTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admission_id", nullable = false)
    private Admission admission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_bed_id", nullable = false)
    private Bed fromBed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_bed_id", nullable = false)
    private Bed toBed;

    @Column(length = 255)
    private String reason;

    @Column(name = "transferred_by", length = 160)
    private String transferredBy;

    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;

    @PrePersist
    void onCreate() {
        if (transferredAt == null) transferredAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public Admission getAdmission() { return admission; }
    public void setAdmission(Admission admission) { this.admission = admission; }
    public Bed getFromBed() { return fromBed; }
    public void setFromBed(Bed fromBed) { this.fromBed = fromBed; }
    public Bed getToBed() { return toBed; }
    public void setToBed(Bed toBed) { this.toBed = toBed; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getTransferredBy() { return transferredBy; }
    public void setTransferredBy(String transferredBy) { this.transferredBy = transferredBy; }
    public LocalDateTime getTransferredAt() { return transferredAt; }
}
