package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import jakarta.persistence.*;

@Entity
@Table(name = "player_families")
public class PlayerFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    @Embedded
    private Wealth familyWealth;

    @Column(name = "family_position")
    private int familyPosition;

    // Default constructor
    public PlayerFamily() {}

    // Constructor with Family argument
    public PlayerFamily(Family family) {
        this.familyName = family.getFamilyName();
        this.familyWealth = family.getFamilyWealth();
    }

    // Getters and setters
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    public void setFamilyWealth(Wealth familyWealth) {
        this.familyWealth = familyWealth;
    }

    public int getFamilyPosition() {
        return familyPosition;
    }

    public void setFamilyPosition(int familyPosition) {
        this.familyPosition = familyPosition;
    }
}

