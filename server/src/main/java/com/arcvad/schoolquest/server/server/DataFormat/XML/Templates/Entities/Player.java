package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopClothes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Playerutils.Color;
import com.arcvad.schoolquest.server.server.Playerutils.Styles;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "playerData")
public class Player extends BaseTemplate implements Mergeable<Player> {
    private static final Logger log = Logger.getLogger(Player.class.getName());

    @XmlElement
    private Styles.EyelashStyles eyeLashType;
    @XmlElement
    private Color eyeLashHue;
    @XmlElement
    private Styles.HairStyles hairType;
    @XmlElement
    private Color hairHue;
    @XmlElement
    private Color irisHue;
    @XmlElement
    private Color skinHue;
    @XmlElement
    private TopClothes currentTopCloth;
    @XmlElement
    private BottomClothes currentBottomCloth;
    @XmlElement
    private Shoes currentShoe;
    @XmlElement
    private PlayerFamily family;

    @XmlElementWrapper
    @XmlElement(name = "accessory")
    private List<Accessory> currentAccessories;
    @XmlElementWrapper(name = "collectedTopCloth")
    @XmlElement(name = "topCloth")
    private List<TopCloth> collectedTopCloth;
    @XmlElementWrapper
    @XmlElement(name = "bottomCloth")
    private List<BottomCloth> collectedBottomCloth;
    @XmlElementWrapper
    @XmlElement(name = "shoe")
    private List<Shoe> collectedShoes;
    @XmlElementWrapper
    @XmlElement(name = "accessory")
    private List<Accessory> collectedAccessories;

    public void setEyeLashDesign(Styles.EyelashStyles eyeLashType) {
        this.eyeLashType = eyeLashType;
    }
    @XmlTransient
    public Styles.EyelashStyles getEyeLashDesign() {
        return this.eyeLashType;
    }
    @XmlTransient
    public Color getEyelashColor() {
        return eyeLashHue;
    }

    public void setEyeLashHue(Color eyeLashHue) {
        this.eyeLashHue = eyeLashHue;
    }
    @XmlTransient
    public Styles.HairStyles getHairType() {
        return hairType;
    }

    public void setHairDesign(Styles.HairStyles hairDesign) {
        this.hairType = hairDesign;
    }
    @XmlTransient
    public Color getHairHue() {
        return hairHue;
    }

    public void setHairShade(Color hairShade) {
        this.hairHue = hairShade;
    }
    @XmlTransient
    public Color getIrisHue() {
        return irisHue;
    }

    public void setIrisColor(Color irisColor) {
        this.irisHue = irisColor;
    }
    @XmlTransient
    public Color getSkinHue() {
        return skinHue;
    }

    public void setComplexion(Color skinHue) {
        this.skinHue = skinHue;
    }
    @XmlTransient
    public TopClothes getFirstLayerCloth() {
        return currentTopCloth;
    }

    public void setUpperWear(TopClothes upperWear) {
        this.currentTopCloth = upperWear;
    }
    @XmlTransient
    public BottomClothes getSecondLayerCloth() {
        return currentBottomCloth;
    }

    public void setLowerWear(BottomClothes lowerWear) {
        this.currentBottomCloth = lowerWear;
    }
    @XmlTransient
    public Shoes getFootwear() {
        return currentShoe;
    }

    public void setFootwear(Shoes thirdLayerCloth) {
        this.currentShoe = thirdLayerCloth;
    }
    @XmlTransient
    public List<Accessory> getAdornments() {
        return currentAccessories;
    }

    public void setAdornments(List<Accessory> forthLayerAccessory) {
        this.currentAccessories = forthLayerAccessory;
    }
    @XmlTransient
    public List<TopCloth> getCollectedUpperWear() {
        return collectedTopCloth;
    }

    public void setCollectedUpperWear(List<TopCloth> collectedFirstLayerCloth) {
        this.collectedTopCloth = collectedFirstLayerCloth;
    }
    @XmlTransient
    public List<BottomCloth> getCollectedLowerWear() {
        return collectedBottomCloth;
    }

    public void setCollectedLowerWear(List<BottomCloth> collectedSecondLayerCloth) {
        this.collectedBottomCloth = collectedSecondLayerCloth;
    }

    @XmlTransient
    public List<Shoe> getCollectedFootwear() {
        return collectedShoes;
    }

    public void setCollectedFootwear(List<Shoe> collectedThirdLayerCloth) {
        this.collectedShoes = collectedThirdLayerCloth;
    }
    @XmlTransient
    public List<Accessory> getCollectedAdornments() {
        return collectedAccessories;
    }

    public void setCollectedAdornments(List<Accessory> collectedForthLayerAccessory) {
        this.collectedAccessories = collectedForthLayerAccessory;
    }

    @XmlTransient
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
