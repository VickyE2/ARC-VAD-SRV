package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

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
