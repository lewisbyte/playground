public class TestGC {

    // 1MB 空间大小
    public static final int _1MB = 1 << 20;


    public static void main(String[] args) {
        //testAllocation();

        testAllocation2();
    }

    /**
     * jvm 参数：-verbose:gc  -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
     */
    public static void testAllocation() {
        byte a1[], a2[], a3[], a4[];

        a1 = new byte[2 * _1MB];
        a2 = new byte[2 * _1MB];
        a3 = new byte[2 * _1MB];
        a4 = new byte[4 * _1MB];
        a4 = new byte[4 * _1MB];
    }


    /**
     * jvm 参数：-verbose:gc  -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:PretenureSizeThreshold=3145728
     */
    public strictfp static void testAllocation2() {
        byte a4[];
        a4 = new byte[4 * _1MB];
    }


}
