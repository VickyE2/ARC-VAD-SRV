package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.Playerutils.Color;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;

import java.util.ArrayList;
import java.util.List;

public class PlayerBuilder {
    private String id;
    private Styles.EyelashStyles eyeLashType;
    private Color eyeLashHue;
    private Styles.HairStyles hairType;
    private Color hairHue;
    private Color irisHue;
    private Color skinHue;
    private TopCloth currentTopCloth;
    private BottomCloth currentBottomCloth;
    private Shoe currentShoe;
    private Family family;
    private List<Accessory> currentAccessories = new ArrayList<>();
    private List<TopCloth> collectedTopCloth = new ArrayList<>();
    private List<BottomCloth> collectedBottomCloth = new ArrayList<>();
    private List<Shoe> collectedShoes = new ArrayList<>();
    private List<Accessory> collectedAccessories = new ArrayList<>();

    public PlayerBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public PlayerBuilder setEyeLashType(Styles.EyelashStyles eyeLashType) {
        this.eyeLashType = eyeLashType;
        return this;
    }

    public PlayerBuilder setEyeLashHue(Color eyeLashHue) {
        this.eyeLashHue = eyeLashHue;
        return this;
    }

    public PlayerBuilder setHairType(Styles.HairStyles hairType) {
        this.hairType = hairType;
        return this;
    }

    public PlayerBuilder setHairHue(Color hairHue) {
        this.hairHue = hairHue;
        return this;
    }

    public PlayerBuilder setIrisHue(Color irisHue) {
        this.irisHue = irisHue;
        return this;
    }

    public PlayerBuilder setSkinHue(Color skinHue) {
        this.skinHue = skinHue;
        return this;
    }

    public PlayerBuilder setCurrentTopCloth(TopCloth currentTopCloth) {
        this.currentTopCloth = currentTopCloth;
        return this;
    }

    public PlayerBuilder setCurrentBottomCloth(BottomCloth currentBottomCloth) {
        this.currentBottomCloth = currentBottomCloth;
        return this;
    }

    public PlayerBuilder setCurrentShoe(Shoe currentShoe) {
        this.currentShoe = currentShoe;
        return this;
    }

    public PlayerBuilder setFamily(Family family) {
        this.family = family;
        return this;
    }

    public PlayerBuilder setCurrentAccessories(List<Accessory> currentAccessories) {
        this.currentAccessories = currentAccessories;
        return this;
    }

    public PlayerBuilder addCurrentAccessory(Accessory accessory) {
        this.currentAccessories.add(accessory);
        return this;
    }

    public PlayerBuilder setCollectedTopCloth(List<TopCloth> collectedTopCloth) {
        this.collectedTopCloth = collectedTopCloth;
        return this;
    }

    public PlayerBuilder addCollectedTopCloth(TopCloth topCloth) {
        this.collectedTopCloth.add(topCloth);
        return this;
    }

    public PlayerBuilder setCollectedBottomCloth(List<BottomCloth> collectedBottomCloth) {
        this.collectedBottomCloth = collectedBottomCloth;
        return this;
    }

    public PlayerBuilder addCollectedBottomCloth(BottomCloth bottomCloth) {
        this.collectedBottomCloth.add(bottomCloth);
        return this;
    }

    public PlayerBuilder setCollectedShoes(List<Shoe> collectedShoes) {
        this.collectedShoes = collectedShoes;
        return this;
    }

    public PlayerBuilder addCollectedShoe(Shoe shoe) {
        this.collectedShoes.add(shoe);
        return this;
    }

    public PlayerBuilder setCollectedAccessories(List<Accessory> collectedAccessories) {
        this.collectedAccessories = collectedAccessories;
        return this;
    }

    public PlayerBuilder addCollectedAccessory(Accessory accessory) {
        this.collectedAccessories.add(accessory);
        return this;
    }

    public Player build() {
        Player player = new Player();
        player.setId(this.id);
        player.setEyeLashType(this.eyeLashType);
        player.setEyeLashHue(this.eyeLashHue);
        player.setHairType(this.hairType);
        player.setHairHue(this.hairHue);
        player.setIrisHue(this.irisHue);
        player.setSkinHue(this.skinHue);
        player.setCurrentTopCloth(this.currentTopCloth);
        player.setCurrentBottomCloth(this.currentBottomCloth);
        player.setCurrentShoe(this.currentShoe);
        player.setFamily(this.family);
        player.setCurrentAccessories(this.currentAccessories);
        player.setCollectedTopCloth(this.collectedTopCloth);
        player.setCollectedBottomCloth(this.collectedBottomCloth);
        player.setCollectedShoes(this.collectedShoes);
        player.setCollectedAccessories(this.collectedAccessories);
        return player;
    }
}

