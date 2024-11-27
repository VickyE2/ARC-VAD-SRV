package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

public class Accessory extends BaseTemplate {
    @XmlAttribute
    public String accessoryRarity;
    @XmlAttribute
    public String accessoryKey;
    @XmlElement
    public Material accessoryMaterial;


    public void setCommonness(String accessoryRarity) {
        this.accessoryRarity = accessoryRarity;
    }

    public void setkeyentifyer(String accessoryKey) {
        this.accessoryKey = accessoryKey;
    }

    public void setItems(Material accessoryMaterial){
        this.accessoryMaterial = accessoryMaterial;
    }
    @XmlTransient
    public Material getItems(){
        return this.accessoryMaterial;
    }
    @XmlTransient
    public String getCommonness() {
        return this.accessoryRarity;
    }
    @XmlTransient
    public String getkeyentifyer() {
        return this.accessoryKey;
    }
}
