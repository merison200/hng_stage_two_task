package com.emma.HNG2.repository;

import com.emma.HNG2.model.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, String> {
    List<Organisation> findByUsersUserId(String userId);
}