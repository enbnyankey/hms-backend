package com.aurahealth.hms.wardbed.repository;

import com.aurahealth.hms.wardbed.entity.Admission;
import com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdmissionRepository extends JpaRepository<Admission, UUID> {

    @Query("""
            SELECT a FROM Admission a
            JOIN FETCH a.patient JOIN FETCH a.bed b JOIN FETCH b.ward
            WHERE a.id = :id
            """)
    Optional<Admission> findDetailed(@Param("id") UUID id);

    /** Current (not discharged) admissions for the given beds, with patient loaded. */
    @Query("""
            SELECT a FROM Admission a
            JOIN FETCH a.patient JOIN FETCH a.bed
            WHERE a.bed.id IN :bedIds AND a.status <> com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus.DISCHARGED
            """)
    List<Admission> findCurrentForBeds(@Param("bedIds") Collection<UUID> bedIds);

    @Query("""
            SELECT a FROM Admission a
            JOIN FETCH a.patient JOIN FETCH a.bed b JOIN FETCH b.ward w
            WHERE a.status IN :statuses
              AND (:wardId IS NULL OR w.id = :wardId)
            ORDER BY w.code, b.code
            """)
    List<Admission> search(@Param("statuses") Collection<AdmissionStatus> statuses, @Param("wardId") UUID wardId);

    @Query("""
            SELECT a FROM Admission a JOIN FETCH a.bed
            WHERE a.patient.id = :patientId AND a.status <> com.aurahealth.hms.wardbed.entity.Admission.AdmissionStatus.DISCHARGED
            """)
    Optional<Admission> findCurrentForPatient(@Param("patientId") UUID patientId);

    long countByStatus(AdmissionStatus status);

    /** Row-locks the admission so two people can't discharge/transfer the same patient at once. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Admission a WHERE a.id = :id")
    Optional<Admission> findByIdForUpdate(@Param("id") UUID id);
}
