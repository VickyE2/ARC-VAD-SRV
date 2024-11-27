package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "available_styles")
public class AvailableStyles{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "available_styles_id")
    private List<HairStyle> availableHairStyles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "available_styles_id")
    private List<EyelashStyle> availableEyelashStyles;

    // Getters and Setters
    public List<HairStyle> getAvailableHairStyles() {
        return availableHairStyles;
    }

    public void setAvailableHairStyles(List<HairStyle> availableHairStyles) {
        this.availableHairStyles = availableHairStyles;
    }

    public List<EyelashStyle> getAvailableEyelashStyles() {
        return availableEyelashStyles;
    }

    public void setAvailableEyelashStyles(List<EyelashStyle> availableEyelashStyles) {
        this.availableEyelashStyles = availableEyelashStyles;
    }
}

