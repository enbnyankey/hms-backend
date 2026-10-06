package com.aurahealth.hms.wardbed.controller;

import com.aurahealth.hms.wardbed.dto.WardDtos.*;
import com.aurahealth.hms.wardbed.entity.Bed.BedStatus;
import com.aurahealth.hms.wardbed.service.WardBedService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Ward board read API + bed status changes.
 * Role gate matches ROUTE_ACCESS["/ward-bed-manager"] in hms/lib/auth.tsx: admin, doctor, nurse.
 */
@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('admin','doctor','nurse')")
public class WardBedController {

    private final WardBedService wardBedService;

    public WardBedController(WardBedService wardBedService) {
        this.wardBedService = wardBedService;
    }

    /** All wards with bed counts per status. */
    @GetMapping("/wards")
    public List<WardSummaryDto> wards() {
        return wardBedService.listWards();
    }

    /** Hospital-wide KPIs: capacity, occupancy %, available, reserved, cleaning, discharge-ready, queue size. */
    @GetMapping("/wards/census")
    public CensusDto census() {
        return wardBedService.census();
    }

    @GetMapping("/wards/{wardId}")
    public WardSummaryDto ward(@PathVariable UUID wardId) {
        return wardBedService.getWard(wardId);
    }

    @GetMapping("/wards/{wardId}/beds")
    public List<BedDto> wardBeds(@PathVariable UUID wardId, @RequestParam(required = false) BedStatus status) {
        return wardBedService.listBeds(wardId, status);
    }

    /** e.g. GET /api/v1/beds?status=AVAILABLE for a transfer / assign-bed picker. */
    @GetMapping("/beds")
    public List<BedDto> beds(@RequestParam(required = false) UUID wardId,
                             @RequestParam(required = false) BedStatus status) {
        return wardBedService.listBeds(wardId, status);
    }

    @GetMapping("/beds/{bedId}")
    public BedDto bed(@PathVariable UUID bedId) {
        return wardBedService.getBed(bedId);
    }

    /** Mark clean, reserve, release, take out of service. Nurses and admin only. */
    @PatchMapping("/beds/{bedId}/status")
    @PreAuthorize("hasAnyRole('admin','nurse')")
    public BedDto updateStatus(@PathVariable UUID bedId, @Valid @RequestBody BedStatusUpdateRequest request) {
        return wardBedService.updateBedStatus(bedId, request);
    }
}
