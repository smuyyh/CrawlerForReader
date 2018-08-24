package com.qy.reader.common.utils;

import com.qy.reader.common.Global;
import com.qy.reader.common.widgets.reader.annotation.FontType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class ZHConverter {

    private Properties charMap = new Properties();
    private Set<String> conflictingSets = new HashSet<>();
    private static final ZHConverter[] converters = new ZHConverter[2];
    private static final String[] propertyFiles = new String[]{"zh2Hant.properties", "zh2Hans.properties"};

    public static ZHConverter getInstance(@FontType int converterType) {
        if (converterType >= 0 && converterType < 2) {
            if (converters[converterType] == null) {
                synchronized (ZHConverter.class) {
                    if (converters[converterType] == null) {
                        converters[converterType] = new ZHConverter(propertyFiles[converterType]);
                    }
                }
            }
            return converters[converterType];
        } else {
            return null;
        }
    }

    public static String convert(String text, @FontType int converterType) {
        ZHConverter instance = getInstance(converterType);
        return instance.convert(text);
    }


    private ZHConverter(String propertyFile) {
        InputStream is = null;
        try {
            is = Global.getApplication().getAssets().open(propertyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                charMap.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        initializeHelper();
    }

    @SuppressWarnings("rawtypes")
    private void initializeHelper() {
        Map<String, Integer> stringPossibilities = new HashMap<>();
        Iterator iter = charMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (key.length() >= 1) {
                for (int i = 0; i < (key.length()); i++) {
                    String keySubstring = key.substring(0, i + 1);
                    if (stringPossibilities.containsKey(keySubstring)) {
                        Integer integer = stringPossibilities.get(keySubstring);
                        stringPossibilities.put(keySubstring, integer + 1);
                    } else {
                        stringPossibilities.put(keySubstring, 1);
                    }
                }
            }
        }
        iter = stringPossibilities.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (stringPossibilities.get(key) > 1) {
                conflictingSets.add(key);
            }
        }
    }

    public String convert(String in) {
        StringBuilder outString = new StringBuilder();
        StringBuilder stackString = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            String key = "" + c;
            stackString.append(key);
            if (conflictingSets.contains(stackString.toString())) {
            } else if (charMap.containsKey(stackString.toString())) {
                outString.append(charMap.get(stackString.toString()));
                stackString.setLength(0);
            } else {
                CharSequence sequence = stackString.subSequence(0, stackString.length() - 1);
                stackString.delete(0, stackString.length() - 1);
                flushStack(outString, new StringBuilder(sequence));
            }
        }
        flushStack(outString, stackString);
        return outString.toString();
    }

    private void flushStack(StringBuilder outString, StringBuilder stackString) {
        while (stackString.length() > 0) {
            if (charMap.containsKey(stackString.toString())) {
                outString.append(charMap.get(stackString.toString()));
                stackString.setLength(0);
            } else {
                outString.append(stackString.charAt(0));
                stackString.delete(0, 1);
            }
        }
    }
}