package com.github.lewis;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 * <p>
 * 有效字符串需满足：
 * <p>
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 注意空字符串可被认为是有效字符串。
 * <p>
 * 示例 1:
 * <p>
 * 输入: "()"
 * 输出: true
 * 示例 2:
 * <p>
 * 输入: "()[]{}"
 * 输出: true
 * 示例 3:
 * <p>
 * 输入: "(]"
 * 输出: false
 * 示例 4:
 * <p>
 * 输入: "([)]"
 * 输出: false
 * 示例 5:
 * <p>
 * 输入: "{[]}"
 * 输出: true
 */
class Solution {
    public static void main(String[] args) {
//        int a[] = {9,1,7,9,7,9,7};
        int a[] = {3, 4, 3, 3};
        System.out.println(new Solution().singleNumber(a));
    }


    public int[] singleNumbers(int[] nums) {
        return IntStream.of(nums)
                .boxed()
                .collect(Collectors.groupingBy(s -> s))
                .entrySet()
                .stream()
                .filter(it -> it.getValue().size() == 1)
                .map(it -> it.getKey())
                .collect(Collectors.toList())
                .stream()
                .mapToInt(i -> i).toArray();
    }


    public boolean isPalindrome(int x) {
        String s = String.valueOf(x);
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length / 2; i++) {
            if (arr[i] != arr[arr.length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    public boolean isValid(String s) {

        boolean isValid = true;

        Map<String, String> pair = new HashMap<>();
        pair.put("(", ")");
        pair.put("[", "]");
        pair.put("{", "}");

        Map<String, String> reversePair = new HashMap<>();
        reversePair.put(")", "(");
        reversePair.put("]", "[");
        reversePair.put("}", "{");

        Stack<String> queue = new Stack<>();

        for (char c : s.toCharArray()) {
            if (pair.containsKey(String.valueOf(c))) {
                queue.push(String.valueOf(c));
            } else {
                if (queue.isEmpty() || !reversePair.get(String.valueOf(c)).equals(queue.pop())) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid & queue.isEmpty();
    }
}
