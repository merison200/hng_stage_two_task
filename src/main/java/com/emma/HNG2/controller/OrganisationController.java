package com.emma.HNG2.controller;

import com.emma.HNG2.model.Organisation;
import com.emma.HNG2.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    @Autowired
    private OrganisationService organisationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserOrganisations(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<Organisation> organisations = organisationService.getOrganisationsForUser(userId);

            if (organisations.isEmpty()) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("status", "success");
                response.put("message", "No organisations found for the user");
                return ResponseEntity.ok(response);
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("message", "Organisations retrieved successfully");
            response.put("data", organisations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve organisations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<?> getOrganisation(@PathVariable String orgId) {
        return organisationService.getOrganisationById(orgId)
                .map(org -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("status", "success");
                    response.put("message", "Organisation retrieved successfully");
                    response.put("data", org);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?> createOrganisation(@RequestBody Organisation organisation, Authentication authentication) {
        String userId = authentication.getName();
        Organisation createdOrg = organisationService.createOrganisation(organisation, userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Organisation created successfully");
        response.put("data", createdOrg);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{orgId}/users")
    public ResponseEntity<?> addUserToOrganisation(@PathVariable String orgId, @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        organisationService.addUserToOrganisation(orgId, userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "User added to organisation successfully");

        return ResponseEntity.ok(response);
    }
}