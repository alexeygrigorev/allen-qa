package com.kaggle.allen.wiki;

import java.util.List;
import java.util.Map;

public class MapWrapper {

    private final Map<String, ?> map;

    public MapWrapper(Map<String, ?> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ?> getMap(String property) {
        String[] split = property.split("[.]");
        Map<String, ?> result = map;
        for (String name : split) {
            result = (Map<String, ?>) result.get(name);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, ?>> getList(String property) {
        String[] split = property.split("[.]");
        Map<String, ?> result = map;
        for (int i = 0; i < split.length - 1; i++) {
            String name = split[i];
            result = (Map<String, ?>) result.get(name);
        }

        return (List<Map<String, ?>>) result.get(split[split.length - 1]);
    }

    @SuppressWarnings("unchecked")
    public String getString(String property) {
        String[] split = property.split("[.]");
        Map<String, ?> result = map;
        for (int i = 0; i < split.length - 1; i++) {
            String name = split[i];
            result = (Map<String, ?>) result.get(name);
        }

        return result.get(split[split.length - 1]).toString();
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
