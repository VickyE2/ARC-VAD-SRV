package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "hair_styles")
public class HairStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eyelash_style", nullable = false)
    private Styles.HairStyles eyelashStyles;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Genders gender;

    @ElementCollection
    @CollectionTable(name = "eyelash_styles_map", joinColumns = @JoinColumn(name = "eyelash_style_id"))
    @MapKeyColumn(name = "style_name")
    @Column(name = "gender")
    private Map<Styles.EyelashStyles, Genders> eyelashStyle;

    // Getters and Setters
    public Styles.HairStyles getEyelashStyles() {
        return eyelashStyles;
    }

    public void setEyelashStyles(Styles.HairStyles eyelashStyles) {
        this.eyelashStyles = eyelashStyles;
    }

    public Genders getGender() {
        return gender;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public Map<Styles.EyelashStyles, Genders> getEyelashStyle() {
        return eyelashStyle;
    }

    public void setEyelashStyle(Map<Styles.EyelashStyles, Genders> eyelashStyle) {
        this.eyelashStyle = eyelashStyle;
    }
}
