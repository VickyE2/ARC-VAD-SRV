package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.Playerutils.Material;
import com.arcvad.schoolquest.server.server.Playerutils.Rarity;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "shoes")
public class Shoe {

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    private Rarity rarity;

    @Enumerated(EnumType.STRING)
    @Column(name = "material")
    private Material material;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "key", nullable = false, unique = true)
    private String key;

    public Shoe(Rarity rarity, Material material, String key){
        this.key = key;
        this.material = material;
        this.rarity = rarity;
    }

    public Shoe () {}

    @ManyToOne
    @JoinColumn(name = "materialRegistrar", nullable = false) // Specify the foreign key column
    private MaterialRegistrar materialRegistrar;

    public MaterialRegistrar getMaterialRegistrar() {
        return materialRegistrar;
    }

    public void setMaterialRegistrar(MaterialRegistrar materialRegistrar) {
        this.materialRegistrar = materialRegistrar;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

