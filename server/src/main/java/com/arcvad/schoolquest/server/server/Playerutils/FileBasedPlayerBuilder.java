package com.arcvad.schoolquest.server.server.Playerutils;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes;

import java.util.ArrayList;
import java.util.List;

public class FileBasedPlayerBuilder {
    private Styles.EyelashStyles eyeLashType;
    private Color eyeLashHue;
    private Styles.HairStyles hairType;
    private Color hairHue;
    private Color irisHue;
    private Color skinHue;

    private TopClothes currentTopClothXml;
    private BottomClothes currentBottomClothXml;
    private Shoes currentShoeXml;
    private PlayerFamily familyXml;
    private List<Accessory> currentAccessoriesXml = new ArrayList<>();
    private List<TopCloth> collectedTopClothXml = new ArrayList<>();
    private List<BottomCloth> collectedBottomClothXml = new ArrayList<>();
    private List<Shoe> collectedShoesXml = new ArrayList<>();
    private List<Accessory> collectedAccessoriesXml = new ArrayList<>();

    private com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopClothes currentTopClothJson;
    private com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomClothes currentBottomClothJson;
    private com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes currentShoeJson;
    private com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.PlayerFamily familyJson;
    private List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> currentAccessoriesJson = new ArrayList<>();
    private List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth> collectedTopClothJson = new ArrayList<>();
    private List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth> collectedBottomClothJson = new ArrayList<>();
    private List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe> collectedShoesJson = new ArrayList<>();
    private List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> collectedAccessoriesJson = new ArrayList<>();


    public FileBasedPlayerBuilder setEyeLashType(Styles.EyelashStyles eyeLashType) {
        this.eyeLashType = eyeLashType;
        return this;
    }
    public FileBasedPlayerBuilder setEyeLashHue(Color eyeLashHue) {
        this.eyeLashHue = eyeLashHue;
        return this;
    }
    public FileBasedPlayerBuilder setHairType(Styles.HairStyles hairType) {
        this.hairType = hairType;
        return this;
    }
    public FileBasedPlayerBuilder setHairHue(Color hairHue) {
        this.hairHue = hairHue;
        return this;
    }
    public FileBasedPlayerBuilder setIrisHue(Color irisHue) {
        this.irisHue = irisHue;
        return this;
    }
    public FileBasedPlayerBuilder setSkinHue(Color skinHue) {
        this.skinHue = skinHue;
        return this;
    }

    public FileBasedPlayerBuilder setCurrentTopClothXml(TopClothes currentTopCloth) {
        this.currentTopClothXml = currentTopCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCurrentBottomClothXml(BottomClothes currentBottomCloth) {
        this.currentBottomClothXml = currentBottomCloth;
        return this;
    }
    public FileBasedPlayerBuilder setcurrentshoexml(Shoes currentShoe) {
        this.currentShoeXml = currentShoe;
        return this;
    }
    public FileBasedPlayerBuilder setFamilyXml(PlayerFamily family) {
        this.familyXml = family;
        return this;
    }
    public FileBasedPlayerBuilder setCurrentAccessoriesXml(List<Accessory> currentAccessories) {
        this.currentAccessoriesXml = currentAccessories;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedTopClothXml(List<TopCloth> collectedTopCloth) {
        this.collectedTopClothXml = collectedTopCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedBottomClothXml(List<BottomCloth> collectedBottomCloth) {
        this.collectedBottomClothXml = collectedBottomCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedShoesXml(List<Shoe> collectedShoes) {
        this.collectedShoesXml = collectedShoes;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedAccessoriesXml(List<com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory> collectedAccessories) {
        this.collectedAccessoriesXml = collectedAccessories;
        return this;
    }

    public FileBasedPlayerBuilder setCurrentTopClothJson(com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopClothes currentTopCloth) {
        this.currentTopClothJson = currentTopCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCurrentBottomClothJson(com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomClothes currentBottomCloth) {
        this.currentBottomClothJson = currentBottomCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCurrentShoeJson(com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes currentShoe) {
        this.currentShoeJson = currentShoe;
        return this;
    }
    public FileBasedPlayerBuilder setFamilyJson(com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.PlayerFamily family) {
        this.familyJson = family;
        return this;
    }
    public FileBasedPlayerBuilder setCurrentAccessoriesJson(List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> currentAccessories) {
        this.currentAccessoriesJson = currentAccessories;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedTopClothJson(List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth> collectedTopCloth) {
        this.collectedTopClothJson = collectedTopCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedBottomClothJson(List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth> collectedBottomCloth) {
        this.collectedBottomClothJson = collectedBottomCloth;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedShoesJson(List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe> collectedShoes) {
        this.collectedShoesJson = collectedShoes;
        return this;
    }
    public FileBasedPlayerBuilder setCollectedAccessoriesJson(List<com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory> collectedAccessories) {
        this.collectedAccessoriesJson = collectedAccessories;
        return this;
    }

    public Player buildXml() {
        Player player = new Player();
        player.setEyeLashDesign(eyeLashType);
        player.setEyeLashHue(eyeLashHue);
        player.setHairDesign(hairType);
        player.setHairShade(hairHue);
        player.setIrisColor(irisHue);
        player.setComplexion(skinHue);
        player.setUpperWear(currentTopClothXml);
        player.setLowerWear(currentBottomClothXml);
        player.setFootwear(currentShoeXml);
        player.setFamily(familyXml);
        player.setAdornments(currentAccessoriesXml);
        player.setCollectedUpperWear(collectedTopClothXml);
        player.setCollectedLowerWear(collectedBottomClothXml);
        player.setCollectedFootwear(collectedShoesXml);
        player.setCollectedAdornments(collectedAccessoriesXml);
        return player;
    }

    public com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player buildJson() {
        com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player player = new com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities.Player();
        player.setEyeLashDesign(eyeLashType);
        player.setEyeLashHue(eyeLashHue);
        player.setHairDesign(hairType);
        player.setHairShade(hairHue);
        player.setIrisColor(irisHue);
        player.setComplexion(skinHue);
        player.setUpperWear(currentTopClothJson);
        player.setLowerWear(currentBottomClothJson);
        player.setFootwear(currentShoeJson);
        player.setFamily(familyJson);
        player.setAdornments(currentAccessoriesJson);
        player.setCollectedUpperWear(collectedTopClothJson);
        player.setCollectedLowerWear(collectedBottomClothJson);
        player.setCollectedFootwear(collectedShoesJson);
        player.setCollectedAdornments(collectedAccessoriesJson);
        return player;
    }
}
