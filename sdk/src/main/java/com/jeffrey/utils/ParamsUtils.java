package com.jeffrey.utils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ParamsUtils {
    private final HashMap<String, ArrayList<String>> excludeKeyWords = new HashMap<>(1);
    private final HashMap<String, ArrayList<String>> markNames = new HashMap<>(1);

    {
        excludeKeyWords.put("keywords", new ArrayList<>());
        markNames.put("markNames", new ArrayList<>());
    }

    public ParamsUtils addExcludeKeyWords(String word) {
        if (markNames.get("markNames").size() > 0) {
            throw new RuntimeException("参数 excludeKeyWords 和 markNames 其中一个必须为空");
        }
        excludeKeyWords.get("keywords").add(word);
        return this;
    }

    public ParamsUtils addMarkNames(String word) {
        if (excludeKeyWords.get("excludeKeyWords").size() > 0) {
            throw new RuntimeException("参数 excludeKeyWords 和 markNames 其中一个必须为空");
        }
        markNames.get("markNames").add(word);
        return this;
    }

    public String toRequestJsonStr() {
        return this.toString();
    }

    @Override
    public String toString() {

        return excludeKeyWords.get("keywords").size() > 0 ? new Gson().toJson(excludeKeyWords) : new Gson().toJson(markNames);
    }
}
