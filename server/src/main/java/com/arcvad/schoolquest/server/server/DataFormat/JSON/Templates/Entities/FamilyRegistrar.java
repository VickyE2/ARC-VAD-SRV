package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class FamilyRegistrar extends BaseTemplate implements Mergeable<FamilyRegistrar> {

    @JsonProperty("family")
    private List<Family> families;

    @JsonIgnore
    public List<Family> getFamilies(){
        return this.families;
    }

    public void setFamilies(List<Family> families) {
        this.families = families;
    }


    @Override
    public void mergeWith(FamilyRegistrar other) {
        if (other == null) {
            logger.severe("ARC-MERGE", "Cannot merge with a null registrar");
            throw new IllegalArgumentException("Cannot merge with a null registrar");
        }

        if (other.families == null || other.families.isEmpty()) {
            return; // Nothing to merge
        }

        if (this.families == null) {
            other.removeDuplicateFamilies();
            this.families = new ArrayList<>(other.families);
            return;
        }

        Map<String, String> memberToFamilyMap = new HashMap<>();

        for (Family family : this.families) {
            if (family.getFamilyMembers() != null) {
                for (MinimalUser member : family.getFamilyMembers()) {
                    memberToFamilyMap.put(member.getUsername(), family.getFamilyName());
                }
            }
            family.removeDuplicateFamilyMembers();
        }

        for (Family otherFamily : other.families) {
            if (otherFamily.getFamilyMembers() != null) {
                for (MinimalUser member : otherFamily.getFamilyMembers()) {
                    String existingFamilyName = memberToFamilyMap.get(member.getUsername());

                    if (existingFamilyName != null) {
                        Family originalFamily = getFamilyByName(existingFamilyName);
                        if (originalFamily != null && !originalFamily.getFamilyMembers().contains(member)) {
                            originalFamily.getFamilyMembers().add(member);
                        }
                    } else {
                        Family thisFamily = getFamilyByName(otherFamily.getFamilyName());
                        if (thisFamily == null) {
                            thisFamily = new Family(FamilyNames.valueOf(otherFamily.getFamilyName().toUpperCase()));
                            thisFamily.setFamilyWealth(otherFamily.getFamilyWealth() != null ? otherFamily.getFamilyWealth() : Wealth.getRandomWealthByWeight());
                            thisFamily.setFamilySize(otherFamily.getFamilySize() != 0 ? otherFamily.getFamilySize() : thisFamily.getFamilyNames().getFamilySize());
                            this.families.add(thisFamily);
                        }
                        thisFamily.getFamilyMembers().add(member);
                        thisFamily.setFamilyWealth(
                            thisFamily.getFamilyWealth()
                        );
                        thisFamily.setFamilySize(thisFamily.getFamilySize());
                        memberToFamilyMap.put(member.getUsername(), thisFamily.getFamilyName());
                    }
                }
            }
            otherFamily.removeDuplicateFamilyMembers();
        }

        for (Family family : this.families) {
            family.removeDuplicateFamilyMembers();
        }

        removeDuplicateFamilies();
    }

    private Family getFamilyByName(String familyName) {
        for (Family family : this.families) {
            if (family.getFamilyName().equals(familyName)) {
                return family;
            }
        }
        return null;
    }
    public void removeDuplicateFamilies() {
        Map<String, Family> uniqueFamilies = new HashMap<>();
        for (Family family : families) {
            uniqueFamilies.put(family.getFamilyName(), family); // Overwrite duplicates
        }
        families = new ArrayList<>(uniqueFamilies.values());
    }
}
