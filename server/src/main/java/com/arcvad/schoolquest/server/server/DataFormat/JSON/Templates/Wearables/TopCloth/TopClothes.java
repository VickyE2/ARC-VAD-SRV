package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopClothes extends BaseTemplate {
    @JsonProperty("topCloth")
    private TopCloth shoe;

    // Constructor, getters, and setters
    public TopClothes(TopCloth shoe) {
        this.shoe = shoe;
    }

    public TopClothes() {}
    @JsonIgnore
    public TopCloth getWearable() { return shoe; }
    public void setShoe(TopCloth shoe) { this.shoe = shoe; }
}
