package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.PlayerFamily;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.BottomCloth;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.Shoe;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Wearables.TopCloth;
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.Playerutils.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultPlayerCreator {
    public static Player createDefaultUser() {
        Player player = new Player();
        FamilyRegistrar registrar = new FamilyRegistrar();

        Family family = null;
        FamilyNames familyName = EnumRandomizer.getRandomEnum(FamilyNames.class);
        Wealth familyWealth = Wealth.getRandomWealthByWeight();
        List<Family> families = registrar.getFamilies();

        boolean isContainedFamily = false;
        for (Family family2 : families){
            if (family2.getFamilyNames().equals(familyName)){
                isContainedFamily = true;
                family = family2;
                break;
            }
        }
        if (!isContainedFamily){
            family = new Family(familyName);
            family.setFamilyMembers(new ArrayList<>());
            family.setFamilySize();
            family.setFamilyWealth(familyWealth);
        }

        PlayerFamily playerFamily = new PlayerFamily(family);
        playerFamily.setFamilyPosition(family.getFamilyMembers().size());

        TopCloth topCloth = createDefaultTopCloth(Genders.MALE, "def");
        BottomCloth bottomCloth = createDefaultBottomCloth(Genders.MALE, "def");
        Shoe shoe = createDefaultShoe("def");

        player.setId("test-user");
        player.setFamily(playerFamily);
        player.setIrisHue(new Color(0, 0, 0, 100));
        player.setSkinHue(new Color(0, 0, 0, 100));
        player.setHairHue(new Color(0, 0, 0, 100));
        player.setEyeLashHue(new Color(0, 0, 0, 100));
        player.setEyeLashType(Styles.EyelashStyles.DEFAULT);
        player.setHairType(Styles.HairStyles.DEFAULT);
        player.setCurrentAccessories(new ArrayList<>());
        player.setCollectedAccessories(new ArrayList<>());
        player.setCollectedBottomCloth(new ArrayList<>());
        player.setCollectedTopCloth(new ArrayList<>());
        player.setCollectedShoes(new ArrayList<>());

        player.getCollectedTopCloth().add(topCloth);
        player.getCollectedTopCloth().add(createDefaultTopCloth(Genders.MALE, "alt"));

        player.getCollectedBottomCloth().add(bottomCloth);
        player.getCollectedBottomCloth().add(createDefaultBottomCloth(Genders.MALE, "alt"));

        player.getCollectedShoes().add(shoe);
        player.getCollectedShoes().add(createDefaultShoe("alt"));

        player.setCurrentBottomCloth(bottomCloth);
        player.setCurrentTopCloth(topCloth);
        player.setCurrentShoe(shoe);

        family.getFamilyMembers().add(player.getId());
        return player;
    }

    public static TopCloth createDefaultTopCloth(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.POLYESTER : Material.WOOL;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? StringTemplate.STR."s_def_\{g}" : StringTemplate.STR."s_alt_\{g}";

        return new TopCloth(rarity, material, key);
    }
    public static BottomCloth createDefaultBottomCloth(Genders gender, String variant) {
        String g;
        if (gender == Genders.MALE){
            g = "m";
        }else{
            g = "f";
        }

        Material material = variant.equals("def") ? Material.CHINOS : Material.JEANS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? StringTemplate.STR."s_def_\{g}" : StringTemplate.STR."s_alt_\{g}";

        return new BottomCloth(rarity, material, key);
    }
    public static Shoe createDefaultShoe(String variant) {
        Material material = variant.equals("def") ? Material.LEATHER : Material.CANVAS;
        Rarity rarity = Rarity.COMMON;
        String key = variant.equals("def") ? "s_def_u" : "s_alt_u";

        return new Shoe(rarity, material, key);
    }
}
