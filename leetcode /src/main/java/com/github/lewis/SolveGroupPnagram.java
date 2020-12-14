package com.github.lewis;


import java.util.*;

public class SolveGroupPnagram {


    public static void main(String[] args) {

    }

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>(strs.length);

        for (String s : strs) {
            map.put(reOrder(s), map.getOrDefault(reOrder(s), new ArrayList<>()));
            map.get(reOrder(s)).add(s);

        }
        List<List<String>> lists = new ArrayList<>();

        map.forEach((k, v) -> lists.add(new ArrayList<>(v)));
        return lists;

    }

    public String reOrder(String s) {
        char[] arr = s.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }

}
