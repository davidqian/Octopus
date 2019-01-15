package com.my.Octopus.dataconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 所有配表
 *
 * @author davidqian
 */
public class DataConfigTable {

    private ImmutableMap<String, DataConfigItem> itemMap = null;

    public DataConfigTable(Map<String, DataConfigItem> data) {
        ImmutableMap.Builder<String, DataConfigItem> builder = ImmutableMap.builder();

        builder.putAll(data);

        itemMap = builder.build();
    }

    public DataConfigItem getConfigItemById(String id) {
        return itemMap.get(id);
    }

    public List<String> getIdList() {
        return itemMap.keySet().asList();
    }

    public <T extends DataConfigItem> List<T> getList(Class<T> clazz) {
        ImmutableList<DataConfigItem> dataConfigItems = itemMap.values().asList();
        List<T> list = new ArrayList<>();
        for (DataConfigItem dataConfigItem : dataConfigItems) {
            list.add((T) dataConfigItem);
        }
        return list;
    }
}