package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.MinimalUser;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class Family extends BaseTemplate implements Mergeable<Family> {
    @JsonProperty("familyName")
    private String familyName;
    @JsonProperty("familyWealth")
    private Wealth familyWealth;
    @JsonProperty("familySize")
    private int familySize;
    @JsonIgnore
    private FamilyNames family;

    @JsonProperty("familyMember")
    private List<MinimalUser> familyMembers;

    @JsonIgnore
    private FamilyNames familyNames;

    // Constructor, getters, and setters
    public Family(FamilyNames family) {
        this.familyName = family.getFamilyName();
        this.family = family;
        this.familyNames = family;
    }

    public Family() {}
    @JsonIgnore
    public String getFamilyName() {
        return familyName;
    }

    @JsonIgnore
    public List<MinimalUser> getFamilyMembers() {
        return familyMembers;
    }

    @JsonIgnore
    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    @JsonIgnore
    public int getFamilySize() {
        return familySize;
    }

    @JsonIgnore
    public FamilyNames getFamilyNames(){
        return familyNames;
    }
    public void setFamilyName() {
        this.familyName = family.getFamilyName();
    }
    public void setFamilyWealth(Wealth familyWealth) {
        this.familyWealth = familyWealth;
    }
    public void setFamilySize() {
        this.familySize = family.getFamilySize();
    }
    public void setFamilyMembers(List<MinimalUser> familyMembers) {
        this.familyMembers = familyMembers;
    }

    @Override
    public void mergeWith(Family other) {
        if (other == null) {
            logger.severe("ARC-MERGE", "Cannot merge with a null Family");
            throw new IllegalArgumentException("Cannot merge with a null Family");
        }
        if (other.familyName.equals(this.familyName)) {
            if (other.getFamilyMembers() != null && other.getFamilyMembers() != this.getFamilyMembers()) {
                this.familyMembers = other.familyMembers;
            }
        }
    }
}
