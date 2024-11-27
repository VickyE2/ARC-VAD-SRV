package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "eyelash_styles")
public class EyelashStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eyelash_style", nullable = false)
    private Styles.EyelashStyles eyelashStyles;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Genders gender;

    @ElementCollection
    @CollectionTable(name = "eyelash_styles_map", joinColumns = @JoinColumn(name = "eyelash_style_id"))
    @MapKeyColumn(name = "style_name")
    @Column(name = "gender")
    private Map<Styles.EyelashStyles, Genders> eyelashStyle;

    // Getters and Setters
    public Styles.EyelashStyles getEyelashStyles() {
        return eyelashStyles;
    }

    public void setEyelashStyles(Styles.EyelashStyles eyelashStyles) {
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

