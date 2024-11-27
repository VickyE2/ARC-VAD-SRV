package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.Map;

public class HairStyle {
    @XmlAttribute
    private Styles.HairStyles hairStyleName;
    @XmlAttribute
    private Genders gender;
    @XmlElement
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

    @XmlTransient
    public Map<Styles.HairStyles, Genders> getHairStyles(){
        return this.hairStyle;
    }
    @XmlTransient
    public Genders getGender(){
        return this.gender;
    }
    @XmlTransient
    public Styles.HairStyles getHairStyleName(){
        return this.hairStyleName;
    }
}
