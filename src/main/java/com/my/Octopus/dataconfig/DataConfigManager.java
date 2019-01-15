package com.my.Octopus.dataconfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import com.my.Octopus.util.StringUtil;

public class DataConfigManager {

    public static String dataConfigPath;

    private static Logger logger = LoggerFactory.getLogger(DataConfigManager.class);

    private static Map<Class<?>, DataConfigTable> dataConfigMap = Maps.newConcurrentMap();

    public static void initAllConfig(String dataPackageName) {
        //反射包
        Reflections reflections = new Reflections(dataPackageName);
        //获取 DataConfigItem 的所有子类
        Set<Class<? extends DataConfigItem>> classes = reflections.getSubTypesOf(DataConfigItem.class);
        for (Class<?> clz : classes) {
            loadSingleConfig(clz); //加截每个策划的配制信息
        }
    }

    /**
     * 获取多个实体id列表
     *
     * @param clz
     * @return
     */
    public static List<String> getDataConfigIdList(Class<?> clz) {
        DataConfigTable table = getDataConfigTable(clz);

        return table.getIdList();
    }

    public static <T extends DataConfigItem> List<T> getDataConfigList(Class<T> clz) {
        DataConfigTable table = getDataConfigTable(clz);

        return table.getList(clz);
    }


    /**
     * 获取多个实体总数
     *
     * @param clz
     * @return
     */
    public static int getDataConfigSize(Class<?> clz) {
        DataConfigTable table = getDataConfigTable(clz);

        return table.getIdList().size();
    }

    /**
     * 返回一个实体对象
     *
     * @param clz
     * @param id
     * @return
     */
    public static <T> T getSettingById(Class<T> clz, String dataConfigId) {

        DataConfigTable table = getDataConfigTable(clz);

        DataConfigItem item = table.getConfigItemById(dataConfigId);

        if (item == null) {
            //logger.debug("DataConfigItem [{}] not found in [{}],dataConfigId:", dataConfigId, clz.getSimpleName());
            return null;
        }

        return clz.cast(item);
    }

    /**
     * 返回一个实体对象
     *
     * @param clz
     * @param id
     * @return
     */
    public static <T> T getSettingById(Class<T> clz, int id) {
        return getSettingById(clz, String.valueOf(id));
    }

    /**
     * 获取多个实体列表
     *
     * @param clz
     * @return
     */
    private static DataConfigTable getDataConfigTable(Class<?> clz) {

        if (dataConfigMap == null || dataConfigMap.isEmpty() || !dataConfigMap.containsKey(clz)) {
            loadSingleConfig(clz);
            logger.info("lazy load DataConfigTable {}", clz.getSimpleName());
        }

        DataConfigTable table = dataConfigMap.get(clz);

        if (table == null) {
            logger.error("DataConfigTable {} not found", clz.getSimpleName());
            return null;
        }

        return table;
    }

    /**
     * 加截每一个实体对应的配制数据(策划配制)
     *
     * @param clz 实体
     */
    private static void loadSingleConfig(Class<?> clz) {

        String filePath = dataConfigPath + StringUtil.toLowerCaseFirstLetter(clz.getSimpleName()) + ".json";
        logger.info("load DataConfigTable: {}", clz.getSimpleName());

        BufferedReader br = null;

        try {
            Map<String, DataConfigItem> data = Maps.newHashMap();
            br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                JSONArray jsonArray = JSONArray.parseArray(line);
                for (Object obj : jsonArray) {
                    JSONObject jsonObject = (JSONObject) obj;
                    DataConfigItem item = (DataConfigItem) JSON.parseObject(jsonObject.toString(), clz);
                    String index = jsonObject.getString(item.getIndexKey());
                    if (index == null) {
                        logger.error("id is null:{},{}", clz, jsonObject);
                        continue;
                    }
                    data.put(index, item);
                }
            }
            //将配表map转换成统一DataConfigTable对象，放至统一配置map中
            DataConfigTable table = new DataConfigTable(data);
            dataConfigMap.put(clz, table);

        } catch (Exception e) {
            logger.error("DataConfig [{}] load failed: {}", clz.getSimpleName(), e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}