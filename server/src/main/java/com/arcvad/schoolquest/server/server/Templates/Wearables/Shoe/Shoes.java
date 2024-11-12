package com.arcvad.schoolquest.server.server.Templates.Wearables.Shoe;

import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class Shoes extends BaseTemplate {
    @XmlElement(name = "shoe")
    private Shoe shoe;

    // Constructor, getters, and setters
    public Shoes(Shoe shoe) {
        this.shoe = shoe;
    }

    public Shoes() {}
    @XmlTransient
    public Shoe getWearable() { return shoe; }
    public void setShoe(Shoe shoe) { this.shoe = shoe; }
}
