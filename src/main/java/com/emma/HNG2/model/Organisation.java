package com.emma.HNG2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "organisations")
@JsonIgnoreProperties({"users"})
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orgId;

    @NotBlank(message = "Organisation name is required")
    private String name;

    private String description;

    @ManyToMany(mappedBy = "organisations", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organisation)) return false;
        Organisation that = (Organisation) o;
        return Objects.equals(orgId, that.orgId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId, name);
    }
}