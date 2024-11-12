package com.arcvad.schoolquest.server.server.Templates.Wearables.Accessory;

import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

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
