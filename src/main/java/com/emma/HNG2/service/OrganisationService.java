package com.emma.HNG2.service;

import com.emma.HNG2.model.Organisation;
import com.emma.HNG2.model.User;
import com.emma.HNG2.repository.OrganisationRepository;
import com.emma.HNG2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Organisation createOrganisation(Organisation organisation, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        organisation.getUsers().add(user);
        user.getOrganisations().add(organisation);
        return organisationRepository.save(organisation);
    }

    public List<Organisation> getOrganisationsForUser(String userId) {
        // Add debug log to verify userId
        System.out.println("Fetching organisations for userId: " + userId);

        return organisationRepository.findByUsersUserId(userId);
    }

    public Optional<Organisation> getOrganisationById(String orgId) {
        return organisationRepository.findById(orgId);
    }

    public void addUserToOrganisation(String orgId, String userId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        org.getUsers().add(user);
        user.getOrganisations().add(org);
        organisationRepository.save(org);
        userRepository.save(user);
    }
}