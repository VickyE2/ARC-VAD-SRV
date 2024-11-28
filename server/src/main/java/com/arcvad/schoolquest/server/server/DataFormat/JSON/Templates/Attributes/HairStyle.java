package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class HairStyle {
    @JsonProperty
    private Styles.HairStyles hairStyleName;
    @JsonProperty
    private Genders gender;
    @JsonProperty
    private Map<Styles.HairStyles, Genders> hairStyle;


    public void setHairStyleName(Styles.HairStyles styleName){
        this.hairStyleName = styleName;
    }
    public void setGender(Genders gender){
        this.gender = gender;
    }
    public void setHairStyles(Map<Styles.HairStyles, Genders> hairStyle){
        this.hairStyle = hairStyle;
    }

    @JsonIgnore
    public Map<Styles.HairStyles, Genders> getHairStyles(){
        return this.hairStyle;
    }
    @JsonIgnore
    public Genders getGender(){
        return this.gender;
    }
    @JsonIgnore
    public Styles.HairStyles getHairStyleName(){
        return this.hairStyleName;
    }
}
