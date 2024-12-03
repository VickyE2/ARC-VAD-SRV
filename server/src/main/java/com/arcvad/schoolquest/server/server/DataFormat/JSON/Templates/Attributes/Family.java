package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.MinimalUser;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (familyMembers == null) {
            familyMembers = new ArrayList<>();
        }
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
    public void setFamilyName(String name) {
        this.familyName = name;
    }
    public void setFamilyWealth(Wealth familyWealth) {
        this.familyWealth = familyWealth;
    }
    public void setFamilySize() {
        this.familySize = family.getFamilySize();
    }
    public void setFamilySize(int size) {
        this.familySize = size;
    }
    public void setFamilyMembers(List<MinimalUser> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addFamilyMember(MinimalUser member) {
        if (familyMembers == null) {
            familyMembers = new ArrayList<>(); // Initialize family members list if null
        }

        // Prevent duplicate members
        boolean isMemberAlreadyPresent = familyMembers.stream()
            .anyMatch(existingMember -> existingMember.getUsername().equals(member.getUsername()));
        if (!isMemberAlreadyPresent) {
            familyMembers.add(member);
        }
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

    public void removeDuplicateFamilyMembers() {
        if (familyMembers == null) {
            return; // No members to process
        }

        // Use a HashSet to track unique usernames
        Set<String> seenUsernames = new HashSet<>();
        List<MinimalUser> uniqueMembers = new ArrayList<>();

        for (MinimalUser member : familyMembers) {
            if (member != null && seenUsernames.add(member.getUsername())) {
                uniqueMembers.add(member); // Add only if the username is unique
            }
        }

        // Replace the old list with the filtered list
        this.familyMembers = uniqueMembers;
    }
}
