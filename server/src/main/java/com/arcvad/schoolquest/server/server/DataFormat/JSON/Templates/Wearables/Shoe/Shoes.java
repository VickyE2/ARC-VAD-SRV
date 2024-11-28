package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Shoes extends BaseTemplate {
    @JsonProperty("shoe")
    private Shoe shoe;

    // Constructor, getters, and setters
    public Shoes(Shoe shoe) {
        this.shoe = shoe;
    }

    public Shoes() {}
    @JsonIgnore
    public Shoe getWearable() { return shoe; }
    public void setShoe(Shoe shoe) { this.shoe = shoe; }
}
