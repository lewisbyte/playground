/**
 * @program: Main
 * @description: acm 模板
 * @author: lewis
 * @create: 2021-04-17 19:43
 * <p>
 *
 *     80
 *     90
 *     95
 *
 *     170
 *     190
 *
 *     360
 *
 *
 *
 *
 */

import java.util.*;

public class Main {
    static class TaskTemplate {
        public void solve() {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
            }
        }
    }

    public static void main(String[] args) {
//        TaskA solver = new TaskA();
//        solver.solve();

        TaskB bsolver = new TaskB();
        bsolver.solve();

//        TaskC solver = new TaskC();
//        solver.solve();

    }


    /**
     * 3 2
     * 1 0
     * 1 1
     * 0 1
     * 3 2 1 0 1 1 0 1
     * <p>
     * <p>
     * <p>
     * 2 2
     * 1 0
     * 0 1
     * 0 1
     * <p>
     * 3 3 1 1 1 0 1 0 1 1 1
     * <p>
     * <p>
     * 1 2
     * 1 1
     */
    static class TaskC {
        // 方向
        int[][] dir = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        public void solve() {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                int n = scanner.nextInt();
                int m = scanner.nextInt();
                int[][] matrix = new int[n][m];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        matrix[i][j] = scanner.nextInt();
                    }
                }
                int maxRet = 0;
                int color = 2333;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        dfs(n, m, i, j, matrix, ++color);
                    }
                }

                Map<Integer, Integer> map = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        map.put(matrix[i][j], map.getOrDefault(matrix[i][j], 0) + 1);
                        if (matrix[i][j] != 0) {
                            maxRet = Math.max(map.getOrDefault(matrix[i][j], 0), maxRet);
                        }
                    }
                }

                System.out.println(maxRet);
            }
        }

        /**
         * @param m
         * @param n
         * @param i
         * @param j
         * @param matrix
         * @param cnt
         */
        private void dfs(int n, int m, int i, int j, int[][] matrix, int cnt) {

            if (i < 0 || j < 0 || i >= n || j >= m) {
                return;
            }
            if (matrix[i][j] != 1) {
                return;
            }
            matrix[i][j] = cnt;

            for (int di = 0; di < 4; di++) {
                int dx = dir[di][0] + i;
                int dy = dir[di][1] + j;
                dfs(n, m, dx, dy, matrix, cnt);
            }
        }
    }


    /**
     * 5 -> 4
     * 6 -> 5
     * 3 -> 3
     */
    static class TaskB {
        public void solve() {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                int n = scanner.nextInt();
                int cnt = 0;
                for (int i = 1; i <= n; i++) {
                    String s = String.valueOf(i);
                    if (s.contains("4")) {
                        s = s.replace("4", "5");
                        i = Integer.valueOf(s);
                    }
                    System.out.print(i + " ");
                    cnt++;
                }
                System.out.println(cnt);
            }

        }
    }


    /**
     * abc
     * abcaybec
     * <p>
     * aa
     * abca
     * <p>
     * a
     * f
     */
    static class TaskA {
        public void solve() {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()) {
                String s1 = scanner.next();
                String s2 = scanner.next();

                if (s1.isEmpty() || s2.isEmpty()) {
                    System.out.println(-1);
                    continue;
                }

                char[] target = s1.toCharArray();
                char[] source = s2.toCharArray();

                if (source.length == 0 || target.length == 0) {
                    System.out.println(-1);
                    continue;
                }
                int cnt = 0;
                int index = source.length;
                for (int i = target.length - 1; i >= 0; i--) {
                    if (index == -1) {
                        cnt = -1;
                        break;
                    }
                    do {
                        index--;
                        if (index == -1) {
                            cnt = -1;
                            break;
                        }
                    }
                    while (index >= 0 && source[index] != target[i]);
                    if (index >= 0 && source[index] == target[i]) {
                        cnt++;
                    }
                }
                System.out.printf("%s\n", cnt == target.length ? index : -1);
            }
        }
    }
}

