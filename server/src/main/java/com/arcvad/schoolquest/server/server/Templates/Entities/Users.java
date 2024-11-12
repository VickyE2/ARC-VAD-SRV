package com.arcvad.schoolquest.server.server.Templates.Entities;

import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;
import com.arcvad.schoolquest.server.server.Templates.Wearables.BottomCloth.BottomCloth;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class Users extends BaseTemplate {
    @XmlElement(name = "bottomCloth")
    private BottomCloth shoe;

    // Constructor, getters, and setters
    public Users(BottomCloth shoe) {
        this.shoe = shoe;
    }

    public Users() {}
    @XmlTransient
    public BottomCloth getWearable() { return shoe; }
    public void setShoe(BottomCloth shoe) { this.shoe = shoe; }
}
