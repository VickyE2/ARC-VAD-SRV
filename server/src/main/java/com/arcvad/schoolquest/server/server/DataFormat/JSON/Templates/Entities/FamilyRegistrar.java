package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    public void mergeWith(FamilyRegistrar other) {
        if (other == null){
            logger.severe("ARC-MERGE", "Cannot merge with a null class");
            throw new IllegalArgumentException("Cannot merge with a null class");
        }

        if (other.families != null) {
            if (this.families == null) {
                this.families = new ArrayList<>(other.families);  // Initialize if null
            } else {
                // Using a set to track existing usernames in this.users
                Set<String> existingUsernames = this.families.stream()
                    .map(Family::getFamilyName)  // Assuming User has a getUsername() method
                    .collect(Collectors.toSet());

                // Add only users from 'other' that are not already in 'this'
                for (Family family : other.families) {
                    if (!existingUsernames.contains(family.getFamilyName())) {
                        this.families.add(family);
                    }
                }
            }
        }

    }
}
