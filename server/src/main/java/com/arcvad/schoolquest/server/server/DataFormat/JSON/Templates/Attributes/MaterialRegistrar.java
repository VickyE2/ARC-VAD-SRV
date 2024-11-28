package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.Shoe.Shoes;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class MaterialRegistrar extends BaseTemplate implements Mergeable<MaterialRegistrar> {
    @JsonProperty
    private List<BottomCloth> bottomClothList;
    @JsonProperty
    private List<TopCloth> topClothList;
    @JsonProperty
    private List<Shoes> shoesList;
    @JsonProperty
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
    @JsonIgnore
    public List<BottomCloth> getBottomClothList(){
        return this.bottomClothList;
    }
    @JsonIgnore
    public List<Accessory> getAccessoryList(){
        return this.accessoryList;
    }
    @JsonIgnore
    public List<TopCloth> getTopClothList(){
        return this.topClothList;
    }
    @JsonIgnore
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
