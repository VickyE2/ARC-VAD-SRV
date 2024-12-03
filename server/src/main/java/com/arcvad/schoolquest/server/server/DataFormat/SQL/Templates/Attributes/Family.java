package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "families")
public class Family {

    @Column(name = "family_name", nullable = false, unique = true)
    private String familyName;

    @Enumerated(
        EnumType.STRING)
    @Column(name = "family_wealth")
    private Wealth familyWealth;

    @Column(name = "family_size")
    private int familySize;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> familyMembers = new ArrayList<>();

    @Id
    private FamilyNames id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER) // Added cascade type
    @JoinColumn(name = "family_registrar_id", nullable = true)
    private FamilyRegistrar familyRegistrar;

    public Family(FamilyNames family) {
        this.familyName = family.getFamilyName();
        this.id = family;
    }

    public Family() {}

    // Getters and Setters
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public FamilyNames getFamilyNames() {
        return id;
    }

    public void setFamilyNames(FamilyNames familyName) {
        this.id = familyName;
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
        this.familySize = id.getFamilySize();
    }

    public void setFamilySize(int size) {
        this.familySize = size;
    }

    public List<User> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<User> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public FamilyRegistrar getFamilyRegistrar() {
        return familyRegistrar;
    }

    public void setFamilyRegistrar(FamilyRegistrar familyRegistrar) {
        this.familyRegistrar = familyRegistrar;
    }
}
