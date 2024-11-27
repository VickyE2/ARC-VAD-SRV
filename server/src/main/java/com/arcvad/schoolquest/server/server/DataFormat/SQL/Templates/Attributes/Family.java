package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_name", nullable = false, unique = true)
    private String familyName;

    @Enumerated(
        EnumType.STRING)
    @Column(name = "family_wealth")
    private Wealth familyWealth;

    @Column(name = "family_size")
    private int familySize;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Object> familyMembers;

    private FamilyNames familyNames;

    private FamilyNames family;

    public Family(FamilyNames family) {
        this.familyName = family.getFamilyName();
        this.family = family;
        this.familyNames = family;
    }

    // Getters and Setters
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public FamilyNames getFamilyNames() {
        return familyNames;
    }

    public void setFamilyNames(FamilyNames familyName) {
        this.familyNames = familyName;
    }

    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    public void setFamilyWealth(Wealth familyWealth) {
        this.familyWealth = familyWealth;
    }

    public int getFamilySize() {
        return familySize;
    }

    public void setFamilySize() {
        this.familySize = family.getFamilySize();
    }

    public List<Object> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<Object> familyMembers) {
        this.familyMembers = familyMembers;
    }
}
