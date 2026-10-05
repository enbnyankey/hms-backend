package com.aurahealth.hms.patientregistry.controller;

import com.aurahealth.hms.patientregistry.dto.PatientCreateRequest;
import com.aurahealth.hms.patientregistry.dto.PatientDto;
import com.aurahealth.hms.patientregistry.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Role gate here matches ROUTE_ACCESS["/patient-registry"] in hms/lib/auth.tsx:
 * admin, doctor, nurse, front_desk, billing_clerk, student_doctor.
 * "admin" bypasses per-route checks on the frontend; here it's granted explicitly.
 */
@RestController
@RequestMapping("/api/v1/patients")
@PreAuthorize("hasAnyRole('admin','doctor','nurse','front_desk','billing_clerk','student_doctor')")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Page<PatientDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return patientService.list(search, pageable);
    }

    @GetMapping("/{id}")
    public PatientDto get(@PathVariable UUID id) {
        return patientService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin','front_desk','nurse')")
    public ResponseEntity<PatientDto> create(@Valid @RequestBody PatientCreateRequest request, Authentication authentication) {
        String registeredBy = authentication != null ? authentication.getName() : "Unknown";
        PatientDto created = patientService.create(request, registeredBy);
        return ResponseEntity.created(URI.create("/api/v1/patients/" + created.id())).body(created);
    }
}
