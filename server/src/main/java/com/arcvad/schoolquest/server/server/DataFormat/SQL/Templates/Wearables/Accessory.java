package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "accessories")
public class Accessory {

    @Column(name = "rarity", nullable = false)
    private String rarity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "material", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "materialRegistrar", nullable = false) // Specify the foreign key column
    private MaterialRegistrar materialRegistrar;


    public Accessory() {}

    public MaterialRegistrar getMaterialRegistrar() {
        return materialRegistrar;
    }

    public void setMaterialRegistrar(MaterialRegistrar materialRegistrar) {
        this.materialRegistrar = materialRegistrar;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

