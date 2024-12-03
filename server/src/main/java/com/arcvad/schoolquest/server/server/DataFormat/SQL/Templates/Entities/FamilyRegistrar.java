package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "family_registrars")
public class FamilyRegistrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "familyRegistrar", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Family> families = new ArrayList<>();


    // Getters and Setters
    public List<Family> getFamilies() {
        return families;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFamilies(List<Family> families) {
        this.families = families;
    }
}

