package com.aurahealth.hms.wardbed.dto;

import com.aurahealth.hms.patientregistry.entity.Patient;
import com.aurahealth.hms.wardbed.dto.WardDtos.*;
import com.aurahealth.hms.wardbed.entity.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class WardBedMapper {

    public BedDto toBedDto(Bed bed, Admission current) {
        return new BedDto(
                bed.getId(),
                bed.getCode(),
                bed.getWard().getId(),
                bed.getWard().getCode(),
                bed.getRoom(),
                bed.getStatus(),
                bed.isIsolation(),
                bed.getStatusNote(),
                bed.getStatusChangedAt(),
                current == null ? null : toOccupantDto(current)
        );
    }

    public OccupantDto toOccupantDto(Admission a) {
        Patient p = a.getPatient();
        return new OccupantDto(
                a.getId(),
                p.getId(),
                p.getName(),
                p.getMrn(),
                p.getAge(),
                sex(p),
                a.getDiagnosis(),
                a.getAttending(),
                a.getNotes(),
                a.getAdmittedAt(),
                dayOfStay(a),
                a.getStatus()
        );
    }

    public AdmissionDto toAdmissionDto(Admission a) {
        Patient p = a.getPatient();
        Bed b = a.getBed();
        return new AdmissionDto(
                a.getId(),
                p.getId(),
                p.getName(),
                p.getMrn(),
                p.getAge(),
                sex(p),
                b.getId(),
                b.getCode(),
                b.getWard().getCode(),
                b.getWard().getName(),
                a.getStatus(),
                a.getDiagnosis(),
                a.getAttending(),
                a.getNotes(),
                a.getAdmittedAt(),
                a.getAdmittedBy(),
                dayOfStay(a),
                a.getDischargedAt(),
                a.getDischargedBy(),
                a.getDischargeNotes()
        );
    }

    public TransferDto toTransferDto(BedTransfer t) {
        return new TransferDto(
                t.getId(),
                t.getFromBed().getCode(),
                t.getToBed().getCode(),
                t.getReason(),
                t.getTransferredBy(),
                t.getTransferredAt()
        );
    }

    public AdmissionRequestDto toRequestDto(AdmissionRequest r) {
        Patient p = r.getPatient();
        LocalDateTime end = r.getResolvedAt() != null ? r.getResolvedAt() : LocalDateTime.now();
        return new AdmissionRequestDto(
                r.getId(),
                p.getId(),
                p.getName(),
                p.getMrn(),
                p.getAge(),
                sex(p),
                p.getNhisNumber(),
                r.getSource(),
                r.getUrgency(),
                r.getDiagnosis(),
                r.getNotes(),
                r.getPreferredWard() == null ? null : r.getPreferredWard().getCode(),
                r.getSuggestedBed() == null ? null : r.getSuggestedBed().getId(),
                r.getSuggestedBed() == null ? null : r.getSuggestedBed().getCode(),
                r.getStatus(),
                r.getRequestedAt(),
                Math.max(0, Duration.between(r.getRequestedAt(), end).toMinutes()),
                r.getAdmission() == null ? null : r.getAdmission().getId()
        );
    }

    /** "Day 1" is the day of admission, matching the ward board's "Day 6" labels. */
    private long dayOfStay(Admission a) {
        LocalDate end = a.getDischargedAt() != null ? a.getDischargedAt().toLocalDate() : LocalDate.now();
        return ChronoUnit.DAYS.between(a.getAdmittedAt().toLocalDate(), end) + 1;
    }

    private String sex(Patient p) {
        return p.getSex() == null ? null : p.getSex().name();
    }
}
