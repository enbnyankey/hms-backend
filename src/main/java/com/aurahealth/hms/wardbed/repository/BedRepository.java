package com.aurahealth.hms.wardbed.repository;

import com.aurahealth.hms.wardbed.entity.Bed;
import com.aurahealth.hms.wardbed.entity.Bed.BedStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedRepository extends JpaRepository<Bed, UUID> {

    /** Bed counts per ward and status, for the census KPIs and ward summaries. */
    interface WardStatusCount {
        UUID getWardId();
        BedStatus getStatus();
        long getTotal();
    }

    @Query("SELECT b.ward.id AS wardId, b.status AS status, COUNT(b) AS total FROM Bed b GROUP BY b.ward.id, b.status")
    List<WardStatusCount> countByWardAndStatus();

    @Query("""
            SELECT b FROM Bed b JOIN FETCH b.ward w
            WHERE (:wardId IS NULL OR w.id = :wardId)
              AND (:status IS NULL OR b.status = :status)
            ORDER BY w.code, b.code
            """)
    List<Bed> search(@Param("wardId") UUID wardId, @Param("status") BedStatus status);

    @Query("SELECT b FROM Bed b JOIN FETCH b.ward WHERE b.id = :id")
    Optional<Bed> findWithWard(@Param("id") UUID id);

    /**
     * Row-locks the bed until the transaction ends, so two people assigning
     * the same bed at the same moment can't both succeed.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bed b WHERE b.id = :id")
    Optional<Bed> findByIdForUpdate(@Param("id") UUID id);
}
