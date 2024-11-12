package com.arcvad.schoolquest.server.server.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class PlayerFamily extends BaseTemplate {
    @XmlElement(name = "familyName")
    private String familyName;
    @XmlElement(name="familyWealth")
    private Wealth familyWealth;
    @XmlElement(name="familyPosition")
    private int familyPosition;
    @XmlElement(name="familySize")
    private int familySize;

    // Constructor, getters, and setters
    public PlayerFamily(Family family) {
        this.familyName = family.getFamilyName();
        this.familySize = family.getFamilySize();
        this.familyWealth = family.getFamilyWealth();
    }

    public PlayerFamily() {}
    @XmlTransient
    public String getFamilyName() {
        return familyName;
    }

    @XmlTransient
    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    @XmlTransient
    public int getFamilyPosition() {
        return familyPosition;
    }

    @XmlTransient
    public int getFamilySize() {
        return familySize;
    }

    public void setFamilyPosition(int familyPosition) {
        this.familyPosition = familyPosition;
    }
}
