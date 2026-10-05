package com.aurahealth.hms.patientregistry.dto;

import com.aurahealth.hms.patientregistry.entity.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class PatientMapper {

    // Matches the display format used across the Next.js frontend, e.g. "25 Sep 2026".
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    public PatientDto toDto(Patient p) {
        return new PatientDto(
                p.getId(),
                p.getMrn(),
                p.getName(),
                p.getAge(),
                p.getSex() == null ? null : p.getSex().name(),
                p.getPhone(),
                p.getAddress(),
                formatDate(p.getDob()),
                p.getNextOfKinName(),
                p.getNextOfKinPhone(),
                p.getNhisNumber(),
                p.getNhisStatus() == null ? null : displayEnum(p.getNhisStatus().name()),
                p.getBloodGroup(),
                p.getAllergies(),
                p.getCategory() == null ? null : displayEnum(p.getCategory().name()),
                formatDate(p.getLastVisit()),
                p.getRegisteredBy(),
                formatDate(p.getRegisteredOn()),
                p.getStatus() == null ? null : p.getStatus().name(),
                p.isDuplicateFlag()
        );
    }

    private String formatDate(LocalDate date) {
        return date == null ? null : DATE_FMT.format(date);
    }

    // "Not_Enrolled" -> "Not Enrolled", "Walk_in" -> "Walk-in" (matches the frontend's literal unions)
    private String displayEnum(String enumName) {
        if ("Walk_in".equals(enumName)) return "Walk-in";
        return enumName.replace('_', ' ');
    }

    public LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DATE_FMT);
    }
}
