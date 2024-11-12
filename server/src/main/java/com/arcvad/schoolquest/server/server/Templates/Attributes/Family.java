package com.arcvad.schoolquest.server.server.Templates.Attributes;

import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.FamilyNames;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;
import com.arcvad.schoolquest.server.server.Templates.Entities.MinimalUser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public class Family extends BaseTemplate implements Mergeable<Family> {
    @XmlAttribute(name = "familyName")
    private String familyName;
    @XmlAttribute(name="familyWealth")
    private Wealth familyWealth;
    @XmlAttribute(name="familySize")
    private int familySize;
    @XmlTransient
    private FamilyNames family;

    @XmlElementWrapper
    @XmlElement(name="familyMember")
    private List<MinimalUser> familyMembers;

    // Constructor, getters, and setters
    public Family(FamilyNames family) {
        this.familyName = family.getFamilyName();
        this.family = family;
    }

    public Family() {}
    @XmlTransient
    public String getFamilyName() {
        return familyName;
    }

    @XmlTransient
    public List<MinimalUser> getFamilyMembers() {
        return familyMembers;
    }

    @XmlTransient
    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    @XmlTransient
    public int getFamilySize() {
        return familySize;
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
            throw new IllegalArgumentException("Cannot merge with a null Family");
        }
        if (other.familyName.equals(this.familyName)) {
            if (other.getFamilyMembers() != null && other.getFamilyMembers() != this.getFamilyMembers()) {
                this.familyMembers = other.familyMembers;
            }
        }
    }
}
