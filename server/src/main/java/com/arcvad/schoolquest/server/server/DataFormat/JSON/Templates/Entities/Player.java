package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopClothes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.Color;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class Player extends BaseTemplate implements Mergeable<Player> {
    private static final Logger log = Logger.getLogger(Player.class.getName());

    @JsonProperty
    private Styles.EyelashStyles eyeLashType;
    @JsonProperty
    private Color eyeLashHue;
    @JsonProperty
    private Styles.HairStyles hairType;
    @JsonProperty
    private Color hairHue;
    @JsonProperty
    private Color irisHue;
    @JsonProperty
    private Color skinHue;
    @JsonProperty
    private TopClothes currentTopCloth;
    @JsonProperty
    private BottomClothes currentBottomCloth;
    @JsonProperty
    private Shoes currentShoe;
    @JsonProperty
    private PlayerFamily family;

    @JsonProperty("accessory")
    private List<Accessory> currentAccessories;

    @JsonProperty("topCloth")
    private List<TopCloth> collectedTopCloth;

    @JsonProperty("bottomCloth")
    private List<BottomCloth> collectedBottomCloth;

    @JsonProperty("shoe")
    private List<Shoe> collectedShoes;

    @JsonProperty
    private List<Accessory> collectedAccessories;

    public void setEyeLashDesign(Styles.EyelashStyles eyeLashType) {
        this.eyeLashType = eyeLashType;
    }
    @JsonIgnore
    public Styles.EyelashStyles getEyeLashDesign() {
        return this.eyeLashType;
    }
    @JsonIgnore
    public Color getEyelashColor() {
        return eyeLashHue;
    }

    public void setEyeLashHue(Color eyeLashHue) {
        this.eyeLashHue = eyeLashHue;
    }
    @JsonIgnore
    public Styles.HairStyles getHairType() {
        return hairType;
    }

    public void setHairDesign(Styles.HairStyles hairDesign) {
        this.hairType = hairDesign;
    }
    @JsonIgnore
    public Color getHairHue() {
        return hairHue;
    }

    public void setHairShade(Color hairShade) {
        this.hairHue = hairShade;
    }
    @JsonIgnore
    public Color getIrisHue() {
        return irisHue;
    }

    public void setIrisColor(Color irisColor) {
        this.irisHue = irisColor;
    }
    @JsonIgnore
    public Color getSkinHue() {
        return skinHue;
    }

    public void setComplexion(Color skinHue) {
        this.skinHue = skinHue;
    }
    @JsonIgnore
    public TopClothes getFirstLayerCloth() {
        return currentTopCloth;
    }

    public void setUpperWear(TopClothes upperWear) {
        this.currentTopCloth = upperWear;
    }
    @JsonIgnore
    public BottomClothes getSecondLayerCloth() {
        return currentBottomCloth;
    }

    public void setLowerWear(BottomClothes lowerWear) {
        this.currentBottomCloth = lowerWear;
    }
    @JsonIgnore
    public Shoes getFootwear() {
        return currentShoe;
    }

    public void setFootwear(Shoes thirdLayerCloth) {
        this.currentShoe = thirdLayerCloth;
    }
    @JsonIgnore
    public List<Accessory> getAdornments() {
        return currentAccessories;
    }

    public void setAdornments(List<Accessory> forthLayerAccessory) {
        this.currentAccessories = forthLayerAccessory;
    }
    @JsonIgnore
    public List<TopCloth> getCollectedUpperWear() {
        return collectedTopCloth;
    }

    public void setCollectedUpperWear(List<TopCloth> collectedFirstLayerCloth) {
        this.collectedTopCloth = collectedFirstLayerCloth;
    }
    @JsonIgnore
    public List<BottomCloth> getCollectedLowerWear() {
        return collectedBottomCloth;
    }

    public void setCollectedLowerWear(List<BottomCloth> collectedSecondLayerCloth) {
        this.collectedBottomCloth = collectedSecondLayerCloth;
    }

    @JsonIgnore
    public List<Shoe> getCollectedFootwear() {
        return collectedShoes;
    }

    public void setCollectedFootwear(List<Shoe> collectedThirdLayerCloth) {
        this.collectedShoes = collectedThirdLayerCloth;
    }
    @JsonIgnore
    public List<Accessory> getCollectedAdornments() {
        return collectedAccessories;
    }

    public void setCollectedAdornments(List<Accessory> collectedForthLayerAccessory) {
        this.collectedAccessories = collectedForthLayerAccessory;
    }

    @JsonIgnore
    public PlayerFamily getFamily() {
        return family;
    }

    public void setFamily(PlayerFamily family) {
        this.family = family;
    }

    @Override
    public void mergeWith(Player otherAvatar) {
        if (otherAvatar == null) {
            logger.severe("ARC-MERGE", "Cannot merge with null Avatar object");
            throw new IllegalArgumentException("Cannot merge with null Avatar object");
        }

        // Merge basic fields (replace if non-null)
        if (otherAvatar.eyeLashType != null && !otherAvatar.eyeLashType.equals(this.eyeLashType)) {
            this.eyeLashType = otherAvatar.eyeLashType;
        }
        if (otherAvatar.eyeLashHue != null && !otherAvatar.eyeLashHue.equals(this.eyeLashHue)) {
            this.eyeLashHue = otherAvatar.eyeLashHue;
        }
        if (otherAvatar.hairType != null && !otherAvatar.hairType.equals(this.hairType)) {
            this.hairType = otherAvatar.hairType;
        }
        if (otherAvatar.hairHue != null && !otherAvatar.hairHue.equals(this.hairHue)) {
            this.hairHue = otherAvatar.hairHue;
        }
        if (otherAvatar.irisHue != null && !otherAvatar.irisHue.equals(this.irisHue)) {
            this.irisHue = otherAvatar.irisHue;
        }
        if (otherAvatar.skinHue != null && !otherAvatar.skinHue.equals(this.skinHue)) {
            this.skinHue = otherAvatar.skinHue;
        }
        if (otherAvatar.currentTopCloth != null && !otherAvatar.currentTopCloth.equals(this.currentTopCloth)) {
            this.currentTopCloth = otherAvatar.currentTopCloth;
        }
        if (otherAvatar.currentBottomCloth != null && !otherAvatar.currentBottomCloth.equals(this.currentBottomCloth)) {
            this.currentBottomCloth = otherAvatar.currentBottomCloth;
        }
        if (otherAvatar.currentShoe != null && !otherAvatar.currentShoe.equals(this.currentShoe)) {
            this.currentShoe = otherAvatar.currentShoe;
        }
        if (otherAvatar.family != null && !otherAvatar.family.equals(this.family)) {
            this.family = otherAvatar.family;
        }

        // Merge lists (only add new elements if they aren't already in the list)
        if (otherAvatar.currentAccessories != null) {
            if (this.currentAccessories == null) {
                this.currentAccessories = new ArrayList<>(otherAvatar.currentAccessories);
            } else {
                for (Accessory accessory : otherAvatar.currentAccessories) {
                    if (!this.currentAccessories.contains(accessory)) {
                        this.currentAccessories.add(accessory);
                    }
                }
            }
        }

        if (otherAvatar.collectedTopCloth != null) {
            if (this.collectedTopCloth == null) {
                this.collectedTopCloth = new ArrayList<>(otherAvatar.collectedTopCloth);
            } else {
                for (TopCloth cloth : otherAvatar.collectedTopCloth) {
                    if (!this.collectedTopCloth.contains(cloth)) {
                        this.collectedTopCloth.add(cloth);
                    }
                }
            }
        }

        if (otherAvatar.collectedBottomCloth != null) {
            if (this.collectedBottomCloth == null) {
                this.collectedBottomCloth = new ArrayList<>(otherAvatar.collectedBottomCloth);
            } else {
                for (BottomCloth cloth : otherAvatar.collectedBottomCloth) {
                    if (!this.collectedBottomCloth.contains(cloth)) {
                        this.collectedBottomCloth.add(cloth);
                    }
                }
            }
        }

        if (otherAvatar.collectedShoes != null) {
            if (this.collectedShoes == null) {
                this.collectedShoes = new ArrayList<>(otherAvatar.collectedShoes);
            } else {
                for (Shoe cloth : otherAvatar.collectedShoes) {
                    if (!this.collectedShoes.contains(cloth)) {
                        this.collectedShoes.add(cloth);
                    }
                }
            }
        }

        if (otherAvatar.collectedAccessories != null) {
            if (this.collectedAccessories == null) {
                this.collectedAccessories = new ArrayList<>(otherAvatar.collectedAccessories);
            } else {
                for (Accessory accessory : otherAvatar.collectedAccessories) {
                    if (!this.collectedAccessories.contains(accessory)) {
                        this.collectedAccessories.add(accessory);
                    }
                }
            }
        }

        logger.info("ARC-MERGE", "green[Merged Avatar data successfully]");
    }

}
