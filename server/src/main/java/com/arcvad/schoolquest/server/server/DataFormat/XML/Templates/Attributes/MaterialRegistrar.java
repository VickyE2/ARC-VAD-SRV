package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Accessory.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.BottomCloth.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.Shoe.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Wearables.TopCloth.TopCloth;
import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "registrar")
public class MaterialRegistrar extends BaseTemplate implements Mergeable<MaterialRegistrar> {
    @XmlElement
    private List<BottomCloth> bottomClothList;
    @XmlElement
    private List<TopCloth> topClothList;
    @XmlElement
    private List<Shoe> shoesList;
    @XmlElement
    private List<Accessory> accessoryList;



    public void setBottomClothList(List<BottomCloth> bottomClothList){
        this.bottomClothList = bottomClothList;
    }

    public void setAccessoryList(List<Accessory> accessoryList){
        this.accessoryList = accessoryList;
    }

    public void setShoesList(List<Shoe> shoesList){
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
    public List<Shoe> getShoesList(){
        return this.shoesList;
    }

    @Override
    public void mergeWith(MaterialRegistrar other) {
        if (other == null) {
            logger.severe("ARC-MERGE", "Cannot merge with null MaterialRegistrar object");
            throw new IllegalArgumentException("Cannot merge with null MaterialRegistrar object");
        }

        // Merge bottomClothList
        if (other.bottomClothList != null) {
            if (this.bottomClothList == null) {
                this.bottomClothList = new ArrayList<>(other.bottomClothList);
            } else {
                mergeUnique(this.bottomClothList, other.bottomClothList);
            }
        }

        // Merge topClothList
        if (other.topClothList != null) {
            if (this.topClothList == null) {
                this.topClothList = new ArrayList<>(other.topClothList);
            } else {
                mergeUnique(this.topClothList, other.topClothList);
            }
        }

        // Merge shoesList
        if (other.shoesList != null) {
            if (this.shoesList == null) {
                this.shoesList = new ArrayList<>(other.shoesList);
            } else {
                mergeUnique(this.shoesList, other.shoesList);
            }
        }

        // Merge accessoryList
        if (other.accessoryList != null) {
            if (this.accessoryList == null) {
                this.accessoryList = new ArrayList<>(other.accessoryList);
            } else {
                mergeUnique(this.accessoryList, other.accessoryList);
            }
        }

        // Log the merge action
        logger.info("ARC-MERGE", "green[Merged MaterialRegistrar data successfully]");
    }

    /**
     * Merges sourceList into targetList, ensuring only unique elements are added.
     *
     * @param targetList The list to merge into.
     * @param sourceList The list to merge from.
     * @param <T>        The type of elements in the list.
     */
    private <T> void mergeUnique(List<T> targetList, List<T> sourceList) {
        Set<T> uniqueSet = new HashSet<>(targetList); // Use a set to track unique items
        for (T item : sourceList) {
            if (!uniqueSet.contains(item)) {
                targetList.add(item); // Add only if not already present
                uniqueSet.add(item);
            }
        }
    }
}
