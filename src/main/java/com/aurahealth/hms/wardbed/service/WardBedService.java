package com.aurahealth.hms.wardbed.service;

import com.aurahealth.hms.exception.ConflictException;
import com.aurahealth.hms.exception.NotFoundException;
import com.aurahealth.hms.wardbed.dto.WardBedMapper;
import com.aurahealth.hms.wardbed.dto.WardDtos.*;
import com.aurahealth.hms.wardbed.entity.Admission;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.RequestStatus;
import com.aurahealth.hms.wardbed.entity.Bed;
import com.aurahealth.hms.wardbed.entity.Bed.BedStatus;
import com.aurahealth.hms.wardbed.entity.Ward;
import com.aurahealth.hms.wardbed.repository.AdmissionRepository;
import com.aurahealth.hms.wardbed.repository.AdmissionRequestRepository;
import com.aurahealth.hms.wardbed.repository.BedRepository;
import com.aurahealth.hms.wardbed.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Read side of the ward board (wards, beds, census) plus manual bed status changes. */
@Service
@Transactional
public class WardBedService {

    /**
     * Which manual status changes are allowed. OCCUPIED is never set by hand:
     * only admit/transfer put a patient in a bed, and only discharge/transfer
     * take them out, so the board can't drift from the admissions table.
     */
    private static final Map<BedStatus, Set<BedStatus>> MANUAL_TRANSITIONS = Map.of(
            BedStatus.AVAILABLE, EnumSet.of(BedStatus.RESERVED, BedStatus.CLEANING, BedStatus.MAINTENANCE),
            BedStatus.RESERVED, EnumSet.of(BedStatus.AVAILABLE, BedStatus.CLEANING, BedStatus.MAINTENANCE),
            BedStatus.CLEANING, EnumSet.of(BedStatus.AVAILABLE, BedStatus.MAINTENANCE),
            BedStatus.MAINTENANCE, EnumSet.of(BedStatus.AVAILABLE, BedStatus.CLEANING),
            BedStatus.OCCUPIED, EnumSet.noneOf(BedStatus.class)
    );

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final AdmissionRequestRepository requestRepository;
    private final WardBedMapper mapper;

    public WardBedService(WardRepository wardRepository, BedRepository bedRepository,
                          AdmissionRepository admissionRepository, AdmissionRequestRepository requestRepository,
                          WardBedMapper mapper) {
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
        this.admissionRepository = admissionRepository;
        this.requestRepository = requestRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<WardSummaryDto> listWards() {
        Map<UUID, Map<BedStatus, Long>> counts = countsByWard();
        return wardRepository.findByActiveTrueOrderByCodeAsc().stream()
                .map(w -> toSummary(w, counts.getOrDefault(w.getId(), Map.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public WardSummaryDto getWard(UUID wardId) {
        Ward ward = findWard(wardId);
        return toSummary(ward, countsByWard().getOrDefault(ward.getId(), Map.of()));
    }

    @Transactional(readOnly = true)
    public CensusDto census() {
        Map<BedStatus, Long> all = new EnumMap<>(BedStatus.class);
        countsByWard().values().forEach(m -> m.forEach((s, n) -> all.merge(s, n, Long::sum)));
        return new CensusDto(
                toCounts(all),
                wardRepository.findByActiveTrueOrderByCodeAsc().size(),
                admissionRepository.countByStatus(Admission.AdmissionStatus.DISCHARGE_READY),
                requestRepository.countByStatus(RequestStatus.PENDING)
        );
    }

    /** Beds (optionally one ward / one status), each with its current occupant if any. */
    @Transactional(readOnly = true)
    public List<BedDto> listBeds(UUID wardId, BedStatus status) {
        if (wardId != null) findWard(wardId);
        List<Bed> beds = bedRepository.search(wardId, status);
        return withOccupants(beds);
    }

    @Transactional(readOnly = true)
    public BedDto getBed(UUID bedId) {
        Bed bed = bedRepository.findWithWard(bedId)
                .orElseThrow(() -> new NotFoundException("Bed " + bedId + " not found"));
        return withOccupants(List.of(bed)).get(0);
    }

    public BedDto updateBedStatus(UUID bedId, BedStatusUpdateRequest request) {
        Bed bed = bedRepository.findByIdForUpdate(bedId)
                .orElseThrow(() -> new NotFoundException("Bed " + bedId + " not found"));
        BedStatus from = bed.getStatus();
        BedStatus to = request.status();

        if (to == BedStatus.OCCUPIED) {
            throw new ConflictException("A bed becomes OCCUPIED by admitting a patient (POST /api/v1/admissions), not by a status change.");
        }
        if (from == BedStatus.OCCUPIED) {
            throw new ConflictException("Bed " + bed.getCode() + " is occupied. Discharge or transfer the patient first.");
        }
        if (from == to) {
            bed.changeStatus(to, request.note());
        } else if (!MANUAL_TRANSITIONS.get(from).contains(to)) {
            throw new ConflictException("Bed " + bed.getCode() + " cannot go from " + from + " to " + to + ".");
        } else {
            bed.changeStatus(to, request.note());
        }
        return getBed(bed.getId());
    }

    // ---- helpers --------------------------------------------------------

    private List<BedDto> withOccupants(List<Bed> beds) {
        if (beds.isEmpty()) return List.of();
        Set<UUID> occupiedIds = beds.stream()
                .filter(b -> b.getStatus() == BedStatus.OCCUPIED)
                .map(Bed::getId)
                .collect(Collectors.toSet());
        Map<UUID, Admission> byBed = occupiedIds.isEmpty() ? Map.of()
                : admissionRepository.findCurrentForBeds(occupiedIds).stream()
                .collect(Collectors.toMap(a -> a.getBed().getId(), Function.identity()));
        return beds.stream().map(b -> mapper.toBedDto(b, byBed.get(b.getId()))).toList();
    }

    private Ward findWard(UUID wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new NotFoundException("Ward " + wardId + " not found"));
    }

    private Map<UUID, Map<BedStatus, Long>> countsByWard() {
        Map<UUID, Map<BedStatus, Long>> result = new HashMap<>();
        for (BedRepository.WardStatusCount c : bedRepository.countByWardAndStatus()) {
            result.computeIfAbsent(c.getWardId(), k -> new EnumMap<>(BedStatus.class)).put(c.getStatus(), c.getTotal());
        }
        return result;
    }

    private WardSummaryDto toSummary(Ward w, Map<BedStatus, Long> counts) {
        return new WardSummaryDto(w.getId(), w.getCode(), w.getName(), w.getShortName(),
                w.getLocation(), w.getChargeNurse(), toCounts(counts));
    }

    private BedCounts toCounts(Map<BedStatus, Long> c) {
        long occupied = c.getOrDefault(BedStatus.OCCUPIED, 0L);
        long total = c.values().stream().mapToLong(Long::longValue).sum();
        double pct = total == 0 ? 0 : Math.round(occupied * 1000.0 / total) / 10.0;
        return new BedCounts(total, occupied,
                c.getOrDefault(BedStatus.AVAILABLE, 0L),
                c.getOrDefault(BedStatus.RESERVED, 0L),
                c.getOrDefault(BedStatus.CLEANING, 0L),
                c.getOrDefault(BedStatus.MAINTENANCE, 0L),
                pct);
    }
}
