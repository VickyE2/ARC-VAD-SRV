package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Wealth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerFamily extends BaseTemplate {
    @JsonProperty("familyName")
    private String familyName;
    @JsonProperty("familyWealth")
    private Wealth familyWealth;
    @JsonProperty("familyPosition")
    private int familyPosition;

    @JsonIgnore
    private Family family;

    // Constructor, getters, and setters
    public PlayerFamily(Family family) {
        this.familyName = family.getFamilyName();
        this.familyWealth = family.getFamilyWealth();
        this.family = family;
    }

    public PlayerFamily() {}
    @JsonIgnore
    public String getFamilyName() {
        return familyName;
    }

    @JsonIgnore
    public Wealth getFamilyWealth() {
        return familyWealth;
    }

    @JsonIgnore
    public int getFamilyPosition() {
        return familyPosition;
    }

    public void setFamilyPosition(int familyPosition) {
        this.familyPosition = familyPosition;
    }

    @JsonIgnore
    public Family getFamily(){
        return this.family;
    }
}
