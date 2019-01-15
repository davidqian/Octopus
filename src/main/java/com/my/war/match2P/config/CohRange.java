package com.my.war.match2P.config;

import com.my.Octopus.dataconfig.DataConfigItem;

public class CohRange extends DataConfigItem {
    public int id;
    public int range;

    public String getIndexKey() {
        return "id";
    }
}
