package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.arcvad.schoolquest.server.server.Playerutils.Rarity;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

public class BottomCloth extends BaseTemplate {
    @XmlTransient
    public Rarity rarity;
    @XmlTransient
    public Material material;
    @XmlTransient
    public String key;

    public BottomCloth(Rarity rarity, Material material, String key) {
        this.rarity = rarity;
        this.material = material;
        this.key = key;
    }

    // Default constructor required for JAXB
    public BottomCloth() {}


    // Getters and setters
    @XmlAttribute
    public Rarity getrarity() { return rarity; }
    public void setrarity(Rarity rarity) { this.rarity = rarity; }

    @XmlAttribute
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    @XmlAttribute
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
