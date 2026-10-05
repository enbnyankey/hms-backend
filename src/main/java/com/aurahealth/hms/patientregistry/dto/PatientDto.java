package com.aurahealth.hms.patientregistry.dto;

import java.util.UUID;

public record PatientDto(
        UUID id,
        String mrn,
        String name,
        int age,
        String sex,
        String phone,
        String address,
        String dob,
        String nextOfKinName,
        String nextOfKinPhone,
        String nhisNumber,
        String nhisStatus,
        String bloodGroup,
        String allergies,
        String category,
        String lastVisit,
        String registeredBy,
        String registeredOn,
        String status,
        boolean duplicateFlag
) {}
