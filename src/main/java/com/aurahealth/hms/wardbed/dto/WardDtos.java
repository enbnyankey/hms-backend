package com.aurahealth.hms.wardbed.dto;

import com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.RequestStatus;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.Urgency;
import com.aurahealth.hms.wardbed.entity.Bed.BedStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request/response shapes for the Ward & Bed Manager API. Timestamps are ISO
 * strings (e.g. "2026-10-06T14:15:00") so the frontend can render "28h ago",
 * "Wait: 1h 15m" etc. itself.
 */
public final class WardDtos {

    private WardDtos() {}

    // ---- responses ------------------------------------------------------

    public record BedCounts(long total, long occupied, long available, long reserved,
                            long cleaning, long maintenance, double occupancyPct) {}

    public record WardSummaryDto(UUID id, String code, String name, String shortName,
                                 String location, String chargeNurse, BedCounts beds) {}

    public record CensusDto(BedCounts beds, int wards, long dischargeReady, long pendingRequests) {}

    public record OccupantDto(UUID admissionId, UUID patientId, String patientName, String mrn,
                              int age, String sex, String diagnosis, String attending, String notes,
                              LocalDateTime admittedAt, long dayOfStay, AdmissionStatus admissionStatus) {}

    public record BedDto(UUID id, String code, UUID wardId, String wardCode, String room,
                         BedStatus status, boolean isolation, String statusNote,
                         LocalDateTime statusChangedAt, OccupantDto occupant) {}

    public record AdmissionDto(UUID id, UUID patientId, String patientName, String mrn, int age, String sex,
                               UUID bedId, String bedCode, String wardCode, String wardName,
                               AdmissionStatus status, String diagnosis, String attending, String notes,
                               LocalDateTime admittedAt, String admittedBy, long dayOfStay,
                               LocalDateTime dischargedAt, String dischargedBy, String dischargeNotes) {}

    public record TransferDto(UUID id, String fromBedCode, String toBedCode, String reason,
                              String transferredBy, LocalDateTime transferredAt) {}

    public record AdmissionRequestDto(UUID id, UUID patientId, String patientName, String mrn, int age,
                                      String sex, String nhisNumber, String source, Urgency urgency,
                                      String diagnosis, String notes, String preferredWardCode,
                                      UUID suggestedBedId, String suggestedBedCode, RequestStatus status,
                                      LocalDateTime requestedAt, long waitingMinutes, UUID admissionId) {}

    // ---- requests -------------------------------------------------------

    /** Admit a patient into a bed. Pass admissionRequestId when admitting from the queue. */
    public record AdmitRequest(@NotNull UUID patientId,
                               @NotNull UUID bedId,
                               @NotBlank @Size(max = 255) String diagnosis,
                               @Size(max = 160) String attending,
                               @Size(max = 500) String notes,
                               UUID admissionRequestId) {}

    public record TransferRequest(@NotNull UUID toBedId, @Size(max = 255) String reason) {}

    public record DischargeRequest(@Size(max = 500) String notes) {}

    /** Manual bed status change (mark clean, reserve, take out of service ...). */
    public record BedStatusUpdateRequest(@NotNull BedStatus status, @Size(max = 255) String note) {}

    public record CreateAdmissionRequest(@NotNull UUID patientId,
                                         @NotBlank @Size(max = 120) String source,
                                         @NotNull Urgency urgency,
                                         @NotBlank @Size(max = 255) String diagnosis,
                                         @Size(max = 500) String notes,
                                         UUID preferredWardId,
                                         UUID suggestedBedId) {}
}
