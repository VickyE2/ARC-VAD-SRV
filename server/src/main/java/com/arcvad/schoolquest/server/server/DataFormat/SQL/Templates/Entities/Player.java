package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.Playerutils.Color;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "players")
public class Player {

    @Enumerated(EnumType.STRING)
    @Column(name = "eyelash_type")
    private Styles.EyelashStyles eyeLashType;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_type")
    private Styles.HairStyles hairType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "red", column = @Column(name = "eyelash_hue_r")),
        @AttributeOverride(name = "green", column = @Column(name = "eyelash_hue_g")),
        @AttributeOverride(name = "blue", column = @Column(name = "eyelash_hue_b")),
        @AttributeOverride(name = "alpha", column = @Column(name = "eyelash_hue_alpha")) // Add this line
    })
    private Color eyeLashHue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "red", column = @Column(name = "hair_hue_r")),
        @AttributeOverride(name = "green", column = @Column(name = "hair_hue_g")),
        @AttributeOverride(name = "blue", column = @Column(name = "hair_hue_b")),
        @AttributeOverride(name = "alpha", column = @Column(name = "hair_hue_alpha")) // Add this line
    })
    private Color hairHue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "red", column = @Column(name = "iris_hue_r")),
        @AttributeOverride(name = "green", column = @Column(name = "iris_hue_g")),
        @AttributeOverride(name = "blue", column = @Column(name = "iris_hue_b")),
        @AttributeOverride(name = "alpha", column = @Column(name = "iris_hue_alpha")) // Add this line
    })
    private Color irisHue;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "red", column = @Column(name = "skin_hue_r")),
        @AttributeOverride(name = "green", column = @Column(name = "skin_hue_g")),
        @AttributeOverride(name = "blue", column = @Column(name = "skin_hue_b")),
        @AttributeOverride(name = "alpha", column = @Column(name = "skin_hue_alpha")) // Add this line
    })
    private Color skinHue;

    @ManyToOne
    @JoinColumn(name = "current_top_cloth_id", nullable = true)
    private TopCloth currentTopCloth;

    @ManyToOne
    @JoinColumn(name = "current_bottom_cloth_id", nullable = true)
    private BottomCloth currentBottomCloth;

    @ManyToOne
    @JoinColumn(name = "current_shoe_id", nullable = true)
    private Shoe currentShoe;

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user", nullable = false) // Specify the foreign key column
    private User user;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_accessories",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "accessory_key")
    )
    private List<Accessory> currentAccessories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_collected_top_clothes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "top_cloth_key")
    )
    private List<TopCloth> collectedTopCloth = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_collected_bottom_clothes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "bottom_cloth_key")
    )
    private List<BottomCloth> collectedBottomCloth = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_collected_shoes",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "shoe_key")
    )
    private List<Shoe> collectedShoes = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_collected_accessories",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "accessory_key")
    )
    private List<Accessory> collectedAccessories = new ArrayList<>();





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

    public User getUser() {
        return user;
    }

    public void setUser(User family) {
        this.user = family;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

