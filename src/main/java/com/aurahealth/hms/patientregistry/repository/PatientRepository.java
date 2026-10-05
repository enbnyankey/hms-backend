package com.aurahealth.hms.patientregistry.repository;

import com.aurahealth.hms.patientregistry.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByMrn(String mrn);

    @Query("""
            SELECT p FROM Patient p
            WHERE (:search IS NULL OR :search = '' OR
                   LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.mrn) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.phone) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Patient> search(@Param("search") String search, Pageable pageable);
}
