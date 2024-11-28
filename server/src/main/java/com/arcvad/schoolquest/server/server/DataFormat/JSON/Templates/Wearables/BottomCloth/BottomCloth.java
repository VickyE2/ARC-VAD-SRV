package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.arcvad.schoolquest.server.server.Playerutils.Rarity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BottomCloth extends BaseTemplate {
    @JsonIgnore
    public Rarity rarity;
    @JsonIgnore
    public Material material;
    @JsonIgnore
    public String key;

    public BottomCloth(Rarity rarity, Material material, String key) {
        this.rarity = rarity;
        this.material = material;
        this.key = key;
    }

    // Default constructor required for JAXB
    public BottomCloth() {}


    // Getters and setters
    @JsonProperty
    public Rarity getrarity() { return rarity; }
    public void setrarity(Rarity rarity) { this.rarity = rarity; }

    @JsonProperty
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    @JsonProperty
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
