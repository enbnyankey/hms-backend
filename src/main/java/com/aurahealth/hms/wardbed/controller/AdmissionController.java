package com.aurahealth.hms.wardbed.controller;

import com.aurahealth.hms.wardbed.dto.WardDtos.*;
import com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.RequestStatus;
import com.aurahealth.hms.wardbed.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Admit / transfer / discharge and the admissions queue.
 * Role gate matches ROUTE_ACCESS["/ward-bed-manager"] in hms/lib/auth.tsx: admin, doctor, nurse.
 */
@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('admin','doctor','nurse')")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    /** Default: everyone currently in a bed (ACTIVE + DISCHARGE_READY). */
    @GetMapping("/admissions")
    public List<AdmissionDto> list(@RequestParam(required = false) AdmissionStatus status,
                                   @RequestParam(required = false) UUID wardId) {
        return admissionService.list(status, wardId);
    }

    @GetMapping("/admissions/{id}")
    public AdmissionDto get(@PathVariable UUID id) {
        return admissionService.get(id);
    }

    @PostMapping("/admissions")
    public ResponseEntity<AdmissionDto> admit(@Valid @RequestBody AdmitRequest request, Authentication auth) {
        AdmissionDto created = admissionService.admit(request, user(auth));
        return ResponseEntity.created(URI.create("/api/v1/admissions/" + created.id())).body(created);
    }

    @PostMapping("/admissions/{id}/discharge-ready")
    public AdmissionDto dischargeReady(@PathVariable UUID id) {
        return admissionService.markDischargeReady(id);
    }

    @PostMapping("/admissions/{id}/discharge")
    public AdmissionDto discharge(@PathVariable UUID id,
                                  @Valid @RequestBody(required = false) DischargeRequest request,
                                  Authentication auth) {
        return admissionService.discharge(id, request, user(auth));
    }

    @PostMapping("/admissions/{id}/transfer")
    public AdmissionDto transfer(@PathVariable UUID id, @Valid @RequestBody TransferRequest request, Authentication auth) {
        return admissionService.transfer(id, request, user(auth));
    }

    @GetMapping("/admissions/{id}/transfers")
    public List<TransferDto> transfers(@PathVariable UUID id) {
        return admissionService.transfers(id);
    }

    /** The "awaiting a bed" queue, most urgent first. Default status: PENDING. */
    @GetMapping("/admission-requests")
    public List<AdmissionRequestDto> queue(@RequestParam(required = false) RequestStatus status) {
        return admissionService.queue(status);
    }

    @PostMapping("/admission-requests")
    public ResponseEntity<AdmissionRequestDto> createRequest(@Valid @RequestBody CreateAdmissionRequest request,
                                                             Authentication auth) {
        AdmissionRequestDto created = admissionService.createRequest(request, user(auth));
        return ResponseEntity.created(URI.create("/api/v1/admission-requests/" + created.id())).body(created);
    }

    @PostMapping("/admission-requests/{id}/cancel")
    public AdmissionRequestDto cancelRequest(@PathVariable UUID id) {
        return admissionService.cancelRequest(id);
    }

    private static String user(Authentication auth) {
        return auth != null ? auth.getName() : "Unknown";
    }
}
