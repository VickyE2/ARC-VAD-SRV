package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

public class PlayerFamily extends BaseTemplate {
    @XmlElement(name = "familyName")
    private String familyName;
    @XmlElement(name="familyWealth")
    private Wealth familyWealth;
    @XmlElement(name="familyPosition")
    private int familyPosition;

    // Constructor, getters, and setters
    public PlayerFamily(Family family) {
        this.familyName = family.getFamilyName();
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

    public void setFamilyPosition(int familyPosition) {
        this.familyPosition = familyPosition;
    }
}
