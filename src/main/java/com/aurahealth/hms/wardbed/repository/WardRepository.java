package com.aurahealth.hms.wardbed.repository;

import com.aurahealth.hms.wardbed.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WardRepository extends JpaRepository<Ward, UUID> {

    List<Ward> findByActiveTrueOrderByCodeAsc();
}
