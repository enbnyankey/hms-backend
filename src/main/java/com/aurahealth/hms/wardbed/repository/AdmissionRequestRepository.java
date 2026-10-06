package com.aurahealth.hms.wardbed.repository;

import com.aurahealth.hms.wardbed.entity.AdmissionRequest;
import com.aurahealth.hms.wardbed.entity.AdmissionRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdmissionRequestRepository extends JpaRepository<AdmissionRequest, UUID> {

    /** Oldest first; the service then sorts by urgency (enum order, not alphabetical). */
    @Query("""
            SELECT r FROM AdmissionRequest r
            JOIN FETCH r.patient
            LEFT JOIN FETCH r.preferredWard
            LEFT JOIN FETCH r.suggestedBed
            WHERE r.status = :status
            ORDER BY r.requestedAt
            """)
    List<AdmissionRequest> findQueue(@Param("status") RequestStatus status);

    @Query("""
            SELECT r FROM AdmissionRequest r
            JOIN FETCH r.patient
            LEFT JOIN FETCH r.preferredWard
            LEFT JOIN FETCH r.suggestedBed
            WHERE r.id = :id
            """)
    Optional<AdmissionRequest> findDetailed(@Param("id") UUID id);

    boolean existsByPatientIdAndStatus(UUID patientId, RequestStatus status);

    long countByStatus(RequestStatus status);
}
