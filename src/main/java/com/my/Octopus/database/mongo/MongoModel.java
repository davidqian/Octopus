package com.my.Octopus.database.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davidqian on 2017/6/17.
 */
public abstract class MongoModel {
    private static final Logger logger = LoggerFactory.getLogger(MongoModel.class);

    public abstract String getStringId();

    public abstract String getDBName();

    public abstract String getCollectionName();

//	protected Map<String, Object> toMap() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
//			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//			for (PropertyDescriptor property : propertyDescriptors) {
//				String key = property.getName();
//
//				if (!key.equals("class")) {
//					Method getter = property.getReadMethod();
//					Object value = getter.invoke(this);
//
//					map.put(key, value);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//
//		return map;
//	}

//	protected void load(Map map) {
//		try {
//			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
//			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//			for (PropertyDescriptor property : propertyDescriptors) {
//				Method setter = property.getWriteMethod();
//				if (setter != null) {
//					setter.invoke(this, map.get(property.getName()));
//				}
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}

    public String[] getColumn() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<String> c = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (!Modifier.isTransient(field.getModifiers())) {
                c.add(fields[i].getName());
            }
        }

        return c.toArray(new String[]{});
    }

    public class Oid {
        private String $oid;

        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }
    }
}
