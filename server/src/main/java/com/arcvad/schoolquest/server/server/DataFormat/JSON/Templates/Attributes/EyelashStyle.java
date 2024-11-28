package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class EyelashStyle {
    @JsonProperty
    private Styles.EyelashStyles eyelashStyles;
    @JsonProperty
    private Genders gender;
    @JsonProperty
    private Map<Styles.EyelashStyles, Genders> eyelashStyle;


    public void setHairStyleName(Styles.EyelashStyles styleName){
        this.eyelashStyles = styleName;
    }
    public void setGender(Genders gender){
        this.gender = gender;
    }

    public void setEyelashStyles(Map<Styles.EyelashStyles, Genders> hairStyle){
        this.eyelashStyle = hairStyle;
    }

    @JsonIgnore
    public Map<Styles.EyelashStyles, Genders> getEyelashStyle(){
        return this.eyelashStyle;
    }
    @JsonIgnore
    public Genders getGender(){
        return this.gender;
    }
    @JsonIgnore
    public Styles.EyelashStyles getEyelashStyles(){
        return this.eyelashStyles;
    }
}
