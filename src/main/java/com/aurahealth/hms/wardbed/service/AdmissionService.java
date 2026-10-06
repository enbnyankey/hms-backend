package com.aurahealth.hms.wardbed.service;

import com.aurahealth.hms.exception.ConflictException;
import com.aurahealth.hms.exception.NotFoundException;
import com.aurahealth.hms.patientregistry.entity.Patient;
import com.aurahealth.hms.patientregistry.repository.PatientRepository;
import com.aurahealth.hms.wardbed.dto.WardBedMapper;
import com.aurahealth.hms.wardbed.dto.WardDtos.*;
import com.aurahealth.hms.wardbed.entity.*;
import com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.RequestStatus;
import com.aurahealth.hms.wardbed.entity.Bed.BedStatus;
import com.aurahealth.hms.wardbed.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Admit / discharge-ready / discharge / transfer, and the admissions queue.
 *
 * Every operation that touches a bed row-locks it first (findByIdForUpdate),
 * so two staff assigning the same bed at once can't both succeed; the partial
 * unique indexes in V5 are a second guard at the database level.
 */
@Service
@Transactional
public class AdmissionService {

    private static final Set<BedStatus> ADMITTABLE = EnumSet.of(BedStatus.AVAILABLE, BedStatus.RESERVED);

    private final AdmissionRepository admissionRepository;
    private final BedRepository bedRepository;
    private final WardRepository wardRepository;
    private final BedTransferRepository transferRepository;
    private final AdmissionRequestRepository requestRepository;
    private final PatientRepository patientRepository;
    private final WardBedMapper mapper;

    public AdmissionService(AdmissionRepository admissionRepository, BedRepository bedRepository,
                            WardRepository wardRepository, BedTransferRepository transferRepository,
                            AdmissionRequestRepository requestRepository, PatientRepository patientRepository,
                            WardBedMapper mapper) {
        this.admissionRepository = admissionRepository;
        this.bedRepository = bedRepository;
        this.wardRepository = wardRepository;
        this.transferRepository = transferRepository;
        this.requestRepository = requestRepository;
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    // ---- admissions -----------------------------------------------------

    /** Default (no status filter): everyone currently in a bed, including discharge-ready. */
    @Transactional(readOnly = true)
    public List<AdmissionDto> list(AdmissionStatus status, UUID wardId) {
        Collection<AdmissionStatus> statuses = status != null
                ? List.of(status)
                : List.of(AdmissionStatus.ACTIVE, AdmissionStatus.DISCHARGE_READY);
        return admissionRepository.search(statuses, wardId).stream().map(mapper::toAdmissionDto).toList();
    }

    @Transactional(readOnly = true)
    public AdmissionDto get(UUID id) {
        return mapper.toAdmissionDto(findDetailed(id));
    }

    public AdmissionDto admit(AdmitRequest request, String user) {
        Bed bed = lockBed(request.bedId());
        if (!ADMITTABLE.contains(bed.getStatus())) {
            throw new ConflictException("Bed " + bed.getCode() + " is " + bed.getStatus() + " and can't take an admission.");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient " + request.patientId() + " not found"));
        admissionRepository.findCurrentForPatient(patient.getId()).ifPresent(a -> {
            throw new ConflictException(patient.getName() + " is already admitted in bed " + a.getBed().getCode() + ".");
        });

        AdmissionRequest queued = null;
        if (request.admissionRequestId() != null) {
            queued = requestRepository.findById(request.admissionRequestId())
                    .orElseThrow(() -> new NotFoundException("Admission request " + request.admissionRequestId() + " not found"));
            if (queued.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Admission request is already " + queued.getStatus() + ".");
            }
            if (!queued.getPatient().getId().equals(patient.getId())) {
                throw new IllegalArgumentException("Admission request belongs to a different patient.");
            }
        }

        Admission admission = new Admission();
        admission.setPatient(patient);
        admission.setBed(bed);
        admission.setStatus(AdmissionStatus.ACTIVE);
        admission.setDiagnosis(request.diagnosis().trim());
        admission.setAttending(blankToNull(request.attending()));
        admission.setNotes(blankToNull(request.notes()));
        admission.setAdmittedAt(LocalDateTime.now());
        admission.setAdmittedBy(user);
        admissionRepository.saveAndFlush(admission);

        bed.changeStatus(BedStatus.OCCUPIED, null);
        patient.setCategory(Patient.PatientCategory.Inpatient);
        patient.setLastVisit(LocalDate.now());

        if (queued != null) {
            queued.setStatus(RequestStatus.ADMITTED);
            queued.setAdmission(admission);
            queued.setResolvedAt(LocalDateTime.now());
        }
        return mapper.toAdmissionDto(admission);
    }

    public AdmissionDto markDischargeReady(UUID id) {
        Admission admission = lockAdmission(id);
        if (admission.getStatus() != AdmissionStatus.ACTIVE) {
            throw new ConflictException("Only an ACTIVE admission can be marked discharge-ready (this one is " + admission.getStatus() + ").");
        }
        admission.setStatus(AdmissionStatus.DISCHARGE_READY);
        return mapper.toAdmissionDto(admission);
    }

    /** Discharges the patient; the bed goes to CLEANING until housekeeping marks it AVAILABLE. */
    public AdmissionDto discharge(UUID id, DischargeRequest request, String user) {
        Admission admission = lockAdmission(id);
        if (!admission.isCurrent()) {
            throw new ConflictException("Admission is already discharged.");
        }
        Bed bed = lockBed(admission.getBed().getId());

        admission.setStatus(AdmissionStatus.DISCHARGED);
        admission.setDischargedAt(LocalDateTime.now());
        admission.setDischargedBy(user);
        admission.setDischargeNotes(request == null ? null : blankToNull(request.notes()));

        bed.changeStatus(BedStatus.CLEANING, "Terminal clean after discharge of " + admission.getPatient().getName());
        admission.getPatient().setCategory(Patient.PatientCategory.Outpatient);
        return mapper.toAdmissionDto(admission);
    }

    /** Moves the patient to another bed; the old bed goes to CLEANING. */
    public AdmissionDto transfer(UUID id, TransferRequest request, String user) {
        Admission admission = lockAdmission(id);
        if (!admission.isCurrent()) {
            throw new ConflictException("A discharged admission can't be transferred.");
        }
        UUID fromId = admission.getBed().getId();
        UUID toId = request.toBedId();
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("The patient is already in that bed.");
        }

        // Lock both beds in a fixed order so two opposite transfers can't deadlock.
        Bed from, to;
        if (fromId.compareTo(toId) < 0) {
            from = lockBed(fromId);
            to = lockBed(toId);
        } else {
            to = lockBed(toId);
            from = lockBed(fromId);
        }
        if (!ADMITTABLE.contains(to.getStatus())) {
            throw new ConflictException("Bed " + to.getCode() + " is " + to.getStatus() + " and can't take a transfer.");
        }

        BedTransfer transfer = new BedTransfer();
        transfer.setAdmission(admission);
        transfer.setFromBed(from);
        transfer.setToBed(to);
        transfer.setReason(blankToNull(request.reason()));
        transfer.setTransferredBy(user);
        transferRepository.save(transfer);

        admission.setBed(to);
        from.changeStatus(BedStatus.CLEANING, "Vacated by transfer to " + to.getCode());
        to.changeStatus(BedStatus.OCCUPIED, null);
        admissionRepository.flush();
        return mapper.toAdmissionDto(findDetailed(id));
    }

    @Transactional(readOnly = true)
    public List<TransferDto> transfers(UUID admissionId) {
        findDetailed(admissionId);
        return transferRepository.findForAdmission(admissionId).stream().map(mapper::toTransferDto).toList();
    }

    // ---- admissions queue -----------------------------------------------

    /** Most urgent first (CRITICAL, URGENT, ROUTINE), then longest waiting. */
    @Transactional(readOnly = true)
    public List<AdmissionRequestDto> queue(RequestStatus status) {
        List<AdmissionRequest> rows = new ArrayList<>(requestRepository.findQueue(status == null ? RequestStatus.PENDING : status));
        rows.sort(Comparator.comparing(AdmissionRequest::getUrgency).thenComparing(AdmissionRequest::getRequestedAt));
        return rows.stream().map(mapper::toRequestDto).toList();
    }

    public AdmissionRequestDto createRequest(CreateAdmissionRequest request, String user) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient " + request.patientId() + " not found"));
        admissionRepository.findCurrentForPatient(patient.getId()).ifPresent(a -> {
            throw new ConflictException(patient.getName() + " is already admitted in bed " + a.getBed().getCode() + ".");
        });
        if (requestRepository.existsByPatientIdAndStatus(patient.getId(), RequestStatus.PENDING)) {
            throw new ConflictException(patient.getName() + " is already waiting in the admissions queue.");
        }

        AdmissionRequest r = new AdmissionRequest();
        r.setPatient(patient);
        r.setSource(request.source().trim());
        r.setUrgency(request.urgency());
        r.setDiagnosis(request.diagnosis().trim());
        r.setNotes(blankToNull(request.notes()));
        if (request.preferredWardId() != null) {
            r.setPreferredWard(wardRepository.findById(request.preferredWardId())
                    .orElseThrow(() -> new NotFoundException("Ward " + request.preferredWardId() + " not found")));
        }
        if (request.suggestedBedId() != null) {
            r.setSuggestedBed(bedRepository.findById(request.suggestedBedId())
                    .orElseThrow(() -> new NotFoundException("Bed " + request.suggestedBedId() + " not found")));
        }
        r.setStatus(RequestStatus.PENDING);
        r.setRequestedBy(user);
        requestRepository.saveAndFlush(r);
        return mapper.toRequestDto(r);
    }

    public AdmissionRequestDto cancelRequest(UUID id) {
        AdmissionRequest r = requestRepository.findDetailed(id)
                .orElseThrow(() -> new NotFoundException("Admission request " + id + " not found"));
        if (r.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Admission request is already " + r.getStatus() + ".");
        }
        r.setStatus(RequestStatus.CANCELLED);
        r.setResolvedAt(LocalDateTime.now());
        return mapper.toRequestDto(r);
    }

    // ---- helpers --------------------------------------------------------

    private Admission findDetailed(UUID id) {
        return admissionRepository.findDetailed(id)
                .orElseThrow(() -> new NotFoundException("Admission " + id + " not found"));
    }

    /** Locks the admission row, then loads it with patient, bed and ward. */
    private Admission lockAdmission(UUID id) {
        admissionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Admission " + id + " not found"));
        return findDetailed(id);
    }

    private Bed lockBed(UUID bedId) {
        return bedRepository.findByIdForUpdate(bedId)
                .orElseThrow(() -> new NotFoundException("Bed " + bedId + " not found"));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
