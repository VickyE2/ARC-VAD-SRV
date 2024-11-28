package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Accessory extends BaseTemplate {
    @JsonProperty
    public String accessoryRarity;
    @JsonProperty
    public String accessoryKey;
    @JsonProperty
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
    @JsonIgnore
    public Material getItems(){
        return this.accessoryMaterial;
    }
    @JsonIgnore
    public String getCommonness() {
        return this.accessoryRarity;
    }
    @JsonIgnore
    public String getkeyentifyer() {
        return this.accessoryKey;
    }
}
