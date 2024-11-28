package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BottomClothes extends BaseTemplate {
    @JsonProperty("bottomCloth")
    private BottomCloth shoe;

    // Constructor, getters, and setters
    public BottomClothes(BottomCloth shoe) {
        this.shoe = shoe;
    }

    public BottomClothes() {}
    @JsonIgnore
    public BottomCloth getWearable() { return shoe; }
    public void setShoe(BottomCloth shoe) { this.shoe = shoe; }
}
