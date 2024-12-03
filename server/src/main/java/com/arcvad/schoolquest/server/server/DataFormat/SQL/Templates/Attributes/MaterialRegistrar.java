package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Accessory;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "material_registrar")
public class MaterialRegistrar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "materialRegistrar", fetch = FetchType.EAGER)
    private List<BottomCloth> bottomClothList;

    @OneToMany(mappedBy = "materialRegistrar", fetch = FetchType.EAGER)
    private List<TopCloth> topClothList;

    @OneToMany(mappedBy = "materialRegistrar", fetch = FetchType.EAGER)
    private List<Shoe> shoesList;

    @OneToMany(mappedBy = "materialRegistrar", fetch = FetchType.EAGER)
    private List<Accessory> accessoryList;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BottomCloth> getBottomClothList() {
        return bottomClothList;
    }

    public void setBottomClothList(List<BottomCloth> bottomClothList) {
        this.bottomClothList = bottomClothList;
    }

    public List<TopCloth> getTopClothList() {
        return topClothList;
    }

    public void setTopClothList(List<TopCloth> topClothList) {
        this.topClothList = topClothList;
    }

    public List<Shoe> getShoesList() {
        return shoesList;
    }

    public void setShoesList(List<Shoe> shoesList) {
        this.shoesList = shoesList;
    }

    public List<Accessory> getAccessoryList() {
        return accessoryList;
    }

    public void setAccessoryList(List<Accessory> accessoryList) {
        this.accessoryList = accessoryList;
    }
}

