package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.Map;

public class EyelashStyle {
    @XmlAttribute
    private Styles.EyelashStyles eyelashStyles;
    @XmlAttribute
    private Genders gender;
    @XmlElement
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

    @XmlTransient
    public Map<Styles.EyelashStyles, Genders> getEyelashStyle(){
        return this.eyelashStyle;
    }
    @XmlTransient
    public Genders getGender(){
        return this.gender;
    }
    @XmlTransient
    public Styles.EyelashStyles getEyelashStyles(){
        return this.eyelashStyles;
    }
}
