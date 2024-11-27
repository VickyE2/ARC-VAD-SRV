package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables;

import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.arcvad.schoolquest.server.server.Playerutils.Rarity;
import jakarta.persistence.*;

@Entity
@Table(name = "top_cloths")
public class TopCloth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    private Rarity rarity;

    @Enumerated(EnumType.STRING)
    @Column(name = "material")
    private Material material;

    @Column(name = "key", unique = true, nullable = false)
    private String key;

    public TopCloth(Rarity rarity, Material material, String key){
        this.key = key;
        this.material = material;
        this.rarity = rarity;
    }

    // Getters and Setters
    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

