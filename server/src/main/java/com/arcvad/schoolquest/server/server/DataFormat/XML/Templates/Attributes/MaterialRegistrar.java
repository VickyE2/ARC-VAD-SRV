package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "registrar")
public class MaterialRegistrar extends BaseTemplate implements Mergeable<MaterialRegistrar> {
    @XmlElement
    private List<BottomCloth> bottomClothList;
    @XmlElement
    private List<TopCloth> topClothList;
    @XmlElement
    private List<Shoes> shoesList;
    @XmlElement
    private List<Accessory> accessoryList;



    public void setBottomClothList(List<BottomCloth> bottomClothList){
        this.bottomClothList = bottomClothList;
    }

    public void setAccessoryList(List<Accessory> accessoryList){
        this.accessoryList = accessoryList;
    }

    public void setShoesList(List<Shoes> shoesList){
        this.shoesList = shoesList;
    }

    public void setTopClothList(List<TopCloth> topClothList){
        this.topClothList = topClothList;
    }
    @XmlTransient
    public List<BottomCloth> getBottomClothList(){
        return this.bottomClothList;
    }
    @XmlTransient
    public List<Accessory> getAccessoryList(){
        return this.accessoryList;
    }
    @XmlTransient
    public List<TopCloth> getTopClothList(){
        return this.topClothList;
    }
    @XmlTransient
    public List<Shoes> getShoesList(){
        return this.shoesList;
    }

    @Override
    public void mergeWith(MaterialRegistrar other) {
        if (other == null) {
            logger.severe("ARC-MERGE", "Cannot merge with null MaterialRegistrar object");
            throw new IllegalArgumentException("Cannot merge with null MaterialRegistrar object");
        }

        // Merging bottomClothList
        if (other.bottomClothList != null) {
            if (this.bottomClothList == null) {
                this.bottomClothList = other.bottomClothList;
            } else {
                this.bottomClothList.addAll(other.bottomClothList);
            }
        }

        // Merging topClothList
        if (other.topClothList != null) {
            if (this.topClothList == null) {
                this.topClothList = other.topClothList;
            } else {
                this.topClothList.addAll(other.topClothList);
            }
        }

        // Merging shoesList
        if (other.shoesList != null) {
            if (this.shoesList == null) {
                this.shoesList = other.shoesList;
            } else {
                this.shoesList.addAll(other.shoesList);
            }
        }

        // Merging accessoryList
        if (other.accessoryList != null) {
            if (this.accessoryList == null) {
                this.accessoryList = other.accessoryList;
            } else {
                this.accessoryList.addAll(other.accessoryList);
            }
        }

        // Log the merge action
        logger.info("ARC-MERGE", "green[Merged MaterialRegistrar data successfully]");
    }
}
