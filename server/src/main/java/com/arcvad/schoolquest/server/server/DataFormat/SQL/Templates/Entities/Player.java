package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.Playerutils.Color;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Entity
@Table(name = "players")
public class Player {
    private static final Logger log = Logger.getLogger(Player.class.getName());

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "eyelash_type")
    private Styles.EyelashStyles eyeLashType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "r", column = @Column(name = "eyelash_hue_r")),
        @AttributeOverride(name = "g", column = @Column(name = "eyelash_hue_g")),
        @AttributeOverride(name = "b", column = @Column(name = "eyelash_hue_b"))
    })
    private Color eyeLashHue;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_type")
    private Styles.HairStyles hairType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "r", column = @Column(name = "hair_hue_r")),
        @AttributeOverride(name = "g", column = @Column(name = "hair_hue_g")),
        @AttributeOverride(name = "b", column = @Column(name = "hair_hue_b"))
    })
    private Color hairHue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "r", column = @Column(name = "iris_hue_r")),
        @AttributeOverride(name = "g", column = @Column(name = "iris_hue_g")),
        @AttributeOverride(name = "b", column = @Column(name = "iris_hue_b"))
    })
    private Color irisHue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "r", column = @Column(name = "skin_hue_r")),
        @AttributeOverride(name = "g", column = @Column(name = "skin_hue_g")),
        @AttributeOverride(name = "b", column = @Column(name = "skin_hue_b"))
    })
    private Color skinHue;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_top_cloth_id")
    private TopCloth currentTopCloth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_bottom_cloth_id")
    private BottomCloth currentBottomCloth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_shoe_id")
    private Shoe currentShoe;

    @Embedded
    private PlayerFamily family;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "player_accessories",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "accessory_id")
    )
    private List<Accessory> currentAccessories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "player_collected_top_clothes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "top_cloth_id")
    )
    private List<TopCloth> collectedTopCloth = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "player_collected_bottom_clothes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "bottom_cloth_id")
    )
    private List<BottomCloth> collectedBottomCloth = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "player_collected_shoes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "shoe_id")
    )
    private List<Shoe> collectedShoes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "player_collected_accessories",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "accessory_id")
    )
    private List<Accessory> collectedAccessories = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Styles.EyelashStyles getEyeLashType() {
        return eyeLashType;
    }

    public void setEyeLashType(Styles.EyelashStyles eyeLashType) {
        this.eyeLashType = eyeLashType;
    }

    public Color getEyeLashHue() {
        return eyeLashHue;
    }

    public void setEyeLashHue(Color eyeLashHue) {
        this.eyeLashHue = eyeLashHue;
    }

    public Styles.HairStyles getHairType() {
        return hairType;
    }

    public void setHairType(Styles.HairStyles hairType) {
        this.hairType = hairType;
    }

    public Color getHairHue() {
        return hairHue;
    }

    public void setHairHue(Color hairHue) {
        this.hairHue = hairHue;
    }

    public Color getIrisHue() {
        return irisHue;
    }

    public void setIrisHue(Color irisHue) {
        this.irisHue = irisHue;
    }

    public Color getSkinHue() {
        return skinHue;
    }

    public void setSkinHue(Color skinHue) {
        this.skinHue = skinHue;
    }

    public TopCloth getCurrentTopCloth() {
        return currentTopCloth;
    }

    public void setCurrentTopCloth(TopCloth currentTopCloth) {
        this.currentTopCloth = currentTopCloth;
    }

    public BottomCloth getCurrentBottomCloth() {
        return currentBottomCloth;
    }

    public void setCurrentBottomCloth(BottomCloth currentBottomCloth) {
        this.currentBottomCloth = currentBottomCloth;
    }

    public Shoe getCurrentShoe() {
        return currentShoe;
    }

    public void setCurrentShoe(Shoe currentShoe) {
        this.currentShoe = currentShoe;
    }

    public PlayerFamily getFamily() {
        return family;
    }

    public void setFamily(PlayerFamily family) {
        this.family = family;
    }

    public List<Accessory> getCurrentAccessories() {
        return currentAccessories;
    }

    public void setCurrentAccessories(List<Accessory> currentAccessories) {
        this.currentAccessories = currentAccessories;
    }

    public List<TopCloth> getCollectedTopCloth() {
        return collectedTopCloth;
    }

    public void setCollectedTopCloth(List<TopCloth> collectedTopCloth) {
        this.collectedTopCloth = collectedTopCloth;
    }

    public List<BottomCloth> getCollectedBottomCloth() {
        return collectedBottomCloth;
    }

    public void setCollectedBottomCloth(List<BottomCloth> collectedBottomCloth) {
        this.collectedBottomCloth = collectedBottomCloth;
    }

    public List<Shoe> getCollectedShoes() {
        return collectedShoes;
    }

    public void setCollectedShoes(List<Shoe> collectedShoes) {
        this.collectedShoes = collectedShoes;
    }

    public List<Accessory> getCollectedAccessories() {
        return collectedAccessories;
    }

    public void setCollectedAccessories(List<Accessory> collectedAccessories) {
        this.collectedAccessories = collectedAccessories;
    }

}

