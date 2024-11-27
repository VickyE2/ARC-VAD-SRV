package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables;

import com.arcvad.schoolquest.server.server.Playerutils.Material;
import jakarta.persistence.*;

@Entity
@Table(name = "accessories")
public class Accessory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rarity", nullable = false)
    private String rarity;

    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "material", nullable = false)
    private Material material;

    // Getters and Setters
    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}

