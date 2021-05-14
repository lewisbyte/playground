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
        ListNode n1 = new ListNode(1);
        ListNode n2 = new ListNode(2);
        ListNode n3 = new ListNode(3);
        ListNode n4 = new ListNode(4);
        ListNode n5 = new ListNode(5);
        ListNode n6 = new ListNode(6);

        n1.next=n2;
        n2.next=n3;
        n3.next=n4;
        n4.next=n5;
        n5.next=n6;

        new Solution().reverseList(n1);
    }
    public ListNode reverseList(ListNode head) {

        ListNode pre = null,next = null;
        while(head!=null){
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }

    public static class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

    public int[][] merge(int[][] intervals) {
        List<Segment> segments = new ArrayList<>();
        for (int[] sg : intervals) {
            segments.add(new Segment(sg[0], sg[1]));
        }

        List<Segment> ret = new ArrayList<>();
        Collections.sort(segments);
        int current;

        for (int i = 0; i < segments.size(); ) {
            Segment sg = segments.get(i);
            Segment ele = new Segment(sg.getX(), sg.getY());
            current = sg.getX();
            while (current >= sg.x) {
                current = Math.max(sg.y,current);
                if (++i >= segments.size()) {
                    break;
                }
                sg = segments.get(i);
            }
            ele.setY(current);
            ret.add(ele);

        }
        int[][] merges = new int[ret.size()][2];
        for (int i = 0; i < ret.size(); i++) {
            merges[i][0] = ret.get(i).getX();
            merges[i][1] = ret.get(i).getY();
        }
        return merges;
    }


    static class Segment implements Comparable<Segment> {
        private int x;
        private int y;

        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int compareTo(Segment o) {
            if (this.x != o.x) {
                return this.x - o.x;
            }
            return this.y - o.y;
        }
    }


    public char firstUniqChar(String s) {
        int cnt[] = new int[1 << 8];
        for (char c : s.toCharArray()) {
            cnt[c]++;
        }
        for (char c : s.toCharArray()) {
            if (cnt[c] == 1) {
                return c;
            }
        }
        return ' ';
    }

    public int[] getLeastNumbers(int[] arr, int k) {

        Queue<Integer> queue = new PriorityQueue<>((a, b) -> b.compareTo(a));

        for (int i : arr) {
            if (queue.size() < k) {
                queue.add(i);
            } else {
                Integer head = queue.peek();
                if (head != null && head > i) {
                    queue.poll();
                    queue.add(i);
                }
            }
        }
        int ret[] = new int[queue.size()];
        int index = 0;
        for (int i : queue) {
            ret[index++] = i;
        }
        return ret;
    }


    public boolean wordBreak(String s, List<String> wordDict) {
        Collections.sort(wordDict);

        for (String i : wordDict) {
            s = s.replace(i, "");
        }
        return s.length() == 0;
    }

    /**
     * 3,30,34,5,9
     * <p>
     * 9533430
     * <p>
     * 3,39,34,5,9
     * <p>
     * 9539343
     * <p>
     * 9539343
     * <p>
     * 9 39
     * <p>
     *
     * @param nums
     * @return
     */
    public String largestNumber(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                String a = String.valueOf(nums[i]);
                String b = String.valueOf(nums[j]);
                boolean ret = compareALessThanB(a, b);
                if (ret) {
                    int tmp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = tmp;
                    System.out.printf("less %s %s\n", a, b);

                } else {
                    System.out.printf("big %s %s\n", a, b);
                }
            }
        }
        String s = "";
        for (int i : nums) {
            s += i;
        }
        return s;
    }


    private boolean compareALessThanB(String a, String b) {
        //比较长度共同的前缀大小
        int index = 0;
        for (; index < a.length() && index < b.length(); index++) {
            if (a.toCharArray()[index] < b.toCharArray()[index]) {
                return true;
            }
        }
        if (a.length() > b.length()) {
            return a.toCharArray()[index] < b.toCharArray()[index - 1];
        } else if (a.length() < b.length()) {
            return a.toCharArray()[index - 1] < b.toCharArray()[index];
        }
        return a.compareTo(b) < 0;
    }

    public boolean canJump(int[] nums) {
        int cnt = 0, index = 0;
        for (int i : nums) {
            cnt = Math.max(cnt - 1, i);
            if (cnt <= 0 && index < nums.length - 1) {
                return false;
            }
            index++;
        }
        return true;
    }

    /**
     * top k 问题
     *
     * @param nums
     * @param k
     * @return
     */
    public int findKthLargest(int[] nums, int k) {
        Queue<Integer> queue = new PriorityQueue<>();
        for (int i : nums) {
            if (queue.size() < k) {
                queue.add(i);
            } else {
                if (queue.peek() < i) {
                    queue.remove();
                    queue.add(i);
                }
            }
        }
        return queue.peek();
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
