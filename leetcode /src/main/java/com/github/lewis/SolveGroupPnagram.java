package com.github.lewis;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SolveGroupPnagram {


    public static void main(String[] args) {
        int ret = new SolveGroupPnagram().largestSubmatrix(new int[][]{
                {1, 1, 1, 1, 1}
                , {0, 1, 0, 1, 1}
                , {1, 1, 0, 0, 0}
                , {1, 1, 1, 1, 1}
                , {1, 1, 0, 0, 0}
        });
        System.out.println(ret);
    }


    public int largestSubmatrix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length, ret = 0;
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    matrix[i][j] += matrix[i - 1][j];
                }
            }
        }
        for (int i = 0; i < m; i++) {
            Arrays.sort(matrix[i]);
            for (int j = 0; j < n; j++) {
                int height = matrix[i][j];
                ret = Math.max(ret, height * (n - j));
            }
        }
        return ret;
    }


    public int tupleSameProduct(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int ret = nums[i] * nums[j];
                map.put(ret, map.getOrDefault(ret, 0) + 1);
            }
        }
        final AtomicInteger ans = new AtomicInteger(0);
        map.forEach((k, v) -> {
            ans.addAndGet(v * (v - 1));
        });
        return ans.get() * 4;
    }


    /**
     * 输入：nums = [-1,0,1,2,-1,-4]
     * 输出：[[-1,-1,2],[-1,0,1]]
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> ret = new ArrayList<>();
        Set<String> s = new HashSet<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int sum = nums[i] + nums[j];
                int ans = Arrays.binarySearch(nums, -sum);

                if (ans == i || ans == j || ans < 0 || ans > nums.length) {
                    continue;
                }

                int[] ansArr = new int[]{nums[i], nums[j], nums[ans]};
                Arrays.sort(ansArr);
                StringBuilder perm = new StringBuilder();
                for (int a : ansArr) {
                    perm.append(a);
                }
                if (s.contains(perm.toString())) {
                    continue;
                }

                if (sum + nums[ans] == 0) {
                    s.add(perm.toString());
                    ret.add(Arrays.asList(nums[i], nums[j], nums[ans]));
                }
            }
        }
        return ret;
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
