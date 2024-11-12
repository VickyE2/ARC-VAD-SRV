package com.arcvad.schoolquest.server.server.Templates.Entities;

import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@XmlRootElement(namespace="families")
public class FamilyRegistrar extends BaseTemplate implements Mergeable<FamilyRegistrar> {

    @XmlElementWrapper
    @XmlElement(name = "family")
    private List<Family> families;

    @XmlTransient
    public List<Family> getFamilies(){
        return this.families;
    }

    public void setFamilies(List<Family> families) {
        this.families = families;
    }


    public void mergeWith(FamilyRegistrar other) {
        if (other == null){
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
