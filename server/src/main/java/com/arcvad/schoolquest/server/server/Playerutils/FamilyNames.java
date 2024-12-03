package com.arcvad.schoolquest.server.server.Playerutils;

import com.arcvad.schoolquest.server.server.GlobalUtils.Range;
import com.arcvad.schoolquest.server.server.GlobalUtils.RangeWeightedRandomInt;

import java.util.ArrayList;
import java.util.List;

public enum FamilyNames {
    SMITHS("Smiths", SizeRandomizer()),
    THORNFIELD("Thornfield", SizeRandomizer()),
    WHITEMORE("Whitemore", SizeRandomizer()),
    LANCASTER("Lancaster", SizeRandomizer()),
    BLACKWOOD("Blackwood", SizeRandomizer()),
    ASHCROFT("Ashcroft", SizeRandomizer()),
    KINGSLEY("Kingsley", SizeRandomizer()),
    ADEDAYO("Adedayo", SizeRandomizer()),
    ADEKUNLE("Adekunle", SizeRandomizer()),
    POPOOLA("Popoola", SizeRandomizer()),
    OSITUNGA("Ositunga", SizeRandomizer()),
    ADEJOH("Adejoh", SizeRandomizer()),
    ADEDO("Adedo", SizeRandomizer()),
    ADEMOLA("Ademola", SizeRandomizer()),
    ADEGBITE("Adegbite", SizeRandomizer()),
    DAWSON("Dawson", SizeRandomizer()),
    AKINYEMI("Akinyemi", SizeRandomizer()),
    AJAYI("Ajayi", SizeRandomizer()),
    SAITO("Saito", SizeRandomizer()),
    WAGNER("Wagner", SizeRandomizer()),
    LOPEZ("Lopez", SizeRandomizer()),
    DUNARD("Dunard", SizeRandomizer()),
    OGUNYEMI("Ogunyemi", SizeRandomizer()),
    FINCH("Finch", SizeRandomizer());


    private final String familyName;
    private final int familySize;

    FamilyNames(String familyName, int familySize) {
        this.familyName = familyName;
        this.familySize = familySize;
    }

    public String getFamilyName() {
        return familyName;
    }

    public int getFamilySize() {
        return familySize;
    }

    public static int SizeRandomizer() {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(50, 200, 50, 20));
        ranges.add(new Range(200, 500, 30, 50));
        ranges.add(new Range(500, 800, 15, 40));
        ranges.add(new Range(800, 1000, 5, 20));

        return RangeWeightedRandomInt.getWeightedRandomInt(ranges);

    }
}
