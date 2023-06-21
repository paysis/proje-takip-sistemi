package com.gurkan.interfaces;

import java.util.Map;

public interface IStorableObject {

    String render();

    default String render(Map<String, String> data) {
        return renderHelper(data);
    }

    public void importData(Map<String, String> map);

    private String renderHelper(Map<String, String> map) {
        String ret = "";
        int keyCounter = 0;

        for (String key : map.keySet()) {

            ret += key + ":\"" + map.get(key) + "\"";
            if (keyCounter < map.size() - 1)
                ret += "|";

            keyCounter++;
        }

        return ret + "\n";
    }
}
