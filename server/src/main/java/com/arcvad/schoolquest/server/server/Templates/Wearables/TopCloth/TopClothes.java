package com.arcvad.schoolquest.server.server.Templates.Wearables.TopCloth;

import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class TopClothes extends BaseTemplate {
    @XmlElement(name = "topCloth")
    private TopCloth shoe;

    // Constructor, getters, and setters
    public TopClothes(TopCloth shoe) {
        this.shoe = shoe;
    }

    public TopClothes() {}
    @XmlTransient
    public TopCloth getWearable() { return shoe; }
    public void setShoe(TopCloth shoe) { this.shoe = shoe; }
}
