package com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class MapableSuperclass {
    @GeneratedValue
    long id;
}

