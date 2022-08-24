package com.notsatvrn.hurricane.util;

public class MathUtil {
    public static int long2int(long l) {
        return (int) l >>> 32;
    }

    public static double long2double(long l) {
        return (1+((double)(((double)l) / Long.MAX_VALUE)))/2;
    }

    public static float long2float(long l) {
        return (1+((float)(((float)l) / Long.MAX_VALUE)))/2;
    }

    public static boolean long2boolean(long l) {
        return (1 - (l >>> 63)) != 0;
    }
}

