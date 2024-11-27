package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
