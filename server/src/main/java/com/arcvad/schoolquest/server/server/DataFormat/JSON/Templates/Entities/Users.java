package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Users extends BaseTemplate {
    @JsonProperty("bottomCloth")
    private BottomCloth shoe;

    // Constructor, getters, and setters
    public Users(BottomCloth shoe) {
        this.shoe = shoe;
    }

    public Users() {}
    @JsonIgnore
    public BottomCloth getWearable() { return shoe; }
    public void setShoe(BottomCloth shoe) { this.shoe = shoe; }
}
