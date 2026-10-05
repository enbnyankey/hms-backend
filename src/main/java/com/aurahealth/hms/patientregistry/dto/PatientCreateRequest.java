package com.aurahealth.hms.patientregistry.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Mirrors the frontend's PatientFormValues (hms/lib/patient-registry-data.ts)
 * used by the "New Registration / Triage" form.
 */
public record PatientCreateRequest(
        @NotBlank String name,
        @NotBlank String dob,
        String age,
        @NotBlank String sex,
        String phone,
        String address,
        String nextOfKinName,
        String nextOfKinPhone,
        String nhisNumber,
        String bloodGroup,
        String allergies,
        @NotBlank String category
) {}
