package com.broscraft.cda.utils;

import java.util.TreeMap;

public class RomanNumber {

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {

        map.put(1000, "m");
        map.put(900, "cm");
        map.put(500, "d");
        map.put(400, "cd");
        map.put(100, "c");
        map.put(90, "xc");
        map.put(50, "l");
        map.put(40, "xl");
        map.put(10, "x");
        map.put(9, "ix");
        map.put(5, "v");
        map.put(4, "iv");
        map.put(1, "i");

    }

    public final static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

}