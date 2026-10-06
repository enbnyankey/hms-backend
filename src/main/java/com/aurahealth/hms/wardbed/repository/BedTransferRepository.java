package com.aurahealth.hms.wardbed.repository;

import com.aurahealth.hms.wardbed.entity.BedTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BedTransferRepository extends JpaRepository<BedTransfer, UUID> {

    @Query("""
            SELECT t FROM BedTransfer t JOIN FETCH t.fromBed JOIN FETCH t.toBed
            WHERE t.admission.id = :admissionId
            ORDER BY t.transferredAt
            """)
    List<BedTransfer> findForAdmission(@Param("admissionId") UUID admissionId);
}
