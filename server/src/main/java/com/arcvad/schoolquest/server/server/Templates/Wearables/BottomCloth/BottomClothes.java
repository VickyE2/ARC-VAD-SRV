package com.arcvad.schoolquest.server.server.Templates.Wearables.BottomCloth;

import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class BottomClothes extends BaseTemplate {
    @XmlElement(name = "bottomCloth")
    private BottomCloth shoe;

    // Constructor, getters, and setters
    public BottomClothes(BottomCloth shoe) {
        this.shoe = shoe;
    }

    public BottomClothes() {}
    @XmlTransient
    public BottomCloth getWearable() { return shoe; }
    public void setShoe(BottomCloth shoe) { this.shoe = shoe; }
}
