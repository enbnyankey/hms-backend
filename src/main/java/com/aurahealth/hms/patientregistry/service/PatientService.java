package com.aurahealth.hms.patientregistry.service;

import com.aurahealth.hms.exception.NotFoundException;
import com.aurahealth.hms.patientregistry.dto.PatientCreateRequest;
import com.aurahealth.hms.patientregistry.dto.PatientDto;
import com.aurahealth.hms.patientregistry.dto.PatientMapper;
import com.aurahealth.hms.patientregistry.entity.Patient;
import com.aurahealth.hms.patientregistry.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper mapper;
    private final AtomicLong mrnSequence = new AtomicLong(9000);

    public PatientService(PatientRepository patientRepository, PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<PatientDto> list(String search, Pageable pageable) {
        return patientRepository.search(search, pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public PatientDto get(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient " + id + " not found"));
        return mapper.toDto(patient);
    }

    public PatientDto create(PatientCreateRequest request, String registeredBy) {
        Patient patient = new Patient();
        patient.setMrn(nextMrn());
        patient.setName(request.name());
        patient.setDob(mapper.parseDate(request.dob()));
        patient.setAge(parseAge(request.age(), patient.getDob()));
        patient.setSex(Patient.Sex.valueOf(request.sex()));
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        patient.setNextOfKinName(request.nextOfKinName());
        patient.setNextOfKinPhone(request.nextOfKinPhone());
        patient.setNhisNumber(request.nhisNumber());
        patient.setNhisStatus(request.nhisNumber() == null || request.nhisNumber().isBlank()
                ? Patient.NhisStatus.Not_Enrolled
                : Patient.NhisStatus.Pending);
        patient.setBloodGroup(request.bloodGroup());
        patient.setAllergies(request.allergies() == null || request.allergies().isBlank() ? "None recorded" : request.allergies());
        patient.setCategory(Patient.PatientCategory.valueOf(request.category().replace("-", "_")));
        patient.setLastVisit(LocalDate.now());
        patient.setRegisteredBy(registeredBy);
        patient.setRegisteredOn(LocalDate.now());
        patient.setStatus(Patient.PatientRecordStatus.Active);
        patient.setDuplicateFlag(patientRepository.findByMrn(patient.getMrn()).isPresent());

        Patient saved = patientRepository.save(patient);
        return mapper.toDto(saved);
    }

    private String nextMrn() {
        return "GH-" + Year.now().getValue() + "-" + mrnSequence.incrementAndGet();
    }

    private int parseAge(String rawAge, LocalDate dob) {
        if (rawAge != null && !rawAge.isBlank()) {
            try {
                return Integer.parseInt(rawAge.trim());
            } catch (NumberFormatException ignored) {
                // fall through to DOB-derived age
            }
        }
        if (dob != null) {
            return Year.now().getValue() - dob.getYear();
        }
        return 0;
    }
}
